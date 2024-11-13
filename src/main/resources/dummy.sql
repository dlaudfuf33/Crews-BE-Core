-- 더미 데이터 삽입 스크립트

-- Bank 데이터 삽입
-- bank 테이블에 데이터 삽입
INSERT INTO bank (id, code, name)
VALUES (1, '001', '통합은행'),
       (2, '002', '산업은행'),
       (3, '003', '가상계좌 채번가능 기업은행'),
       (4, '004', '가상계좌 채번가능 국민은행'),
       (5, '005', '외환은행'),
       (6, '007', '수협은행'),
       (7, '008', '수출입은행'),
       (8, '011', '가상계좌 채번가능 농협은행'),
       (9, '012', '농협회원조합'),
       (10, '020', '가상계좌 채번가능 우리은행'),
       (11, '023', '가상계좌 채번가능 SC제일은행'),
       (12, '026', '서울은행'),
       (13, '027', '한국씨티은행'),
       (14, '031', '가상계좌 채번가능 대구은행'),
       (15, '032', '가상계좌 채번가능 부산은행'),
       (16, '034', '가상계좌 채번가능 광주은행'),
       (17, '035', '제주은행'),
       (18, '037', '전북은행'),
       (19, '039', '경남은행'),
       (20, '045', '새마을금고연합회'),
       (21, '048', '신협중앙회'),
       (22, '050', '상호저축은행'),
       (23, '051', '기타 외국계은행'),
       (24, '052', '모건스탠리은행'),
       (25, '054', 'HSBC은행'),
       (26, '055', '도이치은행'),
       (27, '056', '알비에스피엘씨은행'),
       (28, '057', '제이피모간체이스은행'),
       (29, '058', '미즈호코퍼레이트은행'),
       (30, '059', '미쓰비시도쿄UFJ은행'),
       (31, '060', 'BOA'),
       (32, '061', '비엔피파리바은행'),
       (33, '062', '중국공상은행'),
       (34, '063', '중국은행'),
       (35, '064', '산림조합'),
       (36, '065', '대화은행'),
       (37, '071', '가상계좌 채번가능 우체국'),
       (38, '076', '신용보증기금'),
       (39, '077', '기술신용보증기금'),
       (40, '081', '가상계좌 채번가능 하나은행'),
       (41, '088', '가상계좌 채번가능 신한은행'),
       (42, '089', '가상계좌 채번가능 케이뱅크'),
       (43, '090', '카카오뱅크'),
       (44, '092', '토스뱅크'),
       (45, '093', '한국주택금융공사'),
       (46, '094', '서울보증보험'),
       (47, '095', '경찰청'),
       (48, '099', '금융결제원'),
       (49, '209', '동양종합금융증권'),
       (50, '218', '현대증권'),
       (51, '230', '미래에셋증권'),
       (52, '238', '대우증권'),
       (53, '240', '삼성증권'),
       (54, '243', '한국투자증권'),
       (55, '247', 'NH투자증권'),
       (56, '261', '교보증권'),
       (57, '262', '하이투자증권'),
       (58, '263', '에이치엠씨투자증권'),
       (59, '264', '키움증권'),
       (60, '265', '이트레이드증권'),
       (61, '266', 'SK증권'),
       (62, '267', '대신증권'),
       (63, '268', '솔로몬투자증권'),
       (64, '269', '한화증권'),
       (65, '270', '하나대투증권'),
       (66, '278', '신한금융투자'),
       (67, '279', '동부증권'),
       (68, '280', '유진투자증권'),
       (69, '287', '메리츠증권'),
       (70, '289', '엔에이치투자증권'),
       (71, '290', '부국증권'),
       (72, '291', '신영증권'),
       (73, '292', '엘아이지투자증권');


-- Customer 데이터 삽입
<<<<<<< HEAD
INSERT INTO bank_member (email, name, phone_num, ci)
VALUES ('customer21@example.com', '홍길동', '010-1234-5678', 'ID001'),
       ('customer22@example.com', '김영희', '010-2345-6789', 'ID002'),
       ('use2r3@example.com', '이철수', '010-3456-7890', 'ID003'),
       ('us2er4@example.com', '박미영', '010-4567-8901', 'ID004'),
       ('us2r5@example.com', '정우성', '010-5678-9012', 'ID005'),
       ('testuser6@example.com', '김철민', '010-6666-1111', 'ID006'),
       ('testuser7@example.com', '박지은', '010-7777-2222', 'ID007'),
       ('testuser8@example.com', '이동수', '010-8888-3333', 'ID008'),
       ('testuser9@example.com', '최민경', '010-9999-4444','ID009'),
       ('testuser10@example.com', '정하늘', '010-1010-5555', 'ID010'),
       ('testuser11@example.com', '윤도영', '010-2020-6666', 'ID011'),
       ('testuser12@example.com', '강수지', '010-3030-7777', 'ID012'),
       ('testuser13@example.com', '한기훈', '010-4040-8888', 'ID013'),
       ('testuser14@example.com', '서진우', '010-5050-9999', 'ID014'),
       ('testuser15@example.com', '유소정', '010-6060-0000', 'ID015');
-- Account 데이터 삽입
INSERT INTO core_account (id, customer_id, bank_code_id, product_id, account_number, balance, currency, account_type,
                          fintech_use_num, is_deleted)
