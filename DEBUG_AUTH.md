# Debug Authentication Issue

## Steps to Debug

1. **Clear Browser Storage**:
   - Open DevTools (F12)
   - Go to Application tab
   - Clear all storage for localhost:9002
   - Refresh page

2. **Login and Check Console**:
   - Login with your credentials
   - Check console for these messages:
     - "Login response token: present/missing"
     - "Auth state after setting" with token info
     - "FileUpload - Token found: YES/NO"

3. **Check localStorage**:
   - In DevTools > Application > Local Storage
   - Look for 'auth-store' key
   - Check if it contains accessToken

4. **Backend Role Check**:
   - What role are you logging in with?
   - HRO should have file upload permissions

## Expected Console Output on Login:
```
Login response token: present
Token preview: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM...
Auth state after setting: {
  userId: "some-id",
  role: "HRO", 
  isAuthenticated: true,
  hasAccessToken: true,
  tokenPreview: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM..."
}
```

## Expected Console Output on File Upload:
```
FileUpload - Token found: YES
FileUpload - User role: HRO
FileUpload - Sending Authorization header with token
```

## If Token is Missing:
- Backend might not be returning JWT tokens
- Check backend logs for authentication response
- Verify backend AuthResponse includes token field