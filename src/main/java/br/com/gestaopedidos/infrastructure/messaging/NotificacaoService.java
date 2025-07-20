package br.com.gestaopedidos.infrastructure.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public void notificarMudancaStatus(Long pedidoId, String novoStatus) {
        String message = String.format("{\"pedidoId\": %d, \"novoStatus\": \"%s\"}", pedidoId, novoStatus);
        kafkaProducerService.sendMessage(message);
    }
}
