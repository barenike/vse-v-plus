INSERT INTO users (id, role_id, email, user_balance, is_enabled, phone_number, first_name, last_name, job_title,
                   info_about, image_id)
VALUES ('f2d7c87f-0e32-4470-9189-14cf6a9ffe07', 1, 'user@chelpipegroup.com', 0, true, null, null, null, null, null,
        null)
on conflict (id) do nothing;
