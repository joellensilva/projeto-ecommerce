package com.projeto.ecommerce.repository;

import com.projeto.ecommerce.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Relatório de vendas por data específica
    @Query("SELECT v FROM Venda v WHERE v.dataVenda BETWEEN :startDate AND :endDate")
    List<Venda> findVendasByData(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Relatório de vendas por mês
    @Query("SELECT v FROM Venda v WHERE v.dataVenda BETWEEN :startDate AND :endDate")
    List<Venda> findVendasByMes(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Relatório de vendas pela semana atual
    @Query("SELECT v FROM Venda v WHERE v.dataVenda BETWEEN :startDate AND :endDate")
    List<Venda> findVendasBySemanaAtual(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
