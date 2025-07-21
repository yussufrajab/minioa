--
-- PostgreSQL database dump
--

-- Dumped from database version 16.9 (Ubuntu 16.9-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.9 (Ubuntu 16.9-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE IF EXISTS csms_db;
--
-- Name: csms_db; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE csms_db WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'C.UTF-8';


\connect csms_db

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: appeal_activities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.appeal_activities (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    activity_date timestamp(6) without time zone NOT NULL,
    activity_description text NOT NULL,
    activity_type character varying(255) NOT NULL,
    is_internal boolean,
    new_status character varying(255),
    notes text,
    previous_status character varying(255),
    appeal_id character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL,
    CONSTRAINT appeal_activities_new_status_check CHECK (((new_status)::text = ANY ((ARRAY['DRAFT'::character varying, 'SUBMITTED'::character varying, 'ACKNOWLEDGED'::character varying, 'UNDER_INVESTIGATION'::character varying, 'EVIDENCE_GATHERING'::character varying, 'HEARING_SCHEDULED'::character varying, 'HEARING_COMPLETED'::character varying, 'RESOLVED'::character varying, 'CLOSED'::character varying, 'DISMISSED'::character varying, 'ESCALATED'::character varying, 'APPEALED'::character varying, 'APPEAL_UNDER_REVIEW'::character varying, 'APPEAL_RESOLVED'::character varying, 'WITHDRAWN'::character varying, 'ASSIGNED'::character varying, 'REJECTED'::character varying, 'UNDER_REVIEW'::character varying, 'RETURNED'::character varying, 'EVIDENCE_COLLECTION'::character varying, 'PENDING_DECISION'::character varying, 'APPEAL_REVIEW'::character varying, 'APPEAL_UPHELD'::character varying, 'APPEAL_DISMISSED'::character varying])::text[]))),
    CONSTRAINT appeal_activities_previous_status_check CHECK (((previous_status)::text = ANY ((ARRAY['DRAFT'::character varying, 'SUBMITTED'::character varying, 'ACKNOWLEDGED'::character varying, 'UNDER_INVESTIGATION'::character varying, 'EVIDENCE_GATHERING'::character varying, 'HEARING_SCHEDULED'::character varying, 'HEARING_COMPLETED'::character varying, 'RESOLVED'::character varying, 'CLOSED'::character varying, 'DISMISSED'::character varying, 'ESCALATED'::character varying, 'APPEALED'::character varying, 'APPEAL_UNDER_REVIEW'::character varying, 'APPEAL_RESOLVED'::character varying, 'WITHDRAWN'::character varying, 'ASSIGNED'::character varying, 'REJECTED'::character varying, 'UNDER_REVIEW'::character varying, 'RETURNED'::character varying, 'EVIDENCE_COLLECTION'::character varying, 'PENDING_DECISION'::character varying, 'APPEAL_REVIEW'::character varying, 'APPEAL_UPHELD'::character varying, 'APPEAL_DISMISSED'::character varying])::text[])))
);


--
-- Name: appeal_evidence; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.appeal_evidence (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    content_type character varying(255),
    description character varying(255),
    evidence_hash character varying(255),
    evidence_type character varying(255) NOT NULL,
    file_name character varying(255) NOT NULL,
    file_path character varying(255) NOT NULL,
    file_size bigint,
    is_confidential boolean,
    appeal_id character varying(255) NOT NULL,
    uploaded_by character varying(255)
);


--
-- Name: audit_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.audit_logs (
    id character varying(255) NOT NULL,
    action character varying(255) NOT NULL,
    after_value text,
    before_value text,
    entity_id character varying(255),
    entity_type character varying(255),
    error_message text,
    ip_address character varying(255),
    success boolean,
    "timestamp" timestamp(6) without time zone NOT NULL,
    user_agent text,
    user_id character varying(255) NOT NULL,
    username character varying(255) NOT NULL
);


--
-- Name: cadre_change_documents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cadre_change_documents (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    content_type character varying(255),
    description character varying(255),
    document_type character varying(255) NOT NULL,
    file_name character varying(255) NOT NULL,
    file_path character varying(255) NOT NULL,
    file_size bigint,
    is_mandatory boolean,
    is_verified boolean,
    verification_notes character varying(255),
    cadre_change_request_id character varying(255) NOT NULL,
    uploaded_by character varying(255),
    verified_by character varying(255),
    CONSTRAINT cadre_change_documents_document_type_check CHECK (((document_type)::text = ANY ((ARRAY['SIGNED_LETTER'::character varying, 'CADRE_CHANGE_FORM'::character varying, 'EDUCATION_CERTIFICATE'::character varying, 'TCU_VERIFICATION'::character varying, 'PERFORMANCE_APPRAISAL'::character varying, 'TRAINING_CERTIFICATE'::character varying, 'EXPERIENCE_LETTER'::character varying, 'SUPERVISOR_RECOMMENDATION'::character varying, 'QUALIFICATION_TRANSCRIPT'::character varying, 'PROFESSIONAL_CERTIFICATE'::character varying, 'SKILLS_ASSESSMENT'::character varying, 'BUDGET_APPROVAL'::character varying, 'ADDITIONAL_SUPPORTING_DOCUMENT'::character varying])::text[])))
);


--
-- Name: cadre_change_requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cadre_change_requests (
    budgetary_implications text,
    current_cadre character varying(255) NOT NULL,
    current_salary_scale character varying(255),
    education_completion_year integer NOT NULL,
    education_level character varying(255) NOT NULL,
    effective_date date,
    hr_assessment text,
    institution_attended character varying(255),
    justification text NOT NULL,
    performance_rating character varying(255),
    proposed_cadre character varying(255) NOT NULL,
    proposed_salary_scale character varying(255),
    qualification_obtained character varying(255),
    relevant_experience text,
    skills_acquired text,
    supervisor_recommendation text,
    tcu_verification_date date,
    tcu_verification_required boolean,
    tcu_verification_status character varying(255),
    training_completed text,
    years_of_experience integer,
    id character varying(255) NOT NULL
);


--
-- Name: complaint_activities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.complaint_activities (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    activity_date timestamp(6) without time zone NOT NULL,
    activity_description text NOT NULL,
    activity_type character varying(255) NOT NULL,
    is_internal boolean,
    new_status character varying(255),
    notes text,
    previous_status character varying(255),
    complaint_id character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL,
    CONSTRAINT complaint_activities_new_status_check CHECK (((new_status)::text = ANY ((ARRAY['DRAFT'::character varying, 'SUBMITTED'::character varying, 'ACKNOWLEDGED'::character varying, 'UNDER_INVESTIGATION'::character varying, 'EVIDENCE_GATHERING'::character varying, 'HEARING_SCHEDULED'::character varying, 'HEARING_COMPLETED'::character varying, 'RESOLVED'::character varying, 'CLOSED'::character varying, 'DISMISSED'::character varying, 'ESCALATED'::character varying, 'APPEALED'::character varying, 'APPEAL_UNDER_REVIEW'::character varying, 'APPEAL_RESOLVED'::character varying, 'WITHDRAWN'::character varying, 'ASSIGNED'::character varying, 'REJECTED'::character varying, 'UNDER_REVIEW'::character varying, 'RETURNED'::character varying, 'EVIDENCE_COLLECTION'::character varying, 'PENDING_DECISION'::character varying, 'APPEAL_REVIEW'::character varying, 'APPEAL_UPHELD'::character varying, 'APPEAL_DISMISSED'::character varying])::text[]))),
    CONSTRAINT complaint_activities_previous_status_check CHECK (((previous_status)::text = ANY ((ARRAY['DRAFT'::character varying, 'SUBMITTED'::character varying, 'ACKNOWLEDGED'::character varying, 'UNDER_INVESTIGATION'::character varying, 'EVIDENCE_GATHERING'::character varying, 'HEARING_SCHEDULED'::character varying, 'HEARING_COMPLETED'::character varying, 'RESOLVED'::character varying, 'CLOSED'::character varying, 'DISMISSED'::character varying, 'ESCALATED'::character varying, 'APPEALED'::character varying, 'APPEAL_UNDER_REVIEW'::character varying, 'APPEAL_RESOLVED'::character varying, 'WITHDRAWN'::character varying, 'ASSIGNED'::character varying, 'REJECTED'::character varying, 'UNDER_REVIEW'::character varying, 'RETURNED'::character varying, 'EVIDENCE_COLLECTION'::character varying, 'PENDING_DECISION'::character varying, 'APPEAL_REVIEW'::character varying, 'APPEAL_UPHELD'::character varying, 'APPEAL_DISMISSED'::character varying])::text[])))
);


--
-- Name: complaint_appeals; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.complaint_appeals (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    actual_decision_date timestamp(6) without time zone,
    appeal_number character varying(255) NOT NULL,
    appeal_reason text NOT NULL,
    corrective_action text,
    decision_rationale text,
    decision_summary text,
    grounds_for_appeal text NOT NULL,
    is_upheld boolean,
    new_evidence text,
    review_start_date timestamp(6) without time zone,
    status character varying(255) NOT NULL,
    submission_date timestamp(6) without time zone NOT NULL,
    target_decision_date timestamp(6) without time zone,
    appellant_id character varying(255) NOT NULL,
    assigned_reviewer_id character varying(255),
    original_complaint_id character varying(255) NOT NULL,
    CONSTRAINT complaint_appeals_status_check CHECK (((status)::text = ANY ((ARRAY['DRAFT'::character varying, 'SUBMITTED'::character varying, 'ACKNOWLEDGED'::character varying, 'UNDER_INVESTIGATION'::character varying, 'EVIDENCE_GATHERING'::character varying, 'HEARING_SCHEDULED'::character varying, 'HEARING_COMPLETED'::character varying, 'RESOLVED'::character varying, 'CLOSED'::character varying, 'DISMISSED'::character varying, 'ESCALATED'::character varying, 'APPEALED'::character varying, 'APPEAL_UNDER_REVIEW'::character varying, 'APPEAL_RESOLVED'::character varying, 'WITHDRAWN'::character varying, 'ASSIGNED'::character varying, 'REJECTED'::character varying, 'UNDER_REVIEW'::character varying, 'RETURNED'::character varying, 'EVIDENCE_COLLECTION'::character varying, 'PENDING_DECISION'::character varying, 'APPEAL_REVIEW'::character varying, 'APPEAL_UPHELD'::character varying, 'APPEAL_DISMISSED'::character varying])::text[])))
);


--
-- Name: complaint_evidence; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.complaint_evidence (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    content_type character varying(255),
    description character varying(255),
    evidence_hash character varying(255),
    evidence_type character varying(255) NOT NULL,
    file_name character varying(255) NOT NULL,
    file_path character varying(255) NOT NULL,
    file_size bigint,
    is_confidential boolean,
    complaint_id character varying(255) NOT NULL,
    uploaded_by character varying(255)
);


--
-- Name: complaints; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.complaints (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    acknowledgment_date timestamp(6) without time zone,
    actual_resolution_date timestamp(6) without time zone,
    complainant_satisfaction_rating integer,
    complaint_number character varying(255) NOT NULL,
    complaint_type character varying(255) NOT NULL,
    description text NOT NULL,
    disciplinary_action text,
    evidence_description text,
    follow_up_date timestamp(6) without time zone,
    follow_up_required boolean,
    incident_date timestamp(6) without time zone,
    incident_location character varying(255),
    investigation_notes text,
    investigation_start_date timestamp(6) without time zone,
    is_anonymous boolean,
    is_confidential boolean,
    resolution_summary text,
    severity character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    submission_date timestamp(6) without time zone,
    target_resolution_date timestamp(6) without time zone,
    title character varying(255) NOT NULL,
    witness_names text,
    assigned_investigator_id character varying(255),
    complainant_id character varying(255) NOT NULL,
    respondent_id character varying(255),
    submitted_by character varying(255),
    CONSTRAINT complaints_complaint_type_check CHECK (((complaint_type)::text = ANY ((ARRAY['HARASSMENT'::character varying, 'DISCRIMINATION'::character varying, 'UNFAIR_TREATMENT'::character varying, 'WORKPLACE_SAFETY'::character varying, 'POLICY_VIOLATION'::character varying, 'MISCONDUCT'::character varying, 'ABUSE_OF_POWER'::character varying, 'CORRUPTION'::character varying, 'NEPOTISM'::character varying, 'FAVORITISM'::character varying, 'SALARY_DISPUTE'::character varying, 'PROMOTION_DISPUTE'::character varying, 'WORKING_CONDITIONS'::character varying, 'DISCIPLINARY_ACTION'::character varying, 'OTHER'::character varying])::text[]))),
    CONSTRAINT complaints_severity_check CHECK (((severity)::text = ANY ((ARRAY['LOW'::character varying, 'MEDIUM'::character varying, 'HIGH'::character varying, 'CRITICAL'::character varying])::text[]))),
    CONSTRAINT complaints_status_check CHECK (((status)::text = ANY ((ARRAY['DRAFT'::character varying, 'SUBMITTED'::character varying, 'ACKNOWLEDGED'::character varying, 'UNDER_INVESTIGATION'::character varying, 'EVIDENCE_GATHERING'::character varying, 'HEARING_SCHEDULED'::character varying, 'HEARING_COMPLETED'::character varying, 'RESOLVED'::character varying, 'CLOSED'::character varying, 'DISMISSED'::character varying, 'ESCALATED'::character varying, 'APPEALED'::character varying, 'APPEAL_UNDER_REVIEW'::character varying, 'APPEAL_RESOLVED'::character varying, 'WITHDRAWN'::character varying, 'ASSIGNED'::character varying, 'REJECTED'::character varying, 'UNDER_REVIEW'::character varying, 'RETURNED'::character varying, 'EVIDENCE_COLLECTION'::character varying, 'PENDING_DECISION'::character varying, 'APPEAL_REVIEW'::character varying, 'APPEAL_UPHELD'::character varying, 'APPEAL_DISMISSED'::character varying])::text[])))
);


--
-- Name: confirmation_requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.confirmation_requests (
    current_salary double precision,
    hr_assessment text,
    performance_rating character varying(100),
    probation_end_date date NOT NULL,
    probation_start_date date NOT NULL,
    proposed_confirmation_date date,
    proposed_salary double precision,
    supervisor_recommendation text,
    request_id character varying(255) NOT NULL
);


--
-- Name: dismissal_documents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dismissal_documents (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    confidentiality_level character varying(255),
    content_type character varying(255),
    description character varying(255),
    document_type character varying(255) NOT NULL,
    file_name character varying(255) NOT NULL,
    file_path character varying(255) NOT NULL,
    file_size bigint,
    is_mandatory boolean,
    dismissal_request_id character varying(255) NOT NULL,
    uploaded_by character varying(255),
    CONSTRAINT dismissal_documents_document_type_check CHECK (((document_type)::text = ANY ((ARRAY['INVESTIGATION_REPORT'::character varying, 'INVESTIGATION_EVIDENCE'::character varying, 'WITNESS_STATEMENTS'::character varying, 'EXPERT_REPORTS'::character varying, 'DISCIPLINARY_RECORD'::character varying, 'PRIOR_WARNING_LETTERS'::character varying, 'PREVIOUS_INCIDENTS'::character varying, 'CRIMINAL_CONVICTION_RECORD'::character varying, 'COURT_JUDGMENT'::character varying, 'POLICE_REPORT'::character varying, 'DISMISSAL_REQUEST_LETTER'::character varying, 'SHOW_CAUSE_NOTICE'::character varying, 'EMPLOYEE_RESPONSE'::character varying, 'HEARING_MINUTES'::character varying, 'HR_RECOMMENDATION'::character varying, 'CCTV_FOOTAGE'::character varying, 'AUDIO_RECORDINGS'::character varying, 'FINANCIAL_RECORDS'::character varying, 'EMAIL_CORRESPONDENCE'::character varying, 'SYSTEM_LOGS'::character varying, 'DISMISSAL_ORDER'::character varying, 'APPEAL_DOCUMENTS'::character varying, 'FINAL_CLEARANCE'::character varying, 'OTHER'::character varying])::text[])))
);


--
-- Name: dismissal_requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dismissal_requests (
    aggravating_factors text,
    appeal_period_expires timestamp(6) without time zone,
    detailed_charges text NOT NULL,
    disciplinary_history text,
    dismissal_reason character varying(255) NOT NULL,
    effective_dismissal_date date,
    employee_response text,
    final_settlement_amount double precision,
    hearing_date timestamp(6) without time zone,
    hearing_outcome text,
    hr_recommendations text,
    incident_date date,
    investigation_end_date date,
    investigation_officer character varying(255),
    investigation_start_date date,
    investigation_summary text,
    legal_advice text,
    legal_consultation boolean,
    mitigating_factors text,
    prior_warnings_count integer,
    show_cause_date timestamp(6) without time zone,
    union_notification_date timestamp(6) without time zone,
    union_response text,
    id character varying(255) NOT NULL,
    CONSTRAINT dismissal_requests_dismissal_reason_check CHECK (((dismissal_reason)::text = ANY ((ARRAY['GROSS_MISCONDUCT'::character varying, 'SERIOUS_DISCIPLINARY_OFFENSE'::character varying, 'REPEATED_MISCONDUCT'::character varying, 'FRAUD_OR_THEFT'::character varying, 'INSUBORDINATION'::character varying, 'BREACH_OF_CONFIDENTIALITY'::character varying, 'CRIMINAL_CONVICTION'::character varying, 'FALSIFICATION_OF_RECORDS'::character varying, 'ABUSE_OF_OFFICE'::character varying, 'CONFLICT_OF_INTEREST'::character varying, 'VIOLENCE_OR_HARASSMENT'::character varying, 'SUBSTANCE_ABUSE'::character varying, 'UNAUTHORIZED_ABSENCE'::character varying, 'BREACH_OF_CODE_OF_CONDUCT'::character varying, 'OTHER'::character varying])::text[])))
);


--
-- Name: employee_certificates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.employee_certificates (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    certificate_number character varying(255),
    certificate_type character varying(255) NOT NULL,
    date_obtained date,
    field_of_study character varying(255),
    file_name character varying(255),
    file_path character varying(255),
    institution_name character varying(255),
    employee_id character varying(255) NOT NULL
);


--
-- Name: employee_documents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.employee_documents (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    content_type character varying(255),
    description character varying(255),
    document_type character varying(255) NOT NULL,
    file_name character varying(255) NOT NULL,
    file_path character varying(255) NOT NULL,
    file_size bigint,
    employee_id character varying(255) NOT NULL
);


--
-- Name: employees; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.employees (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    appointment_type character varying(255),
    confirmation_date date,
    contact_address character varying(255),
    contract_type character varying(255),
    country_of_birth character varying(255),
    current_reporting_office character varying(255),
    current_workplace character varying(255),
    date_of_birth date NOT NULL,
    department character varying(255),
    employment_date date NOT NULL,
    employment_status character varying(255) NOT NULL,
    full_name character varying(255) NOT NULL,
    gender character varying(255),
    loan_guarantee_status boolean,
    ministry character varying(255),
    payroll_number character varying(255) NOT NULL,
    phone_number character varying(255),
    place_of_birth character varying(255),
    profile_image character varying(255),
    rank character varying(255),
    recent_title_date date,
    region character varying(255),
    zanzibar_id character varying(255) NOT NULL,
    zssf_number character varying(255) NOT NULL,
    institution_id character varying(255) NOT NULL,
    CONSTRAINT employees_employment_status_check CHECK (((employment_status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'CONFIRMED'::character varying, 'UNCONFIRMED'::character varying, 'ON_LEAVE'::character varying, 'RETIRED'::character varying, 'RESIGNED'::character varying, 'TERMINATED'::character varying, 'DISMISSED'::character varying])::text[])))
);


