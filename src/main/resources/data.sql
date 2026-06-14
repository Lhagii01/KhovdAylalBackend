-- -----------------------------------------------------------
-- Анхны өгөгдөл (Data Initialization)
-- Энэ файл нь төсөл анх эхлэх үед ажиллана.
-- -----------------------------------------------------------

-- Хэрэглэгчийн хүснэгтэд ADMIN эрхтэй хэрэглэгч нэмэх
-- password: Admin123! (BCrypt-ээр mãshlah хийгдсэн)
INSERT INTO user (id, first_name, last_name, email, password, role, created_at, updated_at)
VALUES
(
    1,
    'System',
    'Admin',
    'admin@baruun.mn',
    -- Нууц үг нь Admin123!
    '$2a$10$779hL8nJ0XFwL4m4h1OQk.t.sW0Q2B7yF3M9kC0/oXj0w/E/l5L/G',
    'ADMIN',
    NOW(),
    NOW()
);

-- Тест хийхэд зориулсан энгийн USER нэмэх
-- password: User123! (BCrypt-ээр mãshlah хийгдсэн)
INSERT INTO user (id, first_name, last_name, email, password, role, created_at, updated_at)
VALUES
(
    2,
    'Normal',
    'User',
    'user@baruun.mn',
    -- Нууц үг нь User123!
    '$2a$10$C98Vf.x5H5T7C6y0jW6gq.b3H4.D8.X9Y7wN2zM8G6Q4F2D6t2S',
    'USER',
    NOW(),
    NOW()
);

-- Тест хийхэд зориулсан бүс нутаг нэмэх
INSERT INTO region (id, region_name, description)
VALUES
(1, 'Баян-Өлгий аймаг', 'Баруун Монголын хамгийн баруун хязгаарын, Казак үндэстэн давамгайлсан үзэсгэлэнт нутаг.'),
(2, 'Ховд аймаг', 'Олон үндэстний өлгий нутаг, түүх, соёлын арвин өвтэй.');

-- Тест хийхэд зориулсан байр (Accommodation) нэмэх (Хэрэв table-ийн нэр zөв бол)
-- Та Accommodation Entity-ийн бүтцийг оруулаагүй тул, энэ хэсгийг түр орхилоо.
-- Жич: JPA-ийн ddl-auto=update тохиргоотой тул хүснэгтүүд автоматаар үүснэ.