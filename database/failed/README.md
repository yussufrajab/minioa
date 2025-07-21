# CSMS Database Backup and Restoration Guide

## System Overview
**Civil Service Management System (CSMS)** - HR Management Platform for Civil Service Commission of Zanzibar

## Database Information
- **Database Name:** `prizma`
- **Database Type:** PostgreSQL 15
- **Default Username:** `postgres`
- **Default Password:** `password123`
- **Port:** `5432`

## Files in this Directory

### 1. Database Dumps
- `csms_schema_only.sql` - Database schema without data
- `csms_data_only.sql` - Data only (without schema)
- `csms_complete.sql` - Complete database with schema and data
- `csms_minimal.sql` - Core schema + essential data only

### 2. Configuration Files
- `database_config.env` - Environment variables for database connection
- `prisma_schema.prisma` - Prisma ORM schema file
- `migration_scripts/` - Database migration scripts

### 3. Restoration Scripts
- `restore_database.sh` - Linux/Mac restoration script
- `restore_database.bat` - Windows restoration script
- `setup_new_environment.md` - Complete VPS setup guide

## Quick Restoration Steps

### Prerequisites
1. PostgreSQL 15 installed and running
2. Node.js 18+ installed
3. Git installed

### Step 1: Create Database
```sql
createdb -U postgres prizma
```

### Step 2: Restore Schema and Data
```bash
psql -U postgres -d prizma -f csms_complete.sql
```

### Step 3: Install Dependencies
```bash
# Frontend
cd frontend
npm install
npx prisma generate

# Backend  
cd backend
mvn clean install
```

### Step 4: Configure Environment
```bash
# Copy environment files
cp database_config.env frontend/.env
```

### Step 5: Run Application
```bash
# Start backend (port 8080)
cd backend
mvn spring-boot:run -Dspring.profiles.active=dev

# Start frontend (port 9002)  
cd frontend
npm run dev
```

## System URLs
- **Frontend:** http://localhost:9002
- **Backend API:** http://localhost:8080/api
- **Prisma Studio:** http://localhost:5555 (run: npx prisma studio)

## Default Users
| Username | Password | Role | Institution |
|----------|----------|------|-------------|
| admin | password123 | ADMIN | System |
| hro1 | password123 | HRO | Various |
| hrmo1 | password123 | HRMO | CSC |
| hhrmd1 | password123 | HHRMD | CSC |

## Database Schema Overview

### Core Tables
- `User` - System users (159 records)
- `Employee` - Employee records (151 records)
- `Institution` - Government institutions (41 records)

### Request Tables
- `ConfirmationRequest` - Employee confirmation requests
- `PromotionRequest` - Promotion requests  
- `LwopRequest` - Leave without pay requests
- `CadreChangeRequest` - Cadre change requests
- `RetirementRequest` - Retirement requests
- `ResignationRequest` - Resignation requests
- `ServiceExtensionRequest` - Service extension requests
- `SeparationRequest` - Termination/dismissal requests

### Other Tables
- `Complaint` - Employee complaints
- `Institution` - Government ministries/departments

## Key Features
- 8 HR Request modules implemented
- Role-based access control (9 user roles)
- Parallel workflow (HRMO/HHRMD work simultaneously)
- Automatic employee status updates on approvals
- File upload support (max 2MB per file)
- JWT authentication (10min access, 24hr refresh tokens)

## Technology Stack
- **Frontend:** Next.js 15, TypeScript, Prisma ORM, Tailwind CSS
- **Backend:** Spring Boot 3.1.5, Java 17, PostgreSQL
- **Authentication:** JWT tokens
- **File Storage:** Local filesystem (./uploads)

## Troubleshooting

### Database Connection Issues
1. Verify PostgreSQL is running: `pg_ctl status`
2. Check credentials in environment files
3. Ensure database 'prizma' exists
4. Verify port 5432 is accessible

### Application Issues  
1. Check if backend is running on port 8080
2. Verify frontend connects to localhost:8080/api (not localhost:9002/api)
3. Run `npx prisma generate` after schema changes
4. Use `dev` profile for backend to create tables automatically

### Common Error Fixes
- **Login fails:** Verify backend uses 'prizma' database
- **API 404:** Frontend should call localhost:8080/api/*
- **Table not found:** Run backend with dev profile first
- **Prisma errors:** Run `npx prisma generate`

## Support
- System developed by Claude Code Assistant
- Full documentation in CLAUDE.md file
- All 8 HR modules fully implemented and tested

## Version Information
- **Created:** January 2025
- **Database Version:** PostgreSQL 15
- **System Status:** Production Ready (85% complete)
- **Last Updated:** $(date)