--
-- Name: institutions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.institutions (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    address character varying(255),
    email character varying(255),
    name character varying(255) NOT NULL,
    telephone character varying(255),
    vote_description character varying(255),
    vote_number character varying(255)
);


--
-- Name: leave_without_pay_requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.leave_without_pay_requests (
    has_loan_guarantee boolean,
    leave_end_date date NOT NULL,
    leave_start_date date NOT NULL,
    reason text NOT NULL,
    id character varying(255) NOT NULL,
    employee_id character varying(255)
);


--
-- Name: promotion_requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.promotion_requests (
    current_grade character varying(100) NOT NULL,
    current_position character varying(255) NOT NULL,
    current_salary double precision,
    effective_date date,
    justification text,
    performance_rating character varying(255),
    promotion_type character varying(255) NOT NULL,
    proposed_grade character varying(100) NOT NULL,
    proposed_position character varying(255) NOT NULL,
    proposed_salary double precision,
    qualifications_met text,
    supervisor_recommendation text,
    years_in_current_position integer,
    request_id character varying(255) NOT NULL,
    CONSTRAINT promotion_requests_promotion_type_check CHECK (((promotion_type)::text = ANY ((ARRAY['EDUCATIONAL'::character varying, 'PERFORMANCE'::character varying])::text[]))),
    CONSTRAINT promotion_requests_years_in_current_position_check CHECK ((years_in_current_position >= 0))
);


