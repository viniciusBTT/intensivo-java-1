package com.intensivo.java.controller;

import com.intensivo.java.controller.support.RestPayloadMapper;
import com.intensivo.java.dto.rest.ClienteCreateRequest;
import com.intensivo.java.dto.rest.ClienteResponse;
import com.intensivo.java.model.Cliente;
import com.intensivo.java.service.ClienteService;
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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteRestController {

    private final ClienteService clienteService;
    private final RestPayloadMapper restPayloadMapper;

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listarTodos().stream()
                .map(restPayloadMapper::toClienteResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id) {
        return restPayloadMapper.toClienteResponse(clienteService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteCreateRequest request) {
        Cliente cliente = switch (request.getTipo()) {
            case PF -> clienteService.criarPessoaFisica(restPayloadMapper.toPessoaFisicaForm(request));
            case PJ -> clienteService.criarPessoaJuridica(restPayloadMapper.toPessoaJuridicaForm(request));
        };

        ClienteResponse response = restPayloadMapper.toClienteResponse(cliente);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
