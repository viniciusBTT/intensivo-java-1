package com.intensivo.java.dto.rest;

public record EnderecoResponse(
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf) {
}
