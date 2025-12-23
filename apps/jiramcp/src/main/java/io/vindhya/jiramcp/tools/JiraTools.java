package io.vindhya.jiramcp.tools;

import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@Component
@Slf4j
public class JiraTools {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${jira.base-url}")
    private String jiraBaseUrl;

    @Value("${jira.email}")
    private String jiraEmail;

    @Value("${jira.api-token}")
    private String jiraApiToken;

    @Value("${jira.project-key}")
    private String projectKey;

    @Value("${jira.epic-key}")
    private String epicKey;

    @Value("${jira.assignee}")
    private String assignee;

    @Value("${jira.encoded.token}")
    private String jiraEncodedToken;

    public JiraTools(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @McpTool(description = "Create a new Story issue under an Epic in Jira")
    public Map<String, Object> createStory(
            @McpToolParam(description = "Story title/summary")
            String summary,
            @McpToolParam(description = "Story description")
            String description
    ) {
        try {
            // Build ADF (Atlassian Document Format) description
            Map<String, Object> descriptionContent = new HashMap<>();
            descriptionContent.put("type", "doc");
            descriptionContent.put("version", 1);
            descriptionContent.put("content", java.util.List.of(
                Map.of(
                    "type", "paragraph",
                    "content", java.util.List.of(
                        Map.of(
                            "type", "text",
                            "text", description
                        )
                    )
                )
            ));

            // Build fields
            Map<String, Object> fields = new HashMap<>();
            fields.put("project", Map.of("key", projectKey));
            fields.put("summary", summary);
            fields.put("description", descriptionContent);
            fields.put("issuetype", Map.of("name", "Story"));
            fields.put("parent", Map.of("key", epicKey));

            Map<String, Object> issueData = new HashMap<>();
            issueData.put("fields", fields);

            String url = jiraBaseUrl + "/rest/api/3/issue";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(issueData, createHeaders());

            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            
            log.info("Story created successfully with key: {}", response.get("key"));
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("issueKey", response.get("key"));
            result.put("issueId", response.get("id"));
            return result;
            
        } catch (Exception e) {
            log.error("Error creating story", e);
            return createErrorResponse("Failed to create story: " + e.getMessage());
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        headers.set("Authorization", "Basic " + jiraEncodedToken);
        return headers;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", message);
        return error;
    }
}