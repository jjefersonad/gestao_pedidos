package br.com.gestaopedidos.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {

    private String produto;

    private Integer quantidade;

    private BigDecimal precoUnitario;
}
