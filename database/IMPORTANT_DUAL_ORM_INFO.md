# IMPORTANT: CSMS Dual-ORM Database Architecture

## Critical Information About This System

The CSMS uses **TWO different ORMs** accessing the **SAME PostgreSQL database**:

### 1. Frontend (Next.js)
- **ORM**: Prisma
- **Schema Location**: `/frontend/prisma/schema.prisma`
- **Migration Approach**: Prisma Migrate
- **Connection**: Direct PostgreSQL queries via Prisma Client

### 2. Backend (Spring Boot)
- **ORM**: Hibernate/JPA
- **Entity Location**: `/backend/src/main/java/com/zanzibar/csms/entity/`
- **Migration Approach**: Auto DDL (dev) or Validation only (prod)
- **Connection**: JDBC via Spring Data JPA

## Why This Matters for Backups

### Standard PostgreSQL backup IS sufficient because:
1. Both ORMs read/write to the same PostgreSQL database
2. The database is the single source of truth
3. PostgreSQL backup captures all data regardless of which ORM created it

### However, for complete system restoration you also need:
1. **Prisma schema file** - To regenerate the Prisma client
2. **Hibernate entity classes** - To ensure Spring Boot can map correctly
3. **Configuration files** - Both frontend and backend configs

## Simplified Backup Process

### Option 1: Quick Database-Only Backup (Minimum Required)
```cmd
set PGPASSWORD=Mamlaka2020
pg_dump -U postgres -h localhost -p 5432 prizma > csms_backup.sql
```

This is sufficient to restore all data. The applications will work as long as:
- Prisma schema matches the database
- Hibernate entities match the database

### Option 2: Complete System Backup (Recommended)
Run the provided script:
```cmd
C:\hamisho\project_csms\database\backup_dual_orm.bat
```

This creates:
- PostgreSQL database backup
- Prisma schema backup
- Hibernate entities backup
- Configuration files
- CSV data exports for verification

## Key Points to Remember

1. **The PostgreSQL database is the single source of truth**
   - Both Prisma and Hibernate are just interfaces to the same data

2. **Schema changes should be coordinated**
   - Best practice: Make schema changes via Prisma Migrate
   - Set Hibernate to `validate` mode in production

3. **For backup/restore, PostgreSQL commands work perfectly**
   - You don't need separate Prisma/Hibernate backups for data
   - The ORM files are only needed for application setup

4. **Database credentials**
   - Username: `postgres`
   - Password: `Mamlaka2020`
   - Database: `prizma`
   - Port: `5432`

## Quick Restoration

```cmd
REM Set password
set PGPASSWORD=Mamlaka2020

REM Restore database
psql -U postgres -d prizma -f csms_backup.sql

REM Regenerate Prisma client
cd frontend
npx prisma generate

REM Backend will validate on startup
cd ../backend
mvn spring-boot:run -Dspring.profiles.active=prod
```

## Summary

While CSMS uses two ORMs (Prisma + Hibernate), they both access the same PostgreSQL database. A standard PostgreSQL backup captures everything needed for data restoration. The additional ORM-specific files are only needed to ensure the applications can connect and map to the restored database correctly.