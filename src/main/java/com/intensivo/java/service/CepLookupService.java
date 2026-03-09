package com.intensivo.java.service;

import com.intensivo.java.dto.rest.CepResponse;
import com.intensivo.java.model.clientes.Endereco;

public interface CepLookupService {

    CepResponse buscarCep(String cep);

    Endereco resolverEndereco(String cep, String numero, String complemento);
}
