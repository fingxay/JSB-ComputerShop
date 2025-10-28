# Computer Shop - Spring Boot E-commerce Application

## 🛠️ Prerequisites

### 1. Software Requirements
- **Java 17** or higher
- **Maven 3.8+**
- **SQL Server** (LocalDB, Express, or Full)

### 2. Database Setup
1. Start SQL Server service
2. Create database using the provided script:
   ```sql
   sqlcmd -S localhost -U sa -P 123456 -i database.sql
   ```
   Or run `database.sql` in SQL Server Management Studio

### 3. Application Configuration
Database connection is configured in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ComputerShop
spring.datasource.username=sa
spring.datasource.password=123456
```

## 🚀 Running the Application

### Option 1: Using the provided script
```cmd
start-app.bat
```

### Option 2: Manual Maven commands
```cmd
# Compile
mvn clean compile

# Run application
mvn spring-boot:run
```

### Option 3: Using IDE
1. Open project in IntelliJ IDEA / Eclipse
2. Run `MainApplication.java`

## 🌐 Application URLs

- **Homepage**: http://localhost:8080
- **Login**: http://localhost:8080/login
- **Products**: http://localhost:8080/products
- **Cart**: http://localhost:8080/cart
- **Admin Panel**: http://localhost:8080/admin (requires admin role)

## 👤 Default Users

After running `database.sql`, you'll have:
- **Admin**: username=`admin`, password=`admin123`
- **User**: username=`user`, password=`user123`

## 🛒 Features

### Customer Features
- Browse products by category
- Search products
- Add to cart (session-based)
- User registration/login
- Order checkout
- View order history

### Admin Features  
- Dashboard with statistics
- User management
- Product CRUD operations
- Category management
- Order oversight

## 🏗️ Architecture

- **Backend**: Spring Boot 3.5.6
- **Frontend**: Thymeleaf + CSS/JavaScript
- **Database**: SQL Server with JPA/Hibernate
- **Session**: HTTP sessions for cart & auth

## 📁 Project Structure

```
src/main/java/com/computershop/main/
├── controllers/     # MVC Controllers
├── entities/        # JPA Entities
├── repositories/    # Data Access Layer
├── services/        # Business Logic
└── MainApplication.java

