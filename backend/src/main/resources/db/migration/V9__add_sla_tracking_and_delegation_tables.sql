-- Create SLA tracker table
CREATE TABLE sla_trackers (
    id VARCHAR(36) PRIMARY KEY,
    request_id VARCHAR(36) NOT NULL UNIQUE,
    due_date TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    is_breached BOOLEAN NOT NULL DEFAULT FALSE,
    completed_within_sla BOOLEAN,
    escalation_level INTEGER NOT NULL DEFAULT 0,
    escalated_at TIMESTAMP,
    escalated_to VARCHAR(36),
    extension_days INTEGER DEFAULT 0,
    extension_justification TEXT,
    extended_by VARCHAR(36),
    extended_at TIMESTAMP,
    warning_sent BOOLEAN DEFAULT FALSE,
    warning_sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create user delegations table
CREATE TABLE user_delegations (
    id VARCHAR(36) PRIMARY KEY,
    delegator_id VARCHAR(36) NOT NULL,
    delegate_id VARCHAR(36) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    reason TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    revoked_at TIMESTAMP,
    revoked_by VARCHAR(255),
    revocation_reason TEXT,
    extension_reason TEXT,
    extended_by VARCHAR(255),
    extended_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Add foreign key constraints
ALTER TABLE sla_trackers 
ADD CONSTRAINT fk_sla_trackers_request 
FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE;

ALTER TABLE sla_trackers 
ADD CONSTRAINT fk_sla_trackers_escalated_to 
FOREIGN KEY (escalated_to) REFERENCES users(id);

ALTER TABLE user_delegations 
ADD CONSTRAINT fk_user_delegations_delegator 
FOREIGN KEY (delegator_id) REFERENCES users(id);

ALTER TABLE user_delegations 
ADD CONSTRAINT fk_user_delegations_delegate 
FOREIGN KEY (delegate_id) REFERENCES users(id);

-- Create indexes for better performance
CREATE INDEX idx_sla_trackers_due_date ON sla_trackers(due_date);
CREATE INDEX idx_sla_trackers_is_breached ON sla_trackers(is_breached);
CREATE INDEX idx_sla_trackers_escalation_level ON sla_trackers(escalation_level);
CREATE INDEX idx_sla_trackers_completed_at ON sla_trackers(completed_at);

CREATE INDEX idx_user_delegations_delegator ON user_delegations(delegator_id);
CREATE INDEX idx_user_delegations_delegate ON user_delegations(delegate_id);
CREATE INDEX idx_user_delegations_dates ON user_delegations(start_date, end_date);
CREATE INDEX idx_user_delegations_active ON user_delegations(is_active);

-- Add constraints
ALTER TABLE user_delegations 
ADD CONSTRAINT chk_delegation_dates 
CHECK (end_date > start_date);

ALTER TABLE user_delegations 
ADD CONSTRAINT chk_delegation_different_users 
CHECK (delegator_id != delegate_id);

-- Add comments
COMMENT ON TABLE sla_trackers IS 'Tracks SLA compliance for requests';
COMMENT ON TABLE user_delegations IS 'Manages user delegation and coverage';

COMMENT ON COLUMN sla_trackers.due_date IS 'When the request should be completed according to SLA';
COMMENT ON COLUMN sla_trackers.is_breached IS 'Whether the request has breached SLA';
COMMENT ON COLUMN sla_trackers.escalation_level IS 'Current escalation level (0 = no escalation)';
COMMENT ON COLUMN sla_trackers.extension_days IS 'Number of days the SLA was extended';

COMMENT ON COLUMN user_delegations.delegator_id IS 'User who is delegating their authority';
COMMENT ON COLUMN user_delegations.delegate_id IS 'User who is receiving the delegated authority';
COMMENT ON COLUMN user_delegations.is_active IS 'Whether the delegation is currently active';
COMMENT ON COLUMN user_delegations.revoked_at IS 'When the delegation was revoked (if applicable)';