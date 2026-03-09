package com.intensivo.java.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente extends BaseEntity {

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private TipoCliente tipoCliente;

    @Column(nullable = false, unique = true, length = 14)
    private String documento;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Embedded
    private Endereco endereco;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Conta> contas = new LinkedHashSet<>();

    public String getNomeExibicao() {
        return nome;
    }

    public String getTipoClienteDescricao() {
        return tipoCliente == null ? "" : tipoCliente.getDescricao();
    }

    public String getDocumentoLabel() {
        return tipoCliente == null ? "Documento" : tipoCliente.getDocumentoLabel();
    }
}
