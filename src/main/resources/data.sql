-- Initial admin user: admin@email.com / password123!
INSERT INTO user_account (email, password, name, role, created_at, updated_at)
VALUES ('admin@email.com', '{bcrypt}$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00pdRQXhr9B87i', 'Administrator', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
;
