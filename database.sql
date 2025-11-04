-- database reset and creation


USE master;
GO
IF DB_ID(N'computershop') IS NOT NULL
BEGIN
    ALTER DATABASE [computershop] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE [computershop];
END
GO
CREATE DATABASE [computershop];
GO
USE [computershop];
GO
-- 

IF NOT EXISTS (SELECT * FROM sys.sql_logins WHERE name = N'fuongtuan')
BEGIN
    CREATE LOGIN [fuongtuan] WITH PASSWORD=N'toilabanhmochi', DEFAULT_DATABASE=[computershop], CHECK_EXPIRATION=OFF, CHECK_POLICY=OFF;
END
-- database schema


IF OBJECT_ID(N'dbo.roles', N'U') IS NOT NULL DROP TABLE dbo.roles;
CREATE TABLE dbo.roles (
    role_id INT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);
GO

IF OBJECT_ID(N'dbo.users', N'U') IS NOT NULL DROP TABLE dbo.users;
CREATE TABLE dbo.users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    roleid INT NULL,

    CONSTRAINT FK_users_roles FOREIGN KEY (roleid) REFERENCES dbo.roles(role_id)
);
GO

IF OBJECT_ID(N'dbo.categories', N'U') IS NOT NULL DROP TABLE dbo.categories;
CREATE TABLE dbo.categories (
    category_id INT IDENTITY(1,1) PRIMARY KEY,
    category_name NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(MAX) NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE()
);
GO

IF OBJECT_ID(N'dbo.images', N'U') IS NOT NULL DROP TABLE dbo.images;
CREATE TABLE dbo.images (
    image_id INT IDENTITY(1,1) PRIMARY KEY,
    image_url NVARCHAR(MAX) NOT NULL
);
GO

IF OBJECT_ID(N'dbo.products', N'U') IS NOT NULL DROP TABLE dbo.products;
CREATE TABLE dbo.products (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description NVARCHAR(MAX) NULL,
    price DECIMAL(18,2) NOT NULL,
    stock_quantity INT NOT NULL,
    category_id INT NULL,
    image_id INT NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_products_categories FOREIGN KEY (category_id) REFERENCES dbo.categories(category_id),
    CONSTRAINT FK_products_images FOREIGN KEY (image_id) REFERENCES dbo.images(image_id)
);
GO

IF OBJECT_ID(N'dbo.orders', N'U') IS NOT NULL DROP TABLE dbo.orders;
CREATE TABLE dbo.orders (
    order_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    order_date DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_orders_users FOREIGN KEY (user_id) REFERENCES dbo.users(user_id)
);

IF OBJECT_ID(N'dbo.order_details', N'U') IS NOT NULL DROP TABLE dbo.order_details;
CREATE TABLE dbo.order_details (
    order_detail_id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,

    CONSTRAINT FK_order_details_orders FOREIGN KEY (order_id) REFERENCES dbo.orders(order_id),
    CONSTRAINT FK_order_details_products FOREIGN KEY (product_id) REFERENCES dbo.products(product_id)
);

-- initial data seeding
INSERT INTO dbo.roles (role_id, role_name) VALUES
(1, 'admin'),
(2, 'customer'),
(3, 'staff'),
(4, 'supplier');

GO

INSERT INTO dbo.users (username, password_hash, email, roleid) VALUES
('customer1', '123456_hashed', 'customer1@example.com', 2),
('customer2', '123456_hashed', 'customer2@example.com', 2),
('staff1', '123456_hashed', 'staff1@example.com', 3),
('supplier1', '123456_hashed', 'supplier1@example.com', 4),
('admin', '123456_hashed', 'admin@example.com', 1);

GO

INSERT INTO dbo.categories (category_name, description) VALUES
('Keyboards', 'Computer keyboards, mechanical keyboards, gaming keyboards'),
('Mice', 'Computer mice, gaming mice, wireless mice'),
('Headsets', 'Gaming headsets, audio headphones, communication headsets'),
('Gaming Chairs', 'Gaming chairs, office chairs, ergonomic seating'),
('Components', 'CPU, RAM, GPU, motherboard and computer components'),
('Monitors', 'Computer monitors, gaming displays, LCD, LED screens'),
('Speakers', 'Computer speakers, gaming audio, sound systems');

