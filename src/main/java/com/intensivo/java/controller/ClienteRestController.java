package com.intensivo.java.controller;

// Importa a entidade usada como corpo de entrada e saida da API.
import com.intensivo.java.model.Cliente;
// Importa a service que concentra as regras de negocio.
import com.intensivo.java.service.ClienteService;

// Importa a anotacao que ativa as validacoes do objeto recebido no corpo da requisicao.
import jakarta.validation.Valid;

// Lista usada para retornar varios clientes.
import java.util.List;

// Classes de HTTP usadas para status e montagem das respostas.
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

// Anotacoes que definem os endpoints REST.
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Excecao tratada para devolver erros de negocio com status apropriado.
import org.springframework.web.server.ResponseStatusException;

// Marca a classe como controller REST.
@RestController
// Define o caminho base de todos os endpoints deste controller.
@RequestMapping("/api/clientes")
// Gera construtor para injecao automatica da service.
@RequiredArgsConstructor
// Controller expoe os endpoints HTTP e delega as regras para a service.
public class ClienteRestController {

    // Service chamada por cada endpoint para executar a regra de negocio.
    private final ClienteService clienteService;

    // Mapeia requisicoes GET para /api/clientes.
    @GetMapping
    // Lista todos os clientes cadastrados.
    public ResponseEntity<?> listar() {
        try {
            // Pede a lista de clientes para a camada de service.
            List<Cliente> clientes = clienteService.listarTodos();

            // Retorna 200 OK com a lista encontrada.
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            // Em erro inesperado, devolve 500 com mensagem padrao.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao listar clientes.");
        }
    }

    // Mapeia requisicoes GET para /api/clientes/{id}.
    @GetMapping("/{id}")
    // Busca um cliente especifico pelo id informado na rota.
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            // Encaminha o id recebido para a service buscar no banco.
            Cliente cliente = clienteService.buscarPorId(id);

            // Retorna 200 OK com o cliente encontrado.
            return ResponseEntity.ok(cliente);
        } catch (Exception e) {
            // Se a service lancar uma excecao com status conhecido, reaproveita esse status na resposta.
            if (e instanceof ResponseStatusException responseStatusException) {
                return ResponseEntity.status(responseStatusException.getStatusCode())
                        .body(responseStatusException.getReason());
            }
            // Qualquer outro erro cai como falha interna do servidor.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao buscar cliente.");
        }
    }

    // Mapeia requisicoes POST para /api/clientes.
    @PostMapping
    // Recebe os dados do corpo da requisicao e cria um novo cliente.
    public ResponseEntity<?> criar(@Valid @RequestBody Cliente request) {
        try {
            // Envia o objeto validado para a service criar o cadastro.
            Cliente cliente = clienteService.criar(request);

            // Retorna 201 Created com o cliente salvo.
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } catch (ResponseStatusException e) {
            // Erros de negocio retornam o mesmo status definido na service.
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            // Erros nao previstos retornam 500.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao criar cliente.");
        }
    }

    // Mapeia requisicoes PATCH para /api/clientes/{id}.
    @PatchMapping("/{id}")
    // Atualiza os dados de um cliente ja existente.
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Cliente request) {
        try {
            // Envia o id e os novos dados para a service atualizar o cadastro.
            Cliente cliente = clienteService.atualizar(id, request);

            // Retorna 200 OK com os dados atualizados.
            return ResponseEntity.ok(cliente);
        } catch (ResponseStatusException e) {
            // Mantem o status e a mensagem definidos pela regra de negocio.
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            // Qualquer erro inesperado retorna 500.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao atualizar cliente.");
        }
    }
}
