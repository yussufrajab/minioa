# CSMS System Architecture - Comprehensive Diagram

## Overall System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                 CLIENT LAYER                                         │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐       │
│  │   Web Browser       │  │   Mobile Browser    │  │   Desktop App       │       │
│  │   (Chrome/Firefox)  │  │   (Mobile Safari)   │  │   (Future)          │       │
│  └─────────────────────┘  └─────────────────────┘  └─────────────────────┘       │
│                                          │                                          │
│                                          │ HTTPS/HTTP                              │
│                                          ▼                                          │
└─────────────────────────────────────────────────────────────────────────────────────┘
                                          │
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              FRONTEND LAYER (Next.js)                               │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                    Port: 9002                                      │
│                                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                            User Interface Layer                             │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│  │  │ Dashboard   │ │ Employee    │ │ Requests    │ │ Admin Panel         │  │   │
│  │  │ Components  │ │ Management  │ │ Management  │ │ (User/Role Mgmt)    │  │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                          │                                          │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                        State Management Layer                               │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│  │  │ Zustand     │ │ React Query │ │ Form State  │ │ Local Storage       │  │   │
│  │  │ Auth Store  │ │ Data Cache  │ │ Validation  │ │ Persistence         │  │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                          │                                          │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                          API Client Layer                                   │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│  │  │ HTTP Client │ │ Error       │ │ Request     │ │ Response            │  │   │
│  │  │ (fetch API) │ │ Handling    │ │ Interceptor │ │ Transformation      │  │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                          │                                          │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                         Database Access Layer                               │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│  │  │ Prisma ORM  │ │ Connection  │ │ Query       │ │ Type Generation     │  │   │
│  │  │ Client      │ │ Pooling     │ │ Builder     │ │ & Validation        │  │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────┘
                                          │
                                          │ HTTP/JSON API Calls
                                          │ Database Connections
                                          ▼
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              BACKEND LAYER (Spring Boot)                            │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                    Port: 8080                                      │
│                                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                            API Gateway Layer                                │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│  │  │ REST        │ │ Security    │ │ CORS        │ │ Request Logging     │  │   │
│  │  │ Controllers │ │ Filters     │ │ Config      │ │ & Monitoring        │  │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                          │                                          │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                           Business Logic Layer                              │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│  │  │ Service     │ │ Workflow    │ │ Validation  │ │ Business Rules      │  │   │
│  │  │ Classes     │ │ Engine      │ │ Logic       │ │ Engine              │  │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│  │                                                                             │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│  │  │ Request     │ │ Employee    │ │ User        │ │ Audit & Logging     │  │   │
│  │  │ Service     │ │ Service     │ │ Service     │ │ Service             │  │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                          │                                          │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                         Data Access Layer                                   │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│  │  │ JPA/        │ │ Entity      │ │ Repository  │ │ Connection Pool     │  │   │
│  │  │ Hibernate   │ │ Mapping     │ │ Pattern     │ │ (HikariCP)          │  │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────┘
                                          │
                                          │ JDBC Connections
                                          │ SQL Queries
                                          ▼
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                DATABASE LAYER                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                              PostgreSQL Database                                   │
│                                  Port: 5432                                        │
│                                Database: prizma                                     │
│                                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                              Core Tables                                    │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│  │  │ Employee    │ │ Institution │ │ User        │ │ EmployeeCertificate │  │   │
│  │  │ - id        │ │ - id        │ │ - id        │ │ - id                │  │   │
│  │  │ - name      │ │ - name      │ │ - username  │ │ - employeeId        │  │   │
│  │  │ - zanId     │ │ - location  │ │ - role      │ │ - type              │  │   │
│  │  │ - status    │ │ - type      │ │ - isActive  │ │ - name              │  │   │
│  │  │ - instId    │ │             │ │ - empId     │ │ - url               │  │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                          │                                          │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                           Request Management Tables                          │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│  │  │Confirmation │ │ Promotion   │ │ Retirement  │ │ ResignationRequest  │  │   │
│  │  │Request      │ │ Request     │ │ Request     │ │ - id                │  │   │
│  │  │ - id        │ │ - id        │ │ - id        │ │ - employeeId        │  │   │
│  │  │ - empId     │ │ - empId     │ │ - empId     │ │ - reason            │  │   │
│  │  │ - status    │ │ - fromPos   │ │ - retDate   │ │ - effectiveDate     │  │   │
│  │  │ - stage     │ │ - toPos     │ │ - pensionOk │ │ - status            │  │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                          │                                          │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                             Additional Tables                                │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│  │  │ LwopRequest │ │ Complaint   │ │ Notification│ │ ServiceExtension    │  │   │
│  │  │ SeparationRequest           │ │ CadreChangeRequest              │  │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

