package com.intensivo.java.integration;

import com.intensivo.java.dto.rest.CepResponse;
import com.intensivo.java.exception.CepLookupException;
import com.intensivo.java.service.support.TextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViaCepClient {

    private final RestClient viaCepRestClient;

    public CepResponse buscar(String cep) {
        String cepNormalizado = TextUtils.somenteDigitos(cep);
        if (cepNormalizado.length() != 8) {
            throw new CepLookupException("CEP invalido. Informe 8 digitos.");
        }

        try {
            ViaCepPayload payload = viaCepRestClient.get()
                    .uri("/{cep}/json/", cepNormalizado)
                    .retrieve()
                    .body(ViaCepPayload.class);

            if (payload == null || Boolean.TRUE.equals(payload.erro())) {
                throw new CepLookupException("Nao foi possivel localizar o CEP informado.");
            }

            return new CepResponse(
                    cepNormalizado,
                    payload.logradouro(),
                    payload.bairro(),
                    payload.localidade(),
                    payload.uf(),
                    payload.complemento());
        } catch (RestClientException exception) {
            log.warn("Falha ao consultar ViaCEP para o CEP {}: {}", cepNormalizado, exception.getMessage());
            log.debug("Detalhes da falha na consulta ao ViaCEP", exception);
            throw new CepLookupException("Falha ao consultar o servico de CEP.");
        }
    }

    public record ViaCepPayload(
            String cep,
            String logradouro,
            String complemento,
            String bairro,
            String localidade,
            String uf,
            Boolean erro) {
    }
}
