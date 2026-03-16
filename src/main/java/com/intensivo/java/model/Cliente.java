package com.intensivo.java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Locale;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do cliente.")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Informe o documento.")
    @Column(nullable = false, unique = true, length = 30)
    private String documento;

    @NotBlank(message = "Informe um email.")
    @Email(message = "Informe um email valido.")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Informe um telefone.")
    @Column(nullable = false, length = 30)
    private String telefone;

    @NotBlank(message = "Informe um CEP.")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "Informe um CEP valido.")
    @Column(nullable = false, length = 8)
    private String cep;

    @NotBlank(message = "Informe o logradouro.")
    @Column(nullable = false)
    private String logradouro;

    @NotBlank(message = "Informe o numero do endereco.")
    @Column(nullable = false, length = 20)
    private String numero;

    @Column
    private String complemento;

    @NotBlank(message = "Informe o bairro.")
    @Column(nullable = false)
    private String bairro;

    @NotBlank(message = "Informe a cidade.")
    @Column(nullable = false)
    private String cidade;

    @NotBlank(message = "Informe a UF.")
    @Size(min = 2, max = 2, message = "Informe uma UF com 2 caracteres.")
    @Column(nullable = false, length = 2)
    private String uf;

    public Cliente normalizarCampos() {
        nome = normalizar(nome);
        documento = normalizar(documento);
        email = normalizar(email).toLowerCase(Locale.ROOT);
        telefone = normalizar(telefone);
        cep = normalizar(cep).replaceAll("\\D", "");
        logradouro = normalizar(logradouro);
        numero = normalizar(numero);
        complemento = normalizarOpcional(complemento);
        bairro = normalizar(bairro);
        cidade = normalizar(cidade);
        uf = normalizar(uf).toUpperCase(Locale.ROOT);
        return this;
    }

    private String normalizar(String valor) {
        return valor.trim();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String valorNormalizado = valor.trim();
        return valorNormalizado.isEmpty() ? null : valorNormalizado;
    }
}
