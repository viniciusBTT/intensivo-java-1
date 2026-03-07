package com.intensivo.java.dto.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ClienteForm {

    private Long id;

    @NotBlank(message = "Informe um email.")
    @Email(message = "Informe um email valido.")
    private String email;

    @NotBlank(message = "Informe um telefone.")
    private String telefone;

    @NotBlank(message = "Informe um CEP.")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "Informe um CEP valido.")
    private String cep;

    @NotBlank(message = "Informe o numero do endereco.")
    private String numero;

    private String complemento;

    private String logradouro;

    private String bairro;

    private String cidade;

    private String uf;
}
