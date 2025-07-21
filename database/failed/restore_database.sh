#!/bin/bash

echo "CSMS Database Restoration Script for Linux/Mac"
echo "=============================================="

# Set PostgreSQL password
export PGPASSWORD=password123

echo
echo "Step 1: Creating database 'prizma'..."
createdb -U postgres -h localhost -p 5432 prizma
if [ $? -ne 0 ]; then
    echo "Warning: Database might already exist or PostgreSQL is not running"
    echo "Continuing with restoration..."
fi

echo
echo "Step 2: Restoring database structure and data..."
if [ -f "csms_complete.sql" ]; then
    echo "Restoring from complete backup..."
    psql -U postgres -h localhost -p 5432 -d prizma -f csms_complete.sql
elif [ -f "csms_schema_only.sql" ]; then
    echo "Restoring schema only..."
    psql -U postgres -h localhost -p 5432 -d prizma -f csms_schema_only.sql
    if [ -f "csms_data_only.sql" ]; then
        echo "Restoring data..."
        psql -U postgres -h localhost -p 5432 -d prizma -f csms_data_only.sql
    fi
else
    echo "Error: No backup files found!"
    echo "Please ensure csms_complete.sql or csms_schema_only.sql exists in this directory."
    exit 1
fi

echo
echo "Step 3: Verifying database restoration..."
psql -U postgres -h localhost -p 5432 -d prizma -c "SELECT COUNT(*) as user_count FROM \"User\";"
psql -U postgres -h localhost -p 5432 -d prizma -c "SELECT COUNT(*) as employee_count FROM \"Employee\";"

echo
echo "Database restoration completed!"
echo
echo "Next steps:"
echo "1. Navigate to the project root directory"
echo "2. Install frontend dependencies: cd frontend && npm install"
echo "3. Generate Prisma client: npx prisma generate"
echo "4. Install backend dependencies: cd backend && mvn clean install"
echo "5. Copy database_config.env to frontend/.env"
echo "6. Start backend: cd backend && mvn spring-boot:run -Dspring.profiles.active=dev"
echo "7. Start frontend: cd frontend && npm run dev"
echo
echo "System will be available at:"
echo "- Frontend: http://localhost:9002"
echo "- Backend API: http://localhost:8080/api"
echo