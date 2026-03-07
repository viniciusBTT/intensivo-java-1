package com.intensivo.java.repository;

import com.intensivo.java.model.Conta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    @EntityGraph(attributePaths = "cliente")
    List<Conta> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = "cliente")
    Optional<Conta> findWithClienteById(Long id);

    boolean existsByNumero(String numero);

    boolean existsByClienteId(Long clienteId);
}
