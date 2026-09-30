package br.ufpb.dcx.poo.biblioteca.jogos;


import java.util.ArrayList;
import java.util.List;


public class Item {

    private String codigo;
    private String titulo;
    private String autoria;
    private String categoria;
    private int ano;
    private final List<Exemplar> exemplares = new ArrayList<>();

    public Item(String codigo, String titulo, String autoria, String categoria, int ano) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autoria = autoria;
        this.categoria = categoria;
        this.ano = ano;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutoria() { return autoria; }
    public void setAutoria(String autoria) { this.autoria = autoria; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public List<Exemplar> getExemplares() { return exemplares; }
}
