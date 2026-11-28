package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusEmprestimo;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Ordem de verificação, entradas inválidas e listagens (§3 do enunciado). */
class OrdemDasExcecoesTest extends TesteDeBiblioteca {

    @Test
    void usuarioInativoComExemplarEmprestadoRecebeUsuarioInativo()
            throws BibliotecaException {
        biblioteca.usuarios().desativarUsuario(BRUNO);
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);

        assertThrows(UsuarioInativoException.class,
                () -> biblioteca.emprestimos()
                        .emprestar("T-1", BRUNO, SEGUNDA));
    }

    @Test
    void exemplarIndisponivelVemAntesDoLimiteDeEmprestimos()
            throws BibliotecaException {
        biblioteca.acervo().adicionarExemplar("L1", "T-2");
        biblioteca.acervo().adicionarExemplar("L1", "T-3");
        biblioteca.acervo().adicionarExemplar("L1", "T-4");
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);
        biblioteca.emprestimos().emprestar("T-2", ANA, SEGUNDA);
        biblioteca.emprestimos().emprestar("T-3", ANA, SEGUNDA);
        biblioteca.emprestimos().emprestar("T-4", BRUNO, SEGUNDA);

        // Ana está no limite E T-4 está emprestado: vale a indisponibilidade
        assertThrows(ExemplarIndisponivelException.class,
                () -> biblioteca.emprestimos().emprestar("T-4", ANA, SEGUNDA));
    }

    @Test
    void limiteDeEmprestimosEhRecusadoSemMudarOExemplar()
            throws BibliotecaException {
        biblioteca.acervo().adicionarExemplar("L1", "T-2");
        biblioteca.acervo().adicionarExemplar("L1", "T-3");
        biblioteca.acervo().adicionarExemplar("L1", "T-4");
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);
        biblioteca.emprestimos().emprestar("T-2", ANA, SEGUNDA);
        biblioteca.emprestimos().emprestar("T-3", ANA, SEGUNDA);

        assertThrows(LimiteDeEmprestimosExcedidoException.class,
                () -> biblioteca.emprestimos().emprestar("T-4", ANA, SEGUNDA));

        assertEquals(StatusExemplar.DISPONIVEL, statusDe("L1", "T-4"));
        assertEquals(3, biblioteca.usuarios()
                .buscarUsuario(ANA).emprestimosAtivos());
    }

    @Test
    void devolverTomboInexistenteLancaRecursoNaoEncontrado() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.emprestimos().devolver("T-99", SEGUNDA));
    }

    @Test
    void devolverExemplarSemEmprestimoAtivoLancaOperacaoNaoPermitida() {
        assertThrows(OperacaoNaoPermitidaException.class,
                () -> biblioteca.emprestimos().devolver("T-1", SEGUNDA));
    }

    @Test
    void devolverAntesDaDataDoEmprestimoLancaDadosInvalidosSemResiduo()
            throws BibliotecaException {
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);

        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos()
                        .devolver("T-1", SEGUNDA.minusDays(1)));

        assertEquals(StatusExemplar.EMPRESTADO, statusDe("L1", "T-1"));
        assertEquals(1, biblioteca.usuarios()
                .buscarUsuario(ANA).emprestimosAtivos());
    }

    @Test
    void reservarComUsuarioInativoLancaUsuarioInativo()
            throws BibliotecaException {
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);
        biblioteca.usuarios().desativarUsuario(BRUNO);

        assertThrows(UsuarioInativoException.class,
                () -> biblioteca.emprestimos().reservar("L1", BRUNO, SEGUNDA));
    }

    @Test
    void reservarItemComExemplarDisponivelLancaOperacaoNaoPermitida() {
        assertThrows(OperacaoNaoPermitidaException.class,
                () -> biblioteca.emprestimos().reservar("L1", ANA, SEGUNDA));
    }

    @Test
    void reservarDuasVezesOMesmoItemLancaOperacaoNaoPermitida()
            throws BibliotecaException {
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);
        biblioteca.emprestimos().reservar("L1", BRUNO, SEGUNDA);

        assertThrows(OperacaoNaoPermitidaException.class,
                () -> biblioteca.emprestimos().reservar("L1", BRUNO, SEGUNDA));
        assertEquals(List.of(BRUNO), matriculasDaFila("L1"));
    }

    @Test
    void cancelarReservaInexistenteLancaRecursoNaoEncontrado() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.emprestimos().cancelarReserva("nao-existe"));
    }

    @Test
    void cancelarReservaJaCancelada() throws BibliotecaException {
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);
        String id = biblioteca.emprestimos().reservar("L1", BRUNO, SEGUNDA);
        biblioteca.emprestimos().cancelarReserva(id);

        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.emprestimos().cancelarReserva(id));
    }

    @Test
    void entradasNulasOuEmBrancoLancamDadosInvalidos() {
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().emprestar(null, ANA, SEGUNDA));
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().emprestar("T-1", " ", SEGUNDA));
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().emprestar("T-1", ANA, null));
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().devolver("", SEGUNDA));
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().listarEmprestimosDoUsuario(null));
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().listarReservas(" "));
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().cancelarReserva(null));
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().baixarExemplar(null));
    }

    // ---- prazo, atraso e listagens, com datas fixas ----

    @Test
    void emprestimoTemPrazoDeQuatorzeDias() throws BibliotecaException {
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);

        EmprestimoView emprestimo = biblioteca.emprestimos()
                .listarEmprestimosDoUsuario(ANA).get(0);

        assertEquals(LocalDate.of(2026, 11, 16),
                emprestimo.dataPrevistaDeDevolucao());
        assertNull(emprestimo.dataDaDevolucao());
    }

    @Test
    void noDiaPrevistoAindaNaoHaAtrasoENoDiaSeguinteHa()
            throws BibliotecaException {
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);

        assertEquals(0, biblioteca.emprestimos()
                .listarAtrasados(LocalDate.of(2026, 11, 16)).size());

        List<EmprestimoView> atrasados = biblioteca.emprestimos()
                .listarAtrasados(LocalDate.of(2026, 11, 17));
        assertEquals(1, atrasados.size());
        assertEquals(StatusEmprestimo.ATRASADO, atrasados.get(0).status());
    }

    @Test
    void listarAtrasadosComDataNulaLancaDadosInvalidos() {
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().listarAtrasados(null));
    }

    @Test
    void atrasadosVemDoMaisAntigoParaOMaisRecentePelaDataPrevista()
            throws BibliotecaException {
        biblioteca.acervo().adicionarExemplar("L1", "T-2");
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA.plusDays(2));
        biblioteca.emprestimos().emprestar("T-2", BRUNO, SEGUNDA);

        List<EmprestimoView> atrasados = biblioteca.emprestimos()
                .listarAtrasados(SEGUNDA.plusDays(30));

        assertEquals("T-2", atrasados.get(0).tombo());
        assertEquals("T-1", atrasados.get(1).tombo());
    }

    @Test
    void listagemDoUsuarioNuncaTemStatusAtrasado() throws BibliotecaException {
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);

        EmprestimoView emprestimo = biblioteca.emprestimos()
                .listarEmprestimosDoUsuario(ANA).get(0);

        assertEquals(StatusEmprestimo.ATIVO, emprestimo.status());
    }

    @Test
    void empateDeDataNaListagemTrazOUltimoRegistradoPrimeiro()
            throws BibliotecaException {
        biblioteca.acervo().adicionarExemplar("L1", "T-2");
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);
        biblioteca.emprestimos().emprestar("T-2", ANA, SEGUNDA);

        List<EmprestimoView> lista = biblioteca.emprestimos()
                .listarEmprestimosDoUsuario(ANA);

        assertEquals("T-2", lista.get(0).tombo());
        assertEquals("T-1", lista.get(1).tombo());
    }
}
