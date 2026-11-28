package br.ufpb.dcx.poo.biblioteca.jogos;

import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

import java.time.LocalDate;

/**
 * Uma reserva: liga um usuário a um jogo, na fila desse jogo.
 * Quando um exemplar é devolvido, a reserva mais antiga sem exemplar
 * "separa" um (guarda a referência a ele) até ser atendida ou cancelada.
 */
public class Reserva {

    private final String id;
    private final Jogo jogo;
    private final Usuario usuario;
    private final LocalDate data;

    private Exemplar exemplarSeparado;

    public Reserva(String id, Jogo jogo, Usuario usuario, LocalDate data) {
        if (id == null || id.isBlank()
                || jogo == null
                || usuario == null
                || data == null) {
            throw new DadosInvalidosException(
                    "Os dados da reserva são obrigatórios.");
        }
        this.id = id;
        this.jogo = jogo;
        this.usuario = usuario;
        this.data = data;
    }

    public String getId() { return id; }
    public Jogo getJogo() { return jogo; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getData() { return data; }
    public Exemplar getExemplarSeparado() { return exemplarSeparado; }

    public boolean temExemplarSeparado() {
        return exemplarSeparado != null;
    }

    /** Guarda o exemplar para esta reserva e o marca como RESERVADO. */
    public void separar(Exemplar exemplar) {
        if (exemplarSeparado != null) {
            throw new IllegalStateException(
                    "A reserva " + id + " já tem exemplar separado.");
        }
        exemplar.guardarPara(this);
        this.exemplarSeparado = exemplar;
    }

    /**
     * Solta o exemplar separado e o devolve. O status do exemplar não muda
     * aqui: quem chama decide se ele vai para outra reserva, para
     * DISPONIVEL ou para INDISPONIVEL.
     */
    public Exemplar liberarExemplar() {
        Exemplar exemplar = exemplarSeparado;
        exemplarSeparado = null;
        if (exemplar != null) {
            exemplar.soltarReserva();
        }
        return exemplar;
    }
}
