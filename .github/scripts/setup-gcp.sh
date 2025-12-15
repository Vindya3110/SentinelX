#!/bin/bash

# SentinelX GCP Setup Script
# This script helps you set up GCP resources for CI/CD deployment

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ID="sentinalx"
REGION="us-central1"
REPO_NAME="sentinelx-docker-repo"
SERVICE_ACCOUNT_NAME="github-actions"
WIF_POOL_NAME="github-pool"
WIF_PROVIDER_NAME="github-provider"
GITHUB_REPO_OWNER="Vindya3110"
GITHUB_REPO_NAME="SentinelX"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}SentinelX GCP Setup Script${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Check if gcloud is installed
if ! command -v gcloud &> /dev/null; then
    echo -e "${RED}Error: gcloud CLI is not installed${NC}"
    echo "Please install gcloud CLI: https://cloud.google.com/sdk/docs/install"
    exit 1
fi

# Verify project
echo -e "${YELLOW}Verifying GCP project: ${PROJECT_ID}${NC}"
if ! gcloud projects describe $PROJECT_ID &> /dev/null; then
    echo -e "${RED}Error: Project ${PROJECT_ID} not found or not accessible${NC}"
    exit 1
fi

# Set project
gcloud config set project $PROJECT_ID
echo -e "${GREEN}✓ Project set to ${PROJECT_ID}${NC}"
echo ""

# Enable required APIs
echo -e "${YELLOW}Enabling required GCP APIs...${NC}"
gcloud services enable \
    run.googleapis.com \
    artifactregistry.googleapis.com \
    secretmanager.googleapis.com \
    iam.googleapis.com \
    iamcredentials.googleapis.com \
    cloudbuild.googleapis.com

echo -e "${GREEN}✓ APIs enabled${NC}"
echo ""

# Create Artifact Registry repository
echo -e "${YELLOW}Creating Artifact Registry repository...${NC}"
if gcloud artifacts repositories describe $REPO_NAME --location=$REGION &> /dev/null; then
    echo -e "${YELLOW}Repository ${REPO_NAME} already exists${NC}"
else
    gcloud artifacts repositories create $REPO_NAME \
        --repository-format=docker \
        --location=$REGION \
        --description="Docker repository for SentinelX applications"
    echo -e "${GREEN}✓ Artifact Registry repository created${NC}"
fi
echo ""

# Create Service Account
echo -e "${YELLOW}Creating service account...${NC}"
SERVICE_ACCOUNT_EMAIL="${SERVICE_ACCOUNT_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"

if gcloud iam service-accounts describe $SERVICE_ACCOUNT_EMAIL &> /dev/null; then
    echo -e "${YELLOW}Service account ${SERVICE_ACCOUNT_EMAIL} already exists${NC}"
else
    gcloud iam service-accounts create $SERVICE_ACCOUNT_NAME \
        --description="Service account for GitHub Actions CI/CD" \
        --display-name="GitHub Actions"
    echo -e "${GREEN}✓ Service account created${NC}"
fi
echo ""

# Grant IAM permissions
echo -e "${YELLOW}Granting IAM permissions...${NC}"

# Artifact Registry Writer
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
    --role="roles/artifactregistry.writer" \
    --condition=None > /dev/null

# Cloud Run Admin
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
    --role="roles/run.admin" \
    --condition=None > /dev/null

# Service Account User
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
    --role="roles/iam.serviceAccountUser" \
    --condition=None > /dev/null

# Secret Manager Accessor
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
    --role="roles/secretmanager.secretAccessor" \
    --condition=None > /dev/null

echo -e "${GREEN}✓ IAM permissions granted${NC}"
echo ""

# Get Project Number
PROJECT_NUMBER=$(gcloud projects describe $PROJECT_ID --format='value(projectNumber)')
echo -e "${YELLOW}Project Number: ${PROJECT_NUMBER}${NC}"
echo ""

# Create Workload Identity Pool
echo -e "${YELLOW}Setting up Workload Identity Federation...${NC}"
if gcloud iam workload-identity-pools describe $WIF_POOL_NAME --location=global &> /dev/null; then
    echo -e "${YELLOW}Workload Identity Pool ${WIF_POOL_NAME} already exists${NC}"
else
    gcloud iam workload-identity-pools create $WIF_POOL_NAME \
        --location="global" \
        --display-name="GitHub Actions Pool"
    echo -e "${GREEN}✓ Workload Identity Pool created${NC}"
fi

# Create Workload Identity Provider
if gcloud iam workload-identity-pools providers describe $WIF_PROVIDER_NAME \
    --location=global \
    --workload-identity-pool=$WIF_POOL_NAME &> /dev/null; then
    echo -e "${YELLOW}Workload Identity Provider ${WIF_PROVIDER_NAME} already exists${NC}"
else
    gcloud iam workload-identity-pools providers create-oidc $WIF_PROVIDER_NAME \
        --location="global" \
        --workload-identity-pool="$WIF_POOL_NAME" \
        --issuer-uri="https://token.actions.githubusercontent.com" \
        --attribute-mapping="google.subject=assertion.sub,attribute.actor=assertion.actor,attribute.repository=assertion.repository" \
        --attribute-condition="assertion.repository_owner=='${GITHUB_REPO_OWNER}'"
    echo -e "${GREEN}✓ Workload Identity Provider created${NC}"
fi

# Grant Workload Identity User role
gcloud iam service-accounts add-iam-policy-binding $SERVICE_ACCOUNT_EMAIL \
    --role="roles/iam.workloadIdentityUser" \
    --member="principalSet://iam.googleapis.com/projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${WIF_POOL_NAME}/attribute.repository/${GITHUB_REPO_OWNER}/${GITHUB_REPO_NAME}" \
    > /dev/null

echo -e "${GREEN}✓ Workload Identity Federation configured${NC}"
echo ""

# Get Workload Identity Provider name
WIF_PROVIDER_FULL=$(gcloud iam workload-identity-pools providers describe $WIF_PROVIDER_NAME \
    --location="global" \
    --workload-identity-pool="$WIF_POOL_NAME" \
    --format='value(name)')

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Setup Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${YELLOW}Next Steps:${NC}"
echo ""
echo -e "1. Add the following secrets to your GitHub repository:"
echo -e "   Go to: https://github.com/${GITHUB_REPO_OWNER}/${GITHUB_REPO_NAME}/settings/secrets/actions"
echo ""
echo -e "${GREEN}   GCP_PROJECT_ID:${NC}"
echo -e "   ${PROJECT_ID}"
echo ""
echo -e "${GREEN}   GCP_WORKLOAD_IDENTITY_PROVIDER:${NC}"
echo -e "   ${WIF_PROVIDER_FULL}"
echo ""
echo -e "${GREEN}   GCP_SERVICE_ACCOUNT:${NC}"
echo -e "   ${SERVICE_ACCOUNT_EMAIL}"
echo ""
echo -e "2. Add the following variables to your GitHub repository:"
echo -e "   Go to: https://github.com/${GITHUB_REPO_OWNER}/${GITHUB_REPO_NAME}/settings/variables/actions"
echo ""
echo -e "${GREEN}   GCP_REGION:${NC} ${REGION}"
echo -e "${GREEN}   ARTIFACT_REGISTRY_REPO:${NC} ${REPO_NAME}"
echo ""
echo -e "3. Configure service-specific variables (optional):"
echo -e "   SENTINELX_SERVICE_NAME, SENTINELX_MEMORY, SENTINELX_CPU, etc."
echo -e "   GITHUBMCP_SERVICE_NAME, GITHUBMCP_MEMORY, GITHUBMCP_CPU, etc."
echo ""
echo -e "4. Trigger manual deployment from GitHub Actions"
echo ""
echo -e "${YELLOW}For detailed instructions, see DEPLOYMENT.md${NC}"
echo ""
