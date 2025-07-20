package br.com.gestaopedidos.infrastructure.messaging;

import br.com.gestaopedidos.common.AppConstants;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
public class NotificacaoServiceIntegrationTest {

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps).createConsumer();
        consumer.subscribe(Collections.singleton(AppConstants.KAFKA_TOPIC_NOTIFICACOES_PEDIDOS));
        consumer.poll(Duration.ZERO); // Poll once to ensure assignment
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void notificarMudancaStatus_deveEnviarMensagemParaKafka() {
        Long pedidoId = 123L;
        String novoStatus = "APROVADO";

        notificacaoService.notificarMudancaStatus(pedidoId, novoStatus);

        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer);

        assertNotNull(records);
        assertFalse(records.isEmpty());
        assertTrue(StreamSupport.stream(records.records(AppConstants.KAFKA_TOPIC_NOTIFICACOES_PEDIDOS).spliterator(), false)
                .anyMatch(record -> record.value().contains("\"pedidoId\"") && record.value().contains("123") && record.value().contains("\"novoStatus\"") && record.value().contains("\"APROVADO\"")));
    }
}
