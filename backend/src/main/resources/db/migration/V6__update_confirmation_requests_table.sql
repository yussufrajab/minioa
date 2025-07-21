-- Update confirmation_requests table to match entity definition
-- This script adds missing columns to support the complete Confirmation module

-- Add missing columns to confirmation_requests table
ALTER TABLE confirmation_requests 
ADD COLUMN probation_start_date DATE;

ALTER TABLE confirmation_requests 
ADD COLUMN performance_rating VARCHAR(100);

ALTER TABLE confirmation_requests 
ADD COLUMN supervisor_recommendation TEXT;

ALTER TABLE confirmation_requests 
ADD COLUMN hr_assessment TEXT;

ALTER TABLE confirmation_requests 
ADD COLUMN proposed_confirmation_date DATE;

ALTER TABLE confirmation_requests 
ADD COLUMN current_salary DECIMAL(19, 2);

ALTER TABLE confirmation_requests 
ADD COLUMN proposed_salary DECIMAL(19, 2);

-- Update the foreign key reference to match entity annotation
ALTER TABLE confirmation_requests 
DROP CONSTRAINT IF EXISTS confirmation_requests_id_fkey;

ALTER TABLE confirmation_requests 
ADD CONSTRAINT confirmation_requests_request_id_fkey 
FOREIGN KEY (id) REFERENCES requests(id);

-- Create indexes for better performance
CREATE INDEX idx_confirmation_requests_probation_start_date ON confirmation_requests(probation_start_date);
CREATE INDEX idx_confirmation_requests_probation_end_date ON confirmation_requests(probation_end_date);
CREATE INDEX idx_confirmation_requests_proposed_confirmation_date ON confirmation_requests(proposed_confirmation_date);
CREATE INDEX idx_confirmation_requests_performance_rating ON confirmation_requests(performance_rating);

-- Add constraints for data integrity
ALTER TABLE confirmation_requests 
ADD CONSTRAINT chk_probation_dates 
CHECK (probation_end_date > probation_start_date);

ALTER TABLE confirmation_requests 
ADD CONSTRAINT chk_salary_amounts 
CHECK (current_salary >= 0 AND proposed_salary >= 0);

ALTER TABLE confirmation_requests 
ADD CONSTRAINT chk_performance_rating 
CHECK (performance_rating IN ('EXCELLENT', 'GOOD', 'SATISFACTORY', 'NEEDS_IMPROVEMENT', 'UNSATISFACTORY'));