# Jira MCP Server

This is a Spring Boot application that provides Jira integration through an MCP (Model Context Protocol) server.

## Features

- Create stories under epics in Jira
- Retrieve issue details
- Update issue information

## Configuration

Edit `src/main/resources/application.properties` with your Jira credentials:

```properties
jira.base-url=https://your-domain.atlassian.net
jira.email=your-email@example.com
jira.api-token=YOUR_API_TOKEN
jira.assignee=assignee-id@example.com
```

## Running the Application

```bash
./gradlew bootRun
```

## Building

```bash
./gradlew build
```
