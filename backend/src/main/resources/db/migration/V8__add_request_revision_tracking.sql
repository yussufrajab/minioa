-- Add revision tracking fields to requests table
ALTER TABLE requests 
ADD COLUMN original_request_id VARCHAR(36),
ADD COLUMN revision_number INTEGER DEFAULT 0,
ADD COLUMN is_revised BOOLEAN DEFAULT FALSE,
ADD COLUMN revision_reason TEXT,
ADD COLUMN revised_at TIMESTAMP;

-- Add foreign key constraint for original request
ALTER TABLE requests 
ADD CONSTRAINT fk_requests_original_request 
FOREIGN KEY (original_request_id) REFERENCES requests(id);

-- Create index for better performance on revision queries
CREATE INDEX idx_requests_original_request ON requests(original_request_id);
CREATE INDEX idx_requests_revision_number ON requests(revision_number);

-- Add comment
COMMENT ON COLUMN requests.original_request_id IS 'Reference to the original request if this is a revision';
COMMENT ON COLUMN requests.revision_number IS 'Revision number, 0 for original requests';
COMMENT ON COLUMN requests.is_revised IS 'Flag indicating if the request has been revised';
COMMENT ON COLUMN requests.revision_reason IS 'Reason for revision, typically from rejection';
COMMENT ON COLUMN requests.revised_at IS 'Timestamp when the revision was created';