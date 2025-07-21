# CSMS (Civil Service Management System) API Documentation

## Overview
This document provides comprehensive documentation for all REST API endpoints in the Civil Service Management System. The API follows RESTful conventions and uses Spring Boot with Spring Security for authentication and authorization.

## Base URL
All endpoints are relative to the base URL of the application (e.g., `http://localhost:8080`)

## Authentication
Most endpoints require authentication using JWT tokens. Include the token in the Authorization header:
```
Authorization: Bearer <jwt-token>
```

## API Endpoints by Controller

### 1. Authentication Controller (`/auth`)

#### Login
- **POST** `/auth/login`
- **Description**: Authenticate user and return JWT token
- **Request Body**: `AuthRequest` (username, password)
- **Response**: `AuthResponse` (token, refreshToken, user details)
- **Authentication**: Not required

#### Refresh Token
- **POST** `/auth/refresh`
- **Description**: Generate new access token using refresh token
- **Request Body**: `String` (refresh token)
- **Response**: `AuthResponse`
- **Authentication**: Not required

#### Logout
- **POST** `/auth/logout`
- **Description**: Logout user and invalidate token
- **Query Parameters**: `username` (required)
- **Response**: 200 OK
- **Authentication**: Not required

### 2. Employee Controller (`/employees`)

#### Get All Employees
- **GET** `/employees`
- **Description**: Get paginated list of employees
- **Query Parameters**: 
  - `page` (default: 0)
  - `size` (default: 20)
  - `sort` (default: fullName,ASC)
- **Response**: `Page<EmployeeDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO, CSCS

#### Get Employee by ID
- **GET** `/employees/{id}`
- **Description**: Get employee details by ID
- **Path Variables**: `id` (employee ID)
- **Response**: `EmployeeDto`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO, CSCS

#### Get Employee by Payroll Number
- **GET** `/employees/payroll/{payrollNumber}`
- **Description**: Get employee details by payroll number
- **Path Variables**: `payrollNumber`
- **Response**: `EmployeeDto`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

#### Get Employee by Zanzibar ID
- **GET** `/employees/zanzibar-id/{zanzibarId}`
- **Description**: Get employee details by Zanzibar ID
- **Path Variables**: `zanzibarId`
- **Response**: `EmployeeDto`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

#### Get Employees by Institution
- **GET** `/employees/institution/{institutionId}`
- **Description**: Get employees belonging to a specific institution
- **Path Variables**: `institutionId`
- **Query Parameters**: Pagination parameters
- **Response**: `Page<EmployeeDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO, HRRP

#### Get Employees by Status
- **GET** `/employees/status/{status}`
- **Description**: Get employees with a specific employment status
- **Path Variables**: `status` (EmploymentStatus enum)
- **Response**: `List<EmployeeDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO, CSCS

#### Search Employees
- **GET** `/employees/search`
- **Description**: Search employees by name, payroll number, or Zanzibar ID
- **Query Parameters**: 
  - `searchTerm` (required)
  - Pagination parameters
- **Response**: `Page<EmployeeDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

#### Get Employees Eligible for Confirmation
- **GET** `/employees/eligible-for-confirmation`
- **Description**: Get employees who are eligible for employment confirmation
- **Response**: `List<EmployeeDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO

#### Get Employees Eligible for Retirement
- **GET** `/employees/eligible-for-retirement`
- **Description**: Get employees who are eligible for retirement
- **Response**: `List<EmployeeDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO

#### Create Employee
- **POST** `/employees`
- **Description**: Create a new employee record
- **Request Body**: `EmployeeDto`
- **Response**: `EmployeeDto`
- **Required Roles**: ADMIN

#### Update Employee
- **PUT** `/employees/{id}`
- **Description**: Update an existing employee record
- **Path Variables**: `id`
- **Request Body**: `EmployeeDto`
- **Response**: `EmployeeDto`
- **Required Roles**: ADMIN, HHRMD

#### Delete Employee
- **DELETE** `/employees/{id}`
- **Description**: Soft delete an employee record
- **Path Variables**: `id`
- **Response**: 204 No Content
- **Required Roles**: ADMIN

#### Update Employment Status
- **PATCH** `/employees/{id}/status`
- **Description**: Update employee's employment status
- **Path Variables**: `id`
- **Query Parameters**: `status` (EmploymentStatus)
- **Response**: 200 OK
- **Required Roles**: ADMIN, HHRMD, HRMO

### 3. Complaint Controller (`/api/complaints`)

#### Create Complaint
- **POST** `/api/complaints`
- **Description**: Create a new complaint
- **Request Body**: `ComplaintCreateDto`
- **Response**: `ComplaintResponseDto`
- **Required Roles**: HR_OFFICER, HR_MANAGER, HEAD_HR, EMPLOYEE

#### Update Complaint
- **PUT** `/api/complaints/{id}`
- **Description**: Update an existing complaint
- **Path Variables**: `id`
- **Request Body**: `ComplaintUpdateDto`
- **Response**: `ComplaintResponseDto`
- **Required Roles**: HR_OFFICER, HR_MANAGER, HEAD_HR

#### Get Complaint by ID
- **GET** `/api/complaints/{id}`
- **Description**: Get complaint by ID
- **Path Variables**: `id`
- **Response**: `ComplaintResponseDto`
- **Required Roles**: HR_OFFICER, HR_MANAGER, HEAD_HR, EMPLOYEE

#### Get Complaint by Number
- **GET** `/api/complaints/number/{complaintNumber}`
- **Description**: Get complaint by complaint number
- **Path Variables**: `complaintNumber`
- **Response**: `ComplaintResponseDto`
- **Required Roles**: HR_OFFICER, HR_MANAGER, HEAD_HR, EMPLOYEE

#### Get All Complaints
- **GET** `/api/complaints`
- **Description**: Get all complaints with pagination
- **Query Parameters**: 
  - `page` (default: 0)
  - `size` (default: 20)
  - `sortBy` (default: submissionDate)
  - `sortDir` (default: DESC)
- **Response**: `Page<ComplaintResponseDto>`
- **Required Roles**: HR_OFFICER, HR_MANAGER, HEAD_HR

