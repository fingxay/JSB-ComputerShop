use computershop;

-- Xem user hiện tại
SELECT user_id, username, email, password_hash FROM users;

-- Fix password cho user có password chưa hash (không có '_hashed')
-- Chỉ update những user có password chưa đúng format
UPDATE users 
SET password_hash = password_hash + '_hashed'
WHERE password_hash NOT LIKE '%_hashed';

-- Kiểm tra lại
SELECT user_id, username, email, password_hash FROM users;