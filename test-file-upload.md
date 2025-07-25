# File Upload Test Guide

## Testing File Upload in Change of Cadre Module

### Prerequisites
1. Ensure all services are running:
   - PostgreSQL (port 5432)
   - MinIO (port 9000, console at 9001)
   - Backend (port 8080)
   - Frontend (port 9002)

### Test Steps

1. **Login as HRO**
   - Navigate to http://localhost:9002
   - Login with HRO credentials

2. **Navigate to Change of Cadre**
   - Click on "Change of Cadre" in the sidebar

3. **Submit a Request with Files**
   - Enter a valid ZanID (e.g., from the employee list)
   - Fill in the new cadre and reason
   - Upload the required PDF files:
     - Letter of Request (Required)
     - Certificate (Optional)
     - TCU Form (Required if "studied outside country" is checked)

4. **Verify Upload**
   - The files should upload successfully
   - You should see the uploaded files listed below the upload area
   - Click "View" or "Download" to test file retrieval

5. **Test as HHRMD**
   - Logout and login as HHRMD
   - Navigate to Change of Cadre
   - Find the submitted request
   - Click "View Details"
   - In the modal, you should see the attached documents
   - Click "View" or "Download" to access the files

### Troubleshooting

If file upload fails:

1. **Check MinIO is running**:
   ```bash
   curl http://localhost:9000
   ```

2. **Check MinIO Console**:
   - Open http://localhost:9001
   - Login with minioadmin/minioadmin
   - Check if the "csms-files" bucket exists
   - Check if files are being uploaded to the bucket

3. **Check Backend Logs**:
   - Look for file upload errors in the backend console
   - Check if MinIO connection is established

4. **Check Frontend Console**:
   - Open browser developer tools
   - Look for any errors in the console
   - Check Network tab for failed requests

### MinIO Bucket Structure
Files are organized in folders:
- `cadre-change/` - Files for cadre change requests
- `documents/` - Default folder for other documents

Each file is stored with a timestamp and unique ID:
- Example: `cadre-change/20250724_070000_a1b2c3d4.pdf`