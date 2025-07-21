#!/bin/bash

# CSMS Database Backup Script
# This script creates comprehensive backups of the PostgreSQL database

# Configuration
DB_NAME="csms_db"
DB_USER="postgres"
DB_PASSWORD="Mamlaka2020"
DB_HOST="localhost"
DB_PORT="5432"
BACKUP_DIR="/mnt/c/hamisho/spring/csms/database-backup"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if PostgreSQL is running
check_postgres() {
    print_status "Checking PostgreSQL connection..."
    if pg_isready -h $DB_HOST -p $DB_PORT -U $DB_USER > /dev/null 2>&1; then
        print_status "PostgreSQL is running and accessible"
        return 0
    else
        print_error "PostgreSQL is not running or not accessible"
        return 1
    fi
}

# Function to create SQL dump
create_sql_dump() {
    print_status "Creating SQL dump backup..."
    
    # Set password for pg_dump
    export PGPASSWORD=$DB_PASSWORD
    
    # Create SQL dump
    pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
        --verbose \
        --clean \
        --create \
        --if-exists \
        --format=plain \
        --encoding=UTF8 \
        --no-owner \
        --no-privileges \
        > "$BACKUP_DIR/csms_db_backup_$TIMESTAMP.sql"
    
    if [ $? -eq 0 ]; then
        print_status "SQL dump created successfully: csms_db_backup_$TIMESTAMP.sql"
        
        # Get file size
        file_size=$(ls -lh "$BACKUP_DIR/csms_db_backup_$TIMESTAMP.sql" | awk '{print $5}')
        print_status "Backup file size: $file_size"
        
        return 0
    else
        print_error "Failed to create SQL dump"
        return 1
    fi
}

# Function to create compressed binary dump
create_compressed_dump() {
    print_status "Creating compressed binary dump..."
    
    # Set password for pg_dump
    export PGPASSWORD=$DB_PASSWORD
    
    # Create compressed binary dump
    pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
        --verbose \
        --format=custom \
        --compress=9 \
        --encoding=UTF8 \
        --no-owner \
        --no-privileges \
        --file="$BACKUP_DIR/csms_db_backup_$TIMESTAMP.dump"
    
    if [ $? -eq 0 ]; then
        print_status "Compressed dump created successfully: csms_db_backup_$TIMESTAMP.dump"
        
        # Get file size
        file_size=$(ls -lh "$BACKUP_DIR/csms_db_backup_$TIMESTAMP.dump" | awk '{print $5}')
        print_status "Compressed backup file size: $file_size"
        
        return 0
    else
        print_error "Failed to create compressed dump"
        return 1
    fi
}

# Function to create schema-only backup
create_schema_backup() {
    print_status "Creating schema-only backup..."
    
    # Set password for pg_dump
    export PGPASSWORD=$DB_PASSWORD
    
    # Create schema-only backup
    pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
        --verbose \
        --schema-only \
        --format=plain \
        --encoding=UTF8 \
        --no-owner \
        --no-privileges \
        > "$BACKUP_DIR/csms_db_schema_$TIMESTAMP.sql"
    
    if [ $? -eq 0 ]; then
        print_status "Schema backup created successfully: csms_db_schema_$TIMESTAMP.sql"
        return 0
    else
        print_error "Failed to create schema backup"
        return 1
    fi
}

# Function to create data-only backup
create_data_backup() {
    print_status "Creating data-only backup..."
    
    # Set password for pg_dump
    export PGPASSWORD=$DB_PASSWORD
    
    # Create data-only backup
    pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
        --verbose \
        --data-only \
        --format=plain \
        --encoding=UTF8 \
        --no-owner \
        --no-privileges \
        > "$BACKUP_DIR/csms_db_data_$TIMESTAMP.sql"
    
    if [ $? -eq 0 ]; then
        print_status "Data backup created successfully: csms_db_data_$TIMESTAMP.sql"
        return 0
    else
        print_error "Failed to create data backup"
        return 1
    fi
}

