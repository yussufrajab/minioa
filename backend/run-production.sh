#!/bin/bash

# CSMS Production Mode Startup Script
# This script runs the application in production mode to preserve database tables

echo "====================================="
echo "CSMS Production Mode Startup"
echo "====================================="
echo ""

# Check if PostgreSQL is running
if ! pg_isready -h localhost -p 5432 -U postgres > /dev/null 2>&1; then
    echo "❌ PostgreSQL is not running!"
    echo "Please start PostgreSQL service first:"
    echo "  sudo systemctl start postgresql"
    echo ""
    exit 1
fi

echo "✅ PostgreSQL is running"

# Check if database exists
PGPASSWORD=Mamlaka2020 psql -h localhost -U postgres -lqt | cut -d \| -f 1 | grep -qw csms_db
if [ $? -ne 0 ]; then
    echo "❌ Database 'csms_db' does not exist!"
    echo "💡 Run development mode first to create the database:"
    echo "   ./run-development.sh"
    echo ""
    exit 1
fi

echo "✅ Database 'csms_db' exists"

# Check table count
table_count=$(PGPASSWORD=Mamlaka2020 psql -h localhost -U postgres -d csms_db -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" | xargs)
echo "📊 Current table count: $table_count"

if [ "$table_count" -eq 0 ]; then
    echo "❌ No tables found in database!"
    echo "💡 Run development mode first to create tables:"
    echo "   ./run-development.sh"
    echo ""
    exit 1
fi

echo "✅ Database tables found"
echo ""
echo "🚀 Starting CSMS in PRODUCTION mode..."
echo "🔒 This mode preserves existing tables (validate-only)"
echo "🔧 Profile: prod"
echo "🗃️  DDL Mode: validate (read-only, safe)"
echo ""
echo "Press Ctrl+C to stop the application"
echo ""

# Start the application in production mode
mvn spring-boot:run -Dspring-boot.run.profiles=prod

echo ""
echo "Application stopped."