GO

INSERT INTO dbo.images (image_url) VALUES
('https://www.logitechg.com/content/dam/gaming/en/products/g915/g915-gallery-2.png'),
('https://product.hstatic.net/200000722513/product/thumbchuot_6ed5e43202c9498aacde369cb95573b3_0859ba8bea744152819e77b7e6d0c7f0_master.gif'),
('https://owlgaming.vn/wp-content/uploads/2024/06/ARCTIS-7P-1.jpg'),
('https://www.dxracer-europe.com/bilder/artiklar/32125.jpg?m=1747120248'),
('https://bizweb.dktcdn.net/100/329/122/files/amd-5700g-02.jpg?v=1633579298069'),
('https://dlcdnwebimgs.asus.com/gain/72C16A36-4EE3-4AC4-A58A-35F6B8A2FB6F/w717/h525/fwebp'),
('https://bizweb.dktcdn.net/thumb/grande/100/487/147/products/loa-logitech-speaker-system-z623-eu-246c339d-c1e4-4b1d-9700-1ee364c0ec0d.jpg?v=1691140134430'),
('https://cdn2.cellphones.com.vn/x/media/catalog/product/_/0/_0000_43020_keyboard_corsair_k70_rgb_m.jpg'),
('https://product.hstatic.net/200000722513/product/g-pro-x-superlight-wireless-black-666_83650815ce2e486f9108dbbb17c29159_1450bb4a9bd34dcb92fc77f627eb600d.jpg'),
('https://row.hyperx.com/cdn/shop/products/hyperx_cloud_alpha_s_blackblue_1_main.jpg?v=1662567757&width=1920');

GO

INSERT INTO dbo.products (product_name, description, price, stock_quantity, category_id, image_id) VALUES
('Logitech G915 Mechanical Keyboard', 'Premium wireless mechanical keyboard with tactile switches, RGB lighting', 4200000, 50, 1, 1),
('Razer DeathAdder V3 Gaming Mouse', 'Gaming mouse with Focus Pro 30K sensor, 8000Hz polling rate', 1890000, 75, 2, 2),
('SteelSeries Arctis 7P Headset', 'Wireless gaming headset with 7.1 audio, ClearCast microphone', 3200000, 30, 3, 3),
('DXRacer Formula Series Gaming Chair', 'Ergonomic gaming chair with memory foam padding, multi-directional adjustment', 8500000, 15, 4, 4),
('AMD Ryzen 7 5800X CPU', '8-core 16-thread processor, 3.8GHz base clock, 4.7GHz boost', 7200000, 25, 5, 5),
('ASUS ROG Swift PG279QM Monitor', '27" QHD 240Hz IPS gaming monitor with G-Sync technology', 15800000, 20, 6, 6),
('Logitech Z623 2.1 Speakers', '2.1 speaker system with 200W power output, THX certified', 2800000, 40, 7, 7),
('Corsair K70 RGB MK.2 Keyboard', 'Mechanical keyboard with Cherry MX Red switches, aluminum frame', 3100000, 35, 1, 8),
('Logitech G Pro X Superlight Mouse', 'Ultra-lightweight 63g gaming mouse with HERO 25K sensor', 2650000, 45, 2, 9),
('HyperX Cloud Alpha S Headset', 'Gaming headset with 50mm drivers, 7.1 surround sound', 2400000, 60, 3, 10);

GO

INSERT INTO dbo.orders (user_id, order_date) VALUES
(2, GETDATE()),
(3, GETDATE());
GO

INSERT INTO dbo.order_details (order_id, product_id, quantity, price) VALUES
(1, 1, 2, 39.98),
(1, 2, 1, 29.99),
(2, 2, 2, 59.98),
(2, 3, 1, 39.99);

