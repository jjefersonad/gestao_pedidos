package br.com.gestaopedidos.application.service;

import br.com.gestaopedidos.application.dto.ItemPedidoDTO;
import br.com.gestaopedidos.application.dto.PedidoMapper;
import br.com.gestaopedidos.application.dto.PedidoRequestDTO;
import br.com.gestaopedidos.common.AppConstants;
import br.com.gestaopedidos.domain.model.Parceiro;
import br.com.gestaopedidos.domain.model.StatusPedido;
import br.com.gestaopedidos.domain.repository.ParceiroRepository;
import br.com.gestaopedidos.domain.repository.PedidoRepository;
import br.com.gestaopedidos.domain.service.PedidoService;
import br.com.gestaopedidos.infrastructure.messaging.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({PedidoServiceImpl.class, PedidoMapper.class})
public class PedidoServiceIntegrationTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ParceiroRepository parceiroRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @MockitoBean
    private NotificacaoService notificacaoService;

    private Parceiro parceiro;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.sql.init.mode", () -> "never");
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @BeforeEach
    void setUp() {
        // Cria um parceiro para os testes
        parceiro = new Parceiro();
        parceiro.setNome("Parceiro Teste");
        parceiro.setLimiteCredito(new BigDecimal("1000.00"));
        parceiro = parceiroRepository.save(parceiro);
    }

    @Test
    void criarPedido_comCreditoSuficiente_deveCriarPedidoEDebitarCredito() {
        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setParceiroId(parceiro.getId());
        pedidoDTO.setItens(Arrays.asList(
                new ItemPedidoDTO("Produto A", 1, new BigDecimal("100.00")),
                new ItemPedidoDTO("Produto B", 2, new BigDecimal("50.00"))
        ));

        // Valor total esperado: 100 + (2 * 50) = 200
        
        var pedidoResponse = pedidoService.criarPedido(pedidoDTO);

        assertNotNull(pedidoResponse.getId());
        assertEquals(new BigDecimal("200.00"), pedidoResponse.getValorTotal());
        assertEquals(StatusPedido.PENDENTE, pedidoResponse.getStatus());

        Parceiro parceiroAtualizado = parceiroRepository.findById(parceiro.getId()).orElse(null);
        assertNotNull(parceiroAtualizado);
        assertEquals(new BigDecimal("800.00"), parceiroAtualizado.getLimiteCredito());
    }

    @Test
    void criarPedido_comParceiroInexistente_deveLancarExcecao() {
        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setParceiroId(0L);
        pedidoDTO.setItens(Arrays.asList(
                new ItemPedidoDTO("Produto C", 1, new BigDecimal("1500.00"))
        ));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.criarPedido(pedidoDTO);
        });

        assertEquals(AppConstants.PARCEIRO_NAO_ENCONTRADO, exception.getMessage());

        Parceiro parceiroNaoAtualizado = parceiroRepository.findById(parceiro.getId()).orElse(null);
        assertNotNull(parceiroNaoAtualizado);
        assertEquals(new BigDecimal("1000.00"), parceiroNaoAtualizado.getLimiteCredito());
    }

    @Test
    void criarPedido_comCreditoInsuficiente_deveLancarExcecao() {
        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setParceiroId(parceiro.getId());
        pedidoDTO.setItens(Arrays.asList(
                new ItemPedidoDTO("Produto C", 1, new BigDecimal("1500.00"))
        ));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.criarPedido(pedidoDTO);
        });

        assertEquals("Crédito insuficiente", exception.getMessage());

        Parceiro parceiroNaoAtualizado = parceiroRepository.findById(parceiro.getId()).orElse(null);
        assertNotNull(parceiroNaoAtualizado);
        assertEquals(new BigDecimal("1000.00"), parceiroNaoAtualizado.getLimiteCredito());
    }

    @Test
    void consultarPedidoPorId_deveRetornarPedidoExistente() {
        // Cria um pedido primeiro
        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setParceiroId(parceiro.getId());
        pedidoDTO.setItens(Arrays.asList(
                new ItemPedidoDTO("Produto X", 1, new BigDecimal("50.00"))
        ));
        var pedidoCriado = pedidoService.criarPedido(pedidoDTO);

        var pedidoEncontrado = pedidoService.consultarPedidoPorId(pedidoCriado.getId());

        assertNotNull(pedidoEncontrado);
        assertEquals(pedidoCriado.getId(), pedidoEncontrado.getId());
        assertEquals(new BigDecimal("50.00"), pedidoEncontrado.getValorTotal());
    }

    @Test
    void atualizarStatusPedido_deveAtualizarStatus() {
        // Cria um pedido primeiro
        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setParceiroId(parceiro.getId());
        pedidoDTO.setItens(Arrays.asList(
                new ItemPedidoDTO("Produto Y", 1, new BigDecimal("75.00"))
        ));
        var pedidoCriado = pedidoService.criarPedido(pedidoDTO);

        var pedidoAtualizado = pedidoService.atualizarStatusPedido(pedidoCriado.getId(), StatusPedido.APROVADO);

        assertEquals(StatusPedido.APROVADO, pedidoAtualizado.getStatus());
        
        var pedidoNoBanco = pedidoRepository.findById(pedidoCriado.getId()).orElse(null);
        assertNotNull(pedidoNoBanco);
        assertEquals(StatusPedido.APROVADO, pedidoNoBanco.getStatus());
    }

    @Test
    void cancelarPedido_deveAtualizarStatusECreditarCredito() {
        // Pega o valor do crédito inicial
        BigDecimal limiteCreditoInicial = parceiro.getLimiteCredito();

        // Cria um pedido
        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setParceiroId(parceiro.getId());
        pedidoDTO.setItens(Arrays.asList(
                new ItemPedidoDTO("Produto Z", 1, new BigDecimal("250.00"))
        ));
        var pedidoCriado = pedidoService.criarPedido(pedidoDTO);

        pedidoService.cancelarPedido(pedidoCriado.getId());

        var pedidoCancelado = pedidoRepository.findById(pedidoCriado.getId()).orElse(null);
        assertNotNull(pedidoCancelado);
        assertEquals(StatusPedido.CANCELADO, pedidoCancelado.getStatus());

        Parceiro parceiroAtualizado = parceiroRepository.findById(parceiro.getId()).orElse(null);
        assertNotNull(parceiroAtualizado);
        // Exemplo de cálculo se Crédito inicial igual a 1000 então 1000 - 250 (pedido) + 250 (cancelamento) = 1000
        assertEquals(limiteCreditoInicial, parceiroAtualizado.getLimiteCredito());
    }
}