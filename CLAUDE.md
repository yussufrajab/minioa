# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the Civil Service Management System (CSMS) - a comprehensive HR management platform for the Civil Service Commission of Zanzibar. It's a three-tier web application (85% complete) that manages employee data across all government ministries.

**Architecture**:
- Frontend: Next.js 15 with TypeScript, Prisma ORM, Tailwind CSS
- Backend: Spring Boot 3.1.5 (Java 17) REST API
- Database: PostgreSQL 15 (database name: `prizma`)

## Essential Commands

### Frontend Development (from `/frontend` directory)
```bash
npm run dev        # Start dev server on port 9002
npm run build      # Production build
npm run lint       # Run linting
npm run typecheck  # TypeScript checking

# Database management
npx prisma generate     # Generate Prisma client
npx prisma migrate dev  # Run migrations
npx prisma studio       # Open database GUI
```

### Backend Development (from `/backend` directory)
```bash
mvn spring-boot:run                             # Run with default profile
mvn spring-boot:run -Dspring.profiles.active=dev  # Run with dev profile (creates tables)
mvn clean install                               # Build and test
mvn test                                        # Run tests
```

### Full Stack Setup
1. Ensure PostgreSQL is running (port 5432)
2. Start backend: `cd backend && mvn spring-boot:run`
3. Start frontend: `cd frontend && npm run dev`
4. Access: Frontend at http://localhost:9002, API at http://localhost:8080/api

## Critical Configuration

**Database Credentials**:
- Database: `prizma` (NOT `csms_db`)
- Username: `postgres`
- Password: `Mamlaka2020`
- URL: `postgresql://postgres:Mamlaka2020@localhost:5432/prizma`

**Important**: The frontend and backend share the same database. Frontend uses Prisma ORM, backend uses JPA/Hibernate.

## Architecture & Key Components

### Backend Structure (`/backend/src/main/java/com/zanzibar/csms/`)
- `controller/` - REST endpoints (all prefixed with `/api`)
- `service/` - Business logic with transaction management
- `repository/` - Data access layer
- `dto/` - Data transfer objects
- `entity/` - JPA entities matching Prisma schema
- `security/` - JWT authentication (10min access, 24hr refresh tokens)

### Frontend Structure (`/frontend/src/`)
- `app/` - Next.js app router pages
- `components/ui/` - Radix UI-based components
- `lib/api-client.ts` - Axios client for backend communication
- `store/` - Zustand state management
- `hooks/` - Custom React hooks

### User Roles (9 implemented)
- ADMIN - System administrator
- HRO - HR Officer (institution-specific)
- HHRMD - Head of HR Management (cross-institutional)
- HRMO - HR Management Officer (cross-institutional)
- DO - Disciplinary Officer
- EMPLOYEE - Basic employee
- CSCS - Civil Service Commission Secretary
- HRRP - HR Responsible Personnel
- PO - Planning Officer (read-only)

### HR Request Modules (All 8 implemented)
1. Employee Confirmation (12-month probation)
2. Leave Without Pay (1 month - 3 years)
3. Promotion (education/performance-based)
4. Cadre Change
5. Retirement (compulsory/voluntary/illness)
6. Resignation
7. Service Extension
8. Termination/Dismissal

### Workflow States
DRAFT → SUBMITTED → HRO_REVIEW → HRMO_REVIEW → HHRMD_REVIEW → APPROVED/REJECTED

## Development Guidelines

1. **Database Profiles**: Use `dev` profile for table creation, `prod` for validation only
2. **API Endpoints**: All backend endpoints are under `/api/*` context path
3. **Authentication**: JWT tokens required for all endpoints except `/api/auth/*`
4. **File Uploads**: Max 2MB per file, stored in `./uploads` directory
5. **Logging**: Check `logs/csms.log` for backend issues

## Common Issues

1. **Login fails**: Verify backend is using `prizma` database, not `csms_db`
2. **API 404 errors**: Frontend should call `localhost:8080/api/*`, not `localhost:9002/api/*`
3. **Table not found**: Run backend with `dev` profile first to create tables
4. **Prisma client errors**: Run `npx prisma generate` after schema changes

## Testing Data

- Default password for test users: `password123`
- 159 users, 151 employees pre-seeded
- 41 government institutions configured

## Key Files for Reference

- Database schema: `/frontend/prisma/schema.prisma`
- API client config: `/frontend/src/lib/api-client.ts`
- Backend config: `/backend/src/main/resources/application.properties`
- User authentication: `/backend/src/main/java/com/zanzibar/csms/security/`