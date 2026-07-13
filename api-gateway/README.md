# ApiMeter - API Gateway

This repository contains the API Gateway service for the ApiMeter project. It acts as the single entry point for all client requests, routing them to the appropriate backend microservices.

## Features
- Routing using Spring Cloud Gateway
- Circuit breaking and fault tolerance using Resilience4j

## Configuration
> [!IMPORTANT]
> The `application.yml` / `application.yaml` files have been intentionally excluded from version control for security. To run this service, create a new `application.yaml` inside `src/main/resources/` and add the necessary configurations for your environment.
