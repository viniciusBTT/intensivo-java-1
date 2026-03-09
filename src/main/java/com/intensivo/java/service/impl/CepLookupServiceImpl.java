package com.intensivo.java.service.impl;

import com.intensivo.java.dto.rest.CepResponse;
import com.intensivo.java.integration.ViaCepClient;
import com.intensivo.java.model.clientes.Endereco;
import com.intensivo.java.service.CepLookupService;
import com.intensivo.java.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CepLookupServiceImpl implements CepLookupService {

    private final ViaCepClient viaCepClient;

    @Override
    public CepResponse buscarCep(String cep) {
        return viaCepClient.buscar(cep);
    }

    @Override
    public Endereco resolverEndereco(String cep, String numero, String complemento) {
        CepResponse response = buscarCep(cep);
        return Endereco.builder()
                .cep(response.cep())
                .logradouro(TextUtils.textoNormalizado(response.logradouro()))
                .numero(TextUtils.textoNormalizado(numero))
                .complemento(TextUtils.textoNormalizado(complemento))
                .bairro(TextUtils.textoNormalizado(response.bairro()))
                .cidade(TextUtils.textoNormalizado(response.cidade()))
                .uf(TextUtils.textoNormalizado(response.uf()).toUpperCase())
                .build();
    }
}
