# MinIO Setup Guide for CSMS

This guide will help you set up MinIO object storage for the CSMS project.

## What is MinIO?

MinIO is a high-performance, S3-compatible object storage system. It's used in CSMS to store and manage document files securely.

## Installation Options

### Option 1: Docker (Recommended)

```bash
# Run MinIO using Docker
docker run -d \
  --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  -v C:/minio/data:/data \
  quay.io/minio/minio server /data --console-address ":9001"
```

### Option 2: Windows Binary

1. Download MinIO for Windows from: https://min.io/download
2. Create a folder: `C:\minio`
3. Place the `minio.exe` file in this folder
4. Open Command Prompt as Administrator
5. Navigate to `C:\minio`
6. Run: `minio.exe server C:\minio\data --console-address :9001`

### Option 3: Using Chocolatey

```powershell
# Install Chocolatey first if not installed
# Then install MinIO
choco install minio

# Start MinIO
minio server C:\minio\data --console-address :9001
```

## Configuration

### Environment Variables

The CSMS backend is configured with these MinIO settings:

```properties
minio.url=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket-name=csms-files
minio.secure=false
```

### Access URLs

- **MinIO Console**: http://localhost:9001 (Web UI)
- **MinIO API**: http://localhost:9000 (API endpoint)

### Default Credentials

- **Username**: minioadmin
- **Password**: minioadmin

## First Time Setup

1. Start MinIO using one of the methods above
2. Open the MinIO Console: http://localhost:9001
3. Login with username: `minioadmin`, password: `minioadmin`
4. The CSMS application will automatically create the `csms-files` bucket on first startup

## Testing the Setup

### 1. Check MinIO Status

Visit http://localhost:9001 and login to ensure MinIO is running.

### 2. Test File Upload via CSMS

1. Start the CSMS backend: `cd backend && mvn spring-boot:run`
2. Start the CSMS frontend: `cd frontend && npm run dev`
3. Login to CSMS
4. Navigate to any form with file upload (e.g., Employee Confirmation)
5. Try uploading a document

### 3. Verify Files in MinIO Console

1. Go to http://localhost:9001
2. Navigate to the `csms-files` bucket
3. You should see uploaded files organized by folders

## File Storage Structure

CSMS organizes files in MinIO using this structure:

```
csms-files/
├── documents/           # General documents
├── evaluation-forms/    # Employee evaluation forms
├── certificates/        # Educational certificates
├── contracts/          # Employment contracts
├── appointment-letters/ # Appointment letters
├── complaints/         # Complaint-related documents
└── reports/            # Generated reports
```

## Security Considerations

### Production Setup

For production, you should:

1. **Change default credentials**:
   ```bash
   # Set strong credentials
   export MINIO_ROOT_USER=your-secure-username
   export MINIO_ROOT_PASSWORD=your-secure-password
   ```

2. **Enable HTTPS**:
   ```bash
   # Generate SSL certificates
   minio server /data --certs-dir /etc/minio/certs
   ```

3. **Configure firewall rules** to restrict access to MinIO ports

4. **Set up backup strategy** for the MinIO data directory

### Access Policies

MinIO supports fine-grained access policies. You can create policies for different user types:

- **HRO**: Upload and view documents for their institution
- **HHRMD/HRMO**: View all documents
- **Employees**: View only their own documents

## Backup and Recovery

### Data Location

- **Docker**: Data is stored in the mounted volume (e.g., `C:/minio/data`)
- **Binary**: Data is stored in the specified directory

### Backup Strategy

1. **Regular backups** of the data directory
2. **Database backup** to maintain file metadata consistency
3. **Test restore procedures** regularly

### Recovery Process

1. Stop MinIO service
2. Restore data directory from backup
3. Start MinIO service
4. Verify file accessibility through CSMS

## Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 9000 and 9001 are available
2. **Permission issues**: Run with appropriate user permissions
3. **Disk space**: Ensure adequate disk space for file storage
4. **Network connectivity**: Check firewall and network settings

### Logs

MinIO logs provide detailed information about operations:

```bash
# View logs in Docker
docker logs minio

# Increase log verbosity
minio server /data --log-level debug
```

### Health Check

Test MinIO connectivity:

```bash
# Using curl
curl http://localhost:9000/minio/health/live

# Should return 200 OK if healthy
```

## Development vs Production

### Development Settings

```properties
# Local development
minio.url=http://localhost:9000
minio.secure=false
```

### Production Settings

```properties
# Production setup
minio.url=https://minio.your-domain.com
minio.secure=true
minio.access-key=${MINIO_ACCESS_KEY}
minio.secret-key=${MINIO_SECRET_KEY}
```

## Integration with CSMS

The CSMS application includes:

1. **FileStorageService**: Handles MinIO operations
2. **FileController**: REST endpoints for file operations
3. **FileUpload Component**: React component for file uploads
4. **File validation**: Size and type restrictions
5. **Preview functionality**: For supported file types

## Monitoring

### Performance Metrics

Monitor these metrics:

- **Storage usage**: Track bucket size and file count
- **Request latency**: Monitor API response times
- **Error rates**: Track failed operations
- **Bandwidth usage**: Monitor upload/download traffic

### Health Monitoring

Implement health checks for:

- MinIO service availability
- Bucket accessibility
- Storage capacity
- Network connectivity

## Support

For issues specific to MinIO:
- MinIO Documentation: https://docs.min.io/
- MinIO Community: https://slack.min.io/

For CSMS-specific file storage issues:
- Check the application logs
- Verify MinIO configuration in application.properties
- Test file operations through the REST API endpoints