VALUES (1, 1, 1, 1, '110-1234-5678', 1000000, 'KRW', 'PERSONAL', 'FNUM001', FALSE),
       (2, 2, 2, 1, '120-2345-6789', 2000000, 'KRW', 'PERSONAL', 'FNUM002', FALSE),
       (3, 1, 1, 1, '110-3456-7890', 200000, 'KRW', 'PERSONAL', '0013456789012', FALSE),
       (4, 2, 2, 2, '220-6789-0123', 1500000, 'KRW', 'PERSONAL', '0026789012345', FALSE),
       (5, 3, 3, 2, '330-7890-1234', 300000, 'KRW', 'PERSONAL', '0037890123456', FALSE);

-- Card 데이터 삽입
INSERT INTO core_card (id, customer_id, account_id, card_name, card_number, cvc, is_issued, expired_at, card_status)
VALUES (1, 1, 1, '하나카드', '4862-1234-5678-9012', '123', TRUE, '2025-11-07 00:00:00', TRUE),
       (2, 2, 2, '국민카드', '4862-2345-6789-0123', '456', TRUE, '2025-11-07 00:00:00', TRUE);

-- Product 데이터 삽입
INSERT INTO bank_product (id, bank_id, name, rate)
VALUES (1, 1, '카카오 모임통장 상품', 1.5),
       (2, 2, '우리 일반통장 상품', 1.2);

-- Account 데이터 삽입
INSERT
INTO core_account (id, customer_id, bank_code_id, product_id, account_number, balance, currency, account_type,
                   fintech_use_num, is_deleted)
VALUES (1, 2, 1, 1, '777-7777-7777', 1000000,
        'KRW', 'CORPORATE', '36e1df7d-e5da-42b6-8f18-169e4b05e816', FALSE),
       (2, 5, 2, 1, '120-2345-6789', 2000000,
        'KRW', 'PERSONAL', '9bf53d03-cff5-448a-8c8d-03b36f5c6783',
        FALSE),
       (3, 8, 1, 1, '110-3456-7890', 200000,
        'KRW', 'PERSONAL', 'a7f850c0-8db4-4d12-8cc8-ea4a838fcf18',
        FALSE),
       (4, 9, 10, 1, '220-6789-0123', 1500000,
        'KRW', 'PERSONAL', '7a93e8ce-ccb3-47a1-b7f6-c30991363005',
        FALSE),
       (5, 1, 3, 1, '330-7890-1234', 300000,
        'KRW', 'PERSONAL', '4f8bd5a5-57b4-4258-ba5b-de9581762bb3',
        FALSE),
       (6, 2, 44, 1, '092-0001-0001',
        10000000, 'KRW', 'CORPORATE',
        'cbe1d1d6-c72e-43f9-b6d4-fe1bed13ba3f', FALSE),
       (7, 2, 43, 1, '090-0001-0001',
        15000000, 'KRW', 'CORPORATE',
        '9a34e796-83c2-482c-9c88-a43e45c03547', FALSE),
       (8, 2, 41, 1, '088-0001-0001',
        12000000, 'KRW', 'CORPORATE',
        'e7d0c434-82a7-4a3d-9d4c-0d8431515623', FALSE),
       (9, 2, 10, 1, '020-0001-0001',
        13000000, 'KRW', 'CORPORATE',
        'e4f2c142-997a-4dbb-9b91-1f7003a0f219', FALSE),
       (11, 3, 10, 1, '090-1111-2222',
        50000000, 'KRW', 'CORPORATE',
        '9e9196e7-0469-4424-8c57-076e6f16a284', FALSE);


-- Card 데이터 삽입
INSERT INTO core_card (id, customer_id, account_id, card_name, card_number, cvc, is_issued, expired_at, card_status)
VALUES (1, 1, 1, '하나카드', '4862-1234-5678-9012', '123', TRUE, '2025-11-07 00:00:00', TRUE),
       (2, 2, 2, '국민카드',
        '4862-2345-6789-0123',
        '456', TRUE,
        '2025-11-07 00:00:00',
        TRUE);


-- Subscribe 데이터 삽입
INSERT INTO subscribe (id, bank_id, product_name, business_num, company_name, expire_date, is_subscribe, fee_amount,
                       access_key, secret_key)
VALUES (1, 1, 'API 구독 상품', '123-45-67890', '크루즈', '2025-11-07 00:00:00', TRUE, 100000,
        '7d336211-107f-476e-b910-6afd64503450',
        'vSSqvul8FLDhN2IIQgmn0dy79jOI4hWjRZXgtSAWw1A='),
       (2, 2, 'API 구독 상품', '234-56-78901', 'Crews', '2025-11-07 00:00:00', TRUE, 100000,
        '87d8ee92-6000-40bf-9f3f-f548a683061e',
        'KMq60SuylJxV6/zO7HY67FzQDTlQwjrBUnRJa8xCJMA=');

-- History 데이터 삽입
INSERT INTO core_history (id, core_account_id, core_card, tran_type, tran_amt, after_balance_amt, counterparty_Name,
                          counterparty_account_num, counterparty_bank_code, description)
VALUES (1, 1, 1, 'DEPOSIT', 500000, 1500000, '김치도가', '110-1234-5678', '020', '월급 입금'),
       (2, 2, 2, 'WITHDRAW', 100000, 1900000, '편의점', '120-2345-6789', '020', '편의점 결제');

-- CoreTransaction 데이터 삽입
INSERT INTO transaction (id, status)
VALUES (1, 'SUCCESS'),
       (2, 'SUCCESS');
