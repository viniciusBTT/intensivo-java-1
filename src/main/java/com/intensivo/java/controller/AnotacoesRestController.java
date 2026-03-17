package com.intensivo.java.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exemplos")
public class AnotacoesRestController {

    @GetMapping("/request-param")
    public ResponseEntity<Map<String, String>> explicarRequestParam(@RequestParam String nome) {
        try {
            Map<String, String> response = Map.of(
                    "conceito", "@RequestParam",
                    "valorRecebido", nome,
                    "origem", "O valor veio da query string da requisicao.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao processar o exemplo de RequestParam."));
        }
    }

    @GetMapping("/path-variable/{nome}")
    public ResponseEntity<Map<String, String>> explicarPathVariable(@PathVariable String nome) {
        try {
            Map<String, String> response = Map.of(
                    "conceito", "@PathVariable",
                    "valorRecebido", nome,
                    "mensagem", "O valor veio da URL e foi recebido no parametro do metodo.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao processar o exemplo de PathVariable."));
        }
    }

    @PostMapping("/request-body")
    public ResponseEntity<Map<String, String>> explicarRequestBody(@RequestBody Map<String, String> request) {
        try {
            Map<String, String> response = new HashMap<>();
            response.put("conceito", "@RequestBody");
            response.put("nomeRecebido", request.get("nome"));
            response.put("mensagemRecebida", request.get("mensagem"));
            response.put("origem", "Os dados foram enviados no corpo da requisicao em JSON.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao processar o exemplo de RequestBody."));
        }
    }
}
