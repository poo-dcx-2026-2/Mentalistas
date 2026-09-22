package br.ufpb.dcx.poo.biblioteca.contrato;
import java.util.List;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.*;
public interface AcervoService {
    void cadastrarItem(String codigo, String titulo, String autoria, String categoria, int ano)
            throws RecursoDuplicadoException;
    ItemView buscarItem(String codigo) throws RecursoNaoEncontradoException;
    List<ItemView> listarItens();
    List<ItemView> buscarPorTitulo(String trecho);
    List<ItemView> buscarPorCategoria(String categoria);
    void adicionarExemplar(String codigoDoItem, String tombo)
            throws RecursoNaoEncontradoException, RecursoDuplicadoException;
    List<ExemplarView> listarExemplares(String codigoDoItem) throws RecursoNaoEncontradoException;
    void baixarExemplar(String tombo)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException;
}
