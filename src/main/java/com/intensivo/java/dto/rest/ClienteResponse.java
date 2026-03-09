package com.intensivo.java.dto.rest;

import com.intensivo.java.model.TipoCliente;

public record ClienteResponse(
        Long id,
        TipoCliente tipo,
        String nome,
        String documento,
        String email,
        String telefone,
        EnderecoResponse endereco) {
}
