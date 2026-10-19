-- PostgreSQL 16: chay tung lenh bang tai khoan quan tri, ngoai transaction.
-- Chi chay khi role/database CHUA ton tai. Khong xoa database dang co.
CREATE ROLE tutor_app LOGIN;
-- Trong psql, dat mat khau rieng bang: \password tutor_app
CREATE DATABASE tutor_connect OWNER tutor_app ENCODING 'UTF8' TEMPLATE template0;
-- Flyway tao bang va danh muc mon; khong chay migration MySQL cu.
