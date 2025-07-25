-- CSMS Database Schema Backup
-- Generated on: 2025-07-22
-- Database: prizma
-- PostgreSQL Schema Creation Script

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- Create database
-- CREATE DATABASE prizma WITH ENCODING 'UTF8';
-- \c prizma;

-- Enable extensions if needed
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Drop existing tables if they exist
DROP TABLE IF EXISTS "Notification" CASCADE;
DROP TABLE IF EXISTS "Complaint" CASCADE;
DROP TABLE IF EXISTS "SeparationRequest" CASCADE;
DROP TABLE IF EXISTS "ServiceExtensionRequest" CASCADE;
DROP TABLE IF EXISTS "ResignationRequest" CASCADE;
DROP TABLE IF EXISTS "RetirementRequest" CASCADE;
DROP TABLE IF EXISTS "CadreChangeRequest" CASCADE;
DROP TABLE IF EXISTS "LwopRequest" CASCADE;
DROP TABLE IF EXISTS "PromotionRequest" CASCADE;
DROP TABLE IF EXISTS "ConfirmationRequest" CASCADE;
DROP TABLE IF EXISTS "EmployeeCertificate" CASCADE;
DROP TABLE IF EXISTS "Employee" CASCADE;
DROP TABLE IF EXISTS "User" CASCADE;
DROP TABLE IF EXISTS "Institution" CASCADE;

-- Create Institution table
CREATE TABLE "Institution" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "name" TEXT NOT NULL UNIQUE
);

-- Create User table
CREATE TABLE "User" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "name" TEXT NOT NULL,
    "username" TEXT NOT NULL UNIQUE,
    "password" TEXT NOT NULL,
    "role" TEXT NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "employeeId" TEXT UNIQUE,
    "institutionId" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "User_institutionId_fkey" FOREIGN KEY ("institutionId") REFERENCES "Institution"("id") ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Create Employee table
CREATE TABLE "Employee" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "employeeEntityId" TEXT,
    "name" TEXT NOT NULL,
    "gender" TEXT NOT NULL,
    "profileImageUrl" TEXT,
    "dateOfBirth" TIMESTAMP(3),
    "placeOfBirth" TEXT,
    "region" TEXT,
    "countryOfBirth" TEXT,
    "zanId" TEXT NOT NULL UNIQUE,
    "phoneNumber" TEXT,
    "contactAddress" TEXT,
    "zssfNumber" TEXT,
    "payrollNumber" TEXT,
    "cadre" TEXT,
    "salaryScale" TEXT,
    "ministry" TEXT,
    "department" TEXT,
    "appointmentType" TEXT,
    "contractType" TEXT,
    "recentTitleDate" TIMESTAMP(3),
    "currentReportingOffice" TEXT,
    "currentWorkplace" TEXT,
    "employmentDate" TIMESTAMP(3),
    "confirmationDate" TIMESTAMP(3),
    "retirementDate" TIMESTAMP(3),
    "status" TEXT,
    "ardhilHaliUrl" TEXT,
    "confirmationLetterUrl" TEXT,
    "jobContractUrl" TEXT,
    "birthCertificateUrl" TEXT,
    "institutionId" TEXT NOT NULL,
    CONSTRAINT "Employee_institutionId_fkey" FOREIGN KEY ("institutionId") REFERENCES "Institution"("id") ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Add foreign key for User.employeeId after Employee table is created
ALTER TABLE "User" ADD CONSTRAINT "User_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- Create EmployeeCertificate table
CREATE TABLE "EmployeeCertificate" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "type" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "url" TEXT,
    "employeeId" TEXT NOT NULL,
    CONSTRAINT "EmployeeCertificate_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee"("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create ConfirmationRequest table
CREATE TABLE "ConfirmationRequest" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "status" TEXT NOT NULL,
    "reviewStage" TEXT NOT NULL,
    "documents" TEXT[] NOT NULL,
    "rejectionReason" TEXT,
    "employeeId" TEXT NOT NULL,
    "submittedById" TEXT NOT NULL,
    "reviewedById" TEXT,
    "decisionDate" TIMESTAMP(3),
    "commissionDecisionDate" TIMESTAMP(3),
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "ConfirmationRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "ConfirmationRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "ConfirmationRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE
);

