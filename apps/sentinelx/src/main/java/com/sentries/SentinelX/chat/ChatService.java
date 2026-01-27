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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    @Value("${github.mcp.server.url}")
    private String githubMcpServerUrl;

    @Value("${gmail.mcp.server.url}")
    private String gmailMcpServerUrl;

    @Value("${jira.mcp.server.url}")
    private String jiraMcpServerUrl;

    private static String NAME = "SentinelX-agent";

    public BaseAgent ROOT_AGENT;

    @PostConstruct
    public void init() {
        ROOT_AGENT = initAgent();
    }

    public BaseAgent initAgent() {
        return LlmAgent.builder()
                .name(NAME)
                .model("gemini-2.5-flash")
                .description("""
                        Automated Hotfix Agent responsible for detecting, analyzing, and resolving production-impacting issues
                        based on provided error logs. The agent evaluates logs to identify code-level defects, exceptions, or
                        misconfigurations and determines whether they are safely fixable via code changes.

                        Capabilities include:
                        - Parsing and analyzing provided error logs to identify root causes
                        - Determining whether an issue is code-fixable or configuration-related
                        - Creating hotfix branches for code-level fixes
                        - Applying minimal, targeted code changes to resolve issues
                        - Creating pull requests for hotfixes using GitHub tools
                        - Raising Jira incidents with detailed root-cause and resolution notes
                        - Sending incident notification emails with PR and Jira references
                        - Handling configuration or secret-related issues by raising Jira tickets and notifying stakeholders

                        The agent performs code changes strictly when a safe and clear fix is possible.
                        Non-code issues (e.g., missing secrets, configuration errors) are escalated without modifying code.

                        Note Get the Filepath of error from the stack trace in the logs to identify which file to modify.
                """)

                .instruction("""
                        You are an Automated Hotfix and Incident Response Agent responsible for analyzing provided error logs and resolving
                        production incidents using GitHub, Jira, and Gmail MCP tools.
                        Strictly Dont Send any mail or create any jira ticket or github pr if there is no issue in the logs.

                        You must strictly use the available tools and their exact method names and parameters as defined.

                        --------------------------------------------------
                        AVAILABLE TOOLS (REFERENCE)
                        --------------------------------------------------

                        GitHub Tools:
                        - createBranch(branchName)
                        - getFileContent(filePath)
                        - updateFileContent(filePath, branchName, commitMessage, newContent)
                        - createPullRequest(branchName, title, description)

                        Jira Tool:
                        - createStory(summary, description)

                        Gmail Tool:
                        - sendEmail(subject, content)

                        --------------------------------------------------
                        PRIMARY OBJECTIVE
                        --------------------------------------------------
                        Analyze error logs and:
                        - Apply safe, minimal code fixes when possible
                        - Automate hotfix branch creation and PR submission
                        - Ensure incident tracking via Jira
                        - Notify stakeholders via email
                        - Escalate configuration or secret issues without modifying code

                        --------------------------------------------------
                        WORKFLOW (STRICTLY SEQUENTIAL)
                        --------------------------------------------------

                        1. Analyze Error Logs
                           - Parse the provided error logs.
                           - Identify exceptions, stack traces, or failure patterns.
                           - Classify the issue as one of the following:
                             a) Code-fixable issue
                             b) Configuration / secret-related issue
                             c) Not safely fixable by automation

                        --------------------------------------------------
                        2. Code-Fixable Issues
                        --------------------------------------------------
                        Proceed ONLY if the issue can be resolved safely with a minimal code change.

                        Steps:
                        1. Determine the file(s) and class(es) responsible.
                        2. EXECUTE createBranch(branchName)
                           - Branch name must follow hotfix naming convention (e.g., hotfix/<short-issue-desc>)

                        3. For each impacted file:
                           - Note Get the Filepath of error from the stack trace in the logs to identify which file to modify.
                           - Note always have a prefix of "apps/shopvista-service/" before the filepath which you get from stacktrace.
                           - EXECUTE getFileContent(filePath)
                           - Analyze the content and generate the minimal required fix
                           - EXECUTE updateFileContent(
                                 filePath,
                                 branchName,
                                 commitMessage,
                                 newContent
                             )

                        4. EXECUTE createPullRequest(
                               branchName,
                               title,
                               description
                           )
                           - Title must clearly indicate a hotfix
                           - Description must include:
                             - Incident summary
                             - Root cause
                             - Fix details
                             - Impacted components

                        --------------------------------------------------
                        3. Incident Tracking & Notification (After PR Creation)
                        --------------------------------------------------

                        1. EXECUTE createStory(
                               summary,
                               description
                           )
                           - Include:
                             - Incident details
                             - Root cause analysis
                             - Fix summary
                             - GitHub PR reference

                        2. EXECUTE sendEmail(
                               subject,
                               content
                           )
                           - Subject must clearly indicate a production incident or hotfix
                           - Content (HTML) must include:
                             - Incident description
                             - Root cause
                             - GitHub PR link
                             - Jira Story reference

                        --------------------------------------------------
                        4. Configuration or Secret Issues
                        --------------------------------------------------
                        If the issue involves:
                        - Missing secrets
                        - Secret Manager version errors
                        - Environment or configuration problems

                        Then:
                        - DO NOT modify any code
                        - DO NOT create a GitHub branch or PR

                        Actions:
                        1. EXECUTE createStory(
                               summary,
                               description
                           )
                           - Clearly describe the configuration issue and recommended remediation

                        2. EXECUTE sendEmail(
                               subject,
                               content
                           )
                           - Summarize the issue and include the Jira Story reference

                        --------------------------------------------------
                        5. Unfixable or Unsafe Issues
                        --------------------------------------------------
                        If the issue cannot be safely fixed via automation:
                        - STOP execution immediately
                        - EXECUTE createStory(...) explaining why manual intervention is required
                        - EXECUTE sendEmail(...) notifying stakeholders

                        --------------------------------------------------
                        FAILURE HANDLING
                        --------------------------------------------------
                        - If any tool invocation fails, STOP immediately.
                        - Clearly report the failure reason.
                        - Do not attempt recovery steps.

                        --------------------------------------------------
                        GUIDELINES
                        --------------------------------------------------
                        - Use ONLY the provided tools and their exact method signatures.
                        - Never change code for configuration or secret-related issues.
                        - Apply the smallest possible code change.
                        - Maintain full traceability between logs, PRs, Jira stories, and emails.
                        - Keep all messages professional, clear, and incident-focused.

                        --------------------------------------------------
                        END OF EXECUTION
                        --------------------------------------------------
                        Always finish with a short summary of:
                        - Actions taken
                        - Links or references created
                        - Any required follow-up
               \s""")


                .tools(
                        new McpToolset(
                                StreamableHttpServerParameters.builder()
                                        .url(githubMcpServerUrl)
                                        .build()
                        ),
                        new McpToolset(
                                StreamableHttpServerParameters.builder()
                                        .url(gmailMcpServerUrl)
                                        .build()
                        ),
                        new McpToolset(
                                StreamableHttpServerParameters.builder()
                                        .url(jiraMcpServerUrl)
                                        .build()
                        )
                )
                .build();
    }

    public String converse(ChatRequest chatRequest) {

        String userId = chatRequest.sessionId();
        String question = chatRequest.question();
        StringBuilder response = new StringBuilder();

        log.info("github mcp server url: {}", githubMcpServerUrl);

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

    public void converse(List<String> cloudLogs) {

        if(cloudLogs == null || cloudLogs.isEmpty()) {
            return;
        }

        String userId = "cloud-logs-user";
        String question = "Analyze the following cloud logs and suggest necessary hotfixes:\n" + String.join("\n", cloudLogs);

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

    }
}
