package com.sentries.SentinelX.pubsub;

import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.*;
import com.sentries.SentinelX.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PubSubService {

    private final SubscriptionName subscriptionName;

    private final ChatService chatService;

    @Scheduled(fixedRate = 120000) // 2 minutes
    public void pullMessages() {

        try (SubscriberStub subscriber = SubscriberStubSettings
                .newBuilder()
                .build()
                .createStub()) {

            PullRequest pullRequest =
                    PullRequest.newBuilder()
                            .setSubscription(subscriptionName.toString())
                            .setMaxMessages(1000)
                            .build();

            PullResponse response = subscriber.pullCallable().call(pullRequest);

            List<String> receivedMessages = new ArrayList<>();

            for (ReceivedMessage message : response.getReceivedMessagesList()) {

                String data = message.getMessage().getData().toStringUtf8();
                String messageId = message.getMessage().getMessageId();

                log.info("Received Message from Topic");

                receivedMessages.add(data);

                // ACK message
                AcknowledgeRequest ackRequest =
                        AcknowledgeRequest.newBuilder()
                                .setSubscription(subscriptionName.toString())
                                .addAckIds(message.getAckId())
                                .build();

                subscriber.acknowledgeCallable().call(ackRequest);
            }

            processMessage(receivedMessages);

        } catch (Exception e) {
            log.error("Error pulling messages", e);
        }
    }

    private void processMessage(List<String> data) {

        log.info("Trigger Called from Agent");
        chatService.converse(data);
        log.info("Agent Triggered Successfully with Logs");

    }
}
