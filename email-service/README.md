# ApiMeter - Email Service

This repository contains the Email Service for the ApiMeter project. It handles asynchronous email communications.

## Features
- **Async Delivery:** Listens to Kafka topics for events like invoice generation and quota alerts.
- **Invoices:** Sends generated invoices to clients asynchronously.
- **Quota Alerts:** Notifies users when they reach 80% or 100% of their API quota.

## Technologies
- Spring Boot
- Kafka Consumer
- SMTP integration

## Configuration
> [!IMPORTANT]
> The `application.yml` / `application.yaml` files have been intentionally excluded from version control for security. To run this service, create a new `application.yaml` inside `src/main/resources/` and add the necessary configurations for your environment (e.g., SMTP settings, Kafka brokers).
