# File Upload Implementation - Setup Guide

## ✅ What's Been Implemented

### Backend (Spring Boot)
1. **MinIO Configuration**
   - `MinioConfig.java` - MinIO client setup
   - `application.properties` - MinIO connection settings
   - Dependencies added to `pom.xml`

2. **File Storage Service**
   - `FileStorageService.java` - Complete file operations
   - Validation: 2MB max, PDF/Word/Excel/Images only
   - Automatic bucket creation
   - Unique object key generation

3. **REST API Endpoints**
   - `POST /api/files/upload` - Upload files
   - `GET /api/files/download/{objectKey}` - Download files
   - `GET /api/files/preview/{objectKey}` - Get preview URLs
   - `DELETE /api/files/{objectKey}` - Delete files
   - All secured with JWT authentication

### Frontend (Next.js)
1. **UI Components**
   - `FileUpload.tsx` - Full-featured upload component
   - `FilePreviewModal.tsx` - File preview modal
   - `Progress.tsx` - Upload progress bar

2. **API Proxy Routes**
   - `/api/files/upload` - Proxy to backend
   - `/api/files/preview/[...objectKey]` - Preview proxy
   - `/api/files/download/[...objectKey]` - Download proxy

3. **Features**
   - Drag & drop upload
   - File validation
   - Progress tracking
   - Preview support for images/PDFs
   - Multiple file upload
   - Error handling

## 🚀 Quick Setup

### 1. Install MinIO

**Option A: Docker (Recommended)**
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

### 2. Access MinIO
- Console: http://localhost:9001 (minioadmin/minioadmin)
- API: http://localhost:9000

### 3. Start Services
```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend  
cd frontend && npm run dev
```

## 💡 Usage Example

```tsx
import { FileUpload } from '@/components/ui/file-upload';
import { FilePreviewModal } from '@/components/ui/file-preview-modal';

function MyForm() {
  const [documents, setDocuments] = useState<string[]>([]);
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewKey, setPreviewKey] = useState<string | null>(null);

  return (
    <>
      <FileUpload
        label="Upload Documents"
        description="Select PDF, Word, or Excel files"
        folder="evaluation-forms"
        multiple
        value={documents}
        onChange={setDocuments}
        onPreview={(key) => {
          setPreviewKey(key);
          setPreviewOpen(true);
        }}
        required
      />
      
      <FilePreviewModal
        open={previewOpen}
        onOpenChange={setPreviewOpen}
        objectKey={previewKey}
      />
    </>
  );
}
```

## 🔧 Configuration

### Backend Settings (`application.properties`)
```properties
# MinIO Configuration
minio.url=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket-name=csms-files
minio.secure=false

# File Upload Limits
spring.servlet.multipart.max-file-size=2MB
spring.servlet.multipart.max-request-size=10MB
```

### Frontend Integration
The `FileUpload` component automatically:
- Gets authentication token from Zustand store
- Validates file types and sizes
- Shows upload progress
- Handles errors gracefully
- Provides preview and download options

## 📁 File Organization
Files are stored in MinIO with this structure:
```
csms-files/
├── documents/           # General documents
├── evaluation-forms/    # Employee evaluations
├── certificates/        # Educational certificates
├── contracts/          # Employment contracts
├── complaints/         # Complaint attachments
└── reports/            # Generated reports
```

## 🔐 Security Features
- JWT authentication required for all operations
- File type validation (PDF, Word, Excel, Images only)
- File size limits (2MB maximum)
- Role-based access control
- Unique object keys prevent conflicts

## 🐛 Troubleshooting

### Common Issues

1. **"Unauthorized" errors**
   - Ensure user is logged in
   - Check if JWT token is valid
   - Verify backend Spring Security configuration

2. **MinIO connection failed**
   - Check if MinIO is running on port 9000
   - Verify credentials in application.properties
   - Ensure bucket exists (auto-created on first use)

3. **File upload fails**
   - Check file size (max 2MB)
   - Verify file type is allowed
   - Check network connectivity to backend

4. **Preview not working**
   - Ensure file exists in MinIO
   - Check MinIO console for file presence
   - Verify presigned URL generation

### Debug Steps

1. **Check MinIO Status**
   ```bash
   curl http://localhost:9000/minio/health/live
   ```

2. **Test Backend Endpoints**
   ```bash
   # Upload test (with auth token)
   curl -X POST http://localhost:8080/api/files/upload \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -F "file=@test.pdf" \
     -F "folder=test"
   ```

3. **Check Browser Network Tab**
   - Verify API calls are reaching endpoints
   - Check for CORS issues
   - Inspect response payloads

## 🔄 Integration with Existing Forms

To add file upload to existing request forms:

1. **Import the component**
   ```tsx
   import { FileUpload } from '@/components/ui/file-upload';
   ```

2. **Add to form state**
   ```tsx
   const [documentKeys, setDocumentKeys] = useState<string[]>([]);
   ```

3. **Add to form**
   ```tsx
   <FileUpload
     label="Required Documents"
     folder="confirmation-requests"
     multiple
     value={documentKeys}
     onChange={setDocumentKeys}
     required
   />
   ```

4. **Include in submission**
   ```tsx
   const formData = {
     // ... other fields
     documents: documentKeys
   };
   ```

## 📈 Production Considerations

1. **MinIO Production Setup**
   - Change default credentials
   - Enable HTTPS
   - Set up backup strategy
   - Configure high availability

2. **File Storage Optimization**
   - Implement file cleanup for rejected requests
   - Add file compression for large documents
   - Set up automated backups

3. **Security Enhancements**
   - Add virus scanning
   - Implement file encryption at rest
   - Add audit logging for file operations

The file upload system is now fully integrated and ready to use throughout the CSMS application!