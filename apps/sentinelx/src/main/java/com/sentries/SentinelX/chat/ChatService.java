package com.sentries.SentinelX.chat;


import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.adk.tools.mcp.McpToolset;
import com.google.adk.tools.mcp.StreamableHttpServerParameters;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Value("${github.mcp.server.url}")
    private String githubMcpServerUrl;

    @Value("${gmail.mcp.server.url}")
    private String gmailMcpServerUrl;

    @Value("${jira.mcp.server.url}")
    private String jiraMcpServerUrl;

    private static String NAME = "SentinelX-agent";

    public BaseAgent ROOT_AGENT = initAgent();

    public BaseAgent initAgent() {
        return LlmAgent.builder()
                .name(NAME)
                .model("gemini-2.5-flash")
                .description("""
                        Automated hotfix agent that detects, analyzes, and fixes critical errors in the codebase. \
                        Capable of executing complete hotfix workflow including error log analysis, branch creation, \
                        code analysis and fixing, secret version management, and automated PR creation and merging. \
                        Available operations:
                        - Fetch and analyze last 50 error logs
                        - Create hotfix branches automatically
                        - Retrieve and analyze file content for error resolution
                        - List and manage secret versions for Secret Manager issues
                        - Update file content with minimal necessary changes
                        - Create and merge pull requests with hotfix protocols
                        """)
                .instruction("""
                        You are a GitHub automation agent responsible for managing repositories, code reviews, branches, pull requests, and workflow automation. 
                        When initialized or given a user message, follow the workflow below carefully and sequentially.
                        
                        Primary Objective: Automate repository maintenance, branch management, and PR operations to improve code quality and delivery speed.
                        
                        **Workflow:**
                        
                        1. If the user asks to analyze or check the repository:
                           - EXECUTE getFileContent(filePath) or list repository files as required.
                           - Provide a concise analysis of the code, structure, or recent commits.
                        
                        2. If the user requests a new feature or fix:
                           - EXECUTE createBranch() to create a dedicated feature or hotfix branch.
                        
                        3. If the user wants to modify a file:
                           - EXECUTE getFileContent(filePath) to retrieve it.
                           - Analyze and generate the minimal and correct change.
                           - EXECUTE updateFileContent(filePath, updatedContent) to apply the update.
                        
                        4. For submitting changes:
                           - EXECUTE createPullRequest(targetBranch: 'dev' or as specified) with an appropriate title and summary.
                           - Include details such as change summary, impacted components, and reasoning.
                        
                        5. For merging PRs:
                           - EXECUTE mergePullRequest(prNumber) using a normal merge with a descriptive commit message.
                        
                        6. If any step fails, STOP immediately and report the reason clearly.
                        
                        **Guidelines:**
                        - Always verify the safety and correctness of any change before committing.
                        - Use clear and concise commit and PR messages.
                        - Maintain a professional and traceable workflow aligned with Git best practices.
                        - Never overwrite or delete content unless explicitly directed by the user.
                        
                        End each execution with a short summary of what was done and any follow-up recommendations.
                        """)

                .tools(
                        new McpToolset(
                                StreamableHttpServerParameters.builder(githubMcpServerUrl).build()
                        ),
                        new McpToolset(
                                StreamableHttpServerParameters.builder(gmailMcpServerUrl).build()
                        ),
                        new McpToolset(
                                StreamableHttpServerParameters.builder(jiraMcpServerUrl).build()
                        )
                )
                .build();
    }

    public String converse(ChatRequest chatRequest) {

        String userId = chatRequest.sessionId();
        String question = chatRequest.question();
        StringBuilder response = new StringBuilder();


        InMemoryRunner runner = new InMemoryRunner(ROOT_AGENT);

        Session session = runner
                .sessionService()
                .createSession(runner.appName(), userId)
                .blockingGet();

        Content userMsg = Content.fromParts(Part.fromText(question));
        Flowable<Event> events = runner.runAsync(session.userId(), session.id(), userMsg);

        events.blockingForEach(event -> {
            Content content = event.content().get();
            if (content.parts().get().get(0).text().isPresent()) {
                response.append(content.parts().get().get(0).text().get());
            }
        });

        return response.toString();
    }
}
