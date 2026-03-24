package com.intensivo.java.model;

// Imports do JPA para mapear a classe como tabela do banco.
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

// Imports do Lombok para gerar codigo repetitivo automaticamente.
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Gera os metodos get de todos os atributos.
@Getter
// Gera os metodos set de todos os atributos.
@Setter
// Gera um construtor vazio, exigido em muitos cenarios do JPA.
@NoArgsConstructor
// Marca a classe como uma entidade que sera persistida no banco.
@Entity
// Define o nome da tabela usada para salvar as contas.
@Table(name = "contas")
// Model representa a estrutura da conta que sera salva na tabela "contas".
public class Conta {

    // Chave primaria da tabela.
    @Id
    // Faz o banco gerar o id automaticamente a cada novo registro.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Numero unico que identifica a conta.
    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    // Agencia a qual a conta pertence.
    @Column(nullable = false, length = 10)
    private String agencia;

    // Tipo da conta, por exemplo: CORRENTE ou POUPANCA.
    @Column(nullable = false, length = 20)
    private String tipo;

    // Saldo atual da conta. precision define o total de digitos, scale define as casas decimais.
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    // Indica se a conta esta ativa. Inicia como verdadeiro por padrao.
    @Column(nullable = false)
    private Boolean ativa = true;

}