## Data Flow Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                 DATA FLOW DIAGRAM                                   │
└─────────────────────────────────────────────────────────────────────────────────────┘

USER INTERACTION FLOW:
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Browser   │───▶│  Next.js    │───▶│  Prisma     │───▶│ PostgreSQL  │
│             │    │  Frontend   │    │  ORM        │    │ Database    │
│  - Login    │    │             │    │             │    │             │
│  - Navigate │    │ - Pages     │    │ - Queries   │    │ - Tables    │
│  - Actions  │    │ - APIs      │    │ - Mutations │    │ - Relations │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
       ▲                   │                   │                   │
       │                   ▼                   ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Response   │◀───│   State     │◀───│   Data      │◀───│   Query     │
│             │    │ Management  │    │Processing   │    │ Results     │
│ - UI Update │    │             │    │             │    │             │
│ - Feedback  │    │ - Zustand   │    │ - Transform │    │ - Raw Data  │
│ - Redirect  │    │ - Cache     │    │ - Validate  │    │ - Metadata  │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘

WORKFLOW PROCESSING FLOW:
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Request    │───▶│  Workflow   │───▶│  Status     │───▶│  Employee   │
│ Submission  │    │ Processing  │    │  Update     │    │  Update     │
│             │    │             │    │             │    │             │
│ - HRO       │    │ - Approval  │    │ - Pending   │    │ - Status    │
│ - HRMO      │    │ - Chain     │    │ - Approved  │    │ - Change    │
│ - HHRMD     │    │ - Rules     │    │ - Rejected  │    │ - History   │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

## Security Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                               SECURITY ARCHITECTURE                                 │
└─────────────────────────────────────────────────────────────────────────────────────┘

AUTHENTICATION & AUTHORIZATION FLOW:
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Login     │───▶│  Session    │───▶│ Role-Based  │───▶│ Data Access │
│ Credentials │    │ Management  │    │ Access      │    │ Control     │
│             │    │             │    │ Control     │    │             │
│ - Username  │    │ - Server    │    │ - CSC Roles │    │ - Institution│
│ - Password  │    │ - Side      │    │ - Inst Roles│    │ - Filtering │
│             │    │ - Sessions  │    │ - Permissions│    │ - Row Level │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘

ROLE HIERARCHY:
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          CSC INTERNAL ROLES                                         │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐        │
│  │    ADMIN    │ │    HHRMD    │ │    HRMO     │ │         DO          │        │
│  │             │ │             │ │             │ │                     │        │
│  │ - All Data  │ │ - All Data  │ │ - All Data  │ │ - All Data          │        │
│  │ - All Users │ │ - HR Policy │ │ - HR Ops    │ │ - Development       │        │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────────┘        │
│                                                                                   │
│  ┌─────────────┐ ┌─────────────┐                                                 │
│  │     PO      │ │    CSCS     │                                                 │
│  │             │ │             │                                                 │
│  │ - All Data  │ │ - All Data  │                                                 │
│  │ - Planning  │ │ - Secretary │                                                 │
│  └─────────────┘ └─────────────┘                                                 │
└─────────────────────────────────────────────────────────────────────────────────────┘
                                         │
                                         ▼
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                       INSTITUTION-BASED ROLES                                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐                                  │
│  │     HRO     │ │    HRRP     │ │  EMPLOYEE   │                                  │
│  │             │ │             │ │             │                                  │
│  │ - Own Inst  │ │ - Own Inst  │ │ - Own Data  │                                  │
│  │ - HR Mgmt   │ │ - Reports   │ │ - Profile   │                                  │
│  └─────────────┘ └─────────────┘ └─────────────┘                                  │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

## Request Workflow Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                            REQUEST WORKFLOW SYSTEM                                  │
└─────────────────────────────────────────────────────────────────────────────────────┘

CONFIRMATION REQUEST WORKFLOW:
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│     HRO     │───▶│    HRMO     │───▶│   HHRMD     │───▶│  EMPLOYEE   │
│   Review    │    │  Approval   │    │    Final    │    │   UPDATE    │
│             │    │             │    │  Approval   │    │             │
│ - Verify    │    │ - Evaluate  │    │ - Decision  │    │ - Status:   │
│ - Documents │    │ - Recommend │    │ - Approve/  │    │   Confirmed │
│ - Eligibility│    │ - Comments  │    │   Reject    │    │ - Conf Date │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘

