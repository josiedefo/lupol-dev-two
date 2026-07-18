# 🚀 Lupol Dev Two — Spring Boot + Vue + Docker + AWS App Runner

Lupol Dev Two is a full-stack web application that combines:

- **Java 21 + Spring Boot 4.1** backend with **Spring AI 2.0**
- **Vue 3 (Vite 8)** frontend styled with **Tailwind CSS 4**
- A backend endpoint `/career/chat` that integrates with OpenAI via Spring AI's `ChatClient`
- A Dockerized build that packages both backend + frontend
- CI-friendly Maven build that compiles the Vue app before packaging
- AWS App Runner deployment for a public URL

## 🧱 Project Structure

```
.
├── frontend/              # Vue.js (Vite) app
│   ├── src/
│   ├── package.json
│   └── vite.config.js
├── src/main/java/...      # Spring Boot backend
├── src/main/resources/
│   ├── application.properties
│   └── static/            # Frontend build artifacts copied here
├── Dockerfile             # Multi-stage Docker build
├── docker-compose.yml     # Optional local runner
├── .dockerignore
└── pom.xml                # Maven build with frontend integration
```

## 🔧 Development Workflow

### 1. Start the Spring Boot backend (dev mode)

```bash
./mvnw spring-boot:run
```

To run with the `local` profile (uses `application-local.properties` for your real OpenAI API key):

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Runs on **http://localhost:8080/**

### 2. Start the frontend (dev mode)

```bash
cd frontend
npm install
npm run dev
```

Dev frontend runs on **http://localhost:5173/**

Proxy rules in `vite.config.js` forward API calls to the backend automatically.

## 🛠️ Maven Build (Backend + Frontend)

The project uses `frontend-maven-plugin` to:

1. Install Node/npm locally (isolated)
2. Run `npm install`
3. Run `npm run build` (Vite)
4. Copy `frontend/dist` → `src/main/resources/static/`
5. Package everything into one Spring Boot JAR

**Build:**

```bash
./mvnw clean package -DskipTests
```

Final JAR is created at `target/*.jar`.

**Running the JAR:**

```bash
java -jar target/lupol-dev-two-0.0.1-SNAPSHOT.jar
```

## 🐳 Docker Support

The app is fully Dockerized using a multi-stage build:

- **Stage 1:** Use Maven + Node to build the backend + frontend
- **Stage 2:** Copy the generated JAR into a lightweight JRE image

**Build image:**

```bash
docker build -t lupol-dev-two .
```

**Run locally:**

```bash
docker run --rm -p 8080:8080 \
  -e OPENAI_API_KEY=<your-key> \
  lupol-dev-two
```

Open in browser: **http://localhost:8080/**

### docker-compose (local convenience)

```bash
docker compose up --build
```

This uses the same Dockerfile and exposes port 8080.

## 🔐 Environment Variables

| Variable | Required | Description |
|---|---|---|
| `SPRING_AI_OPENAI_API_KEY` | ✅ Yes | API key used by backend to call OpenAI (overrides `spring.ai.openai.api-key`) |
| `ADMIN_TOKEN` | ✅ Yes | Token required by the admin endpoints (`X-Admin-Token` header) |
| `CLOUDWATCH_LOG_GROUP` | ✅ Yes | CloudWatch log group the admin page reads conversations/feedback from |
| `SPRING_PROFILES_ACTIVE` | Optional | Set to `prod` or a custom profile |
| `JAVA_OPTS` | Optional | JVM memory/GC flags in Docker |

Use environment variables instead of hardcoding secrets in `application.properties`. Spring's relaxed binding maps them onto properties automatically, e.g. `SPRING_AI_OPENAI_API_KEY` → `spring.ai.openai.api-key`.

## 🌐 Backend API

### `POST /career/chat`

**Accepts:**
- `userInput=<text>` (query param)
- `X-Visitor-Id: <uuid>` (header — used to maintain per-user conversation memory)

