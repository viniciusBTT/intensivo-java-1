package com.intensivo.java.config;

import com.intensivo.java.model.Role;
import com.intensivo.java.model.Usuario;
import com.intensivo.java.repository.UsuarioRepository;
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
public class BootstrapDataRunner implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final DefaultUsersProperties defaultUsersProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        criarUsuarioSeNecessario(
                defaultUsersProperties.defaultAdminUsername(),
                defaultUsersProperties.defaultAdminPassword(),
                Set.of(Role.ADMIN, Role.ATENDENTE));

        criarUsuarioSeNecessario(
                defaultUsersProperties.defaultAttendantUsername(),
                defaultUsersProperties.defaultAttendantPassword(),
                Set.of(Role.ATENDENTE));
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
}