--
-- Name: request_documents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.request_documents (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    content_type character varying(255),
    description character varying(255),
    document_type character varying(255) NOT NULL,
    file_name character varying(255) NOT NULL,
    file_path character varying(255) NOT NULL,
    file_size bigint,
    request_id character varying(255) NOT NULL
);


--
-- Name: request_workflow; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.request_workflow (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    comments text,
    completion_date timestamp(6) without time zone,
    days_in_step integer,
    is_current_step boolean,
    required_role character varying(255) NOT NULL,
    start_date timestamp(6) without time zone,
    status character varying(255) NOT NULL,
    step_name character varying(255) NOT NULL,
    step_number integer NOT NULL,
    request_id character varying(255) NOT NULL,
    reviewer_id character varying(255),
    CONSTRAINT request_workflow_required_role_check CHECK (((required_role)::text = ANY ((ARRAY['HRO'::character varying, 'HHRMD'::character varying, 'HRMO'::character varying, 'DO'::character varying, 'EMP'::character varying, 'PO'::character varying, 'CSCS'::character varying, 'HRRP'::character varying, 'ADMIN'::character varying])::text[]))),
    CONSTRAINT request_workflow_status_check CHECK (((status)::text = ANY ((ARRAY['DRAFT'::character varying, 'SUBMITTED'::character varying, 'HRO_REVIEW'::character varying, 'HRMO_REVIEW'::character varying, 'HHRMD_REVIEW'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying, 'CANCELLED'::character varying, 'RETURNED'::character varying])::text[])))
);