#### Get Complaints by Status
- **GET** `/api/complaints/status/{status}`
- **Description**: Get complaints by status
- **Path Variables**: `status` (ComplaintStatus)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ComplaintResponseDto>`
- **Required Roles**: HR_OFFICER, HR_MANAGER, HEAD_HR

#### Search Complaints
- **GET** `/api/complaints/search`
- **Description**: Search complaints
- **Query Parameters**: 
  - `searchTerm` (required)
  - Pagination parameters
- **Response**: `Page<ComplaintResponseDto>`
- **Required Roles**: HR_OFFICER, HR_MANAGER, HEAD_HR

#### Filter Complaints
- **GET** `/api/complaints/filter`
- **Description**: Filter complaints with multiple criteria
- **Query Parameters**: 
  - `complaintType` (optional)
  - `status` (optional)
  - `severity` (optional)
  - `complainantId` (optional)
  - `respondentId` (optional)
  - `investigatorId` (optional)
  - `startDate` (optional)
  - `endDate` (optional)
  - Pagination parameters
- **Response**: `Page<ComplaintResponseDto>`
- **Required Roles**: HR_OFFICER, HR_MANAGER, HEAD_HR

#### Assign Investigator
- **POST** `/api/complaints/{id}/assign-investigator`
- **Description**: Assign investigator to complaint
- **Path Variables**: `id`
- **Query Parameters**: `investigatorId` (required)
- **Response**: `ComplaintResponseDto`
- **Required Roles**: HR_MANAGER, HEAD_HR

#### Get Overdue Complaints
- **GET** `/api/complaints/overdue`
- **Description**: Get overdue complaints
- **Response**: List of overdue complaints
- **Required Roles**: HR_OFFICER, HR_MANAGER, HEAD_HR

### 4. Dashboard Controller (`/api/dashboard`)

#### Get Dashboard Metrics
- **GET** `/api/dashboard/metrics`
- **Description**: Get dashboard metrics based on user role
- **Response**: `DashboardMetricsDto`
- **Authentication**: Required (any authenticated user)

#### Get User Activity
- **GET** `/api/dashboard/activity`
- **Description**: Get user activity and notifications
- **Query Parameters**: `limit` (default: 10)
- **Response**: `UserActivityDto`
- **Authentication**: Required

#### Get Admin Metrics
- **GET** `/api/dashboard/admin/metrics`
- **Description**: Get comprehensive system metrics (Admin only)
- **Response**: `DashboardMetricsDto`
- **Required Roles**: ADMIN

#### Get Commissioner Overview
- **GET** `/api/dashboard/commissioner/overview`
- **Description**: Get commissioner overview dashboard
- **Response**: `DashboardMetricsDto`
- **Required Roles**: COMMISSIONER

#### Get HHRMD Pending Dashboard
- **GET** `/api/dashboard/hhrmd/pending`
- **Description**: Get HHRMD pending approvals dashboard
- **Response**: `DashboardMetricsDto`
- **Required Roles**: HEAD_HR

#### Get HRMO Workload Dashboard
- **GET** `/api/dashboard/hrmo/workload`
- **Description**: Get HRMO workload dashboard
- **Response**: `DashboardMetricsDto`
- **Required Roles**: HR_MANAGER

#### Get Employee Personal Dashboard
- **GET** `/api/dashboard/employee/personal`
- **Description**: Get employee personal dashboard
- **Response**: `DashboardMetricsDto`
- **Required Roles**: EMPLOYEE

### 5. Request Controller (`/requests`)

#### Get All Requests
- **GET** `/requests`
- **Description**: Get paginated list of all HR requests
- **Query Parameters**: Pagination parameters
- **Response**: `Page<RequestDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

#### Get Request by ID
- **GET** `/requests/{id}`
- **Description**: Get HR request details by ID
- **Path Variables**: `id`
- **Response**: `RequestDto`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO or request owner

#### Get Request by Number
- **GET** `/requests/number/{requestNumber}`
- **Description**: Get HR request details by request number
- **Path Variables**: `requestNumber`
- **Response**: `RequestDto`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

#### Get Requests by Status
- **GET** `/requests/status/{status}`
- **Description**: Get requests filtered by status
- **Path Variables**: `status` (RequestStatus)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<RequestDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

#### Get Requests by Type
- **GET** `/requests/type/{type}`
- **Description**: Get requests filtered by request type
- **Path Variables**: `type` (RequestType)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<RequestDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

#### Get My Submissions
- **GET** `/requests/my-submissions`
- **Description**: Get requests submitted by current user
- **Query Parameters**: Pagination parameters
- **Response**: `Page<RequestDto>`
- **Authentication**: Required

#### Get Pending Requests for Review
- **GET** `/requests/pending-review`
- **Description**: Get requests pending review by current user
- **Query Parameters**: Pagination parameters
- **Response**: `Page<RequestDto>`
- **Required Roles**: HHRMD, HRMO, HRO

#### Search Requests
- **GET** `/requests/search`
- **Description**: Search requests by employee name, request number, or description
- **Query Parameters**: 
  - `searchTerm` (required)
  - Pagination parameters
- **Response**: `Page<RequestDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

#### Create Request
- **POST** `/requests`
- **Description**: Create a new HR request in draft status
- **Request Body**: `RequestDto`
- **Response**: `RequestDto`
- **Required Roles**: ADMIN, HRO

#### Submit Request
- **POST** `/requests/{id}/submit`
- **Description**: Submit a draft request for processing
- **Path Variables**: `id`
- **Response**: `RequestDto`
- **Required Roles**: ADMIN, HRO or request owner

#### Process Request
- **POST** `/requests/{id}/process`
- **Description**: Approve or reject a request
- **Path Variables**: `id`
- **Request Body**: Map containing `decision` (RequestStatus) and `comments`
- **Response**: `RequestDto`
- **Required Roles**: HHRMD, HRMO, HRO

#### Cancel Request
- **POST** `/requests/{id}/cancel`
- **Description**: Cancel a pending request
- **Path Variables**: `id`
- **Request Body**: Map containing `reason`
- **Response**: 200 OK
- **Required Roles**: ADMIN or request owner

#### Get Request Workflow History
- **GET** `/requests/{id}/workflow`
- **Description**: Get the complete workflow history for a request
- **Path Variables**: `id`
- **Response**: `List<RequestWorkflowDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO or accessible to user

### 6. Promotion Request Controller (`/api/promotion-requests`)