# Function to create database info file
create_db_info() {
    print_status "Creating database information file..."
    
    info_file="$BACKUP_DIR/csms_db_info_$TIMESTAMP.txt"
    
    cat > "$info_file" << EOF
CSMS Database Backup Information
================================
Backup Date: $(date)
Database Name: $DB_NAME
Database Host: $DB_HOST
Database Port: $DB_PORT
Database User: $DB_USER
PostgreSQL Version: $(psql --version)
Backup Directory: $BACKUP_DIR

Database Size Information:
EOF
    
    # Set password for psql
    export PGPASSWORD=$DB_PASSWORD
    
    # Add database size information
    echo "Database Size:" >> "$info_file"
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT pg_size_pretty(pg_database_size('$DB_NAME')) as database_size;" >> "$info_file"
    
    echo -e "\nTable Information:" >> "$info_file"
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT schemaname, tablename, pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size FROM pg_tables WHERE schemaname = 'public' ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;" >> "$info_file"
    
    echo -e "\nTable Row Counts:" >> "$info_file"
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT schemaname, tablename, n_tup_ins as inserts, n_tup_upd as updates, n_tup_del as deletes FROM pg_stat_user_tables WHERE schemaname = 'public' ORDER BY tablename;" >> "$info_file"
    
    if [ $? -eq 0 ]; then
        print_status "Database info file created successfully: csms_db_info_$TIMESTAMP.txt"
        return 0
    else
        print_error "Failed to create database info file"
        return 1
    fi
}

# Function to clean up old backups (keep last 5)
cleanup_old_backups() {
    print_status "Cleaning up old backups (keeping last 5)..."
    
    # Clean up SQL dumps
    ls -t "$BACKUP_DIR"/csms_db_backup_*.sql 2>/dev/null | tail -n +6 | xargs -r rm -f
    
    # Clean up compressed dumps
    ls -t "$BACKUP_DIR"/csms_db_backup_*.dump 2>/dev/null | tail -n +6 | xargs -r rm -f
    
    # Clean up schema backups
    ls -t "$BACKUP_DIR"/csms_db_schema_*.sql 2>/dev/null | tail -n +6 | xargs -r rm -f
    
    # Clean up data backups
    ls -t "$BACKUP_DIR"/csms_db_data_*.sql 2>/dev/null | tail -n +6 | xargs -r rm -f
    
    # Clean up info files
    ls -t "$BACKUP_DIR"/csms_db_info_*.txt 2>/dev/null | tail -n +6 | xargs -r rm -f
    
    print_status "Old backups cleaned up successfully"
}

# Function to verify backup integrity
verify_backup() {
    print_status "Verifying backup integrity..."
    
    # Check if backup files exist and are not empty
    sql_backup="$BACKUP_DIR/csms_db_backup_$TIMESTAMP.sql"
    dump_backup="$BACKUP_DIR/csms_db_backup_$TIMESTAMP.dump"
    
    if [ -f "$sql_backup" ] && [ -s "$sql_backup" ]; then
        print_status "SQL backup file exists and is not empty"
        
        # Check if SQL file contains expected content
        if grep -q "CREATE TABLE" "$sql_backup" && grep -q "INSERT INTO" "$sql_backup"; then
            print_status "SQL backup contains table structures and data"
        else
            print_warning "SQL backup may be incomplete"
        fi
    else
        print_error "SQL backup file is missing or empty"
    fi
    
    if [ -f "$dump_backup" ] && [ -s "$dump_backup" ]; then
        print_status "Compressed backup file exists and is not empty"
        
        # Test compressed backup integrity
        export PGPASSWORD=$DB_PASSWORD
        if pg_restore --list "$dump_backup" > /dev/null 2>&1; then
            print_status "Compressed backup integrity verified"
        else
            print_error "Compressed backup integrity check failed"
        fi
    else
        print_error "Compressed backup file is missing or empty"
    fi
}

# Main execution
main() {
    print_status "Starting CSMS Database Backup Process..."
    print_status "Timestamp: $TIMESTAMP"
    print_status "Backup Directory: $BACKUP_DIR"
    
    # Check if backup directory exists
    if [ ! -d "$BACKUP_DIR" ]; then
        print_status "Creating backup directory: $BACKUP_DIR"
        mkdir -p "$BACKUP_DIR"
    fi
    
    # Check PostgreSQL connection
    if ! check_postgres; then
        print_error "Cannot connect to PostgreSQL. Exiting."
        exit 1
    fi
    
    # Create backups
    create_sql_dump
    create_compressed_dump
    create_schema_backup
    create_data_backup
    create_db_info
    
    # Verify backup integrity
    verify_backup
    
    # Clean up old backups
    cleanup_old_backups
    
    print_status "Backup process completed successfully!"
    print_status "Backup files created:"
    ls -la "$BACKUP_DIR"/*$TIMESTAMP* 2>/dev/null || print_warning "No backup files found with current timestamp"
    
    # Display total backup size
    total_size=$(du -sh "$BACKUP_DIR" | cut -f1)
    print_status "Total backup directory size: $total_size"
}

# Execute main function
main "$@"