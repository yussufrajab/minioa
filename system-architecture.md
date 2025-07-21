# CSMS System Architecture

## Visual Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                   CLIENT BROWSER                                     │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                          Next.js Frontend (Port 3000)                         │   │
│  │  ┌─────────────────┐  ┌──────────────────┐  ┌────────────────────────────┐ │   │
│  │  │   UI Components  │  │  Zustand Store   │  │    API Client Library      │ │   │
│  │  │  - Dashboard     │  │  - Auth State    │  │  - HTTP requests to        │ │   │
│  │  │  - User Mgmt     │  │  - User Info     │  │    backend endpoints       │ │   │
│  │  │  - Employee Mgmt │  │  - Persistence   │  │  - Error handling          │ │   │
│  │  └─────────────────┘  └──────────────────┘  └────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────┘
                                          │
                                          │ HTTP/JSON
                                          │ (REST API)
                                          ▼
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                            Spring Boot Backend (Port 8080)                           │
│  ┌─────────────────┐  ┌──────────────────┐  ┌────────────────────────────────┐    │
│  │  REST Controllers│  │  Service Layer   │  │    Security & Session Mgmt     │    │
│  │  - /api/login   │  │  - Business Logic │  │  - Session-based auth          │    │
│  │  - /api/users   │  │  - Data Processing│  │  - Role-based access control   │    │
│  │  - /api/employees│ │  - Validation     │  │  - CORS configuration          │    │
│  └─────────────────┘  └──────────────────┘  └────────────────────────────────┘    │
│           │                      │                                                   │
│           └──────────────────────┴───────────────────────┐                         │
│                                                          ▼                          │
│  ┌────────────────────────────────────────────────────────────────────────────┐   │
│  │                        JPA/Hibernate ORM Layer                              │   │
│  │  - Entity mapping (User, Employee, Institution, etc.)                      │   │
│  │  - Query generation and optimization                                       │   │
│  │  - Connection pooling                                                      │   │
│  └────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────┘
                                          │
                                          │ JDBC
                                          │ (SQL Queries)
                                          ▼
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              PostgreSQL Database                                     │
│  ┌─────────────────┐  ┌──────────────────┐  ┌────────────────────────────────┐    │
│  │     Tables       │  │   Relationships   │  │         Sample Data            │    │
│  │  - users         │  │  - Foreign Keys   │  │  - CSC (Central Institution)   │    │
│  │  - employees     │  │  - Indexes        │  │  - Other Institutions          │    │
│  │  - institutions  │  │  - Constraints    │  │  - Role hierarchies            │    │
│  │  - roles         │  │                   │  │                                │    │
│  └─────────────────┘  └──────────────────┘  └────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

## Data Flow Example: User Login

```
1. User enters credentials in browser
   ┌──────────┐
   │  Browser │ ──── POST /api/login ──────┐
   └──────────┘     {username, password}   │
                                           ▼
2. Next.js API Client sends request   ┌─────────────┐
                                     │  Next.js    │
                                     │  API Client │
                                     └─────────────┘
                                           │
                                           │ HTTP POST
                                           ▼
3. Spring Boot receives request      ┌─────────────┐
                                    │ Spring Boot │
                                    │ Controller  │
                                    └─────────────┘
                                           │
                                           ▼
4. Service validates credentials     ┌─────────────┐
                                    │   Service   │
                                    │    Layer    │
                                    └─────────────┘
                                           │
                                           ▼
5. Database query via Hibernate     ┌─────────────┐
                                   │  Hibernate  │
                                   │     ORM     │
                                   └─────────────┘
                                           │
                                           ▼
6. PostgreSQL returns user data     ┌─────────────┐
                                   │ PostgreSQL  │
                                   │  Database   │
                                   └─────────────┘
                                           │
7. Response flows back up          User Data with Role
   through the layers                      │
                                          ▼
8. Zustand store updates           ┌─────────────┐
   with user session              │   Zustand   │
                                  │    Store    │
                                  └─────────────┘
                                          │
9. UI updates to show             ┌─────────────┐
   dashboard with role-based      │ Dashboard   │
   navigation                     │     UI      │
                                  └─────────────┘
```

## Key Connection Points

### 1. Frontend → Backend Communication
- **Protocol**: HTTP/HTTPS
- **Format**: JSON
- **Authentication**: Session-based (cookies)
- **Base URL**: http://localhost:8080
- **API Endpoints**:
  - `/api/login` - Authentication
  - `/api/users` - User management
  - `/api/employees` - Employee data
  - `/api/institutions` - Institution data

### 2. Backend → Database Communication
- **Protocol**: JDBC (Java Database Connectivity)
- **ORM**: Hibernate/JPA
- **Connection Pool**: HikariCP (Spring Boot default)
- **Database**: PostgreSQL
- **Port**: 5432 (default)

### 3. Frontend Internal Connections
- **Prisma**: Used for type generation and some direct DB queries
- **Zustand**: State management with localStorage persistence
- **API Client**: Centralized HTTP client with interceptors

## Role-Based Data Flow

```
┌─────────────────────────────────────────────────────────┐
│                   User Role System                       │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  CSC Internal Roles          Institution-Based Roles   │
│  ┌──────────────────┐       ┌────────────────────┐    │
│  │ • Admin          │       │ • HRO              │    │
│  │ • HHRMD          │       │ • EMPLOYEE         │    │
│  │ • HRMO           │       │ • HRRP             │    │
│  │ • DO             │       └────────────────────┘    │
│  │ • PO             │              │                   │
│  │ • CSCS           │              ▼                   │
│  └──────────────────┘       Can only see data         │
│           │                  from their own            │
│           ▼                  institution               │
│    Can see data from                                  │
│    ALL institutions                                    │
│                                                        │
└────────────────────────────────────────────────────────┘
```

## Security Layers

1. **Frontend Security**
   - Protected routes with role checks
   - API request interceptors for auth headers
   - Secure storage of session data

2. **Backend Security**
   - Session-based authentication
   - CORS configuration for frontend origin
   - Role-based access control (RBAC)
   - Request validation and sanitization

3. **Database Security**
   - Connection encryption
   - User permissions and roles
   - Prepared statements (via Hibernate)

## Current Implementation Details

- **Frontend Port**: 3000
- **Backend Port**: 8080
- **Database**: PostgreSQL (local)
- **Session Management**: Server-side sessions with cookies
- **State Management**: Zustand with persistence
- **API Pattern**: RESTful endpoints
- **Data Format**: JSON for all API communications