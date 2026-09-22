package br.ufpb.dcx.poo.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;

/**
 * Verifica o ponto de entrada do contrato.
 *
 * <p>Se algum destes testes falhar, os testes de correção não conseguirão nem
 * começar a rodar. Eles precisam continuar passando o semestre inteiro.</p>
 */
class FabricaTest {

    @Test
    @DisplayName("a fábrica devolve uma biblioteca com os quatro serviços")
    void servicosDisponiveis() {
        Biblioteca biblioteca = Fabrica.novaBiblioteca();

        assertNotNull(biblioteca);
        assertNotNull(biblioteca.acervo());
        assertNotNull(biblioteca.usuarios());
        assertNotNull(biblioteca.emprestimos());
        assertNotNull(biblioteca.relatorios());
    }

    @Test
    @DisplayName("cada chamada devolve uma biblioteca independente")
    void instanciasIndependentes() throws BibliotecaException {
        Biblioteca uma = Fabrica.novaBiblioteca();
        Biblioteca outra = Fabrica.novaBiblioteca();

        assertNotSame(uma, outra);

        uma.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);

        assertEquals(1, uma.acervo().listarItens().size());
        assertEquals(0, outra.acervo().listarItens().size());
    }
}
