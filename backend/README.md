# Civil Service Management System (CSMS) - Backend

A comprehensive Human Resource Management System for the Civil Service Commission of Zanzibar, built with Spring Boot 3, PostgreSQL, and modern security features.

## Features

- **Role-Based Access Control (RBAC)** with JWT authentication
- **Employee Management** with comprehensive profiles
- **HR Request Processing** (Confirmation, Promotion, LWOP, Retirement, etc.)
- **Complaint Management System**
- **Audit Trail** for all system activities
- **Document Management** with file upload capabilities
- **Dashboard Analytics** with role-based statistics
- **RESTful API** with OpenAPI documentation

## Tech Stack

- **Backend**: Spring Boot 3.1.5
- **Database**: PostgreSQL 15
- **Security**: Spring Security with JWT
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Build Tool**: Maven
- **Java Version**: 17

## User Roles

1. **Admin (ADMIN)**: System administration and user management
2. **HR Officer (HRO)**: Submit requests on behalf of employees
3. **Head of HR Management (HHRMD)**: Approve/reject all types of requests
4. **HR Management Officer (HRMO)**: Approve/reject specific requests
5. **Disciplinary Officer (DO)**: Handle complaints and disciplinary actions
6. **Employee (EMP)**: Submit complaints and view own profile
7. **Planning Officer (PO)**: View reports and analytics
8. **Civil Service Commission Secretary (CSCS)**: High-level oversight
9. **HR Responsible Personnel (HRRP)**: Institutional HR supervision

## Project Structure

```
csms-backend/
├── src/
│   ├── main/
│   │   ├── java/com/zanzibar/csms/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── exception/       # Custom exceptions
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── security/        # Security configuration
│   │   │   ├── service/         # Business logic
│   │   │   └── util/            # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── data.sql         # Sample data
│   │       └── db/migration/    # Database migrations
│   └── test/                    # Test classes
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 15
- Git

### Database Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE csms_db;
-- Using existing postgres user with password: Mamlaka2020
```

2. Update `application.properties` with your database credentials.

### Running the Application

1. Clone the repository:
```bash
git clone <repository-url>
cd csms-backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Development Profile

To run with development profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Authentication

The system uses JWT-based authentication. To access protected endpoints:

1. Login via `/auth/login` with username/password
2. Use the returned JWT token in the Authorization header: `Bearer <token>`

### Sample Users

| Username | Password | Role | Institution |
|----------|----------|------|-------------|
| admin | password123 | ADMIN | Civil Service Commission |
| safia.khamis | password123 | HHRMD | Civil Service Commission |
| khamis.mnyonge | password123 | HRO | Ministry of Education |
| fauzia.iddi | password123 | HRMO | Civil Service Commission |
| kabdul.rahman | password123 | DO | Civil Service Commission |

## Key Endpoints

### Authentication
- `POST /auth/login` - User login
- `POST /auth/refresh` - Refresh token
- `POST /auth/logout` - User logout

### Employee Management
- `GET /employees` - Get all employees
- `POST /employees` - Create new employee
- `GET /employees/{id}` - Get employee by ID
- `PUT /employees/{id}` - Update employee
- `DELETE /employees/{id}` - Delete employee

### Dashboard
- `GET /dashboard/statistics` - Get dashboard statistics
- `GET /dashboard/pending-actions` - Get pending actions
- `GET /dashboard/recent-activities` - Get recent activities

## Security Features

- JWT token-based authentication
- Role-based access control
- Password encryption with BCrypt
- Session management
- Audit logging
- Input validation

## Database Schema

The system includes the following main entities:

- **Users**: System users with different roles
- **Institutions**: Government institutions/ministries
- **Employees**: Employee profiles and information
- **Requests**: HR requests (confirmation, promotion, etc.)
- **Complaints**: Employee complaints
- **Audit Logs**: System activity tracking

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please contact the development team or create an issue in the repository.