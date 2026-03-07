package com.intensivo.java.restController;

import com.intensivo.java.dto.rest.CepResponse;
import com.intensivo.java.service.CepLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ceps")
@RequiredArgsConstructor
public class CepRestController {

    private final CepLookupService cepLookupService;

    @GetMapping("/{cep}")
    public CepResponse buscar(@PathVariable String cep) {
        return cepLookupService.buscarCep(cep);
    }
}
