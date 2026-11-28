package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.ExemplarView;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.List;

/** Cenário comum: item L1 com T-1 e T-2; usuários Ana, Bruno e Carla. */
abstract class TesteDeBiblioteca {

    protected static final LocalDate SEGUNDA = LocalDate.of(2026, 11, 2);

    protected static final String ANA = "2026001";
    protected static final String BRUNO = "2026002";
    protected static final String CARLA = "2026003";

    protected Biblioteca biblioteca;

    @BeforeEach
    void criarBibliotecaComUmItemETresUsuarios() throws BibliotecaException {
        biblioteca = Fabrica.novaBiblioteca();
        biblioteca.acervo().cadastrarItem(
                "L1", "Carcassonne", "Wrede", "jogo", 2000);
        biblioteca.acervo().adicionarExemplar("L1", "T-1");
        biblioteca.usuarios().cadastrarUsuario(ANA, "Ana");
        biblioteca.usuarios().cadastrarUsuario(BRUNO, "Bruno");
        biblioteca.usuarios().cadastrarUsuario(CARLA, "Carla");
    }

    protected StatusExemplar statusDe(String codigo, String tombo)
            throws BibliotecaException {
        return biblioteca.acervo().listarExemplares(codigo).stream()
                .filter(e -> e.tombo().equals(tombo))
                .map(ExemplarView::status)
                .findFirst()
                .orElseThrow();
    }

    protected List<String> matriculasDaFila(String codigo)
            throws BibliotecaException {
        return biblioteca.emprestimos().listarReservas(codigo).stream()
                .map(ReservaView::matricula)
                .toList();
    }
}
