package br.com.gestaopedidos.application.dto;

import br.com.gestaopedidos.domain.model.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    private Long id;

    private Long parceiroId;

    private List<ItemPedidoDTO> itens;

    private BigDecimal valorTotal;

    private StatusPedido status;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;
}
