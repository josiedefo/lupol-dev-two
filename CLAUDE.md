# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Lupol Dev Two is a career assistant web application that helps users explore career options using AI. It features an AI-powered chat interface (named "Lupol") that provides career advice, job searching tips, and professional development guidance.

## Tech Stack

- **Backend**: Spring Boot 4.1, Java 21, Spring AI 2.0 with OpenAI
- **Frontend**: Vue 3 (Composition API, `<script setup>`), Vite 8 (Rolldown), Tailwind CSS 4 (CSS-first config in `style.css` — no `tailwind.config` file)
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

## Endpoints

Everything is served from the root path (no servlet context path):
- Frontend: `http://localhost:8080/`
- Career chat API: `POST /career/chat?userInput=...`
- Analytics: `POST /api/analytics/visit`
- Feedback: `POST /api/feedback`
- Admin (requires `X-Admin-Token` header): `GET /admin/conversations`, `GET /admin/feedback`

## Architecture

### Backend (Spring Boot)

```
src/main/java/com/defosolutions/lupoldevtwo/
├── LupolDevTwoApplication.java     # Main entry point
├── careerassist/
│   └── CareerAssistantController.java  # AI chat endpoint using Spring AI ChatClient
├── admin/
│   └── AdminController.java        # Reads conversations + feedback back from CloudWatch Logs
├── feedback/
│   ├── FeedbackController.java     # Feedback submission endpoint
│   └── FeedbackDTO.java            # Feedback data record
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
    ├── ChatAssistant.vue   # AI chat interface
    ├── FeedbackWidget.vue  # Post-chat thumbs up/down + comment
    └── AdminPage.vue       # Admin view: Conversations and Feedback tabs
```

- **No Vue Router**: Uses `viewState.js` with URL query params (`?view=assistant`) for navigation
- **Visitor ID**: Generated client-side UUID stored in cookie + localStorage, sent as `X-Visitor-Id` header to track conversation memory
- Vite dev server proxies `/career`, `/api/analytics`, `/api/feedback`, and `/admin` to `localhost:8080`

### Build Pipeline

1. Maven's `frontend-maven-plugin` installs Node and runs `npm install` + `npm run build`
2. `maven-resources-plugin` copies `frontend/dist/` to `target/classes/static/`
3. Spring Boot serves static files from `/static/` at the root path `/`

## Configuration

- `application.properties`: Base config with placeholder API key
- `application-local.properties`: Local development config with real OpenAI API key (use `--spring.profiles.active=local`)
- VSCode launch config available for debugging with local profile
- Admin/CloudWatch settings (`admin.token`, `cloudwatch.log.group`, `cloudwatch.region`) live in `application.properties`; override in App Runner via the `ADMIN_TOKEN` and `CLOUDWATCH_LOG_GROUP` env vars
