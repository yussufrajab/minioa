#!/bin/bash

# CSMS Database Restore Script
# This script restores PostgreSQL database from backup files

# Configuration
DB_NAME="csms_db"
DB_USER="postgres"
DB_PASSWORD="Mamlaka2020"
DB_HOST="localhost"
DB_PORT="5432"
BACKUP_DIR="/mnt/c/hamisho/spring/csms/database-backup"

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

# Function to list available backups
list_backups() {
    print_status "Available backup files:"
    echo ""
    echo "SQL Backups:"
    ls -la "$BACKUP_DIR"/csms_db_backup_*.sql 2>/dev/null | awk '{print "  " $9 " (" $5 " bytes, " $6 " " $7 " " $8 ")"}'
    echo ""
    echo "Compressed Backups:"
    ls -la "$BACKUP_DIR"/csms_db_backup_*.dump 2>/dev/null | awk '{print "  " $9 " (" $5 " bytes, " $6 " " $7 " " $8 ")"}'
    echo ""
}

# Function to restore from SQL backup
restore_from_sql() {
    local backup_file="$1"
    
    if [ ! -f "$backup_file" ]; then
        print_error "Backup file not found: $backup_file"
        return 1
    fi
    
    print_status "Restoring database from SQL backup: $(basename "$backup_file")"
    print_warning "This will DROP the existing database and recreate it!"
    
    # Set password for psql
    export PGPASSWORD=$DB_PASSWORD
    
    # Execute SQL backup
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -f "$backup_file"
    
    if [ $? -eq 0 ]; then
        print_status "Database restored successfully from SQL backup"
        return 0
    else
        print_error "Failed to restore from SQL backup"
        return 1
    fi
}

# Function to restore from compressed backup
restore_from_dump() {
    local backup_file="$1"
    
    if [ ! -f "$backup_file" ]; then
        print_error "Backup file not found: $backup_file"
        return 1
    fi
    
    print_status "Restoring database from compressed backup: $(basename "$backup_file")"
    print_warning "This will DROP the existing database and recreate it!"
    
    # Set password for pg_restore
    export PGPASSWORD=$DB_PASSWORD
    
    # Drop existing database
    dropdb -h $DB_HOST -p $DB_PORT -U $DB_USER --if-exists $DB_NAME
    
    # Create new database
    createdb -h $DB_HOST -p $DB_PORT -U $DB_USER $DB_NAME
    
    # Restore from compressed backup
    pg_restore -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
        --verbose \
        --clean \
        --if-exists \
        --no-owner \
        --no-privileges \
        "$backup_file"
    
    if [ $? -eq 0 ]; then
        print_status "Database restored successfully from compressed backup"
        return 0
    else
        print_error "Failed to restore from compressed backup"
        return 1
    fi
}

# Function to verify restore
verify_restore() {
    print_status "Verifying database restore..."
    
    # Set password for psql
    export PGPASSWORD=$DB_PASSWORD
    
    # Check if database exists
    if psql -h $DB_HOST -p $DB_PORT -U $DB_USER -lqt | cut -d \| -f 1 | grep -qw $DB_NAME; then
        print_status "Database $DB_NAME exists"
        
        # Check table count
        table_count=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" | xargs)
        print_status "Number of tables in database: $table_count"
        
        # List tables
        print_status "Tables in database:"
        psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "\dt"
        
        return 0
    else
        print_error "Database $DB_NAME does not exist after restore"
        return 1
    fi
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS] [BACKUP_FILE]"
    echo ""
    echo "Options:"
    echo "  -l, --list          List available backup files"
    echo "  -s, --sql FILE      Restore from SQL backup file"
    echo "  -d, --dump FILE     Restore from compressed dump file"
    echo "  -v, --verify        Verify database after restore"
    echo "  -h, --help          Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 --list"
    echo "  $0 --sql csms_db_backup_20241217_143021.sql"
    echo "  $0 --dump csms_db_backup_20241217_143021.dump"
    echo ""
    echo "If no backup file is specified, the script will use the most recent backup."
}

# Function to get latest backup
get_latest_backup() {
    local backup_type="$1"
    
    if [ "$backup_type" = "sql" ]; then
        latest=$(ls -t "$BACKUP_DIR"/csms_db_backup_*.sql 2>/dev/null | head -1)
    elif [ "$backup_type" = "dump" ]; then
        latest=$(ls -t "$BACKUP_DIR"/csms_db_backup_*.dump 2>/dev/null | head -1)
    fi
    
    echo "$latest"
}

# Main execution
main() {
    if [ $# -eq 0 ]; then
        show_usage
        exit 1
    fi
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            -l|--list)
                list_backups
                exit 0
                ;;
            -s|--sql)
                backup_file="$2"
                if [ -z "$backup_file" ]; then
                    backup_file=$(get_latest_backup "sql")
                    if [ -z "$backup_file" ]; then
                        print_error "No SQL backup files found"
                        exit 1
                    fi
                    print_status "Using latest SQL backup: $(basename "$backup_file")"
                else
                    # If relative path, prepend backup directory
                    if [[ "$backup_file" != /* ]]; then
                        backup_file="$BACKUP_DIR/$backup_file"
                    fi
                fi
                restore_from_sql "$backup_file"
                verify_restore
                exit $?
                ;;
            -d|--dump)
                backup_file="$2"
                if [ -z "$backup_file" ]; then
                    backup_file=$(get_latest_backup "dump")
                    if [ -z "$backup_file" ]; then
                        print_error "No compressed backup files found"
                        exit 1
                    fi
                    print_status "Using latest compressed backup: $(basename "$backup_file")"
                else
                    # If relative path, prepend backup directory
                    if [[ "$backup_file" != /* ]]; then
                        backup_file="$BACKUP_DIR/$backup_file"
                    fi
                fi
                restore_from_dump "$backup_file"
                verify_restore
                exit $?
                ;;
            -v|--verify)
                verify_restore
                exit $?
                ;;
            -h|--help)
                show_usage
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
        shift
    done
}

# Execute main function
main "$@"