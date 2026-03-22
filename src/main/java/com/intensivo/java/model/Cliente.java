package com.intensivo.java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 30)
    private String documento;

    @Email(message = "Informe um email valido.")
    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 30)
    private String telefone;

    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "Informe um CEP valido.")
    @Column(nullable = false, length = 8)
    private String cep;

    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column
    private String complemento;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Size(min = 2, max = 2)
    @Column(nullable = false, length = 2)
    private String uf;
}
