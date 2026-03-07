package com.intensivo.java.dto.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientePessoaJuridicaForm extends ClienteForm {

    @NotBlank(message = "Informe a razao social.")
    private String razaoSocial;

    @NotBlank(message = "Informe o nome fantasia.")
    private String nomeFantasia;

    @NotBlank(message = "Informe o CNPJ.")
    private String cnpj;
}
