package br.com.gestaopedidos.application.dto;

import br.com.gestaopedidos.domain.model.ItemPedido;
import br.com.gestaopedidos.domain.model.Pedido;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PedidoMapper {

    public Pedido toEntity(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        // O parceiro será associado no serviço
        return pedido;
    }

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setParceiroId(pedido.getParceiro().getId());
        dto.setItens(pedido.getItens().stream().map(this::toDTO).collect(Collectors.toList()));
        dto.setValorTotal(pedido.getValorTotal());
        dto.setStatus(pedido.getStatus());
        dto.setDataCriacao(pedido.getDataCriacao());
        dto.setDataAtualizacao(pedido.getDataAtualizacao());
        return dto;
    }

    public ItemPedido toEntity(ItemPedidoDTO dto) {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setProduto(dto.getProduto());
        itemPedido.setQuantidade(dto.getQuantidade());
        itemPedido.setPrecoUnitario(dto.getPrecoUnitario());
        return itemPedido;
    }

    public ItemPedidoDTO toDTO(ItemPedido itemPedido) {
        ItemPedidoDTO dto = new ItemPedidoDTO();
        dto.setProduto(itemPedido.getProduto());
        dto.setQuantidade(itemPedido.getQuantidade());
        dto.setPrecoUnitario(itemPedido.getPrecoUnitario());
        return dto;
    }
}
