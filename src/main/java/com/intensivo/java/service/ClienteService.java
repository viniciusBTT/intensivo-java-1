package com.intensivo.java.service;

// Importa a entidade que sera tratada pela regra de negocio.
import com.intensivo.java.model.Cliente;
// Importa o repository responsavel pelo acesso aos dados.
import com.intensivo.java.repository.ClienteRepository;

// Import utilitario de lista usado nos retornos.
import java.util.List;
// Import utilitario para converter texto em maiusculo ou minusculo com seguranca.
import java.util.Locale;
// Import utilitario para comparar valores com seguranca contra null.
import java.util.Objects;

// Lombok gera construtor com os atributos finais da classe.
import lombok.RequiredArgsConstructor;

// Usado para definir codigos HTTP nas excecoes.
import org.springframework.http.HttpStatus;
// Marca a classe como componente de regra de negocio do Spring.
import org.springframework.stereotype.Service;
// Controla o escopo transacional das operacoes no banco.
import org.springframework.transaction.annotation.Transactional;
// Excecao usada para devolver erros HTTP de forma simples.
import org.springframework.web.server.ResponseStatusException;

// Registra a classe como service no contexto do Spring.
@Service
// Gera um construtor com o repository para injecao de dependencia.
@RequiredArgsConstructor
// Service concentra as regras de negocio do cliente antes de acessar o repository.
public class ClienteService {

    // Repository usado para consultar e salvar clientes no banco.
    private final ClienteRepository clienteRepository;

    // Operacao somente de leitura, sem intencao de alterar dados.
    @Transactional(readOnly = true)
    // Retorna todos os clientes ja ordenados do mais recente para o mais antigo.
    public List<Cliente> listarTodos() {
        return clienteRepository.findAllByOrderByIdDesc();
    }

    // Operacao somente de leitura para consulta de um unico cliente.
    @Transactional(readOnly = true)
    // Busca um cliente pelo id e devolve 404 quando ele nao existe.
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado."));
    }

    // Abre transacao para incluir um novo registro.
    @Transactional
    // Prepara os dados antes de gravar um novo cliente.
    public Cliente criar(Cliente cliente) {
        // Garante que o banco crie um novo id e nao reutilize um valor enviado na requisicao.
        cliente.setId(null);

        // Padroniza os campos antes de validar e salvar.
        normalizarCampos(cliente);

        // Verifica se o documento ja pertence a outro cadastro.
        validarDocumentoDisponivel(cliente.getDocumento());

        // Persiste o novo cliente e devolve o objeto salvo.
        return clienteRepository.save(cliente);
    }

    // Abre transacao para atualizar um registro existente.
    @Transactional
    // Atualiza apenas depois de localizar o registro atual e validar conflitos de documento.
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        // Busca o cliente que ja existe no banco.
        Cliente clienteExistente = buscarPorId(id);

        // Padroniza os dados recebidos na requisicao antes da atualizacao.
        normalizarCampos(clienteAtualizado);

        // So valida duplicidade quando o documento realmente foi alterado.
        if (!Objects.equals(clienteAtualizado.getDocumento(), clienteExistente.getDocumento())) {
            validarDocumentoDisponivel(clienteAtualizado.getDocumento());
        }

        // Copia os novos valores para a entidade que veio do banco.
        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setDocumento(clienteAtualizado.getDocumento());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        clienteExistente.setCep(clienteAtualizado.getCep());
        clienteExistente.setLogradouro(clienteAtualizado.getLogradouro());
        clienteExistente.setNumero(clienteAtualizado.getNumero());
        clienteExistente.setComplemento(clienteAtualizado.getComplemento());
        clienteExistente.setBairro(clienteAtualizado.getBairro());
        clienteExistente.setCidade(clienteAtualizado.getCidade());
        clienteExistente.setUf(clienteAtualizado.getUf());

        // Salva a entidade atualizada no banco.
        return clienteRepository.save(clienteExistente);
    }

    // Impede que dois clientes sejam salvos com o mesmo documento.
    private void validarDocumentoDisponivel(String documento) {
        // Consulta se ja existe algum cliente com o mesmo documento.
        boolean documentoEmUso = clienteRepository.existsByDocumento(documento);

        // Se existir, interrompe o fluxo com erro 400.
        if (documentoEmUso) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existe pessoa cadastrada com esse cpf.");
        }
    }

    // Centraliza ajustes simples dos dados recebidos pela API.
    private void normalizarCampos(Cliente cliente) {
        // Remove espacos desnecessarios dos campos principais.
        cliente.setNome(normalizar(cliente.getNome()));
        cliente.setDocumento(normalizar(cliente.getDocumento()));

        // Normaliza o email e padroniza para letras minusculas.
        String email = normalizar(cliente.getEmail());
        cliente.setEmail(email == null ? null : email.toLowerCase(Locale.ROOT));

        // Normaliza o CEP e remove qualquer caractere que nao seja numero.
        String cep = normalizar(cliente.getCep());
        cliente.setCep(cep == null ? null : cep.replaceAll("\\D", ""));

        // Normaliza os demais campos de endereco e contato.
        cliente.setTelefone(normalizar(cliente.getTelefone()));
        cliente.setLogradouro(normalizar(cliente.getLogradouro()));
        cliente.setNumero(normalizar(cliente.getNumero()));
        cliente.setComplemento(normalizar(cliente.getComplemento()));
        cliente.setBairro(normalizar(cliente.getBairro()));
        cliente.setCidade(normalizar(cliente.getCidade()));

        // Padroniza a UF em letras maiusculas.
        String uf = normalizar(cliente.getUf());
        cliente.setUf(uf == null ? null : uf.toUpperCase(Locale.ROOT));
    }

    // Remove espacos extras e transforma texto vazio em null.
    private String normalizar(String valor) {
        // Se nada foi informado, mantem null.
        if (valor == null) {
            return null;
        }

        // Remove espacos antes e depois do texto.
        String valorNormalizado = valor.trim();

        // Se sobrar texto vazio, transforma em null.
        return valorNormalizado.isEmpty() ? null : valorNormalizado;
    }
}
