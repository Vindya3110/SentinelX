# SentinelX

Monorepo containing several Spring Boot microservices and MCP (Model Context Protocol) servers used to automate repository maintenance, issue creation and email sending, plus an e-commerce backend.

Contents
- `apps/sentinelx/` — AI agent service (SentinelX) — port: 8081
- `apps/githubmcp/` — GitHub MCP server (tools for GitHub) — port: 8080
- `apps/gmailmcp/` — Gmail MCP server (email tools) — port: 8083
- `apps/jiramcp/` — Jira MCP server (Jira automation) — port: 8080
- `apps/shopvista-service/` — ShopVista e-commerce backend (PostgreSQL) — port: 8080

This repository includes GitHub Actions workflows to build, test, containerize and deploy each service to Google Cloud Run and a deletion workflow to remove Cloud Run services safely.

---

## Quick overview

Each project is a Spring Boot application using Java 21 and Gradle. They are designed to run locally using the included Gradle wrapper or be built into Docker images and deployed to Cloud Run.

The repository follows these conventions:
- Each service lives in `apps/<service-name>` with its own `build.gradle` and `Dockerfile`.
- Secrets are kept in Google Cloud Secret Manager for GCP deployments (see application properties using `sm://` placeholders).
- CI/CD is configured via GitHub Actions in `.github/workflows/`.

---

## Prerequisites (local)
- Java 21 (or use the Gradle JVM toolchain)
- Gradle (the repo includes a Gradle wrapper; prefer `./gradlew`)
- Docker (if you want to run containers locally)
- For database-backed services: a PostgreSQL instance (ShopVista)

Optional (for deploying to GCP):
- gcloud CLI configured with a service account or Workload Identity
- GCP project, Artifact Registry, Cloud Run and Secret Manager access

---

## Running services locally (dev)

You can run any service using the Gradle wrapper in its folder. Example — start `sentinelx`:

```bash
# From repo root
cd apps/sentinelx
./gradlew bootRun
```

Run `githubmcp`:

```bash
cd apps/githubmcp
./gradlew bootRun
```

Run `gmailmcp` (stateless profile):

```bash
cd apps/gmailmcp
./gradlew bootRun
# or set profile explicitly
SPRING_PROFILES_ACTIVE=stateless ./gradlew bootRun
```

Run `jiramcp`:

```bash
cd apps/jiramcp
./gradlew bootRun
```

Run `shopvista-service` (requires DB):

```bash
cd apps/shopvista-service
# Set DB config via env or application-local.properties
./gradlew bootRun
```

Notes:
- Each service has `src/main/resources/application.properties` and `application-stateless.properties`/`bootstrap-gcp.yml`. Local runs can use environment variables or local property overrides.
- For `shopvista-service`, ensure `spring.datasource.*` values point to a running PostgreSQL.

---

## Build & run with Docker (locally)

Each service contains a `Dockerfile`. Build and run with Docker like this (example for `githubmcp`):

```bash
# Build
docker build -t githubmcp:local -f apps/githubmcp/Dockerfile apps/githubmcp

# Run (expose port 8080 locally)
docker run --rm -p 8080:8080 \
  -e "SPRING_PROFILES_ACTIVE=stateless" \
  -e "APP_TOOL_REPO_NAME=SentinelX" \
  githubmcp:local
```

For `gmailmcp` (port 8083):

```bash
docker build -t gmailmcp:local -f apps/gmailmcp/Dockerfile apps/gmailmcp
docker run --rm -p 8083:8083 -e SPRING_PROFILES_ACTIVE=stateless gmailmcp:local
```

For `shopvista-service` you will likely need to provide DB connection environment variables and (optionally) mount a Cloud SQL socket or use a Cloud SQL Auth proxy.

---

## GitHub Actions workflows

Workflows live in `.github/workflows/` and include (but are not limited to):
- `build-only.yml` — CI: build and test
- `deploy-sentinelx.yml` — manual Cloud Run deploy for `sentinelx`
- `deploy-githubmcp.yml` — manual deploy for `githubmcp`
- `deploy-gmailmcp.yml` — manual deploy for `gmailmcp`
- `deploy-jiramcp.yml` — manual deploy for `jiramcp`
- `deploy-shopvista.yml` — manual deploy for `shopvista-service`
- `delete-services.yml` — manual workflow to delete Cloud Run services safely (confirm by typing `DELETE`)

To run a deploy workflow from GitHub > Actions: choose the workflow, click "Run workflow", and select the branch.

---

## Secrets & variables required for CI/CD (GCP)

Repository Secrets (GitHub):
- `GCP_PROJECT_ID` — your GCP project id
- `GCP_WORKLOAD_IDENTITY_PROVIDER` — workload identity provider for the repo
- `GCP_SERVICE_ACCOUNT` — service account email used by the actions
- (For ShopVista) `CLOUDSQL_INSTANCE_CONNECTION_NAME` — Cloud SQL connection name (project:region:instance)

Application secrets (GCP Secret Manager keys referenced by apps):
- `github-pat`, `github-userid` — GitHub PAT and owner
- `gmail-host`, `gmail-port`, `gmail-username`, `gmail-password`, `gmail-recipient-list`
- `jira-base-url`, `jira-email`, `jira-api-token`, `jira-encoded-token`
- Postgres credentials for `shopvista-service` (pg-host, pg-username, pg-password)

The apps use `spring.config.import=sm://` to fetch these secrets at runtime when deployed to GCP.

---

## Recommended local environment (.env.example)

You may create `.env` files for each service when running locally. Example keys `apps/gmailmcp/.env.example`:

```ini
GMAIL_HOST=smtp.gmail.com
GMAIL_PORT=587
GMAIL_USERNAME=you@example.com
GMAIL_PASSWORD=app-password
GMAIL_RECIPIENT_LIST=dev@example.com
```

For `shopvista-service`:

```ini
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/shopvista
SPRING_DATASOURCE_USERNAME=shopvista
SPRING_DATASOURCE_PASSWORD=secret
```

Do not commit secrets. Add `.env` to `.gitignore` if you keep local copies.

---

## Tips for developers

- Use the Gradle wrapper (`./gradlew`) from the repository root; each app's `build.gradle` is configured for Java 21.
- When testing Cloud Run deployments, use the GitHub Actions workflows for consistent builds and artifact push.
- `shopvista-service` uses JPA and PostgreSQL; if you're running locally use a local Postgres or Testcontainers.

---

## Contributing / Developing

- Create a feature branch from `main` or your working branch.
- Add tests where applicable; our workflows run `./gradlew test` for each app.
- Open a PR and run CI. If you need to deploy for manual verification, use the deploy workflows.

---

## Where to find things

- Dockerfiles: `apps/*/Dockerfile`
- App entrypoints (`SpringBootApplication`): `apps/*/src/main/java/**/*Application.java`
- Workflows: `.github/workflows/`
- Secrets usage: check `src/main/resources/*` and `bootstrap-gcp.yml` files for each app