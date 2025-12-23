# GitHub Actions CI/CD Configuration

This directory contains GitHub Actions workflows for building, testing, and deploying the SentinelX applications to Google Cloud Platform.

## Workflows

### 1. Build and Test (`build-only.yml`)
- **Trigger**: Pull requests and pushes to `main`/`develop` branches
- **Purpose**: Validates code changes without deploying
- **Actions**:
  - Compiles Java code with Gradle
  - Runs unit tests
  - Uploads test results as artifacts

### 2. Deploy SentinelX (`deploy-sentinelx.yml`)
- **Trigger**: Manual (workflow_dispatch)
- **Purpose**: Builds and deploys SentinelX application to Cloud Run
- **Port**: 8081
- **Branch Selection**: Deploy any branch to production
- **Actions**:
  - Builds application with Gradle
  - Creates Docker image
  - Pushes to Artifact Registry
  - Deploys to Cloud Run (unauthenticated access)

### 3. Deploy GithubMCP (`deploy-githubmcp.yml`)
- **Trigger**: Manual (workflow_dispatch)
- **Purpose**: Builds and deploys GithubMCP application to Cloud Run
- **Port**: 8080
- **Branch Selection**: Deploy any branch to production
- **Actions**:
  - Builds application with Gradle
  - Creates Docker image
  - Pushes to Artifact Registry
  - Deploys to Cloud Run (unauthenticated access)

### 4. Deploy GmailMCP (`deploy-gmailmcp.yml`)
- **Trigger**: Manual (workflow_dispatch)
- **Purpose**: Builds and deploys GmailMCP application to Cloud Run
- **Port**: 8083
- **Branch Selection**: Deploy any branch to production
- **Actions**:
  - Builds application with Gradle
  - Creates Docker image
  - Pushes to Artifact Registry
  - Deploys to Cloud Run (unauthenticated access)

### 5. Deploy JiraMCP (`deploy-jiramcp.yml`)
- **Trigger**: Manual (workflow_dispatch)
- **Purpose**: Builds and deploys JiraMCP application to Cloud Run
- **Port**: 8080
- **Branch Selection**: Deploy any branch to production
- **Actions**:
  - Builds application with Gradle
  - Creates Docker image
  - Pushes to Artifact Registry
  - Deploys to Cloud Run (unauthenticated access)

### 6. Deploy ShopVista (`deploy-shopvista.yml`)
- **Trigger**: Manual (workflow_dispatch)
- **Purpose**: Builds and deploys ShopVista Service to Cloud Run
- **Port**: 8080
- **Database**: PostgreSQL (Cloud SQL)
- **Branch Selection**: Deploy any branch to production
- **Actions**:
  - Builds application with Gradle
  - Creates Docker image
  - Pushes to Artifact Registry
  - Deploys to Cloud Run with Cloud SQL connection
  - Configured with higher resources (1Gi memory, 2 CPU)

### 7. Delete Cloud Run Services (`delete-services.yml`)
- **Trigger**: Manual (workflow_dispatch)
- **Purpose**: Safely delete Cloud Run services
- **Options**: Delete SentinelX, GithubMCP, GmailMCP, JiraMCP, ShopVista, or all
- **Safety**: Requires typing "DELETE" to confirm
- **Actions**:
  - Validates confirmation
  - Checks if service exists
  - Deletes selected service(s)
  - Provides deletion summary

## Configuration

### Required Secrets
Set these in: **Settings → Secrets and variables → Actions → Secrets**

| Secret Name | Description | Example |
|------------|-------------|---------|
| `GCP_PROJECT_ID` | GCP Project ID | `sentinalx` |
| `GCP_WORKLOAD_IDENTITY_PROVIDER` | Workload Identity Provider full path | `projects/123456/locations/global/workloadIdentityPools/github-pool/providers/github-provider` |
| `GCP_SERVICE_ACCOUNT` | Service account email | `github-actions@sentinalx.iam.gserviceaccount.com` |

### Required Variables
Set these in: **Settings → Secrets and variables → Actions → Variables**

#### Common Variables
| Variable Name | Default | Description |
|--------------|---------|-------------|
| `GCP_REGION` | `us-central1` | GCP region for resources |
| `ARTIFACT_REGISTRY_REPO` | `sentinelx-docker-repo` | Artifact Registry repository name |

#### SentinelX Variables
| Variable Name | Default | Description |
|--------------|---------|-------------|
| `SENTINELX_SERVICE_NAME` | `sentinelx-service` | Cloud Run service name |
| `SENTINELX_MEMORY` | `512Mi` | Memory allocation |
| `SENTINELX_CPU` | `1` | CPU allocation |
| `SENTINELX_MIN_INSTANCES` | `0` | Minimum instances |
| `SENTINELX_MAX_INSTANCES` | `10` | Maximum instances |
| `SENTINELX_TIMEOUT` | `300` | Request timeout (seconds) |

#### GithubMCP Variables
| Variable Name | Default | Description |
|--------------|---------|-------------|
| `GITHUBMCP_SERVICE_NAME` | `githubmcp-service` | Cloud Run service name |
| `GITHUBMCP_MEMORY` | `512Mi` | Memory allocation |
| `GITHUBMCP_CPU` | `1` | CPU allocation |
| `GITHUBMCP_MIN_INSTANCES` | `0` | Minimum instances |
| `GITHUBMCP_MAX_INSTANCES` | `10` | Maximum instances |
| `GITHUBMCP_TIMEOUT` | `300` | Request timeout (seconds) |

