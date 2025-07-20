package br.com.gestaopedidos.infrastructure.config;

import br.com.gestaopedidos.common.AppConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic notificacaoTopic() {
        return TopicBuilder.name(AppConstants.KAFKA_TOPIC_NOTIFICACOES_PEDIDOS)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
