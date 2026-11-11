package br.ufpb.dcx.poo.biblioteca.jogos;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.RelatorioService;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;
import br.ufpb.dcx.poo.biblioteca.inicial.EmprestimosNaoImplementados;
import br.ufpb.dcx.poo.biblioteca.inicial.RelatoriosNaoImplementados;
import java.time.LocalDate;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.ExemplarIndisponivelException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.LimiteDeEmprestimosExcedidoException;



public class Locadora implements Biblioteca {

    private final AcervoDeJogos acervo = new AcervoDeJogos();
    private final DadosdoUsuario usuarios = new DadosdoUsuario();
    private final EmprestimosNaoImplementados emprestimos = new EmprestimosNaoImplementados();
    private final RelatoriosNaoImplementados relatorios = new RelatoriosNaoImplementados();
    private final EmprestimosDeJogos emprestimo =
            new EmprestimosDeJogos(acervo, usuarios);

    @Override
    public AcervoService acervo() { return acervo; }

    @Override
    public UsuarioService usuarios() { return usuarios; }

    @Override
    public EmprestimoService emprestimos() { return emprestimos; }

    @Override
    public RelatorioService relatorios() { return relatorios; }

    @Override
    public void salvar() throws PersistenciaException {

    }

    @Override
    public void carregar() throws PersistenciaException {

    }

    public String emprestarComVerificacaoDeIdade(
            String tombo, String matricula, LocalDate data)
            throws RecursoNaoEncontradoException,
                   ExemplarIndisponivelException,
                   UsuarioInativoException,
                   LimiteDeEmprestimosExcedidoException{

        if (tombo == null || tombo.isBlank()
                || matricula == null || matricula.isBlank()
                || data == null) {
            return emprestimo.emprestar(tombo, matricula, data);
        }

        Usuario usuario = usuarios.localizar(matricula);

        Exemplar exemplar = null;

        for (Jogo jogo : acervo.itens()) {
            for (Exemplar e : jogo.getExemplares()) {
                if (e.getTombo().equals(tombo)) {
                    exemplar = e;
                    break;
                }
            }

            if (exemplar != null) {
                break;
            }
        }

        if (exemplar == null) {
            return emprestimo.emprestar(tombo, matricula, data);
        }

        if (usuario.getIdade()
                < exemplar.getItem().getClassificacaoIndicativa()) {
            throw new IllegalArgumentException(
                    "Idade insuficiente para alugar este jogo.");
        }

        return emprestimo.emprestar(tombo, matricula, data);
    }

}