#### GmailMCP Variables
| Variable Name | Default | Description |
|--------------|---------|-------------|
| `GMAILMCP_SERVICE_NAME` | `gmailmcp-service` | Cloud Run service name |
| `GMAILMCP_MEMORY` | `512Mi` | Memory allocation |
| `GMAILMCP_CPU` | `1` | CPU allocation |
| `GMAILMCP_MIN_INSTANCES` | `0` | Minimum instances |
| `GMAILMCP_MAX_INSTANCES` | `10` | Maximum instances |
| `GMAILMCP_TIMEOUT` | `300` | Request timeout (seconds) |

#### JiraMCP Variables
| Variable Name | Default | Description |
|--------------|---------|-------------|
| `JIRAMCP_SERVICE_NAME` | `jiramcp-service` | Cloud Run service name |
| `JIRAMCP_MEMORY` | `512Mi` | Memory allocation |
| `JIRAMCP_CPU` | `1` | CPU allocation |
| `JIRAMCP_MIN_INSTANCES` | `0` | Minimum instances |
| `JIRAMCP_MAX_INSTANCES` | `10` | Maximum instances |
| `JIRAMCP_TIMEOUT` | `300` | Request timeout (seconds) |

#### ShopVista Variables
| Variable Name | Default | Description |
|--------------|---------|-------------|
| `SHOPVISTA_SERVICE_NAME` | `shopvista-service` | Cloud Run service name |
| `SHOPVISTA_MEMORY` | `1Gi` | Memory allocation (higher for database) |
| `SHOPVISTA_CPU` | `2` | CPU allocation (higher for database) |
| `SHOPVISTA_MIN_INSTANCES` | `0` | Minimum instances |
| `SHOPVISTA_MAX_INSTANCES` | `10` | Maximum instances |
| `SHOPVISTA_TIMEOUT` | `300` | Request timeout (seconds) |

#### ShopVista-Specific Secrets
| Secret Name | Description | Example |
|------------|-------------|---------|
| `CLOUDSQL_INSTANCE_CONNECTION_NAME` | Cloud SQL instance connection name | `sentinalx:us-central1:shopvista-db` |

## Quick Start

### 1. Set up GCP
Run the setup script to configure GCP resources:
```bash
./scripts/setup-gcp.sh
```

### 2. Configure GitHub Secrets
Add the secrets output from the setup script to your GitHub repository.

### 3. Deploy
1. Go to **Actions** tab
2. Select the deployment workflow
3. Click **Run workflow**
4. Enter the branch name to deploy (e.g., `main`, `develop`, `feature-branch`)
5. Click **Run workflow** button

## Deployment Model

### Single Production Environment
- All deployments go to **production** environment
- Deploy from **any branch** (main, develop, feature branches)
- **Unauthenticated access** enabled (public endpoints)
- Suitable for testing and rapid iteration
- Full control over which code gets deployed

## Architecture

```
┌─────────────────┐
│  GitHub Actions │
└────────┬────────┘
         │
         ├─── Build with Gradle
         ├─── Run Tests
         ├─── Build Docker Image
         │
         ▼
┌─────────────────────┐
│ Artifact Registry   │
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│   Cloud Run         │
│   ┌──────────────┐  │
│   │  SentinelX   │  │ :8081
│   └──────────────┘  │
│   ┌──────────────┐  │
│   │  GithubMCP   │  │ :8080
│   └──────────────┘  │
│   ┌──────────────┐  │
│   │  GmailMCP    │  │ :8083
│   └──────────────┘  │
│   ┌──────────────┐  │
│   │  JiraMCP     │  │ :8080
│   └──────────────┘  │
│   ┌──────────────┐  │
│   │  ShopVista   │  │ :8080
│   └──────┬───────┘  │
└──────────┼──────────┘
           │
           ▼
    ┌─────────────┐
    │  Cloud SQL  │
    │ (PostgreSQL)│
    └─────────────┘
         │
         ▼
┌─────────────────────┐
│  Secret Manager     │
└─────────────────────┘
```

## Service Management

### Deleting Services

To delete a Cloud Run service:

1. Go to **Actions** tab
2. Select **Delete Cloud Run Services** workflow
3. Click **Run workflow**
4. Select which service to delete:
   - `sentinelx` - Delete only SentinelX
   - `githubmcp` - Delete only GithubMCP
   - `gmailmcp` - Delete only GmailMCP
   - `jiramcp` - Delete only JiraMCP
   - `shopvista` - Delete only ShopVista
   - `all` - Delete all services
5. Type **DELETE** in the confirmation field
6. Click **Run workflow** button

**Note:** This only deletes the Cloud Run service. Docker images in Artifact Registry are preserved.

## Security

- Uses Workload Identity Federation (no service account keys)
- Runs containers as non-root user
- Secrets managed via GCP Secret Manager
- Minimum required IAM permissions
- Attribute conditions restrict repository access
- **⚠️ Services allow unauthenticated access** (publicly accessible)

## Troubleshooting

### Build Failures
- Check Gradle build logs in GitHub Actions
- Verify Java version compatibility (JDK 21)
- Ensure all dependencies are available

### Deployment Failures
- Verify GCP authentication is working
- Check IAM permissions for service account
- Review Cloud Run deployment logs
- Ensure Artifact Registry repository exists

### Runtime Issues
- Check Cloud Run logs: `gcloud run services logs read SERVICE_NAME`
- Verify environment variables are set correctly
- Check Secret Manager permissions
- Review health check endpoint

## Additional Resources

- [Detailed Deployment Guide](../DEPLOYMENT.md)
- [GCP Setup Configuration](config/deployment-config.yml)
- [Cloud Run Documentation](https://cloud.google.com/run/docs)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