--
-- Name: requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.requests (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    approval_date timestamp(6) without time zone,
    comments text,
    current_stage character varying(255),
    description text,
    due_date timestamp(6) without time zone,
    is_revised boolean,
    priority character varying(255),
    rejection_reason text,
    request_number character varying(255) NOT NULL,
    request_type character varying(255) NOT NULL,
    revised_at timestamp(6) without time zone,
    revision_number integer,
    revision_reason text,
    status character varying(255) NOT NULL,
    submission_date timestamp(6) without time zone NOT NULL,
    approver_id character varying(255),
    current_reviewer_id character varying(255),
    employee_id character varying(255) NOT NULL,
    original_request_id character varying(255),
    submitted_by character varying(255) NOT NULL,
    CONSTRAINT requests_priority_check CHECK (((priority)::text = ANY ((ARRAY['LOW'::character varying, 'NORMAL'::character varying, 'HIGH'::character varying, 'URGENT'::character varying])::text[]))),
    CONSTRAINT requests_request_type_check CHECK (((request_type)::text = ANY ((ARRAY['CONFIRMATION'::character varying, 'LWOP'::character varying, 'TERMINATION'::character varying, 'DISMISSAL'::character varying, 'COMPLAINT'::character varying, 'PROMOTION'::character varying, 'CADRE_CHANGE'::character varying, 'SERVICE_EXTENSION'::character varying, 'RETIREMENT'::character varying, 'RESIGNATION'::character varying, 'TRANSFER'::character varying])::text[]))),
    CONSTRAINT requests_status_check CHECK (((status)::text = ANY ((ARRAY['DRAFT'::character varying, 'SUBMITTED'::character varying, 'HRO_REVIEW'::character varying, 'HRMO_REVIEW'::character varying, 'HHRMD_REVIEW'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying, 'CANCELLED'::character varying, 'RETURNED'::character varying])::text[])))
);


--
-- Name: resignation_documents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.resignation_documents (
    id character varying(36) NOT NULL,
    content_type character varying(100),
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(36),
    description text,
    document_type character varying(255) NOT NULL,
    file_name character varying(255) NOT NULL,
    file_path character varying(500) NOT NULL,
    file_size bigint,
    is_active boolean NOT NULL,
    is_mandatory boolean NOT NULL,
    is_verified boolean NOT NULL,
    updated_at timestamp(6) without time zone,
    updated_by character varying(36),
    verification_notes text,
    version bigint,
    resignation_request_id character varying(255) NOT NULL,
    uploaded_by character varying(255),
    verified_by character varying(255),
    CONSTRAINT resignation_documents_document_type_check CHECK (((document_type)::text = ANY ((ARRAY['REQUEST_LETTER'::character varying, 'EMPLOYEE_RESIGNATION_LETTER'::character varying, 'PAYMENT_RECEIPT'::character varying, 'HANDOVER_NOTES'::character varying, 'CLEARANCE_FORMS'::character varying])::text[])))
);


--
-- Name: resignation_requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.resignation_requests (
    clearance_completed boolean,
    handover_completed boolean,
    last_working_date date,
    payment_amount numeric(19,2),
    payment_confirmed boolean,
    reason text,
    resignation_date date,
    resignation_type character varying(255) NOT NULL,
    id character varying(255) NOT NULL,
    CONSTRAINT resignation_requests_resignation_type_check CHECK (((resignation_type)::text = ANY ((ARRAY['THREE_MONTH_NOTICE'::character varying, 'TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT'::character varying])::text[])))
);


--
-- Name: retirement_documents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.retirement_documents (
    id character varying(36) NOT NULL,
    content_type character varying(100),
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(36),
    description text,
    document_type character varying(255) NOT NULL,
    file_name character varying(255) NOT NULL,
    file_path character varying(500) NOT NULL,
    file_size bigint,
    is_active boolean NOT NULL,
    is_mandatory boolean NOT NULL,
    is_verified boolean NOT NULL,
    updated_at timestamp(6) without time zone,
    updated_by character varying(36),
    verification_notes text,
    version bigint,
    retirement_request_id character varying(255) NOT NULL,
    uploaded_by character varying(255),
    verified_by character varying(255),
    CONSTRAINT retirement_documents_document_type_check CHECK (((document_type)::text = ANY ((ARRAY['REQUEST_LETTER'::character varying, 'ARDHIL_HALI'::character varying, 'BIRTH_CERTIFICATE'::character varying, 'HEALTH_BOARD_REPORT'::character varying, 'SICK_LEAVE_RECORDS'::character varying])::text[])))
);


--
-- Name: retirement_requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.retirement_requests (
    clearance_completed boolean,
    last_working_date date,
    pension_eligibility_confirmed boolean,
    retirement_date date,
    retirement_type character varying(255) NOT NULL,
    id character varying(255) NOT NULL,
    CONSTRAINT retirement_requests_retirement_type_check CHECK (((retirement_type)::text = ANY ((ARRAY['COMPULSORY'::character varying, 'VOLUNTARY'::character varying, 'ILLNESS'::character varying])::text[])))
);


--
-- Name: service_extension_documents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.service_extension_documents (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    content_type character varying(255),
    description character varying(500),
    document_type character varying(255) NOT NULL,
    file_name character varying(255) NOT NULL,
    file_path character varying(255) NOT NULL,
    file_size bigint,
    is_mandatory boolean,
    is_verified boolean,
    verification_notes character varying(1000),
    service_extension_request_id character varying(255) NOT NULL,
    uploaded_by character varying(255),
    verified_by character varying(255),
    CONSTRAINT service_extension_documents_document_type_check CHECK (((document_type)::text = ANY ((ARRAY['SERVICE_EXTENSION_REQUEST_LETTER'::character varying, 'EMPLOYEE_CONSENT_LETTER'::character varying])::text[])))
);


--
-- Name: service_extension_requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.service_extension_requests (
    expiration_warning_sent boolean,
    extension_duration_years integer NOT NULL,
    notification_sent_date timestamp(6) without time zone,
    retirement_eligibility_date date,
    id character varying(255) NOT NULL
);


--
-- Name: sla_trackers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sla_trackers (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    completed_at timestamp(6) without time zone,
    completed_within_sla boolean,
    due_date timestamp(6) without time zone NOT NULL,
    escalated_at timestamp(6) without time zone,
    escalation_level integer NOT NULL,
    extended_at timestamp(6) without time zone,
    extended_by character varying(255),
    extension_days integer,
    extension_justification text,
    is_breached boolean NOT NULL,
    warning_sent boolean,
    warning_sent_at timestamp(6) without time zone,
    escalated_to character varying(255),
    request_id character varying(255) NOT NULL
);


--
-- Name: termination_documents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.termination_documents (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    content_type character varying(255),
    description character varying(255),
    document_type character varying(255) NOT NULL,
    file_name character varying(255) NOT NULL,
    file_path character varying(255) NOT NULL,
    file_size bigint,
    is_mandatory boolean,
    termination_request_id character varying(255) NOT NULL,
    CONSTRAINT termination_documents_document_type_check CHECK (((document_type)::text = ANY ((ARRAY['APPLICATION_LETTER'::character varying, 'PROBATION_EXTENSION'::character varying, 'PERFORMANCE_APPRAISAL'::character varying, 'REMINDER_LETTER'::character varying, 'WARNING_LETTER'::character varying, 'MEDIA_ANNOUNCEMENT'::character varying, 'INVESTIGATION_REPORT'::character varying, 'EVIDENCE_PRIOR_DISCIPLINARY'::character varying, 'VERBAL_WARNING'::character varying, 'WRITTEN_WARNING'::character varying, 'SUMMONS_LETTER'::character varying, 'TERMINATION_REQUEST_LETTER'::character varying, 'EMPLOYEE_RESPONSE'::character varying, 'WITNESS_STATEMENT'::character varying, 'MEDICAL_REPORT'::character varying, 'HR_RECOMMENDATION'::character varying, 'FINAL_DECISION_LETTER'::character varying, 'OTHER'::character varying])::text[])))
);


--
-- Name: termination_requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.termination_requests (
    disciplinary_actions text,
    hr_recommendations text,
    incident_date date,
    investigation_summary text,
    prior_warnings_count integer,
    probation_end_date date,
    reason text,
    scenario character varying(255) NOT NULL,
    id character varying(255) NOT NULL,
    CONSTRAINT termination_requests_scenario_check CHECK (((scenario)::text = ANY ((ARRAY['UNCONFIRMED_OUT_OF_PROBATION'::character varying, 'NOT_RETURNING_AFTER_LEAVE'::character varying, 'DISCIPLINARY'::character varying, 'POOR_PERFORMANCE'::character varying, 'MEDICAL_UNFITNESS'::character varying, 'ABANDONMENT_OF_DUTY'::character varying])::text[])))
);


