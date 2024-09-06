-- Criação do banco de dados
CREATE DATABASE ecommerce;

-- Seleciona o banco de dados
\c ecommerce;

-- Criação da tabela de Produtos
CREATE TABLE produtos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10, 2) NOT NULL CHECK (preco > 0),
    quantidade_estoque INT NOT NULL CHECK (quantidade_estoque >= 0),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_estoque CHECK (quantidade_estoque >= 0)
);

-- Criação da tabela de Vendas
CREATE TABLE vendas (
    id SERIAL PRIMARY KEY,
    data_venda TIMESTAMP WITH TIME ZONE NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL CHECK (valor_total >= 0)
);

-- Criação da tabela de Produtos na Venda
CREATE TABLE produtos_venda (
    venda_id INT REFERENCES vendas(id) ON DELETE CASCADE,
    produto_id INT REFERENCES produtos(id),
    quantidade INT NOT NULL CHECK (quantidade > 0),
    preco DECIMAL(10, 2) NOT NULL CHECK (preco > 0),
    PRIMARY KEY (venda_id, produto_id)
);

-- Criação de índices para otimizar consultas
CREATE INDEX idx_produto_nome ON produtos(nome);
CREATE INDEX idx_venda_data ON vendas(data_venda);