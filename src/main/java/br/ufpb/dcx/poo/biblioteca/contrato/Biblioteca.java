package br.ufpb.dcx.poo.biblioteca.contrato;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;
public interface Biblioteca {
    AcervoService acervo();
    UsuarioService usuarios();
    EmprestimoService emprestimos();
    RelatorioService relatorios();
    void salvar() throws PersistenciaException;
    void carregar() throws PersistenciaException;
}
