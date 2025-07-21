#!/bin/bash

# Simple CSMS Database Backup Script
echo "=== CSMS Database Simple Backup ==="
echo "Started: $(date)"

# Database configuration
DB_NAME="csms_db"
DB_USER="postgres"
DB_PASSWORD="Mamlaka2020"
DB_HOST="localhost"
DB_PORT="5432"

# Backup directory
BACKUP_DIR="/mnt/c/hamisho/spring/csms/database-backup"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Set password for pg_dump
export PGPASSWORD=$DB_PASSWORD

echo "Database: $DB_NAME"
echo "Host: $DB_HOST:$DB_PORT"
echo "User: $DB_USER"
echo ""

# Check connection
echo "Testing database connection..."
if pg_isready -h $DB_HOST -p $DB_PORT -U $DB_USER > /dev/null 2>&1; then
    echo "✓ Database connection successful"
else
    echo "✗ Database connection failed"
    exit 1
fi

# Check if database has tables
table_count=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" | xargs)
echo "Tables found: $table_count"

if [ "$table_count" -eq 0 ]; then
    echo "⚠ Warning: Database has no tables. Creating empty backup."
fi

echo ""
echo "Creating backups..."

# 1. Create SQL backup
echo "1. Creating SQL backup..."
pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    --clean \
    --create \
    --if-exists \
    --encoding=UTF8 \
    --no-owner \
    --no-privileges \
    > "$BACKUP_DIR/simple_backup_$TIMESTAMP.sql"

if [ $? -eq 0 ]; then
    sql_size=$(ls -lh "$BACKUP_DIR/simple_backup_$TIMESTAMP.sql" | awk '{print $5}')
    echo "✓ SQL backup created: simple_backup_$TIMESTAMP.sql ($sql_size)"
else
    echo "✗ SQL backup failed"
fi

# 2. Create compressed backup
echo "2. Creating compressed backup..."
pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    --format=custom \
    --compress=9 \
    --encoding=UTF8 \
    --no-owner \
    --no-privileges \
    --file="$BACKUP_DIR/simple_backup_$TIMESTAMP.dump"

if [ $? -eq 0 ]; then
    dump_size=$(ls -lh "$BACKUP_DIR/simple_backup_$TIMESTAMP.dump" | awk '{print $5}')
    echo "✓ Compressed backup created: simple_backup_$TIMESTAMP.dump ($dump_size)"
else
    echo "✗ Compressed backup failed"
fi

# 3. Create database info
echo "3. Creating database info..."
info_file="$BACKUP_DIR/simple_backup_info_$TIMESTAMP.txt"

cat > "$info_file" << EOF
CSMS Database Backup Information
================================
Backup Date: $(date)
Database Name: $DB_NAME
Database Host: $DB_HOST:$DB_PORT
Database User: $DB_USER
PostgreSQL Version: $(psql --version)

Database Statistics:
EOF

# Add database size
echo "Database Size:" >> "$info_file"
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT pg_size_pretty(pg_database_size('$DB_NAME')) as size;" >> "$info_file" 2>/dev/null

echo "" >> "$info_file"
echo "Tables:" >> "$info_file"
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "\dt" >> "$info_file" 2>/dev/null

echo "✓ Database info created: simple_backup_info_$TIMESTAMP.txt"

echo ""
echo "=== Backup Summary ==="
echo "Backup files created in: $BACKUP_DIR"
ls -la "$BACKUP_DIR"/simple_backup*$TIMESTAMP* 2>/dev/null

echo ""
echo "Total backup directory size: $(du -sh $BACKUP_DIR | cut -f1)"
echo "Completed: $(date)"
echo "================="