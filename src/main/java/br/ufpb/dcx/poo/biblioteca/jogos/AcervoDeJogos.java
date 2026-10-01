package br.ufpb.dcx.poo.biblioteca.jogos;


import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.ExemplarView;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;



import java.util.ArrayList;
import java.util.List;



public class AcervoDeJogos implements AcervoService {

    private final List<Jogo> jogos = new ArrayList<>();


    @Override
    public void cadastrarItem(String codigo, String titulo, String autoria,
                              String categoria, int ano)
            throws RecursoDuplicadoException {

        exigirTextoPreenchido(codigo, "codigo");
        exigirTextoPreenchido(titulo, "titulo");

        if (localizar(codigo) != null) {
            throw new RecursoDuplicadoException("Já existe item com o código " + codigo);
        }
        jogos.add(new Jogo(codigo, titulo, autoria, categoria, ano));
    }

    @Override
    public ItemView buscarItem(String codigo) throws RecursoNaoEncontradoException {
        int disponiveis = 0;
        for (Jogo jogo : jogos) {
            if (jogo.getCodigo().equals(codigo)) {
                return new ItemView(
                        jogo.getCodigo(),
                        jogo.getTitulo(),
                        jogo.getAutoria(),
                        jogo.getCategoria(),
                        jogo.getAno(),
                        jogo.getExemplares().size(),
                        disponiveis
                );
            }
        }

        throw new RecursoNaoEncontradoException(
                "Item não encontrado: " + codigo
        );
    }

    @Override
    public List<ItemView> listarItens() {
        return List.of();
    }

    @Override
    public List<ItemView> buscarPorTitulo(String trecho) {
        List<ItemView> resultado = new ArrayList<>();

        for (Jogo jogo : jogos) {
            if (jogo.getTitulo().toLowerCase().contains(trecho.toLowerCase())) {

                int total = jogo.getExemplares().size();

                int disponiveis = 0;

                for (Exemplar exemplar : jogo.getExemplares()) {
                    if (exemplar.getStatus() == StatusExemplar.DISPONIVEL) {
                        disponiveis++;
                    }
                }

                resultado.add(new ItemView(
                        jogo.getCodigo(),
                        jogo.getTitulo(),
                        jogo.getAutoria(),
                        jogo.getCategoria(),
                        jogo.getAno(),
                        total,
                        disponiveis
                ));
            }
        }

        return resultado;

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

    private Jogo localizar(String codigo) {
        for (Jogo item : jogos) {
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

    List<Jogo> itens() {
        return jogos;
    }
}
