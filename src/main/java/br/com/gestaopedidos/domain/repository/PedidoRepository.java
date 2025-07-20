package br.com.gestaopedidos.domain.repository;

import br.com.gestaopedidos.domain.model.Pedido;
import br.com.gestaopedidos.domain.model.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Page<Pedido> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    Page<Pedido> findByStatus(StatusPedido status, Pageable pageable);
}
