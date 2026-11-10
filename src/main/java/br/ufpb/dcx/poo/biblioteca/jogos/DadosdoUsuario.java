package br.ufpb.dcx.poo.biblioteca.jogos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioService;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;

public class DadosdoUsuario implements UsuarioService {

    private final Map<String, Usuario> usuarios = new HashMap<>();

    @Override
    public void cadastrarUsuario(String matricula, String nome)
                throws RecursoDuplicadoException {

            if (matricula == null || matricula.isBlank()) {
                throw new DadosInvalidosException("A matrícula é obrigatória.");
            }
            if (nome == null || nome.isBlank()) {
                throw new DadosInvalidosException("O nome é obrigatório.");
            }
            if (usuarios.containsKey(matricula)) {
                throw new RecursoDuplicadoException("Já existe usuário com a matrícula " + matricula);
            }
            usuarios.put(matricula, new Usuario(matricula,nome));
        }

    @Override
    public UsuarioView buscarUsuario(String matricula) throws RecursoNaoEncontradoException {

        Usuario usuario = usuarios.get(matricula);
        if (usuario == null) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado: " + matricula);
        }
        return paraView(usuario);

    }

    @Override
    public List<UsuarioView> listarUsuarios() {

        List<UsuarioView> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios.values()) {
            resultado.add(paraView(usuario));
        }

        resultado.sort((a, b) ->
                a.nome().compareToIgnoreCase(b.nome()));

        return resultado;

    }

    @Override
    public void desativarUsuario(String matricula)
        throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {

        Usuario usuario = usuarios.get(matricula);

        if (usuario == null) {
            throw new RecursoNaoEncontradoException(
                    "Usuário não encontrado: " + matricula);
        }

        if (usuario.getEmprestimosAtivos() > 0) {
            throw new OperacaoNaoPermitidaException(
                    "Usuário possui empréstimos ativos.");
        }

        usuario.desativar();
    }

    @Override
    public void reativarUsuario(String matricula) throws RecursoNaoEncontradoException {
        Usuario usuario = usuarios.get(matricula);

        if (usuario == null) {
            throw new RecursoNaoEncontradoException(
                    "Usuário não encontrado: " + matricula);
        }

        usuario.reativar();
    }

    private UsuarioView paraView(Usuario usuario) {
        return new UsuarioView(
                usuario.getMatricula(),
                usuario.getNome(),
                usuario.isAtivo(),
                usuario.getEmprestimosAtivos()
        );
    }

}