/**
 * Backend Configuration
 * Maps frontend routes to Spring Boot backend endpoints
 */

export const BACKEND_CONFIG = {
  baseUrl: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api',
  
  // Authentication endpoints
  auth: {
    login: '/auth/login',
    logout: '/auth/logout',
    refresh: '/auth/refresh',
    employeeLogin: '/auth/employee-login', // May need to be implemented in backend
  },

  // Employee endpoints
  employees: {
    list: '/employees',
    create: '/employees',
    update: (id: string) => `/employees/${id}`,
    delete: (id: string) => `/employees/${id}`,
    search: '/employees/search',
    urgentActions: '/employees/urgent-actions', // To be implemented in backend
  },

  // Request endpoints
  requests: {
    confirmations: {
      list: '/confirmation-requests',
      create: '/confirmation-requests',
      update: (id: string) => `/confirmation-requests/${id}`,
      get: (id: string) => `/confirmation-requests/${id}`,
    },
    promotions: {
      list: '/promotion-requests',
      create: '/promotion-requests',
      update: (id: string) => `/promotion-requests/${id}`,
      get: (id: string) => `/promotion-requests/${id}`,
    },
    lwop: {
      list: '/lwop-requests',
      create: '/lwop-requests',
      update: (id: string) => `/lwop-requests/${id}`,
      get: (id: string) => `/lwop-requests/${id}`,
    },
    cadreChange: {
      list: '/cadre-change-requests',
      create: '/cadre-change-requests',
      update: (id: string) => `/cadre-change-requests/${id}`,
      get: (id: string) => `/cadre-change-requests/${id}`,
    },
    retirement: {
      list: '/retirement-requests',
      create: '/retirement-requests',
      update: (id: string) => `/retirement-requests/${id}`,
      get: (id: string) => `/retirement-requests/${id}`,
    },
    resignation: {
      list: '/resignation-requests',
      create: '/resignation-requests',
      update: (id: string) => `/resignation-requests/${id}`,
      get: (id: string) => `/resignation-requests/${id}`,
    },
    serviceExtension: {
      list: '/service-extension-requests',
      create: '/service-extension-requests',
      update: (id: string) => `/service-extension-requests/${id}`,
      get: (id: string) => `/service-extension-requests/${id}`,
    },
    termination: {
      list: '/termination-requests',
      create: '/termination-requests',
      update: (id: string) => `/termination-requests/${id}`,
      get: (id: string) => `/termination-requests/${id}`,
    },
    dismissal: {
      list: '/dismissal-requests',
      create: '/dismissal-requests',
      update: (id: string) => `/dismissal-requests/${id}`,
      get: (id: string) => `/dismissal-requests/${id}`,
    },
  },

  // Complaint endpoints
  complaints: {
    list: '/complaints',
    create: '/complaints',
    update: (id: string) => `/complaints/${id}`,
    get: (id: string) => `/complaints/${id}`,
  },

  // Institution endpoints
  institutions: {
    list: '/institutions',
    create: '/institutions',
    update: (id: string) => `/institutions/${id}`,
    delete: (id: string) => `/institutions/${id}`,
    get: (id: string) => `/institutions/${id}`,
  },

  // User endpoints
  users: {
    list: '/users',
    create: '/users',
    update: (id: string) => `/users/${id}`,
    delete: (id: string) => `/users/${id}`,
    get: (id: string) => `/users/${id}`,
  },

  // Dashboard endpoints
  dashboard: {
    summary: '/dashboard/statistics',
    recentActivities: '/dashboard/recent-activities', // May need to be implemented
  },

  // Reports endpoints
  reports: {
    generate: '/reports/generate',
    export: '/reports/export',
  },

  // Notification endpoints
  notifications: {
    list: '/notifications',
    markRead: (id: string) => `/notifications/${id}/read`,
  },

  // File upload endpoints
  files: {
    upload: '/files/upload',
  },

  // Track requests
  track: {
    request: '/requests/track', // May need to be implemented
  },
};

/**
 * Build full URL for an endpoint
 */
export function buildUrl(endpoint: string): string {
  return `${BACKEND_CONFIG.baseUrl}${endpoint}`;
}

/**
 * Migration status of endpoints
 * Track which endpoints have been migrated and which need backend implementation
 */
export const MIGRATION_STATUS = {
  // Implemented in Spring Boot backend
  implemented: [
    'auth.login',
    'auth.logout', 
    'auth.refresh',
    'employees.list',
    'employees.create',
    'employees.update',
    'employees.delete',
    'requests.confirmations.list',
    'requests.confirmations.create',
    'requests.confirmations.update',
    'requests.promotions.list',
    'requests.promotions.create',
    'requests.promotions.update',
    'complaints.list',
    'complaints.create',
    'complaints.update',
    'institutions.list',
    'institutions.create',
    'institutions.update',
    'users.list',
    'users.create',
    'users.update',
    'dashboard.summary',
  ],

  // Need to be implemented in Spring Boot backend
  needsImplementation: [
    'auth.employeeLogin',
    'employees.search',
    'employees.urgentActions',
    'dashboard.recentActivities',
    'track.request',
    'notifications.list',
    'notifications.markRead',
    'files.upload',
  ],

  // Complex endpoints that may need special handling
  complex: [
    'reports.generate',
    'reports.export',
  ],
};

export default BACKEND_CONFIG;