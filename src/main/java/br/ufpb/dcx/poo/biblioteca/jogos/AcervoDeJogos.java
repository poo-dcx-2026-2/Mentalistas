package br.ufpb.dcx.poo.biblioteca.jogos;


import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.ExemplarView;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;



import java.util.ArrayList;
import java.util.List;



public class AcervoDeJogos implements AcervoService {

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
        return null;
    }

    @Override
    public List<ItemView> listarItens() {
        return List.of();
    }

    @Override
    public List<ItemView> buscarPorTitulo(String trecho) {
        return List.of();
    }

    @Override
    public List<ItemView> buscarPorCategoria(String categoria) {
        return List.of();
    }

    @Override
    public void adicionarExemplar(String codigoDoItem, String tombo) throws RecursoNaoEncontradoException, RecursoDuplicadoException {

    }

    @Override
    public List<ExemplarView> listarExemplares(String codigoDoItem) throws RecursoNaoEncontradoException {
        return List.of();
    }

    @Override
    public void baixarExemplar(String tombo) throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {

    }

    private Item localizar(String codigo) {
        for (Item item : itens) {
            if (item.getCodigo() == codigo) {
                return item;
            }
        }
        return null;
    }

    private static void exigirTextoPreenchido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo " + campo + " é obrigatório.");
        }
    }

    List<Item> itens() {
        return itens;
    }
}
