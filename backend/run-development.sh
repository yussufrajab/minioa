#!/bin/bash

# CSMS Development Mode Startup Script
# This script runs the application in development mode to create/update database tables

echo "====================================="
echo "CSMS Development Mode Startup"
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
if [ $? -eq 0 ]; then
    echo "✅ Database 'csms_db' exists"
    
    # Check table count
    table_count=$(PGPASSWORD=Mamlaka2020 psql -h localhost -U postgres -d csms_db -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" | xargs)
    echo "📊 Current table count: $table_count"
    
    if [ "$table_count" -gt 0 ]; then
        echo ""
        echo "⚠️  WARNING: Database already contains tables!"
        echo "💡 Consider running in production mode to preserve existing data:"
        echo "   ./run-production.sh"
        echo ""
        read -p "Continue with development mode? (y/N): " -n 1 -r
        echo ""
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            echo "Aborted."
            exit 1
        fi
    fi
else
    echo "⚠️  Database 'csms_db' does not exist - will be created"
fi

echo ""
echo "🚀 Starting CSMS in DEVELOPMENT mode..."
echo "📝 This mode allows table creation and updates"
echo "🔧 Profile: dev"
echo "🗃️  DDL Mode: update (can modify tables)"
echo ""
echo "Press Ctrl+C to stop the application"
echo ""

# Start the application in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

echo ""
echo "Application stopped."