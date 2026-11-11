
package br.ufpb.dcx.poo.biblioteca.jogos;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusEmprestimo;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmprestimosDeJogos implements EmprestimoService {

    private final AcervoDeJogos acervo;
    private final DadosdoUsuario usuarios;

    private final List<Emprestimo> emprestimos = new ArrayList<>();
    private final List<ReservaView> reservas = new ArrayList<>();

    public EmprestimosDeJogos(AcervoDeJogos acervo,
                              DadosdoUsuario usuarios) {
        this.acervo = acervo;
        this.usuarios = usuarios;
    }

    @Override
    public String emprestar(String tombo, String matricula, LocalDate data)
            throws RecursoNaoEncontradoException,
            ExemplarIndisponivelException,
            UsuarioInativoException,
            LimiteDeEmprestimosExcedidoException {

        if (tombo == null || tombo.isBlank()
                || matricula == null || matricula.isBlank()
                || data == null) {
            throw new DadosInvalidosException(
                    "Dados do empréstimo inválidos.");
        }

        Exemplar exemplar = localizarExemplar(tombo);
        Usuario usuario = usuarios.localizar(matricula);

        if (!usuario.isAtivo()) {
            throw new UsuarioInativoException("Usuário inativo.");
        }

        if (usuario.getEmprestimosAtivos()
                >= EmprestimoService.LIMITE_DE_EMPRESTIMOS) {
            throw new LimiteDeEmprestimosExcedidoException(
                    "Limite de empréstimos atingido.");
        }

        if (exemplar.getStatus() != StatusExemplar.DISPONIVEL) {
            throw new ExemplarIndisponivelException(
                    "Exemplar indisponível.");
        }

        // Verifica se existe uma reserva anterior.
        List<ReservaView> fila = listarReservas(exemplar.getItem().getCodigo());

        if (!fila.isEmpty()
                && !fila.get(0).matricula().equals(matricula)) {
            throw new ExemplarIndisponivelException(
                    "O exemplar está reservado para outro usuário.");
        }

        String id = UUID.randomUUID().toString();

        Emprestimo emprestimo = new Emprestimo(
                id, exemplar, usuario, data);

        emprestimos.add(emprestimo);

        exemplar.setStatus(StatusExemplar.EMPRESTADO);
        usuario.registrarEmprestimo();

        // Remove a reserva do usuário atendido.
        if (!fila.isEmpty()) {
            reservas.removeIf(reserva ->
                    reserva.codigoDoItem().equals(
                            exemplar.getItem().getCodigo())
                            && reserva.matricula().equals(matricula));
            atualizarPosicoes(exemplar.getItem().getCodigo());
        }

        return id;
    }

    @Override
    public void devolver(String tombo, LocalDate data)
            throws RecursoNaoEncontradoException,
            OperacaoNaoPermitidaException {

        if (tombo == null || tombo.isBlank() || data == null) {
            throw new DadosInvalidosException(
                    "Dados da devolução inválidos.");
        }

        Emprestimo emprestimo = localizarEmprestimoAtivo(tombo);

        if (emprestimo == null) {
            throw new OperacaoNaoPermitidaException(
                    "Não existe empréstimo ativo para este exemplar.");
        }

        if (data.isBefore(emprestimo.getDataDoEmprestimo())) {
            throw new OperacaoNaoPermitidaException(
                    "A data de devolução não pode ser anterior ao empréstimo.");
        }

        emprestimo.registrarDevolucao(data);

        emprestimo.getExemplar().setStatus(
                StatusExemplar.DISPONIVEL);

        emprestimo.getUsuario().registrarDevolucao();
    }

    @Override
    public List<EmprestimoView> listarEmprestimosDoUsuario(
            String matricula) throws RecursoNaoEncontradoException {

        usuarios.localizar(matricula);

        List<EmprestimoView> resultado = new ArrayList<>();

        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getUsuario().getMatricula()
                    .equals(matricula)) {
                resultado.add(paraView(emprestimo, LocalDate.now()));
            }
        }

        return resultado;
    }

    @Override
    public List<EmprestimoView> listarAtrasados(LocalDate data) {

        List<EmprestimoView> resultado = new ArrayList<>();

        if (data == null) {
            return resultado;
        }

        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.estaAtrasadoEm(data)) {
                resultado.add(paraView(emprestimo, data));
            }
        }

        return resultado;
    }

    @Override
    public String reservar(String codigoDoItem, String matricula,
                           LocalDate data)
            throws RecursoNaoEncontradoException,
            OperacaoNaoPermitidaException {

        if (codigoDoItem == null || codigoDoItem.isBlank()
                || matricula == null || matricula.isBlank()
                || data == null) {
            throw new DadosInvalidosException(
                    "Dados da reserva inválidos.");
        }

        Jogo jogo = localizarJogo(codigoDoItem);
        Usuario usuario = usuarios.localizar(matricula);

        if (!usuario.isAtivo()) {
            throw new OperacaoNaoPermitidaException(
                    "Usuário inativo não pode reservar jogos.");
        }

        boolean existeDisponivel = jogo.getExemplares().stream()
                .anyMatch(exemplar ->
                        exemplar.getStatus() == StatusExemplar.DISPONIVEL);

        if (existeDisponivel) {
            throw new OperacaoNaoPermitidaException(
                    "Não é necessário reservar: existe exemplar disponível.");
        }

        boolean reservaExistente = reservas.stream()
                .anyMatch(reserva ->
                        reserva.codigoDoItem().equals(codigoDoItem)
                                && reserva.matricula().equals(matricula));

        if (reservaExistente) {
            throw new OperacaoNaoPermitidaException(
                    "Usuário já possui reserva para este jogo.");
        }

        int posicao = (int) reservas.stream()
                .filter(reserva ->
                        reserva.codigoDoItem().equals(codigoDoItem))
                .count() + 1;

        String id = UUID.randomUUID().toString();

        ReservaView reserva = new ReservaView(
                id,
                codigoDoItem,
                matricula,
                data,
                posicao
        );

        reservas.add(reserva);

        return id;
    }

    @Override
    public List<ReservaView> listarReservas(String codigoDoItem)
            throws RecursoNaoEncontradoException {

        localizarJogo(codigoDoItem);

        List<ReservaView> resultado = new ArrayList<>();

        for (ReservaView reserva : reservas) {
            if (reserva.codigoDoItem().equals(codigoDoItem)) {
                resultado.add(reserva);
            }
        }

        resultado.sort((a, b) ->
                Integer.compare(a.posicaoNaFila(), b.posicaoNaFila()));

        return resultado;
    }

    @Override
    public void cancelarReserva(String idDaReserva)
            throws RecursoNaoEncontradoException {

        if (idDaReserva == null || idDaReserva.isBlank()) {
            throw new DadosInvalidosException(
                    "Identificador da reserva inválido.");
        }

        ReservaView encontrada = null;

        for (ReservaView reserva : reservas) {
            if (reserva.id().equals(idDaReserva)) {
                encontrada = reserva;
                break;
            }
        }

        if (encontrada == null) {
            throw new RecursoNaoEncontradoException(
                    "Reserva não encontrada: " + idDaReserva);
        }

        String codigo = encontrada.codigoDoItem();

        reservas.remove(encontrada);

        atualizarPosicoes(codigo);
    }

    // Localiza um exemplar pelo tombo.
    private Exemplar localizarExemplar(String tombo)
            throws RecursoNaoEncontradoException {

        for (Jogo jogo : acervo.itens()) {
            for (Exemplar exemplar : jogo.getExemplares()) {
                if (exemplar.getTombo().equals(tombo)) {
                    return exemplar;
                }
            }
        }

        throw new RecursoNaoEncontradoException(
                "Exemplar não encontrado: " + tombo);
    }

    // Localiza um jogo pelo código.
    private Jogo localizarJogo(String codigo)
            throws RecursoNaoEncontradoException {

        for (Jogo jogo : acervo.itens()) {
            if (jogo.getCodigo().equals(codigo)) {
                return jogo;
            }
        }

        throw new RecursoNaoEncontradoException(
                "Jogo não encontrado: " + codigo);
    }

    // Localiza um empréstimo ainda não devolvido.
    private Emprestimo localizarEmprestimoAtivo(String tombo) {

        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getExemplar().getTombo().equals(tombo)
                    && emprestimo.estaAtivo()) {
                return emprestimo;
            }
        }

        return null;
    }

    // Converte Emprestimo para EmprestimoView.
    private EmprestimoView paraView(Emprestimo emprestimo,
                                    LocalDate data) {

        StatusEmprestimo status;

        if (!emprestimo.estaAtivo()) {
            status = StatusEmprestimo.DEVOLVIDO;
        } else if (emprestimo.estaAtrasadoEm(data)) {
            status = StatusEmprestimo.ATRASADO;
        } else {
            status = StatusEmprestimo.ATIVO;
        }

        return new EmprestimoView(
                emprestimo.getId(),
                emprestimo.getExemplar().getTombo(),
                emprestimo.getUsuario().getMatricula(),
                emprestimo.getDataDoEmprestimo(),
                emprestimo.getDataPrevistaDeDevolucao(),
                emprestimo.getDataDaDevolucao(),
                status
        );
    }

    // Atualiza as posições depois de um cancelamento ou atendimento.
    private void atualizarPosicoes(String codigoDoItem) {

        List<ReservaView> fila = new ArrayList<>();

        for (ReservaView reserva : reservas) {
            if (reserva.codigoDoItem().equals(codigoDoItem)) {
                fila.add(reserva);
            }
        }

        fila.sort((a, b) ->
                a.dataDaReserva().compareTo(b.dataDaReserva()));

        for (int i = 0; i < fila.size(); i++) {
            ReservaView antiga = fila.get(i);

            reservas.remove(antiga);

            reservas.add(new ReservaView(
                    antiga.id(),
                    antiga.codigoDoItem(),
                    antiga.matricula(),
                    antiga.dataDaReserva(),
                    i + 1
            ));
        }
    }
}
