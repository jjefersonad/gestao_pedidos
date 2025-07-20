package br.com.gestaopedidos.domain.repository;

import br.com.gestaopedidos.domain.model.Parceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParceiroRepository extends JpaRepository<Parceiro, Long> {
}