--
-- Name: user_delegations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_delegations (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    end_date timestamp(6) without time zone NOT NULL,
    extended_at timestamp(6) without time zone,
    extended_by character varying(255),
    extension_reason text,
    reason text,
    revocation_reason text,
    revoked_at timestamp(6) without time zone,
    revoked_by character varying(255),
    start_date timestamp(6) without time zone NOT NULL,
    delegate_id character varying(255) NOT NULL,
    delegator_id character varying(255) NOT NULL
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    created_by character varying(255),
    is_active boolean,
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    version bigint,
    email character varying(255) NOT NULL,
    failed_login_attempts integer,
    full_name character varying(255) NOT NULL,
    is_account_non_expired boolean,
    is_account_non_locked boolean,
    is_credentials_non_expired boolean,
    is_enabled boolean,
    last_login_date timestamp(6) without time zone,
    password character varying(255) NOT NULL,
    password_reset_token character varying(255),
    password_reset_token_expiry timestamp(6) without time zone,
    phone_number character varying(255),
    role character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    employee_id character varying(255),
    institution_id character varying(255),
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['HRO'::character varying, 'HHRMD'::character varying, 'HRMO'::character varying, 'DO'::character varying, 'EMP'::character varying, 'PO'::character varying, 'CSCS'::character varying, 'HRRP'::character varying, 'ADMIN'::character varying])::text[])))
);


--
-- Data for Name: appeal_activities; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.appeal_activities (id, created_at, created_by, is_active, updated_at, updated_by, version, activity_date, activity_description, activity_type, is_internal, new_status, notes, previous_status, appeal_id, user_id) FROM stdin;
\.


--
-- Data for Name: appeal_evidence; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.appeal_evidence (id, created_at, created_by, is_active, updated_at, updated_by, version, content_type, description, evidence_hash, evidence_type, file_name, file_path, file_size, is_confidential, appeal_id, uploaded_by) FROM stdin;
\.


--
-- Data for Name: audit_logs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.audit_logs (id, action, after_value, before_value, entity_id, entity_type, error_message, ip_address, success, "timestamp", user_agent, user_id, username) FROM stdin;
\.


--
-- Data for Name: cadre_change_documents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cadre_change_documents (id, created_at, created_by, is_active, updated_at, updated_by, version, content_type, description, document_type, file_name, file_path, file_size, is_mandatory, is_verified, verification_notes, cadre_change_request_id, uploaded_by, verified_by) FROM stdin;
\.


--
-- Data for Name: cadre_change_requests; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cadre_change_requests (budgetary_implications, current_cadre, current_salary_scale, education_completion_year, education_level, effective_date, hr_assessment, institution_attended, justification, performance_rating, proposed_cadre, proposed_salary_scale, qualification_obtained, relevant_experience, skills_acquired, supervisor_recommendation, tcu_verification_date, tcu_verification_required, tcu_verification_status, training_completed, years_of_experience, id) FROM stdin;
\.


--
-- Data for Name: complaint_activities; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.complaint_activities (id, created_at, created_by, is_active, updated_at, updated_by, version, activity_date, activity_description, activity_type, is_internal, new_status, notes, previous_status, complaint_id, user_id) FROM stdin;
\.


--
-- Data for Name: complaint_appeals; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.complaint_appeals (id, created_at, created_by, is_active, updated_at, updated_by, version, actual_decision_date, appeal_number, appeal_reason, corrective_action, decision_rationale, decision_summary, grounds_for_appeal, is_upheld, new_evidence, review_start_date, status, submission_date, target_decision_date, appellant_id, assigned_reviewer_id, original_complaint_id) FROM stdin;
\.


--
-- Data for Name: complaint_evidence; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.complaint_evidence (id, created_at, created_by, is_active, updated_at, updated_by, version, content_type, description, evidence_hash, evidence_type, file_name, file_path, file_size, is_confidential, complaint_id, uploaded_by) FROM stdin;
\.


--
-- Data for Name: complaints; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.complaints (id, created_at, created_by, is_active, updated_at, updated_by, version, acknowledgment_date, actual_resolution_date, complainant_satisfaction_rating, complaint_number, complaint_type, description, disciplinary_action, evidence_description, follow_up_date, follow_up_required, incident_date, incident_location, investigation_notes, investigation_start_date, is_anonymous, is_confidential, resolution_summary, severity, status, submission_date, target_resolution_date, title, witness_names, assigned_investigator_id, complainant_id, respondent_id, submitted_by) FROM stdin;
\.


--
-- Data for Name: confirmation_requests; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.confirmation_requests (current_salary, hr_assessment, performance_rating, probation_end_date, probation_start_date, proposed_confirmation_date, proposed_salary, supervisor_recommendation, request_id) FROM stdin;
\.


--
-- Data for Name: dismissal_documents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dismissal_documents (id, created_at, created_by, is_active, updated_at, updated_by, version, confidentiality_level, content_type, description, document_type, file_name, file_path, file_size, is_mandatory, dismissal_request_id, uploaded_by) FROM stdin;
\.


--
-- Data for Name: dismissal_requests; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dismissal_requests (aggravating_factors, appeal_period_expires, detailed_charges, disciplinary_history, dismissal_reason, effective_dismissal_date, employee_response, final_settlement_amount, hearing_date, hearing_outcome, hr_recommendations, incident_date, investigation_end_date, investigation_officer, investigation_start_date, investigation_summary, legal_advice, legal_consultation, mitigating_factors, prior_warnings_count, show_cause_date, union_notification_date, union_response, id) FROM stdin;
\.


--
-- Data for Name: employee_certificates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.employee_certificates (id, created_at, created_by, is_active, updated_at, updated_by, version, certificate_number, certificate_type, date_obtained, field_of_study, file_name, file_path, institution_name, employee_id) FROM stdin;
\.


--
-- Data for Name: employee_documents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.employee_documents (id, created_at, created_by, is_active, updated_at, updated_by, version, content_type, description, document_type, file_name, file_path, file_size, employee_id) FROM stdin;
\.


--
-- Data for Name: employees; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.employees (id, created_at, created_by, is_active, updated_at, updated_by, version, appointment_type, confirmation_date, contact_address, contract_type, country_of_birth, current_reporting_office, current_workplace, date_of_birth, department, employment_date, employment_status, full_name, gender, loan_guarantee_status, ministry, payroll_number, phone_number, place_of_birth, profile_image, rank, recent_title_date, region, zanzibar_id, zssf_number, institution_id) FROM stdin;
\.


--
-- Data for Name: institutions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.institutions (id, created_at, created_by, is_active, updated_at, updated_by, version, address, email, name, telephone, vote_description, vote_number) FROM stdin;
\.


--
-- Data for Name: leave_without_pay_requests; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.leave_without_pay_requests (has_loan_guarantee, leave_end_date, leave_start_date, reason, id, employee_id) FROM stdin;
\.


--
-- Data for Name: promotion_requests; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.promotion_requests (current_grade, current_position, current_salary, effective_date, justification, performance_rating, promotion_type, proposed_grade, proposed_position, proposed_salary, qualifications_met, supervisor_recommendation, years_in_current_position, request_id) FROM stdin;
\.


--
-- Data for Name: request_documents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.request_documents (id, created_at, created_by, is_active, updated_at, updated_by, version, content_type, description, document_type, file_name, file_path, file_size, request_id) FROM stdin;
\.


--
-- Data for Name: request_workflow; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.request_workflow (id, created_at, created_by, is_active, updated_at, updated_by, version, comments, completion_date, days_in_step, is_current_step, required_role, start_date, status, step_name, step_number, request_id, reviewer_id) FROM stdin;
\.


--
-- Data for Name: requests; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.requests (id, created_at, created_by, is_active, updated_at, updated_by, version, approval_date, comments, current_stage, description, due_date, is_revised, priority, rejection_reason, request_number, request_type, revised_at, revision_number, revision_reason, status, submission_date, approver_id, current_reviewer_id, employee_id, original_request_id, submitted_by) FROM stdin;
\.


