package br.ufpb.dcx.poo.biblioteca.contrato;
import java.util.List;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.*;
public interface UsuarioService {
    void cadastrarUsuario(String matricula, String nome) throws RecursoDuplicadoException;
    UsuarioView buscarUsuario(String matricula) throws RecursoNaoEncontradoException;
    List<UsuarioView> listarUsuarios();
    void desativarUsuario(String matricula)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException;
    void reativarUsuario(String matricula) throws RecursoNaoEncontradoException;
}
