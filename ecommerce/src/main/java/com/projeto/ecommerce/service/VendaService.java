package com.projeto.ecommerce.service;

import com.projeto.ecommerce.model.Venda;
import com.projeto.ecommerce.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class VendaService {

    @Autowired
    private VendaRepository repositorioVenda;

    // Relatório de vendas por data específica
    public List<Venda> obterVendasPorData(LocalDate startDate, LocalDate endDate) {
        return repositorioVenda.findVendasByData(startDate, endDate);
    }

    // Relatório de vendas por mês
    public List<Venda> obterVendasPorMes(int ano, int mes) {
        LocalDate inicioMes = LocalDate.of(ano, mes, 1);
        LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
        return repositorioVenda.findVendasByMes(inicioMes, fimMes);
    }

    // Relatório de vendas pela semana atual
    public List<Venda> obterVendasPorSemanaAtual() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.with(DayOfWeek.MONDAY);
        LocalDate fimSemana = hoje.with(DayOfWeek.SUNDAY);

        return repositorioVenda.findVendasBySemanaAtual(inicioSemana, fimSemana);
    }
}
