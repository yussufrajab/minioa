# Simple Database Backup Instructions

## Quick Backup Commands

If the automated scripts don't work due to timeout issues, use these manual commands:

### 1. Create Manual Database Backup

```bash
# Set password to avoid prompts
export PGPASSWORD=password123

# Create complete backup
pg_dump -U postgres -h localhost -p 5432 --clean --create --if-exists prizma > csms_complete_manual.sql

# Or create schema and data separately
pg_dump -U postgres -h localhost -p 5432 --schema-only --no-owner --no-privileges prizma > csms_schema_manual.sql
pg_dump -U postgres -h localhost -p 5432 --data-only --no-owner --no-privileges --inserts prizma > csms_data_manual.sql
```

### 2. Alternative: Use psql Copy Commands

```bash
# Connect to database
export PGPASSWORD=password123
psql -U postgres -h localhost -p 5432 -d prizma

# Inside psql, export each table
\copy "User" TO 'users_data.csv' CSV HEADER;
\copy "Employee" TO 'employees_data.csv' CSV HEADER;
\copy "Institution" TO 'institutions_data.csv' CSV HEADER;
\copy "ConfirmationRequest" TO 'confirmation_requests_data.csv' CSV HEADER;
\copy "PromotionRequest" TO 'promotion_requests_data.csv' CSV HEADER;
\copy "LwopRequest" TO 'lwop_requests_data.csv' CSV HEADER;
\copy "CadreChangeRequest" TO 'cadre_change_requests_data.csv' CSV HEADER;
\copy "RetirementRequest" TO 'retirement_requests_data.csv' CSV HEADER;
\copy "ResignationRequest" TO 'resignation_requests_data.csv' CSV HEADER;
\copy "ServiceExtensionRequest" TO 'service_extension_requests_data.csv' CSV HEADER;
\copy "SeparationRequest" TO 'separation_requests_data.csv' CSV HEADER;
\copy "Complaint" TO 'complaints_data.csv' CSV HEADER;
\copy "Notification" TO 'notifications_data.csv' CSV HEADER;
\copy "EmployeeCertificate" TO 'employee_certificates_data.csv' CSV HEADER;
\q
```

### 3. Restore from Manual Backup

```bash
# Restore complete backup
export PGPASSWORD=password123
createdb -U postgres prizma
psql -U postgres -h localhost -p 5432 -d prizma -f csms_complete_manual.sql

# Or restore schema then data
createdb -U postgres prizma  
psql -U postgres -h localhost -p 5432 -d prizma -f csms_schema_manual.sql
psql -U postgres -h localhost -p 5432 -d prizma -f csms_data_manual.sql
```

### 4. Alternative: SQLite Export (as backup format)

```bash
# If PostgreSQL backup is problematic, create SQLite version
# Install sqlite3 if not available
apt-get install sqlite3  # Ubuntu/Debian
yum install sqlite       # CentOS/RHEL

# Connect to PostgreSQL and export schema
export PGPASSWORD=password123
psql -U postgres -h localhost -p 5432 -d prizma -c "\d" > database_schema.txt

# Export data as INSERT statements
pg_dump -U postgres -h localhost -p 5432 --data-only --inserts prizma > data_inserts.sql
```

### 5. Minimal Data Export for Testing

If you just need essential data for testing on new server:

```bash
export PGPASSWORD=password123

# Export just essential tables
pg_dump -U postgres -h localhost -p 5432 --data-only --inserts -t "Institution" -t "User" -t "Employee" prizma > essential_data.sql
```

### 6. Quick Verification Commands

After any backup, verify the data:

```bash
export PGPASSWORD=password123

# Count records in main tables
psql -U postgres -h localhost -p 5432 -d prizma -c "
SELECT 
  'Users' as table_name, COUNT(*) as count FROM \"User\"
UNION ALL
SELECT 
  'Employees' as table_name, COUNT(*) as count FROM \"Employee\"  
UNION ALL
SELECT 
  'Institutions' as table_name, COUNT(*) as count FROM \"Institution\"
UNION ALL
SELECT 
  'Confirmations' as table_name, COUNT(*) as count FROM \"ConfirmationRequest\"
UNION ALL
SELECT 
  'Promotions' as table_name, COUNT(*) as count FROM \"PromotionRequest\"
UNION ALL
SELECT 
  'LWOP' as table_name, COUNT(*) as count FROM \"LwopRequest\";"
```

## Troubleshooting

### If pg_dump times out:
1. Try with specific tables: `pg_dump -t "tablename"`
2. Use `--no-tablespaces` flag
3. Export in chunks with `LIMIT` queries
4. Use the CSV export method above

### If psql import fails:
1. Check PostgreSQL version compatibility
2. Try `--no-owner --no-privileges` flags
3. Import schema first, then data
4. Use `\i filename.sql` inside psql session

### Common Error Fixes:
- **"role does not exist"**: Add `--no-owner` flag
- **"permission denied"**: Add `--no-privileges` flag  
- **"database already exists"**: Use `--clean` flag or drop database first
- **"relation already exists"**: Use `--clean --if-exists` flags

## File Sizes Reference

Typical backup file sizes for CSMS database:
- Complete backup with data: ~5-10MB
- Schema only: ~100KB
- CSV exports: ~2-5MB total
- Essential data only: ~1-2MB

Choose the method that works best with your server's resources and timeout settings.