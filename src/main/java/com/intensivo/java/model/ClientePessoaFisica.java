package com.intensivo.java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clientes_pessoa_fisica")
public class ClientePessoaFisica extends Cliente {

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Override
    public String getNomeExibicao() {
        return nomeCompleto;
    }

    @Override
    public String getDocumento() {
        return cpf;
    }

    @Override
    public String getTipoCliente() {
        return "Pessoa Fisica";
    }
}
