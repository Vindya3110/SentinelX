# ShopVista Backend

A comprehensive e-commerce backend REST API built with Spring Boot, PostgreSQL, and Gradle.

## Overview

ShopVista Backend is a full-featured Spring Boot application that provides REST APIs for managing products and orders in an e-commerce platform. The application is built following the same architecture as the reference app_server implementation.

## Features

### Product Management
- Create, read, update, and delete products
- Categorize products
- Search and filter products
- Manage product inventory
- Activate/deactivate products

### Order Management
- Create and manage customer orders
- Track order status (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED, RETURNED)
- Update shipping and tracking information
- Search orders by customer
- Manage payment methods

## Technology Stack

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Build Tool**: Gradle
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Logging**: SLF4J with Logback
- **Testing**: JUnit 5

## Project Structure

```
shopvista-backend/
├── src/
│   ├── main/
│   │   ├── java/com/shopvista/
│   │   │   ├── config/          # Spring configuration classes
│   │   │   ├── controller/      # REST API endpoints
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── service/         # Business logic
│   │   │   └── ShopvistaApplication.java  # Main application class
│   │   └── resources/
│   │       ├── application.properties     # Main configuration
│   │       └── application-dev.properties # Development configuration
│   └── test/
│       └── java/com/shopvista/
│           └── ShopvistaApplicationTests.java
├── build.gradle       # Gradle build configuration
└── settings.gradle    # Gradle settings
```

## Configuration

### Database Setup

Update `src/main/resources/application.properties` with your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://<HOST>:<PORT>/<DB_NAME>
spring.datasource.username=<DB_USERNAME>
spring.datasource.password=<DB_PASSWORD>
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Example PostgreSQL Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shopvista_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

## Building and Running

### Prerequisites

- Java 21 or higher
- PostgreSQL 12 or higher
- Gradle (or use gradlew)

### Build the Application

```bash
./gradlew clean build
```

### Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Run Tests

```bash
./gradlew test
```

## API Endpoints

### Product Endpoints

- `GET /api/v1/products` - Get all products
- `GET /api/v1/products/active` - Get active products
- `GET /api/v1/products/{id}` - Get product by ID
- `GET /api/v1/products/sku/{sku}` - Get product by SKU
- `GET /api/v1/products/category/{category}` - Get products by category
- `GET /api/v1/products/search?name=...` - Search products by name
- `POST /api/v1/products` - Create a new product
- `PUT /api/v1/products/{id}` - Update a product
- `DELETE /api/v1/products/{id}` - Delete a product
- `PATCH /api/v1/products/{id}/deactivate` - Deactivate a product

### Order Endpoints

- `GET /api/v1/orders` - Get all orders
- `GET /api/v1/orders/{id}` - Get order by ID
- `GET /api/v1/orders/number/{orderNumber}` - Get order by order number
- `GET /api/v1/orders/customer/{email}` - Get orders by customer email
- `GET /api/v1/orders/status/{status}` - Get orders by status
- `GET /api/v1/orders/search?name=...` - Search orders by customer name
- `POST /api/v1/orders` - Create a new order
- `PUT /api/v1/orders/{id}` - Update an order
- `PATCH /api/v1/orders/{id}/status?status=...` - Update order status
- `PATCH /api/v1/orders/{id}/tracking?trackingNumber=...` - Update tracking number
- `DELETE /api/v1/orders/{id}` - Delete an order

## Entity Models

### Product
- id (Long) - Primary key
- sku (String) - Unique product code
- name (String) - Product name
- description (String) - Product description
- price (BigDecimal) - Product price
- quantity (Integer) - Available quantity
- category (String) - Product category
- imageUrl (String) - Product image URL
- isActive (Boolean) - Active status
- createdAt (Long) - Creation timestamp
- updatedAt (Long) - Last update timestamp

### Order
- id (Long) - Primary key
- orderNumber (String) - Unique order number
- customerName (String) - Customer name
- customerEmail (String) - Customer email
- shippingAddress (String) - Shipping address
- totalAmount (BigDecimal) - Order total
- status (OrderStatus) - Order status enum
- paymentMethod (String) - Payment method used
- trackingNumber (String) - Shipping tracking number
- createdAt (Long) - Creation timestamp
- updatedAt (Long) - Last update timestamp

## Development

### Code Style

- Use Lombok annotations for reducing boilerplate code
- Follow standard Java naming conventions
- Use logging extensively for debugging
- Implement proper exception handling in services

### Adding New Features

1. Create entity class in `entity/` package
2. Create repository interface in `repository/` package extending `JpaRepository`
3. Create service class in `service/` package with business logic
4. Create controller class in `controller/` package with REST endpoints
5. Create DTO classes in `dto/` package for request/response
6. Write unit tests in `src/test/java`

## Troubleshooting

### Database Connection Issues
- Verify PostgreSQL is running
- Check connection URL, username, and password
- Ensure the database exists and user has proper permissions

### Build Failures
- Clear cache: `./gradlew clean`
- Check Java version compatibility (requires Java 21+)
- Verify all dependencies are available

### Port Already in Use
- Change the port in `application.properties`:
  ```properties
  server.port=8081
  ```

## License

This project is part of the SentinelX Final Year Project.

## Support

For issues or questions, please refer to the project documentation or contact the development team.
