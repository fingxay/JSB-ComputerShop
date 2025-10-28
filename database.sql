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
('admin', 'hashed_password_1', 'admin@example.com', 1),
('customer1', 'hashed_password_2', 'customer1@example.com', 2),
('customer2', 'hashed_password_3', 'customer2@example.com', 2),
('staff1', 'hashed_password_4', 'staff1@example.com', 3),
('supplier1', 'hashed_password_5', 'supplier1@example.com', 4);


GO

INSERT INTO dbo.categories (category_name, description) VALUES
('Bàn phím', 'Các loại bàn phím máy tính, bàn phím cơ, gaming'),
('Chuột', 'Chuột máy tính, chuột gaming, chuột không dây'),
('Tai nghe', 'Tai nghe gaming, tai nghe âm thanh, headset'),
('Ghế gaming', 'Ghế gaming, ghế văn phòng chuyên dụng'),
('Linh kiện', 'CPU, RAM, VGA, mainboard và các linh kiện máy tính'),
('Màn hình', 'Màn hình máy tính, màn hình gaming, LCD, LED'),
('Loa', 'Loa máy tính, loa gaming, sound system');

GO

INSERT INTO dbo.images (image_url) VALUES
('/Images/keyboard-1.jpg'),
('/Images/mouse-1.jpg'),
('/Images/headset-1.jpg'),
('/Images/chair-1.jpg'),
('/Images/cpu-1.jpg'),
('/Images/monitor-1.jpg'),
('/Images/speaker-1.jpg'),
('/Images/keyboard-2.jpg'),
('/Images/mouse-2.jpg'),
('/Images/headset-2.jpg');

GO

INSERT INTO dbo.products (product_name, description, price, stock_quantity, category_id, image_id) VALUES
('Bàn phím cơ Logitech G915', 'Bàn phím cơ không dây cao cấp với switch tactile, đèn RGB', 4200000, 50, 1, 1),
('Chuột gaming Razer DeathAdder V3', 'Chuột gaming với sensor Focus Pro 30K, tần số quét 8000Hz', 1890000, 75, 2, 2),
('Tai nghe SteelSeries Arctis 7P', 'Tai nghe gaming không dây với âm thanh 7.1, mic ClearCast', 3200000, 30, 3, 3),
('Ghế gaming DXRacer Formula Series', 'Ghế gaming ergonomic với đệm memory foam, điều chỉnh đa chiều', 8500000, 15, 4, 4),
('CPU AMD Ryzen 7 5800X', 'Bộ vi xử lý 8 core 16 thread, xung nhịp 3.8GHz boost 4.7GHz', 7200000, 25, 5, 5),
('Màn hình ASUS ROG Swift PG279QM', 'Màn hình gaming 27" QHD 240Hz IPS với G-Sync', 15800000, 20, 6, 6),
('Loa Logitech Z623 2.1', 'Hệ thống loa 2.1 với công suất 200W, chứng nhận THX', 2800000, 40, 7, 7),
('Bàn phím Corsair K70 RGB MK.2', 'Bàn phím cơ với switch Cherry MX Red, khung nhôm', 3100000, 35, 1, 8),
('Chuột Logitech G Pro X Superlight', 'Chuột gaming siêu nhẹ 63g với sensor HERO 25K', 2650000, 45, 2, 9),
('Tai nghe HyperX Cloud Alpha S', 'Tai nghe gaming với driver 50mm, 7.1 surround sound', 2400000, 60, 3, 10);

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


