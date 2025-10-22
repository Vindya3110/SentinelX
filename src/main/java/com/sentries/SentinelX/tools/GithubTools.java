package com.sentries.SentinelX.tools;


import com.google.adk.tools.Annotations.Schema;
import com.sentries.SentinelX.secret.SecretConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubTools {


     private final SecretConfig secretConfig;

    private final static RestTemplate restTemplate = new RestTemplate();

    private static String gitToken;

    private static String gitOwner;

    private static String repoName;



     @PostConstruct
    public void init(){
        
        gitToken = secretConfig.getGithubToken();
        gitOwner = secretConfig.getGithubOwner();
        repoName = secretConfig.getRepoName();
    }

   

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + gitToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        return headers;
    }

    public static Map<String,String> createBranch(
            @Schema(description = "branch name to be created")
            String branchName
    ) {
        log.info("Creating branch {}", branchName);

        String orgName = gitOwner; // Replace with your organization name

        // Get repo info to find default branch
        String repoUrl = "https://api.github.com/repos/" + orgName + "/" + repoName;
        HttpEntity<String> repoEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map> repoResponse = restTemplate.exchange(repoUrl, HttpMethod.GET, repoEntity, Map.class);
        String defaultBranch = (String) repoResponse.getBody().get("default_branch");

        // Get default branch SHA
        String refUrl = repoUrl + "/git/refs/heads/" + defaultBranch;
        HttpEntity<String> getEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map> refResponse = restTemplate.exchange(refUrl, HttpMethod.GET, getEntity, Map.class);
        String sha = (String) ((Map) refResponse.getBody().get("object")).get("sha");

        // Create branch
        String url = repoUrl + "/git/refs";
        Map<String, Object> body = Map.of(
                "ref", "refs/heads/" + branchName,
                "sha", sha
        );

        HttpEntity<Map> entity = new HttpEntity<>(body, getHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return Map.of(
                "branchName", branchName,
                "message", "Branch created successfully"
        );
    }

    public static Map<String, String> createPullRequest(
            @Schema(description = "branch name for which PR needs to be created")
            String branchName,
            @Schema(description = "title of the pull request")
            String title,
            @Schema(description = "description of the changes")
            String description
    ) {
        log.info("Creating pull request for branch {}", branchName);

        String orgName = gitOwner;

        // Get repo info to find default branch
        String repoUrl = "https://api.github.com/repos/" + orgName + "/" + repoName;
        HttpEntity<String> repoEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map> repoResponse = restTemplate.exchange(repoUrl, HttpMethod.GET, repoEntity, Map.class);
        String defaultBranch = (String) repoResponse.getBody().get("default_branch");

        // Create pull request
        String prUrl = repoUrl + "/pulls";
        Map<String, Object> body = Map.of(
                "title", title,
                "body", description,
                "head", branchName,
                "base", defaultBranch
        );

        HttpEntity<Map> entity = new HttpEntity<>(body, getHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(prUrl, HttpMethod.POST, entity, Map.class);

        return Map.of(
                "pullRequestUrl", (String) response.getBody().get("html_url"),
                "message", "Pull request created successfully"
        );
    }

    public static Map<String, String> mergePullRequest(
            @Schema(description = "pull request number")
            int prNumber,
            @Schema(description = "commit message for the merge")
            String commitMessage,
            @Schema(description = "merge method: merge, squash, or rebase")
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

        HttpEntity<Map> entity = new HttpEntity<>(body, getHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(mergeUrl, HttpMethod.PUT, entity, Map.class);

        return Map.of(
                "message", "Pull request merged successfully",
                "sha", (String) response.getBody().get("sha")
        );
    }

    public static Map<String, String> getFileContent(
            @Schema(description = "path of the file in the repository")
            String filePath
    ) {
        log.info("Fetching file content for path: {}", filePath);

        String orgName = gitOwner;
        String url = String.format(
                "https://api.github.com/repos/%s/%s/contents/%s",
                orgName, "order-service", filePath
        );

        HttpEntity<String> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map body = response.getBody();
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

    public static Map<String, String> updateFileContent(
            @Schema(description = "path of the file in the repository")
            String filePath,
            @Schema(description = "branch name to update the file in")
            String branchName,
            @Schema(description = "commit message")
            String commitMessage,
            @Schema(description = "new file content")
            String newContent
    ) {
        log.info("Updating file content for path: {}", filePath);
        String orgName = gitOwner;
        String url = String.format(
                "https://api.github.com/repos/%s/%s/contents/%s",
                orgName, "order-service" , filePath
        );

        HttpEntity<String> getEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map> getResponse = restTemplate.exchange(url + "?ref=" + branchName, HttpMethod.GET, getEntity, Map.class);
        String sha = (String) getResponse.getBody().get("sha");

        String base64Content = java.util.Base64.getEncoder().encodeToString(newContent.getBytes());

        Map<String, Object> body = Map.of(
                "message", commitMessage,
                "content", base64Content,
                "branch", branchName,
                "sha", sha
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);

        return Map.of(
                "message", "File updated successfully"
        );
    }
}

