-- Seed admin user (password: admin123)
INSERT INTO users (username, password, role) VALUES ('admin', '$2b$10$RWpd9QZHzOL4HJlX46diQeRNaOz5bHELo.CizNG2tY6jQeiu.3Fe2', 'ADMIN');

-- Sample authors
INSERT INTO authors (first_name, last_name) VALUES ('Robert', 'Martin');
INSERT INTO authors (first_name, last_name) VALUES ('Martin', 'Fowler');
INSERT INTO authors (first_name, last_name) VALUES ('Joshua', 'Bloch');

-- Sample books
INSERT INTO books (title, author_id, isbn, price) VALUES ('Clean Code', 1, '9780132350884', 29.99);
INSERT INTO books (title, author_id, isbn, price) VALUES ('Refactoring', 2, '9780134757599', 39.99);
INSERT INTO books (title, author_id, isbn, price) VALUES ('Effective Java', 3, '9780134685991', 34.99);
