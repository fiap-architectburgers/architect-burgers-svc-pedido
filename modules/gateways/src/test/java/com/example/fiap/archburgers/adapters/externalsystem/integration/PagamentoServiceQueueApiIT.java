package com.example.fiap.archburgers.adapters.externalsystem.integration;//import static org.junit.jupiter.api.Assertions.*;

import com.example.fiap.archburgers.adapters.externalsystem.PagamentoServiceQueueApi;
import com.example.fiap.archburgers.testUtils.StaticEnvironment;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.aot.hint.TypeReference.listOf;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

class PagamentoServiceQueueApiIT {
    private PagamentoServiceQueueApi queueApi;

    private static LocalStackContainer localstack;

    @BeforeAll
    static void beforeAll() {
        DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:3.5.0");

        localstack = new LocalStackContainer(localstackImage).withServices(SQS);
        localstack.start();
    }

    @AfterAll
    static void afterAll() {
        localstack.stop();
    }

    @BeforeEach
    void setUp() {
        System.setProperty("aws.accessKeyId", localstack.getAccessKey());
        System.setProperty("aws.secretAccessKey", localstack.getSecretKey());

        queueApi = new PagamentoServiceQueueApi(new StaticEnvironment(Map.of(
                "archburgers.integration.sqs.sqsEndpoint", localstack.getEndpoint().toString(),
                "archburgers.integration.sqs.pedidosQueueName", "pedidos",
                "archburgers.integration.sqs.pedidosQueueUrl", localstack.getEndpoint() + "/000000000000/pedidos",
                "archburgers.integration.sqs.pagamentosConcluidosQueueName", "pagamentos_concluidos",
                "archburgers.integration.sqs.pagamentosConcluidosQueueUrl", localstack.getEndpoint() + "/000000000000/pagamentos_concluidos"
        )));
    }

    @AfterEach
    void tearDown() {
        queueApi.close();
    }

    @Test
    void sendMessageQueuePagamento() throws Exception {
        String testMessage = "Teste_" + System.currentTimeMillis();

        queueApi.sendMessageQueuePagamento(testMessage);

        try (SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(localstack.getEndpoint())
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
                        )
                )
                .build()) {

            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(localstack.getEndpoint() + "/000000000000/pedidos")
                    .maxNumberOfMessages(10)
                    .build();

            Thread.sleep(100L);
            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

            assertThat(messages).hasSize(1);
            Message message = messages.getFirst();
            assertThat(message.body()).isEqualTo(testMessage);
        }
    }

    @Test
    public void receiveMessageQueueConfirmacao() throws InterruptedException {
        String testMessage1 = "Teste_1_" + System.currentTimeMillis();
        String testMessage2 = "Teste_2_" + System.currentTimeMillis();

        try (SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(localstack.getEndpoint())
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
                        )
                )
                .build()) {

            for (String testMessage : List.of(testMessage1, testMessage2)) {
                SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                        .queueUrl(localstack.getEndpoint() + "/000000000000/pagamentos_concluidos")
                        .messageBody(testMessage)
                        .build();

                sqsClient.sendMessage(sendMessageRequest);
            }
        }

        List<PagamentoServiceQueueApi.MessageSummary> messages = queueApi.receiveMessagesQueueConfirmacao();

        assertThat(messages.stream().map(PagamentoServiceQueueApi.MessageSummary::body))
                .containsExactlyInAnyOrder(testMessage1, testMessage2);

        for (PagamentoServiceQueueApi.MessageSummary message : messages) {
            queueApi.deleteMessagesQueueConfirmacao(message);
        }

        // Waiting past the Visibility Timeout
        Thread.sleep(3000);

        messages = queueApi.receiveMessagesQueueConfirmacao();

        assertThat(messages).isEmpty();
    }


}