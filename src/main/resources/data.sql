-- rolesテーブル
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_GENERAL');

-- usersテーブル
INSERT IGNORE INTO users (id, role_id, name, email, password, address, phone_number, enabled) VALUES (1, 2, 'test', 'test@co.jp', '$2a$08$F5XKtwi4UgSigQJakLwJHeFXYzj3U1bACdCbiBVNNLPsWhwE8NZqK', '東京都 渋谷区', '03-3333-3333', 1);
