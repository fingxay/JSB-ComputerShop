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

IF OBJECT_ID(N'dbo.images', N'U') IS NOT NULL DROP TABLE dbo.images;
CREATE TABLE dbo.images (
    image_id INT IDENTITY(1,1) PRIMARY KEY,
    image_url NVARCHAR(MAX) NOT NULL
);

IF OBJECT_ID(N'dbo.products', N'U') IS NOT NULL DROP TABLE dbo.products;
CREATE TABLE dbo.products (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description NVARCHAR(MAX) NULL,
    price DECIMAL(18,2) NOT NULL,
    stock_quantity INT NOT NULL,
    image_id INT NULL,

    CONSTRAINT FK_products_images FOREIGN KEY (image_id) REFERENCES dbo.images(image_id)
);

IF OBJECT_ID(N'dbo.orders', N'U') IS NOT NULL DROP TABLE dbo.orders;
CREATE TABLE dbo.orders (
    order_id INT IDENTITY(1,1) PRIMARY KEY,
    
);
GO





