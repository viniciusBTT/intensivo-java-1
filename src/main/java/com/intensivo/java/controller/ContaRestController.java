package com.intensivo.java.controller;

import com.intensivo.java.model.Conta;
import com.intensivo.java.model.Cliente;
import com.intensivo.java.service.ContaService;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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

    @GetMapping
    public List<Conta> listar() {
        return contaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Conta buscar(@PathVariable Long id) {
        return contaService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Conta request) {
        try {
            garantirCliente(request);
            Conta response = contaService.criar(request);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(response.getId())
                    .toUri();
            return ResponseEntity.created(location).body(response);
        } catch (NoSuchElementException exception) {
            return ResponseEntity.status(404).body(Map.of("message", exception.getMessage()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException exception) {
        return ResponseEntity.status(404).body(Map.of("message", exception.getMessage()));
    }

    private void garantirCliente(Conta conta) {
        if (conta.getCliente() == null) {
            conta.setCliente(new Cliente());
        }
    }
}
