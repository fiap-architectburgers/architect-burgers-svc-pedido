package com.example.fiap.archburgers.adapters.externalsystem;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagamentoServiceQueueApi implements AutoCloseable {

    private final String sqsEndpoint;
    private final String pedidosQueueName;
    private final String pedidosQueueUrl;
    private final String pagamentosConcluidosQueueName;
    private final String pagamentosConcluidosQueueUrl;

    private final SqsClient sqsClient;

    public PagamentoServiceQueueApi(Environment environment) {
        this.sqsEndpoint = environment.getProperty("archburgers.integration.sqs.sqsEndpoint");
        if (sqsEndpoint == null)
            throw new IllegalArgumentException("archburgers.integration.sqs.sqsEndpoint not set");

        this.pedidosQueueName = environment.getProperty("archburgers.integration.sqs.pagamentosEmAbertoQueueName");
        if (pedidosQueueName == null)
            throw new IllegalArgumentException("archburgers.integration.sqs.pagamentosEmAbertoQueueName not set");

        this.pedidosQueueUrl = environment.getProperty("archburgers.integration.sqs.pagamentosEmAbertoQueueUrl");
        if (pedidosQueueUrl == null)
            throw new IllegalArgumentException("archburgers.integration.sqs.pagamentosEmAbertoQueueUrl not set");

        this.pagamentosConcluidosQueueName = environment.getProperty("archburgers.integration.sqs.pagamentosConcluidosQueueName");
        if (pagamentosConcluidosQueueName == null)
            throw new IllegalArgumentException("archburgers.integration.sqs.pagamentosConcluidosQueueName not set");

        this.pagamentosConcluidosQueueUrl = environment.getProperty("archburgers.integration.sqs.pagamentosConcluidosQueueUrl");
        if (pagamentosConcluidosQueueUrl == null)
            throw new IllegalArgumentException("archburgers.integration.sqs.pagamentosConcluidosQueueUrl not set");

        sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(sqsEndpoint))
                .build();

        // Create queues if they don't exist
        for (String queueName : List.of(pedidosQueueName, pagamentosConcluidosQueueName)) {
            CreateQueueRequest createRequest = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();
            sqsClient.createQueue(createRequest);
        }
    }

    public void sendMessageQueuePagamento(String payload) {
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(pedidosQueueUrl)
                .messageBody(payload)
                .delaySeconds(0)
                .build();
        sqsClient.sendMessage(sendMsgRequest);
    }

    public List<MessageSummary> receiveMessagesQueueConfirmacao() {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(pagamentosConcluidosQueueUrl)
                .maxNumberOfMessages(10)
                .visibilityTimeout(2)
                .build();

        return sqsClient.receiveMessage(receiveMessageRequest).messages()
                .stream().map(message -> new MessageSummary(message.body(), message.receiptHandle()))
                .toList();
    }

    public void deleteMessagesQueueConfirmacao(MessageSummary message) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(pagamentosConcluidosQueueUrl)
                .receiptHandle(message.receipt)
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);
    }

    @Override
    public void close() {
        sqsClient.close();
    }

    public record MessageSummary(String body, String receipt) {

    }
}
