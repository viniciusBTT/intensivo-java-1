package com.intensivo.java.dto.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientePessoaFisicaForm extends ClienteForm {

    @NotBlank(message = "Informe o nome completo.")
    private String nomeCompleto;

    @NotBlank(message = "Informe o CPF.")
    private String cpf;
}
