-- Update promotion_requests table to match entity definition
-- This script updates the table to support the complete Promotion module

-- Drop existing table and recreate with proper schema
DROP TABLE IF EXISTS promotion_requests;

-- Create promotion_requests table with complete schema
CREATE TABLE promotion_requests (
    id VARCHAR(36) PRIMARY KEY,
    promotion_type VARCHAR(20) NOT NULL CHECK (promotion_type IN ('EDUCATIONAL', 'PERFORMANCE')),
    current_position VARCHAR(255) NOT NULL,
    current_grade VARCHAR(100) NOT NULL,
    proposed_position VARCHAR(255) NOT NULL,
    proposed_grade VARCHAR(100) NOT NULL,
    current_salary DECIMAL(19, 2),
    proposed_salary DECIMAL(19, 2),
    effective_date DATE,
    justification TEXT,
    performance_rating VARCHAR(100),
    years_in_current_position INTEGER,
    qualifications_met TEXT,
    supervisor_recommendation TEXT,
    FOREIGN KEY (id) REFERENCES requests(id)
);

-- Create indexes for better performance
CREATE INDEX idx_promotion_requests_promotion_type ON promotion_requests(promotion_type);
CREATE INDEX idx_promotion_requests_current_position ON promotion_requests(current_position);
CREATE INDEX idx_promotion_requests_current_grade ON promotion_requests(current_grade);
CREATE INDEX idx_promotion_requests_proposed_position ON promotion_requests(proposed_position);
CREATE INDEX idx_promotion_requests_proposed_grade ON promotion_requests(proposed_grade);
CREATE INDEX idx_promotion_requests_effective_date ON promotion_requests(effective_date);
CREATE INDEX idx_promotion_requests_performance_rating ON promotion_requests(performance_rating);
CREATE INDEX idx_promotion_requests_years_in_current_position ON promotion_requests(years_in_current_position);

-- Add constraints for data integrity
ALTER TABLE promotion_requests 
ADD CONSTRAINT chk_promotion_salary_amounts 
CHECK (current_salary >= 0 AND proposed_salary >= 0);

ALTER TABLE promotion_requests 
ADD CONSTRAINT chk_promotion_years_in_position 
CHECK (years_in_current_position >= 0);

ALTER TABLE promotion_requests 
ADD CONSTRAINT chk_promotion_performance_rating 
CHECK (performance_rating IN ('EXCELLENT', 'GOOD', 'SATISFACTORY', 'NEEDS_IMPROVEMENT', 'UNSATISFACTORY'));

-- Ensure proposed salary is not less than current salary
ALTER TABLE promotion_requests 
ADD CONSTRAINT chk_promotion_salary_increase 
CHECK (proposed_salary >= current_salary);