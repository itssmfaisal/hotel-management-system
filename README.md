# Hotel Management System

A comprehensive hotel management application built with JavaFX and MySQL, featuring role-based access control for administrators and customers.

## Features

### Authentication System
- **Login & Registration**: Secure user authentication with username and password
- **Role-Based Access**: Two distinct user roles (Admin and Customer)
- **Session Management**: Maintains user session throughout the application
- **Default Accounts**:
  - Admin: `username: admin`, `password: admin123`
  - Customer: `username: customer`, `password: customer123`

### Admin Dashboard (Full Access)
- **Room Management**
  - Add new rooms with room number, type, and price
  - Update existing room details
  - Delete rooms
  - View all rooms with their status (Available/Occupied)
  - Room types: Single, Double, Suite, Deluxe, Presidential

- **Guest Management**
  - Add new guests with personal information
  - Update guest details
  - Delete guest records
  - Track guest contact information (name, phone, email, ID number)

- **Booking Management**
  - Create new bookings
  - View all bookings with detailed information
  - Check out guests (marks booking as completed)
  - Delete bookings
  - Automatic room status updates (Available ↔ Occupied)
  - View booking details:
    - Guest name
    - Room number
    - Check-in and check-out dates
    - Number of days
    - Price per day
    - Total amount
    - Booking status (Active/Completed)

### Customer Dashboard (Limited Access)
- **Browse Available Rooms**
  - View all available rooms
  - Filter rooms by type
  - See room prices and details

- **Make Bookings**
  - Select room from available options
  - Choose check-in and check-out dates
  - Automatic price calculation based on:
    - Number of days
    - Room price per night
  - Real-time total amount display

- **Manage My Bookings**
  - View personal booking history
  - See booking details (dates, price, status)
  - Cancel active bookings
  - Track completed bookings

### Technical Features
- **Database Integration**: MySQL database for persistent storage
- **Automatic Calculations**:
  - Total booking amount (days × price per night)
  - Number of days staying
- **Data Validation**: Input validation for all forms
- **Responsive UI**: Clean and modern JavaFX interface
- **Real-time Updates**: Tables refresh automatically after operations

## Technologies Used

- **Java 25**: Core programming language
- **JavaFX 21**: User interface framework
- **MySQL 8.0**: Database management system
- **Maven**: Build and dependency management
- **JDBC**: Database connectivity

## Prerequisites

Before running this application, ensure you have the following installed:

1. **Java Development Kit (JDK) 21 or higher**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Verify installation: `java -version`

2. **MySQL Server 8.0 or higher**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Install and start MySQL service

3. **Maven** (Usually bundled with IDE or can be installed separately)
   - Download from: https://maven.apache.org/download.cgi
   - Verify installation: `mvn -version`

4. **IDE** (Recommended)
   - IntelliJ IDEA, Eclipse, or NetBeans
   - VS Code with Java extensions

## Database Setup

### 1. Configure MySQL Connection

Open the file: `src/main/java/com/example/hotelmanagementmine/util/DatabaseUtil.java`

Update the following credentials to match your MySQL setup:

```java
private static final String URL = "jdbc:mysql://localhost:3306/hotel_management";
private static final String USER = "root";          // Your MySQL username
private static final String PASSWORD = "";          // Your MySQL password
```

### 2. Database Auto-Initialization

The application will automatically:
- Create the database `hotel_management` if it doesn't exist
- Create all required tables (`users`, `rooms`, `guests`, `bookings`)
- Insert default admin and customer accounts

**No manual database setup required!** Just ensure MySQL is running.

## Installation & Running

### Option 1: Using Command Line

1. **Clone or download the project**
   ```bash
   cd hotel-management-mine
   ```

