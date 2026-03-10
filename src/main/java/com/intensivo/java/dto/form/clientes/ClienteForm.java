package com.intensivo.java.dto.form.clientes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteForm {

    private String tipoCliente;
    private String nome;
    private String documento;
    private String email;
    private String telefone;
    private String cep;
    private String numero;
    private String complemento;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
}
