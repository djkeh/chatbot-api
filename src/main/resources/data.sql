-- Initial admin user: admin@email.com / password123!
INSERT INTO user_account (email, password, name, role, created_at, updated_at)
VALUES ('admin@email.com', '{bcrypt}$2a$10$6.8WY2DvLzvhrFjNaveH/uqVVs1cTDC.776El6YReGOInli4bLgMy', 'Administrator', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
;
