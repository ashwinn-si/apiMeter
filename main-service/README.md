# ApiMeter - Main Service

This repository contains the Main Service for the ApiMeter project. It forms the core of the API monetization platform.

## Features
- **Rate Limiting:** Implements both Token Bucket and Sliding Window algorithms to enforce per-client rate limits. Uses Redis for storing rate limit state.
- **Token Management:** Issues and manages tokens tied to subscription tiers (Free, Pro, Enterprise).
- **Usage Tracking:** Logs and tracks API usage in real-time.
- **Invoicing:** Generates invoices based on usage and triggers async notifications via Kafka.

## Technologies
- Spring Boot
- MySQL
- Redis
- Kafka Producer

## Configuration
> [!IMPORTANT]
> The `application.yml` / `application.yaml` files have been intentionally excluded from version control for security. To run this service, create a new `application.yaml` inside `src/main/resources/` and add the necessary configurations for your environment (e.g., Database credentials, Redis config).