#### Create Promotion Request
- **POST** `/api/promotion-requests`
- **Description**: Creates a new promotion request for an employee
- **Request Body**: `PromotionRequestCreateDto`
- **Response**: `PromotionRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Approve Promotion Request
- **PUT** `/api/promotion-requests/{id}/approve`
- **Description**: Approves a pending promotion request
- **Path Variables**: `id`
- **Query Parameters**: `comments` (optional)
- **Response**: `PromotionRequestResponseDto`
- **Required Roles**: HHRMD, HRMO

#### Reject Promotion Request
- **PUT** `/api/promotion-requests/{id}/reject`
- **Description**: Rejects a pending promotion request
- **Path Variables**: `id`
- **Query Parameters**: `rejectionReason` (required)
- **Response**: `PromotionRequestResponseDto`
- **Required Roles**: HHRMD, HRMO

#### Update Promotion Request
- **PUT** `/api/promotion-requests/{id}`
- **Description**: Updates a pending promotion request
- **Path Variables**: `id`
- **Request Body**: `PromotionRequestUpdateDto`
- **Response**: `PromotionRequestResponseDto`
- **Required Roles**: HRO

#### Get Promotion Request by ID
- **GET** `/api/promotion-requests/{id}`
- **Description**: Retrieves a specific promotion request by its ID
- **Path Variables**: `id`
- **Response**: `PromotionRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get All Promotion Requests
- **GET** `/api/promotion-requests`
- **Description**: Retrieves all promotion requests with pagination
- **Query Parameters**: Pagination parameters
- **Response**: `Page<PromotionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Promotion Requests by Status
- **GET** `/api/promotion-requests/status/{status}`
- **Description**: Retrieves promotion requests filtered by status
- **Path Variables**: `status` (RequestStatus)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<PromotionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Promotion Requests by Employee
- **GET** `/api/promotion-requests/employee/{employeeId}`
- **Description**: Retrieves promotion requests for a specific employee
- **Path Variables**: `employeeId`
- **Query Parameters**: Pagination parameters
- **Response**: `Page<PromotionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Promotion Requests by Type
- **GET** `/api/promotion-requests/type/{promotionType}`
- **Description**: Retrieves promotion requests filtered by promotion type
- **Path Variables**: `promotionType` (PromotionType)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<PromotionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Search Promotion Requests
- **GET** `/api/promotion-requests/search`
- **Description**: Searches promotion requests by employee name, payroll number, or position
- **Query Parameters**: 
  - `searchTerm` (required)
  - Pagination parameters
- **Response**: `Page<PromotionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Upcoming Promotions
- **GET** `/api/promotion-requests/upcoming`
- **Description**: Retrieves promotions with effective date approaching
- **Query Parameters**: `targetDate` (ISO date format)
- **Response**: `List<PromotionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Employee Promotion History
- **GET** `/api/promotion-requests/employee/{employeeId}/history`
- **Description**: Retrieves promotion history for a specific employee
- **Path Variables**: `employeeId`
- **Response**: `List<PromotionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Promotion Statistics
- **GET** `/api/promotion-requests/statistics`
- **Description**: Retrieves statistics about promotion requests
- **Response**: Map containing statistics (totalSubmitted, totalApproved, totalRejected, etc.)
- **Required Roles**: HRO, HHRMD, HRMO

### 7. Analytics Controller (`/api/reports/analytics`)

#### Get Analytics Overview
- **GET** `/api/reports/analytics/overview`
- **Description**: Get analytics overview
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `institutionId` (optional)
- **Response**: `AnalyticsReportDto`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, DO, PO

#### Get Analytics Trends
- **GET** `/api/reports/analytics/trends`
- **Description**: Get analytics trends
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `category` (required)
  - `institutionId` (optional)
- **Response**: `List<AnalyticsReportDto.TrendData>`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, DO, PO

#### Get System Performance
- **GET** `/api/reports/analytics/performance`
- **Description**: Get system performance metrics
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `institutionId` (optional)
- **Response**: `AnalyticsReportDto.SystemPerformance`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, DO, PO

#### Get Comparison Analytics
- **GET** `/api/reports/analytics/comparisons`
- **Description**: Get comparison analytics
- **Query Parameters**: 
  - `currentPeriodStart` (ISO date-time, required)
  - `currentPeriodEnd` (ISO date-time, required)
  - `previousPeriodStart` (ISO date-time, required)
  - `previousPeriodEnd` (ISO date-time, required)
  - `category` (required)
  - `institutionId` (optional)
- **Response**: `List<AnalyticsReportDto.ComparisonData>`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, DO, PO

#### Get KPIs
- **GET** `/api/reports/analytics/kpis`
- **Description**: Get key performance indicators
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `institutionId` (optional)
- **Response**: `Map<String, Object>`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, DO, PO

#### Get Bottlenecks Analysis
- **GET** `/api/reports/analytics/bottlenecks`
- **Description**: Get workflow bottlenecks analysis
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `institutionId` (optional)
- **Response**: `Map<String, Object>`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, DO, PO

#### Get Predictive Analytics
- **GET** `/api/reports/analytics/predictions`
- **Description**: Get predictive analytics
- **Query Parameters**: 
  - `predictionType` (required)
  - `horizonDays` (default: 30)
  - `institutionId` (optional)
- **Response**: `Map<String, Object>`
- **Required Roles**: ADMIN, CSCS, PO

#### Export Analytics Data
- **GET** `/api/reports/analytics/export`
- **Description**: Export analytics data
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `format` (default: CSV)
  - `categories` (required list)
  - `institutionId` (optional)
- **Response**: Export URL string
- **Required Roles**: ADMIN, CSCS, PO

### 8. Report Controller (`/api/reports`)

#### Generate Standard Report
- **POST** `/api/reports/generate`
- **Description**: Generate a standard report
- **Request Body**: `ReportRequestDto`
- **Response**: `ReportDto`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, DO, HRO, HRRP, PO

#### Generate Employee Report
- **GET** `/api/reports/employee`
- **Description**: Generate employee report
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `institutionId` (optional)
  - `department` (optional)
  - `employmentStatus` (optional)
- **Response**: `List<StandardReportDto.EmployeeReportDto>`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, HRO, HRRP, PO

#### Generate Requests Report
- **GET** `/api/reports/requests`
- **Description**: Generate requests report
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `institutionId` (optional)
  - `requestType` (optional)
  - `requestStatus` (optional)
- **Response**: `List<StandardReportDto.RequestReportDto>`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, HRO, HRRP, PO

#### Generate Complaints Report
- **GET** `/api/reports/complaints`
- **Description**: Generate complaints report
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `institutionId` (optional)
  - `complaintType` (optional)
  - `complaintStatus` (optional)
- **Response**: `List<StandardReportDto.ComplaintReportDto>`
- **Required Roles**: ADMIN, CSCS, HHRMD, DO, HRO, HRRP, PO

#### Generate Institutional Report
- **GET** `/api/reports/institutions`
- **Description**: Generate institutional report
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `institutionIds` (optional list)
- **Response**: `List<StandardReportDto.InstitutionalReportDto>`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, PO

#### Generate Workflow Report
- **GET** `/api/reports/workflow`
- **Description**: Generate workflow report
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `institutionId` (optional)
  - `requestType` (optional)
  - `currentReviewer` (optional)
- **Response**: `List<StandardReportDto.WorkflowReportDto>`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, DO, PO

#### Generate Performance Report
- **GET** `/api/reports/performance`
- **Description**: Generate performance report
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `institutionId` (optional)
  - `department` (optional)
  - `category` (optional)
- **Response**: `List<StandardReportDto.PerformanceReportDto>`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, DO, PO

#### Generate Audit Report
- **GET** `/api/reports/audit`
- **Description**: Generate audit report
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `userId` (optional)
  - `action` (optional)
  - `entityType` (optional)
- **Response**: `List<StandardReportDto.AuditReportDto>`
- **Required Roles**: ADMIN, CSCS, PO

#### Generate SLA Report
- **GET** `/api/reports/sla`
- **Description**: Generate SLA report
- **Query Parameters**: 
  - `periodStart` (ISO date-time, required)
  - `periodEnd` (ISO date-time, required)
  - `institutionId` (optional)
  - `requestType` (optional)
  - `withinSLA` (optional boolean)
- **Response**: `List<StandardReportDto.SLAReportDto>`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, DO, PO

#### Download Report
- **GET** `/api/reports/download/{filename}`
- **Description**: Download generated report file
- **Path Variables**: `filename`
- **Response**: File download
- **Authentication**: Required

#### Get Report Types
- **GET** `/api/reports/types`
- **Description**: Get available report types
- **Response**: List of report types (EMPLOYEE, REQUEST, COMPLAINT, etc.)
- **Authentication**: Required

#### Get Report Formats
- **GET** `/api/reports/formats`
- **Description**: Get available report formats
- **Response**: List of formats (PDF, EXCEL, CSV)
- **Authentication**: Required

#### Get Report Templates
- **GET** `/api/reports/templates`
- **Description**: Get available report templates
- **Response**: `List<ReportRequestDto>`
- **Required Roles**: ADMIN, CSCS, HHRMD, HRMO, DO, PO

### 9. Confirmation Request Controller (`/requests/confirmation`)

#### Get All Confirmation Requests
- **GET** `/requests/confirmation`
- **Description**: Get paginated list of confirmation requests
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ConfirmationRequestDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

#### Get Confirmation Request by ID
- **GET** `/requests/confirmation/{id}`
- **Description**: Get confirmation request details by ID
- **Path Variables**: `id`
- **Response**: `ConfirmationRequestDto`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

#### Create Confirmation Request
- **POST** `/requests/confirmation`
- **Description**: Create a new employee confirmation request
- **Request Body**: `ConfirmationRequestDto`
- **Response**: `ConfirmationRequestDto`
- **Required Roles**: ADMIN, HRO

#### Update Confirmation Request
- **PUT** `/requests/confirmation/{id}`
- **Description**: Update a confirmation request in draft status
- **Path Variables**: `id`
- **Request Body**: `ConfirmationRequestDto`
- **Response**: `ConfirmationRequestDto`
- **Required Roles**: ADMIN, HRO

#### Check Confirmation Eligibility
- **GET** `/requests/confirmation/employee/{employeeId}/eligible`
- **Description**: Check if employee is eligible for confirmation
- **Path Variables**: `employeeId`
- **Response**: `Boolean`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

#### Get Pending Confirmations
- **GET** `/requests/confirmation/pending-confirmations`
- **Description**: Get employees with upcoming confirmation eligibility
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ConfirmationRequestDto>`
- **Required Roles**: ADMIN, HHRMD, HRMO, HRO

