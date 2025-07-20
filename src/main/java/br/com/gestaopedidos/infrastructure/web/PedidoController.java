package br.com.gestaopedidos.infrastructure.web;

import br.com.gestaopedidos.application.dto.PedidoRequestDTO;
import br.com.gestaopedidos.application.dto.PedidoResponseDTO;
import br.com.gestaopedidos.common.AppConstants;
import br.com.gestaopedidos.domain.model.StatusPedido;
import br.com.gestaopedidos.domain.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.web.PageableDefault;

import java.time.LocalDateTime;

@RestController
@Tag(name = AppConstants.SWAGGER_TAG_PEDIDOS_NAME, description = AppConstants.SWAGGER_TAG_PEDIDOS_DESCRIPTION)
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @Operation(summary = AppConstants.SWAGGER_SUMMARY_CADASTRO_PEDIDOS)
    @ApiResponse(responseCode = "200", description = AppConstants.SWAGGER_RESPONSE_PEDIDO_CADASTRADO)
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody PedidoRequestDTO pedidoDTO) {
        return ResponseEntity.ok(pedidoService.criarPedido(pedidoDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = AppConstants.SWAGGER_SUMMARY_DETALHE_PEDIDO)
    @ApiResponse(responseCode = "200", description = AppConstants.SWAGGER_RESPONSE_DETALHE_PEDIDO)
    public ResponseEntity<PedidoResponseDTO> consultarPedidoPorId(
            @Parameter(description = AppConstants.SWAGGER_PARAM_PEDIDO_ID_DESCRIPTION) @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.consultarPedidoPorId(id));
    }

    @GetMapping
    @Operation(summary = AppConstants.SWAGGER_SUMMARY_LISTA_PEDIDOS)
    @ApiResponse(responseCode = "200", description = AppConstants.SWAGGER_RESPONSE_LISTA_PEDIDOS)
    public ResponseEntity<Page<PedidoResponseDTO>> consultarPedidos(
            @Parameter(description = AppConstants.SWAGGER_PARAM_PERIODO_INICIO_DESCRIPTION) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = AppConstants.SWAGGER_PARAM_PERIODO_FIM_DESCRIPTION) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @Parameter(description = AppConstants.SWAGGER_PARAM_STATUS_PEDIDO_DESCRIPTION) @RequestParam(required = false) StatusPedido status,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        if (inicio != null && fim != null) {
            return ResponseEntity.ok(pedidoService.consultarPedidosPorPeriodo(inicio, fim, pageable));
        } else if (status != null) {
            return ResponseEntity.ok(pedidoService.consultarPedidosPorStatus(status, pageable));
        } else {
            return ResponseEntity.ok(pedidoService.consultarTodosPedidos(pageable));
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = AppConstants.SWAGGER_SUMMARY_ATUALIZA_STATUS_PEDIDO)
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(
            @Parameter(description = AppConstants.SWAGGER_PARAM_PEDIDO_ID_DESCRIPTION) @PathVariable Long id,
            @Parameter(description = AppConstants.SWAGGER_PARAM_STATUS_PEDIDO_DESCRIPTION) @RequestBody StatusPedido status) {
        return ResponseEntity.ok(pedidoService.atualizarStatusPedido(id, status));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = AppConstants.SWAGGER_SUMMARY_CANCELAR_PEDIDO)
    public ResponseEntity<Void> cancelarPedido(
            @Parameter(description = AppConstants.SWAGGER_PARAM_PEDIDO_ID_DESCRIPTION) @PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.ok().build();
    }
}

