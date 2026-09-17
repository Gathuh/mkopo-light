# Lending Application (Spring Boot Microservices)

This project is a simplified lending platform built with Spring Boot and split into microservices.
It covers loan product setup, loan lifecycle management, customer profile handling, and event-driven notifications.

## Services

- `discovery` - Eureka service registry (`8761`)
- `gateway` - API gateway (entry point)
- `security` - authentication/authorization service
- `mkopo.light` - core lending domain (products, loans, sweeps)
- `notifications` - notification service (Kafka consumer + channel handling)

## Tech Stack

- Java + Spring Boot
- Spring Cloud (Eureka, Gateway)
- MySQL
- Apache Kafka
- Gradle
- Docker Compose (for infrastructure)

## Project Distribution

This repository is a multi-module Gradle project. Each service is independently deployable and can run as its own process.

At a high level, distribution is:

1. **Service Discovery** (`discovery`)
2. **API Entry Point** (`gateway`)
3. **Domain Services** (`mkopo.light`, `security`, `notifications`)
4. **Infrastructure** (`docker-compose.yml`) for shared runtime dependencies like Kafka and MySQL

## Prerequisites

Install the following on your machine:

- JDK 17+
- Docker + Docker Compose
- Gradle (optional, wrapper is included)

## Configuration

Most services use Spring Boot `application.yml`/`application.yaml` files under:

- `*/src/main/resources/`

Before running, review and adjust where needed:

- Database URL, username, password
- Kafka bootstrap server
- Mail credentials (notifications service)
- Any environment-specific secrets

## How to Run

### 1) Start infrastructure

From project root:

```bash
docker compose up -d
```

### 2) Build all services

```bash
./gradlew clean build
```

### 3) Start services

Start each service in a separate terminal, in this order:

```bash
./gradlew :discovery:bootRun
./gradlew :gateway:bootRun
./gradlew :security:bootRun
./gradlew :mkopo.light:bootRun
./gradlew :notifications:bootRun
```

## API Access

- Gateway is the main entry point for client requests.
- Swagger/OpenAPI endpoints are available per service where configured (for example, `notifications` exposes docs endpoints in its config).

## Running Tests

Run all tests:

```bash
./gradlew test
```

Run tests for one module:

```bash
./gradlew :mkopo.light:test
```

## Notes

- Database schemas are created automatically by Spring Boot/JPA based on current service configuration.
- Services communicate using REST and event-driven messaging (Kafka).
- If you change hostnames/ports for containers, update service configs accordingly.
