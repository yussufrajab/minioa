# Civil Service Management System (CSMS) - Frontend Development Guide

## Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [Authentication & Security](#authentication--security)
4. [API Integration](#api-integration)
5. [Data Models & DTOs](#data-models--dtos)
6. [User Roles & Permissions](#user-roles--permissions)
7. [Frontend Architecture Recommendations](#frontend-architecture-recommendations)
8. [UI/UX Guidelines](#uiux-guidelines)
9. [Workflow Implementation](#workflow-implementation)
10. [Forms & Validation](#forms--validation)
11. [File Upload & Document Management](#file-upload--document-management)
12. [Reports & Analytics](#reports--analytics)
13. [Error Handling](#error-handling)
14. [Performance Considerations](#performance-considerations)
15. [Testing Strategy](#testing-strategy)
16. [Deployment & Configuration](#deployment--configuration)

## Project Overview

The Civil Service Management System (CSMS) is a comprehensive Spring Boot 3 application designed to manage HR processes for the Civil Service Commission of Zanzibar. The system oversees employees across all government ministries and includes modules for Employee Profiles, Employment Confirmation, Leave Without Pay, Termination and Dismissal, Complaints, Promotions, Change of Cadre, Service Extensions, Retirement, Resignations, Audit Trail, and Reports.

### Tech Stack
- **Frontend**: Next.js 15 (Target)
- **Backend**: Spring Boot 3 with RESTful APIs
- **Database**: PostgreSQL 15
- **Authentication**: JWT-based stateless authentication
- **UI Framework**: Tailwind CSS 3.4.1, Radix UI components, Lucide React 0.475.0

### Key Features
- Multi-role user management (9 distinct user roles)
- Complex workflow management for HR requests
- Document upload and management
- Comprehensive reporting and analytics
- Audit trail and compliance features
- Role-based access control
- Institution-based data isolation

## System Architecture

### Backend Architecture Overview
The Spring Boot application follows a layered architecture:

```
com.zanzibar.csms/
├── config/                 # Configuration classes
├── controller/            # REST API controllers (18 controllers)
├── dto/                   # Data Transfer Objects
├── entity/                # JPA entities with inheritance
├── exception/             # Custom exceptions and handlers
├── mapper/                # Entity-DTO mappers
├── repository/            # JPA repositories
├── security/              # JWT security components
├── service/               # Business logic services
├── util/                  # Utility classes
└── validation/            # Custom validators
```

### Database Design
- **Base Entity Pattern**: All entities extend `BaseEntity` with audit fields
- **Inheritance Strategy**: Request entities use `@Inheritance(strategy = InheritanceType.JOINED)`
- **Soft Delete**: Uses `isActive` flag for logical deletion
- **Optimistic Locking**: Version field for concurrency control
- **UUID Primary Keys**: All entities use UUID string IDs

## Authentication & Security

### JWT Authentication Flow

The system implements JWT-based stateless authentication with the following characteristics:

- **Access Token Expiration**: 10 minutes (600,000ms)
- **Refresh Token Expiration**: 24 hours (86,400,000ms)
- **Algorithm**: HMAC SHA-512 (HS512)
- **Password Encoding**: BCrypt

### Frontend Authentication Implementation

#### 1. Login Process

```javascript
// Login API call
const login = async (credentials) => {
  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: credentials.username,
        password: credentials.password
      })
    });

    if (response.ok) {
      const authData = await response.json();
      
      // Store tokens securely
      localStorage.setItem('accessToken', authData.token);
      localStorage.setItem('refreshToken', authData.refreshToken);
      localStorage.setItem('user', JSON.stringify(authData.user));
      
      return authData;
    } else {
      throw new Error('Login failed');
    }
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
};
```

#### 2. Token Refresh Implementation

```javascript
// Automatic token refresh using Axios interceptor
import axios from 'axios';

// Set up axios defaults
axios.defaults.baseURL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// Request interceptor to add auth token
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for token refresh
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        const response = await axios.post('/auth/refresh', refreshToken, {
          headers: { 'Content-Type': 'text/plain' }
        });

        const { token, refreshToken: newRefreshToken } = response.data;

        localStorage.setItem('accessToken', token);
        localStorage.setItem('refreshToken', newRefreshToken);

        // Retry original request with new token
        originalRequest.headers.Authorization = `Bearer ${token}`;
        return axios(originalRequest);

      } catch (refreshError) {
        // Refresh failed - redirect to login
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);
```

#### 3. Route Protection

```javascript
// Protected Route Component
import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';

const ProtectedRoute = ({ children, allowedRoles = [] }) => {
  const router = useRouter();
  const [isAuthorized, setIsAuthorized] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const checkAuth = () => {
      const token = localStorage.getItem('accessToken');
      const userStr = localStorage.getItem('user');

      if (!token || !userStr) {
        router.replace('/login');
        return;
      }

      try {
        const user = JSON.parse(userStr);
        
        if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
          router.replace('/unauthorized');
          return;
        }

        setIsAuthorized(true);
      } catch (error) {
        console.error('Auth check failed:', error);
        router.replace('/login');
      } finally {
        setIsLoading(false);
      }
    };

    checkAuth();
  }, [router, allowedRoles]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return isAuthorized ? children : null;
};

export default ProtectedRoute;
```

## API Integration

### Base API Configuration

```javascript
// utils/api.js
import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to all requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

### Key API Endpoints

#### Employee Management
```javascript
// Employee API service
export const employeeService = {
  // Get all employees with pagination
  getEmployees: (page = 0, size = 20, search = '') => 
    api.get(`/employees?page=${page}&size=${size}&search=${search}`),
  
  // Get employee by ID
  getEmployee: (id) => api.get(`/employees/${id}`),
  
  // Create new employee
  createEmployee: (employeeData) => api.post('/employees', employeeData),
  
  // Update employee
  updateEmployee: (id, employeeData) => api.put(`/employees/${id}`, employeeData),
  
  // Delete employee (soft delete)
  deleteEmployee: (id) => api.delete(`/employees/${id}`),
  
  // Search employees
  searchEmployees: (query) => api.get(`/employees/search?q=${query}`),
  
  // Get employees by institution
  getEmployeesByInstitution: (institutionId) => 
    api.get(`/employees/institution/${institutionId}`)
};
```

#### Request Management
```javascript
// Request API service
export const requestService = {
  // Promotion Requests
  createPromotionRequest: (data) => api.post('/promotion-requests', data),
  getPromotionRequests: (page = 0, size = 20) => 
    api.get(`/promotion-requests?page=${page}&size=${size}`),
  getPromotionRequest: (id) => api.get(`/promotion-requests/${id}`),
  updatePromotionRequest: (id, data) => api.put(`/promotion-requests/${id}`, data),
  approvePromotionRequest: (id, data) => api.post(`/promotion-requests/${id}/approve`, data),
  rejectPromotionRequest: (id, data) => api.post(`/promotion-requests/${id}/reject`, data),

  // Confirmation Requests
  createConfirmationRequest: (data) => api.post('/confirmation-requests', data),
  getConfirmationRequests: (page = 0, size = 20) => 
    api.get(`/confirmation-requests?page=${page}&size=${size}`),

  // Leave Without Pay
  createLWOPRequest: (data) => api.post('/leave-without-pay', data),
  getLWOPRequests: (page = 0, size = 20) => 
    api.get(`/leave-without-pay?page=${page}&size=${size}`),

  // General request operations
  getRequestsByStatus: (status, page = 0, size = 20) => 
    api.get(`/requests/status/${status}?page=${page}&size=${size}`),
  getRequestsByEmployee: (employeeId, page = 0, size = 20) => 
    api.get(`/requests/employee/${employeeId}?page=${page}&size=${size}`),
  getMyRequests: (page = 0, size = 20) => 
    api.get(`/requests/my-requests?page=${page}&size=${size}`)
};
```

#### Complaint Management
```javascript
// Complaint API service
export const complaintService = {
  createComplaint: (data) => api.post('/complaints', data),
  getComplaints: (page = 0, size = 20) => 
    api.get(`/complaints?page=${page}&size=${size}`),
  getComplaint: (id) => api.get(`/complaints/${id}`),
  updateComplaint: (id, data) => api.put(`/complaints/${id}`, data),
  assignComplaint: (id, data) => api.post(`/complaints/${id}/assign`, data),
  resolveComplaint: (id, data) => api.post(`/complaints/${id}/resolve`, data)
};
```

#### Dashboard & Analytics
```javascript
// Dashboard API service
export const dashboardService = {
  getDashboardMetrics: () => api.get('/dashboard/metrics'),
  getPendingRequests: () => api.get('/dashboard/pending-requests'),
  getRecentActivities: () => api.get('/dashboard/recent-activities'),
  getRequestStatusDistribution: () => api.get('/dashboard/request-status-distribution'),
  getInstitutionMetrics: (institutionId) => 
    api.get(`/dashboard/institution/${institutionId}/metrics`)
};
```

### API Response Format

All API responses follow a consistent format:

```javascript
// Success Response
{
  "status": "success",
  "message": "Operation completed successfully",
  "data": {
    // Response payload
  }
}

// Error Response
{
  "status": "error",
  "message": "Validation failed",
  "error": {
    "field": "employeeId",
    "message": "Employee ID is required"
  }
}

// Pagination Response
{
  "status": "success",
  "message": "Data retrieved successfully",
  "data": {
    "content": [...], // Array of items
    "totalElements": 100,
    "totalPages": 10,
    "size": 10,
    "number": 0,
    "first": true,
    "last": false
  }
}
```

## Data Models & DTOs

### Core Entities

#### Employee Entity Structure
```javascript
const Employee = {
  id: 'string (UUID)',
  fullName: 'string (required)',
  profileImage: 'string (optional)',
  dateOfBirth: 'YYYY-MM-DD (required)',
  placeOfBirth: 'string (optional)',
  region: 'string (optional)',
  countryOfBirth: 'string (optional)',
  payrollNumber: 'string (required, unique)',
  zanzibarId: 'string (required, unique)',
  zssfNumber: 'string (required, unique)',
  rank: 'string (optional)',
  ministry: 'string (optional)',
  institutionId: 'string (UUID, required)',
  institutionName: 'string (read-only)',
  department: 'string (optional)',
  appointmentType: 'string (optional)',
  contractType: 'string (optional)',
  recentTitleDate: 'YYYY-MM-DD (optional)',
  currentReportingOffice: 'string (optional)',
  currentWorkplace: 'string (optional)',
  employmentDate: 'YYYY-MM-DD (required)',
  confirmationDate: 'YYYY-MM-DD (optional)',
  loanGuaranteeStatus: 'boolean (default: false)',
  employmentStatus: 'EmploymentStatus enum (required)',
  phoneNumber: 'string (optional)',
  gender: 'string (optional)',
  contactAddress: 'string (optional)',
  createdAt: 'YYYY-MM-DDTHH:mm:ss',
  updatedAt: 'YYYY-MM-DDTHH:mm:ss',
  isActive: 'boolean (default: true)'
};
```

#### Request Entity Structure
```javascript
const BaseRequest = {
  id: 'string (UUID)',
  requestNumber: 'string (auto-generated, unique)',
  employeeId: 'string (UUID, required)',
  employeeName: 'string (read-only)',
  submittedById: 'string (UUID, required)',
  submittedByName: 'string (read-only)',
  submissionDate: 'YYYY-MM-DDTHH:mm:ss (auto-generated)',
  status: 'RequestStatus enum (required)',
  requestType: 'RequestType enum (required)',
  approverId: 'string (UUID, optional)',
  approverName: 'string (read-only)',
  approvalDate: 'YYYY-MM-DDTHH:mm:ss (optional)',
  comments: 'string (optional)',
  rejectionReason: 'string (optional)',
  priority: 'Priority enum (default: NORMAL)',
  dueDate: 'YYYY-MM-DDTHH:mm:ss (optional)',
  description: 'string (optional)',
  currentStage: 'string (optional)',
  currentReviewerId: 'string (UUID, optional)',
  institutionId: 'string (UUID)',
  institutionName: 'string (read-only)',
  documents: 'RequestDocument[] (optional)',
  workflowSteps: 'RequestWorkflow[] (read-only)'
};
```

### Enum Values

#### User Roles
```javascript
const UserRole = {
  HRO: 'HR Officer',
  HHRMD: 'Head of HR Management',
  HRMO: 'Human Resource Management Officer',
  DO: 'Disciplinary Officer',
  EMP: 'Employee',
  PO: 'Planning Officer',
  CSCS: 'Civil Service Commission Secretary',
  HRRP: 'Human Resource Responsible Personnel',
  ADMIN: 'Administrator'
};
```

#### Request Status
```javascript
const RequestStatus = {
  DRAFT: 'Draft',
  SUBMITTED: 'Submitted',
  HRO_REVIEW: 'HRO Review',
  HRMO_REVIEW: 'HRMO Review',
  HHRMD_REVIEW: 'HHRMD Review',
  APPROVED: 'Approved',
  REJECTED: 'Rejected',
  CANCELLED: 'Cancelled',
  RETURNED: 'Returned for Revision'
};
```

#### Employment Status
```javascript
const EmploymentStatus = {
  ACTIVE: 'Active',
  CONFIRMED: 'Confirmed',
  UNCONFIRMED: 'Unconfirmed',
  ON_LEAVE: 'On Leave',
  RETIRED: 'Retired',
  RESIGNED: 'Resigned',
  TERMINATED: 'Terminated',
  DISMISSED: 'Dismissed'
};
```

## User Roles & Permissions

### Role-Based Access Control Matrix

| Feature | HRO | HHRMD | HRMO | DO | EMP | PO | CSCS | HRRP | ADMIN |
|---------|-----|-------|------|----|----|----| -----|------|-------|
| Employee Profile (Create/Edit) | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Employee Profile (View) | ✅* | ✅ | ✅ | ✅* | ✅** | ✅ | ✅ | ✅* | ✅ |
| Submit HR Requests | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Submit Complaints | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ✅ |
| Approve Confirmations | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Approve Promotions | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Approve LWOP | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Handle Complaints | ❌ | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Handle Terminations | ❌ | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ |
| View Reports | ❌ | ✅ | ✅ | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ |
| Dashboard Access | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

**Legend:**
- ✅ = Full access
- ❌ = No access
- ✅* = Institution-specific access only
- ✅** = Own profile only

### Frontend Role Implementation

```javascript
// Role-based component rendering
import { useAuth } from '../hooks/useAuth';

const ActionButtons = ({ request }) => {
  const { user } = useAuth();
  
  const canApprove = ['HHRMD', 'HRMO', 'ADMIN'].includes(user.role);
  const canEdit = user.role === 'HRO' && request.status === 'DRAFT';
  const isOwner = request.submittedById === user.id;

  return (
    <div className="flex gap-2">
      {canEdit && (
        <button className="btn btn-primary">Edit</button>
      )}
      {canApprove && request.status === 'SUBMITTED' && (
        <>
          <button className="btn btn-success">Approve</button>
          <button className="btn btn-danger">Reject</button>
        </>
      )}
      {isOwner && ['DRAFT', 'RETURNED'].includes(request.status) && (
        <button className="btn btn-warning">Submit</button>
      )}
    </div>
  );
};
```

## Frontend Architecture Recommendations

### Project Structure

```
src/
├── components/           # Reusable UI components
│   ├── ui/              # Basic UI components (Button, Input, etc.)
│   ├── forms/           # Form components
│   ├── layout/          # Layout components
│   └── modules/         # Feature-specific components
├── pages/               # Next.js pages
├── hooks/               # Custom React hooks
├── services/            # API service functions
├── utils/               # Utility functions
├── types/               # TypeScript type definitions
├── contexts/            # React contexts
├── constants/           # Application constants
└── styles/              # CSS/Tailwind styles
```

### State Management

```javascript
// Use React Context for global state
import { createContext, useContext, useReducer } from 'react';

const AppContext = createContext();

const initialState = {
  user: null,
  institutions: [],
  employees: [],
  requests: [],
  complaints: [],
  notifications: []
};

const appReducer = (state, action) => {
  switch (action.type) {
    case 'SET_USER':
      return { ...state, user: action.payload };
    case 'SET_EMPLOYEES':
      return { ...state, employees: action.payload };
    case 'ADD_REQUEST':
      return { ...state, requests: [...state.requests, action.payload] };
    case 'UPDATE_REQUEST':
      return {
        ...state,
        requests: state.requests.map(req =>
          req.id === action.payload.id ? action.payload : req
        )
      };
    default:
      return state;
  }
};

export const AppProvider = ({ children }) => {
  const [state, dispatch] = useReducer(appReducer, initialState);

  return (
    <AppContext.Provider value={{ state, dispatch }}>
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within AppProvider');
  }
  return context;
};
```

### Custom Hooks

```javascript
// hooks/useAuth.js
import { useState, useEffect } from 'react';

export const useAuth = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = () => {
      const token = localStorage.getItem('accessToken');
      const userStr = localStorage.getItem('user');

      if (token && userStr) {
        try {
          const userData = JSON.parse(userStr);
          setUser(userData);
        } catch (error) {
          console.error('Invalid user data:', error);
          localStorage.clear();
        }
      }
      setLoading(false);
    };

    initAuth();
  }, []);

  const login = async (credentials) => {
    try {
      const response = await authService.login(credentials);
      setUser(response.user);
      return response;
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    localStorage.clear();
    setUser(null);
    window.location.href = '/login';
  };

  return {
    user,
    loading,
    login,
    logout,
    isAuthenticated: !!user,
    hasRole: (roles) => user && roles.includes(user.role),
    hasInstitutionAccess: (institutionId) => 
      user && (user.role === 'ADMIN' || user.institutionId === institutionId)
  };
};
```

## UI/UX Guidelines

### Design System

#### Color Palette
```css
/* Primary Colors */
--primary-50: #eff6ff;
--primary-500: #3b82f6;
--primary-600: #2563eb;
--primary-700: #1d4ed8;

/* Status Colors */
--success-500: #10b981;
--warning-500: #f59e0b;
--danger-500: #ef4444;
--info-500: #6366f1;

/* Neutral Colors */
--gray-50: #f9fafb;
--gray-100: #f3f4f6;
--gray-500: #6b7280;
--gray-900: #111827;
```

#### Typography
```css
/* Font Sizes */
.text-xs { font-size: 0.75rem; }
.text-sm { font-size: 0.875rem; }
.text-base { font-size: 1rem; }
.text-lg { font-size: 1.125rem; }
.text-xl { font-size: 1.25rem; }
.text-2xl { font-size: 1.5rem; }
.text-3xl { font-size: 1.875rem; }
```

### Component Library

#### Button Component
```javascript
import { cva } from 'class-variance-authority';

const buttonVariants = cva(
  'inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none ring-offset-background',
  {
    variants: {
      variant: {
        default: 'bg-primary text-primary-foreground hover:bg-primary/90',
        destructive: 'bg-destructive text-destructive-foreground hover:bg-destructive/90',
        outline: 'border border-input hover:bg-accent hover:text-accent-foreground',
        secondary: 'bg-secondary text-secondary-foreground hover:bg-secondary/80',
        ghost: 'hover:bg-accent hover:text-accent-foreground',
        link: 'underline-offset-4 hover:underline text-primary'
      },
      size: {
        default: 'h-10 py-2 px-4',
        sm: 'h-9 px-3 rounded-md',
        lg: 'h-11 px-8 rounded-md',
        icon: 'h-10 w-10'
      }
    },
    defaultVariants: {
      variant: 'default',
      size: 'default'
    }
  }
);

const Button = ({ className, variant, size, ...props }) => {
  return (
    <button
      className={cn(buttonVariants({ variant, size, className }))}
      {...props}
    />
  );
};
```

#### Status Badge Component
```javascript
const StatusBadge = ({ status, size = 'default' }) => {
  const statusConfig = {
    DRAFT: { label: 'Draft', color: 'bg-gray-100 text-gray-800' },
    SUBMITTED: { label: 'Submitted', color: 'bg-blue-100 text-blue-800' },
    APPROVED: { label: 'Approved', color: 'bg-green-100 text-green-800' },
    REJECTED: { label: 'Rejected', color: 'bg-red-100 text-red-800' },
    RETURNED: { label: 'Returned', color: 'bg-yellow-100 text-yellow-800' }
  };

  const config = statusConfig[status] || statusConfig.DRAFT;
  const sizeClass = size === 'sm' ? 'px-2 py-1 text-xs' : 'px-2.5 py-0.5 text-sm';

  return (
    <span className={`inline-flex items-center rounded-full font-medium ${config.color} ${sizeClass}`}>
      {config.label}
    </span>
  );
};
```

### Layout Components

#### Main Layout
```javascript
const MainLayout = ({ children }) => {
  const { user } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-gray-900">
                Civil Service Management System
              </h1>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-700">{user?.fullName}</span>
              <span className="text-xs text-gray-500">{user?.role}</span>
              <button 
                onClick={() => logout()}
                className="text-sm text-red-600 hover:text-red-800"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <div className="flex">
        <Sidebar />
        <main className="flex-1 p-6">
          {children}
        </main>
      </div>
    </div>
  );
};
```

#### Sidebar Navigation
```javascript
const Sidebar = () => {
  const { user } = useAuth();
  const router = useRouter();

  const navigationItems = [
    { 
      name: 'Dashboard', 
      href: '/dashboard', 
      icon: HomeIcon,
      roles: ['HRO', 'HHRMD', 'HRMO', 'DO', 'EMP', 'PO', 'CSCS', 'HRRP', 'ADMIN']
    },
    {
      name: 'Employees',
      href: '/employees',
      icon: UsersIcon,
      roles: ['HRO', 'HHRMD', 'HRMO', 'DO', 'CSCS', 'HRRP', 'ADMIN']
    },
    {
      name: 'Requests',
      href: '/requests',
      icon: DocumentTextIcon,
      roles: ['HRO', 'HHRMD', 'HRMO', 'ADMIN']
    },
    {
      name: 'Complaints',
      href: '/complaints',
      icon: ExclamationTriangleIcon,
      roles: ['HHRMD', 'DO', 'EMP', 'ADMIN']
    },
    {
      name: 'Reports',
      href: '/reports',
      icon: ChartBarIcon,
      roles: ['HHRMD', 'HRMO', 'DO', 'PO', 'CSCS', 'HRRP', 'ADMIN']
    },
    {
      name: 'Administration',
      href: '/admin',
      icon: CogIcon,
      roles: ['ADMIN']
    }
  ];

  const filteredItems = navigationItems.filter(item => 
    item.roles.includes(user?.role)
  );

  return (
    <div className="w-64 bg-white shadow-sm h-full">
      <nav className="mt-5 px-2">
        <div className="space-y-1">
          {filteredItems.map((item) => {
            const isActive = router.pathname.startsWith(item.href);
            return (
              <Link
                key={item.name}
                href={item.href}
                className={`${
                  isActive
                    ? 'bg-gray-100 text-gray-900'
                    : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                } group flex items-center px-2 py-2 text-sm font-medium rounded-md`}
              >
                <item.icon
                  className={`${
                    isActive ? 'text-gray-500' : 'text-gray-400 group-hover:text-gray-500'
                  } mr-3 h-6 w-6`}
                  aria-hidden="true"
                />
                {item.name}
              </Link>
            );
          })}
        </div>
      </nav>
    </div>
  );
};
```

## Workflow Implementation

### Request Workflow States

The system implements a complex workflow for HR requests:

```
DRAFT → SUBMITTED → HRO_REVIEW → HRMO_REVIEW → HHRMD_REVIEW → APPROVED
           ↓              ↓              ↓              ↓
        REJECTED     REJECTED      REJECTED      REJECTED
           ↓              ↓              ↓              ↓
        RETURNED     RETURNED      RETURNED      RETURNED
```

### Workflow Component

```javascript
const WorkflowTracker = ({ request }) => {
  const workflowSteps = [
    { key: 'DRAFT', label: 'Draft', icon: DocumentIcon },
    { key: 'SUBMITTED', label: 'Submitted', icon: PaperAirplaneIcon },
    { key: 'HRO_REVIEW', label: 'HRO Review', icon: UserIcon },
    { key: 'HRMO_REVIEW', label: 'HRMO Review', icon: UserGroupIcon },
    { key: 'HHRMD_REVIEW', label: 'HHRMD Review', icon: ShieldCheckIcon },
    { key: 'APPROVED', label: 'Approved', icon: CheckCircleIcon }
  ];

  const getCurrentStepIndex = () => {
    return workflowSteps.findIndex(step => step.key === request.status);
  };

  const currentStepIndex = getCurrentStepIndex();

  return (
    <div className="bg-white p-6 rounded-lg shadow">
      <h3 className="text-lg font-medium text-gray-900 mb-4">Request Status</h3>
      <div className="flex items-center">
        {workflowSteps.map((step, index) => {
          const isCompleted = index < currentStepIndex;
          const isCurrent = index === currentStepIndex;
          const isRejected = request.status === 'REJECTED';
          const isReturned = request.status === 'RETURNED';

          return (
            <div key={step.key} className="flex items-center">
              <div className={`flex items-center justify-center w-8 h-8 rounded-full ${
                isCompleted 
                  ? 'bg-green-500 text-white' 
                  : isCurrent && !isRejected && !isReturned
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-300 text-gray-600'
              }`}>
                <step.icon className="w-4 h-4" />
              </div>
              {index < workflowSteps.length - 1 && (
                <div className={`h-0.5 w-16 ${
                  isCompleted ? 'bg-green-500' : 'bg-gray-300'
                }`} />
              )}
            </div>
          );
        })}
      </div>
      
      {(request.status === 'REJECTED' || request.status === 'RETURNED') && (
        <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-md">
          <p className="text-sm text-red-700">
            <span className="font-medium">
              {request.status === 'REJECTED' ? 'Rejected' : 'Returned for Revision'}:
            </span>
            {' '}
            {request.rejectionReason || 'No reason provided'}
          </p>
        </div>
      )}
    </div>
  );
};
```

### Approval Actions Component

```javascript
const ApprovalActions = ({ request, onApprove, onReject, onReturn }) => {
  const { user } = useAuth();
  const [showModal, setShowModal] = useState(false);
  const [action, setAction] = useState(null);
  const [comments, setComments] = useState('');

  const canApprove = () => {
    const allowedRoles = ['HHRMD', 'HRMO', 'ADMIN'];
    const pendingStatuses = ['SUBMITTED', 'HRO_REVIEW', 'HRMO_REVIEW', 'HHRMD_REVIEW'];
    
    return allowedRoles.includes(user.role) && pendingStatuses.includes(request.status);
  };

  const handleAction = async () => {
    try {
      switch (action) {
        case 'approve':
          await onApprove(request.id, { comments });
          break;
        case 'reject':
          await onReject(request.id, { rejectionReason: comments });
          break;
        case 'return':
          await onReturn(request.id, { rejectionReason: comments });
          break;
      }
      setShowModal(false);
      setComments('');
    } catch (error) {
      console.error('Action failed:', error);
    }
  };

  if (!canApprove()) {
    return null;
  }

  return (
    <>
      <div className="flex gap-2">
        <button
          onClick={() => { setAction('approve'); setShowModal(true); }}
          className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700"
        >
          Approve
        </button>
        <button
          onClick={() => { setAction('reject'); setShowModal(true); }}
          className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
        >
          Reject
        </button>
        <button
          onClick={() => { setAction('return'); setShowModal(true); }}
          className="px-4 py-2 bg-yellow-600 text-white rounded-md hover:bg-yellow-700"
        >
          Return for Revision
        </button>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-lg max-w-md w-full">
            <h3 className="text-lg font-medium mb-4">
              {action === 'approve' ? 'Approve Request' : 
               action === 'reject' ? 'Reject Request' : 'Return for Revision'}
            </h3>
            <textarea
              value={comments}
              onChange={(e) => setComments(e.target.value)}
              placeholder="Enter comments or reason..."
              className="w-full p-2 border border-gray-300 rounded-md"
              rows={4}
              required={action !== 'approve'}
            />
            <div className="flex gap-2 mt-4">
              <button
                onClick={handleAction}
                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                disabled={action !== 'approve' && !comments.trim()}
              >
                Confirm
              </button>
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};
```

## Forms & Validation

### Form Component with Validation

```javascript
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';

const promotionRequestSchema = yup.object({
  employeeId: yup.string().required('Employee is required'),
  promotionType: yup.string().oneOf(['EDUCATIONAL', 'PERFORMANCE']).required('Promotion type is required'),
  currentPosition: yup.string().required('Current position is required').max(255),
  currentGrade: yup.string().required('Current grade is required').max(100),
  proposedPosition: yup.string().required('Proposed position is required').max(255),
  proposedGrade: yup.string().required('Proposed grade is required').max(100),
  currentSalary: yup.number().min(0, 'Salary must be non-negative').nullable(),
  proposedSalary: yup.number().min(0, 'Salary must be non-negative').nullable(),
  effectiveDate: yup.date().min(new Date(), 'Effective date must be in the future').nullable(),
  justification: yup.string().max(2000, 'Justification cannot exceed 2000 characters'),
  yearsInCurrentPosition: yup.number()
    .required('Years in current position is required')
    .min(2, 'Employee must have served minimum 2 years in current position'),
  performanceRating: yup.string()
    .oneOf(['EXCELLENT', 'GOOD', 'SATISFACTORY', 'NEEDS_IMPROVEMENT', 'UNSATISFACTORY'])
});

const PromotionRequestForm = ({ onSubmit, initialData = {} }) => {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    watch,
    setValue
  } = useForm({
    resolver: yupResolver(promotionRequestSchema),
    defaultValues: initialData
  });

  const promotionType = watch('promotionType');

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Employee Selection */}
      <div>
        <label className="block text-sm font-medium text-gray-700">
          Employee *
        </label>
        <EmployeeSelect
          value={watch('employeeId')}
          onChange={(value) => setValue('employeeId', value)}
          error={errors.employeeId?.message}
        />
      </div>

      {/* Promotion Type */}
      <div>
        <label className="block text-sm font-medium text-gray-700">
          Promotion Type *
        </label>
        <select
          {...register('promotionType')}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
        >
          <option value="">Select promotion type</option>
          <option value="EDUCATIONAL">Educational</option>
          <option value="PERFORMANCE">Performance</option>
        </select>
        {errors.promotionType && (
          <p className="mt-1 text-sm text-red-600">{errors.promotionType.message}</p>
        )}
      </div>

      {/* Current Position */}
      <div>
        <label className="block text-sm font-medium text-gray-700">
          Current Position *
        </label>
        <input
          type="text"
          {...register('currentPosition')}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
        />
        {errors.currentPosition && (
          <p className="mt-1 text-sm text-red-600">{errors.currentPosition.message}</p>
        )}
      </div>

      {/* Proposed Position */}
      <div>
        <label className="block text-sm font-medium text-gray-700">
          Proposed Position *
        </label>
        <input
          type="text"
          {...register('proposedPosition')}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
        />
        {errors.proposedPosition && (
          <p className="mt-1 text-sm text-red-600">{errors.proposedPosition.message}</p>
        )}
      </div>

      {/* Salary Information */}
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Current Salary
          </label>
          <input
            type="number"
            step="0.01"
            {...register('currentSalary')}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          />
          {errors.currentSalary && (
            <p className="mt-1 text-sm text-red-600">{errors.currentSalary.message}</p>
          )}
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Proposed Salary
          </label>
          <input
            type="number"
            step="0.01"
            {...register('proposedSalary')}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          />
          {errors.proposedSalary && (
            <p className="mt-1 text-sm text-red-600">{errors.proposedSalary.message}</p>
          )}
        </div>
      </div>

      {/* Conditional Fields based on Promotion Type */}
      {promotionType === 'PERFORMANCE' && (
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Performance Rating
          </label>
          <select
            {...register('performanceRating')}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          >
            <option value="">Select rating</option>
            <option value="EXCELLENT">Excellent</option>
            <option value="GOOD">Good</option>
            <option value="SATISFACTORY">Satisfactory</option>
            <option value="NEEDS_IMPROVEMENT">Needs Improvement</option>
            <option value="UNSATISFACTORY">Unsatisfactory</option>
          </select>
          {errors.performanceRating && (
            <p className="mt-1 text-sm text-red-600">{errors.performanceRating.message}</p>
          )}
        </div>
      )}

      {/* Years in Current Position */}
      <div>
        <label className="block text-sm font-medium text-gray-700">
          Years in Current Position *
        </label>
        <input
          type="number"
          min="0"
          {...register('yearsInCurrentPosition')}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
        />
        {errors.yearsInCurrentPosition && (
          <p className="mt-1 text-sm text-red-600">{errors.yearsInCurrentPosition.message}</p>
        )}
      </div>

      {/* Justification */}
      <div>
        <label className="block text-sm font-medium text-gray-700">
          Justification
        </label>
        <textarea
          {...register('justification')}
          rows={4}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          placeholder="Provide justification for this promotion..."
        />
        {errors.justification && (
          <p className="mt-1 text-sm text-red-600">{errors.justification.message}</p>
        )}
      </div>

      {/* Submit Button */}
      <div className="flex justify-end">
        <button
          type="submit"
          disabled={isSubmitting}
          className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
        >
          {isSubmitting ? 'Submitting...' : 'Submit Request'}
        </button>
      </div>
    </form>
  );
};
```

### Employee Selection Component

```javascript
import { useState, useEffect } from 'react';
import { employeeService } from '../services/api';

const EmployeeSelect = ({ value, onChange, error, institutionId = null }) => {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');

  useEffect(() => {
    const fetchEmployees = async () => {
      setLoading(true);
      try {
        const response = institutionId 
          ? await employeeService.getEmployeesByInstitution(institutionId)
          : await employeeService.getEmployees(0, 100, search);
        setEmployees(response.data.content || response.data);
      } catch (error) {
        console.error('Failed to fetch employees:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchEmployees();
  }, [institutionId, search]);

  const selectedEmployee = employees.find(emp => emp.id === value);

  return (
    <div>
      <div className="relative">
        <input
          type="text"
          value={selectedEmployee ? `${selectedEmployee.fullName} (${selectedEmployee.payrollNumber})` : ''}
          placeholder="Search and select employee..."
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 ${
            error ? 'border-red-300' : ''
          }`}
          readOnly
        />
        <select
          value={value || ''}
          onChange={(e) => onChange(e.target.value)}
          className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
        >
          <option value="">Select employee</option>
          {employees.map(employee => (
            <option key={employee.id} value={employee.id}>
              {employee.fullName} ({employee.payrollNumber})
            </option>
          ))}
        </select>
      </div>
      {error && (
        <p className="mt-1 text-sm text-red-600">{error}</p>
      )}
      {loading && (
        <p className="mt-1 text-sm text-gray-500">Loading employees...</p>
      )}
    </div>
  );
};
```

## File Upload & Document Management

### File Upload Component

```javascript
import { useState } from 'react';
import { DocumentArrowUpIcon, XMarkIcon } from '@heroicons/react/24/outline';

const FileUpload = ({ onFilesChange, maxFiles = 5, maxSize = 2 * 1024 * 1024, acceptedTypes = ['.pdf', '.doc', '.docx'] }) => {
  const [files, setFiles] = useState([]);
  const [dragActive, setDragActive] = useState(false);

  const handleFiles = (fileList) => {
    const newFiles = Array.from(fileList).filter(file => {
      // Check file type
      const fileExtension = '.' + file.name.split('.').pop().toLowerCase();
      if (!acceptedTypes.includes(fileExtension)) {
        alert(`File type ${fileExtension} is not allowed. Allowed types: ${acceptedTypes.join(', ')}`);
        return false;
      }

      // Check file size
      if (file.size > maxSize) {
        alert(`File ${file.name} is too large. Maximum size is ${maxSize / (1024 * 1024)}MB`);
        return false;
      }

      return true;
    });

    const updatedFiles = [...files, ...newFiles].slice(0, maxFiles);
    setFiles(updatedFiles);
    onFilesChange(updatedFiles);
  };

  const removeFile = (index) => {
    const updatedFiles = files.filter((_, i) => i !== index);
    setFiles(updatedFiles);
    onFilesChange(updatedFiles);
  };

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFiles(e.dataTransfer.files);
    }
  };

  const handleChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      handleFiles(e.target.files);
    }
  };

  return (
    <div className="space-y-4">
      <div
        className={`relative border-2 border-dashed rounded-lg p-6 ${
          dragActive 
            ? 'border-indigo-500 bg-indigo-50' 
            : 'border-gray-300 hover:border-gray-400'
        }`}
        onDragEnter={handleDrag}
        onDragLeave={handleDrag}
        onDragOver={handleDrag}
        onDrop={handleDrop}
      >
        <input
          type="file"
          multiple
          accept={acceptedTypes.join(',')}
          onChange={handleChange}
          className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
        />
        <div className="text-center">
          <DocumentArrowUpIcon className="mx-auto h-12 w-12 text-gray-400" />
          <div className="mt-2">
            <p className="text-sm text-gray-600">
              <span className="font-medium text-indigo-600 hover:text-indigo-500">
                Click to upload
              </span>
              {' '}or drag and drop
            </p>
            <p className="text-xs text-gray-500">
              {acceptedTypes.join(', ')} up to {maxSize / (1024 * 1024)}MB each
            </p>
          </div>
        </div>
      </div>

      {files.length > 0 && (
        <div className="space-y-2">
          <h4 className="text-sm font-medium text-gray-900">Uploaded Files:</h4>
          {files.map((file, index) => (
            <div key={index} className="flex items-center justify-between p-2 bg-gray-50 rounded-md">
              <div>
                <p className="text-sm font-medium text-gray-900">{file.name}</p>
                <p className="text-xs text-gray-500">
                  {(file.size / (1024 * 1024)).toFixed(2)} MB
                </p>
              </div>
              <button
                type="button"
                onClick={() => removeFile(index)}
                className="ml-2 text-red-600 hover:text-red-800"
              >
                <XMarkIcon className="h-5 w-5" />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
```

### Document Upload Service

```javascript
// services/documentService.js
export const documentService = {
  uploadDocuments: async (files, requestId, documentType) => {
    const formData = new FormData();
    
    files.forEach((file, index) => {
      formData.append(`files`, file);
    });
    
    formData.append('requestId', requestId);
    formData.append('documentType', documentType);

    return api.post('/documents/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress: (progressEvent) => {
        const percentCompleted = Math.round(
          (progressEvent.loaded * 100) / progressEvent.total
        );
        console.log(`Upload Progress: ${percentCompleted}%`);
      },
    });
  },

  downloadDocument: async (documentId) => {
    const response = await api.get(`/documents/${documentId}/download`, {
      responseType: 'blob',
    });
    
    // Create blob link to download
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    
    // Try to get filename from response headers
    const contentDisposition = response.headers['content-disposition'];
    let fileName = 'document';
    if (contentDisposition) {
      const fileNameMatch = contentDisposition.match(/filename="(.+)"/);
      if (fileNameMatch && fileNameMatch[1]) {
        fileName = fileNameMatch[1];
      }
    }
    
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },

  getDocumentsByRequest: (requestId) => 
    api.get(`/documents/request/${requestId}`),
    
  deleteDocument: (documentId) => 
    api.delete(`/documents/${documentId}`)
};
```

## Reports & Analytics

### Dashboard Components

```javascript
const DashboardMetrics = () => {
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMetrics = async () => {
      try {
        const response = await dashboardService.getDashboardMetrics();
        setMetrics(response.data);
      } catch (error) {
        console.error('Failed to fetch metrics:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchMetrics();
  }, []);

  if (loading) {
    return <div>Loading metrics...</div>;
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <MetricCard
        title="Total Employees"
        value={metrics?.totalEmployees || 0}
        icon={UsersIcon}
        trend={metrics?.employeesTrend}
      />
      <MetricCard
        title="Pending Requests"
        value={metrics?.pendingRequests || 0}
        icon={ClockIcon}
        trend={metrics?.requestsTrend}
        color="text-yellow-600"
      />
      <MetricCard
        title="Completed This Month"
        value={metrics?.completedThisMonth || 0}
        icon={CheckCircleIcon}
        trend={metrics?.completedTrend}
        color="text-green-600"
      />
      <MetricCard
        title="Overdue Requests"
        value={metrics?.overdueRequests || 0}
        icon={ExclamationTriangleIcon}
        trend={metrics?.overdueTrend}
        color="text-red-600"
      />
    </div>
  );
};

const MetricCard = ({ title, value, icon: Icon, trend, color = "text-blue-600" }) => {
  return (
    <div className="bg-white p-6 rounded-lg shadow">
      <div className="flex items-center">
        <div className="flex-shrink-0">
          <Icon className={`h-8 w-8 ${color}`} />
        </div>
        <div className="ml-5 w-0 flex-1">
          <dl>
            <dt className="text-sm font-medium text-gray-500 truncate">
              {title}
            </dt>
            <dd className="text-lg font-medium text-gray-900">
              {value.toLocaleString()}
            </dd>
          </dl>
        </div>
      </div>
      {trend && (
        <div className="mt-4">
          <div className={`flex items-center text-sm ${
            trend.direction === 'up' ? 'text-green-600' : 'text-red-600'
          }`}>
            <span>{trend.percentage}%</span>
            <span className="ml-1">from last month</span>
          </div>
        </div>
      )}
    </div>
  );
};
```

### Chart Components

```javascript
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const RequestStatusChart = () => {
  const [chartData, setChartData] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await dashboardService.getRequestStatusDistribution();
        const data = response.data;

        setChartData({
          labels: Object.keys(data),
          datasets: [
            {
              label: 'Number of Requests',
              data: Object.values(data),
              backgroundColor: [
                'rgba(59, 130, 246, 0.8)',   // Blue
                'rgba(16, 185, 129, 0.8)',   // Green
                'rgba(245, 158, 11, 0.8)',   // Yellow
                'rgba(239, 68, 68, 0.8)',    // Red
                'rgba(156, 163, 175, 0.8)',  // Gray
              ],
              borderColor: [
                'rgba(59, 130, 246, 1)',
                'rgba(16, 185, 129, 1)',
                'rgba(245, 158, 11, 1)',
                'rgba(239, 68, 68, 1)',
                'rgba(156, 163, 175, 1)',
              ],
              borderWidth: 1,
            },
          ],
        });
      } catch (error) {
        console.error('Failed to fetch chart data:', error);
      }
    };

    fetchData();
  }, []);

  const options = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Request Status Distribution',
      },
    },
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow">
      {chartData ? (
        <Bar data={chartData} options={options} />
      ) : (
        <div>Loading chart...</div>
      )}
    </div>
  );
};
```

### Report Generation

```javascript
const ReportGenerator = () => {
  const [reportType, setReportType] = useState('');
  const [dateRange, setDateRange] = useState({
    startDate: '',
    endDate: ''
  });
  const [filters, setFilters] = useState({
    institutionId: '',
    status: '',
    requestType: ''
  });
  const [generating, setGenerating] = useState(false);

  const reportTypes = [
    { value: 'employee-summary', label: 'Employee Summary Report' },
    { value: 'request-summary', label: 'Request Summary Report' },
    { value: 'promotion-analysis', label: 'Promotion Analysis Report' },
    { value: 'complaint-summary', label: 'Complaint Summary Report' },
    { value: 'institutional-performance', label: 'Institutional Performance Report' }
  ];

  const generateReport = async () => {
    if (!reportType) {
      alert('Please select a report type');
      return;
    }

    setGenerating(true);
    try {
      const params = {
        reportType,
        ...dateRange,
        ...filters
      };

      const response = await api.post('/reports/generate', params, {
        responseType: 'blob'
      });

      // Download the generated report
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${reportType}-${new Date().toISOString().split('T')[0]}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);

    } catch (error) {
      console.error('Report generation failed:', error);
      alert('Failed to generate report. Please try again.');
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow">
      <h3 className="text-lg font-medium text-gray-900 mb-4">Generate Report</h3>
      
      <div className="space-y-4">
        {/* Report Type */}
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Report Type *
          </label>
          <select
            value={reportType}
            onChange={(e) => setReportType(e.target.value)}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          >
            <option value="">Select report type</option>
            {reportTypes.map(type => (
              <option key={type.value} value={type.value}>
                {type.label}
              </option>
            ))}
          </select>
        </div>

        {/* Date Range */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Start Date
            </label>
            <input
              type="date"
              value={dateRange.startDate}
              onChange={(e) => setDateRange(prev => ({...prev, startDate: e.target.value}))}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              End Date
            </label>
            <input
              type="date"
              value={dateRange.endDate}
              onChange={(e) => setDateRange(prev => ({...prev, endDate: e.target.value}))}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
            />
          </div>
        </div>

        {/* Filters */}
        <div className="grid grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Institution
            </label>
            <InstitutionSelect
              value={filters.institutionId}
              onChange={(value) => setFilters(prev => ({...prev, institutionId: value}))}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Status
            </label>
            <select
              value={filters.status}
              onChange={(e) => setFilters(prev => ({...prev, status: e.target.value}))}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
            >
              <option value="">All statuses</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
              <option value="PENDING">Pending</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Request Type
            </label>
            <select
              value={filters.requestType}
              onChange={(e) => setFilters(prev => ({...prev, requestType: e.target.value}))}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
            >
              <option value="">All types</option>
              <option value="PROMOTION">Promotion</option>
              <option value="CONFIRMATION">Confirmation</option>
              <option value="LWOP">Leave Without Pay</option>
              <option value="RETIREMENT">Retirement</option>
            </select>
          </div>
        </div>

        {/* Generate Button */}
        <div className="flex justify-end">
          <button
            onClick={generateReport}
            disabled={generating}
            className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
          >
            {generating ? 'Generating...' : 'Generate Report'}
          </button>
        </div>
      </div>
    </div>
  );
};
```

## Error Handling

### Global Error Handler

```javascript
// contexts/ErrorContext.js
import { createContext, useContext, useReducer } from 'react';

const ErrorContext = createContext();

const errorReducer = (state, action) => {
  switch (action.type) {
    case 'ADD_ERROR':
      return {
        ...state,
        errors: [...state.errors, { ...action.payload, id: Date.now() }]
      };
    case 'REMOVE_ERROR':
      return {
        ...state,
        errors: state.errors.filter(error => error.id !== action.payload)
      };
    case 'CLEAR_ERRORS':
      return { ...state, errors: [] };
    default:
      return state;
  }
};

export const ErrorProvider = ({ children }) => {
  const [state, dispatch] = useReducer(errorReducer, { errors: [] });

  const addError = (error) => {
    dispatch({ type: 'ADD_ERROR', payload: error });
    
    // Auto-remove error after 5 seconds
    setTimeout(() => {
      dispatch({ type: 'REMOVE_ERROR', payload: error.id });
    }, 5000);
  };

  const removeError = (id) => {
    dispatch({ type: 'REMOVE_ERROR', payload: id });
  };

  const clearErrors = () => {
    dispatch({ type: 'CLEAR_ERRORS' });
  };

  return (
    <ErrorContext.Provider value={{ ...state, addError, removeError, clearErrors }}>
      {children}
    </ErrorContext.Provider>
  );
};

export const useError = () => {
  const context = useContext(ErrorContext);
  if (!context) {
    throw new Error('useError must be used within ErrorProvider');
  }
  return context;
};
```

### Error Boundary Component

```javascript
import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    
    // Log error to monitoring service
    if (typeof window !== 'undefined' && window.Sentry) {
      window.Sentry.captureException(error, { extra: errorInfo });
    }
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
          <div className="max-w-md w-full bg-white shadow-lg rounded-lg p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <ExclamationTriangleIcon className="h-8 w-8 text-red-400" />
              </div>
              <div className="ml-3">
                <h3 className="text-sm font-medium text-gray-800">
                  Something went wrong
                </h3>
                <div className="mt-2 text-sm text-gray-500">
                  <p>
                    We're sorry, but something unexpected happened. Please try refreshing the page.
                  </p>
                </div>
                <div className="mt-4">
                  <button
                    onClick={() => window.location.reload()}
                    className="text-sm bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700"
                  >
                    Refresh Page
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
```

### Toast Notification Component

```javascript
import { useError } from '../contexts/ErrorContext';
import { CheckCircleIcon, XCircleIcon, ExclamationTriangleIcon, InformationCircleIcon } from '@heroicons/react/24/outline';

const Toast = ({ error, onClose }) => {
  const getIcon = () => {
    switch (error.type) {
      case 'success':
        return <CheckCircleIcon className="h-6 w-6 text-green-400" />;
      case 'error':
        return <XCircleIcon className="h-6 w-6 text-red-400" />;
      case 'warning':
        return <ExclamationTriangleIcon className="h-6 w-6 text-yellow-400" />;
      default:
        return <InformationCircleIcon className="h-6 w-6 text-blue-400" />;
    }
  };

  const getBgColor = () => {
    switch (error.type) {
      case 'success':
        return 'bg-green-50';
      case 'error':
        return 'bg-red-50';
      case 'warning':
        return 'bg-yellow-50';
      default:
        return 'bg-blue-50';
    }
  };

  return (
    <div className={`max-w-sm w-full ${getBgColor()} shadow-lg rounded-lg pointer-events-auto ring-1 ring-black ring-opacity-5`}>
      <div className="p-4">
        <div className="flex items-start">
          <div className="flex-shrink-0">
            {getIcon()}
          </div>
          <div className="ml-3 w-0 flex-1 pt-0.5">
            <p className="text-sm font-medium text-gray-900">
              {error.title || 'Notification'}
            </p>
            <p className="mt-1 text-sm text-gray-500">
              {error.message}
            </p>
          </div>
          <div className="ml-4 flex-shrink-0 flex">
            <button
              className="bg-white rounded-md inline-flex text-gray-400 hover:text-gray-500 focus:outline-none"
              onClick={() => onClose(error.id)}
            >
              <XMarkIcon className="h-5 w-5" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

const ToastContainer = () => {
  const { errors, removeError } = useError();

  return (
    <div className="fixed top-0 right-0 z-50 p-4 space-y-4">
      {errors.map(error => (
        <Toast
          key={error.id}
          error={error}
          onClose={removeError}
        />
      ))}
    </div>
  );
};

export default ToastContainer;
```

## Performance Considerations

### Pagination Implementation

```javascript
const usePagination = (fetchFn, pageSize = 20) => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const fetchData = async (page = 0, size = pageSize, ...args) => {
    setLoading(true);
    try {
      const response = await fetchFn(page, size, ...args);
      const result = response.data;
      
      setData(result.content || []);
      setCurrentPage(result.number || 0);
      setTotalPages(result.totalPages || 0);
      setTotalElements(result.totalElements || 0);
    } catch (error) {
      console.error('Failed to fetch data:', error);
      setData([]);
    } finally {
      setLoading(false);
    }
  };

  const goToPage = (page) => {
    if (page >= 0 && page < totalPages) {
      fetchData(page);
    }
  };

  const nextPage = () => goToPage(currentPage + 1);
  const prevPage = () => goToPage(currentPage - 1);

  return {
    data,
    loading,
    currentPage,
    totalPages,
    totalElements,
    fetchData,
    goToPage,
    nextPage,
    prevPage,
    hasNext: currentPage < totalPages - 1,
    hasPrev: currentPage > 0
  };
};
```

### Lazy Loading and Code Splitting

```javascript
// Dynamic imports for code splitting
import dynamic from 'next/dynamic';

const EmployeeList = dynamic(() => import('../components/EmployeeList'), {
  loading: () => <div>Loading employees...</div>
});

const RequestForm = dynamic(() => import('../components/RequestForm'), {
  loading: () => <div>Loading form...</div>
});

const Reports = dynamic(() => import('../components/Reports'), {
  loading: () => <div>Loading reports...</div>
});
```

### Debounced Search Hook

```javascript
import { useState, useEffect } from 'react';

const useDebounce = (value, delay) => {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
};

const SearchableList = ({ onSearch }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearchTerm = useDebounce(searchTerm, 300);

  useEffect(() => {
    if (debouncedSearchTerm) {
      onSearch(debouncedSearchTerm);
    }
  }, [debouncedSearchTerm, onSearch]);

  return (
    <input
      type="text"
      value={searchTerm}
      onChange={(e) => setSearchTerm(e.target.value)}
      placeholder="Search..."
      className="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
    />
  );
};
```

## Testing Strategy

### Unit Testing with Jest and React Testing Library

```javascript
// __tests__/components/EmployeeForm.test.js
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import EmployeeForm from '../components/EmployeeForm';

describe('EmployeeForm', () => {
  const mockOnSubmit = jest.fn();

  beforeEach(() => {
    mockOnSubmit.mockClear();
  });

  test('renders form fields correctly', () => {
    render(<EmployeeForm onSubmit={mockOnSubmit} />);
    
    expect(screen.getByLabelText(/full name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/payroll number/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/zanzibar id/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /submit/i })).toBeInTheDocument();
  });

  test('shows validation errors for required fields', async () => {
    render(<EmployeeForm onSubmit={mockOnSubmit} />);
    
    const submitButton = screen.getByRole('button', { name: /submit/i });
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(screen.getByText(/full name is required/i)).toBeInTheDocument();
      expect(screen.getByText(/payroll number is required/i)).toBeInTheDocument();
    });
    
    expect(mockOnSubmit).not.toHaveBeenCalled();
  });

  test('submits form with valid data', async () => {
    const user = userEvent.setup();
    render(<EmployeeForm onSubmit={mockOnSubmit} />);
    
    await user.type(screen.getByLabelText(/full name/i), 'John Doe');
    await user.type(screen.getByLabelText(/payroll number/i), 'PAY001');
    await user.type(screen.getByLabelText(/zanzibar id/i), 'ZAN001');
    
    await user.click(screen.getByRole('button', { name: /submit/i }));
    
    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledWith({
        fullName: 'John Doe',
        payrollNumber: 'PAY001',
        zanzibarId: 'ZAN001'
      });
    });
  });
});
```

### API Testing

```javascript
// __tests__/services/api.test.js
import { employeeService } from '../services/api';
import api from '../utils/api';

jest.mock('../utils/api');

describe('employeeService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('getEmployees calls correct endpoint', async () => {
    const mockResponse = {
      data: {
        content: [{ id: '1', fullName: 'John Doe' }],
        totalElements: 1
      }
    };
    
    api.get.mockResolvedValue(mockResponse);
    
    const result = await employeeService.getEmployees(0, 20, 'John');
    
    expect(api.get).toHaveBeenCalledWith('/employees?page=0&size=20&search=John');
    expect(result.data.content).toHaveLength(1);
  });

  test('createEmployee sends correct data', async () => {
    const employeeData = {
      fullName: 'Jane Doe',
      payrollNumber: 'PAY002'
    };
    
    const mockResponse = { data: { id: '2', ...employeeData } };
    api.post.mockResolvedValue(mockResponse);
    
    const result = await employeeService.createEmployee(employeeData);
    
    expect(api.post).toHaveBeenCalledWith('/employees', employeeData);
    expect(result.data.id).toBe('2');
  });
});
```

### E2E Testing with Cypress

```javascript
// cypress/e2e/employee-management.cy.js
describe('Employee Management', () => {
  beforeEach(() => {
    // Login as admin
    cy.login('admin@test.com', 'password');
    cy.visit('/employees');
  });

  it('should create a new employee', () => {
    cy.get('[data-testid="add-employee-btn"]').click();
    
    cy.get('[data-testid="fullName"]').type('Test Employee');
    cy.get('[data-testid="payrollNumber"]').type('TEST001');
    cy.get('[data-testid="zanzibarId"]').type('ZAN001');
    cy.get('[data-testid="employmentDate"]').type('2024-01-01');
    
    cy.get('[data-testid="submit-btn"]').click();
    
    cy.get('[data-testid="success-message"]').should('contain', 'Employee created successfully');
    cy.url().should('include', '/employees');
  });

  it('should search for employees', () => {
    cy.get('[data-testid="search-input"]').type('John');
    cy.get('[data-testid="employee-list"]').should('contain', 'John');
  });

  it('should handle validation errors', () => {
    cy.get('[data-testid="add-employee-btn"]').click();
    cy.get('[data-testid="submit-btn"]').click();
    
    cy.get('[data-testid="error-message"]').should('contain', 'Full name is required');
  });
});
```

## Deployment & Configuration

### Environment Configuration

```javascript
// next.config.js
/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
    NEXT_PUBLIC_APP_NAME: 'Civil Service Management System',
    NEXT_PUBLIC_VERSION: process.env.npm_package_version,
  },
  async redirects() {
    return [
      {
        source: '/',
        destination: '/dashboard',
        permanent: false,
      },
    ];
  },
  async headers() {
    return [
      {
        source: '/(.*)',
        headers: [
          {
            key: 'X-Frame-Options',
            value: 'DENY',
          },
          {
            key: 'X-Content-Type-Options',
            value: 'nosniff',
          },
          {
            key: 'Referrer-Policy',
            value: 'strict-origin-when-cross-origin',
          },
        ],
      },
    ];
  },
};

module.exports = nextConfig;
```

### Environment Variables

```bash
# .env.local (development)
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_APP_ENV=development

# .env.production (production)
NEXT_PUBLIC_API_URL=https://api.csms.zanzibar.go.tz
NEXT_PUBLIC_APP_ENV=production
```

### Docker Configuration

```dockerfile
# Dockerfile
FROM node:18-alpine AS deps
RUN apk add --no-cache libc6-compat
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --only=production

FROM node:18-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

FROM node:18-alpine AS runner
WORKDIR /app

ENV NODE_ENV production

RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

COPY --from=builder /app/public ./public
COPY --from=builder /app/package.json ./package.json

COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

EXPOSE 3000

ENV PORT 3000

CMD ["node", "server.js"]
```

### Production Build Script

```json
{
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint",
    "test": "jest",
    "test:watch": "jest --watch",
    "test:coverage": "jest --coverage",
    "e2e": "cypress open",
    "e2e:headless": "cypress run",
    "type-check": "tsc --noEmit",
    "format": "prettier --write .",
    "format:check": "prettier --check ."
  }
}
```

This comprehensive frontend development guide provides everything needed to build a robust Next.js 15 application for the CSMS backend. The guide covers authentication, API integration, component design, workflow management, and production deployment considerations specific to the civil service management requirements.

## Key Takeaways

1. **Security First**: Implement proper JWT handling with automatic refresh
2. **Role-Based Access**: Ensure UI components respect user permissions
3. **Form Validation**: Use comprehensive client-side validation with server-side backup
4. **Error Handling**: Implement global error handling with user-friendly messages
5. **Performance**: Use pagination, lazy loading, and debounced search
6. **Testing**: Comprehensive testing strategy with unit, integration, and E2E tests
7. **Documentation**: Maintain clear documentation for API integration and data models

The backend provides a solid foundation with comprehensive APIs, JWT security, and well-structured data models. The frontend should focus on creating an intuitive user experience that guides users through complex HR workflows while maintaining security and performance standards.