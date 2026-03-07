package com.intensivo.java.dto.rest;

public record CepResponse(
        String cep,
        String logradouro,
        String bairro,
        String cidade,
        String uf,
        String complemento) {
}
