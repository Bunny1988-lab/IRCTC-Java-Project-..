# IRCTC Railway Reservation System

A modular, console-based Railway Reservation Management System built in Java 17 using standard JDBC for database operations and a clean layered monolithic architecture. Designed for academic and intermediate Java developers to master core software engineering principles, design patterns, and database interactions.

---

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Database Configuration](#database-configuration)
- [How to Build and Run](#how-to-build-and-run)
- [Current Development Status](#current-development-status)

---

## Overview

The IRCTC Railway Reservation System provides a simulated console application mimicking core functionalities of the Indian Railway Catering and Tourism Corporation (IRCTC). The system will allow passengers to search trains, check seat availability across classes, book tickets, manage PNR statuses, and process cancellations while maintaining transaction integrity through MySQL 8.

---

## Technology Stack

- **Programming Language**: Java 17 (LTS)
- **Build & Dependency Management**: Apache Maven 3.9+
- **Application Type**: Console-based CLI application
- **Database**: MySQL 8.x
- **Database Connectivity**: JDBC (`mysql-connector-j` 8.3.0)
- **Testing Framework**: JUnit 5 (Jupiter 5.10.2)
- **Architecture**: Layered Monolithic Architecture

---

## Architecture Overview

The system follows a strict **Layered Monolithic Architecture** to ensure separation of concerns, maintainability, and clean code principles:

```text
[ Console UI / CLI View ]
           │
           ▼
 [ Controller Layer ]   --> Handles user input, routing, and console interaction
           │
           ▼
   [ Service Layer ]    --> Encapsulates core business logic, validations, workflows
           │
           ▼
     [ DAO Layer ]      --> Data Access Objects handling SQL queries and mapping
           │
           ▼
  [ Database Layer ]    --> Centralized JDBC connection management (ConnectionPool/Singleton)
           │
           ▼
     [ MySQL 8.x ]      --> Relational Database Storage
```

Supporting Packages:
- **`model`**: Domain entities representing real-world business objects (e.g., `User`, `Train`, `Booking`, `Station`).
- **`enums`**: Type-safe enumerations (e.g., `BerthType`, `BookingStatus`, `TrainType`, `Role`).
- **`exception`**: Custom checked and unchecked domain exceptions (e.g., `BookingException`, `SeatUnavailableException`).
- **`util`**: Utility classes for validation, password hashing, date-time formatting, and console helpers.

---

## Project Structure

```text
IRCTC-Java-Project/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── irctc/
│   │   │           ├── Main.java             # Application entry point
│   │   │           ├── controller/           # CLI controllers handling user menus & input
│   │   │           ├── model/                # Data/Entity models
│   │   │           ├── service/              # Business logic interfaces & implementations
│   │   │           ├── dao/                  # Data Access Object interfaces & implementations
│   │   │           ├── database/             # JDBC connection manager & utilities
│   │   │           ├── enums/                # Enumeration definitions
│   │   │           ├── exception/            # Custom application exceptions
│   │   │           └── util/                 # Reusable utility and helper classes
│   │   │
│   │   └── resources/
│   │       └── db.properties.example         # Database configuration template
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── irctc/
│                   └── MainTest.java         # Initial sanity and test harness
│
├── database/                                 # SQL DDL & DML scripts and migration files
├── docs/                                     # Architecture documentation and diagrams
├── pom.xml                                   # Maven build descriptor
├── README.md                                 # Project documentation
└── .gitignore                                # Git ignore rules
```

---

## Prerequisites

Before building and running the project, ensure you have the following installed on your machine:

1. **Java Development Kit (JDK)**: Java 17 or higher (configured in `JAVA_HOME`).
2. **Apache Maven**: Version 3.8.0 or higher (configured in `PATH`).
3. **MySQL Server**: MySQL 8.0 or higher.
4. **Git**: For version control.

Verify your environment by running:
```bash
java -version
mvn -version
```

---

## Database Configuration

1. Create a MySQL database for the application:
   ```sql
   CREATE DATABASE irctc_db;
   ```
2. Navigate to `src/main/resources/` and make a copy of the template:
   ```bash
   cp src/main/resources/db.properties.example src/main/resources/db.properties
   ```
3. Update `src/main/resources/db.properties` with your MySQL credentials:
   ```properties
   db.driver=com.mysql.cj.jdbc.Driver
   db.url=jdbc:mysql://localhost:3306/irctc_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.username=your_mysql_username
   db.password=your_mysql_password
   ```
   > **Note**: `db.properties` is listed in `.gitignore` and must never be committed to source control.

---

## How to Build and Run

### 1. Compile the Project
```bash
mvn clean compile
```

### 2. Run Tests
```bash
mvn clean test
```

### 3. Package into a JAR
```bash
mvn package
```

### 4. Run the Application

You can execute the packaged JAR directly:
```bash
java -jar target/irctc-railway-reservation-1.0.0-SNAPSHOT.jar
```

Or run via Maven Exec:
```bash
mvn compile exec:java -Dexec.mainClass="com.irctc.Main"
```

Or run directly with the compiled classes:
```bash
java -cp target/classes com.irctc.Main
```

---

## Current Development Status

- [x] Initial project scaffolding and standard Maven directory structure established.
- [x] Maven configuration configured for Java 17 release, MySQL Connector/J 8.3.0, and JUnit 5.
- [x] Application entry point (`Main.java`) initialized with startup verification.
- [x] Centralized database properties template created.
- [x] Git configuration and `.gitignore` safety rules configured.
- [x] Database schema DDL & DML scripts (`01_schema.sql` and `02_seed_data.sql`).
- [x] Centralized JDBC `DatabaseConnection` singleton/factory implementation.
- [x] Type-safe Enums and Domain Model POJOs implemented (`com.irctc.enums`, `com.irctc.model`).
- [x] DAO Layer implementation with JDBC (`com.irctc.dao`, `com.irctc.dao.impl`).
- [x] Service Layer business workflows (Seat allocation, PNR generation, Pricing, ACID transactions).
- [x] Interactive Console Controller & Menu flow (`MainController`, `PassengerController`, `AdminController`).
