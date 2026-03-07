package com.intensivo.java.repository;

import com.intensivo.java.model.ClientePessoaJuridica;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientePessoaJuridicaRepository extends JpaRepository<ClientePessoaJuridica, Long> {

    boolean existsByCnpj(String cnpj);

    boolean existsByCnpjAndIdNot(String cnpj, Long id);

    List<ClientePessoaJuridica> findAllByOrderByNomeFantasiaAsc();
}
