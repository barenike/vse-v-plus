INSERT INTO users (id, role_id, email, user_balance, is_enabled)
VALUES ('958e91f2-d3b1-4b33-af84-52fec6205ad9', 2, 'admin@chelpipegroup.com', 0, true)
on conflict (id) do nothing;
