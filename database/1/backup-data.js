const { PrismaClient } = require('../frontend/node_modules/@prisma/client');
const fs = require('fs');
const path = require('path');

const prisma = new PrismaClient();

async function exportData() {
  try {
    console.log('Starting data export...');

    // Export all tables
    const data = {
      institutions: await prisma.institution.findMany(),
      users: await prisma.user.findMany(),
      employees: await prisma.employee.findMany(),
      employeeCertificates: await prisma.employeeCertificate.findMany(),
      confirmationRequests: await prisma.confirmationRequest.findMany(),
      promotionRequests: await prisma.promotionRequest.findMany(),
      lwopRequests: await prisma.lwopRequest.findMany(),
      cadreChangeRequests: await prisma.cadreChangeRequest.findMany(),
      retirementRequests: await prisma.retirementRequest.findMany(),
      resignationRequests: await prisma.resignationRequest.findMany(),
      serviceExtensionRequests: await prisma.serviceExtensionRequest.findMany(),
      separationRequests: await prisma.separationRequest.findMany(),
      complaints: await prisma.complaint.findMany(),
      notifications: await prisma.notification.findMany()
    };

    // Write data to JSON file
    const outputPath = path.join(__dirname, 'csms-data-export.json');
    fs.writeFileSync(outputPath, JSON.stringify(data, null, 2));

    // Create SQL insert statements
    const sqlPath = path.join(__dirname, 'csms-data-export.sql');
    let sqlContent = `-- CSMS Database Data Export
-- Generated on: ${new Date().toISOString()}
-- Database: prizma
-- Total records exported: ${Object.values(data).reduce((sum, arr) => sum + arr.length, 0)}

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- Clear existing data
TRUNCATE TABLE "Notification", "Complaint", "SeparationRequest", "ServiceExtensionRequest", 
"ResignationRequest", "RetirementRequest", "CadreChangeRequest", "LwopRequest", 
"PromotionRequest", "ConfirmationRequest", "EmployeeCertificate", "Employee", "User", "Institution" CASCADE;

`;

    // Generate SQL inserts for each table
    if (data.institutions.length > 0) {
      sqlContent += `\n-- Insert Institutions\n`;
      data.institutions.forEach(record => {
        const values = [record.id, record.name].map(v => v === null ? 'NULL' : `'${String(v).replace(/'/g, "''")}'`).join(', ');
        sqlContent += `INSERT INTO "Institution" ("id", "name") VALUES (${values});\n`;
      });
    }

    if (data.users.length > 0) {
      sqlContent += `\n-- Insert Users\n`;
      data.users.forEach(record => {
        const values = [
          record.id, record.name, record.username, record.password, record.role,
          record.active, record.employeeId, record.institutionId,
          record.createdAt?.toISOString(), record.updatedAt?.toISOString()
        ].map(v => v === null ? 'NULL' : `'${String(v).replace(/'/g, "''")}'`).join(', ');
        sqlContent += `INSERT INTO "User" ("id", "name", "username", "password", "role", "active", "employeeId", "institutionId", "createdAt", "updatedAt") VALUES (${values});\n`;
      });
    }

    if (data.employees.length > 0) {
      sqlContent += `\n-- Insert Employees\n`;
      data.employees.forEach(record => {
        const values = [
          record.id, record.employeeEntityId, record.name, record.gender, record.profileImageUrl,
          record.dateOfBirth?.toISOString(), record.placeOfBirth, record.region, record.countryOfBirth,
          record.zanId, record.phoneNumber, record.contactAddress, record.zssfNumber, record.payrollNumber,
          record.cadre, record.salaryScale, record.ministry, record.department, record.appointmentType,
          record.contractType, record.recentTitleDate?.toISOString(), record.currentReportingOffice,
          record.currentWorkplace, record.employmentDate?.toISOString(), record.confirmationDate?.toISOString(),
          record.retirementDate?.toISOString(), record.status, record.ardhilHaliUrl, record.confirmationLetterUrl,
          record.jobContractUrl, record.birthCertificateUrl, record.institutionId
        ].map(v => v === null ? 'NULL' : `'${String(v).replace(/'/g, "''")}'`).join(', ');
        sqlContent += `INSERT INTO "Employee" ("id", "employeeEntityId", "name", "gender", "profileImageUrl", "dateOfBirth", "placeOfBirth", "region", "countryOfBirth", "zanId", "phoneNumber", "contactAddress", "zssfNumber", "payrollNumber", "cadre", "salaryScale", "ministry", "department", "appointmentType", "contractType", "recentTitleDate", "currentReportingOffice", "currentWorkplace", "employmentDate", "confirmationDate", "retirementDate", "status", "ardhilHaliUrl", "confirmationLetterUrl", "jobContractUrl", "birthCertificateUrl", "institutionId") VALUES (${values});\n`;
      });
    }

    // Add other tables...
    const tables = [
      { name: 'EmployeeCertificate', data: data.employeeCertificates, columns: ['id', 'type', 'name', 'url', 'employeeId'] },
      { name: 'ConfirmationRequest', data: data.confirmationRequests, columns: ['id', 'status', 'reviewStage', 'documents', 'rejectionReason', 'employeeId', 'submittedById', 'reviewedById', 'decisionDate', 'commissionDecisionDate', 'createdAt', 'updatedAt'] },
      { name: 'PromotionRequest', data: data.promotionRequests, columns: ['id', 'status', 'reviewStage', 'proposedCadre', 'promotionType', 'studiedOutsideCountry', 'documents', 'rejectionReason', 'employeeId', 'submittedById', 'reviewedById', 'createdAt', 'updatedAt', 'commissionDecisionReason'] },
      { name: 'LwopRequest', data: data.lwopRequests, columns: ['id', 'status', 'reviewStage', 'duration', 'reason', 'documents', 'rejectionReason', 'employeeId', 'submittedById', 'reviewedById', 'createdAt', 'updatedAt', 'endDate', 'startDate'] },
      { name: 'CadreChangeRequest', data: data.cadreChangeRequests, columns: ['id', 'status', 'reviewStage', 'newCadre', 'reason', 'studiedOutsideCountry', 'documents', 'rejectionReason', 'employeeId', 'submittedById', 'reviewedById', 'createdAt', 'updatedAt'] },
      { name: 'RetirementRequest', data: data.retirementRequests, columns: ['id', 'status', 'reviewStage', 'retirementType', 'illnessDescription', 'proposedDate', 'delayReason', 'documents', 'rejectionReason', 'employeeId', 'submittedById', 'reviewedById', 'createdAt', 'updatedAt'] },
      { name: 'ResignationRequest', data: data.resignationRequests, columns: ['id', 'status', 'reviewStage', 'effectiveDate', 'reason', 'documents', 'rejectionReason', 'employeeId', 'submittedById', 'reviewedById', 'createdAt', 'updatedAt'] },
      { name: 'ServiceExtensionRequest', data: data.serviceExtensionRequests, columns: ['id', 'status', 'reviewStage', 'currentRetirementDate', 'requestedExtensionPeriod', 'justification', 'documents', 'rejectionReason', 'employeeId', 'submittedById', 'reviewedById', 'createdAt', 'updatedAt'] },
      { name: 'SeparationRequest', data: data.separationRequests, columns: ['id', 'type', 'status', 'reviewStage', 'reason', 'documents', 'rejectionReason', 'employeeId', 'submittedById', 'reviewedById', 'createdAt', 'updatedAt'] },
      { name: 'Complaint', data: data.complaints, columns: ['id', 'complaintType', 'subject', 'details', 'complainantPhoneNumber', 'nextOfKinPhoneNumber', 'attachments', 'status', 'reviewStage', 'officerComments', 'internalNotes', 'rejectionReason', 'complainantId', 'assignedOfficerRole', 'reviewedById', 'createdAt', 'updatedAt'] },
      { name: 'Notification', data: data.notifications, columns: ['id', 'message', 'link', 'isRead', 'userId', 'createdAt'] }
    ];

    tables.forEach(table => {
      if (table.data.length > 0) {
        sqlContent += `\n-- Insert ${table.name}\n`;
        table.data.forEach(record => {
          const values = table.columns.map(col => {
            const value = record[col];
            if (value === null || value === undefined) return 'NULL';
            if (value instanceof Date) return `'${value.toISOString()}'`;
            if (Array.isArray(value)) return `'${JSON.stringify(value).replace(/'/g, "''")}'`;
            if (typeof value === 'boolean') return value.toString();
            return `'${String(value).replace(/'/g, "''")}'`;
          }).join(', ');
          sqlContent += `INSERT INTO "${table.name}" ("${table.columns.join('", "')}") VALUES (${values});\n`;
        });
      }
    });

    fs.writeFileSync(sqlPath, sqlContent);

    console.log(`Data export completed!`);
    console.log(`- JSON export: ${outputPath}`);
    console.log(`- SQL export: ${sqlPath}`);
    console.log(`- Total records: ${Object.values(data).reduce((sum, arr) => sum + arr.length, 0)}`);

    // Print summary
    Object.entries(data).forEach(([table, records]) => {
      console.log(`  ${table}: ${records.length} records`);
    });

  } catch (error) {
    console.error('Export failed:', error);
  } finally {
    await prisma.$disconnect();
  }
}

exportData();