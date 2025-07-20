package br.com.gestaopedidos.infrastructure.messaging;

import br.com.gestaopedidos.common.AppConstants;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = AppConstants.KAFKA_TOPIC_NOTIFICACOES_PEDIDOS, groupId = "my-group-id")
    public void listen(String message) {
        System.out.println("Received Message in group my-group-id: " + message);
    }
}
