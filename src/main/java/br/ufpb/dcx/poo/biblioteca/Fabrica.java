package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.inicial.BibliotecaInicial;

/**
 * Ponto de entrada do sistema.
 *
 * <p><strong>O nome da classe, o pacote e a assinatura do método são fixos.</strong>
 * É por aqui que os testes de correção obtêm a implementação da sua equipe. Você pode
 * — e vai — trocar o que o método devolve. Não troque o nome nem a assinatura.</p>
 */
public final class Fabrica {

    private Fabrica() {
    }

    /**
     * Devolve uma biblioteca nova e vazia.
     *
     * <p>Cada chamada precisa devolver uma instância independente: os testes contam
     * com isso para não interferirem uns nos outros.</p>
     */
    public static Biblioteca novaBiblioteca() {
        return new BibliotecaInicial();
    }
}
