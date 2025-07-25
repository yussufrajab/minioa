# File Upload/Download Implementation in LWOP Module

## Summary
Successfully integrated MinIO-based file upload/download functionality into the LWOP (Leave Without Pay) module.

## What Was Implemented

### 1. File Upload Component Integration
- Integrated the existing `FileUpload` component into LWOP form
- Component uses MinIO backend for storage (accessible at http://localhost:9001/browser/csms-files)
- Files are organized in folders: `lwop/letters` and `lwop/consents`

### 2. LWOP Form Updates
- Replaced basic file inputs with `FileUpload` components
- Added support for:
  - Letter of Request upload
  - Employee Consent Letter upload
  - File validation (PDF only, max 2MB)
  - Upload progress tracking
  - File preview and download capabilities

### 3. File Management Features
- **Upload**: Drag & drop or click to upload files
- **Preview**: Click eye icon to preview PDF documents in modal
- **Download**: Click download icon to download files
- **Delete**: Click X to remove uploaded files before submission

### 4. Document Viewing in Request Details
- Updated request details modal to show uploaded documents
- Each document displays:
  - Document type (Letter of Request / Employee Consent Letter)
  - Filename
  - View button (opens preview modal)
  - Download button (downloads file directly)

### 5. File Resubmission
- When correcting rejected requests, users can upload new documents
- Previous documents are replaced with new uploads

## Technical Details

### Frontend Changes
- Modified `frontend/src/app/dashboard/lwop/page.tsx`:
  - Imported `FileUpload` and `FilePreviewModal` components
  - Changed from FileList to string keys for file storage
  - Added preview modal state management
  - Integrated file operations with MinIO backend

### API Routes Used
- `/api/files/upload` - Handles file uploads to MinIO
- `/api/files/preview/[objectKey]` - Provides file preview URLs
- `/api/files/download/[objectKey]` - Handles file downloads

### File Storage Structure
```
csms-files/
├── lwop/
│   ├── letters/
│   │   └── [timestamp]_[filename].pdf
│   └── consents/
│       └── [timestamp]_[filename].pdf
```

## Testing the Implementation

1. **Login as HRO** (HR Officer)
2. **Navigate to LWOP module** at `/dashboard/lwop`
3. **Create new LWOP request**:
   - Enter employee ZanID
   - Fill in dates and reason
   - Upload Letter of Request (PDF)
   - Upload Employee Consent Letter (PDF)
   - Submit request

4. **Review uploaded documents**:
   - Click "View Details" on any request
   - See attached documents section
   - Click "View" to preview PDF in modal
   - Click "Download" to download file

5. **Test file operations**:
   - Try uploading non-PDF files (should be rejected)
   - Upload files larger than 2MB (should be rejected)
   - Remove and re-upload files before submission
   - Preview uploaded PDFs

## MinIO Access
- **MinIO Console**: http://localhost:9001/browser/csms-files
- **Bucket**: csms-files
- **Access via console to see all uploaded files organized by module**

## Security
- All file operations require JWT authentication
- Files are accessed through authenticated API endpoints
- Direct MinIO access is restricted to backend only

## Next Steps
To implement file upload in other modules:
1. Use the same `FileUpload` component
2. Specify appropriate folder structure (e.g., `promotion/certificates`)
3. Update the module's form and detail views similarly