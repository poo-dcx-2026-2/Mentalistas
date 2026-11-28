package br.ufpb.dcx.poo.biblioteca.jogos;

import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;

import java.util.ArrayList;
import java.util.List;

public class Jogo {

    private final String codigo;
    private final String titulo;
    private final String autoria;
    private final String categoria;
    private final int ano;
    private int classificacaoIndicativa;
    private final List<Exemplar> exemplares = new ArrayList<>();
    private final FilaDeReservas fila = new FilaDeReservas();

    public Jogo(String codigo, String titulo, String autoria,
                String categoria, int ano) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autoria = autoria;
        this.categoria = categoria;
        this.ano = ano;
    }

    public String getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public String getAutoria() { return autoria; }
    public String getCategoria() { return categoria; }
    public int getAno() { return ano; }

    // TODO (PR de encapsulamento): devolver cópia imutável e criar
    // adicionarExemplar(...) aqui.
    public List<Exemplar> getExemplares() { return exemplares; }

    public FilaDeReservas getFila() { return fila; }

    /** Especialista: quem tem os exemplares sabe se algum está disponível. */
    public boolean temExemplarDisponivel() {
        return exemplares.stream()
                .anyMatch(e -> e.getStatus() == StatusExemplar.DISPONIVEL);
    }

    public int getClassificacaoIndicativa() {
        return classificacaoIndicativa;
    }

    public void definirClassificacaoIndicativa(int classificacaoIndicativa) {
        if (classificacaoIndicativa != 0
                && classificacaoIndicativa != 10
                && classificacaoIndicativa != 12
                && classificacaoIndicativa != 14
                && classificacaoIndicativa != 16
                && classificacaoIndicativa != 18) {
            throw new IllegalArgumentException(
                    "Classificação indicativa inválida.");
        }
        this.classificacaoIndicativa = classificacaoIndicativa;
    }
}
