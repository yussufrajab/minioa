# File Upload Fix - Complete Solution

## Issues Identified and Fixed

### 1. JWT Authentication Issue
**Problem**: Frontend wasn't storing/sending JWT tokens
**Solution**: 
- Fixed auth store to extract and save JWT tokens from login response
- Updated API client to include Authorization header in requests
- Modified file upload component to send Bearer token

### 2. Role Permission Issue  
**Problem**: 403 Forbidden due to role permission mismatch
**Solution**:
- Backend User entity returns authorities as "ROLE_" + role (e.g., "ROLE_HRO")
- File controller was checking for just role names without prefix
- **Fixed**: Changed `@PreAuthorize("hasAnyAuthority(...)")` to `@PreAuthorize("hasAnyRole(...)")` in FileController.java

## Files Modified

### Backend
- `FileController.java` - Fixed all permission annotations to use `hasAnyRole()` instead of `hasAnyAuthority()`

### Frontend
- `auth-store.ts` - Extract and store JWT tokens from login
- `api-client.ts` - Send Authorization header with requests  
- `file-upload.tsx` - Include Bearer token in upload requests
- `cadre-change/page.tsx` - Updated file viewing with auth headers
- API routes: `upload/route.ts`, `download/route.ts`, `preview/route.ts`

## Current Status
✅ Backend restarted with permission fixes
✅ JWT authentication properly implemented
✅ File upload endpoints accessible
✅ Role permissions corrected

## Testing Steps
1. **Login Fresh**: Clear browser cache and login again to get new JWT token
2. **Test Upload**: Navigate to Change of Cadre and try uploading a PDF file
3. **Verify Storage**: Check MinIO console at http://localhost:9001 for uploaded files
4. **Test Viewing**: Login as HHRMD and verify you can view/download files

## Expected Behavior
- File uploads should now work without 401/403 errors
- Files are stored in MinIO under `cadre-change/` folder
- HHRMD can view and download uploaded documents
- JWT tokens are automatically included in all requests

## Troubleshooting
If issues persist:
1. Check browser console for JWT token presence in localStorage
2. Verify Authorization header in Network tab
3. Check backend logs for permission errors
4. Ensure MinIO is running on port 9000