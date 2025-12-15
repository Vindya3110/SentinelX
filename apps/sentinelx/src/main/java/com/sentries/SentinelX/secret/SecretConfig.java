package com.sentries.SentinelX.secret;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SecretConfig {

    @Value("${app.tool.githubToken}")
    private String githubToken;

    @Value("${app.tool.githubOwner}")
    private String githubOwner;

    @Value("${app.tool.repoName}")
    private String repoName;

    // @Value("${app.project-id}")
    // private String projectId;

    // @Value("${mail.smtp.host}")
    // private String host;

    // @Value("${mail.smtp.port}")
    // private int port;

    // @Value("${mail.smtp.username}")
    // private String username;

    // @Value("${mail.smtp.password}")
    // private String password;

    // @Value("${mail.recipients}")
    // private String recipientList;

    // // Getter method
    // public String getGithubToken() {
    //     return githubToken;
    // }

    // public String getGithubOwner() {
    //     return githubOwner;
    // }

    // public String getProjectId() {
    //     return projectId;
    // }

    // public String getHost() {
    //     return host;
    // }

    // public int getPort() {
    //     return port;
    // }

    // public String getUsername() {
    //     return username;
    // }

    // public String getPassword() {
    //     return password;
    // }

    // public String getRecipientList() {
    //     return recipientList;
    // }
}
