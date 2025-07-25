#!/bin/bash

# CSMS Database Restoration Script for Linux/Unix
# This script restores the CSMS database on a new VPS
# Run this script with appropriate permissions

set -e  # Exit on any error

echo "========================================="
echo "CSMS Database Restoration Script"
echo "========================================="
echo

# Check if PostgreSQL is installed
echo "[1/6] Checking PostgreSQL installation..."
if ! command -v pg_config &> /dev/null; then
    echo "ERROR: PostgreSQL is not installed"
    echo "Please install PostgreSQL 15 or later first:"
    echo "  Ubuntu/Debian: sudo apt-get install postgresql postgresql-contrib"
    echo "  CentOS/RHEL: sudo yum install postgresql postgresql-server"
    exit 1
fi

echo "PostgreSQL found: $(pg_config --version)"
echo

# Check if psql is available
echo "[2/6] Checking psql command..."
if ! command -v psql &> /dev/null; then
    echo "ERROR: psql command not found"
    echo "Please install PostgreSQL client tools"
    exit 1
fi

echo "psql command available: $(psql --version)"
echo

# Set database credentials
DB_NAME="prizma"
DB_USER="postgres"
read -sp "Enter PostgreSQL password for user 'postgres': " DB_PASSWORD
echo
export PGPASSWORD="$DB_PASSWORD"

echo
echo "[3/6] Creating database '$DB_NAME'..."
psql -U "$DB_USER" -c "CREATE DATABASE $DB_NAME;" 2>/dev/null || {
    echo "Database '$DB_NAME' may already exist. Continuing..."
}

echo
echo "[4/6] Creating database schema..."
psql -U "$DB_USER" -d "$DB_NAME" -f schema-backup.sql
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to create database schema"
    exit 1
fi

echo "Schema created successfully."
echo

echo "[5/6] Importing data..."
psql -U "$DB_USER" -d "$DB_NAME" -f csms-data-export.sql
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to import data"
    exit 1
fi

echo "Data imported successfully."
echo

echo "[6/6] Verifying installation..."
psql -U "$DB_USER" -d "$DB_NAME" -c "SELECT 'Institutions: ' || COUNT(*) FROM \"Institution\";"
psql -U "$DB_USER" -d "$DB_NAME" -c "SELECT 'Users: ' || COUNT(*) FROM \"User\";"
psql -U "$DB_USER" -d "$DB_NAME" -c "SELECT 'Employees: ' || COUNT(*) FROM \"Employee\";"

echo
echo "========================================="
echo "Database restoration completed successfully!"
echo "========================================="
echo
echo "Database Details:"
echo "- Name: $DB_NAME"
echo "- Host: localhost"
echo "- Port: 5432"
echo "- User: $DB_USER"
echo
echo "Next steps:"
echo "1. Update your .env files with the database connection"
echo "2. Install Node.js dependencies: npm install"
echo "3. Generate Prisma client: npx prisma generate"
echo "4. Start your application"
echo

# Unset password
unset PGPASSWORD