GO


-- Insert sample data for testing products page

-- Insert additional images
INSERT INTO images (image_url) VALUES
('/Images/keyboard1.jpg'),
('/Images/keyboard2.jpg'),
('/Images/mouse1.jpg'),
('/Images/mouse2.jpg'),
('/Images/headset1.jpg'),
('/Images/headset2.jpg'),
('/Images/chair1.jpg'),
('/Images/ssd1.jpg'),
('/Images/ram1.jpg'),
('/Images/gpu1.jpg');

-- Insert additional products
INSERT INTO products (product_name, description, price, stock_quantity, category_id, image_id, created_at) VALUES
-- Keyboards
('Razer BlackWidow V3 Mechanical Keyboard', 'Gaming mechanical keyboard with Razer Green switches, RGB Chroma', 3200000, 25, 1, 1, GETDATE()),
('Corsair K70 RGB MK.2 Keyboard', 'Mechanical keyboard with Cherry MX Red switches, aluminum frame', 2850000, 18, 1, 2, GETDATE()),
('Logitech G915 TKL Keyboard', 'Wireless mechanical keyboard, GL Tactile switches, ultra-slim design', 4200000, 12, 1, 1, GETDATE()),

-- Mice
('Logitech G502 HERO Gaming Mouse', 'Wired gaming mouse, HERO 25K sensor, 11 programmable buttons', 1450000, 35, 2, 3, GETDATE()),
('Razer DeathAdder V3 Gaming Mouse', 'Ergonomic gaming mouse, Focus Pro 30K sensor', 1650000, 28, 2, 4, GETDATE()),
('Corsair M65 RGB ELITE Gaming Mouse', 'FPS gaming mouse, aluminum frame, adjustable weight', 1380000, 22, 2, 3, GETDATE()),

-- Headsets
('SteelSeries Arctis 7 Headset', 'Wireless gaming headset, DTS Headphone:X 2.0', 3800000, 15, 3, 5, GETDATE()),
('HyperX Cloud II Gaming Headset', 'Gaming headset with microphone, virtual 7.1 audio', 1950000, 30, 3, 6, GETDATE()),
('Audio-Technica ATH-M50xBT2 Headphones', 'Bluetooth studio headphones, high-quality audio', 4200000, 8, 3, 5, GETDATE()),

-- Gaming Chairs
('DXRacer Formula Series Gaming Chair', 'Ergonomic gaming chair, premium PU leather, multi-directional adjustment', 8500000, 6, 4, 7, GETDATE()),
('Noblechairs EPIC Gaming Chair', 'Premium gaming chair, real leather, sturdy steel frame', 15200000, 3, 4, 7, GETDATE()),

-- Components
('Samsung 980 PRO 1TB SSD', 'NVMe PCIe 4.0 SSD, 7000MB/s read speed', 2850000, 45, 5, 8, GETDATE()),
('Corsair Vengeance LPX 16GB RAM', 'DDR4 3200MHz RAM, 2x8GB kit, aluminum heat spreader', 1650000, 38, 5, 9, GETDATE()),
('MSI RTX 4070 Ti SUPER Graphics Card', 'High-end gaming graphics card, 16GB GDDR6X', 25500000, 8, 5, 10, GETDATE()),
('Kingston NV2 500GB SSD', 'Budget NVMe SSD, good performance for office use', 950000, 52, 5, 8, GETDATE()),
('G.Skill Ripjaws V 32GB RAM', 'DDR4 3600MHz RAM, 2x16GB kit, high performance', 3200000, 15, 5, 9, GETDATE());

-- Update some products to have low stock for testing
UPDATE products SET stock_quantity = 2 WHERE product_name LIKE '%Noblechairs%';
UPDATE products SET stock_quantity = 4 WHERE product_name LIKE '%Audio-Technica%';
UPDATE products SET stock_quantity = 0 WHERE product_name LIKE '%G915%'; -- Out of stock for testing