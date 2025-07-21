# Current System Architecture - Three-Tier Implementation

## 🏗️ **System Overview**

The Civil Service Management System (CSMS) now implements a clean **three-tier architecture** with HTTP/JSON communication between frontend and backend.

---

## 📊 **Visual Architecture Diagram**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           🌐 CLIENT TIER (Presentation)                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                    📱 Frontend - Next.js App                        │    │
│  │                     Port: 9002                                      │    │
│  │                                                                     │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐     │    │
│  │  │   🏠 Dashboard   │  │ 👥 User Mgmt    │  │ 📋 Requests     │     │    │
│  │  │                 │  │                 │  │                 │     │    │
│  │  │ • Metrics       │  │ • Admin Panel   │  │ • Confirmation  │     │    │
│  │  │ • Charts        │  │ • User CRUD     │  │ • Promotion     │     │    │
│  │  │ • Reports       │  │ • Institutions  │  │ • LWOP          │     │    │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘     │    │
│  │                                                                     │    │
│  │  ┌─────────────────────────────────────────────────────────────┐   │    │
│  │  │                🔧 API Client Layer                          │   │    │
│  │  │                                                             │   │    │
│  │  │  • HTTP/JSON Communication                                  │   │    │
│  │  │  • CORS Enabled                                             │   │    │
│  │  │  • No Authentication (Temporary)                            │   │    │
│  │  │  • Base URL: http://localhost:8080/api                      │   │    │
│  │  └─────────────────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        │ 🌐 HTTP/JSON
                                        │ GET, POST, PUT, DELETE
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        ⚙️ APPLICATION TIER (Business Logic)                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                   🚀 Backend - Spring Boot 3                       │    │
│  │                     Port: 8080                                      │    │
│  │                                                                     │    │
│  │  ┌─────────────────────────────────────────────────────────────┐   │    │
│  │  │                🎛️ REST Controllers                          │   │    │
│  │  │                                                             │   │    │
│  │  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐       │   │    │
│  │  │  │   /api/users │ │/api/employees│ │/api/requests │       │   │    │
│  │  │  │              │ │              │ │              │       │   │    │
│  │  │  │ • GET users  │ │ • GET list   │ │ • GET list   │       │   │    │
│  │  │  │ • POST user  │ │ • POST new   │ │ • POST new   │       │   │    │
│  │  │  │ • PUT update │ │ • PUT update │ │ • PUT update │       │   │    │
│  │  │  │ • DELETE     │ │ • DELETE     │ │ • DELETE     │       │   │    │
│  │  │  └──────────────┘ └──────────────┘ └──────────────┘       │   │    │
│  │  │                                                             │   │    │
│  │  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐       │   │    │
│  │  │  │/api/institutions│ │/api/complaints│ │ /api/status  │       │   │    │
│  │  │  │              │ │              │ │              │       │   │    │
│  │  │  │ • Institution│ │ • Complaint  │ │ • Health     │       │   │    │
│  │  │  │   Management │ │   System     │ │   Check      │       │   │    │
│  │  │  └──────────────┘ └──────────────┘ └──────────────┘       │   │    │
│  │  └─────────────────────────────────────────────────────────────┘   │    │
│  │                                                                     │    │
│  │  ┌─────────────────────────────────────────────────────────────┐   │    │
│  │  │                   🛡️ Security Layer                          │   │    │
│  │  │                                                             │   │    │
│  │  │  • CORS Configuration ✅                                    │   │    │
│  │  │  • Authentication: DISABLED (Temporary) ⚠️                 │   │    │
│  │  │  • Authorization: permitAll() ⚠️                           │   │    │
│  │  │  • Cross-Origin: localhost:9002, localhost:3000            │   │    │
│  │  └─────────────────────────────────────────────────────────────┘   │    │
│  │                                                                     │    │
│  │  ┌─────────────────────────────────────────────────────────────┐   │    │
│  │  │                    💾 Data Access Layer                     │   │    │
│  │  │                                                             │   │    │
│  │  │  • JDBC Template for direct SQL ✅                          │   │    │
│  │  │  • JPA/Hibernate for complex queries                       │   │    │
│  │  │  • Connection Pool: HikariCP                               │   │    │
│  │  │  • Single Database Connection Point ✅                      │   │    │
│  │  └─────────────────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        │ 🗄️ JDBC/Hibernate
                                        │ SQL Queries
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           🗄️ DATA TIER (Persistence)                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                      🐘 PostgreSQL Database                         │    │
│  │                        Port: 5432                                   │    │
│  │                      Database: prizma                               │    │
│  │                                                                     │    │
│  │  ┌─────────────────────────────────────────────────────────────┐   │    │
│  │  │                    📋 Tables (Prisma Schema)                │   │    │
│  │  │                                                             │   │    │
│  │  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐       │   │    │
│  │  │  │    "User"    │ │ "Institution"│ │  "Employee"  │       │   │    │
│  │  │  │              │ │              │ │              │       │   │    │
│  │  │  │ • id         │ │ • id         │ │ • id         │       │   │    │
│  │  │  │ • name       │ │ • name       │ │ • name       │       │   │    │
│  │  │  │ • username   │ │ • active     │ │ • zanId      │       │   │    │
│  │  │  │ • role       │ │ • createdAt  │ │ • institutionId      │       │   │    │
│  │  │  │ • active     │ │ • updatedAt  │ │ • department │       │   │    │
│  │  │  │ • institutionId │ │              │ │ • status     │       │   │    │
│  │  │  └──────────────┘ └──────────────┘ └──────────────┘       │   │    │
│  │  │                                                             │   │    │
│  │  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐       │   │    │
│  │  │  │"ConfirmationRequest"│ │"PromotionRequest"│ │ "Complaint"  │       │   │    │
│  │  │  │              │ │              │ │              │       │   │    │
│  │  │  │ • id         │ │ • id         │ │ • id         │       │   │    │
│  │  │  │ • status     │ │ • status     │ │ • status     │       │   │    │
│  │  │  │ • employeeId │ │ • employeeId │ │ • complainantId      │       │   │    │
│  │  │  │ • reviewStage│ │ • reviewStage│ │ • reviewStage│       │   │    │
│  │  │  └──────────────┘ └──────────────┘ └──────────────┘       │   │    │
│  │  └─────────────────────────────────────────────────────────────┘   │    │
│  │                                                                     │    │
│  │  ┌─────────────────────────────────────────────────────────────┐   │    │
│  │  │                    🔐 Database Security                     │   │    │
│  │  │                                                             │   │    │
│  │  │  • Username: postgres                                      │   │    │
│  │  │  • Password: Protected                                     │   │    │
│  │  │  • Access: Backend Only ✅                                  │   │    │
│  │  │  • Connection Pool: HikariCP (Max: 10)                     │   │    │
│  │  └─────────────────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 **Data Flow Diagram**

