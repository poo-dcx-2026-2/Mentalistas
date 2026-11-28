package br.ufpb.dcx.poo.biblioteca.jogos;

import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;

public class Exemplar {

    private final String tombo;
    private final Jogo item;
    private StatusExemplar status;
    private Reserva reservaDona;

    public Exemplar(String tombo, Jogo item) {
        this.tombo = tombo;
        this.item = item;
        this.status = StatusExemplar.DISPONIVEL;
    }

    public String getTombo() { return tombo; }

    public Jogo getItem() { return item; }

    public StatusExemplar getStatus() { return status; }
    public void setStatus(StatusExemplar status) { this.status = status; }

    public Reserva getReservaDona() { return reservaDona; }

    /**
     * Um exemplar DISPONIVEL pode ir para qualquer usuário; um RESERVADO só
     * para o dono da reserva; os demais status, para ninguém.
     */
    public boolean podeSerEmprestadoPara(Usuario usuario) {
        if (status == StatusExemplar.DISPONIVEL) {
            return true;
        }
        return status == StatusExemplar.RESERVADO
                && reservaDona != null
                && reservaDona.getUsuario().getMatricula()
                .equals(usuario.getMatricula());
    }

    void guardarPara(Reserva reserva) {
        this.reservaDona = reserva;
        this.status = StatusExemplar.RESERVADO;
    }

    void soltarReserva() {
        this.reservaDona = null;
    }
}
