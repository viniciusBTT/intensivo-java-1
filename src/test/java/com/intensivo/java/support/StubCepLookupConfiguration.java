package com.intensivo.java.support;

import com.intensivo.java.model.Endereco;
import com.intensivo.java.dto.rest.CepResponse;
import com.intensivo.java.service.CepLookupService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration(proxyBeanMethods = false)
public class StubCepLookupConfiguration {

    @Bean
    @Primary
    CepLookupService cepLookupService() {
        return new CepLookupService() {
            @Override
            public CepResponse buscarCep(String cep) {
                return new CepResponse("01001000", "Praca da Se", "Se", "Sao Paulo", "SP", "");
            }

            @Override
            public Endereco resolverEndereco(String cep, String numero, String complemento) {
                return Endereco.builder()
                        .cep("01001000")
                        .logradouro("Praca da Se")
                        .bairro("Se")
                        .cidade("Sao Paulo")
                        .uf("SP")
                        .numero(numero)
                        .complemento(complemento)
                        .build();
            }
        };
    }
}
