package com.intensivo.java.model;

// Imports do JPA para mapear a classe como tabela do banco.
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Imports de validacao para conferir formato de alguns campos.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
// Define o nome da tabela usada para salvar os clientes.
@Table(name = "clientes")
// Model representa a estrutura do cliente que sera salva na tabela "clientes".
public class Cliente {

    // Chave primaria da tabela.
    @Id
    // Faz o banco gerar o id automaticamente a cada novo registro.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dados principais usados no cadastro.
    // Coluna obrigatoria no banco.
    @Column(nullable = false)
    private String nome;

    // Documento obrigatorio e unico para evitar duplicidade de cliente.
    @Column(nullable = false, unique = true, length = 30)
    private String documento;

    // Valida se o texto recebido tem formato de email.
    @Email(message = "Informe um email valido.")
    // Coluna obrigatoria no banco.
    @Column(nullable = false)
    private String email;

    // Telefone obrigatorio com limite de tamanho no banco.
    @Column(nullable = false, length = 30)
    private String telefone;

    // Campos de endereco do cliente.
    // Valida o formato do CEP aceitando com ou sem hifen(regexp).
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "Informe um CEP valido.")
    // Coluna obrigatoria com tamanho maximo de 8 caracteres.
    @Column(nullable = false, length = 8)
    private String cep;

    // Nome da rua, avenida ou logradouro informado no cadastro.
    @Column(nullable = false)
    private String logradouro;

    // Numero do endereco.
    @Column(nullable = false, length = 20)
    private String numero;

    // Complemento e opcional.
    @Column
    private String complemento;

    // Bairro obrigatorio.
    @Column(nullable = false)
    private String bairro;

    // Cidade obrigatoria.
    @Column(nullable = false)
    private String cidade;

    // Garante que a UF tenha exatamente 2 caracteres.
    @Size(min = 2, max = 2)
    // Coluna obrigatoria com tamanho maximo de 2 caracteres.
    @Column(nullable = false, length = 2)
    private String uf;
}
