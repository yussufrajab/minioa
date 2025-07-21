# Civil Service Management System (CSMS) - System Architecture

## System Overview

The CSMS is a three-tier web application designed for the Civil Service Commission of Zanzibar to manage HR operations across all government ministries.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                 CLIENT TIER                                     │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐  │
│  │    Web Browser      │    │    Web Browser      │    │    Web Browser      │  │
│  │   (Admin User)      │    │   (HR Officer)      │    │   (Employee)        │  │
│  │                     │    │                     │    │                     │  │
│  │  - User Management  │    │  - Employee Mgmt    │    │  - View Profile     │  │
│  │  - Institution Mgmt │    │  - Request Review   │    │  - Submit Requests  │  │
│  │  - System Reports   │    │  - Report Generation│    │  - Track Status     │  │
│  └─────────────────────┘    └─────────────────────┘    └─────────────────────┘  │
│             │                          │                          │             │
│             └──────────────────────────┼──────────────────────────┘             │
│                                        │                                        │
│                              HTTP/HTTPS Requests                               │
│                                        │                                        │
└────────────────────────────────────────┼────────────────────────────────────────┘
                                         │
┌────────────────────────────────────────┼────────────────────────────────────────┐
│                              APPLICATION TIER                                  │
├────────────────────────────────────────┼────────────────────────────────────────┤
│                                        │                                        │
│  ┌─────────────────────────────────────┼─────────────────────────────────────┐  │
│  │               Next.js Frontend      │                                     │  │
│  │                 (Port 9002)         │                                     │  │
│  │                                     │                                     │  │
│  │  ┌─────────────────┐               │                                     │  │
│  │  │   Components    │               │                                     │  │
│  │  │  - User Mgmt    │               │                                     │  │
│  │  │  - Navigation   │               │                                     │  │
│  │  │  - Forms        │               │                                     │  │
│  │  └─────────────────┘               │                                     │  │
│  │                                     │                                     │  │
│  │  ┌─────────────────┐               │                                     │  │
│  │  │   Auth Store    │               │                                     │  │
│  │  │  (Zustand)      │               │                                     │  │
│  │  │  - User State   │               │                                     │  │
│  │  │  - Role Info    │               │                                     │  │
│  │  └─────────────────┘               │                                     │  │
│  │                                     │                                     │  │
│  │  ┌─────────────────┐               │                                     │  │
│  │  │   API Client    │               │                                     │  │
│  │  │  - JWT Token    │               │                                     │  │
│  │  │  - HTTP Client  │               │──────────────┐                      │  │
│  │  │  - Error Handling│              │              │                      │  │
│  │  └─────────────────┘               │              │                      │  │
│  └─────────────────────────────────────┘              │                      │  │
│                                                       │                      │  │
│                                        HTTP API Calls │                      │  │
│                                        (JWT Bearer)   │                      │  │
│                                                       │                      │  │
│  ┌─────────────────────────────────────────────────────┼─────────────────────┐  │
│  │               Spring Boot Backend                   │                     │  │
│  │                 (Port 8080)                         │                     │  │
│  │                                                     │                     │  │
│  │  ┌─────────────────┐    ┌─────────────────┐        │                     │  │
│  │  │  REST Controllers│    │  Security Layer │        │                     │  │
│  │  │  - UserController│    │  - JWT Filter   │        │                     │  │
│  │  │  - RequestCtrl   │    │  - CORS Config  │        │                     │  │
│  │  │  - EmployeeCtrl  │    │  - Auth Config  │        │                     │  │
│  │  └─────────────────┘    └─────────────────┘        │                     │  │
│  │                                                     │                     │  │
│  │  ┌─────────────────┐    ┌─────────────────┐        │                     │  │
│  │  │   Service Layer │    │   Data Transfer │        │                     │  │
│  │  │  - UserService  │    │   Objects (DTOs)│        │                     │  │
│  │  │  - AuthService  │    │  - Request/Response      │                     │  │
│  │  │  - RequestSvc   │    │    Mapping      │        │                     │  │
│  │  └─────────────────┘    └─────────────────┘        │                     │  │
│  │                                                     │                     │  │
│  │  ┌─────────────────┐    ┌─────────────────┐        │                     │  │
│  │  │  Repository     │    │  JPA Entities   │        │                     │  │
│  │  │  - UserRepo     │    │  - User Entity  │        │                     │  │
│  │  │  - RequestRepo  │    │  - Employee     │        │                     │  │
│  │  │  - EmployeeRepo │    │  - Institution │        │ JDBC Connection     │  │
│  │  └─────────────────┘    └─────────────────┘        │ Pool (HikariCP)     │  │
│  └─────────────────────────────────────────────────────┼─────────────────────┘  │
│                                                        │                        │
└────────────────────────────────────────────────────────┼────────────────────────┘
                                                         │
