# CSMS Database Migration Guide

This guide provides complete instructions for migrating the Civil Service Management System (CSMS) database to a new VPS or environment.

## Overview

The CSMS system uses a PostgreSQL database named `prizma` with a complete schema supporting all HR modules including:
- Employee management (174 employees, 42 institutions)
- HR request workflows (8 modules: confirmation, promotion, LWOP, cadre change, retirement, resignation, service extension, termination)
- Complaints system
- User management (162 users with 9 roles)
- Notifications system

**Total Data**: 1,044 records across 14 tables

## Prerequisites

### Software Requirements
- PostgreSQL 15 or later
- Node.js 18 or later
- npm or yarn package manager
- Java 17 (for backend)
- Maven 3.6+ (for backend)

### System Requirements
- Minimum 4GB RAM
- 10GB free disk space
- Ubuntu 20.04+ / Windows Server 2019+ / CentOS 8+

## Migration Steps

### Step 1: Prepare Target Environment

1. **Install PostgreSQL**
   ```bash
   # Ubuntu/Debian
   sudo apt update
   sudo apt install postgresql postgresql-contrib
   
   # CentOS/RHEL
   sudo yum install postgresql postgresql-server
   
   # Windows
   # Download from https://www.postgresql.org/download/windows/
   ```

2. **Start PostgreSQL service**
   ```bash
   # Linux
   sudo systemctl start postgresql
   sudo systemctl enable postgresql
   
   # Windows
   # PostgreSQL should start automatically after installation
   ```

3. **Create database user (if needed)**
   ```sql
   sudo -u postgres psql
   CREATE USER postgres WITH PASSWORD 'Mamlaka2020';
   ALTER USER postgres CREATEDB;
   \q
   ```

### Step 2: Copy Migration Files

Copy all files from the `database` directory to your target server:
- `schema-backup.sql` - Complete database schema
- `csms-data-export.sql` - All data in SQL format  
- `csms-data-export.json` - All data in JSON format (backup)
- `restore-database.bat` - Windows restoration script
- `restore-database.sh` - Linux restoration script
- `.env.example` - Environment configuration template

### Step 3: Restore Database

#### Option A: Automated Restoration (Recommended)

**For Windows:**
```cmd
cd C:\path\to\database
restore-database.bat
```

**For Linux:**
```bash
cd /path/to/database
chmod +x restore-database.sh
./restore-database.sh
```

#### Option B: Manual Restoration

1. **Create database**
   ```sql
   psql -U postgres -c "CREATE DATABASE prizma;"
   ```

2. **Create schema**
   ```bash
   psql -U postgres -d prizma -f schema-backup.sql
   ```

3. **Import data**
   ```bash
   psql -U postgres -d prizma -f csms-data-export.sql
   ```

4. **Verify data**
   ```sql
   psql -U postgres -d prizma -c "SELECT COUNT(*) FROM \"User\";"
   psql -U postgres -d prizma -c "SELECT COUNT(*) FROM \"Employee\";"
   psql -U postgres -d prizma -c "SELECT COUNT(*) FROM \"Institution\";"
   ```

### Step 4: Setup Application

1. **Clone or copy application code**
   ```bash
   git clone <repository-url> csms
   cd csms
   ```

