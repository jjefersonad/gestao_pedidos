package br.com.gestaopedidos.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    private Long parceiroId;

    private List<ItemPedidoDTO> itens;
}