--
-- Data for Name: resignation_documents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.resignation_documents (id, content_type, created_at, created_by, description, document_type, file_name, file_path, file_size, is_active, is_mandatory, is_verified, updated_at, updated_by, verification_notes, version, resignation_request_id, uploaded_by, verified_by) FROM stdin;
\.


--
-- Data for Name: resignation_requests; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.resignation_requests (clearance_completed, handover_completed, last_working_date, payment_amount, payment_confirmed, reason, resignation_date, resignation_type, id) FROM stdin;
\.


--
-- Data for Name: retirement_documents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.retirement_documents (id, content_type, created_at, created_by, description, document_type, file_name, file_path, file_size, is_active, is_mandatory, is_verified, updated_at, updated_by, verification_notes, version, retirement_request_id, uploaded_by, verified_by) FROM stdin;
\.


--
-- Data for Name: retirement_requests; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.retirement_requests (clearance_completed, last_working_date, pension_eligibility_confirmed, retirement_date, retirement_type, id) FROM stdin;
\.


--
-- Data for Name: service_extension_documents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.service_extension_documents (id, created_at, created_by, is_active, updated_at, updated_by, version, content_type, description, document_type, file_name, file_path, file_size, is_mandatory, is_verified, verification_notes, service_extension_request_id, uploaded_by, verified_by) FROM stdin;
\.


--
-- Data for Name: service_extension_requests; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.service_extension_requests (expiration_warning_sent, extension_duration_years, notification_sent_date, retirement_eligibility_date, id) FROM stdin;
\.


--
-- Data for Name: sla_trackers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.sla_trackers (id, created_at, created_by, is_active, updated_at, updated_by, version, completed_at, completed_within_sla, due_date, escalated_at, escalation_level, extended_at, extended_by, extension_days, extension_justification, is_breached, warning_sent, warning_sent_at, escalated_to, request_id) FROM stdin;
\.


--
-- Data for Name: termination_documents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.termination_documents (id, created_at, created_by, is_active, updated_at, updated_by, version, content_type, description, document_type, file_name, file_path, file_size, is_mandatory, termination_request_id) FROM stdin;
\.


--
-- Data for Name: termination_requests; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.termination_requests (disciplinary_actions, hr_recommendations, incident_date, investigation_summary, prior_warnings_count, probation_end_date, reason, scenario, id) FROM stdin;
\.


--
-- Data for Name: user_delegations; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.user_delegations (id, created_at, created_by, is_active, updated_at, updated_by, version, end_date, extended_at, extended_by, extension_reason, reason, revocation_reason, revoked_at, revoked_by, start_date, delegate_id, delegator_id) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (id, created_at, created_by, is_active, updated_at, updated_by, version, email, failed_login_attempts, full_name, is_account_non_expired, is_account_non_locked, is_credentials_non_expired, is_enabled, last_login_date, password, password_reset_token, password_reset_token_expiry, phone_number, role, username, employee_id, institution_id) FROM stdin;
\.


--
-- Name: appeal_activities appeal_activities_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appeal_activities
    ADD CONSTRAINT appeal_activities_pkey PRIMARY KEY (id);


--
-- Name: appeal_evidence appeal_evidence_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appeal_evidence
    ADD CONSTRAINT appeal_evidence_pkey PRIMARY KEY (id);


--
-- Name: audit_logs audit_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT audit_logs_pkey PRIMARY KEY (id);


--
-- Name: cadre_change_documents cadre_change_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cadre_change_documents
    ADD CONSTRAINT cadre_change_documents_pkey PRIMARY KEY (id);


--
-- Name: cadre_change_requests cadre_change_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cadre_change_requests
    ADD CONSTRAINT cadre_change_requests_pkey PRIMARY KEY (id);


--
-- Name: complaint_activities complaint_activities_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaint_activities
    ADD CONSTRAINT complaint_activities_pkey PRIMARY KEY (id);


--
-- Name: complaint_appeals complaint_appeals_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaint_appeals
    ADD CONSTRAINT complaint_appeals_pkey PRIMARY KEY (id);


--
-- Name: complaint_evidence complaint_evidence_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaint_evidence
    ADD CONSTRAINT complaint_evidence_pkey PRIMARY KEY (id);


--
-- Name: complaints complaints_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaints
    ADD CONSTRAINT complaints_pkey PRIMARY KEY (id);


--
-- Name: confirmation_requests confirmation_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.confirmation_requests
    ADD CONSTRAINT confirmation_requests_pkey PRIMARY KEY (request_id);


--
-- Name: dismissal_documents dismissal_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dismissal_documents
    ADD CONSTRAINT dismissal_documents_pkey PRIMARY KEY (id);


--
-- Name: dismissal_requests dismissal_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dismissal_requests
    ADD CONSTRAINT dismissal_requests_pkey PRIMARY KEY (id);


--
-- Name: employee_certificates employee_certificates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employee_certificates
    ADD CONSTRAINT employee_certificates_pkey PRIMARY KEY (id);


--
-- Name: employee_documents employee_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employee_documents
    ADD CONSTRAINT employee_documents_pkey PRIMARY KEY (id);


--
-- Name: employees employees_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (id);


--
-- Name: institutions institutions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.institutions
    ADD CONSTRAINT institutions_pkey PRIMARY KEY (id);


--
-- Name: leave_without_pay_requests leave_without_pay_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.leave_without_pay_requests
    ADD CONSTRAINT leave_without_pay_requests_pkey PRIMARY KEY (id);


--
-- Name: promotion_requests promotion_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.promotion_requests
    ADD CONSTRAINT promotion_requests_pkey PRIMARY KEY (request_id);


--
-- Name: request_documents request_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.request_documents
    ADD CONSTRAINT request_documents_pkey PRIMARY KEY (id);


--
-- Name: request_workflow request_workflow_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.request_workflow
    ADD CONSTRAINT request_workflow_pkey PRIMARY KEY (id);


--
-- Name: requests requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_pkey PRIMARY KEY (id);


--
-- Name: resignation_documents resignation_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.resignation_documents
    ADD CONSTRAINT resignation_documents_pkey PRIMARY KEY (id);


--
-- Name: resignation_requests resignation_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.resignation_requests
    ADD CONSTRAINT resignation_requests_pkey PRIMARY KEY (id);


--
-- Name: retirement_documents retirement_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.retirement_documents
    ADD CONSTRAINT retirement_documents_pkey PRIMARY KEY (id);


--
-- Name: retirement_requests retirement_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.retirement_requests
    ADD CONSTRAINT retirement_requests_pkey PRIMARY KEY (id);


--
-- Name: service_extension_documents service_extension_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_extension_documents
    ADD CONSTRAINT service_extension_documents_pkey PRIMARY KEY (id);


--
-- Name: service_extension_requests service_extension_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_extension_requests
    ADD CONSTRAINT service_extension_requests_pkey PRIMARY KEY (id);


--
-- Name: sla_trackers sla_trackers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sla_trackers
    ADD CONSTRAINT sla_trackers_pkey PRIMARY KEY (id);


--
-- Name: termination_documents termination_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.termination_documents
    ADD CONSTRAINT termination_documents_pkey PRIMARY KEY (id);


--
-- Name: termination_requests termination_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.termination_requests
    ADD CONSTRAINT termination_requests_pkey PRIMARY KEY (id);


--
-- Name: complaints uk_348qxg8v14l5t9lp4a9sxxyt6; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaints
    ADD CONSTRAINT uk_348qxg8v14l5t9lp4a9sxxyt6 UNIQUE (complaint_number);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: users uk_d1s31g1a7ilra77m65xmka3ei; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_d1s31g1a7ilra77m65xmka3ei UNIQUE (employee_id);


--
-- Name: sla_trackers uk_dd2hchi7dhwsjug42ot1j14tv; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sla_trackers
    ADD CONSTRAINT uk_dd2hchi7dhwsjug42ot1j14tv UNIQUE (request_id);


