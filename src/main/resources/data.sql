-- rolesテーブル
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_GENERAL');

-- usersテーブル
INSERT IGNORE INTO users (id, role_id, name, email, password, address, phone_number, enabled) VALUES (1, 1, '管理者', 'test-n18@hotmail.co.jp', '$2a$08$DD.Xs.edaIub6R96/T/CReC8hUxUBquEI7a1FbvSYkIlwNFm/833i', '神奈川県', '080-6651-8190', 1);