```
👤 User Interaction
        │
        ▼
┌─────────────────┐    HTTP Request     ┌─────────────────┐    SQL Query    ┌─────────────────┐
│                 │ ──────────────────> │                 │ ─────────────> │                 │
│   Next.js App   │                     │  Spring Boot    │                │   PostgreSQL    │
│  (Frontend)     │                     │   (Backend)     │                │   (Database)    │
│                 │ <────────────────── │                 │ <───────────── │                 │
└─────────────────┘    JSON Response    └─────────────────┘   Result Set    └─────────────────┘
    localhost:9002                          localhost:8080                      localhost:5432
```

---

## 🌍 **Network Communication**

### **Frontend → Backend Communication**
```
Protocol: HTTP/1.1
Method: GET, POST, PUT, DELETE
Content-Type: application/json
CORS: Enabled
Authentication: Disabled (Temporary)

Example API Calls:
• GET  http://localhost:8080/api/users
• POST http://localhost:8080/api/users
• PUT  http://localhost:8080/api/users/{id}
• GET  http://localhost:8080/api/institutions
• GET  http://localhost:8080/api/test-connection ✅
```

### **Backend → Database Communication**
```
Protocol: JDBC
Driver: PostgreSQL JDBC Driver
Connection Pool: HikariCP
Max Connections: 10
Schema: Prisma-generated tables with quoted identifiers

Example Queries:
• SELECT * FROM "User" u LEFT JOIN "Institution" i ON u."institutionId" = i.id
• INSERT INTO "User" (name, username, role, active, "institutionId") VALUES (?, ?, ?, ?, ?)
• UPDATE "User" SET active = ? WHERE id = ?
```

---

## ⚙️ **Technology Stack**

| **Tier** | **Technology** | **Port** | **Purpose** | **Status** |
|----------|----------------|----------|-------------|------------|
| **Frontend** | Next.js 15.2.3 | 9002 | User Interface | ✅ Running |
| **Backend** | Spring Boot 3.1.5 | 8080 | Business Logic | ✅ Running |
| **Database** | PostgreSQL | 5432 | Data Storage | ✅ Connected |

---

## 🔧 **Current Configuration**

### **Security Settings (Temporary)**
```java
// Spring Security Configuration
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf().disable()
            .authorizeHttpRequests()
                .anyRequest().permitAll(); // ⚠️ Temporary - allows all requests
        return http.build();
    }
}
```

### **CORS Configuration**
```java
// Cross-Origin Resource Sharing
@CrossOrigin(origins = {"http://localhost:9002", "http://localhost:3000"})
```

### **Database Connection**
```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/prizma
spring.datasource.username=postgres
spring.jpa.hibernate.ddl-auto=none  # Preserves existing Prisma schema
```

---

## ✅ **Architecture Benefits**

1. **🎯 Single Source of Truth**: Backend is the only component accessing the database
2. **🔄 Scalability**: Each tier can be scaled independently
3. **🛡️ Security**: Database is isolated behind the application layer
4. **🔧 Maintainability**: Clear separation of concerns
5. **🌐 API-First**: RESTful APIs enable future mobile/third-party integrations
6. **📊 Monitoring**: Each component can be monitored independently

---

## ⚠️ **Current Limitations & Next Steps**

### **Immediate Items to Address:**
- [ ] Re-enable authentication with proper JWT handling
- [ ] Add input validation and error handling
- [ ] Implement comprehensive logging
- [ ] Add API rate limiting

### **Future Enhancements:**
- [ ] Add Redis for session management
- [ ] Implement microservices architecture
- [ ] Add API documentation with Swagger
- [ ] Set up load balancing for high availability

---

## 🚀 **System Status**

```
┌─────────────────────────────────────────────────────────────┐
│                    🟢 SYSTEM OPERATIONAL                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Frontend:  ✅ Running on http://localhost:9002             │
│  Backend:   ✅ Running on http://localhost:8080             │
│  Database:  ✅ Connected to PostgreSQL                      │
│  API:       ✅ HTTP/JSON communication working              │
│  CORS:      ✅ Cross-origin requests enabled                │
│                                                             │
│  🎯 Ready for user management and admin operations          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

This architecture successfully resolves the original "could not load users" error by establishing a clean HTTP/JSON communication channel between the frontend and backend, with the backend serving as the single point of database access.