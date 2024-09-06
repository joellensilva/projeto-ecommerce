package com.projeto.ecommerce.service;

import com.projeto.ecommerce.exception.BadRequestException;
import com.projeto.ecommerce.exception.ResourceNotFoundException;
import com.projeto.ecommerce.model.Produto;
import com.projeto.ecommerce.model.Venda;
import com.projeto.ecommerce.repository.ProdutoRepository;
import com.projeto.ecommerce.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private VendaRepository vendaRepository;

    @CacheEvict(value = "produtos", allEntries = true)
    public Produto criarProduto(Produto produto) {
        if (produto.getPreco() <= 0) {
            throw new BadRequestException("O preço do produto deve ser maior que zero.");
        }
        return produtoRepository.save(produto);
    }

    @CacheEvict(value = "produtos", allEntries = true)
    public Produto atualizarProduto(Long id, Produto produtoAtualizado) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);
        if (produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produto.setAtivo(produtoAtualizado.getAtivo());
            return produtoRepository.save(produto);
        } else {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + id);
        }
    }

    public void inativarProduto(Long id) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);
        if (produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();
            produto.setAtivo(false);
            produtoRepository.save(produto);
        } else {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + id);
        }
    }

    public Produto buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
    }

    public List<Produto> listarProdutosAtivos() {
        return produtoRepository.findAll().stream()
                .filter(Produto::getAtivo)
                .toList();
    }

    @Cacheable("produtos")
    public List<Produto> listarTodosProdutos() {
        return produtoRepository.findAll();
    }

    public void realizarVenda(Long produtoId, Integer quantidade) {
        Optional<Produto> produtoOptional = produtoRepository.findById(produtoId);
        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();
            if (produto.getAtivo() && produto.getQuantidadeEstoque() >= quantidade) {
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
                produtoRepository.save(produto);

                Venda venda = new Venda();
                venda.setProduto(produto);
                venda.setQuantidade(quantidade);
                vendaRepository.save(venda);
            } else {
                throw new BadRequestException("Estoque insuficiente ou produto inativo");
            }
        } else {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + produtoId);
        }
    }

    @CacheEvict(value = "vendas", allEntries = true)
    public Venda criarVenda(Venda venda) {
        if (venda.getQuantidade() >= 1 && venda.getProduto() != null) {
            return vendaRepository.save(venda);
        } else {
            throw new BadRequestException("Venda deve ter pelo menos um produto");
        }
    }

    @CacheEvict(value = "vendas", allEntries = true)
    public Venda atualizarVenda(Long id, Venda vendaAtualizada) {
        Optional<Venda> vendaExistente = vendaRepository.findById(id);
        if (vendaExistente.isPresent()) {
            Venda venda = vendaExistente.get();
            venda.setProduto(vendaAtualizada.getProduto());
            venda.setQuantidade(vendaAtualizada.getQuantidade());
            return vendaRepository.save(venda);
        } else {
            throw new ResourceNotFoundException("Venda não encontrada com ID: " + id);
        }
    }

    @CacheEvict(value = "vendas", allEntries = true)
    public void excluirVenda(Long id) {
        Optional<Venda> vendaExistente = vendaRepository.findById(id);
        if (vendaExistente.isPresent()) {
            vendaRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Venda não encontrada com ID: " + id);
        }
    }

    public Venda buscarVendaPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));
    }

    @Cacheable("vendas")
    public List<Venda> listarVendas() {
        return vendaRepository.findAll();
    }
}
