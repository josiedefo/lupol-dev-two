# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Lupol Dev Two is a career assistant web application that helps users explore career options using AI. It features an AI-powered chat interface (named "Lupol") that provides career advice, job searching tips, and professional development guidance.

## Tech Stack

- **Backend**: Spring Boot 3.5.8, Java 21, Spring AI with OpenAI
- **Frontend**: Vue 3 (Composition API, `<script setup>`), Vite 7, Tailwind CSS
- **Build**: Maven with frontend-maven-plugin (builds frontend during Maven compile)

## Build & Run Commands

```bash
# Full build (compiles Java + frontend, copies dist to static resources)
./mvnw clean package

# Run with local profile (uses application-local.properties for API key)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Run tests
./mvnw test

# Frontend development (with hot reload, proxies to backend)
cd frontend && npm run dev

# Frontend build only
cd frontend && npm run build
```

## Application Context Path

All endpoints are served under `/lupoldevtwo/`:
- Frontend: `http://localhost:8080/lupoldevtwo/`
- Career chat API: `POST /lupoldevtwo/career/chat?userInput=...`
- Analytics: `POST /lupoldevtwo/api/analytics/visit`

## Architecture

### Backend (Spring Boot)

```
src/main/java/com/defosolutions/lupoldevtwo/
├── LupolDevTwoApplication.java     # Main entry point
├── careerassist/
│   └── CareerAssistantController.java  # AI chat endpoint using Spring AI ChatClient
└── analytics/web/
    ├── AnalyticsController.java    # Visit tracking endpoint
    ├── VisitDTO.java               # Visit data record
    └── IpUtil.java                 # Client IP extraction
```

- **CareerAssistantController**: Uses Spring AI's `ChatClient` with `MessageChatMemoryAdvisor` for conversation memory. Accepts `X-Visitor-Id` header to track conversations per user.
- System prompt restricts responses to career-related topics only, prioritizing careers with short ramp-up times.

### Frontend (Vue 3)

```
frontend/src/
├── App.vue                 # Root component with dynamic view switching
├── viewState.js            # Simple global state (no Vue Router)
├── main.js                 # App entry point
├── analytics/client.ts     # Visitor tracking (generates UUID, sends beacons)
└── components/
    ├── Home.vue            # Landing page
    └── ChatAssistant.vue   # AI chat interface
```

- **No Vue Router**: Uses `viewState.js` with URL query params (`?view=assistant`) for navigation
- **Visitor ID**: Generated client-side UUID stored in cookie + localStorage, sent as `X-Visitor-Id` header to track conversation memory
- Vite dev server proxies `/lupoldevtwo/career` and `/lupoldevtwo/api/analytics` to `localhost:8080`

### Build Pipeline

1. Maven's `frontend-maven-plugin` installs Node and runs `npm install` + `npm run build`
2. `maven-resources-plugin` copies `frontend/dist/` to `target/classes/static/`
3. Spring Boot serves static files from `/static/` at context path `/lupoldevtwo/`

## Configuration

- `application.properties`: Base config with placeholder API key
- `application-local.properties`: Local development config with real OpenAI API key (use `--spring.profiles.active=local`)
- VSCode launch config available for debugging with local profile
