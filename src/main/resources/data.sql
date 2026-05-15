-- Initial admin user: admin@example.com / password123!
-- Password is BCrypt encoded: {bcrypt}$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00pdRQXhr9B87i
INSERT INTO users (email, password, name, role, created_at)
VALUES ('admin@example.com', '{bcrypt}$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00pdRQXhr9B87i', 'Administrator', 'ADMIN', CURRENT_TIMESTAMP);

