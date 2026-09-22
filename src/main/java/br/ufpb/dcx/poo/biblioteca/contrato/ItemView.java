package br.ufpb.dcx.poo.biblioteca.contrato;
public record ItemView(String codigo, String titulo, String autoria, String categoria,
                       int ano, int totalDeExemplares, int exemplaresDisponiveis) { }
