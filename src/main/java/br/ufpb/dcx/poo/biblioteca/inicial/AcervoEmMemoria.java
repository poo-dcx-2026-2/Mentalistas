package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.ArrayList;
import java.util.List;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.ExemplarView;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;

/**
 * Implementação inicial e parcial do acervo, guardando tudo em memória.
 *
 * <p><strong>Leia antes de estender.</strong> Três métodos já funcionam para os casos
 * mais simples: {@code cadastrarItem}, {@code buscarItem} e {@code listarItens}. Os
 * demais lançam {@link UnsupportedOperationException} e são tarefa sua.</p>
 *
 * <p>O que já está pronto <em>não</em> é um bom exemplo a copiar. Há decisões aqui
 * que você deve questionar ao longo da disciplina: como os itens são guardados, como
 * são procurados, quem é responsável por validar o quê e o que cada objeto expõe
 * para fora de si. Parte da avaliação é justamente perceber e justificar essas
 * mudanças.</p>
 */
public class AcervoEmMemoria implements AcervoService {

    private final List<Item> itens = new ArrayList<>();

    @Override
    public void cadastrarItem(String codigo, String titulo, String autoria,
                              String categoria, int ano)
            throws RecursoDuplicadoException {

        exigirTextoPreenchido(codigo, "codigo");
        exigirTextoPreenchido(titulo, "titulo");

        if (localizar(codigo) != null) {
            throw new RecursoDuplicadoException("Já existe item com o código " + codigo);
        }
        itens.add(new Item(codigo, titulo, autoria, categoria, ano));
    }

    @Override
    public ItemView buscarItem(String codigo) throws RecursoNaoEncontradoException {
        Item item = localizar(codigo);
        if (item == null) {
            throw new RecursoNaoEncontradoException("Item não encontrado: " + codigo);
        }
        return paraView(item);
    }

    @Override
    public List<ItemView> listarItens() {
        List<ItemView> resultado = new ArrayList<>();
        for (Item item : itens) {
            resultado.add(paraView(item));
        }
        resultado.sort((a, b) -> a.titulo().compareToIgnoreCase(b.titulo()));
        return resultado;
    }

    @Override
    public List<ItemView> buscarPorTitulo(String trecho) {
        throw new UnsupportedOperationException("Entrega 1: implementar buscarPorTitulo");
    }

    @Override
    public List<ItemView> buscarPorCategoria(String categoria) {
        throw new UnsupportedOperationException("Entrega 2: implementar buscarPorCategoria");
    }

    @Override
    public void adicionarExemplar(String codigoDoItem, String tombo)
            throws RecursoNaoEncontradoException, RecursoDuplicadoException {
        throw new UnsupportedOperationException("Entrega 1: implementar adicionarExemplar");
    }

    @Override
    public List<ExemplarView> listarExemplares(String codigoDoItem)
            throws RecursoNaoEncontradoException {
        throw new UnsupportedOperationException("Entrega 1: implementar listarExemplares");
    }

    @Override
    public void baixarExemplar(String tombo)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {
        throw new UnsupportedOperationException("Entrega 2: implementar baixarExemplar");
    }

    /**
     * Procura um item pelo código. Devolve {@code null} quando não encontra.
     *
     * <p>Este é o método que os outros usam para localizar um item.</p>
     */
    private Item localizar(String codigo) {
        for (Item item : itens) {
            if (item.getCodigo() == codigo) {
                return item;
            }
        }
        return null;
    }

    private ItemView paraView(Item item) {
        int disponiveis = 0;
        for (Exemplar exemplar : item.getExemplares()) {
            if (exemplar.getStatus() == StatusExemplar.DISPONIVEL) {
                disponiveis++;
            }
        }
        return new ItemView(
                item.getCodigo(),
                item.getTitulo(),
                item.getAutoria(),
                item.getCategoria(),
                item.getAno(),
                item.getExemplares().size(),
                disponiveis);
    }

    private static void exigirTextoPreenchido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo " + campo + " é obrigatório.");
        }
    }

    /** Acesso interno usado pelos demais serviços da implementação inicial. */
    List<Item> itens() {
        return itens;
    }
}
