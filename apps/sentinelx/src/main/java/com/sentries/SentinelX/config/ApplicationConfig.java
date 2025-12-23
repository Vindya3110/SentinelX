package com.sentries.SentinelX.config;

import com.google.pubsub.v1.SubscriptionName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${gcp.pubsub.subscription}")
    private String subscriptionId;

    @Bean
    public SubscriptionName subscriptionName() {
        return SubscriptionName.of(projectId, subscriptionId);
    }
}
