# Shopping Cart Brownfield Architecture Document

## Introduction

This document captures the CURRENT STATE of the Shopping Cart codebase, including technical debt, workarounds, and real-world patterns. It serves as a reference for AI agents and senior developers working on documenting, testing, and modernizing the application.

### Document Scope

Comprehensive documentation of the entire system, as requested.

### Change Log

| Date       | Version | Description                 | Author |
|------------|---------|-----------------------------|--------|
| 2025-09-15 | 1.0     | Initial brownfield analysis | pm     |

## Quick Reference - Key Files and Entry Points

### Critical Files for Understanding the System

- **Configuration**: `src/application.properties` (This file is git-ignored and must be created manually based on `README.md` instructions).
- **Core Business Logic**: `src/com/shashi/service/` package.
- **Web Entry Points**: Servlets located in the `src/com/shashi/srv/` package (using `@WebServlet` annotations).
- **Database Models (JavaBeans)**: Located in the `src/com/shashi/beans/` package.
- **Database Schema**: `databases/mysql_query.sql`.
- **Build Configuration**: `pom.xml`.

## High Level Architecture

### Technical Summary

The project is a monolithic Java web application built with Maven. It follows a classic Servlet/JSP architecture. The backend is written in Java (version 8), using JDBC for database connectivity. The frontend is composed of JSP files using Bootstrap for styling. The application is designed to be deployed as a WAR file on a Tomcat v8.0+ server. It does not use a modern framework like Spring or Jakarta EE for dependency injection or object-relational mapping. Business logic is separated into service classes, and data is passed around using JavaBean objects.

### Actual Tech Stack (from pom.xml)

| Category      | Technology         | Version      | Notes                                            |
|---------------|--------------------|--------------|--------------------------------------------------|
| Language      | Java               | 1.8          | As configured in `pom.xml`.                      |
| Web           | Java Servlet       | 3.1.0        | Uses annotations (`@WebServlet`).                |
| Web           | JSP                | (Implicit)   | Used for all frontend rendering.                 |
| Database Conn | JDBC               | (Implicit)   | Direct JDBC calls are likely used in services.   |
| Database      | MySQL              | 8.0.33       | `mysql-connector-java` dependency.               |
| Mail          | Jakarta Mail       | 2.1.1, 2.0.1 | Used for sending application emails.             |
| Build         | Maven              |              | Defined in `pom.xml`.                            |
| Deployment    | Tomcat             | 8.0+         | As specified in `README.md`.                     |

### Repository Structure Reality Check

- **Type**: Single repository.
- **Package Manager**: Maven.
- **Notable**: The project follows a standard, if somewhat dated, Java web application structure. The separation of concerns into packages like `beans`, `srv`, and `service` is clear.

## Source Tree and Module Organization

### Project Structure (Actual)

```text
shopping-cart/
├── src/
│   └── com/
│       └── shashi/
│           ├── beans/         # Data transfer objects (JavaBeans)
│           ├── constants/     # Application-wide constants
│           ├── service/       # Business logic implementation
│           ├── srv/           # Servlets (HTTP request handlers)
│           └── utility/       # Helper and utility classes
├── WebContent/
│   ├── *.jsp                # Frontend view files
│   ├── WEB-INF/
│   │   └── web.xml          # Minimal; relies on annotations
│   └── css/
├── databases/
│   └── mysql_query.sql      # Database schema and initial data
└── pom.xml                  # Maven build configuration
```

### Key Modules and Their Purpose

- **`com.shashi.beans`**: Contains the JavaBean classes (e.g., `UserBean.java`, `ProductBean.java`) that act as data transfer objects (DTOs) throughout the application.
- **`com.shashi.srv`**: Contains all Java Servlets. These are the primary entry points for HTTP requests from the browser. They handle user input, call service methods, and dispatch to JSP pages.
- **`com.shashi.service`**: Contains the core business logic. Classes in this package are responsible for interacting with the database and performing application-specific operations.
- **`com.shashi.utility`**: Contains utility classes for tasks like database connections (`DBUtil.java`) and sending emails (`MailMessage.java`).
- **`com.shashi.constants`**: A single place for application-level constants.
- **`WebContent`**: The web root, containing all user-facing JSP files, CSS, and images.

## Data Models and APIs

### Data Models

Data models are defined as simple JavaBean classes in the `src/com/shashi/beans/` package. They have private fields with public getters and setters. There is no ORM in use.

- **User Model**: See `src/com/shashi/beans/UserBean.java`
- **Product Model**: See `src/com/shashi/beans/ProductBean.java`
- *(And others in the same package)*

### API Specifications

The application does not expose a formal REST or JSON API. The "API" is the set of URL patterns defined by the `@WebServlet` annotations on the servlet classes in the `src/com/shashi/srv/` package. These servlets process standard HTTP GET/POST requests from HTML forms and browser links, and typically respond with an HTML page rendered by a JSP.

## Technical Debt and Known Issues

### Critical Technical Debt

1.  **Lack of Automated Tests**: The codebase has no discernible automated unit or integration tests. This makes refactoring or adding new features risky and requires extensive manual testing.
2.  **Manual Configuration**: The application relies on a manually created `application.properties` file for database and email credentials. This is error-prone and makes deployment less portable.
3.  **Hardcoded Admin Credentials**: The `LoginSrv.java` servlet contains hardcoded credentials for the admin user (`admin@gmail.com`, `admin`). This is a major security vulnerability.
4.  **Direct JDBC/SQL in Services**: Business logic layers likely contain raw JDBC calls and SQL strings, tightly coupling them to the database schema and making them vulnerable to SQL injection if not handled carefully.
5.  **No Dependency Injection**: Dependencies (like service classes or utility classes) are instantiated directly (e.g., `new UserServiceImpl()`). This makes the code rigid and difficult to test in isolation.

### Workarounds and Gotchas

- **Annotation-based Servlets**: Developers must be aware that all web endpoints are defined via `@WebServlet` annotations, not in `web.xml`.
- **Manual Database Setup**: The database schema and initial data must be loaded manually from `databases/mysql_query.sql`.
- **Gmail App Password**: The mailing functionality requires generating and using a "Gmail App Password," which can be a confusing setup process for new developers.

## Development and Deployment

### Local Development Setup

The setup process is documented in the `README.md` file and involves:
1.  Installing Java, Eclipse, Maven, Tomcat, and MySQL.
2.  Manually creating the database.
3.  Cloning the project.
4.  Manually creating and configuring the `src/application.properties` file.
5.  Building with Maven (`clean install`).
6.  Running on a Tomcat server from within Eclipse.

### Build and Deployment Process

- **Build Command**: `mvn clean install`
- **Deployment**: The process described in the README is manual, involving running the project on a server from the Eclipse IDE. A standard deployment would involve taking the generated `.war` file from the `target/` directory and deploying it to a standalone Tomcat server.

## Testing Reality

### Current Test Coverage

- **Unit Tests**: None found.
- **Integration Tests**: None found.
- **E2E Tests**: None.
- **Manual Testing**: This is the only method of testing. The `README.md` provides default user and admin credentials to facilitate this.
