# Fleet Management System

A production-grade backend system for managing fleet vehicles, drivers, trips, and maintenance operations.

## 🚀 Features

### Core Functionality
- **User Management**: JWT-based authentication with role-based access control (Admin, Fleet Manager, Driver)
- **Vehicle Management**: Complete CRUD operations with status tracking (Available, In Use, Maintenance, Retired)
- **Driver Management**: Driver profiles linked to users with license tracking and performance metrics
- **Trip Management**: Trip scheduling, tracking, and completion with automatic status updates
- **Maintenance System**: Maintenance scheduling, tracking, and email notifications
- **Document Management**: Upload and manage vehicle documents (Insurance, Registration, etc.)

### Advanced Features
- **Analytics Dashboard**: Fleet utilization, driver performance, vehicle statistics, monthly trends
- **Currency Conversion**: Real-time exchange rates via external API
- **Email Notifications**: Automated alerts for maintenance due dates and completions
- **File Upload**: Support for PDF and image documents with validation
- **Caching**: Optimized performance with Spring Cache
- **Rate Limiting**: 100 requests per minute per IP
- **Pagination & Filtering**: All list endpoints support pagination, sorting, and filtering

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.2.x
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Validation**: Jakarta Validation
- **Documentation**: Swagger/OpenAPI
- **Caching**: Spring Cache
- **Email**: Spring Mail (SMTP)
- **External API**: WebClient for HTTP requests

## 📋 Prerequisites

- JDK 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- SMTP server (Gmail recommended for testing)

## ⚙️ Installation & Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd fleet-management
```

### 2. Database Setup
```sql
CREATE DATABASE fleet_management;
```

### 3. Configure Application
Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/fleet_management
spring.datasource.username=postgres
spring.datasource.password=your_password

# JWT
jwt.secret=your-secret-key-minimum-256-bits
jwt.expiration=86400000

# Email (Gmail)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### 4. Run Application
```bash
mvn clean install
mvn spring-boot:run
```

Application starts on: `http://localhost:8080`

## 📚 API Documentation

**Swagger UI**: http://localhost:8080/swagger-ui.html

### Key Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

#### Vehicles
- `POST /api/vehicles` - Create vehicle
- `GET /api/vehicles` - Get all vehicles (paginated)
- `GET /api/vehicles/{id}` - Get vehicle by ID
- `PUT /api/vehicles/{id}` - Update vehicle
- `DELETE /api/vehicles/{id}` - Delete vehicle
- `GET /api/vehicles/search?keyword=` - Search vehicles
- `GET /api/vehicles/filter/status?status=AVAILABLE` - Filter by status

#### Drivers
- `POST /api/drivers` - Create driver
- `GET /api/drivers` - Get all drivers
- `GET /api/drivers/filter/rating?minRating=4.0&maxRating=5.0` - Filter by rating

#### Trips
- `POST /api/trips` - Create trip
- `PUT /api/trips/{id}/start` - Start trip
- `PUT /api/trips/{id}/complete` - Complete trip
- `GET /api/trips/filter/date-range` - Filter by date range

#### Maintenance
- `POST /api/maintenance` - Create maintenance record
- `GET /api/maintenance/filter/vehicle/{vehicleId}` - Get maintenance by vehicle
- `POST /api/maintenance/send-alerts` - Send maintenance alerts

#### Documents
- `POST /api/documents` - Upload document (multipart/form-data)
- `GET /api/documents/{id}/download` - Download document

#### Analytics
- `GET /api/analytics/fleet-utilization` - Fleet utilization metrics
- `GET /api/analytics/driver-performance` - Driver performance
- `GET /api/analytics/vehicle-statistics` - Vehicle stats
- `GET /api/analytics/monthly-trip-stats?year=2026` - Monthly trends
- `GET /api/analytics/top-drivers?limit=5` - Top performing drivers

#### Currency Conversion
- `POST /api/currency/convert` - Convert currency
- `GET /api/currency/rates/{baseCurrency}` - Get exchange rates

## 🗂️ Project Structure

```
src/main/java/com/fleetmanagement/fleetsystem/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── exception/      # Exception handlers
├── model/          # Entity classes
├── repository/     # JPA repositories
├── service/        # Business logic
└── util/           # Utility classes
```

## 🧪 Testing

### Sample Test Flow
1. Register user: `POST /api/auth/register`
2. Login: `POST /api/auth/login` (get JWT token)
3. Create vehicle: `POST /api/vehicles`
4. Create driver: `POST /api/drivers`
5. Create trip: `POST /api/trips`
6. Complete trip: `PUT /api/trips/{id}/complete`
7. View analytics: `GET /api/analytics/fleet-utilization`

### Postman Collection
Import the API collection from Swagger for easy testing.

## 📊 Database Schema

### Key Tables
- `users` - User accounts
- `drivers` - Driver profiles (linked to users)
- `vehicles` - Fleet vehicles
- `trips` - Trip records
- `maintenance` - Maintenance records
- `documents` - Vehicle documents

### Relationships
- Driver → User (One-to-One)
- Trip → Vehicle (Many-to-One)
- Trip → Driver (Many-to-One)
- Maintenance → Vehicle (Many-to-One)
- Document → Vehicle (Many-to-One)

## 🔒 Security

- Password encryption with BCrypt
- JWT token-based authentication
- Role-based access control
- Rate limiting (100 req/min per IP)
- Input validation on all endpoints
- File upload size limits (5MB)

## 📧 Email Configuration

For Gmail:
1. Enable 2-Step Verification
2. Generate App Password
3. Use app password in `application.properties`

## 🚀 Deployment

### Environment Variables
Set these in production:
- `DATABASE_URL`
- `JWT_SECRET`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

### Docker (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 📝 Future Enhancements

- Real-time GPS tracking integration
- Mobile app support
- Advanced reporting with charts
- Multi-tenant support
- WebSocket for live updates

## 👨‍💻 Developer

Created as a Backend Engineering project using Spring Boot.

## 📄 License

MIT License