PROMOTION REQUEST WORKFLOW:
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│     HRO     │───▶│    HRMO     │───▶│   HHRMD     │
│   Review    │    │ Evaluation  │    │    Final    │
│             │    │             │    │  Approval   │
│ - Check     │    │ - Assess    │    │ - Commission│
│   Criteria  │    │   Merit     │    │   Decision  │
│ - Verify    │    │ - Budget    │    │ - Policy    │
│   Documents │    │   Impact    │    │   Alignment │
└─────────────┘    └─────────────┘    └─────────────┘

STATUS TRACKING:
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   DRAFT     │───▶│ SUBMITTED   │───▶│UNDER REVIEW │───▶│  APPROVED/  │
│             │    │             │    │             │    │  REJECTED   │
│ - Initial   │    │ - In Queue  │    │ - Processing│    │ - Final     │
│ - Editable  │    │ - Assigned  │    │ - Comments  │    │ - Complete  │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

## Technology Stack Details

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              TECHNOLOGY STACK                                       │
└─────────────────────────────────────────────────────────────────────────────────────┘

FRONTEND STACK:
┌─────────────────────────────────────────────────────────────────────────────────────┐
│ Framework:     Next.js 14 (React 18)                                               │
│ Language:      TypeScript                                                          │
│ Styling:       Tailwind CSS + shadcn/ui                                           │
│ State:         Zustand + React Query                                              │
│ Forms:         React Hook Form + Zod                                              │
│ Database:      Prisma ORM                                                         │
│ Auth:          Session-based (cookies)                                            │
│ Icons:         Lucide React                                                       │
│ Build:         Webpack (Next.js)                                                  │
│ Package Mgr:   npm                                                                │
└─────────────────────────────────────────────────────────────────────────────────────┘

BACKEND STACK:
┌─────────────────────────────────────────────────────────────────────────────────────┐
│ Framework:     Spring Boot 3                                                      │
│ Language:      Java 17                                                            │
│ Database:      PostgreSQL 13+                                                     │
│ ORM:           Hibernate/JPA                                                      │
│ Security:      Spring Security                                                    │
│ API Docs:      OpenAPI/Swagger                                                    │
│ Testing:       JUnit 5 + Mockito                                                  │
│ Build:         Maven                                                              │
│ Monitoring:    Spring Actuator                                                    │
│ Logging:       Logback                                                            │
└─────────────────────────────────────────────────────────────────────────────────────┘

DATABASE DESIGN:
┌─────────────────────────────────────────────────────────────────────────────────────┐
│ Database:      PostgreSQL (prizma)                                                │
│ Schema:        Prisma-generated + Custom                                          │
│ Tables:        15+ core entities                                                  │
│ Relations:     Foreign keys + Indexes                                             │
│ Constraints:   Unique keys + Check constraints                                    │
│ Features:      ACID compliance + Transactions                                     │
│ Backup:        Automated backups                                                  │
│ Performance:   Connection pooling + Query optimization                            │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

## Deployment Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                             DEPLOYMENT DIAGRAM                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

DEVELOPMENT ENVIRONMENT:
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Frontend   │    │  Backend    │    │  Database   │
│             │    │             │    │             │
│ localhost   │    │ localhost   │    │ localhost   │
│ :9002       │───▶│ :8080       │───▶│ :5432       │
│             │    │             │    │             │
│ Next.js Dev │    │Spring Boot  │    │PostgreSQL   │
│ Server      │    │Application  │    │ Server      │
└─────────────┘    └─────────────┘    └─────────────┘

PRODUCTION ENVIRONMENT (Future):
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Web Server  │    │App Server   │    │DB Server    │
│             │    │             │    │             │
│ Nginx       │    │ Docker      │    │PostgreSQL   │
│ Load Bal    │───▶│ Container   │───▶│ Cluster     │
│ SSL Term    │    │             │    │             │
│ Static      │    │Spring Boot  │    │Replication  │
│ Assets      │    │+ Next.js    │    │+ Backup     │
└─────────────┘    └─────────────┘    └─────────────┘
```

This comprehensive architecture diagram shows:

1. **Three-tier architecture** with clear separation of concerns
2. **Dual database access** (Prisma from frontend, Hibernate from backend)
3. **Role-based security** with CSC internal vs institution-based roles
4. **Request workflow system** with multi-step approval processes
5. **Complete technology stack** for both frontend and backend
6. **Data flow patterns** showing how information moves through the system
7. **Security layers** protecting data and ensuring proper access control

The system is designed to be scalable, maintainable, and secure while handling complex HR workflows for the Zanzibar Civil Service Commission.
