# CSMS Dual-ORM Database Backup Guide
## For Prisma (Frontend) + Hibernate (Backend) Architecture

## Understanding the Architecture

CSMS uses a **shared PostgreSQL database** accessed by:
- **Frontend**: Next.js with Prisma ORM
- **Backend**: Spring Boot with Hibernate/JPA

Both ORMs manage the same database schema, which requires special backup considerations.

## Complete Backup Strategy

### 1. PostgreSQL Native Backup (Recommended)

This captures the actual database state regardless of which ORM created the data:

```bash
# Set password
export PGPASSWORD=Mamlaka2020  # Linux/Mac
set PGPASSWORD=Mamlaka2020     # Windows

# Full backup with all PostgreSQL-specific features
pg_dump -U postgres -h localhost -p 5432 \
  --format=custom \
  --verbose \
  --file="csms_full_backup_$(date +%Y%m%d_%H%M%S).backup" \
  prizma

# Alternative: Plain SQL format (larger but readable)
pg_dump -U postgres -h localhost -p 5432 \
  --clean \
  --create \
  --if-exists \
  --file="csms_complete.sql" \
  prizma
```

### 2. Prisma Schema Backup

```bash
# Navigate to frontend directory
cd frontend

# Export current Prisma schema
cp prisma/schema.prisma ../database/prisma_schema_backup.prisma

# Generate migration SQL (if using Prisma migrations)
npx prisma migrate diff \
  --from-empty \
  --to-schema-datamodel prisma/schema.prisma \
  --script > ../database/prisma_generated_schema.sql

# Pull current database state into schema (to capture any Hibernate changes)
npx prisma db pull
```

### 3. Hibernate/JPA Entity Backup

```bash
# Navigate to backend directory  
cd backend

# Copy JPA entities
cp -r src/main/java/com/zanzibar/csms/entity ../database/jpa_entities_backup/

# Copy Hibernate configuration
cp src/main/resources/application.properties ../database/
cp src/main/resources/application-dev.properties ../database/
cp src/main/resources/application-prod.properties ../database/
```

### 4. Schema Synchronization Check

Create a verification script to ensure both ORMs see the same schema:

```sql
-- File: verify_schema_sync.sql

-- Check all tables accessible by both ORMs
SELECT 
    table_name,
    table_type
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- Check column definitions
SELECT 
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'public'
ORDER BY table_name, ordinal_position;

-- Check constraints
SELECT
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_schema = 'public'
ORDER BY tc.table_name, tc.constraint_name;
```

## Backup Procedure

### Step 1: Pre-Backup Verification

```bash
# Ensure both applications can connect
cd frontend
npx prisma db pull  # This will show if Prisma can read the schema

cd ../backend
mvn spring-boot:run -Dspring.profiles.active=dev  # Verify Hibernate can connect
```

### Step 2: Stop Applications

```bash
# Stop both frontend and backend to ensure data consistency
# Windows
taskkill /F /IM node.exe
taskkill /F /IM java.exe

# Linux/Mac
pkill -f "npm run dev"
pkill -f "spring-boot:run"
```

### Step 3: Create Backups

```bash
# 1. Database backup
set PGPASSWORD=Mamlaka2020
pg_dump -U postgres -h localhost -p 5432 --format=custom prizma > csms_$(date +%Y%m%d).backup

# 2. Schema backups
cd frontend
npx prisma db pull
cp prisma/schema.prisma ../database/

cd ../backend
cp -r src/main/java/com/zanzibar/csms/entity ../database/hibernate_entities/

# 3. Data verification backup (CSV format)
psql -U postgres -d prizma -c "\copy \"User\" TO 'users.csv' CSV HEADER"
psql -U postgres -d prizma -c "\copy \"Employee\" TO 'employees.csv' CSV HEADER"
psql -U postgres -d prizma -c "\copy \"Institution\" TO 'institutions.csv' CSV HEADER"
```

## Restoration Procedure

### Step 1: Restore Database

```bash
# Create fresh database
export PGPASSWORD=Mamlaka2020
dropdb -U postgres prizma --if-exists
createdb -U postgres prizma

# Restore from backup
pg_restore -U postgres -h localhost -p 5432 -d prizma -v csms_$(date +%Y%m%d).backup

# Or if using SQL format
psql -U postgres -d prizma -f csms_complete.sql
```

### Step 2: Sync Prisma Schema

```bash
cd frontend

# Pull actual database schema
npx prisma db pull

# Compare with backed-up schema
diff prisma/schema.prisma ../database/prisma_schema_backup.prisma

# Generate Prisma client
npx prisma generate
```

### Step 3: Verify Hibernate Compatibility

```bash
cd backend

# Run with validation only (no schema changes)
mvn spring-boot:run -Dspring.profiles.active=prod

# Check logs for any schema validation errors
tail -f logs/csms.log | grep -i "schema"
```

