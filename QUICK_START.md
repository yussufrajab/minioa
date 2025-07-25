# CSMS Quick Start Guide

## 🚀 Get CSMS Running in 5 Minutes

### Prerequisites
- Java 17 installed
- Node.js installed
- PostgreSQL running on port 5432
- Git bash or Command Prompt

### Option 1: Quick Start (Basic Features)

```bash
# 1. Start PostgreSQL
net start postgresql-x64-15

# 2. Open 2 terminals and run:
# Terminal 1 - Backend
cd backend
mvn spring-boot:run -Dspring.profiles.active=prod

# Terminal 2 - Frontend (wait for backend to start)
cd frontend
npm run dev -- -p 3000
```

**Access:** http://localhost:3000

### Option 2: Full Features (with File Upload)

```bash
# 1. Start PostgreSQL
net start postgresql-x64-15

# 2. Start MinIO (Docker required)
docker run --rm --name minio -p 9000:9000 -p 9001:9001 \
  -e "MINIO_ROOT_USER=minioadmin" -e "MINIO_ROOT_PASSWORD=minioadmin" \
  -v C:/minio/data:/data quay.io/minio/minio server /data --console-address ":9001"

# 3. Start Backend
cd backend
mvn spring-boot:run -Dspring.profiles.active=prod

# 4. Start Frontend
cd frontend
npm run dev -- -p 3000
```

**Access:** 
- App: http://localhost:3000
- MinIO: http://localhost:9001 (minioadmin/minioadmin)

### Option 3: Automated Start

```bash
# Run the batch script
start-services.bat
```

## 🔑 Default Login Credentials

```
Username: admin
Password: password123
```

## 📱 What You Can Do

### ✅ Available Features
- User authentication
- Employee management
- All 8 HR request modules:
  - Employee Confirmation
  - Leave Without Pay (LWOP)
  - Promotion
  - Cadre Change
  - Retirement
  - Resignation
  - Service Extension
  - Termination/Dismissal
- Complaints management
- Reports generation
- Dashboard analytics

### 🎯 Test the System

1. **Login:** Use admin/password123
2. **Create Request:** Go to any HR module
3. **Search Employee:** Use ZanID like "221458232"
4. **Submit Request:** Fill form and submit
5. **Review Request:** Switch roles to approve/reject
6. **Generate Report:** Go to Reports section

### 🔧 Troubleshooting

**Port Conflicts?**
```bash
# Use different port
npm run dev -- -p 3001
```

**Backend Won't Start?**
```bash
# Check PostgreSQL
netstat -an | findstr ":5432"
```

**File Upload Issues?**
- Start MinIO or use app without file uploads

**Need Help?**
- Check `TROUBLESHOOTING.md` for detailed solutions
- View logs in the terminal windows

## 🎉 Success!

When working correctly you should see:
- Frontend: "Ready in X.Xs" 
- Backend: "Started CsmsApplication"
- Login page accessible at http://localhost:3000

Happy coding! 🚀