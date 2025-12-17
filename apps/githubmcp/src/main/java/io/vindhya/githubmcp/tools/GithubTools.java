package io.vindhya.githubmcp.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class GithubTools {

    @Value("${github.pat}")
    private String gitToken;

    @Value("${github.userId}")
    private String gitOwner;

    @Value("${app.tool.repoName}")
    private String repoName;

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + gitToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        return headers;
    }

    @McpTool(description = "Create a new branch in the GitHub repository")
    public Map<String, String> createBranch(
            @McpToolParam(description = "Name of the branch to be created")
            String branchName
    ) {
        log.info("Creating branch {}", branchName);

        String orgName = gitOwner;

        // Get repo info to find default branch
        String repoUrl = "https://api.github.com/repos/" + orgName + "/" + repoName;
        HttpEntity<String> repoEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map<String, Object>> repoResponse = restTemplate.exchange(
                repoUrl,
                HttpMethod.GET,
                repoEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        String defaultBranch = (String) repoResponse.getBody().get("default_branch");

        // Get default branch SHA
        String refUrl = repoUrl + "/git/refs/heads/" + defaultBranch;
        HttpEntity<String> getEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map<String, Object>> refResponse = restTemplate.exchange(
                refUrl,
                HttpMethod.GET,
                getEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        @SuppressWarnings("unchecked")
        String sha = (String) ((Map<String, Object>) refResponse.getBody().get("object")).get("sha");

        // Create branch
        String url = repoUrl + "/git/refs";
        Map<String, Object> body = Map.of(
                "ref", "refs/heads/" + branchName,
                "sha", sha
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
        restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        return Map.of(
                "branchName", branchName,
                "message", "Branch created successfully"
        );
    }

    @McpTool(description = "Create a pull request in the GitHub repository")
    public Map<String, String> createPullRequest(
            @McpToolParam(description = "Branch name for which PR needs to be created")
            String branchName,
            @McpToolParam(description = "Title of the pull request")
            String title,
            @McpToolParam(description = "Description of the changes")
            String description
    ) {
        log.info("Creating pull request for branch {}", branchName);

        String orgName = gitOwner;

        // Get repo info to find default branch
        String repoUrl = "https://api.github.com/repos/" + orgName + "/" + repoName;
        HttpEntity<String> repoEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map<String, Object>> repoResponse = restTemplate.exchange(
                repoUrl,
                HttpMethod.GET,
                repoEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        String defaultBranch = (String) repoResponse.getBody().get("default_branch");

        // Create pull request
        String prUrl = repoUrl + "/pulls";
        Map<String, Object> body = Map.of(
                "title", title,
                "body", description,
                "head", branchName,
                "base", defaultBranch
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                prUrl,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        return Map.of(
                "pullRequestUrl", (String) response.getBody().get("html_url"),
                "message", "Pull request created successfully"
        );
    }

    @McpTool(description = "Merge a pull request in the GitHub repository")
    public Map<String, String> mergePullRequest(
            @McpToolParam(description = "Pull request number")
            int prNumber,
            @McpToolParam(description = "Commit message for the merge")
            String commitMessage,
            @McpToolParam(description = "Merge method: merge, squash, or rebase")
            String mergeMethod
    ) {
        log.info("Merging pull request {}", prNumber);

        String orgName = gitOwner;

        String mergeUrl = String.format("https://api.github.com/repos/%s/%s/pulls/%d/merge",
                orgName, repoName, prNumber);

        Map<String, Object> body = Map.of(
                "commit_message", commitMessage,
                "merge_method", mergeMethod.toLowerCase()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                mergeUrl,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        return Map.of(
                "message", "Pull request merged successfully",
                "sha", (String) response.getBody().get("sha")
        );
    }

    @McpTool(description = "Get file content from the GitHub repository")
    public Map<String, String> getFileContent(
            @McpToolParam(description = "Path of the file in the repository")
            String filePath
    ) {
        log.info("Fetching file content for path: {}", filePath);

        String orgName = gitOwner;
        String url = String.format(
                "https://api.github.com/repos/%s/%s/contents/%s",
                orgName, repoName, filePath
        );

        HttpEntity<String> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> body = response.getBody();
        String content = (String) body.get("content");
        String encoding = (String) body.get("encoding");

        String decodedContent = content;
        if ("base64".equalsIgnoreCase(encoding) && content != null) {
            String base64Content = content.replaceAll("\\s", "");
            decodedContent = new String(java.util.Base64.getDecoder().decode(base64Content));
        }

        return Map.of(
                "message", "File content retrieved successfully",
                "filePath", filePath,
                "content", decodedContent
        );
    }

    @McpTool(description = "Update file content in the GitHub repository")
    public Map<String, String> updateFileContent(
            @McpToolParam(description = "Path of the file in the repository")
            String filePath,
            @McpToolParam(description = "Branch name to update the file in")
            String branchName,
            @McpToolParam(description = "Commit message")
            String commitMessage,
            @McpToolParam(description = "New file content")
            String newContent
    ) {
        log.info("Updating file content for path: {}", filePath);
        String orgName = gitOwner;
        String url = String.format(
                "https://api.github.com/repos/%s/%s/contents/%s",
                orgName, repoName, filePath
        );

        HttpEntity<String> getEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map<String, Object>> getResponse = restTemplate.exchange(
                url + "?ref=" + branchName,
                HttpMethod.GET,
                getEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        String sha = (String) getResponse.getBody().get("sha");

        String base64Content = java.util.Base64.getEncoder().encodeToString(newContent.getBytes());

        Map<String, Object> body = Map.of(
                "message", commitMessage,
                "content", base64Content,
                "branch", branchName,
                "sha", sha
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
        restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        return Map.of(
                "message", "File updated successfully"
        );
    }
}
