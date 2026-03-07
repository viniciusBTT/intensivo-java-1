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
@Table(name = "clientes_pessoa_juridica")
public class ClientePessoaJuridica extends Cliente {

    @Column(nullable = false)
    private String razaoSocial;

    @Column(nullable = false)
    private String nomeFantasia;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Override
    public String getNomeExibicao() {
        return nomeFantasia;
    }

    @Override
    public String getDocumento() {
        return cnpj;
    }

    @Override
    public String getTipoCliente() {
        return "Pessoa Juridica";
    }
}
