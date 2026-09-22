package br.ufpb.dcx.poo.biblioteca.inicial;

import java.time.LocalDate;
import java.util.List;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.ExemplarIndisponivelException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.LimiteDeEmprestimosExcedidoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;

/**
 * Esqueleto dos empréstimos. Conteúdo da Entrega 2.
 *
 * <p>As regras estão no enunciado da entrega, não aqui. Repare que
 * {@link EmprestimoService#PRAZO_EM_DIAS} e
 * {@link EmprestimoService#LIMITE_DE_EMPRESTIMOS} são constantes do contrato: os
 * testes contam com esses valores.</p>
 */
public class EmprestimosNaoImplementados implements EmprestimoService {

    @Override
    public String emprestar(String tombo, String matricula, LocalDate data)
            throws RecursoNaoEncontradoException, ExemplarIndisponivelException,
                   UsuarioInativoException, LimiteDeEmprestimosExcedidoException {
        throw new UnsupportedOperationException("Entrega 2: implementar emprestar");
    }

    @Override
    public void devolver(String tombo, LocalDate data)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {
        throw new UnsupportedOperationException("Entrega 2: implementar devolver");
    }

    @Override
    public List<EmprestimoView> listarEmprestimosDoUsuario(String matricula)
            throws RecursoNaoEncontradoException {
        throw new UnsupportedOperationException("Entrega 2: implementar listarEmprestimosDoUsuario");
    }

    @Override
    public List<EmprestimoView> listarAtrasados(LocalDate data) {
        throw new UnsupportedOperationException("Entrega 2: implementar listarAtrasados");
    }

    @Override
    public String reservar(String codigoDoItem, String matricula, LocalDate data)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {
        throw new UnsupportedOperationException("Entrega 2: implementar reservar");
    }

    @Override
    public List<ReservaView> listarReservas(String codigoDoItem)
            throws RecursoNaoEncontradoException {
        throw new UnsupportedOperationException("Entrega 2: implementar listarReservas");
    }

    @Override
    public void cancelarReserva(String idDaReserva) throws RecursoNaoEncontradoException {
        throw new UnsupportedOperationException("Entrega 2: implementar cancelarReserva");
    }
}