src/main/resources/
├── templates/       # Thymeleaf HTML templates
├── static/CSS/      # Stylesheets
├── static/JavaScript/ # Client-side scripts
└── application.properties
```

## 🔧 Troubleshooting

### Database Connection Issues
1. Verify SQL Server is running: `services.msc` → SQL Server service
2. Test connection: `sqlcmd -S localhost -U sa -P 123456`
3. Check firewall settings for port 1433

### Port Already in Use
Change port in `application.properties`:
```properties
server.port=8081
```

### Build Issues
Clean and rebuild:
```cmd
mvn clean install
```

## 📝 Development Notes

- Controllers handle HTTP requests and return Thymeleaf views
- Services contain business logic and transaction management  
- Repositories use Spring Data JPA for database operations
- Entities are mapped to SQL Server tables with proper relationships
- CSS is separated from HTML templates for maintainability
This project builds a full-featured e-commerce platform using Spring Boot (REST API) and React/Vue. It supports User flows (browse, cart, secure checkout, order tracking) and an Admin module for comprehensive management (products, inventory, orders, users, reporting). Architecture includes MySQL payment integration (VNPay/Momo).

FINAL PROJECT REQUIREMENTS
1. System Overview
Objective: Build an e-commerce website that allows users to browse products, shopping carts, payments, and manage orders; Admins manage products, inventory, orders, users, and reports.
Proposed architecture:
Backend: Spring Boot (RESTful API), Spring Data JPA (Hibernate), Spring Security (JWT or session), Spring MVC if using server-side rendering.
Database: MySQL/PostgreSQL/SQLServer
Frontend: Option A: SPA (React/Vue) calls REST API — recommended. Option B: Thymeleaf (server-side) if you want pure Java.
Image file storage: local (dev) or S3/GCS (prod).
Payment: integrated payment gateway (VNPay, Momo, Stripe) — webhook processing.
Environment: Docker, CI/CD (GitHub Actions/GitLab CI), deploy Kubernetes or VPS.
2. Roles & Permissions
Anonymous (Guest): browse products, search, view details, add to cart (temporarily local), create account.
User (Customer): all customer rights + order, payment, view history, manage profile, reviews, wishlist.
Seller (if multi-vendor): (optional) manage their own products.
Admin: manage products, categories, inventory, orders, users, promotions, reports, system settings.
Support/Staff: process orders, respond to customers (depending on design).
3. FUNCTIONAL REQUIREMENTS — USER PART (User / Customer)
3.1. Register & Login
Register with email + password, email authentication (token).
Login with email/password
Forgot password: send reset email (short-term token).
2-step authentication (2FA) — optional.
3.2. User profile (Profile)
View/edit: name, email, phone number, address (multiple addresses), avatar.
Save default shipping address, billing address.
Activity history (orders, reviews).
3.3. Browse & Search products
Product hierarchy category.
Filters: price, brand, color, size, stock status, rating, promotion.
Sort: popular, newest, price (ascending/decreasing), rating.
Full-text search (Elasticsearch or MySQL fulltext).
List page + pagination + lazy loading.
3.4. Product detail page
Images (gallery), description, specifications, variants (color/size), SKU, inventory by variant, original price & promotional price, reviews & comments, questions & answers.
Add to cart (select variant, quantity), “Buy now”.
3.5. Cart
Save cart for logged-in users (including session memory for guests).
Update quantity, delete item, apply discount code.
Check inventory when updating quantity/checkout.
3.6. Checkout
Multi-step checkout: delivery information → shipping → payment → confirmation.
Support multiple payment methods: cash on delivery (COD), payment gateway (VNPay/Momo/Stripe), bank transfer.
Secure transaction processing (3DS if needed), save transaction logs.
Support invoice / print invoice (PDF).
3.7. Order Management (User)
View order list, status (Pending, Paid, Processing, Shipped, Delivered, Cancelled, Returned, Refunded).
View order details, status history, tracking number (if any).
Request to cancel order, request to exchange/return (with reason & processing status).
Rate product after receiving (rating + comment). Regulations: only the purchasing user can rate.
3.8. Discount code / Voucher
Apply discount code at checkout, check conditions (expiration date, number of times, minimum value, product type).
3.9. Notification
Email/SMS/Notification in-app for order status, promotions, reset password.
Configure email template.
3.10. Wishlist / Favorites
Save favorite products, quickly transfer to cart.
3.11. Chat / Support
Chat widget (live chat) or simple contact form + ticket.
4. FUNCTION REQUIREMENTS — ADMINISTRATION SECTION (Admin)
Admin interface only for authorized admin/staff, UI: Dashboard + sidebar menu.
4.1. Authentication & Authorization
Admin login (2FA required).
Role management & granular rights (RBAC): SuperAdmin, ProductManager, OrderManager, Marketing, Support.
4.2. Product management
Product CRUD: name, slug, description, category, brand, attributes (size, color), variants (SKU), image (multi), price (original, sale), weight, dimensions, tag.
Integrated import/export (CSV/Excel).
Status management: Draft / Published / Archived.
SEO settings (meta title, meta desc, OG image).
4.3. Category & Brand Management
CRUD category (recursive), tags, brands.
4.4. Inventory Management
Inventory information by variant (warehouse if multi-warehouse).
Update import, export, transfer.
Low inventory warning (threshold), inventory report.
4.5. Order Management
View & filter orders by status, date, customer.
Change status, print invoice, create bill of lading, assign tracking number.
Process refunds, partial refunds, create credit notes.
Internal notes, upload documents (e.g. transfer files).
4.6. User Management
View user list, lock/unlock accounts, grant roles, view order & transaction history.
4.7. Promotion & Discount Code Management
Create coupons: % discount, fixed amount, free shipping, applicable conditions (by product/category/total amount), expiry date, number limit.
4.8. Content Management (CMS)
Static pages (About, Policy), banners, sliders, blog/news (optional).
Met management