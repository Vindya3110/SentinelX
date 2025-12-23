package com.sentries.SentinelX.pubsub;

import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PubSubService {

    private final SubscriptionName subscriptionName;

    @Scheduled(fixedRate = 120000) // 2 minutes
    public void pullMessages() {

        try (SubscriberStub subscriber = SubscriberStubSettings
                .newBuilder()
                .build()
                .createStub()) {

            PullRequest pullRequest =
                    PullRequest.newBuilder()
                            .setSubscription(subscriptionName.toString())
                            .setMaxMessages(10)
                            .build();

            PullResponse response = subscriber.pullCallable().call(pullRequest);

            for (ReceivedMessage message : response.getReceivedMessagesList()) {

                String data = message.getMessage().getData().toStringUtf8();
                String messageId = message.getMessage().getMessageId();

                log.info("Received messageId={}, data={}", messageId, data);

                processMessage(data);

                // ACK message
                AcknowledgeRequest ackRequest =
                        AcknowledgeRequest.newBuilder()
                                .setSubscription(subscriptionName.toString())
                                .addAckIds(message.getAckId())
                                .build();

                subscriber.acknowledgeCallable().call(ackRequest);
            }

        } catch (Exception e) {
            log.error("Error pulling messages", e);
        }
    }

    private void processMessage(String data) {
        // Implement your message processing logic here
        log.info("Processing message: {}", data);
    }
}
