package com.intensivo.java.dto.rest.clientes;

import com.intensivo.java.model.clientes.TipoCliente;

public record ClienteResponse(
        Long id,
        TipoCliente tipo,
        String nome,
        String documento,
        String email,
        String telefone,
        EnderecoResponse endereco) {
}