### 10. Retirement Request Controller (`/api/retirement-requests`)

#### Create Retirement Request
- **POST** `/api/retirement-requests`
- **Description**: Creates a new retirement request for an employee
- **Request Body**: `RetirementRequestCreateDto`
- **Response**: `RetirementRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Approve Retirement Request
- **PUT** `/api/retirement-requests/{id}/approve`
- **Description**: Approves a pending retirement request
- **Path Variables**: `id`
- **Query Parameters**: `comments` (optional)
- **Response**: `RetirementRequestResponseDto`
- **Required Roles**: HHRMD, HRMO

#### Reject Retirement Request
- **PUT** `/api/retirement-requests/{id}/reject`
- **Description**: Rejects a pending retirement request
- **Path Variables**: `id`
- **Query Parameters**: `rejectionReason` (required)
- **Response**: `RetirementRequestResponseDto`
- **Required Roles**: HHRMD, HRMO

#### Update Retirement Request
- **PUT** `/api/retirement-requests/{id}`
- **Description**: Updates a pending retirement request
- **Path Variables**: `id`
- **Request Body**: `RetirementRequestUpdateDto`
- **Response**: `RetirementRequestResponseDto`
- **Required Roles**: HRO

#### Get Retirement Request by ID
- **GET** `/api/retirement-requests/{id}`
- **Description**: Retrieves a specific retirement request
- **Path Variables**: `id`
- **Response**: `RetirementRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get All Retirement Requests
- **GET** `/api/retirement-requests`
- **Description**: Retrieves all retirement requests with pagination
- **Query Parameters**: Pagination parameters
- **Response**: `Page<RetirementRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Retirement Requests by Status
- **GET** `/api/retirement-requests/status/{status}`
- **Description**: Retrieves retirement requests filtered by status
- **Path Variables**: `status` (RequestStatus)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<RetirementRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Retirement Requests by Type
- **GET** `/api/retirement-requests/type/{retirementType}`
- **Description**: Retrieves retirement requests filtered by retirement type
- **Path Variables**: `retirementType` (RetirementType: COMPULSORY, VOLUNTARY, ILLNESS)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<RetirementRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Retirement Requests by Employee
- **GET** `/api/retirement-requests/employee/{employeeId}`
- **Description**: Retrieves retirement requests for a specific employee
- **Path Variables**: `employeeId`
- **Query Parameters**: Pagination parameters
- **Response**: `Page<RetirementRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Retirement Requests by Institution
- **GET** `/api/retirement-requests/institution/{institutionId}`
- **Description**: Retrieves retirement requests for a specific institution
- **Path Variables**: `institutionId`
- **Query Parameters**: Pagination parameters
- **Response**: `Page<RetirementRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Search Retirement Requests
- **GET** `/api/retirement-requests/search`
- **Description**: Searches retirement requests by various criteria
- **Query Parameters**: 
  - `searchTerm` (required)
  - Pagination parameters
- **Response**: `Page<RetirementRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Overdue Retirement Requests
- **GET** `/api/retirement-requests/overdue`
- **Description**: Retrieves retirement requests that are overdue based on SLA
- **Query Parameters**: `slaDays` (default: 30)
- **Response**: `List<RetirementRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Upcoming Retirements
- **GET** `/api/retirement-requests/upcoming`
- **Description**: Retrieves upcoming retirements within specified days
- **Query Parameters**: `days` (default: 90)
- **Response**: `List<RetirementRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Pending Retirement Requests
- **GET** `/api/retirement-requests/pending`
- **Description**: Retrieves all pending retirement requests
- **Query Parameters**: Pagination parameters
- **Response**: `Page<RetirementRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Retirement Statistics
- **GET** `/api/retirement-requests/statistics`
- **Description**: Retrieves statistics about retirement requests
- **Response**: Map containing statistics (totalRequests, pendingRequests, approvedRequests, etc.)
- **Required Roles**: HRO, HHRMD, HRMO

### 11. Resignation Request Controller (`/api/resignation-requests`)

#### Create Resignation Request
- **POST** `/api/resignation-requests`
- **Description**: Creates a new resignation request for an employee
- **Request Body**: `ResignationRequestCreateDto`
- **Response**: `ResignationRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Approve Resignation Request
- **PUT** `/api/resignation-requests/{id}/approve`
- **Description**: Approves a pending resignation request
- **Path Variables**: `id`
- **Query Parameters**: `comments` (optional)
- **Response**: `ResignationRequestResponseDto`
- **Required Roles**: HHRMD, HRMO

