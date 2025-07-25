# Fix for File Upload Authentication Issue

## Problem
File uploads were failing with 403 Forbidden error because the access token was not being persisted in localStorage.

## Solution Applied
Updated the auth store (`frontend/src/store/auth-store.ts`) to:
1. Include `accessToken` and `refreshToken` in the persisted state
2. Restore the access token to the API client after page reload

## Changes Made
1. Modified `partialize` function to include tokens in persisted state
2. Added token restoration in `onRehydrateStorage` callback
3. Set token in API client after rehydration

## To Apply the Fix
Since the auth store persistence has changed, you need to:

1. **Log out** of the current session
2. **Log in** again with your credentials
3. The tokens will now be properly persisted

## Testing
After logging in again:
1. Navigate to LWOP module
2. Try uploading a PDF file
3. The upload should now work without authentication errors
4. Refresh the page and try uploading again - it should still work

## Technical Details
- Tokens are now stored in localStorage along with user data
- On page reload, tokens are restored from localStorage
- The API client is automatically configured with the restored token
- All authenticated API calls (including file uploads) will now work properly