package io.vindhya.githubmcp.tools;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GithubTools {

    @Value("${github.userId}")
    private  String userId;

    @Value("${github.pat}")
    private  String pat;

    @McpTool(description = "Create a new branch in the GitHub repository")
    public String createBranch() {
        return "Branch created successfully in the GitHub repository for user: " + userId;
    }
}
