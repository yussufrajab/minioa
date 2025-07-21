import { NextResponse } from 'next/server';
import { db } from '@/lib/db';

export async function GET(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const reportType = searchParams.get('reportType');
    const fromDate = searchParams.get('fromDate');
    const toDate = searchParams.get('toDate');
    const institutionId = searchParams.get('institutionId');

    console.log('Reports API called with:', { reportType, fromDate, toDate, institutionId });

    if (!reportType) {
      return NextResponse.json({ 
        success: false, 
        message: 'Report type is required' 
      }, { status: 400 });
    }

    // Build date filter
    let dateFilter: any = {};
    if (fromDate && toDate) {
      dateFilter.createdAt = {
        gte: new Date(fromDate),
        lte: new Date(toDate)
      };
    } else if (fromDate) {
      dateFilter.createdAt = {
        gte: new Date(fromDate)
      };
    } else if (toDate) {
      dateFilter.createdAt = {
        lte: new Date(toDate)
      };
    }

    // Build institution filter
    let institutionFilter: any = {};
    if (institutionId) {
      institutionFilter.employee = {
        institutionId: institutionId
      };
    }

    // Combine filters
    const whereClause = {
      ...dateFilter,
      ...institutionFilter
    };

    let reportData: any[] = [];

    // Generate reports based on type
    switch (reportType) {
      case 'confirmation':
        reportData = await db.confirmationRequest.findMany({
          where: whereClause,
          include: {
            employee: {
              select: {
                id: true,
                name: true,
                zanId: true,
                institution: { select: { id: true, name: true } }
              }
            },
            submittedBy: { select: { id: true, name: true, username: true } },
            reviewedBy: { select: { id: true, name: true, username: true } }
          },
          orderBy: { createdAt: 'desc' }
        }).catch(() => []);
        break;

      case 'promotion':
      case 'promotionExperience':
      case 'promotionEducation':
        reportData = await db.promotionRequest.findMany({
          where: whereClause,
          include: {
            employee: {
              select: {
                id: true,
                name: true,
                zanId: true,
                institution: { select: { id: true, name: true } }
              }
            },
            submittedBy: { select: { id: true, name: true, username: true } },
            reviewedBy: { select: { id: true, name: true, username: true } }
          },
          orderBy: { createdAt: 'desc' }
        }).catch(() => []);
        break;

      case 'lwop':
        reportData = await db.lwopRequest.findMany({
          where: whereClause,
          include: {
            employee: {
              select: {
                id: true,
                name: true,
                zanId: true,
                institution: { select: { id: true, name: true } }
              }
            },
            submittedBy: { select: { id: true, name: true, username: true } },
            reviewedBy: { select: { id: true, name: true, username: true } }
          },
          orderBy: { createdAt: 'desc' }
        }).catch(() => []);
        break;

      case 'cadreChange':
      case 'cadre-change':
        reportData = await db.cadreChangeRequest.findMany({
          where: whereClause,
          include: {
            employee: {
              select: {
                id: true,
                name: true,
                zanId: true,
                institution: { select: { id: true, name: true } }
              }
            },
            submittedBy: { select: { id: true, name: true, username: true } },
            reviewedBy: { select: { id: true, name: true, username: true } }
          },
          orderBy: { createdAt: 'desc' }
        }).catch(() => []);
        break;

      case 'retirement':
      case 'voluntaryRetirement':
      case 'compulsoryRetirement':
      case 'illnessRetirement':
        reportData = await db.retirementRequest.findMany({
          where: whereClause,
          include: {
            employee: {
              select: {
                id: true,
                name: true,
                zanId: true,
                institution: { select: { id: true, name: true } }
              }
            },
            submittedBy: { select: { id: true, name: true, username: true } },
            reviewedBy: { select: { id: true, name: true, username: true } }
          },
          orderBy: { createdAt: 'desc' }
        }).catch(() => []);
        break;

      case 'resignation':
        reportData = await db.resignationRequest.findMany({
          where: whereClause,
          include: {
            employee: {
              select: {
                id: true,
                name: true,
                zanId: true,
                institution: { select: { id: true, name: true } }
              }
            },
            submittedBy: { select: { id: true, name: true, username: true } },
            reviewedBy: { select: { id: true, name: true, username: true } }
          },
          orderBy: { createdAt: 'desc' }
        }).catch(() => []);
        break;

      case 'serviceExtension':
      case 'service-extension':
        reportData = await db.serviceExtensionRequest.findMany({
          where: whereClause,
          include: {
            employee: {
              select: {
                id: true,
                name: true,
                zanId: true,
                institution: { select: { id: true, name: true } }
              }
            },
            submittedBy: { select: { id: true, name: true, username: true } },
            reviewedBy: { select: { id: true, name: true, username: true } }
          },
          orderBy: { createdAt: 'desc' }
        }).catch(() => []);
        break;

      case 'termination':
      case 'terminationDismissal':
        reportData = await db.separationRequest.findMany({
          where: whereClause,
          include: {
            employee: {
              select: {
                id: true,
                name: true,
                zanId: true,
                institution: { select: { id: true, name: true } }
              }
            },
            submittedBy: { select: { id: true, name: true, username: true } },
            reviewedBy: { select: { id: true, name: true, username: true } }
          },
          orderBy: { createdAt: 'desc' }
        }).catch(() => []);
        break;

      case 'complaints':
        reportData = await db.complaint.findMany({
          where: {
            ...dateFilter,
            ...(institutionId && {
              employee: {
                institutionId: institutionId
              }
            })
          },
          include: {
            employee: {
              select: {
                id: true,
                name: true,
                zanId: true,
                institution: { select: { id: true, name: true } }
              }
            }
          },
          orderBy: { createdAt: 'desc' }
        }).catch(() => []);
        break;

      case 'contractual':
        // For now, return empty array as contractual employment might need special handling
        reportData = [];
        break;

      case 'all':
        // Get all request types and combine them
        const [confirmations, promotions, lwops, cadreChanges, retirements, resignations, serviceExtensions, terminations] = await Promise.all([
          db.confirmationRequest.findMany({ where: whereClause, include: { employee: { select: { id: true, name: true, zanId: true, institution: { select: { id: true, name: true } } } }, submittedBy: { select: { id: true, name: true, username: true } }, reviewedBy: { select: { id: true, name: true, username: true } } }, orderBy: { createdAt: 'desc' } }).catch(() => []),
          db.promotionRequest.findMany({ where: whereClause, include: { employee: { select: { id: true, name: true, zanId: true, institution: { select: { id: true, name: true } } } }, submittedBy: { select: { id: true, name: true, username: true } }, reviewedBy: { select: { id: true, name: true, username: true } } }, orderBy: { createdAt: 'desc' } }).catch(() => []),
          db.lwopRequest.findMany({ where: whereClause, include: { employee: { select: { id: true, name: true, zanId: true, institution: { select: { id: true, name: true } } } }, submittedBy: { select: { id: true, name: true, username: true } }, reviewedBy: { select: { id: true, name: true, username: true } } }, orderBy: { createdAt: 'desc' } }).catch(() => []),
          db.cadreChangeRequest.findMany({ where: whereClause, include: { employee: { select: { id: true, name: true, zanId: true, institution: { select: { id: true, name: true } } } }, submittedBy: { select: { id: true, name: true, username: true } }, reviewedBy: { select: { id: true, name: true, username: true } } }, orderBy: { createdAt: 'desc' } }).catch(() => []),
          db.retirementRequest.findMany({ where: whereClause, include: { employee: { select: { id: true, name: true, zanId: true, institution: { select: { id: true, name: true } } } }, submittedBy: { select: { id: true, name: true, username: true } }, reviewedBy: { select: { id: true, name: true, username: true } } }, orderBy: { createdAt: 'desc' } }).catch(() => []),
          db.resignationRequest.findMany({ where: whereClause, include: { employee: { select: { id: true, name: true, zanId: true, institution: { select: { id: true, name: true } } } }, submittedBy: { select: { id: true, name: true, username: true } }, reviewedBy: { select: { id: true, name: true, username: true } } }, orderBy: { createdAt: 'desc' } }).catch(() => []),
          db.serviceExtensionRequest.findMany({ where: whereClause, include: { employee: { select: { id: true, name: true, zanId: true, institution: { select: { id: true, name: true } } } }, submittedBy: { select: { id: true, name: true, username: true } }, reviewedBy: { select: { id: true, name: true, username: true } } }, orderBy: { createdAt: 'desc' } }).catch(() => []),
          db.separationRequest.findMany({ where: whereClause, include: { employee: { select: { id: true, name: true, zanId: true, institution: { select: { id: true, name: true } } } }, submittedBy: { select: { id: true, name: true, username: true } }, reviewedBy: { select: { id: true, name: true, username: true } } }, orderBy: { createdAt: 'desc' } }).catch(() => [])
        ]);

        reportData = [
          ...confirmations.map(req => ({ ...req, requestType: 'Confirmation' })),
          ...promotions.map(req => ({ ...req, requestType: 'Promotion' })),
          ...lwops.map(req => ({ ...req, requestType: 'LWOP' })),
          ...cadreChanges.map(req => ({ ...req, requestType: 'Cadre Change' })),
          ...retirements.map(req => ({ ...req, requestType: 'Retirement' })),
          ...resignations.map(req => ({ ...req, requestType: 'Resignation' })),
          ...serviceExtensions.map(req => ({ ...req, requestType: 'Service Extension' })),
          ...terminations.map(req => ({ ...req, requestType: 'Termination' }))
        ].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        break;

      default:
        return NextResponse.json({ 
          success: false, 
          message: 'Invalid report type' 
        }, { status: 400 });
    }

    return NextResponse.json({
      success: true,
      data: reportData,
      reportType,
      filters: {
        fromDate,
        toDate,
        institutionId
      },
      count: reportData.length
    });

  } catch (error) {
    console.error("[REPORTS_GET]", error);
    return NextResponse.json({ 
      success: false, 
      message: 'Internal Server Error',
      error: error instanceof Error ? error.message : 'Unknown error'
    }, { status: 500 });
  }
}