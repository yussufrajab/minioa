# Role Permission Fix

## Issue Found
The file upload was returning 403 (Forbidden) because:

1. **Backend User Entity** (`User.java` line 93):
   ```java
   public Collection<? extends GrantedAuthority> getAuthorities() {
       return List.of(new SimpleGrantedAuthority("ROLE_" + role));
   }
   ```
   - This adds "ROLE_" prefix to roles (e.g., "ROLE_HRO")

2. **File Controller** was using:
   ```java
   @PreAuthorize("hasAnyAuthority('ADMIN', 'HRO', 'HHRMD', 'HRMO', 'DO', 'EMPLOYEE')")
   ```
   - This checks for exact authority names without "ROLE_" prefix

## Fix Applied
Changed all `@PreAuthorize` annotations in `FileController.java` from:
- `hasAnyAuthority('ADMIN', 'HRO', ...)` 
- To: `hasAnyRole('ADMIN', 'HRO', ...)`

The `hasAnyRole()` method automatically adds the "ROLE_" prefix when checking permissions.

## Files Modified
- `backend/src/main/java/com/zanzibar/csms/controller/FileController.java`
  - Upload endpoint (line 33)
  - Download endpoint (line 70) 
  - Preview endpoint (line 105)
  - Delete endpoint (line 141)
  - Exists endpoint (line 170)

## Next Steps
1. Restart the backend server for changes to take effect
2. Test file upload in Change of Cadre module
3. Verify that HRO users can now upload files successfully

## Backend Restart Command
From `/backend` directory:
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```