--
-- Name: employees uk_hin95j728bvl5g2f3b9tvx8o3; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT uk_hin95j728bvl5g2f3b9tvx8o3 UNIQUE (zssf_number);


--
-- Name: institutions uk_l3uewvcgggjpwyvk8gu8rs2b9; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.institutions
    ADD CONSTRAINT uk_l3uewvcgggjpwyvk8gu8rs2b9 UNIQUE (name);


--
-- Name: employees uk_me0bqxabqqdm83dvc6qy1niex; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT uk_me0bqxabqqdm83dvc6qy1niex UNIQUE (payroll_number);


--
-- Name: requests uk_oonqrar8b975jo10i82bjg6gj; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT uk_oonqrar8b975jo10i82bjg6gj UNIQUE (request_number);


--
-- Name: users uk_r43af9ap4edm43mmtq01oddj6; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_r43af9ap4edm43mmtq01oddj6 UNIQUE (username);


--
-- Name: employees uk_rxld7g9q1b25yon42oswm8gld; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT uk_rxld7g9q1b25yon42oswm8gld UNIQUE (zanzibar_id);


--
-- Name: complaint_appeals uk_seri72pkwv0prbt3c81rp1ueg; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaint_appeals
    ADD CONSTRAINT uk_seri72pkwv0prbt3c81rp1ueg UNIQUE (appeal_number);


--
-- Name: user_delegations user_delegations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_delegations
    ADD CONSTRAINT user_delegations_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: termination_documents fk16imj0pfejbnf6xvc94qj5317; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.termination_documents
    ADD CONSTRAINT fk16imj0pfejbnf6xvc94qj5317 FOREIGN KEY (termination_request_id) REFERENCES public.termination_requests(id);


--
-- Name: termination_requests fk1cu5mbx197ywt7p0t495u1sti; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.termination_requests
    ADD CONSTRAINT fk1cu5mbx197ywt7p0t495u1sti FOREIGN KEY (id) REFERENCES public.requests(id);


--
-- Name: complaint_evidence fk1mef0y6nyajf9xlk77h7gp52q; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaint_evidence
    ADD CONSTRAINT fk1mef0y6nyajf9xlk77h7gp52q FOREIGN KEY (uploaded_by) REFERENCES public.users(id);


--
-- Name: cadre_change_documents fk1nfoa0it2ma7eokh7b4orwrq5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cadre_change_documents
    ADD CONSTRAINT fk1nfoa0it2ma7eokh7b4orwrq5 FOREIGN KEY (cadre_change_request_id) REFERENCES public.cadre_change_requests(id);


--
-- Name: resignation_documents fk1owdv7yxojr8dp5u61h77g1bd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.resignation_documents
    ADD CONSTRAINT fk1owdv7yxojr8dp5u61h77g1bd FOREIGN KEY (resignation_request_id) REFERENCES public.resignation_requests(id);


--
-- Name: appeal_activities fk1qj8wcmt1yiv7r9wkbane6kud; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appeal_activities
    ADD CONSTRAINT fk1qj8wcmt1yiv7r9wkbane6kud FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: complaint_appeals fk22r2mtq4v2rt91ywcf9q7h7rp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaint_appeals
    ADD CONSTRAINT fk22r2mtq4v2rt91ywcf9q7h7rp FOREIGN KEY (appellant_id) REFERENCES public.employees(id);


--
-- Name: complaint_appeals fk27muin0yoqifmmf3jshki0jg1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaint_appeals
    ADD CONSTRAINT fk27muin0yoqifmmf3jshki0jg1 FOREIGN KEY (assigned_reviewer_id) REFERENCES public.users(id);


--
-- Name: employee_documents fk28g0aba9xtbkf6bp9pnvtcw5e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employee_documents
    ADD CONSTRAINT fk28g0aba9xtbkf6bp9pnvtcw5e FOREIGN KEY (employee_id) REFERENCES public.employees(id);


--
-- Name: resignation_requests fk2ddh2t4peh9bnx125r2lombl9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.resignation_requests
    ADD CONSTRAINT fk2ddh2t4peh9bnx125r2lombl9 FOREIGN KEY (id) REFERENCES public.requests(id);


--
-- Name: users fk2qqjpih9isqcs22710v8lef9w; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk2qqjpih9isqcs22710v8lef9w FOREIGN KEY (institution_id) REFERENCES public.institutions(id);


--
-- Name: requests fk3shwabv0u3mfqtj3w7oap8x4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT fk3shwabv0u3mfqtj3w7oap8x4 FOREIGN KEY (original_request_id) REFERENCES public.requests(id);


--
-- Name: complaint_activities fk5swnh7s14wcad1jat43hthgsv; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaint_activities
    ADD CONSTRAINT fk5swnh7s14wcad1jat43hthgsv FOREIGN KEY (complaint_id) REFERENCES public.complaints(id);


--
-- Name: retirement_requests fk5yl9nt0n503q1ofxvsjyek1gu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.retirement_requests
    ADD CONSTRAINT fk5yl9nt0n503q1ofxvsjyek1gu FOREIGN KEY (id) REFERENCES public.requests(id);


--
-- Name: cadre_change_documents fk6cv72qeb6ql4fao3mvacvjhfr; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cadre_change_documents
    ADD CONSTRAINT fk6cv72qeb6ql4fao3mvacvjhfr FOREIGN KEY (uploaded_by) REFERENCES public.users(id);


--
-- Name: requests fk6d5wo87uaoq9bpmco5y0ve30a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT fk6d5wo87uaoq9bpmco5y0ve30a FOREIGN KEY (submitted_by) REFERENCES public.users(id);


--
-- Name: retirement_documents fk6l3v1n0mk8ha7670ecaf4tlrq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.retirement_documents
    ADD CONSTRAINT fk6l3v1n0mk8ha7670ecaf4tlrq FOREIGN KEY (verified_by) REFERENCES public.users(id);


--
-- Name: users fk6p2ib82uai0pj9yk1iassppgq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk6p2ib82uai0pj9yk1iassppgq FOREIGN KEY (employee_id) REFERENCES public.employees(id);


--
-- Name: employee_certificates fk6qfnnwpkel3x0m86qmpiyn6wg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employee_certificates
    ADD CONSTRAINT fk6qfnnwpkel3x0m86qmpiyn6wg FOREIGN KEY (employee_id) REFERENCES public.employees(id);


--
-- Name: requests fk79eh6srcu6jggta5sqfk43mg5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT fk79eh6srcu6jggta5sqfk43mg5 FOREIGN KEY (approver_id) REFERENCES public.users(id);


--
-- Name: retirement_documents fk79hfb4en8x59w3sj072il0km1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.retirement_documents
    ADD CONSTRAINT fk79hfb4en8x59w3sj072il0km1 FOREIGN KEY (retirement_request_id) REFERENCES public.retirement_requests(id);


--
-- Name: complaint_appeals fk8h74uxd15d1lksk42abfwv75h; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaint_appeals
    ADD CONSTRAINT fk8h74uxd15d1lksk42abfwv75h FOREIGN KEY (original_complaint_id) REFERENCES public.complaints(id);


--
-- Name: confirmation_requests fk8l885rkcrta8spsnrwxacp5bn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.confirmation_requests
    ADD CONSTRAINT fk8l885rkcrta8spsnrwxacp5bn FOREIGN KEY (request_id) REFERENCES public.requests(id);


--
-- Name: leave_without_pay_requests fk988j4fw9nxrasie530ymnnyaa; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.leave_without_pay_requests
    ADD CONSTRAINT fk988j4fw9nxrasie530ymnnyaa FOREIGN KEY (id) REFERENCES public.requests(id);


--
-- Name: dismissal_documents fk9m8a19pkqe325fvqy9kpheh25; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dismissal_documents
    ADD CONSTRAINT fk9m8a19pkqe325fvqy9kpheh25 FOREIGN KEY (uploaded_by) REFERENCES public.users(id);