-- Create PromotionRequest table
CREATE TABLE "PromotionRequest" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "status" TEXT NOT NULL,
    "reviewStage" TEXT NOT NULL,
    "proposedCadre" TEXT NOT NULL,
    "promotionType" TEXT NOT NULL,
    "studiedOutsideCountry" BOOLEAN,
    "documents" TEXT[] NOT NULL,
    "rejectionReason" TEXT,
    "employeeId" TEXT NOT NULL,
    "submittedById" TEXT NOT NULL,
    "reviewedById" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "commissionDecisionReason" TEXT,
    CONSTRAINT "PromotionRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "PromotionRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "PromotionRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE
);

-- Create LwopRequest table
CREATE TABLE "LwopRequest" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "status" TEXT NOT NULL,
    "reviewStage" TEXT NOT NULL,
    "duration" TEXT NOT NULL,
    "reason" TEXT NOT NULL,
    "documents" TEXT[] NOT NULL,
    "rejectionReason" TEXT,
    "employeeId" TEXT NOT NULL,
    "submittedById" TEXT NOT NULL,
    "reviewedById" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "endDate" TIMESTAMP(3),
    "startDate" TIMESTAMP(3),
    CONSTRAINT "LwopRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "LwopRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "LwopRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE
);

-- Create CadreChangeRequest table
CREATE TABLE "CadreChangeRequest" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "status" TEXT NOT NULL,
    "reviewStage" TEXT NOT NULL,
    "newCadre" TEXT NOT NULL,
    "reason" TEXT,
    "studiedOutsideCountry" BOOLEAN,
    "documents" TEXT[] NOT NULL,
    "rejectionReason" TEXT,
    "employeeId" TEXT NOT NULL,
    "submittedById" TEXT NOT NULL,
    "reviewedById" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "CadreChangeRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "CadreChangeRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "CadreChangeRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE
);

-- Create RetirementRequest table
CREATE TABLE "RetirementRequest" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "status" TEXT NOT NULL,
    "reviewStage" TEXT NOT NULL,
    "retirementType" TEXT NOT NULL,
    "illnessDescription" TEXT,
    "proposedDate" TIMESTAMP(3) NOT NULL,
    "delayReason" TEXT,
    "documents" TEXT[] NOT NULL,
    "rejectionReason" TEXT,
    "employeeId" TEXT NOT NULL,
    "submittedById" TEXT NOT NULL,
    "reviewedById" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "RetirementRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "RetirementRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "RetirementRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE
);

-- Create ResignationRequest table
CREATE TABLE "ResignationRequest" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "status" TEXT NOT NULL,
    "reviewStage" TEXT NOT NULL,
    "effectiveDate" TIMESTAMP(3) NOT NULL,
    "reason" TEXT,
    "documents" TEXT[] NOT NULL,
    "rejectionReason" TEXT,
    "employeeId" TEXT NOT NULL,
    "submittedById" TEXT NOT NULL,
    "reviewedById" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "ResignationRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "ResignationRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "ResignationRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE
);

-- Create ServiceExtensionRequest table
CREATE TABLE "ServiceExtensionRequest" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "status" TEXT NOT NULL,
    "reviewStage" TEXT NOT NULL,
    "currentRetirementDate" TIMESTAMP(3) NOT NULL,
    "requestedExtensionPeriod" TEXT NOT NULL,
    "justification" TEXT NOT NULL,
    "documents" TEXT[] NOT NULL,
    "rejectionReason" TEXT,
    "employeeId" TEXT NOT NULL,
    "submittedById" TEXT NOT NULL,
    "reviewedById" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "ServiceExtensionRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "ServiceExtensionRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "ServiceExtensionRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE
);

-- Create SeparationRequest table
CREATE TABLE "SeparationRequest" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "type" TEXT NOT NULL,
    "status" TEXT NOT NULL,
    "reviewStage" TEXT NOT NULL,
    "reason" TEXT NOT NULL,
    "documents" TEXT[] NOT NULL,
    "rejectionReason" TEXT,
    "employeeId" TEXT NOT NULL,
    "submittedById" TEXT NOT NULL,
    "reviewedById" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "SeparationRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "SeparationRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "SeparationRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE
);

