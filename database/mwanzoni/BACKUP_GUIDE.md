# CSMS Database Backup Guide - CORRECTED VERSION

## IMPORTANT: Correct Database Credentials
- **Username:** `postgres`
- **Password:** `Mamlaka2020` (NOT password123)
- **Database:** `prizma`
- **Port:** `5432`

## Manual Backup Steps (Since Automated Commands Time Out)

### Option 1: Using Command Line

Open Command Prompt as Administrator and run:

```cmd
# Windows
set PGPASSWORD=Mamlaka2020
pg_dump -U postgres -h localhost -p 5432 prizma > csms_backup.sql

# Linux/Mac
export PGPASSWORD=Mamlaka2020
pg_dump -U postgres -h localhost -p 5432 prizma > csms_backup.sql
```

### Option 2: Using pgAdmin (Recommended)

1. Open pgAdmin
2. Connect to localhost server
   - Host: localhost
   - Port: 5432
   - Username: postgres
   - Password: Mamlaka2020
3. Right-click on 'prizma' database
4. Select "Backup..."
5. Choose filename: `csms_complete_backup.sql`
6. Format: Plain (SQL)
7. Click "Backup"

### Option 3: Using DBeaver or Similar Tools

1. Connect to PostgreSQL:
   - Host: localhost
   - Port: 5432
   - Database: prizma
   - Username: postgres
   - Password: Mamlaka2020
2. Right-click on 'prizma' database
3. Select "Export Data"
4. Choose SQL format
5. Export all tables with data

## What's Already in This Folder

1. **Configuration Files:**
   - `database_config.env` - Environment variables (UPDATED with correct password)
   - `prisma_schema.prisma` - Complete database schema

2. **Restoration Scripts:**
   - `restore_database.bat` - Windows script (UPDATED with correct password)
   - `restore_database.sh` - Linux/Mac script (UPDATED with correct password)

3. **Documentation:**
   - `README.md` - Full restoration guide
   - `simple_backup_instructions.md` - Manual backup methods
   - `manual_backup_commands.bat` - Windows commands reference

4. **Migration Scripts:**
   - `migration_scripts/001_initial_schema.sql` - Complete schema creation

5. **Verification:**
   - `backup_verification.sql` - Script to verify restored database

## Quick Test Commands

To verify your credentials are correct:

```sql
-- Windows
set PGPASSWORD=Mamlaka2020
psql -U postgres -h localhost -p 5432 -d prizma -c "SELECT version();"

-- Linux/Mac  
export PGPASSWORD=Mamlaka2020
psql -U postgres -h localhost -p 5432 -d prizma -c "SELECT version();"
```

## Restoration on New Server

1. Install PostgreSQL 15
2. Create database: `createdb -U postgres prizma`
3. Set password: `export PGPASSWORD=Mamlaka2020` (Linux) or `set PGPASSWORD=Mamlaka2020` (Windows)
4. Restore: `psql -U postgres -d prizma -f csms_backup.sql`
5. Update frontend/.env with credentials from database_config.env
6. Run application

## Important Notes

- The automated backup commands are timing out, so manual backup is required
- All configuration files have been updated with the correct password (Mamlaka2020)
- The database name is `prizma` (not csms_db)
- User passwords in the application remain as `password123` (only PostgreSQL password is Mamlaka2020)

## Files That Need Manual Creation

Due to timeout issues, you need to manually create:
- `csms_complete.sql` - Full database backup
- `csms_schema_only.sql` - Schema only backup  
- `csms_data_only.sql` - Data only backup

Use one of the manual methods above to create these files.