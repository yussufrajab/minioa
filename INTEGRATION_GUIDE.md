# CSMS Integration Guide - External System Integration Documentation

**Civil Service Management System (CSMS) - Version 3.1.5**  
**Generated on:** July 25, 2025  
**Contact:** Technical Integration Team  
**Environment:** Production Ready  

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication & Security](#authentication--security)
3. [API Architecture](#api-architecture)
4. [Integration Patterns](#integration-patterns)
5. [Core API Endpoints](#core-api-endpoints)
6. [Data Models](#data-models)
7. [Error Handling](#error-handling)
8. [Rate Limiting & Best Practices](#rate-limiting--best-practices)
9. [Integration Examples](#integration-examples)
10. [Testing & Monitoring](#testing--monitoring)
11. [Support & Contact](#support--contact)

---

## Overview

### System Architecture
The Civil Service Management System (CSMS) is a comprehensive HR management platform built on modern technology stack:

- **Backend:** Spring Boot 3.1.5 (Java 17) with RESTful APIs
- **Database:** PostgreSQL 15 (Database: `prizma`)
- **Authentication:** JWT-based with role-based access control
- **File Storage:** MinIO for document management
- **Base URL:** `https://csms.zanzibar.gov.tz/api` (Production)
- **Base URL:** `http://localhost:8080/api` (Development)

### Integration Capabilities
CSMS provides comprehensive APIs for:
- Employee data management
- HR request processing (8 modules)
- Document management
- Reporting and analytics
- Real-time notifications
- Audit trail access

### Supported Integration Types
1. **Real-time API Integration** - Direct REST API calls
2. **Webhook Integration** - Event-driven notifications
3. **Batch Data Exchange** - Scheduled data synchronization
4. **File-based Integration** - CSV/Excel data exchange

---

## Authentication & Security

### JWT Authentication

CSMS uses JWT (JSON Web Token) based authentication with role-based access control.

#### Authentication Flow
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "your_username",
  "password": "your_password"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 600,
    "user": {
      "id": 1,
      "username": "integration_user",
      "role": "HRMO",
      "institutionId": 5,
      "institutionName": "Ministry of Finance"
    }
  }
}
```

#### Token Usage
Include the access token in all API requests:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Token Refresh
Access tokens expire in 10 minutes. Use refresh token to get new access token:
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### API Keys (Alternative Authentication)
For system-to-system integration, API keys can be provided:
```http
X-API-Key: your_api_key_here
```

### Role-Based Access Control

| Role | Access Level | Integration Use Case |
|------|--------------|---------------------|
| **ADMIN** | Full system access | Complete data synchronization |
| **CSCS** | Cross-institutional oversight | Strategic reporting and analytics |
| **HHRMD** | Cross-institutional management | Policy implementation, approvals |
| **HRMO** | Cross-institutional operations | Data processing, coordination |
| **HRO** | Institution-specific | Department-level integration |
| **SYSTEM_INTEGRATION** | API-specific access | Dedicated integration role |

---

## API Architecture

### Base URL Structure
```
Production: https://csms.zanzibar.gov.tz/api
Development: http://localhost:8080/api
Testing: https://test-csms.zanzibar.gov.tz/api
```

### Standard Response Format
All API responses follow this standard format:
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation successful",
  "timestamp": "2025-07-25T10:30:00Z",
  "path": "/api/employees",
  "status": 200
}
```

### Error Response Format
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid employee data provided",
    "details": [
      {
        "field": "email",
        "message": "Email format is invalid"
      }
    ]
  },
  "timestamp": "2025-07-25T10:30:00Z",
  "path": "/api/employees",
  "status": 400
}
```

### Pagination
List endpoints support pagination:
```http
GET /api/employees?page=0&size=20&sort=firstName,asc
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [...],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "direction": "ASC",
        "property": "firstName"
      }
    },
    "totalElements": 1500,
    "totalPages": 75,
    "first": true,
    "last": false
  }
}
```

---

## Integration Patterns

### 1. Employee Data Synchronization

#### Pull Employee Data
```http
GET /api/employees?institutionId=5&status=ACTIVE&page=0&size=100
Authorization: Bearer {token}
```

#### Push Employee Updates
```http
PUT /api/employees/{employeeId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@ministry.gov.tz",
  "payrollNumber": "PAY001234",
  "position": "Senior Officer",
  "salaryGrade": "M7",
  "institutionId": 5,
  "employmentStatus": "ACTIVE"
}
```

### 2. HR Request Processing Integration

#### Submit HR Request
```http
POST /api/promotion-requests
Authorization: Bearer {token}
Content-Type: application/json

{
  "employeeId": 123,
  "promotionType": "EDUCATION_BASED",
  "newPosition": "Principal Officer",
  "newSalaryGrade": "M8",
  "effectiveDate": "2025-08-01",
  "justification": "Completed Master's degree",
  "documentObjectKeys": ["promotion/certificates/masters_degree.pdf"]
}
```

#### Monitor Request Status
```http
GET /api/promotion-requests/{requestId}
Authorization: Bearer {token}
```

### 3. Real-time Notifications (Webhooks)

Register webhook endpoints to receive real-time updates:
```http
POST /api/webhooks/register
Authorization: Bearer {token}
Content-Type: application/json

{
  "url": "https://your-system.com/webhooks/csms",
  "events": ["EMPLOYEE_UPDATED", "REQUEST_APPROVED", "REQUEST_REJECTED"],
  "secret": "webhook_secret_key"
}
```

**Webhook Payload Example:**
```json
{
  "event": "REQUEST_APPROVED",
  "timestamp": "2025-07-25T10:30:00Z",
  "data": {
    "requestId": 12345,
    "requestType": "PROMOTION",
    "employeeId": 123,
    "status": "APPROVED",
    "approvedBy": "jane.smith@cscs.gov.tz",
    "approvalDate": "2025-07-25T10:30:00Z"
  },
  "signature": "sha256=..."
}
```

### 4. Batch Data Exchange

#### Export Employee Data
```http
GET /api/reports/employee?format=JSON&institutionId=5&dateFrom=2025-01-01&dateTo=2025-07-25
Authorization: Bearer {token}
```

#### Import Bulk Data
```http
POST /api/employees/bulk-import
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: employees_data.csv
```

---

## Core API Endpoints

### Employee Management

#### Get Employee List
```http
GET /api/employees
```
**Query Parameters:**
- `page` (int): Page number (default: 0)
- `size` (int): Page size (default: 20, max: 100)
- `sort` (string): Sort field and direction (e.g., "firstName,asc")
- `institutionId` (int): Filter by institution
- `status` (string): Employment status (ACTIVE, INACTIVE, TERMINATED)
- `search` (string): Search by name or payroll number

#### Get Single Employee
```http
GET /api/employees/{employeeId}
```

#### Search Employees
```http
GET /api/employees/search?query=john&institutionId=5
```

#### Update Employee
```http
PUT /api/employees/{employeeId}
```

### HR Request Management

#### Confirmation Requests
```http
GET /api/requests/confirmation
POST /api/requests/confirmation
PUT /api/requests/confirmation/{id}
GET /api/requests/confirmation/{id}
```

#### Promotion Requests
```http
GET /api/promotion-requests
POST /api/promotion-requests
PUT /api/promotion-requests/{id}
PUT /api/promotion-requests/{id}/approve
PUT /api/promotion-requests/{id}/reject
```

#### Retirement Requests
```http
GET /api/retirement-requests
POST /api/retirement-requests
PUT /api/retirement-requests/{id}
PUT /api/retirement-requests/{id}/approve
```

#### Other Request Types
- `/api/resignation-requests`
- `/api/service-extension-requests`
- `/api/cadre-change-requests`
- `/api/termination-requests`
- `/api/lwop` (Leave Without Pay)

### Document Management

#### Upload Document
```http
POST /api/files/upload
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: document.pdf
folder: promotion_requests
```

#### Download Document
```http
GET /api/files/download/{objectKey}
Authorization: Bearer {token}
```

#### Get Document Preview URL
```http
GET /api/files/preview/{objectKey}
Authorization: Bearer {token}
```

### Reporting & Analytics

#### Generate Report
```http
POST /api/reports/generate
Authorization: Bearer {token}
Content-Type: application/json

{
  "reportType": "EMPLOYEE_SUMMARY",
  "format": "PDF",
  "filters": {
    "institutionId": 5,
    "dateFrom": "2025-01-01",
    "dateTo": "2025-07-25",
    "status": "ACTIVE"
  }
}
```

#### Get Dashboard Metrics
```http
GET /api/dashboard/metrics
Authorization: Bearer {token}
```

#### Get Analytics Data
```http
GET /api/analytics/system
GET /api/analytics/institution/{institutionId}
```

---

## Data Models

### Employee Model
```json
{
  "id": 123,
  "payrollNumber": "PAY001234",
  "zanzibarId": "ZNZ1234567890",
  "firstName": "John",
  "middleName": "Michael",
  "lastName": "Doe",
  "email": "john.doe@ministry.gov.tz",
  "phoneNumber": "+255777123456",
  "dateOfBirth": "1985-03-15",
  "gender": "MALE",
  "maritalStatus": "MARRIED",
  "nationality": "TANZANIAN",
  "employmentDate": "2010-06-01",
  "employmentStatus": "ACTIVE",
  "position": "Senior Officer",
  "salaryGrade": "M7",
  "salaryStep": 5,
  "department": "Finance Department",
  "institution": {
    "id": 5,
    "name": "Ministry of Finance",
    "code": "MOF",
    "type": "MINISTRY"
  },
  "qualifications": [
    {
      "level": "MASTERS",
      "field": "Business Administration",
      "institution": "University of Dar es Salaam",
      "year": 2015
    }
  ],
  "createdAt": "2010-06-01T08:00:00Z",
  "updatedAt": "2025-07-25T10:30:00Z"
}
```

### HR Request Model
```json
{
  "id": 12345,
  "requestNumber": "PROM-2025-001234",
  "requestType": "PROMOTION",
  "employee": {
    "id": 123,
    "name": "John Michael Doe",
    "payrollNumber": "PAY001234"
  },
  "status": "PENDING_HHRMD_REVIEW",
  "submittedBy": {
    "id": 45,
    "name": "Jane Smith",
    "role": "HRO"
  },
  "submissionDate": "2025-07-20T09:00:00Z",
  "requestData": {
    "promotionType": "EDUCATION_BASED",
    "currentPosition": "Senior Officer",
    "newPosition": "Principal Officer",
    "currentSalaryGrade": "M7",
    "newSalaryGrade": "M8",
    "effectiveDate": "2025-08-01",
    "justification": "Completed Master's degree in Business Administration"
  },
  "documents": [
    {
      "objectKey": "promotion/certificates/masters_degree.pdf",
      "fileName": "Masters_Degree_Certificate.pdf",
      "uploadDate": "2025-07-20T09:15:00Z"
    }
  ],
  "workflow": [
    {
      "stage": "SUBMITTED",
      "status": "COMPLETED",
      "date": "2025-07-20T09:00:00Z",
      "actor": "jane.smith@ministry.gov.tz"
    },
    {
      "stage": "HRO_REVIEW",
      "status": "COMPLETED",
      "date": "2025-07-21T14:30:00Z",
      "actor": "jane.smith@ministry.gov.tz",
      "comments": "All documents verified"
    },
    {
      "stage": "HRMO_REVIEW",
      "status": "COMPLETED",
      "date": "2025-07-23T11:00:00Z",
      "actor": "mark.johnson@cscs.gov.tz",
      "comments": "Approved for HHRMD review"
    },
    {
      "stage": "HHRMD_REVIEW",
      "status": "PENDING",
      "date": null,
      "actor": null
    }
  ],
  "createdAt": "2025-07-20T09:00:00Z",
  "updatedAt": "2025-07-23T11:00:00Z"
}
```

### Institution Model
```json
{
  "id": 5,
  "name": "Ministry of Finance",
  "shortName": "MOF",
  "code": "MOF",
  "type": "MINISTRY",
  "parentInstitution": null,
  "address": {
    "street": "Malindi Road",
    "city": "Stone Town",
    "region": "Urban West",
    "postalCode": "P.O. Box 1000",
    "country": "Tanzania"
  },
  "contact": {
    "email": "info@finance.zanzibar.gov.tz",
    "phone": "+255 24 223 1006",
    "website": "https://finance.zanzibar.gov.tz"
  },
  "establishmentDate": "1964-01-12",
  "status": "ACTIVE",
  "employeeCount": 250,
  "createdAt": "2010-01-01T00:00:00Z",
  "updatedAt": "2025-07-25T10:30:00Z"
}
```

### Complaint Model
```json
{
  "id": 6789,
  "complaintNumber": "COMP-2025-001234",
  "complainant": {
    "id": 123,
    "name": "John Doe",
    "institution": "Ministry of Finance"
  },
  "subject": "Workplace harassment complaint",
  "description": "Detailed description of the complaint...",
  "category": "WORKPLACE_CONDUCT",
  "priority": "HIGH",
  "status": "UNDER_INVESTIGATION",
  "investigator": {
    "id": 78,
    "name": "Mary Wilson",
    "role": "DO"
  },
  "submissionDate": "2025-07-20T10:00:00Z",
  "targetResolutionDate": "2025-08-20T23:59:59Z",
  "documents": [
    {
      "objectKey": "complaints/evidence/incident_report.pdf",
      "fileName": "Incident_Report.pdf",
      "uploadDate": "2025-07-20T10:15:00Z"
    }
  ],
  "updates": [
    {
      "date": "2025-07-20T10:00:00Z",
      "status": "SUBMITTED",
      "comments": "Complaint submitted",
      "updatedBy": "system"
    },
    {
      "date": "2025-07-21T09:00:00Z",
      "status": "UNDER_INVESTIGATION",
      "comments": "Investigation assigned to Mary Wilson",
      "updatedBy": "admin@cscs.gov.tz"
    }
  ],
  "createdAt": "2025-07-20T10:00:00Z",
  "updatedAt": "2025-07-21T09:00:00Z"
}
```

---

## Error Handling

### HTTP Status Codes

| Status Code | Description | Usage |
|------------|-------------|-------|
| 200 | OK | Successful GET, PUT requests |
| 201 | Created | Successful POST requests |
| 204 | No Content | Successful DELETE requests |
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Missing or invalid authentication |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource conflict (duplicate data) |
| 422 | Unprocessable Entity | Validation errors |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Server error |
| 503 | Service Unavailable | System maintenance |

### Error Response Examples

#### Validation Error (400)
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Request validation failed",
    "details": [
      {
        "field": "email",
        "message": "Email format is invalid",
        "rejectedValue": "invalid-email"
      },
      {
        "field": "payrollNumber",
        "message": "Payroll number already exists",
        "rejectedValue": "PAY001234"
      }
    ]
  },
  "timestamp": "2025-07-25T10:30:00Z",
  "path": "/api/employees",
  "status": 400
}
```

#### Authentication Error (401)
```json
{
  "success": false,
  "error": {
    "code": "AUTHENTICATION_FAILED",
    "message": "Invalid or expired token",
    "details": null
  },
  "timestamp": "2025-07-25T10:30:00Z",
  "path": "/api/employees",
  "status": 401
}
```

#### Permission Error (403)
```json
{
  "success": false,
  "error": {
    "code": "ACCESS_DENIED",
    "message": "Insufficient permissions to access this resource",
    "details": {
      "requiredRole": "HHRMD",
      "userRole": "HRO",
      "resource": "/api/employees/123"
    }
  },
  "timestamp": "2025-07-25T10:30:00Z",
  "path": "/api/employees/123",
  "status": 403
}
```

#### Rate Limit Error (429)
```json
{
  "success": false,
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Too many requests. Please try again later.",
    "details": {
      "limit": 100,
      "windowSeconds": 3600,
      "retryAfter": 1800
    }
  },
  "timestamp": "2025-07-25T10:30:00Z",
  "path": "/api/employees",
  "status": 429
}
```

---

## Rate Limiting & Best Practices

### Rate Limits

| Endpoint Category | Requests per Hour | Burst Limit |
|------------------|-------------------|-------------|
| Authentication | 100 | 10 |
| Employee Data | 1000 | 50 |
| HR Requests | 500 | 25 |
| File Operations | 200 | 10 |
| Reports | 100 | 5 |
| Analytics | 50 | 3 |

### Headers
Rate limit information is provided in response headers:
```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 985
X-RateLimit-Reset: 1690282800
X-RateLimit-RetryAfter: 3600
```

### Best Practices

#### 1. Connection Management
- Use connection pooling for multiple requests
- Implement proper timeout handling (30 seconds recommended)
- Use HTTP/2 for better performance

#### 2. Caching Strategy
- Cache authentication tokens until expiry
- Cache static data (institutions, lookup values)
- Implement ETags for conditional requests

#### 3. Error Handling
- Implement exponential backoff for retries
- Handle rate limits gracefully
- Log all errors for monitoring

#### 4. Data Synchronization
- Use incremental sync with `lastModified` timestamps
- Implement idempotent operations
- Batch requests when possible

#### 5. Security
- Store API credentials securely
- Use HTTPS for all communications
- Validate webhook signatures
- Implement request/response logging

---

## Integration Examples

### 1. Payroll System Integration

#### Sync Employee Salary Data
```python
import requests
import json
from datetime import datetime

class CSMSIntegration:
    def __init__(self, base_url, api_key):
        self.base_url = base_url
        self.headers = {
            'Authorization': f'Bearer {api_key}',
            'Content-Type': 'application/json'
        }
    
    def get_salary_changes(self, since_date):
        """Get promotion requests approved since given date"""
        url = f"{self.base_url}/promotion-requests"
        params = {
            'status': 'APPROVED',
            'approvedAfter': since_date.isoformat(),
            'size': 100
        }
        
        response = requests.get(url, headers=self.headers, params=params)
        response.raise_for_status()
        
        return response.json()['data']['content']
    
    def update_payroll_data(self, promotion_data):
        """Update payroll system with promotion data"""
        for promotion in promotion_data:
            payload = {
                'employeeId': promotion['employee']['id'],
                'payrollNumber': promotion['employee']['payrollNumber'],
                'newSalaryGrade': promotion['requestData']['newSalaryGrade'],
                'effectiveDate': promotion['requestData']['effectiveDate'],
                'approvalDate': promotion['workflow'][-1]['date']
            }
            
            # Send to payroll system
            payroll_response = self.send_to_payroll(payload)
            
            # Confirm update in CSMS
            if payroll_response['success']:
                self.confirm_payroll_update(promotion['id'])

# Usage
csms = CSMSIntegration('https://csms.zanzibar.gov.tz/api', 'your_api_key')
changes = csms.get_salary_changes(datetime(2025, 7, 1))
csms.update_payroll_data(changes)
```

#### Node.js Integration Example
```javascript
const axios = require('axios');

class CSMSClient {
    constructor(baseURL, token) {
        this.client = axios.create({
            baseURL,
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            timeout: 30000
        });
        
        // Add response interceptor for error handling
        this.client.interceptors.response.use(
            response => response,
            error => {
                if (error.response?.status === 401) {
                    // Handle token refresh
                    return this.refreshToken().then(() => {
                        return this.client.request(error.config);
                    });
                }
                throw error;
            }
        );
    }
    
    async getEmployees(filters = {}) {
        try {
            const response = await this.client.get('/employees', {
                params: filters
            });
            return response.data.data;
        } catch (error) {
            console.error('Error fetching employees:', error.response?.data);
            throw error;
        }
    }
    
    async createPromotionRequest(requestData) {
        try {
            const response = await this.client.post('/promotion-requests', requestData);
            return response.data.data;
        } catch (error) {
            console.error('Error creating promotion request:', error.response?.data);
            throw error;
        }
    }
    
    async uploadDocument(file, folder) {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('folder', folder);
        
        try {
            const response = await this.client.post('/files/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            return response.data.data.objectKey;
        } catch (error) {
            console.error('Error uploading document:', error.response?.data);
            throw error;
        }
    }
}

// Usage
const csms = new CSMSClient('https://csms.zanzibar.gov.tz/api', 'your_token');

// Get active employees
csms.getEmployees({ status: 'ACTIVE', institutionId: 5 })
    .then(employees => {
        console.log(`Found ${employees.content.length} employees`);
    })
    .catch(error => {
        console.error('Integration failed:', error);
    });
```

### 2. HRIMS Integration Example

```python
import requests
import schedule
import time
from datetime import datetime, timedelta

class HRIMSSync:
    def __init__(self, csms_url, hrims_url, csms_token, hrims_token):
        self.csms_url = csms_url
        self.hrims_url = hrims_url
        self.csms_headers = {'Authorization': f'Bearer {csms_token}'}
        self.hrims_headers = {'Authorization': f'Bearer {hrims_token}'}
    
    def sync_employees(self):
        """Bidirectional sync between CSMS and HRIMS"""
        print(f"Starting employee sync at {datetime.now()}")
        
        # Get recent updates from CSMS
        since_date = datetime.now() - timedelta(hours=1)
        csms_updates = self.get_csms_updates(since_date)
        
        # Push updates to HRIMS
        for update in csms_updates:
            self.push_to_hrims(update)
        
        # Get updates from HRIMS
        hrims_updates = self.get_hrims_updates(since_date)
        
        # Push updates to CSMS
        for update in hrims_updates:
            self.push_to_csms(update)
        
        print(f"Sync completed. Processed {len(csms_updates + hrims_updates)} updates")
    
    def get_csms_updates(self, since_date):
        """Get employee updates from CSMS"""
        url = f"{self.csms_url}/employees"
        params = {
            'updatedAfter': since_date.isoformat(),
            'size': 1000
        }
        
        response = requests.get(url, headers=self.csms_headers, params=params)
        response.raise_for_status()
        
        return response.json()['data']['content']
    
    def push_to_hrims(self, employee_data):
        """Push employee data to HRIMS"""
        url = f"{self.hrims_url}/employees/sync"
        
        # Transform CSMS data to HRIMS format
        hrims_data = self.transform_csms_to_hrims(employee_data)
        
        response = requests.post(url, json=hrims_data, headers=self.hrims_headers)
        
        if response.status_code == 200:
            print(f"Successfully synced employee {employee_data['payrollNumber']} to HRIMS")
        else:
            print(f"Failed to sync employee {employee_data['payrollNumber']}: {response.text}")

# Schedule sync every hour
sync = HRIMSSync(
    'https://csms.zanzibar.gov.tz/api',
    'https://hrims.zanzibar.gov.tz/api',
    'csms_token',
    'hrims_token'
)

schedule.every().hour.do(sync.sync_employees)

while True:
    schedule.run_pending()
    time.sleep(60)
```

### 3. Webhook Integration Example

```javascript
const express = require('express');
const crypto = require('crypto');
const app = express();

app.use(express.json());

// Webhook endpoint
app.post('/webhooks/csms', (req, res) => {
    const signature = req.headers['x-csms-signature'];
    const payload = JSON.stringify(req.body);
    const secret = process.env.WEBHOOK_SECRET;
    
    // Verify webhook signature
    const expectedSignature = crypto
        .createHmac('sha256', secret)
        .update(payload)
        .digest('hex');
    
    if (signature !== `sha256=${expectedSignature}`) {
        return res.status(401).send('Invalid signature');
    }
    
    // Process webhook event
    const { event, data } = req.body;
    
    switch (event) {
        case 'REQUEST_APPROVED':
            handleRequestApproved(data);
            break;
        case 'EMPLOYEE_UPDATED':
            handleEmployeeUpdated(data);
            break;
        case 'COMPLAINT_SUBMITTED':
            handleComplaintSubmitted(data);
            break;
        default:
            console.log(`Unhandled event: ${event}`);
    }
    
    res.status(200).send('OK');
});

function handleRequestApproved(data) {
    console.log(`Request ${data.requestId} approved for employee ${data.employeeId}`);
    
    // Update local system
    updateLocalSystem(data);
    
    // Send notification
    sendNotification(data);
}

app.listen(3000, () => {
    console.log('Webhook server listening on port 3000');
});
```

---

## Testing & Monitoring

### Testing Endpoints

#### Health Check
```http
GET /api/health
```

#### Connection Test
```http
GET /api/test-connection
```

#### Authentication Test
```http
GET /api/status
Authorization: Bearer {token}
```

### Monitoring Integration

#### System Metrics
```http
GET /api/analytics/health
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "status": "HEALTHY",
    "uptime": 86400,
    "database": {
      "status": "CONNECTED",
      "activeConnections": 15,
      "responseTime": 12
    },
    "api": {
      "requestsPerMinute": 150,
      "averageResponseTime": 200,
      "errorRate": 0.05
    },
    "storage": {
      "status": "AVAILABLE",
      "usedSpace": "45GB",
      "totalSpace": "100GB"
    }
  }
}
```

### Integration Testing Script

```bash
#!/bin/bash

# CSMS Integration Test Script
BASE_URL="https://csms.zanzibar.gov.tz/api"
TOKEN="your_test_token"

echo "Testing CSMS Integration..."

# Test 1: Health Check
echo "1. Testing health endpoint..."
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/health"

# Test 2: Authentication
echo "2. Testing authentication..."
curl -s -H "Authorization: Bearer $TOKEN" -o /dev/null -w "%{http_code}" "$BASE_URL/status"

# Test 3: Employee Data Access
echo "3. Testing employee data access..."
curl -s -H "Authorization: Bearer $TOKEN" -o /dev/null -w "%{http_code}" "$BASE_URL/employees?size=1"

# Test 4: Request Creation
echo "4. Testing request creation..."
curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"test": true}' \
  -o /dev/null -w "%{http_code}" \
  "$BASE_URL/test/request"

echo "Integration tests completed."
```

### Error Monitoring

Implement logging for integration monitoring:
```python
import logging
import requests
from datetime import datetime

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('csms_integration.log'),
        logging.StreamHandler()
    ]
)

logger = logging.getLogger('CSMS_Integration')

class CSMSMonitor:
    def __init__(self, base_url, token):
        self.base_url = base_url
        self.token = token
        
    def monitor_api_health(self):
        """Monitor API health and log issues"""
        try:
            response = requests.get(
                f"{self.base_url}/health",
                timeout=10
            )
            
            if response.status_code == 200:
                logger.info("CSMS API health check passed")
                return True
            else:
                logger.error(f"CSMS API health check failed: {response.status_code}")
                return False
                
        except requests.exceptions.Timeout:
            logger.error("CSMS API health check timed out")
            return False
        except requests.exceptions.ConnectionError:
            logger.error("CSMS API connection error")
            return False
        except Exception as e:
            logger.error(f"CSMS API health check error: {str(e)}")
            return False
```

---

## Support & Contact

### Technical Support

**Integration Support Team**  
- **Email:** integration-support@cscs.zanzibar.gov.tz
- **Phone:** +255 24 223 1006 (Ext. 150)
- **Office Hours:** Monday - Friday, 8:00 AM - 5:00 PM (EAT)

### Emergency Support
- **24/7 Emergency:** +255 777 123 456
- **Critical Issues:** critical-support@cscs.zanzibar.gov.tz

### Documentation & Resources

- **API Documentation:** https://docs.csms.zanzibar.gov.tz
- **Developer Portal:** https://developers.csms.zanzibar.gov.tz
- **Status Page:** https://status.csms.zanzibar.gov.tz
- **Change Log:** https://changelog.csms.zanzibar.gov.tz

### Integration Request Process

1. **Initial Contact**
   - Email integration-support@cscs.zanzibar.gov.tz
   - Provide system details and integration requirements
   
2. **Technical Assessment**
   - Technical team reviews requirements
   - Integration approach discussion
   - Timeline and resource planning
   
3. **Credentials & Access**
   - Test environment access provisioning
   - API credentials generation
   - Security review and approval
   
4. **Development & Testing**
   - Integration development
   - Testing in sandbox environment
   - Performance and security testing
   
5. **Production Deployment**
   - Production credentials provisioning
   - Go-live planning and execution
   - Post-deployment monitoring

### Service Level Agreements (SLA)

| Priority | Response Time | Resolution Time |
|----------|---------------|-----------------|
| Critical (P1) | 1 hour | 4 hours |
| High (P2) | 4 hours | 24 hours |
| Medium (P3) | 24 hours | 72 hours |
| Low (P4) | 72 hours | 1 week |

### API Versioning

- **Current Version:** v1.0
- **Supported Versions:** v1.0
- **Deprecation Policy:** 6 months notice
- **Version Header:** `X-API-Version: 1.0`

### Terms of Use

By integrating with the CSMS API, you agree to:
- Use the API only for authorized purposes
- Implement appropriate security measures
- Report security vulnerabilities responsibly
- Comply with data protection regulations
- Provide integration documentation for support

---

**Document Version:** 1.0  
**Last Updated:** July 25, 2025  
**Next Review:** October 25, 2025  

*This document is maintained by the Civil Service Commission of Zanzibar IT Department*