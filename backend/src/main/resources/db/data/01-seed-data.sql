-- Seed Data for SmartProcure Enterprise System

-- Insert Roles
INSERT IGNORE INTO roles (id, name, description, created_by) VALUES 
(1, 'ADMINISTRATOR', 'System Administrator with full enterprise privileges', 'SYSTEM'),
(2, 'PROCUREMENT_MANAGER', 'Procurement Manager managing sourcing, PRs, POs, and Vendor approvals', 'SYSTEM'),
(3, 'VENDOR', 'External Supplier managing catalog and billing', 'SYSTEM'),
(4, 'WAREHOUSE_MANAGER', 'Warehouse Manager managing stock levels, receipts, and transfers', 'SYSTEM'),
(5, 'FINANCE_MANAGER', 'Finance Manager managing 3-way invoice matching and payments', 'SYSTEM');

-- Insert Initial Permissions
INSERT IGNORE INTO permissions (id, name, description, created_by) VALUES
(1, 'USER_MANAGEMENT', 'Manage system users and access roles', 'SYSTEM'),
(2, 'VENDOR_MANAGEMENT', 'Approve, reject, and manage vendors', 'SYSTEM'),
(3, 'PRODUCT_CATALOG', 'Create and modify products and categories', 'SYSTEM'),
(4, 'INVENTORY_CONTROL', 'Adjust stock levels and approve stock transfers', 'SYSTEM'),
(5, 'PROCUREMENT_WORKFLOW', 'Create and approve PRs and POs', 'SYSTEM'),
(6, 'FINANCE_OPERATIONS', 'Process invoices, 3-way matching, and payments', 'SYSTEM');

-- Assign Permissions to Roles
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
(2, 2), (2, 3), (2, 5),
(3, 3),
(4, 3), (4, 4),
(5, 6);

-- Insert Development Seed Users (Password: ChangeMe@123)
-- BCrypt Hash for "ChangeMe@123": $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd0P1ZpH.a8g.26y
INSERT IGNORE INTO users (id, email, password, first_name, last_name, phone, active, verified, created_by) VALUES 
(1, 'admin@smartprocure.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd0P1ZpH.a8g.26y', 'System', 'Administrator', '+1-800-555-0100', TRUE, TRUE, 'SYSTEM'),
(2, 'procurement@smartprocure.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd0P1ZpH.a8g.26y', 'Sarah', 'Jenkins', '+1-800-555-0200', TRUE, TRUE, 'SYSTEM'),
(3, 'warehouse@smartprocure.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd0P1ZpH.a8g.26y', 'Robert', 'Chen', '+1-800-555-0300', TRUE, TRUE, 'SYSTEM'),
(4, 'finance@smartprocure.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd0P1ZpH.a8g.26y', 'Elena', 'Rostova', '+1-800-555-0400', TRUE, TRUE, 'SYSTEM'),
(5, 'vendor@smartprocure.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd0P1ZpH.a8g.26y', 'John', 'Supplier', '+1-800-555-0500', TRUE, TRUE, 'SYSTEM');

-- Assign Roles to Users
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES 
(1, 1), -- Admin
(2, 2), -- Procurement Mgr
(3, 4), -- Warehouse Mgr
(4, 5), -- Finance Mgr
(5, 3); -- Vendor

-- Insert Sample Approved Vendor
INSERT IGNORE INTO vendors (id, company_name, tax_id, contact_email, contact_phone, address, status, rating, payment_terms, user_id, created_by) VALUES
(1, 'Acme Global Logistics Inc.', 'US-TAX-99887766', 'contact@acme.com', '+1-555-0144', '100 Enterprise Way, Suite 400, Austin, TX', 'APPROVED', 4.8, 'NET_30', 5, 'SYSTEM'),
(2, 'TechCorp Hardware Supplies', 'US-TAX-11223344', 'sales@techcorp.com', '+1-555-0155', '500 Silicon Ave, San Jose, CA', 'UNDER_REVIEW', 4.2, 'NET_60', NULL, 'SYSTEM');

-- Insert Sample Categories
INSERT IGNORE INTO categories (id, code, name, description, created_by) VALUES
(1, 'CAT-IT-HW', 'IT Hardware & Equipment', 'Laptops, Servers, Monitors, Networking gear', 'SYSTEM'),
(2, 'CAT-OFF-SUP', 'Office Supplies', 'Paper, stationery, desk ergonomics', 'SYSTEM'),
(3, 'CAT-RAW-MAT', 'Raw Industrial Materials', 'Steel, aluminum, polymers, components', 'SYSTEM');

-- Insert Sample Products
INSERT IGNORE INTO products (id, sku, name, description, category_id, unit_price, reorder_level, created_by) VALUES
(1, 'SKU-LAP-001', 'Enterprise Laptop Pro 15', '16-inch Core i9, 32GB RAM, 1TB SSD', 1, 1499.99, 15, 'SYSTEM'),
(2, 'SKU-MON-002', 'UltraWide 34-inch Monitor', '4K Curved IPS Display with USB-C Hub', 1, 499.50, 20, 'SYSTEM'),
(3, 'SKU-PAP-003', 'Recycled A4 Paper Case', 'Box of 10 reams heavy-duty 80gsm paper', 2, 45.00, 50, 'SYSTEM');

-- Insert Sample Warehouses
INSERT IGNORE INTO warehouses (id, code, name, location, capacity, manager_id, created_by) VALUES
(1, 'WH-EAST-01', 'Primary Eastern Logistics Hub', '700 Distribution Blvd, Newark, NJ', 50000, 3, 'SYSTEM'),
(2, 'WH-WEST-02', 'Pacific Northwest Distribution Center', '1200 Logistics Pkwy, Seattle, WA', 35000, 3, 'SYSTEM');

-- Insert Sample Inventory Levels
INSERT IGNORE INTO inventory (id, product_id, warehouse_id, quantity_on_hand, quantity_allocated, quantity_available, min_stock_level, created_by) VALUES
(1, 1, 1, 100, 10, 90, 15, 'SYSTEM'),
(2, 2, 1, 50, 5, 45, 20, 'SYSTEM'),
(3, 3, 2, 200, 20, 180, 50, 'SYSTEM');

-- Insert System Configuration Parameters
INSERT IGNORE INTO system_config (id, config_key, config_value, description) VALUES
(1, 'AUTO_APPROVE_PR_THRESHOLD', '5000.00', 'Maximum amount for automatic Purchase Request approval'),
(2, 'CURRENCY_SYMBOL', 'USD', 'Primary system currency ISO code'),
(3, 'COMPANY_NAME', 'SmartProcure Enterprise Corp', 'Organization identity display name');
