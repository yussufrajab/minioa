#!/bin/bash

# CSMS Database Sample Data Population Script
echo "==========================================="
echo "CSMS Database Sample Data Population"
echo "==========================================="
echo ""

# Database configuration
DB_NAME="csms_db"
DB_USER="postgres"
DB_PASSWORD="Mamlaka2020"
DB_HOST="localhost"
DB_PORT="5432"

# Set password for psql
export PGPASSWORD=$DB_PASSWORD

echo "Database: $DB_NAME"
echo "Host: $DB_HOST:$DB_PORT"
echo "User: $DB_USER"
echo ""

# Check connection
echo "Testing database connection..."
if pg_isready -h $DB_HOST -p $DB_PORT -U $DB_USER > /dev/null 2>&1; then
    echo "✅ Database connection successful"
else
    echo "❌ Database connection failed"
    exit 1
fi

# Check if database has tables
table_count=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" | xargs)
echo "📊 Tables found: $table_count"

if [ "$table_count" -eq 0 ]; then
    echo "❌ No tables found in database!"
    echo "💡 Please run the application first to create tables:"
    echo "   ./run-development.sh"
    exit 1
fi

echo ""

# Check if data already exists
employee_count=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM employees;" 2>/dev/null | xargs)
if [ "$employee_count" -gt 0 ]; then
    echo "⚠️  Warning: Database already contains $employee_count employees"
    echo ""
    read -p "Do you want to continue and add more sample data? (y/N): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Operation cancelled."
        exit 0
    fi
fi

echo "🚀 Populating database with sample data..."
echo ""

# Execute the sample data script
if psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f populate_sample_data.sql; then
    echo ""
    echo "✅ Sample data population completed successfully!"
    echo ""
    
    # Show summary
    echo "📊 Database Summary:"
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "
    SELECT 'Institutions' as table_name, COUNT(*) as record_count FROM institutions
    UNION ALL
    SELECT 'Employees', COUNT(*) FROM employees
    UNION ALL
    SELECT 'Users', COUNT(*) FROM users
    UNION ALL
    SELECT 'Requests', COUNT(*) FROM requests
    UNION ALL
    SELECT 'Complaints', COUNT(*) FROM complaints
    UNION ALL
    SELECT 'Audit Logs', COUNT(*) FROM audit_logs
    ORDER BY table_name;"
    
else
    echo ""
    echo "❌ Sample data population failed!"
    echo "Please check the error messages above."
    exit 1
fi

echo ""
echo "🎉 Sample data population completed!"
echo ""
echo "Sample User Accounts Created:"
echo "=============================="
echo "🏢 HRO (HR Officer): hro001@csms.go.tz / password123"
echo "👨‍💼 HHRMD (Head of HR): hhrmd001@csms.go.tz / password123"
echo "👩‍💼 HRMO (HR Management Officer): hrmo001@csms.go.tz / password123"
echo "⚖️ DO (Disciplinary Officer): do001@csms.go.tz / password123"
echo "👤 Employee: emp001@csms.go.tz / password123"
echo "🔧 Admin: admin001@csms.go.tz / password123"
echo "📋 CSCS (Commission Secretary): cscs001@csms.go.tz / password123"
echo "📊 PO (Planning Officer): po001@csms.go.tz / password123"
echo "👥 HRRP (HR Responsible Personnel): hrrp001@csms.go.tz / password123"
echo ""
echo "Note: All passwords are hashed using BCrypt"
echo "Default password for testing: password123"
echo ""
echo "✅ Ready to test the CSMS application with sample data!"