2. **Configure database credentials** (as mentioned above)

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn javafx:run
   ```

### Option 2: Using IntelliJ IDEA

1. **Open the project**
   - File → Open → Select the project folder

2. **Configure database credentials** in `DatabaseUtil.java`

3. **Wait for Maven to download dependencies** (check bottom right)

4. **Run the application**
   - Navigate to `src/main/java/com/example/hotelmanagementmine/Launcher.java`
   - Right-click → Run 'Launcher.main()'

### Option 3: Using Eclipse

1. **Import the project**
   - File → Import → Existing Maven Projects

2. **Configure database credentials** in `DatabaseUtil.java`

3. **Update Maven project**
   - Right-click project → Maven → Update Project

4. **Run the application**
   - Right-click `Launcher.java` → Run As → Java Application

## Usage Guide

### First Time Login

1. **Start the application** - Login screen appears

2. **Login as Admin** (to set up initial data)
   - Username: `admin`
   - Password: `admin123`

3. **Add Rooms**
   - Go to "Rooms" tab
   - Enter room number (e.g., 101)
   - Select room type (Single, Double, Suite, etc.)
   - Enter price per night (e.g., 1000)
   - Click "Add Room"

4. **Logout** and test customer login

### Customer Workflow

1. **Register a new account** or use default customer account
   - Username: `customer`
   - Password: `customer123`

2. **Browse available rooms**
   - View all available rooms
   - Filter by room type if needed

3. **Make a booking**
   - Select a room from dropdown
   - Choose check-in date
   - Choose check-out date
   - See automatic total calculation
   - Click "Book Now"

4. **View your bookings**
   - See all your booking history
   - Cancel active bookings if needed

### Admin Workflow

1. **Login as admin**

2. **Manage Rooms**
   - Add new rooms as inventory grows
   - Update room prices
   - Delete rooms if needed

3. **Manage Bookings**
   - View all bookings across all customers
   - Check out guests when they leave
   - Delete cancelled bookings

## Project Structure

```
hotel-management-mine/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/hotelmanagementmine/
│   │   │       ├── controller/          # UI Controllers
│   │   │       │   ├── MainController.java
│   │   │       │   ├── LoginController.java
│   │   │       │   ├── RegisterController.java
│   │   │       │   └── CustomerController.java
│   │   │       ├── dao/                 # Data Access Objects
│   │   │       │   ├── RoomDAO.java
│   │   │       │   ├── GuestDAO.java
│   │   │       │   ├── BookingDAO.java
│   │   │       │   └── UserDAO.java
│   │   │       ├── model/               # Data Models
│   │   │       │   ├── Room.java
│   │   │       │   ├── Guest.java
│   │   │       │   ├── Booking.java
│   │   │       │   └── User.java
│   │   │       ├── util/                # Utilities
│   │   │       │   ├── DatabaseUtil.java
│   │   │       │   └── SessionManager.java
│   │   │       ├── HelloApplication.java
│   │   │       └── Launcher.java
│   │   └── resources/
│   │       └── com/example/hotelmanagementmine/
│   │           ├── main-view.fxml       # Admin Dashboard UI
│   │           ├── customer-view.fxml   # Customer Dashboard UI
│   │           ├── login-view.fxml      # Login Screen UI
│   │           ├── register-view.fxml   # Registration UI
│   │           └── style.css            # Stylesheet
│   └── test/
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

## Database Schema

### Tables

1. **users**
   - `id` (Primary Key)
   - `username` (Unique)
   - `password`
   - `role` (ADMIN/CUSTOMER)
   - `full_name`

2. **rooms**
   - `id` (Primary Key)
   - `room_number` (Unique)
   - `room_type`
   - `price`
   - `status` (AVAILABLE/OCCUPIED)

3. **guests**
   - `id` (Primary Key)
   - `name`
   - `phone`
   - `email`
   - `id_number`

4. **bookings**
   - `id` (Primary Key)
   - `guest_id` (Foreign Key → guests)
   - `room_id` (Foreign Key → rooms)
   - `check_in_date`
   - `check_out_date`
   - `total_amount`
   - `status` (ACTIVE/COMPLETED)

## Troubleshooting

### MySQL Connection Issues

**Problem**: "Cannot connect to database"

**Solution**:
- Verify MySQL is running
- Check credentials in `DatabaseUtil.java`
- Ensure MySQL is listening on port 3306
- Test connection: `mysql -u root -p`

### JavaFX Runtime Issues

**Problem**: "Error: JavaFX runtime components are missing"

**Solution**:
- Ensure you're using JDK 11 or higher
- Maven should automatically download JavaFX dependencies
- Run: `mvn clean install` to re-download dependencies

### Build Failures

**Problem**: Maven build fails

**Solution**:
- Check internet connection (Maven needs to download dependencies)
- Delete `.m2/repository` folder and rebuild
- Verify Java version: `java -version` (should be 21+)

### Application Won't Start

**Problem**: Application crashes on startup

**Solution**:
1. Check MySQL is running
2. Verify database credentials
3. Check console for error messages
4. Ensure port 3306 is not blocked by firewall

## Default Credentials

### Admin Account
- **Username**: `admin`
- **Password**: `admin123`
- **Access**: Full system access

### Customer Account
- **Username**: `customer`
- **Password**: `customer123`
- **Access**: View rooms and make bookings only

**⚠️ Security Note**: Change default passwords in production!

## Future Enhancements

Potential features for future versions:
- Password encryption (BCrypt/SHA-256)
- Email notifications for bookings
- Payment integration
- Room availability calendar view
- Advanced search and filtering
- Reporting and analytics dashboard
- Multi-language support
- Export bookings to PDF/Excel

## License

This project is open-source and available for educational purposes.

## Support

For issues or questions:
1. Check the Troubleshooting section
2. Review error messages in console
3. Verify database connection and credentials

## Contributors

- Developer: Hotel Management System Team

---

**Version**: 1.0.0
**Last Updated**: October 2025
**Java Version**: 25
**JavaFX Version**: 21.0.6
