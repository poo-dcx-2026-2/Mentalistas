package br.ufpb.dcx.poo.biblioteca.jogos;

import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;


public class Exemplar {

    private String tombo;
    private Jogo item;
    private StatusExemplar status;

    public Exemplar(String tombo, Jogo item) {
        this.tombo = tombo;
        this.item = item;
        this.status = StatusExemplar.DISPONIVEL;
    }

    public String getTombo() { return tombo; }
    public void setTombo(String tombo) { this.tombo = tombo; }

    public Jogo getItem() { return item; }
    public void setItem(Jogo item) { this.item = item; }

    public StatusExemplar getStatus() { return status; }
    public void setStatus(StatusExemplar status) { this.status = status; }
}
