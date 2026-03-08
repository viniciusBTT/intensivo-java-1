package com.intensivo.java.controller;

import com.intensivo.java.controller.support.RestPayloadMapper;
import com.intensivo.java.dto.rest.ContaCreateRequest;
import com.intensivo.java.dto.rest.ContaResponse;
import com.intensivo.java.model.Conta;
import com.intensivo.java.service.ContaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
public class ContaRestController {

    private final ContaService contaService;
    private final RestPayloadMapper restPayloadMapper;

    @GetMapping
    public List<ContaResponse> listar() {
        return contaService.listarTodas().stream()
                .map(restPayloadMapper::toContaResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ContaResponse buscar(@PathVariable Long id) {
        return restPayloadMapper.toContaResponse(contaService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ContaResponse> criar(@Valid @RequestBody ContaCreateRequest request) {
        Conta conta = switch (request.getTipo()) {
            case CORRENTE -> contaService.criarContaCorrente(restPayloadMapper.toContaCorrenteForm(request));
            case JURIDICA -> contaService.criarContaJuridica(restPayloadMapper.toContaJuridicaForm(request));
        };

        ContaResponse response = restPayloadMapper.toContaResponse(conta);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
