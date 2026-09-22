package br.ufpb.dcx.poo.biblioteca.contrato;
import java.time.LocalDate;
import java.util.List;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.*;
public interface EmprestimoService {
    int LIMITE_DE_EMPRESTIMOS = 3;
    int PRAZO_EM_DIAS = 14;
    String emprestar(String tombo, String matricula, LocalDate data)
            throws RecursoNaoEncontradoException, ExemplarIndisponivelException,
                   UsuarioInativoException, LimiteDeEmprestimosExcedidoException;
    void devolver(String tombo, LocalDate data)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException;
    List<EmprestimoView> listarEmprestimosDoUsuario(String matricula)
            throws RecursoNaoEncontradoException;
    List<EmprestimoView> listarAtrasados(LocalDate data);
    String reservar(String codigoDoItem, String matricula, LocalDate data)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException;
    List<ReservaView> listarReservas(String codigoDoItem) throws RecursoNaoEncontradoException;
    void cancelarReserva(String idDaReserva) throws RecursoNaoEncontradoException;
}
