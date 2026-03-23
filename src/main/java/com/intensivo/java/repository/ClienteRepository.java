package com.intensivo.java.repository;

// Importa a entidade Cliente, que sera manipulada por este repository.
import com.intensivo.java.model.Cliente;

// Lista usada no retorno da consulta de varios clientes.
import java.util.List;

// Interface base do Spring Data JPA com operacoes prontas de banco.
import org.springframework.data.jpa.repository.JpaRepository;

// Repository responsavel pelo acesso aos dados da entidade Cliente.
// Ao herdar JpaRepository, ja ganha metodos como save, findById, findAll e deleteById.
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca todos os clientes ordenando do maior id para o menor id.
    List<Cliente> findAllByOrderByIdDesc();

    // Verifica se ja existe cliente cadastrado com o documento informado.
    boolean existsByDocumento(String documento);
}