-- Create Complaint table
CREATE TABLE "Complaint" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "complaintType" TEXT NOT NULL,
    "subject" TEXT NOT NULL,
    "details" TEXT NOT NULL,
    "complainantPhoneNumber" TEXT NOT NULL,
    "nextOfKinPhoneNumber" TEXT NOT NULL,
    "attachments" TEXT[] NOT NULL,
    "status" TEXT NOT NULL,
    "reviewStage" TEXT NOT NULL,
    "officerComments" TEXT,
    "internalNotes" TEXT,
    "rejectionReason" TEXT,
    "complainantId" TEXT NOT NULL,
    "assignedOfficerRole" TEXT NOT NULL,
    "reviewedById" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "Complaint_complainantId_fkey" FOREIGN KEY ("complainantId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "Complaint_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE
);

-- Create Notification table
CREATE TABLE "Notification" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "message" TEXT NOT NULL,
    "link" TEXT,
    "isRead" BOOLEAN NOT NULL DEFAULT false,
    "userId" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "Notification_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create indexes for better performance
CREATE INDEX "User_username_idx" ON "User"("username");
CREATE INDEX "User_employeeId_idx" ON "User"("employeeId");
CREATE INDEX "User_institutionId_idx" ON "User"("institutionId");
CREATE INDEX "Employee_zanId_idx" ON "Employee"("zanId");
CREATE INDEX "Employee_institutionId_idx" ON "Employee"("institutionId");
CREATE INDEX "Employee_payrollNumber_idx" ON "Employee"("payrollNumber");
CREATE INDEX "Employee_zssfNumber_idx" ON "Employee"("zssfNumber");
CREATE INDEX "EmployeeCertificate_employeeId_idx" ON "EmployeeCertificate"("employeeId");
CREATE INDEX "ConfirmationRequest_employeeId_idx" ON "ConfirmationRequest"("employeeId");
CREATE INDEX "ConfirmationRequest_submittedById_idx" ON "ConfirmationRequest"("submittedById");
CREATE INDEX "PromotionRequest_employeeId_idx" ON "PromotionRequest"("employeeId");
CREATE INDEX "PromotionRequest_submittedById_idx" ON "PromotionRequest"("submittedById");
CREATE INDEX "LwopRequest_employeeId_idx" ON "LwopRequest"("employeeId");
CREATE INDEX "LwopRequest_submittedById_idx" ON "LwopRequest"("submittedById");
CREATE INDEX "CadreChangeRequest_employeeId_idx" ON "CadreChangeRequest"("employeeId");
CREATE INDEX "CadreChangeRequest_submittedById_idx" ON "CadreChangeRequest"("submittedById");
CREATE INDEX "RetirementRequest_employeeId_idx" ON "RetirementRequest"("employeeId");
CREATE INDEX "RetirementRequest_submittedById_idx" ON "RetirementRequest"("submittedById");
CREATE INDEX "ResignationRequest_employeeId_idx" ON "ResignationRequest"("employeeId");
CREATE INDEX "ResignationRequest_submittedById_idx" ON "ResignationRequest"("submittedById");
CREATE INDEX "ServiceExtensionRequest_employeeId_idx" ON "ServiceExtensionRequest"("employeeId");
CREATE INDEX "ServiceExtensionRequest_submittedById_idx" ON "ServiceExtensionRequest"("submittedById");
CREATE INDEX "SeparationRequest_employeeId_idx" ON "SeparationRequest"("employeeId");
CREATE INDEX "SeparationRequest_submittedById_idx" ON "SeparationRequest"("submittedById");
CREATE INDEX "Complaint_complainantId_idx" ON "Complaint"("complainantId");
CREATE INDEX "Notification_userId_idx" ON "Notification"("userId");

-- Set up triggers for updatedAt fields
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW."updatedAt" = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_user_updated_at BEFORE UPDATE ON "User" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_confirmation_request_updated_at BEFORE UPDATE ON "ConfirmationRequest" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_promotion_request_updated_at BEFORE UPDATE ON "PromotionRequest" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_lwop_request_updated_at BEFORE UPDATE ON "LwopRequest" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_cadre_change_request_updated_at BEFORE UPDATE ON "CadreChangeRequest" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_retirement_request_updated_at BEFORE UPDATE ON "RetirementRequest" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_resignation_request_updated_at BEFORE UPDATE ON "ResignationRequest" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_service_extension_request_updated_at BEFORE UPDATE ON "ServiceExtensionRequest" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_separation_request_updated_at BEFORE UPDATE ON "SeparationRequest" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_complaint_updated_at BEFORE UPDATE ON "Complaint" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();