┌────────────────────────────────────────────────────────┼────────────────────────┐
│                                DATA TIER               │                        │
├────────────────────────────────────────────────────────┼────────────────────────┤
│                                                        │                        │
│  ┌─────────────────────────────────────────────────────┼─────────────────────┐  │
│  │                  PostgreSQL Database                │                     │  │
│  │                     (Port 5432)                     │                     │  │
│  │                   Database: "prizma"                │                     │  │
│  │                                                     │                     │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐  │  │
│  │  │                      Tables                                         │  │  │
│  │  │                                                                     │  │  │
│  │  │  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐  │  │  │
│  │  │  │     "User"      │    │   "Employee"    │    │  "Institution"  │  │  │  │
│  │  │  │  - id (UUID)    │    │  - id (UUID)    │    │  - id (UUID)    │  │  │  │
│  │  │  │  - username     │    │  - name         │    │  - name         │  │  │  │
│  │  │  │  - password     │    │  - zanId        │    │  - email        │  │  │  │
│  │  │  │  - name         │    │  - department   │    │  - address      │  │  │  │
│  │  │  │  - role         │    │  - ministry     │    │  - telephone    │  │  │  │
│  │  │  │  - active       │    │  - status       │    └─────────────────┘  │  │  │
│  │  │  │  - institutionId│    │  - institutionId│                       │  │  │
│  │  │  └─────────────────┘    └─────────────────┘                       │  │  │
│  │  │                                                                     │  │  │
│  │  │  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐  │  │  │
│  │  │  │"ConfirmationReq"│    │ "PromotionReq"  │    │   "LwopReq"     │  │  │  │
│  │  │  │  - id           │    │  - id           │    │  - id           │  │  │  │
│  │  │  │  - employeeId   │    │  - employeeId   │    │  - employeeId   │  │  │  │
│  │  │  │  - status       │    │  - proposedCadre│    │  - duration     │  │  │  │
│  │  │  │  - reviewStage  │    │  - status       │    │  - reason       │  │  │  │
│  │  │  │  - documents    │    │  - reviewStage  │    │  - status       │  │  │  │
│  │  │  └─────────────────┘    └─────────────────┘    └─────────────────┘  │  │  │
│  │  │                                                                     │  │  │
│  │  │  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐  │  │  │
│  │  │  │ "RetirementReq" │    │"ServiceExtReq"  │    │ "TerminationReq"│  │  │  │
│  │  │  │  - id           │    │  - id           │    │  - id           │  │  │  │
│  │  │  │  - employeeId   │    │  - employeeId   │    │  - employeeId   │  │  │  │
│  │  │  │  - type         │    │  - extension    │    │  - reason       │  │  │  │
│  │  │  │  - status       │    │  - justification│    │  - status       │  │  │  │
│  │  │  └─────────────────┘    └─────────────────┘    └─────────────────┘  │  │  │
│  │  └─────────────────────────────────────────────────────────────────────┘  │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│                                                                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │                           Indexes                                       │  │
│  │  - Users by username (unique)                                           │  │
│  │  - Employees by zanId (unique)                                          │  │
│  │  - Requests by employeeId                                               │  │
│  │  - Requests by status                                                   │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────────────┘
```

## Data Flow Explanation

### 1. Authentication Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Browser   │    │  Frontend   │    │   Backend   │    │  Database   │
│             │    │   (Next.js) │    │(Spring Boot)│    │(PostgreSQL) │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
       │                  │                  │                  │
       │ 1. Login Request │                  │                  │
       ├─────────────────→│                  │                  │
       │                  │ 2. JWT Token     │                  │
       │                  │   (Pre-generated)│                  │
       │                  │                  │                  │
       │                  │ 3. API Request   │                  │
       │                  │   + Bearer Token │                  │
       │                  ├─────────────────→│                  │
       │                  │                  │ 4. Validate JWT  │
       │                  │                  │   & Load User    │
       │                  │                  ├─────────────────→│
       │                  │                  │ 5. User Data     │
       │                  │                  │←─────────────────┤
       │                  │ 6. API Response  │                  │
       │                  │←─────────────────┤                  │
       │ 7. UI Update     │                  │                  │
       │←─────────────────┤                  │                  │
```

