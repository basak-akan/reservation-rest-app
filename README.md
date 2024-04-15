# CodeChallenge Application

This application is designed to demonstrate a implementation for a RESTful API that manages reservations for a restaurant using Spring Boot for serverside services.
It includes functionalities for managing user data and reservations through RESTful APIs.

## Features

- **User Management**: Create, retrieve, update, and delete users.
- **Reservation Management**: Manage reservations with features to add, view, update, and delete reservations.
- **Filter Capability**: Filter reservations based on specific date range criteria. Filter users based on name or email criteria.

## Technologies

- **Spring Boot**: Framework for building Java-based applications that you can easily run.
- **MySQL**: Database for storing user and reservation data.
- **Docker**: Containerization of the application and its environment.
- **JUnit**: For conducting integration and unit tests.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java JDK 21
- Docker
- Gradle

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/basak-akan/reservation-rest-app.git
   cd .\codechallenge\docker
   
2. **Start the application with Docker**

    ```bash
   docker-compose up --build

This command builds the application and starts the services defined in the docker-compose.yml file, including the MySQL database and the Spring Boot application.

3. **Access the application**

The application will be accessible at http://localhost:8080.
You can use any API testing tool like Postman or curl to interact with the API.

## API Documentation

After starting the application, you can access the API documentation at http://localhost:8080/swagger-ui/index.html. This documentation provides interactive features to explore the API endpoints.

## Running Tests

To run the automated tests for this system, use:

   ```bash 
    ./gradlew test

