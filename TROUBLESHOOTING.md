# CSMS Troubleshooting Guide

## 🚨 Common Issues and Solutions

### Issue 1: App Not Starting

**Symptoms:**
- Backend fails to start
- Frontend shows port conflicts
- Database connection errors

**Solutions:**

#### Step 1: Check Required Services

1. **PostgreSQL Status**
   ```bash
   # Check if PostgreSQL is running
   netstat -an | findstr ":5432"
   
   # Start PostgreSQL (Windows)
   net start postgresql-x64-15
   
   # Or start manually if installed differently
   ```

2. **Port Conflicts**
   ```bash
   # Check what's using ports
   netstat -ano | findstr ":3000\|:8080\|:9000\|:9001\|:9002"
   
   # Kill processes if needed
   taskkill /F /PID [PID_NUMBER]
   ```

#### Step 2: Start Services in Order

1. **Start PostgreSQL first**
2. **Start MinIO (optional for basic functionality)**
3. **Start Backend**
4. **Start Frontend**

### Issue 2: Backend Won't Start

**Error:** `Connection refused to MinIO`

**Solution:**
The backend is now configured to start without MinIO. File upload features will be disabled until MinIO is running.

```bash
cd backend
mvn spring-boot:run -Dspring.profiles.active=prod
```

### Issue 3: Frontend Port Conflict

**Error:** `EADDRINUSE: address already in use :::9002`

**Solutions:**

1. **Use different port:**
   ```bash
   cd frontend
   npm run dev -- -p 3000
   ```

2. **Kill existing process:**
   ```bash
   netstat -ano | findstr ":9002"
   taskkill /F /PID [PID_NUMBER]
   ```

3. **Update package.json:**
   ```json
   {
     "scripts": {
       "dev": "next dev -p 3000"
     }
   }
   ```

### Issue 4: Database Connection Failed

**Error:** `Connection to localhost:5432 refused`

**Solutions:**

1. **Check PostgreSQL service:**
   ```bash
   net start postgresql-x64-15
   ```

2. **Verify credentials in backend/src/main/resources/application.properties:**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/prizma
   spring.datasource.username=postgres
   spring.datasource.password=Mamlaka2020
   ```

3. **Test connection manually:**
   ```bash
   psql -h localhost -p 5432 -U postgres -d prizma
   ```

### Issue 5: MinIO Not Available

**Error:** `MinIO service is not available`

**Solutions:**

1. **Install MinIO:**

   **Option A: Docker**
   ```bash
   docker run -d --name minio \
     -p 9000:9000 -p 9001:9001 \
     -e "MINIO_ROOT_USER=minioadmin" \
     -e "MINIO_ROOT_PASSWORD=minioadmin" \
     -v C:/minio/data:/data \
     quay.io/minio/minio server /data --console-address ":9001"
   ```

   **Option B: Windows Binary**
   ```bash
   # Download from https://min.io/download
   # Create folder C:\minio
   # Run:
   minio.exe server C:\minio\data --console-address :9001
   ```

2. **Verify MinIO is running:**
   ```bash
   curl http://localhost:9000/minio/health/live
   # Should return 200 OK
   ```

3. **App will work without MinIO but file uploads will be disabled**

## 🔧 Quick Start Commands

### Manual Startup (Recommended)

1. **Start PostgreSQL:**
   ```bash
   net start postgresql-x64-15
   ```

2. **Start MinIO (optional):**
   ```bash
   # Docker version
   docker run --rm --name minio -p 9000:9000 -p 9001:9001 \
     -e "MINIO_ROOT_USER=minioadmin" -e "MINIO_ROOT_PASSWORD=minioadmin" \
     -v C:/minio/data:/data quay.io/minio/minio server /data --console-address ":9001"
   ```

3. **Start Backend:**
   ```bash
   cd backend
   mvn spring-boot:run -Dspring.profiles.active=prod
   ```

4. **Start Frontend:**
   ```bash
   cd frontend
   npm run dev -- -p 3000
   ```

### Using Batch Scripts

1. **With MinIO:**
   ```bash
   start-services.bat
   ```

2. **Without MinIO:**
   ```bash
   start-without-minio.bat
   ```

## 🌐 Access URLs

- **Frontend:** http://localhost:3000 (or 9002 if available)
- **Backend API:** http://localhost:8080
- **MinIO Console:** http://localhost:9001 (minioadmin/minioadmin)
- **API Documentation:** http://localhost:8080/swagger-ui.html

## 🔍 Debugging Steps

### Check Service Status

```bash
# Check all ports
netstat -an | findstr ":3000\|:8080\|:9000\|:9001\|:5432"

# Check specific services
curl http://localhost:8080/actuator/health  # Backend health
curl http://localhost:3000                  # Frontend
curl http://localhost:9000/minio/health/live # MinIO health
```

### View Logs

1. **Backend logs:** Check the terminal where you ran `mvn spring-boot:run`
2. **Frontend logs:** Check the terminal where you ran `npm run dev`
3. **MinIO logs:** Check MinIO terminal or `docker logs minio`

### Common Log Messages

- ✅ `Started CsmsApplication` - Backend started successfully
- ✅ `Ready in X.Xs` - Frontend ready
- ⚠️ `MinIO is not available during startup` - MinIO not running (app still works)
- ❌ `Connection refused` - Service not running
- ❌ `EADDRINUSE` - Port already in use

## 🛠️ Reset Everything

If you encounter persistent issues:

1. **Stop all services:**
   ```bash
   # Kill all Java processes (backend)
   taskkill /F /IM java.exe
   
   # Kill all Node processes (frontend)
   taskkill /F /IM node.exe
   
   # Stop Docker containers
   docker stop minio
   ```

2. **Clear ports:**
   ```bash
   netstat -ano | findstr ":3000\|:8080\|:9000\|:9001"
   # Kill any remaining processes using these ports
   ```

3. **Restart services in order:**
   - PostgreSQL
   - MinIO (optional)
   - Backend
   - Frontend

## 📞 Getting Help

1. **Check logs first** - Most issues show clear error messages
2. **Verify service status** - Ensure required services are running
3. **Test connectivity** - Use curl to test each service
4. **Check firewall** - Ensure ports are not blocked
5. **Verify credentials** - Database and MinIO credentials must match configuration

## 🎯 Success Indicators

When everything is working correctly:

- ✅ Backend: `Started CsmsApplication` in logs
- ✅ Frontend: `Ready in X.Xs` message
- ✅ MinIO: Console accessible at http://localhost:9001
- ✅ Database: Connection successful
- ✅ Login: Can access CSMS login page
- ✅ Authentication: Can log in with valid credentials

## 🔧 Environment Specific Issues

### Windows Issues
- Use `net start` for Windows services
- Use `taskkill` instead of `kill`
- Path separators use backslash `\`
- PowerShell vs Command Prompt differences

### Docker Issues
- Ensure Docker Desktop is running
- Check Docker daemon status
- Verify port bindings
- Check volume mounts

### Java Issues
- Verify Java 17 is installed
- Check JAVA_HOME environment variable
- Maven configuration correct
- Port conflicts with other Java apps