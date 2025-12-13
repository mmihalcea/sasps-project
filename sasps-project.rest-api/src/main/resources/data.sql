INSERT INTO public.county (id, name) VALUES (1, 'Alba');
INSERT INTO public.county (id, name) VALUES (2, 'Arad');
INSERT INTO public.county (id, name) VALUES (3, 'Arges');
INSERT INTO public.county (id, name) VALUES (4, 'Bacau');
INSERT INTO public.county (id, name) VALUES (5, 'Bihor');
INSERT INTO public.county (id, name) VALUES (6, 'Bistrita-Nasaud');
INSERT INTO public.county (id, name) VALUES (7, 'Botosani');
INSERT INTO public.county (id, name) VALUES (8, 'Braila');
INSERT INTO public.county (id, name) VALUES (9, 'Brasov');
INSERT INTO public.county (id, name) VALUES (10, 'Bucuresti');
INSERT INTO public.county (id, name) VALUES (11, 'Buzau');
INSERT INTO public.county (id, name) VALUES (12, 'Calarasi');
INSERT INTO public.county (id, name) VALUES (13, 'Caras-Severin');
INSERT INTO public.county (id, name) VALUES (14, 'Cluj');
INSERT INTO public.county (id, name) VALUES (15, 'Constanta');
INSERT INTO public.county (id, name) VALUES (16, 'Covasna');
INSERT INTO public.county (id, name) VALUES (17, 'Dambovita');
INSERT INTO public.county (id, name) VALUES (18, 'Dolj');
INSERT INTO public.county (id, name) VALUES (19, 'Galati');
INSERT INTO public.county (id, name) VALUES (20, 'Giurgiu');
INSERT INTO public.county (id, name) VALUES (21, 'Gorj');
INSERT INTO public.county (id, name) VALUES (22, 'Harghita');
INSERT INTO public.county (id, name) VALUES (23, 'Hunedoara');
INSERT INTO public.county (id, name) VALUES (24, 'Ialomita');
INSERT INTO public.county (id, name) VALUES (25, 'Iasi');
INSERT INTO public.county (id, name) VALUES (26, 'Ilfov');
INSERT INTO public.county (id, name) VALUES (27, 'Maramures');
INSERT INTO public.county (id, name) VALUES (28, 'Mehedinti');
INSERT INTO public.county (id, name) VALUES (29, 'Mures');
INSERT INTO public.county (id, name) VALUES (30, 'Neamt');
INSERT INTO public.county (id, name) VALUES (31, 'Olt');
INSERT INTO public.county (id, name) VALUES (32, 'Prahova');
INSERT INTO public.county (id, name) VALUES (33, 'Salaj');
INSERT INTO public.county (id, name) VALUES (34, 'Satu Mare');
INSERT INTO public.county (id, name) VALUES (35, 'Sibiu');
INSERT INTO public.county (id, name) VALUES (36, 'Suceava');
INSERT INTO public.county (id, name) VALUES (37, 'Teleorman');
INSERT INTO public.county (id, name) VALUES (38, 'Timis');
INSERT INTO public.county (id, name) VALUES (39, 'Tulcea');
INSERT INTO public.county (id, name) VALUES (40, 'Valcea');
INSERT INTO public.county (id, name) VALUES (41, 'Vaslui');
INSERT INTO public.county (id, name) VALUES (42, 'Vrancea');

-- Test users for baseline authentication system (NO encryption for baseline metrics)
INSERT INTO public.users (id, name, email, phone, county, city, address, active, email_notifications_enabled, sms_notifications_enabled, reminder_hours_before, user_role, password)
VALUES (1, 'Admin User', 'admin@test.com', '0700000001', 'Bucuresti', 'Bucuresti', 'Str. Administratorilor 1', true, true, true, 24, 'ADMIN', 'admin123');

INSERT INTO public.users (id, name, email, phone, county, city, address, active, email_notifications_enabled, sms_notifications_enabled, reminder_hours_before, user_role, password)
VALUES (2, 'Regular User', 'user@test.com', '0700000002', 'Cluj', 'Cluj-Napoca', 'Str. Utilizatorilor 2', true, true, false, 24, 'USER', 'user123');

INSERT INTO public.users (id, name, email, phone, county, city, address, active, email_notifications_enabled, sms_notifications_enabled, reminder_hours_before, user_role, password)
VALUES (3, 'Test Client', 'test@example.com', '0700000003', 'Timis', 'Timisoara', 'Str. Testelor 3', true, true, true, 48, 'USER', 'test123');

