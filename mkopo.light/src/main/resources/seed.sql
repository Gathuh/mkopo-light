INSERT INTO products (id, name, description, active, created_at, updated_at)
SELECT 'prod-starter', 'Starter Loan', 'Small short-term loan', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 'prod-starter');

INSERT INTO products (id, name, description, active, created_at, updated_at)
SELECT 'prod-flex', 'Flex Loan', 'Flexible monthly loan', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 'prod-flex');

INSERT INTO loan_limits (id, customer_id, max_limit, available_limit, active, created_at, updated_at)
SELECT 'limit-cust-001', 'cust-001', 5000.0000, 4200.0000, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM loan_limits WHERE id = 'limit-cust-001');

INSERT INTO loan_limits (id, customer_id, max_limit, available_limit, active, created_at, updated_at)
SELECT 'limit-cust-002', 'cust-002', 12000.0000, 8500.0000, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM loan_limits WHERE id = 'limit-cust-002');

INSERT INTO loan_limits (id, customer_id, max_limit, available_limit, active, created_at, updated_at)
SELECT 'limit-cust-003', 'cust-003', 2500.0000, 1700.0000, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM loan_limits WHERE id = 'limit-cust-003');

INSERT INTO loans (
    id, customer_id, approved_by_user_id, product_id, product_name, tenure_option_id,
    tenure_value, tenure_type, loan_type, billing_type, consolidated_due_day,
    principal_amount, disbursed_amount, outstanding_balance, accrued_interest, total_repaid,
    snapshot_service_fee_rate, snapshot_service_fee_timing, snapshot_late_fee_amount,
    snapshot_trigger_days, snapshot_daily_fee_rate, snapshot_product_fee_amount,
    snapshot_capitalized, loan_state, application_date, approval_date, disbursement_date,
    due_date, maturity_date, created_at, updated_at
)
SELECT
    'loan-active-001', 'cust-001', 'approver-1', 'prod-starter', 'Starter Loan', 'tenure-30d',
    30, 'DAYS', 'BULLET', 'INDIVIDUAL', NULL,
    800.0000, 760.0000, 400.0000, 12.5000, 400.0000,
    0.0500, 'UPFRONT', 25.0000,
    3, 0.0100, 0.0000,
    false, 'ACTIVE', CURDATE(), CURDATE(), CURDATE(),
    DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 30 DAY), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM loans WHERE id = 'loan-active-001');

INSERT INTO loans (
    id, customer_id, approved_by_user_id, product_id, product_name, tenure_option_id,
    tenure_value, tenure_type, loan_type, billing_type, consolidated_due_day,
    principal_amount, disbursed_amount, outstanding_balance, accrued_interest, total_repaid,
    snapshot_service_fee_rate, snapshot_service_fee_timing, snapshot_late_fee_amount,
    snapshot_trigger_days, snapshot_daily_fee_rate, snapshot_product_fee_amount,
    snapshot_capitalized, loan_state, application_date, approval_date, disbursement_date,
    due_date, maturity_date, created_at, updated_at
)
SELECT
    'loan-overdue-001', 'cust-002', 'approver-2', 'prod-flex', 'Flex Loan', 'tenure-3m',
    3, 'MONTHS', 'INSTALLMENT', 'CONSOLIDATED', 25,
    3000.0000, 3000.0000, 1800.0000, 35.0000, 1200.0000,
    0.0200, 'POST_DISBURSEMENT', 0.0300,
    2, 0.0080, 0.0000,
    false, 'OVERDUE', DATE_SUB(CURDATE(), INTERVAL 45 DAY), DATE_SUB(CURDATE(), INTERVAL 44 DAY), DATE_SUB(CURDATE(), INTERVAL 44 DAY),
    DATE_SUB(CURDATE(), INTERVAL 6 DAY), DATE_ADD(CURDATE(), INTERVAL 46 DAY), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM loans WHERE id = 'loan-overdue-001');

INSERT INTO loans (
    id, customer_id, approved_by_user_id, product_id, product_name, tenure_option_id,
    tenure_value, tenure_type, loan_type, billing_type, consolidated_due_day,
    principal_amount, disbursed_amount, outstanding_balance, accrued_interest, total_repaid,
    snapshot_service_fee_rate, snapshot_service_fee_timing, snapshot_late_fee_amount,
    snapshot_trigger_days, snapshot_daily_fee_rate, snapshot_product_fee_amount,
    snapshot_capitalized, loan_state, application_date, approval_date, disbursement_date,
    due_date, maturity_date, created_at, updated_at
)
SELECT
    'loan-closed-001', 'cust-003', 'approver-1', 'prod-starter', 'Starter Loan', 'tenure-14d',
    14, 'DAYS', 'BULLET', 'INDIVIDUAL', NULL,
    500.0000, 475.0000, 0.0000, 5.0000, 500.0000,
    0.0500, 'UPFRONT', 20.0000,
    3, 0.0100, 0.0000,
    false, 'CLOSED', DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 19 DAY), DATE_SUB(CURDATE(), INTERVAL 19 DAY),
    DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM loans WHERE id = 'loan-closed-001');