#### Reject Resignation Request
- **PUT** `/api/resignation-requests/{id}/reject`
- **Description**: Rejects a pending resignation request
- **Path Variables**: `id`
- **Query Parameters**: `rejectionReason` (required)
- **Response**: `ResignationRequestResponseDto`
- **Required Roles**: HHRMD, HRMO

#### Update Resignation Request
- **PUT** `/api/resignation-requests/{id}`
- **Description**: Updates a pending resignation request
- **Path Variables**: `id`
- **Request Body**: `ResignationRequestUpdateDto`
- **Response**: `ResignationRequestResponseDto`
- **Required Roles**: HRO

#### Confirm Payment
- **PUT** `/api/resignation-requests/{id}/confirm-payment`
- **Description**: Confirms payment for 24-hour notice resignation
- **Path Variables**: `id`
- **Query Parameters**: `paymentAmount` (BigDecimal, required)
- **Response**: `ResignationRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Complete Clearance
- **PUT** `/api/resignation-requests/{id}/complete-clearance`
- **Description**: Marks clearance as completed for approved resignation
- **Path Variables**: `id`
- **Response**: `ResignationRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Complete Handover
- **PUT** `/api/resignation-requests/{id}/complete-handover`
- **Description**: Marks handover as completed for approved resignation
- **Path Variables**: `id`
- **Response**: `ResignationRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Resignation Request by ID
- **GET** `/api/resignation-requests/{id}`
- **Description**: Retrieves a specific resignation request by its ID
- **Path Variables**: `id`
- **Response**: `ResignationRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get All Resignation Requests
- **GET** `/api/resignation-requests`
- **Description**: Retrieves all resignation requests with pagination
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ResignationRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Resignation Requests by Status
- **GET** `/api/resignation-requests/status/{status}`
- **Description**: Retrieves resignation requests filtered by status
- **Path Variables**: `status` (RequestStatus)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ResignationRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Resignation Requests by Employee
- **GET** `/api/resignation-requests/employee/{employeeId}`
- **Description**: Retrieves resignation requests for a specific employee
- **Path Variables**: `employeeId`
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ResignationRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Resignation Requests by Type
- **GET** `/api/resignation-requests/type/{resignationType}`
- **Description**: Retrieves resignation requests filtered by resignation type
- **Path Variables**: `resignationType` (ResignationType: THREE_MONTH_NOTICE, TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ResignationRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Search Resignation Requests
- **GET** `/api/resignation-requests/search`
- **Description**: Searches resignation requests by employee name, payroll number, or request number
- **Query Parameters**: 
  - `searchTerm` (required)
  - Pagination parameters
- **Response**: `Page<ResignationRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Upcoming Resignations
- **GET** `/api/resignation-requests/upcoming`
- **Description**: Retrieves resignations with last working date approaching
- **Query Parameters**: `targetDate` (ISO date format)
- **Response**: `List<ResignationRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Pending Payment Requests
- **GET** `/api/resignation-requests/pending-payment`
- **Description**: Retrieves immediate resignation requests with pending payment
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ResignationRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Pending Clearance Requests
- **GET** `/api/resignation-requests/pending-clearance`
- **Description**: Retrieves approved resignations with pending clearance
- **Response**: `List<ResignationRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Pending Handover Requests
- **GET** `/api/resignation-requests/pending-handover`
- **Description**: Retrieves approved resignations with pending handover
- **Response**: `List<ResignationRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Resignation Statistics
- **GET** `/api/resignation-requests/statistics`
- **Description**: Retrieves statistics about resignation requests
- **Response**: Map containing statistics (totalSubmitted, totalApproved, totalRejected, etc.)
- **Required Roles**: HRO, HHRMD, HRMO

### 12. Termination Request Controller (`/api/termination-requests`)

#### Create Termination Request
- **POST** `/api/termination-requests`
- **Description**: Create a termination request for an unconfirmed employee
- **Request Body**: `TerminationRequestCreateDto`
- **Response**: `ApiResponse<TerminationRequestResponseDto>`
- **Authentication**: Required

#### Approve Termination Request
- **PUT** `/api/termination-requests/{requestId}/approve`
- **Description**: Approve a pending termination request
- **Path Variables**: `requestId`
- **Request Body**: `String` comments (optional)
- **Response**: `ApiResponse<TerminationRequestResponseDto>`
- **Authentication**: Required

#### Reject Termination Request
- **PUT** `/api/termination-requests/{requestId}/reject`
- **Description**: Reject a pending termination request
- **Path Variables**: `requestId`
- **Request Body**: `String` rejectionReason (required)
- **Response**: `ApiResponse<TerminationRequestResponseDto>`
- **Authentication**: Required

#### Update Termination Request
- **PUT** `/api/termination-requests/{requestId}`
- **Description**: Update a pending termination request
- **Path Variables**: `requestId`
- **Request Body**: `TerminationRequestUpdateDto`
- **Response**: `ApiResponse<TerminationRequestResponseDto>`
- **Authentication**: Required

#### Get Termination Request by ID
- **GET** `/api/termination-requests/{requestId}`
- **Description**: Retrieve a specific termination request
- **Path Variables**: `requestId`
- **Response**: `ApiResponse<TerminationRequestResponseDto>`
- **Authentication**: Required

#### Get All Termination Requests
- **GET** `/api/termination-requests`
- **Description**: Retrieve termination requests with pagination and filtering
- **Query Parameters**: 
  - `page` (default: 0)
  - `size` (default: 10)
  - `sortBy` (default: submissionDate)
  - `sortDir` (default: desc)
  - `status` (RequestStatus, optional)
  - `scenario` (TerminationScenario, optional)
  - `institutionId` (optional)
  - `search` (optional)
- **Response**: `ApiResponse<Page<TerminationRequestResponseDto>>`
- **Authentication**: Required

#### Get Termination Requests by Employee
- **GET** `/api/termination-requests/employee/{employeeId}`
- **Description**: Retrieve all termination requests for a specific employee
- **Path Variables**: `employeeId`
- **Query Parameters**: Pagination parameters
- **Response**: `ApiResponse<Page<TerminationRequestResponseDto>>`
- **Authentication**: Required

#### Get Pending Termination Requests
- **GET** `/api/termination-requests/pending`
- **Description**: Retrieve all pending termination requests
- **Query Parameters**: Pagination parameters
- **Response**: `ApiResponse<Page<TerminationRequestResponseDto>>`
- **Authentication**: Required

#### Get Termination Statistics
- **GET** `/api/termination-requests/statistics`
- **Description**: Retrieve statistics about termination requests
- **Response**: `ApiResponse<Object>`
- **Authentication**: Required

### 13. Dismissal Request Controller (`/api/dismissal-requests`)

#### Create Dismissal Request
- **POST** `/api/dismissal-requests`
- **Description**: Creates a new dismissal request for a confirmed employee
- **Request Body**: `DismissalRequestCreateDto`
- **Response**: `DismissalRequestResponseDto`
- **Required Roles**: HRO, HHRMD, DO

#### Approve Dismissal Request
- **PUT** `/api/dismissal-requests/{id}/approve`
- **Description**: Approves a pending dismissal request
- **Path Variables**: `id`
- **Query Parameters**: `comments` (optional)
- **Response**: `DismissalRequestResponseDto`
- **Required Roles**: HHRMD, DO

#### Reject Dismissal Request
- **PUT** `/api/dismissal-requests/{id}/reject`
- **Description**: Rejects a pending dismissal request
- **Path Variables**: `id`
- **Query Parameters**: `rejectionReason` (required)
- **Response**: `DismissalRequestResponseDto`
- **Required Roles**: HHRMD, DO

#### Update Dismissal Request
- **PUT** `/api/dismissal-requests/{id}`
- **Description**: Updates a pending dismissal request
- **Path Variables**: `id`
- **Request Body**: `DismissalRequestUpdateDto`
- **Response**: `DismissalRequestResponseDto`
- **Required Roles**: HRO

#### Get Dismissal Request by ID
- **GET** `/api/dismissal-requests/{id}`
- **Description**: Retrieves a specific dismissal request
- **Path Variables**: `id`
- **Response**: `DismissalRequestResponseDto`
- **Required Roles**: HRO, HHRMD, DO

#### Get All Dismissal Requests
- **GET** `/api/dismissal-requests`
- **Description**: Retrieves all dismissal requests with pagination
- **Query Parameters**: Pagination parameters
- **Response**: `Page<DismissalRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, DO

#### Get Dismissal Requests by Status
- **GET** `/api/dismissal-requests/status/{status}`
- **Description**: Retrieves dismissal requests filtered by status
- **Path Variables**: `status` (RequestStatus)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<DismissalRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, DO

#### Get Dismissal Requests by Employee
- **GET** `/api/dismissal-requests/employee/{employeeId}`
- **Description**: Retrieves dismissal requests for a specific employee
- **Path Variables**: `employeeId`
- **Query Parameters**: Pagination parameters
- **Response**: `Page<DismissalRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, DO

#### Get Dismissal Requests by Institution
- **GET** `/api/dismissal-requests/institution/{institutionId}`
- **Description**: Retrieves dismissal requests for a specific institution
- **Path Variables**: `institutionId`
- **Query Parameters**: Pagination parameters
- **Response**: `Page<DismissalRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, DO

#### Search Dismissal Requests
- **GET** `/api/dismissal-requests/search`
- **Description**: Searches dismissal requests by various criteria
- **Query Parameters**: 
  - `searchTerm` (required)
  - Pagination parameters
- **Response**: `Page<DismissalRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, DO

#### Get Overdue Dismissal Requests
- **GET** `/api/dismissal-requests/overdue`
- **Description**: Retrieves dismissal requests that are overdue based on SLA
- **Query Parameters**: `slaDays` (default: 30)
- **Response**: `List<DismissalRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, DO

#### Get Active Investigations
- **GET** `/api/dismissal-requests/active-investigations`
- **Description**: Retrieves dismissal requests with active investigations
- **Response**: `List<DismissalRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, DO

#### Get Requests with Expired Appeal Period
- **GET** `/api/dismissal-requests/appeal-period-expired`
- **Description**: Retrieves dismissal requests where appeal period has expired
- **Response**: `List<DismissalRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, DO

#### Get Requests with Appeal Period Expiring Soon
- **GET** `/api/dismissal-requests/appeal-period-expiring`
- **Description**: Retrieves dismissal requests where appeal period is expiring soon
- **Query Parameters**: `days` (default: 7)
- **Response**: `List<DismissalRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, DO

#### Get Dismissal Statistics
- **GET** `/api/dismissal-requests/statistics`
- **Description**: Retrieves statistics about dismissal requests
- **Response**: Map containing statistics
- **Required Roles**: HRO, HHRMD, DO

### 14. Cadre Change Request Controller (`/api/cadre-change-requests`)

#### Create Cadre Change Request
- **POST** `/api/cadre-change-requests`
- **Description**: Creates a new cadre change request for an employee
- **Request Body**: `CadreChangeRequestCreateDto`
- **Response**: `CadreChangeRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Approve Cadre Change Request
- **PUT** `/api/cadre-change-requests/{id}/approve`
- **Description**: Approves a pending cadre change request
- **Path Variables**: `id`
- **Query Parameters**: `comments` (optional)
- **Response**: `CadreChangeRequestResponseDto`
- **Required Roles**: HHRMD, HRMO

#### Reject Cadre Change Request
- **PUT** `/api/cadre-change-requests/{id}/reject`
- **Description**: Rejects a pending cadre change request
- **Path Variables**: `id`
- **Query Parameters**: `rejectionReason` (required)
- **Response**: `CadreChangeRequestResponseDto`
- **Required Roles**: HHRMD, HRMO

#### Update Cadre Change Request
- **PUT** `/api/cadre-change-requests/{id}`
- **Description**: Updates a pending cadre change request
- **Path Variables**: `id`
- **Request Body**: `CadreChangeRequestUpdateDto`
- **Response**: `CadreChangeRequestResponseDto`
- **Required Roles**: HRO

#### Get Cadre Change Request by ID
- **GET** `/api/cadre-change-requests/{id}`
- **Description**: Retrieves a specific cadre change request
- **Path Variables**: `id`
- **Response**: `CadreChangeRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get All Cadre Change Requests
- **GET** `/api/cadre-change-requests`
- **Description**: Retrieves all cadre change requests with pagination
- **Query Parameters**: Pagination parameters
- **Response**: `Page<CadreChangeRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Cadre Change Requests by Status
- **GET** `/api/cadre-change-requests/status/{status}`
- **Description**: Retrieves cadre change requests filtered by status
- **Path Variables**: `status` (RequestStatus)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<CadreChangeRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Cadre Change Requests by Employee
- **GET** `/api/cadre-change-requests/employee/{employeeId}`
- **Description**: Retrieves cadre change requests for a specific employee
- **Path Variables**: `employeeId`
- **Query Parameters**: Pagination parameters
- **Response**: `Page<CadreChangeRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Cadre Change Requests by Institution
- **GET** `/api/cadre-change-requests/institution/{institutionId}`
- **Description**: Retrieves cadre change requests for a specific institution
- **Path Variables**: `institutionId`
- **Query Parameters**: Pagination parameters
- **Response**: `Page<CadreChangeRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Search Cadre Change Requests
- **GET** `/api/cadre-change-requests/search`
- **Description**: Searches cadre change requests by various criteria
- **Query Parameters**: 
  - `searchTerm` (required)
  - Pagination parameters
- **Response**: `Page<CadreChangeRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Overdue Cadre Change Requests
- **GET** `/api/cadre-change-requests/overdue`
- **Description**: Retrieves cadre change requests that are overdue based on SLA
- **Query Parameters**: `slaDays` (default: 30)
- **Response**: `List<CadreChangeRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Requests Requiring TCU Verification
- **GET** `/api/cadre-change-requests/tcu-verification`
- **Description**: Retrieves cadre change requests that require TCU verification
- **Response**: `List<CadreChangeRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Requests Ready for Implementation
- **GET** `/api/cadre-change-requests/implementation`
- **Description**: Retrieves approved cadre change requests ready for implementation
- **Response**: `List<CadreChangeRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Cadre Change Statistics
- **GET** `/api/cadre-change-requests/statistics`
- **Description**: Retrieves statistics about cadre change requests
- **Response**: Map containing statistics
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Pending Cadre Change Requests
- **GET** `/api/cadre-change-requests/pending`
- **Description**: Retrieves all pending cadre change requests
- **Query Parameters**: Pagination parameters
- **Response**: `Page<CadreChangeRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

### 15. Service Extension Request Controller (`/api/service-extension-requests`)

#### Create Service Extension Request
- **POST** `/api/service-extension-requests`
- **Description**: Creates a new service extension request for an employee
- **Request Body**: `ServiceExtensionRequestCreateDto`
- **Response**: `ServiceExtensionRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Approve Service Extension Request
- **PUT** `/api/service-extension-requests/{id}/approve`
- **Description**: Approves a pending service extension request
- **Path Variables**: `id`
- **Query Parameters**: `comments` (optional)
- **Response**: `ServiceExtensionRequestResponseDto`
- **Required Roles**: HHRMD, HRMO

#### Reject Service Extension Request
- **PUT** `/api/service-extension-requests/{id}/reject`
- **Description**: Rejects a pending service extension request
- **Path Variables**: `id`
- **Query Parameters**: `rejectionReason` (required)
- **Response**: `ServiceExtensionRequestResponseDto`
- **Required Roles**: HHRMD, HRMO

#### Update Service Extension Request
- **PUT** `/api/service-extension-requests/{id}`
- **Description**: Updates a pending service extension request
- **Path Variables**: `id`
- **Request Body**: `ServiceExtensionRequestUpdateDto`
- **Response**: `ServiceExtensionRequestResponseDto`
- **Required Roles**: HRO

#### Get Service Extension Request by ID
- **GET** `/api/service-extension-requests/{id}`
- **Description**: Retrieves a specific service extension request
- **Path Variables**: `id`
- **Response**: `ServiceExtensionRequestResponseDto`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get All Service Extension Requests
- **GET** `/api/service-extension-requests`
- **Description**: Retrieves all service extension requests with pagination
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ServiceExtensionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Service Extension Requests by Status
- **GET** `/api/service-extension-requests/status/{status}`
- **Description**: Retrieves service extension requests filtered by status
- **Path Variables**: `status` (RequestStatus)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ServiceExtensionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Service Extension Requests by Employee
- **GET** `/api/service-extension-requests/employee/{employeeId}`
- **Description**: Retrieves service extension requests for a specific employee
- **Path Variables**: `employeeId`
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ServiceExtensionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Service Extension Requests by Institution
- **GET** `/api/service-extension-requests/institution/{institutionId}`
- **Description**: Retrieves service extension requests for a specific institution
- **Path Variables**: `institutionId`
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ServiceExtensionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Search Service Extension Requests
- **GET** `/api/service-extension-requests/search`
- **Description**: Searches service extension requests by various criteria
- **Query Parameters**: 
  - `searchTerm` (required)
  - Pagination parameters
- **Response**: `Page<ServiceExtensionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Overdue Service Extension Requests
- **GET** `/api/service-extension-requests/overdue`
- **Description**: Retrieves service extension requests that are overdue based on SLA
- **Query Parameters**: `slaDays` (default: 30)
- **Response**: `List<ServiceExtensionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Expiring Service Extensions
- **GET** `/api/service-extension-requests/expiring`
- **Description**: Retrieves service extensions that are expiring soon
- **Query Parameters**: `days` (default: 90)
- **Response**: `List<ServiceExtensionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Active Service Extensions
- **GET** `/api/service-extension-requests/active`
- **Description**: Retrieves currently active service extensions
- **Response**: `List<ServiceExtensionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Service Extension Requests by Extension Type
- **GET** `/api/service-extension-requests/extension-type/{extensionType}`
- **Description**: Retrieves service extension requests filtered by extension type
- **Path Variables**: `extensionType` (ServiceExtensionType)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ServiceExtensionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Service Extension Requests by Extension Status
- **GET** `/api/service-extension-requests/extension-status/{extensionStatus}`
- **Description**: Retrieves service extension requests filtered by extension status
- **Path Variables**: `extensionStatus` (ServiceExtensionStatus)
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ServiceExtensionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Service Extension Statistics
- **GET** `/api/service-extension-requests/statistics`
- **Description**: Retrieves statistics about service extension requests
- **Response**: Map containing statistics
- **Required Roles**: HRO, HHRMD, HRMO

#### Get Pending Service Extension Requests
- **GET** `/api/service-extension-requests/pending`
- **Description**: Retrieves all pending service extension requests
- **Query Parameters**: Pagination parameters
- **Response**: `Page<ServiceExtensionRequestResponseDto>`
- **Required Roles**: HRO, HHRMD, HRMO

#### Process Expiration Notifications
- **POST** `/api/service-extension-requests/process-expiration-notifications`
- **Description**: Processes and sends expiration notifications for service extensions
- **Response**: Success message
- **Required Roles**: HHRMD, HRMO

#### Update Expired Extensions
- **POST** `/api/service-extension-requests/update-expired-extensions`
- **Description**: Updates the status of expired service extensions
- **Response**: Success message
- **Required Roles**: HHRMD, HRMO

### 16. Leave Without Pay Controller (`/api/lwop`)

#### Create LWOP Request
- **POST** `/api/lwop`
- **Description**: Create a new LWOP request
- **Request Body**: `LeaveWithoutPayRequestDto`
- **Headers**: `X-User-ID` (required)
- **Response**: `LeaveWithoutPay`
- **Authentication**: Required

#### Get LWOP Request by ID
- **GET** `/api/lwop/{id}`
- **Description**: Get LWOP request by ID
- **Path Variables**: `id` (Long)
- **Response**: `LeaveWithoutPay`
- **Authentication**: Required

#### Get LWOP Requests by Employee
- **GET** `/api/lwop/employee/{employeeId}`
- **Description**: Get all LWOP requests for an employee
- **Path Variables**: `employeeId`
- **Response**: `List<LeaveWithoutPay>`
- **Authentication**: Required

#### Get Approved LWOP Requests by Employee
- **GET** `/api/lwop/employee/{employeeId}/approved`
- **Description**: Get approved LWOP requests for an employee
- **Path Variables**: `employeeId`
- **Response**: `List<LeaveWithoutPay>`
- **Authentication**: Required

#### Get Pending LWOP Requests in Period
- **GET** `/api/lwop/pending`
- **Description**: Get pending LWOP requests in a period
- **Query Parameters**: 
  - `startDate` (ISO date, required)
  - `endDate` (ISO date, required)
- **Response**: `List<LeaveWithoutPay>`
- **Authentication**: Required

#### Update LWOP Request Status
- **PUT** `/api/lwop/{id}/status`
- **Description**: Update LWOP request status
- **Path Variables**: `id` (Long)
- **Query Parameters**: `status` (RequestStatus, required)
- **Headers**: `X-User-ID` (required)
- **Response**: `LeaveWithoutPay`
- **Authentication**: Required

#### Check if Employee Exceeded Max LWOP Periods
- **GET** `/api/lwop/employee/{employeeId}/exceeded-max-periods`
- **Description**: Check if employee has exceeded maximum LWOP periods
- **Path Variables**: `employeeId`
- **Response**: `Boolean`
- **Authentication**: Required

#### Delete LWOP Request
- **DELETE** `/api/lwop/{id}`
- **Description**: Delete LWOP request
- **Path Variables**: `id` (Long)
- **Headers**: `X-User-ID` (required)
- **Response**: 204 No Content
- **Authentication**: Required

### 17. Complaint Analytics Controller (`/api/complaints/analytics`)

#### Get Complaint Analytics
- **GET** `/api/complaints/analytics`
- **Description**: Get comprehensive complaint analytics
- **Query Parameters**: 
  - `startDate` (ISO date-time, required)
  - `endDate` (ISO date-time, required)
- **Response**: `Map<String, Object>`
- **Required Roles**: HR_MANAGER, HEAD_HR, COMMISSIONER

#### Get Dashboard Metrics
- **GET** `/api/complaints/analytics/dashboard`
- **Description**: Get dashboard metrics for complaints
- **Response**: `Map<String, Long>`
- **Required Roles**: HR_OFFICER, HR_MANAGER, HEAD_HR, COMMISSIONER

### 18. System Analytics Controller (`/api/analytics`)

#### Generate System-wide Analytics
- **GET** `/api/analytics/system`
- **Description**: Generate system-wide analytics report
- **Query Parameters**: 
  - `startDate` (ISO date-time, required)
  - `endDate` (ISO date-time, required)
- **Response**: `AnalyticsReportDto`
- **Required Roles**: ADMIN, COMMISSIONER, PLANNING_OFFICER, HEAD_HR

#### Generate Institutional Analytics
- **GET** `/api/analytics/institution/{institutionId}`
- **Description**: Generate institutional analytics report
- **Path Variables**: `institutionId`
- **Query Parameters**: 
  - `startDate` (ISO date-time, required)
  - `endDate` (ISO date-time, required)
- **Response**: `AnalyticsReportDto`
- **Required Roles**: ADMIN, COMMISSIONER, PLANNING_OFFICER, HEAD_HR, HR_MANAGER, HR_RESPONSIBLE

#### Get System Health Metrics
- **GET** `/api/analytics/health`
- **Description**: Get system health metrics
- **Response**: `Map<String, Object>`
- **Required Roles**: ADMIN, COMMISSIONER

#### Get Workflow Bottlenecks
- **GET** `/api/analytics/bottlenecks`
- **Description**: Get workflow bottleneck analysis
- **Response**: `List<Map<String, Object>>`
- **Required Roles**: ADMIN, COMMISSIONER, PLANNING_OFFICER, HEAD_HR

#### Get User Productivity Metrics
- **GET** `/api/analytics/productivity`
- **Description**: Get user productivity metrics
- **Query Parameters**: 
  - `startDate` (ISO date-time, required)
  - `endDate` (ISO date-time, required)
- **Response**: `Map<String, Object>`
- **Required Roles**: ADMIN, COMMISSIONER, PLANNING_OFFICER, HEAD_HR

#### Export System Analytics
- **GET** `/api/analytics/export/system`
- **Description**: Export system analytics report
- **Query Parameters**: 
  - `startDate` (ISO date-time, required)
  - `endDate` (ISO date-time, required)
  - `format` (default: JSON)
- **Response**: `AnalyticsReportDto`
- **Required Roles**: ADMIN, COMMISSIONER, PLANNING_OFFICER

#### Export Institutional Analytics
- **GET** `/api/analytics/export/institution/{institutionId}`
- **Description**: Export institutional analytics report
- **Path Variables**: `institutionId`
- **Query Parameters**: 
  - `startDate` (ISO date-time, required)
  - `endDate` (ISO date-time, required)
  - `format` (default: JSON)
- **Response**: `AnalyticsReportDto`
- **Required Roles**: ADMIN, COMMISSIONER, PLANNING_OFFICER, HR_RESPONSIBLE

## Common Response Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **204 No Content**: Request successful with no content to return
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

## Pagination Parameters

Most list endpoints support pagination with the following parameters:
- `page`: Page number (0-based)
- `size`: Number of items per page
- `sort`: Sort field
- `direction`: Sort direction (ASC/DESC)

## Date Format

All date/time parameters should be in ISO 8601 format:
- Date: `YYYY-MM-DD`
- DateTime: `YYYY-MM-DDTHH:mm:ss`

## User Roles

The system uses the following roles for authorization:
- **ADMIN**: System administrator
- **COMMISSIONER**: Civil Service Commissioner
- **HHRMD** (HEAD_HR): Head of HR Management Department
- **HRMO** (HR_MANAGER): HR Management Officer
- **HRO** (HR_OFFICER): HR Officer
- **HRRP** (HR_RESPONSIBLE): HR Responsible Person
- **DO** (DISCIPLINARY_OFFICER): Disciplinary Officer
- **PO** (PLANNING_OFFICER): Planning Officer
- **CSCS**: Civil Service Commission Secretary
- **EMPLOYEE**: Regular employee

## Notes for Frontend Developers

1. **Authentication**: All endpoints except login require JWT authentication. Include the token in the Authorization header.

2. **Error Handling**: The API returns standardized error responses with error codes and messages.

3. **Pagination**: Use the pagination parameters to handle large datasets efficiently.

4. **Filtering**: Many endpoints support filtering by status, type, institution, etc. Use these to reduce data transfer.

5. **Search**: Search endpoints typically support partial matching on multiple fields.

6. **File Downloads**: Report download endpoints return file streams. Handle appropriately in the frontend.

7. **Real-time Updates**: Consider implementing polling or WebSocket connections for real-time dashboard updates.

8. **Date Handling**: Ensure proper timezone handling for date/time fields.

9. **Role-based UI**: Use the user's roles to show/hide UI elements based on permissions.

10. **Caching**: Consider caching frequently accessed data like employee lists and institution data.