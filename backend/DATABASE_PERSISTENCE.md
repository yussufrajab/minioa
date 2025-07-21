# CSMS Database Persistence Configuration

This document explains how the CSMS application is configured to ensure database tables remain forever once created.

## 🔒 Database Persistence Strategy

### Configuration Overview

The application now uses multiple configuration profiles to ensure maximum database safety:

1. **Default Mode (Production)**: `validate` - Tables are never modified
2. **Development Mode**: `update` - Tables can be created/updated
3. **Production Mode**: `validate` + enhanced safety

## 📁 Configuration Files

### 1. `application.properties` (Default/Production)
```properties
# Safe production configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.hbm2ddl.auto=validate
spring.jpa.properties.hibernate.globally_quoted_identifiers=true
```

**Features:**
- ✅ **Never modifies existing tables**
- ✅ **Only validates schema matches entities**
- ✅ **Fails fast if schema doesn't match**
- ✅ **Maximum safety for production**

### 2. `application-dev.properties` (Development)
```properties
# Development configuration for table creation
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**Features:**
- ✅ **Creates tables if they don't exist**
- ✅ **Updates table structure if needed**
- ✅ **Detailed SQL logging**
- ⚠️ **Use only for development**

### 3. `application-prod.properties` (Enhanced Production)
```properties
# Maximum safety production configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.hbm2ddl.halt_on_error=true
```

**Features:**
- ✅ **Ultra-safe validate-only mode**
- ✅ **Enhanced connection pooling**
- ✅ **Optimized logging**
- ✅ **Halts on any schema errors**

## 🚀 Usage Instructions

### First Time Setup (Create Tables)

```bash
# Run in development mode to create tables
./run-development.sh

# OR manually with profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production Usage (Preserve Tables)

```bash
# Run in production mode (safe)
./run-production.sh

# OR manually with profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# OR default mode (also safe)
mvn spring-boot:run
```

## 🛡️ Safety Features

### 1. **Automatic Database Monitoring**
- `DatabaseInitializationConfig` class monitors database state
- Reports table count on startup
- Warns about dangerous configurations
- Validates DDL settings

### 2. **Profile-Based Safety**
- **Development Profile**: Allows table creation/updates
- **Production Profile**: Read-only, validate-only mode
- **Default Profile**: Safe validate mode

### 3. **Configuration Validation**
- Checks for dangerous DDL settings
- Warns about `create` or `create-drop` modes
- Logs database connection details
- Validates table existence

### 4. **Startup Scripts**
- `run-development.sh`: Safe development mode
- `run-production.sh`: Safe production mode
- Both check database state before starting

## 📊 Database States

### State 1: No Database/Tables
```
Database: Not exists
Tables: 0
Action: Run development mode to create
```

### State 2: Database Exists, No Tables
```
Database: Exists
Tables: 0
Action: Run development mode to create tables
```

### State 3: Database and Tables Exist
```
Database: Exists
Tables: 32+
Action: Run production mode to preserve
```

## 🔧 Configuration Examples

### Development Mode Startup
```bash
./run-development.sh
```
**Output:**
```
✅ PostgreSQL is running
✅ Database 'csms_db' exists
📊 Current table count: 0
🚀 Starting CSMS in DEVELOPMENT mode...
```

### Production Mode Startup
```bash
./run-production.sh
```
**Output:**
```
✅ PostgreSQL is running
✅ Database 'csms_db' exists
📊 Current table count: 32
✅ Database tables found
🚀 Starting CSMS in PRODUCTION mode...
```

## ⚠️ Important Warnings

### Dangerous Configurations (AVOIDED)
```properties
# NEVER use these in production:
spring.jpa.hibernate.ddl-auto=create       # Drops all tables!
spring.jpa.hibernate.ddl-auto=create-drop  # Drops tables on shutdown!
```

### Safe Configurations (RECOMMENDED)
```properties
# Always safe:
spring.jpa.hibernate.ddl-auto=validate     # Read-only validation
spring.jpa.hibernate.ddl-auto=update       # Only for development
```

## 🔍 Monitoring and Verification

### Check Table Count
```bash
PGPASSWORD=Mamlaka2020 psql -h localhost -U postgres -d csms_db -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';"
```

### List All Tables
```bash
PGPASSWORD=Mamlaka2020 psql -h localhost -U postgres -d csms_db -c "\dt"
```

### Application Logs
The application logs database information on startup:
```
INFO - Active profiles: prod
INFO - DDL Auto mode: validate
INFO - ✅ Found 32 tables in database
INFO - ✅ Safe mode: DDL Auto is set to 'validate' - tables will be preserved
```

## 🆘 Troubleshooting

### Problem: "Table doesn't exist" error
**Solution:** Run development mode first to create tables:
```bash
./run-development.sh
```

### Problem: Application won't start with "validate" mode
**Cause:** Schema mismatch between entities and database
**Solution:** 
1. Check entity definitions
2. Run development mode to update schema
3. Then switch back to production mode

### Problem: Accidentally deleted tables
**Solution:** 
1. Restore from backup in `database-backup/` folder
2. Or run development mode to recreate tables

## 📋 Checklist for Production Deployment

- [ ] ✅ Tables created in development mode
- [ ] ✅ Database backup completed
- [ ] ✅ Application tested in production mode
- [ ] ✅ DDL mode set to `validate`
- [ ] ✅ No dangerous configurations present
- [ ] ✅ Startup scripts tested
- [ ] ✅ Database monitoring enabled

## 🔗 Related Files

- `application.properties` - Default safe configuration
- `application-dev.properties` - Development configuration
- `application-prod.properties` - Enhanced production configuration
- `DatabaseInitializationConfig.java` - Database monitoring
- `run-development.sh` - Development startup script
- `run-production.sh` - Production startup script
- `database-backup/` - Database backup utilities

---

**Summary**: The CSMS application is now configured to ensure that once database tables are created, they will persist forever. The application uses safe `validate` mode by default and provides development mode only when explicitly requested. Multiple safety layers prevent accidental data loss.