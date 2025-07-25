import { NextResponse } from 'next/server';
import { db } from '@/lib/db';
import { z } from 'zod';
import { ROLES } from '@/lib/constants';

const promotionSchema = z.object({
  employeeId: z.string().min(1),
  submittedById: z.string().min(1),
  status: z.string(),
  reviewStage: z.enum(['initial', 'commission_review', 'completed']).default('initial'),
  rejectionReason: z.string().nullable().optional(),
  reviewedById: z.string().nullable().optional(),
  commissionDecisionDate: z.string().nullable().optional(),
  commissionDecisionReason: z.string().nullable().optional(),
  proposedCadre: z.string().min(1),
  promotionType: z.enum(['Experience', 'EducationAdvancement']),
  documents: z.array(z.string()),
  studiedOutsideCountry: z.boolean().optional(),
});

export async function POST(req: Request) {
  try {
    const body = await req.json();
    const parsedData = promotionSchema.parse(body);

    const newRequest = await db.promotionRequest.create({
      data: {
        ...parsedData,
        reviewStage: 'initial',
      },
       include: {
        employee: { select: { name: true, zanId: true, department: true, cadre: true, employmentDate: true, dateOfBirth: true, institution: { select: { name: true } }, payrollNumber: true, zssfNumber: true }},
        submittedBy: { select: { name: true, role: true } },
      },
    });
    return NextResponse.json(newRequest, { status: 201 });
  } catch (error) {
    console.error("[PROMOTIONS_POST]", error);
    if (error instanceof z.ZodError) {
      return new NextResponse(JSON.stringify(error.errors), { status: 400 });
    }
    return new NextResponse('Internal Server Error', { status: 500 });
  }
}

export async function PATCH(req: Request) {
  try {
    const body = await req.json();
    const { id, status, reviewStage, rejectionReason, reviewedById } = body;

    if (!id) {
      return new NextResponse("Request ID is required", { status: 400 });
    }

    const existingRequest = await db.promotionRequest.findUnique({
      where: { id },
      include: {
        employee: true
      }
    });

    if (!existingRequest) {
      return new NextResponse("Promotion request not found", { status: 404 });
    }

    const updatedData: any = {};
    if (status !== undefined) updatedData.status = status;
    if (reviewStage !== undefined) updatedData.reviewStage = reviewStage;
    if (rejectionReason !== undefined) updatedData.rejectionReason = rejectionReason;
    if (reviewedById !== undefined) updatedData.reviewedById = reviewedById;

    // Check if this is a Commission approval
    const isCommissionApproval = status === "Approved by Commission";

    if (isCommissionApproval) {
      // Use a transaction to update both the promotion request and employee cadre
      const result = await db.$transaction(async (tx) => {
        // Update the promotion request
        const updatedRequest = await tx.promotionRequest.update({
          where: { id },
          data: updatedData,
          include: {
            employee: { select: { name: true, zanId: true, department: true, cadre: true, employmentDate: true, dateOfBirth: true, institution: { select: { name: true } }, payrollNumber: true, zssfNumber: true }},
            submittedBy: { select: { name: true, role: true } },
            reviewedBy: { select: { name: true, role: true } },
          }
        });

        // Update the employee's cadre to the proposed cadre
        await tx.employee.update({
          where: { id: existingRequest.employeeId },
          data: {
            cadre: existingRequest.proposedCadre
          }
        });

        return updatedRequest;
      });

      return NextResponse.json(result, { status: 200 });
    } else {
      // Regular update without employee cadre change
      const updatedRequest = await db.promotionRequest.update({
        where: { id },
        data: updatedData,
        include: {
          employee: { select: { name: true, zanId: true, department: true, cadre: true, employmentDate: true, dateOfBirth: true, institution: { select: { name: true } }, payrollNumber: true, zssfNumber: true }},
          submittedBy: { select: { name: true, role: true } },
          reviewedBy: { select: { name: true, role: true } },
        },
      });

      return NextResponse.json(updatedRequest, { status: 200 });
    }
  } catch (error) {
    console.error("[PROMOTIONS_PATCH]", error);
    if (error instanceof z.ZodError) {
      return new NextResponse(JSON.stringify(error.errors), { status: 400 });
    }
    return new NextResponse('Internal Server Error', { status: 500 });
  }
}

export async function GET(req: Request) {
  const { searchParams } = new URL(req.url);
  const userId = searchParams.get('userId');
  const userRole = searchParams.get('userRole');
  const userInstitutionId = searchParams.get('userInstitutionId');

  if (!userId || !userRole) {
    return new NextResponse("User ID and Role are required", { status: 400 });
  }

  try {
    let requests;
    const includeOptions = {
        employee: { select: { name: true, zanId: true, department: true, cadre: true, employmentDate: true, dateOfBirth: true, institution: { select: { name: true } }, payrollNumber: true, zssfNumber: true }},
        submittedBy: { select: { name: true, role: true } },
        reviewedBy: { select: { name: true, role: true } },
    };

    const whereClause: any = {};
    
    if (userRole === ROLES.HRO) {
        whereClause.submittedById = userId;
    } else if (userRole === ROLES.HHRMD || userRole === ROLES.HRMO) {
        // Both HHRMD and HRMO see same requests at same time
        whereClause.OR = [
            { 
                reviewStage: 'initial',
                status: 'Pending HRMO/HHRMD Review'
            },
            {
                reviewStage: 'commission_review',
                status: 'Request Received – Awaiting Commission Decision'
            }
        ];
    } else {
        // Higher roles (CSCS, Admin) can see all non-completed requests
        whereClause.status = { notIn: ['Approved by Commission', 'Rejected by Commission'] };
    }
    
    requests = await db.promotionRequest.findMany({
      where: whereClause,
      orderBy: { createdAt: 'desc' },
      include: includeOptions,
    });
    
    return NextResponse.json(requests);
  } catch (error) {
    console.error("[PROMOTIONS_GET]", error);
    return new NextResponse('Internal Server Error', { status: 500 });
  }
}