--
-- Name: complaints fk9xk3cxjexyqvextbpufv3dom3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaints
    ADD CONSTRAINT fk9xk3cxjexyqvextbpufv3dom3 FOREIGN KEY (assigned_investigator_id) REFERENCES public.users(id);


--
-- Name: cadre_change_requests fkbanvwxn1okntjo6uggofg30o9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cadre_change_requests
    ADD CONSTRAINT fkbanvwxn1okntjo6uggofg30o9 FOREIGN KEY (id) REFERENCES public.requests(id);


--
-- Name: promotion_requests fkcouax16eubsk8m9xys2hpllcl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.promotion_requests
    ADD CONSTRAINT fkcouax16eubsk8m9xys2hpllcl FOREIGN KEY (request_id) REFERENCES public.requests(id);


--
-- Name: request_workflow fkdn4gihvab5dha2a9vjwn9kkp5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.request_workflow
    ADD CONSTRAINT fkdn4gihvab5dha2a9vjwn9kkp5 FOREIGN KEY (request_id) REFERENCES public.requests(id);


--
-- Name: sla_trackers fkevo0fwmxlkbjixr5qvh498t5f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sla_trackers
    ADD CONSTRAINT fkevo0fwmxlkbjixr5qvh498t5f FOREIGN KEY (escalated_to) REFERENCES public.users(id);


--
-- Name: service_extension_requests fkg5gjpq65u7qv49p23qwx7hs6o; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_extension_requests
    ADD CONSTRAINT fkg5gjpq65u7qv49p23qwx7hs6o FOREIGN KEY (id) REFERENCES public.requests(id);


--
-- Name: complaints fkgbjnqj81ff81qpjwv5u7etkvh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaints
    ADD CONSTRAINT fkgbjnqj81ff81qpjwv5u7etkvh FOREIGN KEY (respondent_id) REFERENCES public.employees(id);


--
-- Name: resignation_documents fkhjkbfi2asjtyw3jyqhsmjr725; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.resignation_documents
    ADD CONSTRAINT fkhjkbfi2asjtyw3jyqhsmjr725 FOREIGN KEY (verified_by) REFERENCES public.users(id);


--
-- Name: requests fkhog28kbtnh3vwryed4rrjayul; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT fkhog28kbtnh3vwryed4rrjayul FOREIGN KEY (employee_id) REFERENCES public.employees(id);


--
-- Name: user_delegations fkivqm82qheckehhquqw48k596a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_delegations
    ADD CONSTRAINT fkivqm82qheckehhquqw48k596a FOREIGN KEY (delegator_id) REFERENCES public.users(id);


--
-- Name: complaints fkj2x961l9r2om7ka5nxypp1wcy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaints
    ADD CONSTRAINT fkj2x961l9r2om7ka5nxypp1wcy FOREIGN KEY (complainant_id) REFERENCES public.employees(id);


--
-- Name: appeal_activities fkjp8cf1hsuycquycqypdcej4eg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appeal_activities
    ADD CONSTRAINT fkjp8cf1hsuycquycqypdcej4eg FOREIGN KEY (appeal_id) REFERENCES public.complaint_appeals(id);


--
-- Name: retirement_documents fkjy0e7log6okmoc1hl9ac600vp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.retirement_documents
    ADD CONSTRAINT fkjy0e7log6okmoc1hl9ac600vp FOREIGN KEY (uploaded_by) REFERENCES public.users(id);


--
-- Name: dismissal_requests fkk1ihoeq9keht83soh4dlf577f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dismissal_requests
    ADD CONSTRAINT fkk1ihoeq9keht83soh4dlf577f FOREIGN KEY (id) REFERENCES public.requests(id);


--
-- Name: appeal_evidence fkk1rf8vc1gvmkqvte2n2mawuvh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appeal_evidence
    ADD CONSTRAINT fkk1rf8vc1gvmkqvte2n2mawuvh FOREIGN KEY (uploaded_by) REFERENCES public.users(id);


--
-- Name: complaints fkmaqybdkb9jsyhvl71js2c30ge; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaints
    ADD CONSTRAINT fkmaqybdkb9jsyhvl71js2c30ge FOREIGN KEY (submitted_by) REFERENCES public.users(id);


--
-- Name: cadre_change_documents fkmhw737f0lh3w6uuj9ia5l0pp5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cadre_change_documents
    ADD CONSTRAINT fkmhw737f0lh3w6uuj9ia5l0pp5 FOREIGN KEY (verified_by) REFERENCES public.users(id);


--
-- Name: sla_trackers fkmkfqtmft3besw1skjqjqbjmxy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sla_trackers
    ADD CONSTRAINT fkmkfqtmft3besw1skjqjqbjmxy FOREIGN KEY (request_id) REFERENCES public.requests(id);


--
-- Name: service_extension_documents fkmqb5f9n0pcweiywtvl1o70brd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_extension_documents
    ADD CONSTRAINT fkmqb5f9n0pcweiywtvl1o70brd FOREIGN KEY (service_extension_request_id) REFERENCES public.service_extension_requests(id);


--
-- Name: employees fkmv5f7xsi8phtoqg8jdltjie5x; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT fkmv5f7xsi8phtoqg8jdltjie5x FOREIGN KEY (institution_id) REFERENCES public.institutions(id);


--
-- Name: user_delegations fkn1w0psdpqvecxilxntvnsit; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_delegations
    ADD CONSTRAINT fkn1w0psdpqvecxilxntvnsit FOREIGN KEY (delegate_id) REFERENCES public.users(id);


--
-- Name: request_documents fkn96pvwixw0db6ln3o3m3d6lbw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.request_documents
    ADD CONSTRAINT fkn96pvwixw0db6ln3o3m3d6lbw FOREIGN KEY (request_id) REFERENCES public.requests(id);


--
-- Name: service_extension_documents fkoascdijuykc1nwgtpy9l9oyfq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_extension_documents
    ADD CONSTRAINT fkoascdijuykc1nwgtpy9l9oyfq FOREIGN KEY (verified_by) REFERENCES public.users(id);


--
-- Name: dismissal_documents fkoh2prhnlsaep6t7ai3ofw5phu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dismissal_documents
    ADD CONSTRAINT fkoh2prhnlsaep6t7ai3ofw5phu FOREIGN KEY (dismissal_request_id) REFERENCES public.dismissal_requests(id);


--
-- Name: request_workflow fkpl9py5nrtrh8afyiv8lwkwy8k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.request_workflow
    ADD CONSTRAINT fkpl9py5nrtrh8afyiv8lwkwy8k FOREIGN KEY (reviewer_id) REFERENCES public.users(id);


--
-- Name: service_extension_documents fkpnbse1qpx693foi2ieesdinfi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_extension_documents
    ADD CONSTRAINT fkpnbse1qpx693foi2ieesdinfi FOREIGN KEY (uploaded_by) REFERENCES public.users(id);


--
-- Name: complaint_evidence fkqstj1btfknw2oaeu98r00v3f6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaint_evidence
    ADD CONSTRAINT fkqstj1btfknw2oaeu98r00v3f6 FOREIGN KEY (complaint_id) REFERENCES public.complaints(id);


--
-- Name: requests fkrvd5vs8wa2u8joxv20p0j2w0h; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT fkrvd5vs8wa2u8joxv20p0j2w0h FOREIGN KEY (current_reviewer_id) REFERENCES public.users(id);


--
-- Name: appeal_evidence fkseggn3kk871m7lwam5kbom1d6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appeal_evidence
    ADD CONSTRAINT fkseggn3kk871m7lwam5kbom1d6 FOREIGN KEY (appeal_id) REFERENCES public.complaint_appeals(id);


--
-- Name: complaint_activities fksyqb6wfw8dvdvshuand4hueb9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.complaint_activities
    ADD CONSTRAINT fksyqb6wfw8dvdvshuand4hueb9 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: resignation_documents fktivx8w6hqecnd4vnq7oodciq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.resignation_documents
    ADD CONSTRAINT fktivx8w6hqecnd4vnq7oodciq FOREIGN KEY (uploaded_by) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

