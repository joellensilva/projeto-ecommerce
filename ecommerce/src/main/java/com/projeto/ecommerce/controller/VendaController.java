package com.projeto.ecommerce.controller;

import com.projeto.ecommerce.exception.BadRequestException;
import com.projeto.ecommerce.exception.ResourceNotFoundException;
import com.projeto.ecommerce.model.ItemVenda;
import com.projeto.ecommerce.model.Venda;
import com.projeto.ecommerce.service.ProdutoService;
import com.projeto.ecommerce.service.VendaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private VendaService vendaService;

    @PostMapping
    public ResponseEntity<Venda> criarVenda(@RequestBody List<ItemVenda> itensVenda) {
        try {
            Venda novaVenda = produtoService.realizarVenda(itensVenda);
            return new ResponseEntity<>(novaVenda, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println(e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venda> atualizarVenda(@PathVariable Long id, @RequestBody List<ItemVenda> itensAtualizados) {
        try {
            Venda venda = produtoService.atualizarVenda(id, itensAtualizados);
            return new ResponseEntity<>(venda, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirVenda(@PathVariable Long id) {
        try {
            produtoService.excluirVenda(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarVendaPorId(@PathVariable Long id) {
        try {
            Venda venda = produtoService.buscarVendaPorId(id);
            return new ResponseEntity<>(venda, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Venda>> listarVendas() {
        List<Venda> vendas = produtoService.listarVendas();
        return new ResponseEntity<>(vendas, HttpStatus.OK);
    }

    // Endpoint para obter vendas por data específica
    @GetMapping("/relatorio/data")
    public List<Venda> obterVendasPorData(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return vendaService.obterVendasPorData(startDate, endDate);
    }

    // Endpoint para obter vendas por mês
    @GetMapping("/relatorio/mes")
    public List<Venda> obterVendasPorMes(@RequestParam int ano, @RequestParam int mes) {
        return vendaService.obterVendasPorMes(ano, mes);
    }

    // Endpoint para obter vendas pela semana atual
    @GetMapping("/relatorio/semana-atual")
    public List<Venda> obterVendasPorSemanaAtual() {
        return vendaService.obterVendasPorSemanaAtual();
    }
}
