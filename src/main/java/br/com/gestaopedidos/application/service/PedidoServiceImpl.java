package br.com.gestaopedidos.application.service;

import br.com.gestaopedidos.common.AppConstants;
import br.com.gestaopedidos.application.dto.PedidoMapper;
import br.com.gestaopedidos.application.dto.PedidoRequestDTO;
import br.com.gestaopedidos.application.dto.PedidoResponseDTO;
import br.com.gestaopedidos.domain.model.ItemPedido;
import br.com.gestaopedidos.domain.model.Parceiro;
import br.com.gestaopedidos.domain.model.Pedido;
import br.com.gestaopedidos.domain.model.StatusPedido;
import br.com.gestaopedidos.domain.repository.ParceiroRepository;
import br.com.gestaopedidos.domain.repository.PedidoRepository;
import br.com.gestaopedidos.domain.service.PedidoService;
import br.com.gestaopedidos.infrastructure.messaging.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ParceiroRepository parceiroRepository;

    @Autowired
    private PedidoMapper pedidoMapper;

    @Autowired
    private NotificacaoService notificacaoService;

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoDTO) {
        Parceiro parceiro = parceiroRepository.findById(pedidoDTO.getParceiroId())
                .orElseThrow(() -> new RuntimeException(AppConstants.PARCEIRO_NAO_ENCONTRADO));

        Pedido pedido = new Pedido();
        pedido.setParceiro(parceiro);

        List<ItemPedido> itens = pedidoDTO.getItens().stream()
                .map(itemDto -> {
                    ItemPedido item = pedidoMapper.toEntity(itemDto);
                    item.setPedido(pedido); // Associar item ao pedido
                    return item;
                })
                .collect(Collectors.toList());
        pedido.setItens(itens);

        BigDecimal valorTotal = itens.stream()
                .map(item -> item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setValorTotal(valorTotal);

        // Valida se o perceiro possui limite de crédito suficiente
        if (parceiro.getLimiteCredito().compareTo(valorTotal) < 0) {
            throw new RuntimeException(AppConstants.CREDITO_INSUFICIENTE);
        }

        parceiro.setLimiteCredito(parceiro.getLimiteCredito().subtract(valorTotal));
        parceiroRepository.save(parceiro);

        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setDataAtualizacao(LocalDateTime.now());

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        notificacaoService.notificarMudancaStatus(pedidoSalvo.getId(), pedidoSalvo.getStatus().toString());
        return pedidoMapper.toResponseDTO(pedidoSalvo);
    }

    public PedidoResponseDTO consultarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.PEDIDO_NAO_ENCONTRADO));
        return pedidoMapper.toResponseDTO(pedido);
    }

    public Page<PedidoResponseDTO> consultarTodosPedidos(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(pedidoMapper::toResponseDTO);
    }

    public Page<PedidoResponseDTO> consultarPedidosPorPeriodo(LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        return pedidoRepository.findByDataCriacaoBetween(inicio, fim, pageable).map(pedidoMapper::toResponseDTO);
    }

    public Page<PedidoResponseDTO> consultarPedidosPorStatus(StatusPedido status, Pageable pageable) {
        return pedidoRepository.findByStatus(status, pageable).map(pedidoMapper::toResponseDTO);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.PEDIDO_NAO_ENCONTRADO));
        pedido.setStatus(status);
        pedido.setDataAtualizacao(LocalDateTime.now());
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        notificacaoService.notificarMudancaStatus(pedidoAtualizado.getId(), pedidoAtualizado.getStatus().toString());
        return pedidoMapper.toResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.PEDIDO_NAO_ENCONTRADO));

        if (pedido.getStatus() != StatusPedido.CANCELADO) {
            Parceiro parceiro = pedido.getParceiro();
            parceiro.setLimiteCredito(parceiro.getLimiteCredito().add(pedido.getValorTotal()));
            parceiroRepository.save(parceiro);
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setDataAtualizacao(LocalDateTime.now());
        pedidoRepository.save(pedido);
        notificacaoService.notificarMudancaStatus(id, AppConstants.NOTIFICACAO_MUDANCA_STATUS.formatted(id, StatusPedido.CANCELADO.toString()));
    }
}
