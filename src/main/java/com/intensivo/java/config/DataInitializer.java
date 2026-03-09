package com.intensivo.java.config;

import com.intensivo.java.model.Role;
import com.intensivo.java.model.Usuario;
import com.intensivo.java.model.clientes.Cliente;
import com.intensivo.java.model.clientes.Endereco;
import com.intensivo.java.model.clientes.TipoCliente;
import com.intensivo.java.model.contas.ContaCorrente;
import com.intensivo.java.model.contas.ContaJuridica;
import com.intensivo.java.model.contas.ContaStatus;
import com.intensivo.java.repository.clientes.ClienteRepository;
import com.intensivo.java.repository.contas.ContaRepository;
import com.intensivo.java.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;
    private final DefaultUsersProperties defaultUsersProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        criarUsuariosPadrao();
        criarDadosExemplo();
    }

    private void criarUsuariosPadrao() {
        criarUsuarioSeNecessario(
                defaultUsersProperties.defaultAdminUsername(),
                defaultUsersProperties.defaultAdminPassword(),
                Set.of(Role.ADMIN, Role.ATENDENTE));
        criarUsuarioSeNecessario(
                defaultUsersProperties.defaultAttendantUsername(),
                defaultUsersProperties.defaultAttendantPassword(),
                Set.of(Role.ATENDENTE));
    }

    private void criarDadosExemplo() {
        if (clienteRepository.count() > 0) {
            return;
        }

        Cliente clientePf = new Cliente();
        clientePf.atualizarCadastro(
                TipoCliente.PF,
                "Joao da Silva",
                "12345678901",
                "joao@example.com",
                "11999998888",
                endereco("01001000", "Praca da Se", "100", "Se", "Sao Paulo", "SP", "Casa"));
        clientePf = clienteRepository.save(clientePf);

        Cliente clientePj = new Cliente();
        clientePj.atualizarCadastro(
                TipoCliente.PJ,
                "XPTO LTDA",
                "12345678000199",
                "contato@xpto.com",
                "1133334444",
                endereco("01310930", "Avenida Paulista", "900", "Bela Vista", "Sao Paulo", "SP", "Sala 12"));
        clientePj = clienteRepository.save(clientePj);

        if (contaRepository.count() == 0) {
            ContaCorrente corrente = new ContaCorrente();
            corrente.abrirConta(
                    "0001",
                    "00000001",
                    clientePf,
                    new BigDecimal("1500.00"),
                    ContaStatus.ATIVA,
                    new BigDecimal("500.00"));
            contaRepository.save(corrente);

            ContaJuridica juridica = new ContaJuridica();
            juridica.abrirConta(
                    "0001",
                    "00000002",
                    clientePj,
                    new BigDecimal("5000.00"),
                    ContaStatus.ATIVA,
                    new BigDecimal("89.90"),
                    "Ana Financeira");
            contaRepository.save(juridica);
        }

        log.info("Dados iniciais de clientes e contas criados");
    }

    private void criarUsuarioSeNecessario(String username, String password, Set<Role> roles) {
        if (usuarioRepository.existsByUsername(username)) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setAtivo(true);
        usuario.setRoles(roles);
        usuarioRepository.save(usuario);
        log.info("Usuario padrao {} criado", username);
    }

    private Endereco endereco(String cep, String logradouro, String numero, String bairro, String cidade, String uf,
            String complemento) {
        return Endereco.builder()
                .cep(cep)
                .logradouro(logradouro)
                .numero(numero)
                .complemento(complemento)
                .bairro(bairro)
                .cidade(cidade)
                .uf(uf)
                .build();
    }
}
