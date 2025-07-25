# JWT Authentication Fix Summary

## Issue
The file upload was failing with 401 Unauthorized because:
1. Backend uses JWT authentication
2. Frontend was not properly storing JWT tokens from login response
3. File upload was not sending the JWT token in Authorization header

## Changes Made

### 1. Auth Store (`frontend/src/store/auth-store.ts`)
- Updated to extract `token` and `refreshToken` from login response
- Now properly stores tokens in auth state
- Sets token in API client after successful login

### 2. API Client (`frontend/src/lib/api-client.ts`)
- Re-enabled JWT authentication by adding Authorization header
- Uses stored token for all API requests

### 3. File Upload Component (`frontend/src/components/ui/file-upload.tsx`)
- Retrieves JWT token from auth store
- Sends Authorization header with file upload requests

### 4. API Routes
- `/api/files/upload/route.ts` - Now forwards Authorization header to backend
- `/api/files/download/[...objectKey]/route.ts` - Forwards auth header
- `/api/files/preview/[...objectKey]/route.ts` - Forwards auth header

### 5. Cadre Change Page
- Updated View/Download buttons to include auth headers when accessing files

## Testing Instructions

1. Clear browser storage/cache to ensure fresh login
2. Login with valid credentials
3. Check browser console for:
   - "Login response token: present"
   - "Login response refreshToken: present"
4. Navigate to Change of Cadre module
5. Try uploading a PDF file - it should now work
6. After submission, test viewing/downloading files as HHRMD

## Backend JWT Configuration
- Access token expires in 10 minutes (600000ms)
- Refresh token expires in 24 hours (86400000ms)
- All file endpoints require authentication except `/api/auth/*`

## Troubleshooting
If still getting 401 errors:
1. Check if JWT token is present in localStorage: `auth-store` key
2. Verify token is being sent in requests (Network tab > Headers)
3. Check backend logs for JWT validation errors
4. Ensure MinIO service is running on port 9000