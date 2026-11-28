package br.ufpb.dcx.poo.biblioteca.jogos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fila de reservas de um jogo, por ordem de chegada.
 * Composição (e não "extends ArrayList"): a ordem só muda pelos métodos
 * desta classe; ninguém de fora consegue furar a fila com add(int, ...),
 * set ou clear.
 */
public class FilaDeReservas {

    private final List<Reserva> reservas = new ArrayList<>();

    public void entrar(Reserva reserva) {
        reservas.add(reserva);
    }

    public void remover(Reserva reserva) {
        reservas.remove(reserva);
    }

    /** A reserva mais antiga que ainda não tem exemplar separado. */
    public Optional<Reserva> primeiraSemExemplar() {
        return reservas.stream()
                .filter(reserva -> !reserva.temExemplarSeparado())
                .findFirst();
    }

    public Optional<Reserva> buscar(String id) {
        return reservas.stream()
                .filter(reserva -> reserva.getId().equals(id))
                .findFirst();
    }

    public boolean temReservaDe(String matricula) {
        return reservas.stream()
                .anyMatch(reserva ->
                        reserva.getUsuario().getMatricula().equals(matricula));
    }

    /** Posição na fila, de 1 em diante. */
    public int posicaoDe(Reserva reserva) {
        return reservas.indexOf(reserva) + 1;
    }

    public boolean estaVazia() {
        return reservas.isEmpty();
    }

    /** Cópia imutável, na ordem de chegada. */
    public List<Reserva> emOrdem() {
        return List.copyOf(reservas);
    }
}