### 2. User Management Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│Admin Browser│    │  Frontend   │    │   Backend   │    │  Database   │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
       │                  │                  │                  │
       │ 1. Access Users  │                  │                  │
       │    Page          │                  │                  │
       ├─────────────────→│                  │                  │
       │                  │ 2. GET /api/users│                  │
       │                  │   Authorization: │                  │
       │                  │   Bearer <token> │                  │
       │                  ├─────────────────→│                  │
       │                  │                  │ 3. JWT Validation│
       │                  │                  │   Extract username│
       │                  │                  │                  │
       │                  │                  │ 4. Load User     │
       │                  │                  │   From Database  │
       │                  │                  ├─────────────────→│
       │                  │                  │ 5. User Entity   │
       │                  │                  │←─────────────────┤
       │                  │                  │ 6. Check Role    │
       │                  │                  │   (Admin Access) │
       │                  │                  │                  │
       │                  │                  │ 7. Query All     │
       │                  │                  │   Users          │
       │                  │                  ├─────────────────→│
       │                  │                  │ 8. Users List    │
       │                  │                  │←─────────────────┤
       │                  │ 9. JSON Response │                  │
       │                  │   with Users     │                  │
       │                  │←─────────────────┤                  │
       │ 10. Display Users│                  │                  │
       │     in Table     │                  │                  │
       │←─────────────────┤                  │                  │
```

## Component Details

### Frontend Architecture (Next.js 15)

**Key Components:**
- **Pages**: App router structure (`/dashboard/admin/users`)
- **Components**: Reusable UI components with Radix UI
- **State Management**: Zustand stores for authentication and app state
- **API Client**: Centralized HTTP client with JWT token management
- **Styling**: Tailwind CSS with shadcn/ui components

**Authentication State:**
```javascript
// Frontend Auth Store
{
  user: {
    id: "admin-backend-id",
    name: "System Administrator", 
    username: "admin",
    role: "Admin",
    institutionId: "...",
    institution: { name: "TUME YA UTUMISHI SERIKALINI" }
  },
  isAuthenticated: true,
  accessToken: "eyJhbGci..." // JWT Token
}
```

### Backend Architecture (Spring Boot 3.1.5)

**Security Configuration:**
- **JWT Authentication**: HS512 algorithm with 10-minute access tokens
- **Role-based Authorization**: Method-level security with roles
- **CORS Configuration**: Allows frontend origin (localhost:9002)

**Data Layer:**
- **JPA Entities**: Match Prisma schema with quoted table names
- **Repository Pattern**: Spring Data JPA repositories
- **Transaction Management**: @Transactional for data consistency

**API Structure:**
```
/api/auth/login     - Authentication endpoint
/api/users          - User management (Admin only)
/api/employees      - Employee data access
/api/institutions   - Institution management
/api/*-requests     - HR request endpoints
/api/notifications  - User notifications
/api/reports        - Report generation
```

### Database Schema (PostgreSQL)

**Key Tables:**
- **"User"**: System users with roles and authentication
- **"Employee"**: Employee records with personal/professional data  
- **"Institution"**: Government ministries and departments
- **Request Tables**: Various HR request types (8 modules)

**Relationships:**
- User ↔ Institution (Many-to-One)
- Employee ↔ Institution (Many-to-One)  
- Employee ↔ Requests (One-to-Many)
- User ↔ Requests (One-to-Many, as submitter/reviewer)

## Network Configuration

**Ports:**
- Frontend: `http://localhost:9002`
- Backend: `http://localhost:8080`
- Database: `localhost:5432`

**API Base URL:** `http://localhost:8080/api`

**CORS Policy:** 
- Allowed Origins: `http://localhost:9002`, `http://localhost:3000`
- Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
- Credentials: Included for session management

## Security Features

1. **JWT Authentication**: Stateless token-based auth
2. **Role-based Access Control**: 9 user roles with specific permissions
3. **Password Encryption**: BCrypt hashing for stored passwords
4. **CORS Protection**: Configured allowed origins
5. **Input Validation**: Both frontend (Zod) and backend (Bean Validation)
6. **SQL Injection Protection**: Parameterized queries via JPA

## Current Configuration Issues Resolved

1. **Database Mismatch**: Backend now uses Prisma schema tables
2. **User Synchronization**: Single admin user works in both systems
3. **JWT Token**: Valid token generated with correct secret and algorithm
4. **Authentication Flow**: Frontend auth state synced with backend JWT validation

This architecture provides a robust, scalable foundation for the Civil Service Management System with proper separation of concerns and security implementations.