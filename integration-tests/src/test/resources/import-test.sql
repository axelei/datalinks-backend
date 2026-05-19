-- Insert a test user
INSERT INTO USERS (id, username, level, email, name, creationDate, language, password, salt) 
VALUES ('00000000-0000-0000-0000-000000000001', 'admin', 'ADMIN', 'admin@example.com', 'Admin User', CURRENT_TIMESTAMP, 'en-GB', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'salt');

-- Insert a test category
INSERT INTO CATEGORIES (id, name, slug, creationDate) 
VALUES ('00000000-0000-0000-0000-000000000002', 'Test Category', 'test-category', CURRENT_TIMESTAMP);

-- Insert a test page
INSERT INTO PAGES (id, slug, title, content, summary, creationDate, modifiedDate, creator_id) 
VALUES ('00000000-0000-0000-0000-000000000003', 'test-page', 'Test Page', 'This is a test page content.', 'Summary of test page', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001');

-- Insert initial config
INSERT INTO CONFIGLETS (configlet_key, configlet_value) VALUES ('EDIT_LEVEL', 'USER');
INSERT INTO CONFIGLETS (configlet_key, configlet_value) VALUES ('CREATE_LEVEL', 'USER');
INSERT INTO CONFIGLETS (configlet_key, configlet_value) VALUES ('DELETE_LEVEL', 'ADMIN');
INSERT INTO CONFIGLETS (configlet_key, configlet_value) VALUES ('READ_LEVEL', 'GUEST');
