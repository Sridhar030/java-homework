# Rewards Calculation Service

## Overview

This Spring Boot application calculates reward points for customers based on their purchase transactions. The application provides a RESTful API to interact with the reward points calculation system and includes a service for saving transactions and calculating rewards.

## Features

- **Spring Boot Application**: Built with Spring Boot for rapid development and ease of use.
- **RESTful API**: Provides endpoints to save transactions and calculate rewards.
- **In-Memory Database**: Uses H2 for an in-memory database to simplify testing and development.
- **JUnit 5 Tests**: Includes unit tests to ensure the functionality of the reward calculation.
- **API Documentation**: Integrated with Swagger for API documentation and testing.

## Prerequisites

- **Java 15** or higher
- **Maven** (to build and manage dependencies)

## Setup

### Clone the Repository
```git clone git@github.com:Sridhar030/java-homework.git```

## Build the Project
```mvn clean install```

## Run the Application
```mvn spring-boot:run```

## Testing
```mvn test```

## Swagger API Documentation

The application includes Swagger for API documentation. You can view and interact with the API endpoints through the Swagger UI.

### Swagger UI

Once the application is running, access the Swagger UI at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Available Endpoints

1. **Add Transaction**
    - **Endpoint:** `POST /api/transactions`
    - **Description:** Saves a new transaction.
    - **Request Body:**
      ```json
      {
       "customerId": "cust1",
       "amount": 150,
       "date": "2024-01-01"
      }
      ```
    - **Responses:**
        - `200 OK` – Transaction saved successfully.
        - `500 Internal Server Error` – Error adding transaction.

2. **Get Rewards Per Month with Total**
    - **Endpoint:** `GET /api/transactions/rewards-per-month-with-total`
    - **Description:** Retrieves the calculated rewards per month and total for each customer.
    - **Responses:**
      - `200 OK` – Returns a map of rewards per customer and month.
       ```json
       {
        "cust1": {
        "2024-01": 100,
        "Total": 100
        },
        "cust2": {
        "2024-02": 50,
        "2024-03": 30,
        "Total": 80
        }
       }
       ```
      - `500 Internal Server Error` – Error getting the rewards information.
