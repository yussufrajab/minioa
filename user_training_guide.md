# Civil Service Management System (CSMS) - User Training Guide

## Table of Contents
1. [System Overview](#system-overview)
2. [General Login & Navigation](#general-login--navigation)
3. [Role-Specific Training Materials](#role-specific-training-materials)
   - [1. ADMIN - System Administrator](#1-admin---system-administrator)
   - [2. HRO - HR Officer](#2-hro---hr-officer)
   - [3. HHRMD - Head of HR Management](#3-hhrmd---head-of-hr-management)
   - [4. HRMO - HR Management Officer](#4-hrmo---hr-management-officer)
   - [5. DO - Disciplinary Officer](#5-do---disciplinary-officer)
   - [6. EMPLOYEE - Basic Employee](#6-employee---basic-employee)
   - [7. CSCS - Civil Service Commission Secretary](#7-cscs---civil-service-commission-secretary)
   - [8. HRRP - HR Responsible Personnel](#8-hrrp---hr-responsible-personnel)
   - [9. PO - Planning Officer](#9-po---planning-officer)
4. [Common Features Training](#common-features-training)
5. [Troubleshooting & Support](#troubleshooting--support)

---

## System Overview

The Civil Service Management System (CSMS) is a comprehensive HR management platform for the Civil Service Commission of Zanzibar. It manages employee data and HR processes across all government ministries through 8 core modules:

1. **Employee Confirmation** - Processing 12-month probation confirmations
2. **Leave Without Pay (LWOP)** - Managing unpaid leave requests
3. **Promotion** - Education and performance-based promotions
4. **Cadre Change** - Employee cadre transitions
5. **Retirement** - Compulsory, voluntary, and illness-based retirement
6. **Resignation** - Employee resignation processing
7. **Service Extension** - Extending service beyond retirement
8. **Termination/Dismissal** - Employee termination and dismissal
9. **Complaints** - Employee complaint management system

---

## General Login & Navigation

### System Access
- **URL**: http://localhost:9002
- **Default Password**: `password123` (change immediately after first login)
- **Session Timeout**: 10 minutes of inactivity

### First Time Login Steps
1. Open web browser and navigate to the CSMS URL
2. Enter your username and password
3. Click "Login"
4. Change your password (strongly recommended)
5. Familiarize yourself with the dashboard

### Navigation Basics
- **Dashboard**: Main overview screen showing statistics and recent activities
- **Left Sidebar**: Access to all modules based on your role
- **Top Bar**: User profile, notifications, and logout
- **Breadcrumbs**: Shows your current location in the system

---

# Role-Specific Training Materials

## 1. ADMIN - System Administrator

### **Role Overview**
System Administrators have full access to all system features and are responsible for technical and operational management including user accounts, institutions, and system health.

### **Key Responsibilities**
- User account management (create, update, deactivate, delete)
- Institution management
- Role assignment and permissions
- System monitoring and maintenance
- Password resets and account unlocks

### **Dashboard Features**
- User statistics overview
- Institution summary
- System health indicators
- Recent admin activities
- Quick access to user and institution management

### **Main Functions**

#### **1.1 User Management**
**Location**: Dashboard → Admin → Users

**Creating New Users**:
1. Click "Add New User" button
2. Fill in required information:
   - Full Name
   - Username (unique)
   - Email
   - Phone Number
   - Institution (select from dropdown)
   - Role (ADMIN, HRO, HHRMD, HRMO, DO, EMPLOYEE, CSCS, HRRP, PO)
3. Set temporary password
4. Click "Create User"

**Managing Existing Users**:
- **Search Users**: Use search bar to find users by name, ZanID, or institution
- **Edit User**: Click edit icon to modify user details
- **Reset Password**: Click "Reset Password" to generate new temporary password
- **Deactivate User**: Toggle active status to disable user access
- **Delete User**: Permanently remove user (use with caution)

#### **1.2 Institution Management**
**Location**: Dashboard → Admin → Institutions

**Adding New Institution**:
1. Click "Add Institution" button
2. Fill institution details:
   - Institution Name
   - Email Address
   - Physical Address
   - Telephone Number
   - Vote Number
   - Vote Description
3. Click "Save Institution"

**Managing Institutions**:
- View all 41 government institutions
- Edit institution information
- Track employee count per institution
- Assign HROs to institutions

#### **1.3 System Monitoring**
**Location**: Dashboard → Admin → System Health

**Monitor**:
- System uptime and response times
- Database connection status
- Background job status
- Error logs and alerts
- User activity patterns

#### **1.4 Role Assignment**
**Best Practices**:
- **HRO**: Assign to institution-specific HR staff
- **HHRMD**: Assign to senior HR management (usually 1-2 users)
- **HRMO**: Assign to HR management officers (2-3 users)
- **DO**: Assign to disciplinary officers (1-2 users)
- **EMPLOYEE**: Assign to regular employees for complaint submission
- **CSCS**: Assign to Commission Secretary (usually 1 user)
- **HRRP**: Assign to institutional HR supervisors
- **PO**: Assign to planning officers for reporting access

### **Common Admin Tasks**

#### **Password Reset Procedure**:
1. Navigate to Users → Find user
2. Click "Reset Password"
3. System generates temporary password
4. Communicate new password to user securely
5. Advise user to change password on first login

#### **Troubleshooting User Issues**:
- **Login Problems**: Check if user account is active
- **Access Issues**: Verify correct role assignment
- **Permission Problems**: Review role-based permissions
- **Performance Issues**: Check system health dashboard

---

## 2. HRO - HR Officer

### **Role Overview**
HR Officers are institution-specific users who submit requests on behalf of employees for all modules except Complaints. They can only view and manage data related to their assigned institution.

### **Key Responsibilities**
- Submit HR requests for employees in their institution
- Upload required documents
- Track request status
- Respond to rejection feedback
- Maintain employee records for their institution

### **Dashboard Features**
- Institution-specific request statistics
- Pending actions summary
- Recent request activities
- Quick links to submit new requests

### **Access Restrictions**
- Can only see employees from their assigned institution
- Cannot view other institutions' data
- Cannot submit complaints (employee self-service)
- Cannot approve or reject requests

### **Main Functions**

#### **2.1 Employee Confirmation Requests**
**Location**: Dashboard → Confirmation

**When to Use**:
- Employee has completed 12+ months of probation
- Performance evaluations are complete
- Required documents are available

**Step-by-Step Process**:
1. Click "New Confirmation Request"
2. Search and select employee by ZanID
3. Verify probation completion date
4. Upload required documents:
   - IPA Certificate
   - Performance Appraisal Form
   - Letter of Request
5. Review information and click "Submit"

**Required Documents**:
- **IPA Certificate**: Valid certificate from Institute of Public Administration
- **Performance Appraisal**: Completed evaluation form
- **Letter of Request**: Formal confirmation request letter

#### **2.2 Leave Without Pay (LWOP) Requests**
**Location**: Dashboard → LWOP

**Eligibility Criteria**:
- Duration: 1 month to 3 years
- Maximum: 2 LWOP periods per employee
- No active bank loan guarantees

**Prohibited Reasons**:
- Employment in internal organizations
- Spouse relocation for work
- Political engagement
- Funerals outside Zanzibar
- Caring for sick family member
- Spouse studying abroad
- Spouse living outside Zanzibar
- Spouse of high-ranking officials

**Submission Process**:
1. Click "New LWOP Request"
2. Select employee
3. Set leave start and end dates
4. Enter reason (must not be prohibited)
5. Check loan guarantee status
6. Upload supporting documents
7. Submit for review

#### **2.3 Promotion Requests**
**Location**: Dashboard → Promotion

**Types of Promotion**:

**A. Education-Based Promotion**:
- For employees with new educational qualifications
- Required documents:
  - Educational Certificate
  - TCU Verification (for foreign degrees)
  - Request Letter

**B. Performance-Based Promotion**:
- For employees with excellent performance
- Required documents:
  - 3 consecutive annual appraisals
  - CSC Job Promotion Form
  - Recommendation Form

**Submission Process**:
1. Click "New Promotion Request"
2. Select employee and promotion type
3. Verify 2+ years in current position
4. Upload required documents
5. Fill promotion details
6. Submit for approval

#### **2.4 Cadre Change Requests**
**Location**: Dashboard → Cadre Change

**Requirements**:
- Educational qualifications for new cadre
- Completed Cadre Change Form
- Supporting documentation

**Process**:
1. Select employee
2. Enter current and proposed cadre
3. Upload educational certificates
4. Complete built-in cadre change form
5. Submit for HHRMD approval

#### **2.5 Retirement Requests**
**Location**: Dashboard → Retirement

**Types**:

**A. Compulsory Retirement** (Age 60):
- Request Letter
- Ardhil-Hali
- Birth Certificate

**B. Voluntary Retirement** (Before age 60):
- Request Letter
- Ardhil-Hali
- Birth Certificate
- Justification

**C. Illness-Based Retirement**:
- Request Letter
- Health Board Report
- Sick Leave Records
- Ardhil-Hali
- Birth Certificate

#### **2.6 Resignation Requests**
**Location**: Dashboard → Resignation

**Notice Types**:
- **3-Month Notice**: Standard resignation process
- **24-Hour Notice**: With payment of 3 months salary

**Required Documents**:
- Employee resignation letter
- Request letter
- Payment receipt (if 24-hour notice)

#### **2.7 Service Extension Requests**
**Location**: Dashboard → Service Extension

**For**:
- Employees nearing retirement
- Contract completion

**Requirements**:
- Service extension request letter
- Employee consent document
- Justification for extension

#### **2.8 Termination/Dismissal Requests**
**Location**: Dashboard → Termination or Dashboard → Dismissal

**Termination** (Probationary employees):
- Application letter
- Probation extension records
- Performance appraisal
- Warning letters

**Dismissal** (Confirmed employees):
- Investigation report
- Evidence of prior disciplinary actions
- Warning letters
- Disciplinary committee reports

### **Request Management**

#### **Tracking Requests**:
1. Use "Track Status" to monitor all submitted requests
2. Filter by status, date, or request type
3. View detailed request history
4. Download status reports

#### **Handling Rejections**:
1. Review rejection reason carefully
2. Gather additional information or documents
3. Correct identified issues
4. Resubmit request with modifications
5. Add comment explaining corrections made

#### **Document Management**:
- **File Format**: PDF only
- **File Size**: Maximum 2MB per document
- **Naming**: Use clear, descriptive filenames
- **Quality**: Ensure documents are clear and legible

### **Best Practices for HROs**

1. **Before Submitting**:
   - Verify all employee information is current
   - Ensure all required documents are complete
   - Double-check eligibility criteria
   - Review submission for accuracy

2. **Document Preparation**:
   - Scan documents at high resolution
   - Ensure all pages are included
   - Check file size limits
   - Use descriptive filenames

3. **Communication**:
   - Keep employees informed of request status
   - Respond promptly to revision requests
   - Maintain clear records of all interactions

---

## 3. HHRMD - Head of HR Management

### **Role Overview**
HHRMD has the highest approval authority in the system, responsible for reviewing and approving all types of HR requests across all institutions. This role has cross-institutional visibility and final decision-making power.

### **Key Responsibilities**
- Approve or reject all HR requests (Confirmation, Promotion, LWOP, Cadre Change, Retirement, Resignation, Service Extension)
- Handle complex complaints and appeals
- Review termination and dismissal requests
- Monitor system-wide HR activities
- Generate comprehensive reports

### **Dashboard Features**
- System-wide request statistics
- Pending approvals across all modules
- Performance metrics by institution
- Urgent actions requiring attention
- Comprehensive analytics

### **Access Level**
- View all data from all institutions
- Final approval authority for all modules
- Access to system-wide reports and analytics
- Can delegate authority when needed

### **Main Functions**

#### **3.1 Request Approval Workflow**

**Pending Requests Review**:
**Location**: Dashboard → Urgent Actions

**Daily Workflow**:
1. Review pending requests by priority/SLA
2. Click on request to view details
3. Review all submitted documents
4. Check employee history and eligibility
5. Make approval decision

**Approval Process**:
1. Click "Review" on pending request
2. Examine all documentation thoroughly
3. Verify compliance with policies
4. Choose action:
   - **Approve**: Upload signed decision letter
   - **Reject**: Provide detailed reason
   - **Return**: Request additional information

#### **3.2 Module-Specific Approvals**

**Employee Confirmation**:
- Verify 12+ months probation completion
- Review performance evaluations
- Check IPA certificate validity
- Upload signed confirmation letter

**Leave Without Pay**:
- Validate duration (1 month - 3 years)
- Verify reason is not prohibited
- Check loan guarantee status
- Consider impact on service delivery

**Promotion Requests**:
- Verify educational qualifications (TCU for foreign degrees)
- Review performance appraisals (3 years for performance-based)
- Confirm position availability in new grade
- Ensure budget allocation exists

**Cadre Changes**:
- Verify educational requirements for new cadre
- Check availability of positions
- Review justification and need
- Confirm institutional approval

**Retirement Processing**:
- Verify age and service requirements
- Process pension integration
- Coordinate with relevant departments
- Handle final clearances

**Service Extensions**:
- Evaluate business justification
- Check employee consent
- Consider succession planning
- Approve extension period

#### **3.3 Complaints Management**
**Location**: Dashboard → Complaints

**Complaint Types**:
- Unconfirmed Employee Issues
- Job-Related Complaints
- Disciplinary Matters
- Policy Violations

**Review Process**:
1. Review complaint details and evidence
2. Investigate claims if necessary
3. Coordinate with relevant departments
4. Make resolution decision
5. Document actions taken

#### **3.4 Termination & Dismissal Authority**
**Location**: Dashboard → Termination / Dismissal

**Termination Reviews**:
- Verify due process was followed
- Review investigation reports
- Ensure progressive discipline
- Check legal compliance

**Dismissal Authority**:
- Review serious misconduct cases
- Ensure thorough investigation
- Verify evidence sufficiency
- Make final dismissal decision

#### **3.5 System-Wide Monitoring**

**Performance Metrics**:
- Request processing times by institution
- Approval rates by module
- Bottlenecks identification
- SLA compliance tracking

**Institutional Analysis**:
- Compare performance across ministries
- Identify training needs
- Monitor compliance patterns
- Generate improvement recommendations

### **Advanced Features**

#### **3.6 Delegation Management**
**Location**: Dashboard → Profile → Delegation

**When to Use**:
- Extended absence or leave
- High volume periods
- Workload distribution
- Succession planning

**Setup Process**:
1. Select deputy (HRMO or another HHRMD)
2. Define delegation scope and duration
3. Set approval limits
4. Activate delegation

#### **3.7 Reports and Analytics**
**Location**: Dashboard → Reports

**Available Reports**:
- Monthly HR activity summary
- Institutional performance reports
- Compliance monitoring reports
- Employee statistics by category
- Request processing metrics

**Custom Reporting**:
- Create ad-hoc reports
- Set up automated report schedules
- Export to PDF/Excel
- Share with stakeholders

### **Decision-Making Guidelines**

#### **Approval Criteria**:
1. **Completeness**: All required documents submitted
2. **Eligibility**: Employee meets all criteria
3. **Compliance**: Request follows established policies
4. **Impact**: Consider organizational implications
5. **Precedent**: Maintain consistency in decisions

#### **Rejection Guidelines**:
- Provide clear, specific reasons
- Reference relevant policies
- Suggest corrective actions
- Allow for resubmission after corrections

---

## 4. HRMO - HR Management Officer

### **Role Overview**
HRMO works under HHRMD and has approval authority for specific modules: Confirmation, Promotion, LWOP, Cadre Change, Retirement, Resignation, and Service Extension. Cannot handle Complaints, Termination, or Dismissal requests.

### **Key Responsibilities**
- Review and approve assigned module requests
- Support HHRMD in high-volume periods
- Provide first-level review for complex cases
- Monitor processing times and SLAs
- Generate module-specific reports

### **Dashboard Features**
- Requests pending HRMO review
- Module-specific statistics
- Processing time metrics
- Recent decisions made
- Workload distribution

### **Access Level**
- Cross-institutional view for assigned modules only
- Cannot access Complaints, Termination, or Dismissal
- Limited to specific approval authority
- Can view reports for assigned modules

### **Main Functions**

#### **4.1 Request Processing**

**Daily Workflow**:
**Location**: Dashboard → Urgent Actions

1. Review requests assigned to HRMO
2. Process in order of SLA urgency
3. Apply consistent decision criteria
4. Escalate complex cases to HHRMD
5. Update request status promptly

#### **4.2 Module-Specific Processing**

**Employee Confirmation** (Authority Level: Full):
- Review probation completion
- Verify performance standards
- Process routine confirmations
- Escalate policy exceptions to HHRMD

**Promotion Requests** (Authority Level: Full):
- Verify educational qualifications
- Review performance history
- Check budget implications
- Approve within grade limitations

**LWOP Processing** (Authority Level: Full):
- Validate duration and reasons
- Check service impact
- Coordinate with payroll
- Monitor return compliance

**Cadre Changes** (Authority Level: Limited):
- Process routine cadre changes
- Verify educational requirements
- Escalate inter-departmental changes
- Coordinate with establishments

**Retirement Processing** (Authority Level: Limited):
- Handle routine age retirements
- Process voluntary retirements
- Escalate illness-based cases
- Coordinate pension processing

**Service Extensions** (Authority Level: Full):
- Review business justification
- Process routine extensions
- Monitor extension compliance
- Coordinate with departments

### **Quality Control**

#### **4.3 Review Standards**
1. **Document Verification**:
   - Check completeness and authenticity
   - Verify signatures and dates
   - Ensure proper formatting

2. **Eligibility Confirmation**:
   - Apply policy criteria consistently
   - Check employee history
   - Verify institutional approvals

3. **Decision Documentation**:
   - Provide clear rationale
   - Reference applicable policies
   - Maintain audit trail

#### **4.4 Escalation Procedures**

**When to Escalate to HHRMD**:
- Policy interpretation questions
- High-value or sensitive cases
- Precedent-setting decisions
- Inter-institutional disputes
- Budget implications over limit

**Escalation Process**:
1. Document issue clearly
2. Provide recommendation
3. Attach all relevant information
4. Set escalation priority
5. Follow up on decision

### **Performance Monitoring**

#### **4.5 SLA Management**
- **Standard Processing**: 5-7 business days
- **Complex Cases**: 10-15 business days
- **Escalated Cases**: 3-5 days after HHRMD decision

**Monitoring Tools**:
- Dashboard SLA indicators
- Overdue request alerts
- Processing time reports
- Performance metrics

---

## 5. DO - Disciplinary Officer

### **Role Overview**
Disciplinary Officers specialize in handling employee complaints, termination, and dismissal cases. They have expertise in disciplinary procedures and legal compliance.

### **Key Responsibilities**
- Review and resolve employee complaints
- Process termination requests
- Handle dismissal cases
- Conduct disciplinary investigations
- Ensure legal compliance in all actions

### **Dashboard Features**
- Pending complaints summary
- Termination/dismissal cases
- Investigation status tracker
- Resolution metrics
- Legal compliance indicators

### **Access Level**
- View complaint and disciplinary data across all institutions
- Limited to complaints, termination, and dismissal modules
- Cannot access other HR requests
- Full authority for disciplinary actions

### **Main Functions**

#### **5.1 Complaint Management**
**Location**: Dashboard → Complaints

**Complaint Categories**:
1. **Unconfirmed Employee Issues**
2. **Job-Related Complaints**
3. **Disciplinary Matters**
4. **Policy Violations**

**Review Process**:
1. **Initial Assessment**:
   - Verify complaint validity
   - Check employee credentials
   - Categorize complaint type
   - Assign priority level

2. **Investigation Phase**:
   - Gather additional evidence
   - Interview relevant parties
   - Review policies and procedures
   - Document findings

3. **Resolution**:
   - Determine appropriate action
   - Coordinate with relevant departments
   - Implement corrective measures
   - Close complaint with resolution

#### **5.2 Termination Processing**
**Location**: Dashboard → Termination

**For Probationary Employees**:

**Review Criteria**:
- Verify probationary status
- Check performance records
- Review warning letters
- Ensure due process followed

**Required Documentation**:
- Application letter for termination
- Probation extension records
- Performance appraisal
- Warning letters (verbal and written)
- Investigation reports

**Decision Process**:
1. Review complete case file
2. Verify policy compliance
3. Check procedural fairness
4. Make termination decision
5. Upload signed response letter

#### **5.3 Dismissal Processing**
**Location**: Dashboard → Dismissal

**For Confirmed Employees**:

**Grounds for Dismissal**:
- Gross misconduct
- Repeated violations
- Criminal activity
- Breach of trust
- Incompetence after improvement efforts

**Investigation Requirements**:
- Formal investigation committee
- Employee representation allowed
- Evidence documentation
- Witness statements
- Appeal process followed

**Decision Criteria**:
1. Severity of misconduct
2. Impact on service
3. Previous disciplinary record
4. Mitigating circumstances
5. Legal compliance

### **Legal Compliance**

#### **5.4 Due Process Requirements**
1. **Notice**: Employee must receive formal notice
2. **Hearing**: Right to be heard and represented
3. **Evidence**: Decision based on clear evidence
4. **Appeal**: Right to appeal decisions
5. **Documentation**: Complete record keeping

#### **5.5 Investigation Procedures**
**Standard Process**:
1. Suspend employee pending investigation (if necessary)
2. Appoint investigation committee
3. Conduct thorough investigation
4. Prepare detailed report
5. Make recommendation
6. Hold disciplinary hearing
7. Make final decision
8. Communicate outcome

### **Best Practices**

#### **5.6 Documentation Standards**
- Maintain chronological case files
- Record all interactions
- Keep evidence secure
- Document decisions rationale
- Maintain confidentiality

#### **5.7 Communication Guidelines**
- Use formal, professional language
- Provide clear explanations
- Respect confidentiality
- Follow established procedures
- Maintain objectivity

---

## 6. EMPLOYEE - Basic Employee

### **Role Overview**
Employees have limited access to the system, primarily for submitting complaints and viewing their own profile information. They cannot submit other HR requests (those are handled by their institution's HRO).

### **Key Responsibilities**
- Submit personal complaints
- View own employee profile
- Track complaint status
- Update personal information (limited)

### **Dashboard Features**
- Personal profile summary
- Submitted complaints status
- Personal notifications
- Quick complaint submission

### **Access Level**
- View only own profile and data
- Cannot see other employees' information
- Cannot submit HR requests
- Cannot access administrative functions

### **Main Functions**

#### **6.1 Complaint Submission**
**Location**: Dashboard → Submit Complaint

**Authentication Required**:
Before submitting a complaint, you must provide:
- **ZAN-ID** (Zanzibar Identification Number)
- **Payroll Number**
- **ZSSF Number** (Zanzibar Social Security Fund)

**Complaint Categories**:
1. **Unconfirmed Employees** - Issues related to employment confirmation
2. **Job-Related** - Work environment, duties, conditions
3. **Other** - General employment issues

**Submission Process**:
1. **Verify Identity**:
   - Enter your ZAN-ID accurately
   - Provide correct Payroll Number
   - Enter valid ZSSF Number
   - Click "Verify" to proceed

2. **Complete Complaint Form**:
   - Select complaint category
   - Provide detailed description
   - Specify incident date
   - Choose complaint source
   - Add respondent information (if applicable)

3. **Upload Evidence** (Optional):
   - Maximum file size: 1MB per file
   - Accepted formats: PDF, images
   - Multiple files allowed
   - Use clear, descriptive filenames

4. **Review and Submit**:
   - Review all information for accuracy
   - Check that description is clear and detailed
   - Click "Submit Complaint"
   - Note your complaint reference number

#### **6.2 Profile Management**
**Location**: Dashboard → Profile

**View Information**:
- Personal details
- Employment history
- Current position and rank
- Contact information
- Document status

**Limited Updates**:
- Contact phone number
- Contact address
- Emergency contact information
- Profile picture (with approval)

#### **6.3 Complaint Tracking**
**Location**: Dashboard → My Complaints

**Status Tracking**:
- **Pending** - Complaint submitted, awaiting review
- **Under Review** - Being investigated by DO/HHRMD
- **Resolved** - Complaint addressed and closed
- **Rejected** - Complaint dismissed with reasons

**Information Available**:
- Complaint reference number
- Submission date
- Current status
- Assigned officer
- Last update date
- Resolution summary (when complete)

### **Guidelines for Employees**

#### **6.4 Writing Effective Complaints**

**Best Practices**:
1. **Be Specific**: Provide exact dates, times, and locations
2. **Be Factual**: Stick to facts, avoid emotional language
3. **Be Clear**: Use simple, direct language
4. **Be Complete**: Include all relevant information
5. **Be Professional**: Maintain respectful tone

**Information to Include**:
- What happened (specific incident)
- When it happened (date and time)
- Where it happened (location)
- Who was involved (names and positions)
- Why you believe it's wrong
- What outcome you're seeking

#### **6.5 Evidence Guidelines**

**What to Submit**:
- Email communications
- Written documents
- Photographs (if relevant)
- Witness contact information
- Policy references

**What NOT to Submit**:
- Confidential information belonging to others
- Falsified documents
- Personal attacks or accusations
- Irrelevant personal information

### **Process Understanding**

#### **6.6 After Submission**
1. **Acknowledgment**: System generates complaint reference number
2. **Assignment**: Complaint assigned to appropriate officer (DO or HHRMD)
3. **Review**: Officer reviews complaint and evidence
4. **Investigation**: Additional information may be requested
5. **Resolution**: Decision made and communicated
6. **Follow-up**: Monitor resolution implementation

#### **6.7 Response Times**
- **Initial Review**: Within 7 business days
- **Investigation**: 14-30 days depending on complexity
- **Final Resolution**: Within 45 days maximum
- **Updates**: You'll receive notifications at each stage

### **Important Notes**

#### **6.8 Confidentiality**
- Your complaint will be handled confidentially
- Information is only shared with investigating officers
- Retaliation for filing complaints is prohibited
- Anonymous complaints are not accepted

#### **6.9 Appeals Process**
- If unsatisfied with resolution, appeal to HHRMD
- Appeals must be submitted within 14 days
- Provide additional evidence if available
- Final decisions rest with senior management

---

## 7. CSCS - Civil Service Commission Secretary

### **Role Overview**
The Civil Service Commission Secretary is the highest authority in the Commission with comprehensive oversight of all system activities. This role provides strategic oversight and monitoring of all HR processes across all institutions.

### **Key Responsibilities**
- Monitor all HHRMD, HRMO, and DO activities
- Access system-wide dashboards and reports
- View all employee profiles across institutions
- Oversee institutional and system-wide performance
- Make strategic decisions based on system data

### **Dashboard Features**
- Executive summary of all HR activities
- Institution performance comparison
- Key performance indicators (KPIs)
- Strategic metrics and trends
- System-wide alerts and notifications

### **Access Level**
- Complete system access (read-only for most functions)
- View all data across all institutions
- Access to all reports and analytics
- Strategic oversight capabilities
- No direct approval authority (oversight only)

### **Main Functions**

#### **7.1 Executive Dashboard**
**Location**: Main Dashboard

**Key Metrics Overview**:
- Total active employees across all institutions
- Pending requests by type and priority
- Processing time performance
- Approval rates by institution
- System utilization statistics

**Performance Indicators**:
- Request processing SLA compliance
- Institutional HR performance rankings
- Employee satisfaction metrics
- System efficiency measures
- Compliance indicators

#### **7.2 Activity Monitoring**
**Location**: Dashboard → Activity Monitor

**HHRMD Activity Tracking**:
- Current workload and pending decisions
- Decision patterns and approval rates
- Processing time performance
- Case complexity distribution
- Quality indicators

**HRMO Activity Tracking**:
- Module-specific performance
- Workload distribution
- Decision consistency
- Escalation patterns
- Training needs identification

**DO Activity Tracking**:
- Complaint resolution efficiency
- Investigation quality
- Disciplinary action patterns
- Legal compliance indicators
- Case outcome analysis

#### **7.3 Institutional Oversight**
**Location**: Dashboard → Institutions

**Performance Comparison**:
- Request volume by institution
- Processing efficiency metrics
- Compliance rates
- Employee satisfaction scores
- HR staff performance

**Strategic Analysis**:
- Workforce trends by ministry
- Capacity planning indicators
- Training and development needs
- Policy impact assessment
- Resource allocation insights

#### **7.4 Comprehensive Reporting**
**Location**: Dashboard → Executive Reports

**Standard Reports Available**:
1. **Monthly Executive Summary**
2. **Institutional Performance Report**
3. **HR Process Efficiency Analysis**
4. **Compliance and Audit Report**
5. **Workforce Analytics Dashboard**
6. **Strategic Planning Metrics**
7. **System Utilization Report**

**Custom Analytics**:
- Create custom report templates
- Schedule automated report delivery
- Export comprehensive datasets
- Generate strategic insights
- Trend analysis and forecasting

### **Strategic Functions**

#### **7.5 Policy Impact Analysis**
**Tools**: Advanced analytics dashboard

**Capabilities**:
- Track policy implementation effects
- Monitor compliance across institutions
- Identify policy gaps or conflicts
- Measure process effectiveness
- Generate improvement recommendations

#### **7.6 Resource Planning**
**Location**: Dashboard → Planning Analytics

**Strategic Planning Support**:
- Workforce planning data
- Capacity analysis by institution
- Skills gap identification
- Succession planning insights
- Budget impact projections

### **Navigation Guide**

#### **7.7 Dashboard Navigation**
**Left Sidebar Access**:
- **Executive Overview** - Main KPI dashboard
- **Institutional Analysis** - Ministry-wise performance
- **Staff Activity** - HHRMD/HRMO/DO monitoring
- **Process Analytics** - Workflow efficiency
- **Strategic Reports** - Comprehensive reporting
- **System Health** - Technical performance

#### **7.8 Key Workflows**

**Daily Executive Review**:
1. Review executive dashboard KPIs
2. Check urgent items requiring attention
3. Monitor staff performance indicators
4. Review institutional performance
5. Address strategic issues

**Weekly Strategic Analysis**:
1. Generate weekly performance reports
2. Analyze trends and patterns
3. Identify areas for improvement
4. Plan strategic interventions
5. Schedule stakeholder meetings

**Monthly Planning Review**:
1. Comprehensive performance analysis
2. Strategic goal progress assessment
3. Resource allocation review
4. Policy effectiveness evaluation
5. Long-term planning updates

### **Best Practices for CSCS**

#### **7.9 Strategic Oversight**
- Focus on trends rather than individual cases
- Use data to drive strategic decisions
- Monitor system-wide performance indicators
- Identify systemic issues early
- Promote continuous improvement

#### **7.10 Communication**
- Share strategic insights with stakeholders
- Provide feedback to operational staff
- Communicate policy directions clearly
- Ensure transparency in oversight
- Maintain professional relationships

---

## 8. HRRP - HR Responsible Personnel

### **Role Overview**
HR Responsible Personnel serve as supervisory HR figures within specific institutions (such as Directors of Administration or HR Managers). They supervise HROs and provide institutional oversight of HR activities.

### **Key Responsibilities**
- Supervise institutional HRO activities
- Monitor HR request status for their institution
- Access institution-specific dashboards and reports
- Track request processing by HHRMD, HRMO, and DO
- Ensure institutional HR compliance

### **Dashboard Features**
- Institution-specific HR activity summary
- HRO performance monitoring
- Request status tracking
- Institutional employee overview
- Compliance indicators

### **Access Level**
- Limited to assigned institution only
- Cannot view other institutions' data
- Supervisory oversight of HRO activities
- Read-only access to employee profiles
- Institution-specific reporting

### **Main Functions**

#### **8.1 Institutional Dashboard**
**Location**: Main Dashboard

**Key Metrics**:
- Total employees in institution
- Active requests by type
- HRO performance indicators
- Processing time statistics
- Pending actions summary

**Recent Activities**:
- Latest request submissions
- Status updates received
- Approvals and rejections
- Document uploads
- System notifications

#### **8.2 HRO Supervision**
**Location**: Dashboard → Staff Oversight

**HRO Performance Monitoring**:
- Request submission frequency
- Document quality assessment
- Response time to revisions
- Compliance with guidelines
- Training needs identification

**Support Activities**:
- Provide guidance to HRO
- Review complex cases before submission
- Ensure policy compliance
- Monitor workload distribution
- Coordinate with senior management

#### **8.3 Request Tracking**
**Location**: Dashboard → Request Monitor

**Institution Request Overview**:
- All requests by status (Pending, Approved, Rejected)
- Processing timeline tracking
- Document status verification
- Approval workflow progress
- SLA compliance monitoring

**Detailed Tracking**:
- Employee-specific request history
- Request type distribution
- Success and rejection rates
- Average processing times
- Bottleneck identification

#### **8.4 Employee Management**
**Location**: Dashboard → Employees

**Institution Employee Access**:
- View all employee profiles in institution
- Access employment history
- Review request participation
- Monitor compliance status
- Track career progression

**Limited Editing Rights**:
- Update employee contact information
- Modify reporting relationships
- Update departmental assignments
- Maintain organizational charts

### **Supervisory Functions**

#### **8.5 Quality Assurance**
**Pre-Submission Review**:
- Review HRO submissions for completeness
- Verify document quality and authenticity
- Check policy compliance
- Validate employee eligibility
- Ensure proper justification

**Process Improvement**:
- Identify common rejection reasons
- Develop improved submission practices
- Provide training to HRO staff
- Streamline internal processes
- Enhance document preparation

#### **8.6 Institutional Reporting**
**Location**: Dashboard → Institution Reports

**Available Reports**:
1. **Monthly HR Activity Report**
2. **Employee Status Summary**
3. **Request Processing Analysis**
4. **HRO Performance Report**
5. **Compliance Monitoring Report**
6. **Institutional Metrics Dashboard**

**Report Features**:
- Export to PDF/Excel
- Schedule automated delivery
- Customize reporting periods
- Filter by employee categories
- Compare with previous periods

### **Best Practices**

#### **8.7 HRO Support**
**Guidance Areas**:
- Policy interpretation and application
- Document preparation standards
- Submission timing optimization
- Revision response procedures
- Employee communication

**Training Support**:
- Regular policy updates
- System feature training
- Best practice sharing
- Error prevention techniques
- Efficiency improvement methods

#### **8.8 Strategic Planning**
**Institutional Planning**:
- Workforce planning support
- Career development tracking
- Succession planning assistance
- Skills gap identification
- Training needs assessment

**Performance Analysis**:
- Regular performance reviews
- Trend identification
- Benchmark comparisons
- Improvement opportunity identification
- Strategic recommendation development

---

## 9. PO - Planning Officer

### **Role Overview**
Planning Officers are internal users within the Civil Service Commission responsible for monitoring performance, workforce trends, and strategic HR data. They have read-only access to reports and dashboards across all CSMS modules.

### **Key Responsibilities**
- Monitor system-wide performance and workforce trends
- Generate and analyze aggregated data reports
- Support strategic planning with HR analytics
- Export reports and visual analytics
- Provide data insights for decision-making

### **Dashboard Features**
- System-wide analytics overview
- Workforce trend indicators
- Performance metrics dashboard
- Report generation interface
- Data export capabilities

### **Access Level**
- Read-only access across all institutions
- Cannot approve, submit, or modify any requests
- Full access to reports and analytics
- System-wide data visibility
- Export and download privileges

### **Main Functions**

#### **9.1 Analytics Dashboard**
**Location**: Main Dashboard

**Key Performance Indicators**:
- Total workforce across all institutions
- Request processing metrics
- Approval rates by request type
- Processing time trends
- Institutional performance comparison

**Visual Analytics**:
- Interactive charts and graphs
- Trend analysis displays
- Performance comparison matrices
- Geographic distribution maps
- Time-series analysis

#### **9.2 Workforce Analytics**
**Location**: Dashboard → Workforce Analytics

**Employee Demographics**:
- Age distribution analysis
- Gender representation metrics
- Educational qualification breakdown
- Geographic distribution
- Department and ministry allocation

**Career Progression Tracking**:
- Promotion patterns and trends
- Career advancement timelines
- Skills development indicators
- Succession planning metrics
- Retirement projection models

#### **9.3 Performance Monitoring**
**Location**: Dashboard → Performance Monitor

**System Efficiency Metrics**:
- Request processing times by type
- SLA compliance rates
- Bottleneck identification
- Resource utilization analysis
- User activity patterns

**Institutional Comparisons**:
- Performance rankings by ministry
- Best practice identification
- Efficiency benchmarking
- Capacity utilization rates
- Process improvement opportunities

#### **9.4 Report Generation**
**Location**: Dashboard → Reports

**Standard Reports Available**:
1. **Workforce Demographics Report**
2. **HR Process Efficiency Analysis**
3. **Institutional Performance Comparison**
4. **Career Progression Analytics**
5. **System Utilization Report**
6. **Strategic Planning Dashboard**
7. **Compliance Monitoring Report**
8. **Trend Analysis Summary**
9. **Resource Allocation Analysis**
10. **Performance Benchmark Report**

**Custom Report Builder**:
- Drag-and-drop interface
- Flexible date range selection
- Multi-dimensional data filtering
- Custom visualization options
- Automated report scheduling

### **Advanced Analytics**

#### **9.5 Trend Analysis**
**Location**: Dashboard → Trends

**Analytical Capabilities**:
- Historical data comparison
- Predictive trend modeling
- Seasonal pattern identification
- Growth rate calculations
- Forecast projections

**Strategic Insights**:
- Workforce planning projections
- Retirement wave analysis
- Skills gap forecasting
- Capacity planning models
- Resource requirement predictions

#### **9.6 Data Export Functions**
**Location**: All report sections

**Export Options**:
- **PDF**: Professional formatted reports
- **Excel**: Raw data for further analysis
- **CSV**: Data interchange format
- **PowerPoint**: Presentation-ready charts
- **Interactive Dashboards**: Web-based sharing

**Automated Delivery**:
- Schedule regular report delivery
- Email distribution lists
- Automated data updates
- Real-time dashboard sharing
- Alert-based notifications

### **Strategic Planning Support**

#### **9.7 Planning Analytics**
**Location**: Dashboard → Planning

**Workforce Planning Tools**:
- Demographic projection models
- Skills inventory analysis
- Succession planning matrices
- Capacity planning calculators
- Resource allocation optimizers

**Decision Support**:
- Evidence-based policy recommendations
- Impact assessment modeling
- Cost-benefit analysis tools
- Risk assessment frameworks
- Performance improvement strategies

#### **9.8 Data Quality Management**
**Best Practices**:
- Regular data accuracy validation
- Trend anomaly identification
- Data consistency checking
- Report quality assurance
- User feedback integration

### **Navigation and Usage**

#### **9.9 Dashboard Navigation**
**Primary Sections**:
- **Overview**: System-wide KPI dashboard
- **Workforce**: Employee analytics and demographics
- **Performance**: Process and efficiency metrics
- **Trends**: Historical and predictive analysis
- **Reports**: Comprehensive reporting tools
- **Export**: Data download and sharing options

#### **9.10 Daily Workflow**
**Routine Activities**:
1. Review system-wide performance indicators
2. Monitor trend developments
3. Generate routine reports
4. Analyze performance anomalies
5. Prepare strategic insights

**Weekly Analysis**:
1. Comprehensive performance review
2. Trend analysis and forecasting
3. Institutional comparison analysis
4. Report preparation for stakeholders
5. Strategic recommendation development

### **Best Practices for Planning Officers**

#### **9.11 Data Analysis**
- Focus on meaningful trends over isolated data points
- Use multiple data sources for validation
- Consider external factors affecting trends
- Maintain objectivity in analysis
- Document methodology and assumptions

#### **9.12 Report Presentation**
- Use clear, professional visualizations
- Provide context for all metrics
- Include actionable recommendations
- Maintain consistent formatting
- Ensure accessibility for all audiences

---

# Common Features Training

## Dashboard Navigation

### **Universal Elements**
- **Header**: User profile, notifications, system status
- **Sidebar**: Role-based menu options
- **Main Area**: Content display and interaction
- **Footer**: System information and help links

### **Notification System**
- **Bell Icon**: Click to view recent notifications
- **Badge Numbers**: Indicate unread notification count
- **Notification Types**:
  - Request status updates
  - System announcements
  - Urgent actions required
  - Deadline reminders

## Document Management

### **File Upload Guidelines**
- **Supported Format**: PDF only
- **Maximum Size**: 2MB per file
- **Naming Convention**: Use descriptive, professional names
- **Quality Requirements**: Clear, legible scanned documents

### **Upload Process**
1. Click "Choose File" or drag-and-drop area
2. Select appropriate PDF document
3. Wait for upload completion indicator
4. Verify file appears in document list
5. Add description if required

## Search and Filter Functions

### **Employee Search**
- **By ZAN-ID**: Most accurate method
- **By Name**: Use full or partial names
- **By Payroll Number**: Unique identifier search
- **By Institution**: Filter by ministry/department

### **Request Filtering**
- **By Status**: Filter by workflow stage
- **By Date Range**: Specific time periods
- **By Request Type**: Module-specific filtering
- **By Employee**: Individual employee requests

## Status Tracking

### **Request Statuses**
- **DRAFT**: Being prepared by HRO
- **SUBMITTED**: Awaiting initial review
- **HRO_REVIEW**: Under HRO examination
- **HRMO_REVIEW**: With HRMO for decision
- **HHRMD_REVIEW**: Final review stage
- **APPROVED**: Request accepted and processed
- **REJECTED**: Request denied with reasons
- **RETURNED**: Sent back for corrections

### **Status Indicators**
- **Green**: Approved or completed
- **Yellow**: In progress or pending
- **Red**: Rejected or overdue
- **Blue**: Informational status

## Report Generation

### **Standard Reports**
All roles (except EMPLOYEE) have access to role-appropriate reports:
- Monthly activity summaries
- Status distribution reports
- Processing time analytics
- Institution-specific data

### **Export Options**
- **PDF**: Professional presentation format
- **Excel**: Data manipulation and analysis
- **Print**: Direct browser printing
- **Email**: Share reports with stakeholders

---

# Troubleshooting & Support

## Common Issues

### **Login Problems**
**Issue**: Cannot log in to system
**Solutions**:
1. Verify username and password accuracy
2. Check if account is active (contact ADMIN)
3. Try password reset if available
4. Clear browser cache and cookies
5. Try different browser or incognito mode

### **Permission Denied**
**Issue**: Cannot access certain features
**Solutions**:
1. Verify your role permissions with ADMIN
2. Check if you're accessing correct institution data
3. Ensure you haven't been delegated different permissions
4. Log out and log back in to refresh permissions

### **Document Upload Failures**
**Issue**: Cannot upload documents
**Solutions**:
1. Check file size (must be under 2MB)
2. Verify file format (PDF only)
3. Ensure file is not corrupted
4. Try renaming file to remove special characters
5. Check internet connection stability

### **Slow Performance**
**Issue**: System running slowly
**Solutions**:
1. Check internet connection speed
2. Close unnecessary browser tabs
3. Clear browser cache
4. Update browser to latest version
5. Contact ADMIN if system-wide issue

## Error Messages

### **Common Error Codes**
- **Error 401**: Authentication required - log in again
- **Error 403**: Permission denied - check role permissions
- **Error 404**: Page not found - check URL or navigation
- **Error 500**: Server error - contact system administrator

### **Validation Errors**
- **Required Field**: Complete all mandatory fields
- **Invalid Format**: Check date, number, or text format
- **File Too Large**: Reduce file size to under 2MB
- **Invalid File Type**: Use PDF format only

## Getting Help

### **Internal Support**
1. **System Administrator (ADMIN)**: Technical issues, account problems
2. **HHRMD**: Policy questions, process clarification
3. **Institution HRRP**: Local guidance and support
4. **User Manual**: This document for reference

### **Self-Help Resources**
- **Dashboard Help**: Click "?" icons for contextual help
- **Tool Tips**: Hover over elements for quick guidance
- **Status Indicators**: Color-coded status meanings
- **Navigation Breadcrumbs**: Track your location in system

### **Training Resources**
- **Role-specific sections** in this manual
- **Video tutorials** (if available)
- **Practice environment** for safe learning
- **User group meetings** for knowledge sharing

## Best Practices

### **Security**
- Never share login credentials
- Log out when finished using system
- Use strong passwords and change regularly
- Report suspicious activity to ADMIN
- Keep personal information confidential

### **Data Quality**
- Double-check all entered information
- Use clear, professional language
- Maintain accurate employee records
- Upload quality document scans
- Follow naming conventions

### **Professional Standards**
- Maintain respectful communication
- Follow established processes
- Document decisions clearly
- Respond promptly to requests
- Seek help when uncertain

---

*Civil Service Management System - Version 1.0*  
*Training Guide Last Updated: January 2025*  
*For additional support, contact your System Administrator*