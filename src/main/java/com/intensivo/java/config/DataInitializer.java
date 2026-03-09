package com.intensivo.java.config;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.model.ContaCorrente;
import com.intensivo.java.model.ContaJuridica;
import com.intensivo.java.model.ContaStatus;
import com.intensivo.java.model.Endereco;
import com.intensivo.java.model.Role;
import com.intensivo.java.model.TipoCliente;
import com.intensivo.java.model.Usuario;
import com.intensivo.java.repository.ClienteRepository;
import com.intensivo.java.repository.ContaRepository;
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
        clientePf.setNome("Joao da Silva");
        clientePf.setTipoCliente(TipoCliente.PF);
        clientePf.setDocumento("12345678901");
        clientePf.setEmail("joao@example.com");
        clientePf.setTelefone("11999998888");
        clientePf.setEndereco(endereco("01001000", "Praca da Se", "100", "Se", "Sao Paulo", "SP", "Casa"));
        clientePf = clienteRepository.save(clientePf);

        Cliente clientePj = new Cliente();
        clientePj.setNome("XPTO LTDA");
        clientePj.setTipoCliente(TipoCliente.PJ);
        clientePj.setDocumento("12345678000199");
        clientePj.setEmail("contato@xpto.com");
        clientePj.setTelefone("1133334444");
        clientePj.setEndereco(endereco("01310930", "Avenida Paulista", "900", "Bela Vista", "Sao Paulo", "SP", "Sala 12"));
        clientePj = clienteRepository.save(clientePj);

        if (contaRepository.count() == 0) {
            ContaCorrente corrente = new ContaCorrente();
            corrente.setAgencia("0001");
            corrente.setNumero("00000001");
            corrente.setSaldoInicial(new BigDecimal("1500.00"));
            corrente.setStatus(ContaStatus.ATIVA);
            corrente.setLimiteChequeEspecial(new BigDecimal("500.00"));
            corrente.setCliente(clientePf);
            contaRepository.save(corrente);

            ContaJuridica juridica = new ContaJuridica();
            juridica.setAgencia("0001");
            juridica.setNumero("00000002");
            juridica.setSaldoInicial(new BigDecimal("5000.00"));
            juridica.setStatus(ContaStatus.ATIVA);
            juridica.setTaxaPacoteMensal(new BigDecimal("89.90"));
            juridica.setResponsavelFinanceiro("Ana Financeira");
            juridica.setCliente(clientePj);
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
