package com.intensivo.java.service;

import com.intensivo.java.model.Endereco;
import com.intensivo.java.dto.rest.CepResponse;

public interface CepLookupService {

    CepResponse buscarCep(String cep);

    Endereco resolverEndereco(String cep, String numero, String complemento);
}
