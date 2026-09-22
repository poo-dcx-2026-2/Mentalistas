package br.ufpb.dcx.poo.biblioteca.contrato;
import java.time.LocalDate;
public record EmprestimoView(String id, String tombo, String matricula,
                             LocalDate dataDoEmprestimo, LocalDate dataPrevistaDeDevolucao,
                             LocalDate dataDaDevolucao, StatusEmprestimo status) { }
