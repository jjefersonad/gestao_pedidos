package br.com.gestaopedidos.domain.service;

import br.com.gestaopedidos.application.dto.PedidoRequestDTO;
import br.com.gestaopedidos.application.dto.PedidoResponseDTO;
import br.com.gestaopedidos.domain.model.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PedidoService {
    PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoDTO);
    PedidoResponseDTO consultarPedidoPorId(Long id);
    Page<PedidoResponseDTO> consultarTodosPedidos(Pageable pageable);
    Page<PedidoResponseDTO> consultarPedidosPorPeriodo(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
    Page<PedidoResponseDTO> consultarPedidosPorStatus(StatusPedido status, Pageable pageable);
    PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido status);
    void cancelarPedido(Long id);
}
