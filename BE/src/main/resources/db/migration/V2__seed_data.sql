-- Seed data. Passwords are BCrypt hashes:
--   tala   / admin123   (ADMIN)
--   yazeed / user123    (USER)
--   yahya  / user123    (USER)

insert into users (name, username, email, password, role, status) values
('Tala Skafi',     'tala',   'tala@example.com',   '$2y$10$6tmSGBS4wD0GBzcFKnCkYOfF8uvvod/L6lG28/USwrTBdJ9WDMBJG', 'ADMIN', 'ACTIVE'),
('Yazeed Aloufee', 'yazeed', 'yazeed@example.com', '$2y$10$fhiCHpyLlflTAAYBeX3LkeXp42xHt839jiUK0XJJJEvhnx0/oPusy', 'USER',  'ACTIVE'),
('Yahya Aloufee',  'yahya',  'yahya@example.com',  '$2y$10$fhiCHpyLlflTAAYBeX3LkeXp42xHt839jiUK0XJJJEvhnx0/oPusy', 'USER',  'ACTIVE');

insert into tasks (title, description, status, due_date, assigned_user_id) values
('Set up project repository', 'Initialize the git repository and push the project skeleton.', 'COMPLETED',   '2026-07-15', 2),
('Design database schema',    'Create the ER diagram and the initial database tables.',       'IN_PROGRESS', '2026-07-25', 2),
('Write API documentation',   'Document all REST endpoints with request/response examples.',  'PENDING',     '2026-07-30', 3);

insert into comments (task_id, user_id, content) values
(2, 1, 'Please prioritize the users and tasks tables.'),
(2, 2, 'Working on it, I will share the diagram today.');
