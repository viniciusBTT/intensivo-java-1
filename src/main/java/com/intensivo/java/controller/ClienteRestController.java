package com.intensivo.java.controller;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.service.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteRestController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<Cliente> clientes = clienteService.listarTodos();
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao listar clientes.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.buscarPorId(id);
            return ResponseEntity.ok(cliente);
        } catch (Exception e) {
            if (e instanceof ResponseStatusException responseStatusException) {
                return ResponseEntity.status(responseStatusException.getStatusCode())
                        .body(responseStatusException.getReason());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao buscar cliente.");
        }
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody Cliente request) {
        try {
            Cliente cliente = clienteService.criar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao criar cliente.");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Cliente request) {
        try {
            Cliente cliente = clienteService.atualizar(id, request);
            return ResponseEntity.ok(cliente);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao atualizar cliente.");
        }
    }
}