2. **Configure environment variables**
   
   **Frontend (.env.local):**
   ```env
   DATABASE_URL="postgresql://postgres:Mamlaka2020@localhost:5432/prizma"
   NEXT_PUBLIC_API_URL="http://localhost:8080/api"
   NEXT_PUBLIC_BACKEND_URL="http://localhost:8080"
   ```

   **Backend (application.properties):**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/prizma
   spring.datasource.username=postgres
   spring.datasource.password=Mamlaka2020
   spring.profiles.active=prod
   ```

3. **Install dependencies**
   ```bash
   # Frontend
   cd frontend
   npm install
   npx prisma generate
   
   # Backend
   cd ../backend
   mvn clean install
   ```

4. **Start services**
   ```bash
   # Backend (Terminal 1)
   cd backend
   mvn spring-boot:run
   
   # Frontend (Terminal 2)
   cd frontend
   npm run dev
   ```

5. **Access application**
   - Frontend: http://localhost:9002
   - Backend API: http://localhost:8080/api

## Verification Checklist

After migration, verify these components:

### Database Verification
- [ ] All 14 tables created successfully
- [ ] 1,044 total records imported
- [ ] 42 institutions loaded
- [ ] 162 users with correct roles
- [ ] 174 employees with all data
- [ ] All foreign key relationships intact

### Application Verification
- [ ] Frontend loads without errors
- [ ] Backend API responds to health checks
- [ ] User login works (test with existing credentials)
- [ ] Employee search and display works
- [ ] HR modules load correctly
- [ ] Complaints system functional
- [ ] File uploads working (check uploads directory)

### Test Credentials
Use these pre-existing accounts for testing:

**Admin User:**
- Username: admin
- Password: password123

**HRO Users:**
- Username: hro01
- Password: password123

**Employee Login:**
- ZanID: 1905980520
- ZSSF: ZSSF052
- Payroll: PAY0052

## Troubleshooting

### Common Issues

**1. Database Connection Errors**
- Verify PostgreSQL is running: `sudo systemctl status postgresql`
- Check credentials in .env files
- Ensure database `prizma` exists
- Test connection: `psql -U postgres -d prizma -c "SELECT 1;"`

**2. Prisma Client Errors**
- Regenerate client: `npx prisma generate`
- Check DATABASE_URL format
- Verify schema matches database

**3. Backend API Errors**
- Check Java version: `java -version` (should be 17+)
- Verify Maven build: `mvn clean compile`
- Check application.properties configuration
- Review logs in `logs/csms.log`

**4. Frontend Build Errors**
- Clear node_modules: `rm -rf node_modules && npm install`
- Update dependencies: `npm update`
- Check Next.js configuration in `next.config.ts`

**5. Missing Data**
- Re-run data import: `psql -U postgres -d prizma -f csms-data-export.sql`
- Check for SQL errors in console output
- Verify JSON backup exists: `csms-data-export.json`

### Performance Optimization

**Database Indexes**
All necessary indexes are included in the schema backup:
- User lookups (username, employeeId, institutionId)
- Employee searches (zanId, payrollNumber, zssfNumber)
- Request filtering (employeeId, submittedById)

**File Storage**
- Uploads directory: `./uploads` (create if missing)
- Max file size: 2MB per file
- Supported formats: PDF, DOC, DOCX, JPG, PNG

## Security Considerations

### Database Security
- Change default password after migration
- Restrict database access to application only
- Enable SSL connections in production
- Regular backup schedule

### Application Security
- Update JWT secret in production
- Configure HTTPS for frontend
- Set proper CORS origins
- Review user permissions

### File Security
- Restrict uploads directory permissions
- Implement virus scanning if needed
- Regular cleanup of temp files

## Backup Strategy

### Automated Backups
Set up regular database backups:

```bash
# Daily backup script
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -U postgres prizma > /backups/csms_backup_$DATE.sql
find /backups -name "csms_backup_*.sql" -mtime +7 -delete
```

### Manual Backup
```bash
# Create fresh backup
node backup-data.js
pg_dump -U postgres prizma > manual_backup_$(date +%Y%m%d).sql
```

## Support Information

### System Architecture
- **Frontend**: Next.js 15 with TypeScript, Prisma ORM, Tailwind CSS
- **Backend**: Spring Boot 3.1.5 with Java 17
- **Database**: PostgreSQL 15 with dual ORM (Prisma + JPA/Hibernate)
- **Authentication**: JWT with 10min access tokens, 24hr refresh tokens

### Key Components
- **9 User Roles**: ADMIN, HRO, HHRMD, HRMO, DO, EMPLOYEE, CSCS, HRRP, PO
- **8 HR Modules**: Confirmation, Promotion, LWOP, Cadre Change, Retirement, Resignation, Service Extension, Termination
- **Complaint System**: Full workflow with file attachments
- **Dual API**: Next.js API routes + Spring Boot REST API

### Monitoring
- Application logs: `backend/logs/csms.log`
- Database queries: Enable PostgreSQL query logging
- Performance: Monitor response times and query performance

---

**Migration completed successfully!** The CSMS system should now be fully operational on the new environment with all data preserved and functionality intact.