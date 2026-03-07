package com.intensivo.java.repository;

import com.intensivo.java.model.ClientePessoaFisica;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientePessoaFisicaRepository extends JpaRepository<ClientePessoaFisica, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    List<ClientePessoaFisica> findAllByOrderByNomeCompletoAsc();
}
