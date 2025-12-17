# Gmail MCP Server

Gmail MCP (Model Context Protocol) Server - A Spring Boot application that provides MCP tools for sending emails via Gmail SMTP server.

## Overview

This application serves as an MCP server that integrates with Gmail to provide email sending capabilities. It supports both batch email sending to a predefined recipient list and sending emails to specific recipients.

## Features

- **Send Emails to Recipient List**: Send emails to a predefined list of recipients configured via Google Cloud Secret Manager
- **Send Email to Specific Recipient**: Send emails to individual recipients
- **HTML Content Support**: Full support for HTML formatted email content
- **Secure Credentials**: Uses Google Cloud Secret Manager for storing sensitive credentials
- **Logging**: Comprehensive logging for debugging and monitoring
- **MCP Protocol Support**: Integrates with Spring AI MCP server framework

## Prerequisites

- Java 21+
- Gradle 8.5+
- Google Cloud Project with Secret Manager enabled
- Gmail account with App Password (if 2FA is enabled)

## Configuration

### Environment Variables / Secret Manager

The application uses Google Cloud Secret Manager to store sensitive information:

```
gmail-host: SMTP server host (e.g., smtp.gmail.com)
gmail-port: SMTP server port (e.g., 587)
gmail-username: Gmail email address
gmail-password: Gmail App Password or password
gmail-recipient-list: Comma-separated list of recipient emails
github-userid: GitHub user ID (for future use)
github-pat: GitHub PAT (for future use)
```

### Application Properties

Main configuration in `src/main/resources/application.properties`:
- `spring.application.name`: gmailmcp
- `server.port`: 8083 (configurable in application-stateless.properties)
- `spring.cloud.gcp.project-id`: Your GCP project ID

## Building

```bash
./gradlew clean build
```

## Running

### Development
```bash
./gradlew bootRun
```

### Production
```bash
java -jar build/libs/gmailmcp-0.0.1-SNAPSHOT.jar
```

## Docker

Build the Docker image:
```bash
docker build -t gmailmcp:latest .
```

Run the Docker container:
```bash
docker run -p 8083:8083 \
  -e GOOGLE_APPLICATION_CREDENTIALS=/path/to/credentials.json \
  gmailmcp:latest
```

## MCP Tools

### 1. sendEmail

Sends an email to all recipients in the configured recipient list.

**Parameters:**
- `subject` (String): Subject line of the email
- `content` (String): Email body in HTML format

**Returns:**
- Map of recipient email → "SUCCESS" or "FAILED: error message"

### 2. sendEmailToRecipient

Sends an email to a specific recipient.

**Parameters:**
- `recipientEmail` (String): Target recipient email address
- `subject` (String): Subject line of the email
- `content` (String): Email body in HTML format

**Returns:**
- Map of recipient email → "SUCCESS" or "FAILED: error message"

## Gmail Configuration

### For Gmail with 2FA Enabled

1. Enable 2-Step Verification on your Google Account
2. Generate an App Password for your Gmail account
3. Use the App Password instead of your regular Gmail password
4. Store it in Google Cloud Secret Manager

### SMTP Settings

- **Host**: smtp.gmail.com
- **Port**: 587
- **Protocol**: SMTP with STARTTLS
- **TLS Version**: 1.2

## Troubleshooting

### Authentication Failures
- Verify your Gmail credentials are correct
- If using 2FA, ensure you're using an App Password, not your regular password
- Check that the Secret Manager secrets exist and are correctly named

### Connection Issues
- Ensure the SMTP host and port are correct
- Verify firewall allows outbound connections on port 587
- Check Gmail account security settings if blocking "Less secure apps"

### Recipient Email Errors
- Ensure email addresses are properly formatted
- Check for extra whitespace in recipient list configuration
- Verify recipients are valid email addresses

## Logs

The application logs are output to console. You can configure logging levels in `application.properties`:

```properties
logging.level.root=INFO
logging.level.io.vindhya.gmailmcp=DEBUG
```

## Contributing

When contributing, please ensure:
- All tools have proper logging
- Error handling is comprehensive
- Configuration is managed via Secret Manager
- Code follows Spring Best Practices

## License

This project is part of the SentinelX ecosystem.