## Important Considerations

### 1. Schema Evolution
- **Prisma**: Uses explicit migrations via `prisma migrate`
- **Hibernate**: Can auto-update schema with `spring.jpa.hibernate.ddl-auto`
- **Best Practice**: Always use Prisma for schema changes, set Hibernate to `validate` only

### 2. Data Type Mappings
Ensure consistent mappings between Prisma and Hibernate:

| PostgreSQL | Prisma | Hibernate/JPA |
|------------|---------|---------------|
| TEXT | String | @Column(columnDefinition = "TEXT") |
| TIMESTAMP | DateTime | @Temporal(TemporalType.TIMESTAMP) |
| TEXT[] | String[] | @Type(type = "string-array") |
| BOOLEAN | Boolean | Boolean |

### 3. Backup Validation Checklist

- [ ] All tables present in backup
- [ ] Row counts match expected values
- [ ] Prisma can read schema without errors
- [ ] Hibernate validates without errors
- [ ] Both apps can perform CRUD operations
- [ ] No foreign key violations
- [ ] Sequences/auto-increments preserved

## Automated Backup Script

Create `backup_dual_orm.sh`:

```bash
#!/bin/bash

# Configuration
BACKUP_DIR="/home/csms/backups/$(date +%Y%m%d_%H%M%S)"
DB_NAME="prizma"
DB_USER="postgres"
export PGPASSWORD="Mamlaka2020"

# Create backup directory
mkdir -p "$BACKUP_DIR"

echo "Starting CSMS Dual-ORM Backup..."

# 1. Stop applications
echo "Stopping applications..."
systemctl stop csms-frontend csms-backend

# 2. Database backup
echo "Backing up PostgreSQL database..."
pg_dump -U $DB_USER -h localhost -p 5432 \
  --format=custom \
  --verbose \
  --file="$BACKUP_DIR/database.backup" \
  $DB_NAME

# 3. Prisma schema backup  
echo "Backing up Prisma schema..."
cd /home/csms/project_csms/frontend
npx prisma db pull
cp prisma/schema.prisma "$BACKUP_DIR/"

# 4. Hibernate entities backup
echo "Backing up Hibernate entities..."
cd /home/csms/project_csms/backend
cp -r src/main/java/com/zanzibar/csms/entity "$BACKUP_DIR/hibernate_entities"
cp src/main/resources/application*.properties "$BACKUP_DIR/"

# 5. Export data as CSV for verification
echo "Exporting data as CSV..."
cd "$BACKUP_DIR"
psql -U $DB_USER -d $DB_NAME -c "\copy \"User\" TO 'users.csv' CSV HEADER"
psql -U $DB_USER -d $DB_NAME -c "\copy \"Employee\" TO 'employees.csv' CSV HEADER"
psql -U $DB_USER -d $DB_NAME -c "\copy \"Institution\" TO 'institutions.csv' CSV HEADER"

# 6. Create metadata file
echo "Creating backup metadata..."
cat > "$BACKUP_DIR/backup_metadata.json" << EOF
{
  "backup_date": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "database_name": "$DB_NAME",
  "prisma_version": "$(cd /home/csms/project_csms/frontend && npx prisma version --json)",
  "spring_boot_version": "3.1.5",
  "postgresql_version": "$(psql -U $DB_USER -t -c 'SELECT version()')",
  "tables_count": $(psql -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='public'"),
  "total_records": {
    "users": $(psql -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM \"User\""),
    "employees": $(psql -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM \"Employee\""),
    "institutions": $(psql -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM \"Institution\"")
  }
}
EOF

# 7. Compress backup
echo "Compressing backup..."
cd "$(dirname "$BACKUP_DIR")"
tar -czf "csms_backup_$(date +%Y%m%d_%H%M%S).tar.gz" "$(basename "$BACKUP_DIR")"

# 8. Restart applications
echo "Restarting applications..."
systemctl start csms-backend csms-frontend

echo "Backup completed: $BACKUP_DIR"
echo "Compressed file: csms_backup_$(date +%Y%m%d_%H%M%S).tar.gz"
```

## Recovery Testing

Always test your backups:

```bash
# 1. Create test database
createdb -U postgres prizma_test

# 2. Restore backup
pg_restore -U postgres -d prizma_test database.backup

# 3. Test Prisma connection
cd frontend
DATABASE_URL="postgresql://postgres:Mamlaka2020@localhost:5432/prizma_test" npx prisma db pull

# 4. Test Hibernate connection  
cd backend
mvn spring-boot:run -Dspring.profiles.active=test -Dspring.datasource.url=jdbc:postgresql://localhost:5432/prizma_test

# 5. Clean up
dropdb -U postgres prizma_test
```

This comprehensive approach ensures both ORMs remain synchronized and the database can be fully restored.