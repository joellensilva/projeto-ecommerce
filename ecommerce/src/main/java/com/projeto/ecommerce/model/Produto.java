package com.projeto.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do produto não pode estar vazio")
    private String nome;

    @NotBlank(message = "A descrição do produto não pode estar vazia")
    @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
    private String descricao;

    @NotNull(message = "O preço do produto não pode ser nulo")
    @Min(value = 0, message = "O preço do produto deve ser positivo")
    private Double preco;

    @NotNull(message = "A quantidade em estoque não pode ser nula")
    @Min(value = 0, message = "A quantidade em estoque deve ser zero ou positiva")
    private Integer quantidadeEstoque;

    private Boolean ativo = true; // Novo campo para indicar se o produto está ativo

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
