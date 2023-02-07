INSERT INTO users (id, role_id, email, password, user_balance, enabled, phone_number, first_name, last_name)
VALUES ('958e91f2-d3b1-4b33-af84-52fec6205ad9', 2, 'admin@chelpipegroup.com',
        '$2a$10$8QKaWUF0UzfQpwV4jSYYPuV8RL.1kJ9eQM5qsD/fePjJFJHEQwkVa', 0, true, '+77777777777', 'Admin', 'Admin')
on conflict (id) do nothing;
