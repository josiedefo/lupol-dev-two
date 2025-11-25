🏛️ ARCHITECTURE OVERVIEW — Lupol Dev Two

This document describes the system architecture, runtime flow, build pipeline, and deployment model of the Lupol Dev Two application.

1. High-Level Architecture Diagram
                           ┌─────────────────────────┐
                           │      Vue Frontend       │
                           │  (Vite-built SPA)       │
                           │                         │
                           │ Runs in browser, served │
                           │   by Spring Boot static │
                           └──────────────┬──────────┘
                                          │
                                          ▼
                             HTTP (Browser → App Runner)
                          http(s)://<host>/lupoldevtwo/

                 ┌────────────────────────────────────────────────┐
                 │                Spring Boot API                 │
                 │        Java 21 / Web MVC / ChatClient         │
                 │   Context path: /lupoldevtwo                  │
                 │   API path:     /career/chat                  │
                 └───────────────────┬────────────────────────────┘
                                     │
                                     ▼
                         Calls OpenAI (External API)
                          https://api.openai.com/...


2. Components
2.1 Frontend (Vue 3 + Vite)

Located in /frontend

Single Page Application (SPA)

Built with Vite and output to frontend/dist/

The final static bundle (HTML, JS, assets) is copied into:

src/main/resources/static/


Runtime behavior:

Loads at /lupoldevtwo/

Handles UI and user interactions

Sends chat requests to:

POST /lupoldevtwo/career/chat

2.2 Backend (Spring Boot)

Java 21

Spring Boot Web

Declared context path:

server.servlet.context-path=/lupoldevtwo


Key API endpoint:

POST /lupoldevtwo/career/chat


Responsibilities:

Receives user’s chat input

Logs input

Forwards request to OpenAI using Spring’s ChatClient

Returns the AI-generated result as a plain response

2.2.1 OpenAI Integration

Uses ChatClient or WebClient under the hood

Reads OPENAI_API_KEY from environment variables

Makes outbound HTTP requests to OpenAI APIs

Needs outbound internet access (App Runner supports this by default)

3. Build Architecture
3.1 Maven + frontend-maven-plugin

The build pipeline integrates both back-end and front-end:

frontend-maven-plugin:

Installs local Node.js + npm (isolated for reproducibility)

Runs npm install

Runs npm run build

Produces /frontend/dist/

maven-resources-plugin:

Copies /frontend/dist → Spring’s static resources:

target/classes/static/


Spring Boot Maven Plugin:

Packages everything into a fat JAR:

target/lupol-dev-two.jar


This results in a single deployable artifact.

4. Runtime Architecture
4.1 Container Runtime

The project uses a multi-stage Dockerfile:

Stage 1: Build the application with Maven (runs front + back build)

Stage 2: Copy the JAR into a lightweight Java 21 runtime image

Container entrypoint:

java $JAVA_OPTS -jar app.jar

4.2 Environment Variables Required
Variable	Description
OPENAI_API_KEY	Required — Auth token for OpenAI API
SPRING_PROFILES_ACTIVE	Optional — set environment-specific config
JAVA_OPTS	Optional — JVM tuning
4.3 Port

App listens on 8080

App Runner exposes via HTTPS on public URL

Context path means UI lives here:

/lupoldevtwo/

5. AWS Deployment Architecture

The application is deployed to:

5.1 Amazon ECR

Stores Docker images.

Repository: lupol-dev-two

Tag: usually latest or versioned tags

5.2 AWS App Runner

Runs the container with auto-scaling, HTTPS, and rollback capabilities.

App Runner handles:

Fetching the image from ECR

Running the container

Public URL with HTTPS

Scaling instances based on traffic

Logging (CloudWatch)

Zero-downtime deployments

Key App Runner configuration:
Setting	Value
Port	8080
Context Path	/lupoldevtwo
CPU/Memory	0.25 vCPU / 0.5–1GB RAM
Deployment trigger	Manual or Auto on new ECR push
Environment variables	OPENAI_API_KEY
App Runner URL:
https://<random>.awsapprunner.com/lupoldevtwo/

6. Networking and Security
6.1 Outbound Internet

App Runner must reach:

api.openai.com


App Runner provides outbound internet by default.

6.2 Inbound HTTP/HTTPS

App Runner auto-provisions HTTPS certificates

No need for a load balancer or security groups

6.3 Secrets

Handled via App Runner environment variables

OPENAI_API_KEY must not be in source code

Consider future use of:

SSM Parameter Store

AWS Secrets Manager

7. Deployment Flow Summary
Developer
   |
   | 1. Build + test locally
   |    (mvn clean package, docker build)
   |
   | 2. docker push → Amazon ECR
   |
   v
AWS App Runner
   |
   | 3. Pull image
   | 4. Start container
   | 5. Expose public HTTPS URL
   | 6. Run auto-scaling service
   |
   v
Public Internet

8. Future Enhancements (Optional)
a) Add CI/CD pipeline

GitHub Actions workflow to:

Build Docker image

Push to ECR

Trigger App Runner deployment

b) Use Parameter Store for secrets

Better than environment vars for production.

c) Add database support

PostgreSQL (RDS or Aurora Serverless V2)

d) Add metrics

Prometheus metrics endpoint + CloudWatch dashboards.

e) Add request tracing

AWS X-Ray or OpenTelemetry.

9. Summary

This architecture provides:

Modern full-stack setup (Vue + Spring Boot)

Single deployable JAR

Fully containerized

Zero-DevOps hosting via AWS App Runner

Secure integration with OpenAI

Simple scaling and HTTPS without managing servers

It is clean, simple, and production-ready for small to medium workloads.