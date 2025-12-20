# Jira MCP Server - Help

## Overview
This is a Jira Team-Managed integration for the Model Context Protocol (MCP), built with Spring Boot 3.5.8 and Java 21.

## Architecture

### Components

1. **JiraTeamManagedClient**: Core client for Jira API interactions
   - Uses RestTemplate for HTTP communication
   - Handles Basic Authentication with email and API token
   - Methods return `Map<String, String>` for flexible response handling

2. **JiraConfig**: Spring configuration for dependency injection
   - Configures RestTemplate bean
   - Creates JiraTeamManagedClient bean with properties from application.properties

3. **JiraTools**: Service layer for Jira operations
   - Wrapper around JiraTeamManagedClient
   - Provides easy-to-use methods for common Jira operations

## Methods

### JiraTeamManagedClient

#### createStoryUnderEpic()
```java
public Map<String, String> createStoryUnderEpic(
    String projectKey,
    String epicKey,
    String summary,
    String description
)
```
Creates a new Story issue linked to an Epic.

**Parameters:**
- `projectKey`: Jira project key (e.g., "KAN")
- `epicKey`: Parent Epic key (e.g., "KAN-4")
- `summary`: Story title
- `description`: Story description

**Returns:** Map with keys: status, message, response

#### getIssue()
```java
public Map<String, String> getIssue(String issueKey)
```
Retrieves details of an issue.

**Parameters:**
- `issueKey`: Issue key (e.g., "KAN-5")

**Returns:** Map with keys: status, message, response

#### updateIssue()
```java
public Map<String, String> updateIssue(
    String issueKey,
    Map<String, Object> updateFields
)
```
Updates an issue with new field values.

**Parameters:**
- `issueKey`: Issue key
- `updateFields`: Map of field names to new values

**Returns:** Map with keys: status, message, exception

## Configuration

Update `src/main/resources/application.properties`:

```properties
jira.base-url=https://your-instance.atlassian.net
jira.email=your-email@example.com
jira.api-token=YOUR_ATLASSIAN_API_TOKEN
jira.assignee=assignee-account-id@example.com
```

## Getting Jira API Token

1. Go to https://id.atlassian.com/manage-profile/security
2. Click "Create and manage API tokens"
3. Click "Create API token"
4. Copy the token and use in configuration

## Authentication

Uses Basic Authentication encoded in Base64:
- Username: Email address
- Password: API token

## Response Format

All methods return `Map<String, String>` with these possible keys:

**Success Response:**
```
{
  "status": "success",
  "message": "Operation description",
  "response": "API response details"
}
```

**Error Response:**
```
{
  "status": "error",
  "message": "Error description",
  "exception": "ExceptionClassName" (optional)
}
```

## Building & Running

### Build
```bash
./gradlew build
```

### Run
```bash
./gradlew bootRun
```

### Docker
```bash
./gradlew build
docker build -t jiramcp:latest .
docker run -p 8080:8080 -e JIRA_BASE_URL=... jiramcp:latest
```

## Dependencies

- Spring Boot 3.5.8
- Spring Web (RestTemplate)
- Spring AI MCP Server
- Google Cloud Secret Manager integration
- Lombok
- JUnit 5 (testing)

## Notes

- No static methods - all components are beans managed by Spring
- No main class in client code - only in JiramcpApplication
- All operations are asynchronous-ready through Spring
- Credentials can be managed through Google Cloud Secret Manager
