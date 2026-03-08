package com.intensivo.java.dto.rest;

public record ClienteResponse(
        Long id,
        ClienteRestType tipo,
        String nomeExibicao,
        String documento,
        String email,
        String telefone,
        EnderecoResponse endereco,
        String nomeCompleto,
        String cpf,
        String razaoSocial,
        String nomeFantasia,
        String cnpj) {
}
