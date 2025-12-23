# Getting Started with ShopVista Backend

## Quick Start Guide

### Prerequisites
- Java 21 or higher
- PostgreSQL database
- Git

### Step 1: Database Setup

1. Install PostgreSQL if not already installed
2. Create a new database:
```sql
CREATE DATABASE shopvista_db;
CREATE USER shopvista_user WITH PASSWORD 'your_secure_password';
ALTER ROLE shopvista_user SET client_encoding TO 'utf8';
ALTER ROLE shopvista_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE shopvista_user SET default_transaction_deferrable TO on;
ALTER ROLE shopvista_user SET default_transaction_read_only TO off;
GRANT ALL PRIVILEGES ON DATABASE shopvista_db TO shopvista_user;
```

### Step 2: Configure Application

Edit `src/main/resources/application.properties` and update the database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shopvista_db
spring.datasource.username=shopvista_user
spring.datasource.password=your_secure_password
```

### Step 3: Build and Run

```bash
# Build the project
./gradlew clean build -x test

# Run the application
./gradlew bootRun
```

The application will start at `http://localhost:8080`

### Step 4: Test the API

#### Create a Product
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "PROD001",
    "name": "Sample Product",
    "description": "A sample product",
    "price": 99.99,
    "quantity": 100,
    "category": "Electronics",
    "imageUrl": "https://example.com/image.jpg",
    "isActive": true
  }'
```

#### Get All Products
```bash
curl http://localhost:8080/api/v1/products
```

#### Create an Order
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "ORD001",
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "shippingAddress": "123 Main St, City, Country",
    "totalAmount": 199.99,
    "status": "PENDING",
    "paymentMethod": "Credit Card"
  }'
```

#### Get All Orders
```bash
curl http://localhost:8080/api/v1/orders
```

## Project Structure

- **Entity Models**: `src/main/java/com/shopvista/entity/`
  - `Product.java` - Product entity
  - `Order.java` - Order entity
  - `OrderStatus.java` - Order status enum

- **Services**: `src/main/java/com/shopvista/service/`
  - `ProductService.java` - Product business logic
  - `OrderService.java` - Order business logic

- **Controllers**: `src/main/java/com/shopvista/controller/`
  - `ProductController.java` - Product API endpoints
  - `OrderController.java` - Order API endpoints

- **Repositories**: `src/main/java/com/shopvista/repository/`
  - `ProductRepository.java` - Product data access
  - `OrderRepository.java` - Order data access

## Available Profiles

- **default** (development): Uses local configuration
- **dev**: Development environment settings in `application-dev.properties`
- **production**: Production settings (set in Docker environment)

## Building Docker Image

```bash
./gradlew clean build -x test
docker build -t shopvista-backend:latest .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/shopvista_db" \
  -e SPRING_DATASOURCE_USERNAME="shopvista_user" \
  -e SPRING_DATASOURCE_PASSWORD="your_password" \
  shopvista-backend:latest
```

## Troubleshooting

### Issue: Connection refused
- Ensure PostgreSQL is running
- Check the database URL and credentials in `application.properties`

### Issue: Build fails
- Run `./gradlew clean` to remove cached files
- Ensure Java 21 is installed: `java -version`

### Issue: Port 8080 already in use
- Change the port in `application.properties`:
  ```properties
  server.port=8081
  ```

## Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## Support

For more information, see the `README.md` file.