**Returns:** Markdown-formatted plain text response from the AI assistant. Each response includes career suggestions with:

- Fit explanation and learning time
- Earning potential (entry / mid-career / top earner)
- Recommended training programs with direct links and costs

Internally calls OpenAI GPT-4o via Spring AI `ChatClient` with conversation memory.

### `POST /api/analytics/visit`

Accepts a JSON body with visit metadata. Used for visitor tracking and conversation logging.

### `POST /api/feedback`

Accepts a JSON body `{ visitorId, helpful, comment }`. Logs user feedback (thumbs up/down + optional comment) to CloudWatch.

### `GET /admin/conversations` · `GET /admin/feedback`

Require `X-Admin-Token` header; read conversations and feedback back from CloudWatch Logs for the admin page (`?view=admin`).

## 🚢 Deploying to AWS App Runner

AWS App Runner provides a simple "run my Docker container publicly" service with auto-scaling and HTTPS. Below are the end-to-end steps.

### 1. Create ECR Repository

```bash
aws ecr create-repository \
  --repository-name lupol-dev-two \
  --region us-east-1
```

Save the output ARN, e.g. `123456789012.dkr.ecr.us-east-1.amazonaws.com/lupol-dev-two`

### 2. Authenticate & Push Image to ECR

```bash
aws ecr get-login-password --region us-east-1 \
  | docker login --username AWS --password-stdin 123456789012.dkr.ecr.us-east-1.amazonaws.com
```

**Build:**

```bash
docker build -t lupol-dev-two .
```

**Tag:**

```bash
docker tag lupol-dev-two:latest \
  123456789012.dkr.ecr.us-east-1.amazonaws.com/lupol-dev-two:latest
```

**Push:**

```bash
docker push 123456789012.dkr.ecr.us-east-1.amazonaws.com/lupol-dev-two:latest
```

### 3. Create AWS App Runner Service

1. Go to **AWS Console → App Runner**
2. Click **Create Service**
3. Source: **Container Registry** → Provider: **ECR** → Select image `lupol-dev-two:latest`
4. Port: `8080`
5. Instance size: `0.25 vCPU / 0.5–1GB RAM` (good for small apps)
6. Env vars:
   - `OPENAI_API_KEY=sk-xxxx`
   - `SPRING_PROFILES_ACTIVE=prod` (optional)
7. Auto-create IAM role for ECR access
8. Click **Create & Deploy**

Within 2–3 minutes, App Runner will give you a URL:

```
https://<random>.awsapprunner.com
```

Your app is served from the root path: `https://<random>.awsapprunner.com/`

## 🔍 Logs & Monitoring

App Runner automatically streams:

- stdout/stderr from your container
- Spring Boot logs
- Startup errors
- Health check failures

All viewable under **App Runner → Your Service → Logs**.

## 🔄 Redeploying After Code Changes

1. Rebuild Docker image
2. Re-tag with same ECR tag (`latest` or versioned)
3. Push again
4. In **App Runner → Deployments → Deploy latest image**

Or enable **Automatic Deployment** to redeploy on new image push.

## 🛡️ HTTPS / Domains

App Runner automatically provisions HTTPS, TLS, and certificates.

To use your custom domain:

1. Go to **App Runner → Custom Domains**
2. Add your domain
3. Add a CNAME record to your DNS

## 📦 Summary

This project is a modern full-stack containerized application using:

- **Vue 3 + Tailwind 4** → Vite 8 dev server + production build
- **Spring Boot 4 + Spring AI 2** → Chat backend + OpenAI integration
- **Maven** → orchestrates front + back build
- **Docker** → deployable anywhere
- **AWS App Runner** → simplest public hosting on AWS

With this setup you can develop frontend/backend independently, build everything into a single JAR, run locally or remotely in Docker, deploy to AWS in minutes, and scale automatically on demand.
