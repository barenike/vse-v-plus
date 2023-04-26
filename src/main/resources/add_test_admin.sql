INSERT INTO users (id, role_id, email, user_balance, is_enabled, phone_number, first_name, last_name, job_title,
                   info_about, image_id)
VALUES ('9e61f1a4-f08b-4acf-b47a-28b98c523705', 2, 'admin@chelpipegroup.com', 0, true, null, null, null, null, null,
        null)
on conflict (id) do nothing;
