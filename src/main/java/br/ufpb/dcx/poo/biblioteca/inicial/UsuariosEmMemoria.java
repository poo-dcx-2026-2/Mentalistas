package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.ArrayList;
import java.util.List;

import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioService;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;

/**
 * Implementação inicial e parcial dos usuários.
 *
 * <p>Não existe classe de domínio para o usuário: os dados estão soltos em listas
 * paralelas. É proposital. Uma das primeiras decisões da Entrega 1 é definir se
 * isso deve continuar assim.</p>
 */
public class UsuariosEmMemoria implements UsuarioService {

    private final List<String> matriculas = new ArrayList<>();
    private final List<String> nomes = new ArrayList<>();

    @Override
    public void cadastrarUsuario(String matricula, String nome)
            throws RecursoDuplicadoException {

        if (matricula == null || matricula.isBlank()) {
            throw new DadosInvalidosException("A matrícula é obrigatória.");
        }
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome é obrigatório.");
        }
        if (matriculas.contains(matricula)) {
            throw new RecursoDuplicadoException("Já existe usuário com a matrícula " + matricula);
        }
        matriculas.add(matricula);
        nomes.add(nome);
    }

    @Override
    public UsuarioView buscarUsuario(String matricula) throws RecursoNaoEncontradoException {
        int posicao = matriculas.indexOf(matricula);
        if (posicao < 0) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado: " + matricula);
        }
        return new UsuarioView(matriculas.get(posicao), nomes.get(posicao), true, 0);
    }

    @Override
    public List<UsuarioView> listarUsuarios() {
        List<UsuarioView> resultado = new ArrayList<>();
        for (int i = 0; i < matriculas.size(); i++) {
            resultado.add(new UsuarioView(matriculas.get(i), nomes.get(i), true, 0));
        }
        resultado.sort((a, b) -> a.nome().compareToIgnoreCase(b.nome()));
        return resultado;
    }

    @Override
    public void desativarUsuario(String matricula)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {
        throw new UnsupportedOperationException("Entrega 2: implementar desativarUsuario");
    }

    @Override
    public void reativarUsuario(String matricula) throws RecursoNaoEncontradoException {
        throw new UnsupportedOperationException("Entrega 2: implementar reativarUsuario");
    }
}
