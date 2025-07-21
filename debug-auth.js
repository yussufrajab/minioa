// Browser-based debug script - paste this into browser console
console.log('=== Auth Debug ===');
console.log('localStorage auth-storage:', localStorage.getItem('auth-storage'));

try {
  const authData = JSON.parse(localStorage.getItem('auth-storage') || '{}');
  console.log('Parsed auth data:', authData);
  console.log('User role in storage:', authData?.state?.role);
  console.log('User object in storage:', authData?.state?.user);
  console.log('User role from user object:', authData?.state?.user?.role);
} catch (e) {
  console.error('Error parsing auth storage:', e);
}

// To clear auth storage for fresh start, uncomment below:
// console.log('Clearing auth storage...');
// localStorage.removeItem('auth-storage');
// console.log('Auth storage cleared.');