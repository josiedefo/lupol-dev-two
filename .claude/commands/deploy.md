---
description: Build the app, push the image to ECR, and deploy to AWS App Runner
---

Build and deploy this app to AWS App Runner. Follow these steps in order; stop and report if any step fails.

## Constants

- ECR registry: `762891556894.dkr.ecr.us-east-1.amazonaws.com`
- ECR repo: `762891556894.dkr.ecr.us-east-1.amazonaws.com/lupol-dev-two`
- App Runner service ARN: `arn:aws:apprunner:us-east-1:762891556894:service/lupol-dev-two-service/777cbb1c7d7448e591830f2eeab3bebb`
- Public URL: `https://v4ybpmidbj.us-east-1.awsapprunner.com`
- Region: `us-east-1`

## Steps

1. **Preflight**
   - Run `git status --short`. If there are uncommitted changes, warn the user that the deploy will include them (the Docker image is built from the working tree, not from git) but continue.
   - Run `docker info` to confirm Docker Desktop is running. If not, tell the user to start it and stop.

2. **Build the jar** (compiles Java + builds the Vue frontend, ~2 min)
   ```
   ./mvnw clean package
   ```
   Abort if the build or tests fail.

3. **Build the Docker image** (the Dockerfile copies the locally built jar)
   ```
   docker build -t lupol-dev-two .
   ```

4. **Push to ECR**
   ```
   aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 762891556894.dkr.ecr.us-east-1.amazonaws.com
   docker tag lupol-dev-two:latest 762891556894.dkr.ecr.us-east-1.amazonaws.com/lupol-dev-two:latest
   docker push 762891556894.dkr.ecr.us-east-1.amazonaws.com/lupol-dev-two:latest
   ```

5. **Trigger the App Runner deployment** (auto-deploy is OFF for this service, so this is required)
   ```
   aws apprunner start-deployment --region us-east-1 --service-arn <service ARN>
   ```

6. **Wait for the deployment to finish** (typically 2–5 min). Poll every ~30 s until `Service.Status` returns to `RUNNING`:
   ```
   aws apprunner describe-service --region us-east-1 --service-arn <service ARN> --query "Service.Status" --output text
   ```
   While deploying it reports `OPERATION_IN_PROGRESS`. If it stays in progress for more than 10 minutes, or ends in a failed state, fetch the latest operation with `aws apprunner list-operations` and report what happened.

7. **Smoke test the live site**
   - `GET https://v4ybpmidbj.us-east-1.awsapprunner.com/` must return 200.
   - `POST https://v4ybpmidbj.us-east-1.awsapprunner.com/api/analytics/visit` with a small JSON body must return 202.

8. **Report**: image pushed, deployment status, smoke-test results, and the public URL.
