package com.projeto.ecommerce.service;

import com.projeto.ecommerce.exception.BadRequestException;
import com.projeto.ecommerce.exception.ResourceNotFoundException;
import com.projeto.ecommerce.model.ItemVenda;
import com.projeto.ecommerce.model.Produto;
import com.projeto.ecommerce.model.Venda;
import com.projeto.ecommerce.repository.ProdutoRepository;
import com.projeto.ecommerce.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @CacheEvict(value = "vendas", allEntries = true)
    public Venda realizarVenda(List<ItemVenda> itensVenda) {
        // Verificar se a lista de itensVenda não está vazia
    if (itensVenda == null || itensVenda.isEmpty()) {
        throw new BadRequestException("A venda deve ter pelo menos um item.");
    }

    // Criar a nova venda
    Venda novaVenda = new Venda();
    novaVenda.setDataVenda(LocalDateTime.now());

    // Atualizar o estoque dos produtos e associar itens à venda
    double valorTotal = 0.0;
    for (ItemVenda item : itensVenda) {
        Produto produto = produtoRepository.findById(item.getProduto().getId())
                                           .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + item.getProduto().getId()));

        if (!produto.getAtivo()) {
            throw new BadRequestException("Produto inativo: " + produto.getId());
        }

        if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
            throw new BadRequestException("Estoque insuficiente para o produto: " + produto.getId());
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
        produtoRepository.save(produto);

        item.setVenda(novaVenda); // Associar item à venda
        item.setProduto(produto); // Definir produto no item

        valorTotal += produto.getPreco() * item.getQuantidade();
    }

    novaVenda.setValorTotal(valorTotal);
    novaVenda.setItens(itensVenda); // Associar todos os itens à venda

    // Salvar a nova venda
    return vendaRepository.save(novaVenda);
}
    

    // @CacheEvict(value = "vendas", allEntries = true)
    // public Venda criarVenda(Venda venda) {
    // if (venda.getItens() != null && !venda.getItens().isEmpty()) {
    // venda.getItens().forEach(item -> {
    // if (item.getQuantidade() < 1 || item.getProduto() == null) {
    // throw new BadRequestException("Cada item da venda deve ter pelo menos uma
    // quantidade e um produto associado");
    // }
    // if (item.getQuantidade() > item.getProduto().getQuantidadeEstoque()) {
    // throw new BadRequestException("Quantidade do item não pode exceder o estoque
    // do produto");
    // }
    // });
    // return vendaRepository.save(venda);
    // } else {
    // throw new BadRequestException("Venda deve ter pelo menos um item");
    // }
    // }

    @CacheEvict(value = "vendas", allEntries = true)
    public Venda atualizarVenda(Long id, List<ItemVenda> itensAtualizados) {
        Optional<Venda> vendaExistente = vendaRepository.findById(id);
        if (vendaExistente.isPresent()) {
            Venda venda = vendaExistente.get();

            // Validar e atualizar o estoque de cada produto
            for (ItemVenda item : itensAtualizados) {
                Optional<Produto> produtoOptional = produtoRepository.findById(item.getProduto().getId());
                if (produtoOptional.isPresent()) {
                    Produto produto = produtoOptional.get();
                    if (produto.getAtivo() && produto.getQuantidadeEstoque() >= item.getQuantidade()) {
                        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
                        produtoRepository.save(produto);
                    } else {
                        throw new BadRequestException(
                                "Estoque insuficiente ou produto inativo para o produto ID: " + produto.getId());
                    }
                } else {
                    throw new ResourceNotFoundException("Produto não encontrado com ID: " + item.getProduto().getId());
                }
            }

            venda.setItens(itensAtualizados);
            venda.setDataVenda(LocalDateTime.now());
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
