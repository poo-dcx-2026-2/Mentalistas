package br.ufpb.dcx.poo.biblioteca.jogos;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusEmprestimo;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EmprestimosDeJogos implements EmprestimoService {

    private final AcervoDeJogos acervo;
    private final DadosdoUsuario usuarios;

    private final List<Emprestimo> emprestimos = new ArrayList<>();

    public EmprestimosDeJogos(AcervoDeJogos acervo,
                              DadosdoUsuario usuarios) {
        this.acervo = acervo;
        this.usuarios = usuarios;
    }

    // ------------------------------------------------------------------
    // Empréstimo e devolução
    // ------------------------------------------------------------------

    @Override
    public String emprestar(String tombo, String matricula, LocalDate data)
            throws RecursoNaoEncontradoException,
            ExemplarIndisponivelException,
            UsuarioInativoException,
            LimiteDeEmprestimosExcedidoException {

        exigirTexto(tombo, "tombo");
        exigirTexto(matricula, "matrícula");
        exigirData(data);

        Exemplar exemplar = localizarExemplar(tombo);
        Usuario usuario = usuarios.localizar(matricula);

        if (!usuario.isAtivo()) {
            throw new UsuarioInativoException(
                    "Usuário inativo: " + matricula);
        }

        if (!exemplar.podeSerEmprestadoPara(usuario)) {
            throw new ExemplarIndisponivelException(
                    "Exemplar indisponível: " + tombo);
        }

        if (usuario.getEmprestimosAtivos()
                >= EmprestimoService.LIMITE_DE_EMPRESTIMOS) {
            throw new LimiteDeEmprestimosExcedidoException(
                    "Limite de empréstimos atingido.");
        }

        // Tudo validado: só agora o estado muda.
        String id = UUID.randomUUID().toString();
        Emprestimo emprestimo = new Emprestimo(id, exemplar, usuario, data);

        atenderReservaDo(exemplar);

        emprestimos.add(emprestimo);
        exemplar.setStatus(StatusExemplar.EMPRESTADO);
        usuario.registrarEmprestimo();

        return id;
    }

    @Override
    public void devolver(String tombo, LocalDate data)
            throws RecursoNaoEncontradoException,
            OperacaoNaoPermitidaException {

        exigirTexto(tombo, "tombo");
        exigirData(data);

        Exemplar exemplar = localizarExemplar(tombo);
        Emprestimo emprestimo = localizarEmprestimoAtivo(tombo);

        if (emprestimo == null) {
            throw new OperacaoNaoPermitidaException(
                    "Não existe empréstimo ativo para este exemplar.");
        }

        // Lança DadosInvalidosException (data anterior) antes de mudar algo.
        emprestimo.registrarDevolucao(data);
        emprestimo.getUsuario().registrarDevolucao();

        destinarExemplarLiberado(exemplar);
    }

    @Override
    public List<EmprestimoView> listarEmprestimosDoUsuario(
            String matricula) throws RecursoNaoEncontradoException {

        exigirTexto(matricula, "matrícula");
        usuarios.localizar(matricula);

        // Percorre do último para o primeiro registrado; o sort é estável,
        // então no empate de data o registrado por último vem primeiro.
        List<Emprestimo> doUsuario = new ArrayList<>();
        for (int i = emprestimos.size() - 1; i >= 0; i--) {
            Emprestimo emprestimo = emprestimos.get(i);
            if (emprestimo.getUsuario().getMatricula().equals(matricula)) {
                doUsuario.add(emprestimo);
            }
        }

        return doUsuario.stream()
                .sorted(Comparator.comparing(
                        Emprestimo::getDataDoEmprestimo).reversed())
                .map(this::paraViewSemAtraso)
                .toList();
    }

    @Override
    public List<EmprestimoView> listarAtrasados(LocalDate data) {

        exigirData(data);

        return emprestimos.stream()
                .filter(emprestimo -> emprestimo.estaAtrasadoEm(data))
                .sorted(Comparator.comparing(
                        Emprestimo::getDataPrevistaDeDevolucao))
                .map(emprestimo -> paraView(
                        emprestimo, StatusEmprestimo.ATRASADO))
                .toList();
    }

    // ------------------------------------------------------------------
    // Reservas
    // ------------------------------------------------------------------

    @Override
    public String reservar(String codigoDoItem, String matricula,
                           LocalDate data)
            throws RecursoNaoEncontradoException,
            OperacaoNaoPermitidaException {

        exigirTexto(codigoDoItem, "código do item");
        exigirTexto(matricula, "matrícula");
        exigirData(data);

        Jogo jogo = localizarJogo(codigoDoItem);
        Usuario usuario = usuarios.localizar(matricula);

        if (!usuario.isAtivo()) {
            throw new UsuarioInativoException(
                    "Usuário inativo não pode reservar jogos.");
        }

        if (jogo.temExemplarDisponivel()) {
            throw new OperacaoNaoPermitidaException(
                    "Não é necessário reservar: existe exemplar disponível.");
        }

        if (jogo.getFila().temReservaDe(matricula)) {
            throw new OperacaoNaoPermitidaException(
                    "Usuário já possui reserva para este jogo.");
        }

        String id = UUID.randomUUID().toString();
        jogo.getFila().entrar(new Reserva(id, jogo, usuario, data));

        return id;
    }

    @Override
    public List<ReservaView> listarReservas(String codigoDoItem)
            throws RecursoNaoEncontradoException {

        exigirTexto(codigoDoItem, "código do item");

        Jogo jogo = localizarJogo(codigoDoItem);
        FilaDeReservas fila = jogo.getFila();

        return fila.emOrdem().stream()
                .map(reserva -> new ReservaView(
                        reserva.getId(),
                        jogo.getCodigo(),
                        reserva.getUsuario().getMatricula(),
                        reserva.getData(),
                        fila.posicaoDe(reserva)))
                .toList();
    }

    @Override
    public void cancelarReserva(String idDaReserva)
            throws RecursoNaoEncontradoException {

        exigirTexto(idDaReserva, "id da reserva");

        for (Jogo jogo : acervo.itens()) {
            Optional<Reserva> reserva = jogo.getFila().buscar(idDaReserva);

            if (reserva.isPresent()) {
                jogo.getFila().remover(reserva.get());

                Exemplar separado = reserva.get().liberarExemplar();
                if (separado != null) {
                    destinarExemplarLiberado(separado);
                }
                return;
            }
        }

        throw new RecursoNaoEncontradoException(
                "Reserva não encontrada: " + idDaReserva);
    }

    // ------------------------------------------------------------------
    // Regras de reserva compartilhadas
    // ------------------------------------------------------------------

    /**
     * Exemplar que ficou livre (devolução ou reserva cancelada): vai para a
     * reserva mais antiga ainda sem exemplar, ou volta a DISPONIVEL.
     */
    private void destinarExemplarLiberado(Exemplar exemplar) {
        exemplar.getItem().getFila().primeiraSemExemplar().ifPresentOrElse(
                reserva -> reserva.separar(exemplar),
                () -> exemplar.setStatus(StatusExemplar.DISPONIVEL));
    }

    /** Se o exemplar estava separado para uma reserva, ela foi atendida. */
    private void atenderReservaDo(Exemplar exemplar) {
        Reserva reserva = exemplar.getReservaDona();

        if (reserva != null) {
            reserva.liberarExemplar();
            exemplar.getItem().getFila().remover(reserva);
        }
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    private static void exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(
                    "O campo " + campo + " é obrigatório.");
        }
    }

    private static void exigirData(LocalDate data) {
        if (data == null) {
            throw new DadosInvalidosException("A data é obrigatória.");
        }
    }

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

    private Emprestimo localizarEmprestimoAtivo(String tombo) {
        return emprestimos.stream()
                .filter(e -> e.estaAtivo()
                        && e.getExemplar().getTombo().equals(tombo))
                .findFirst()
                .orElse(null);
    }

    /** Na listagem do usuário o status é só ATIVO ou DEVOLVIDO. */
    private EmprestimoView paraViewSemAtraso(Emprestimo emprestimo) {
        return paraView(emprestimo, emprestimo.estaAtivo()
                ? StatusEmprestimo.ATIVO
                : StatusEmprestimo.DEVOLVIDO);
    }

    private EmprestimoView paraView(Emprestimo emprestimo,
                                    StatusEmprestimo status) {
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

    // ------------------------------------------------------------------
    // Uso interno da persistência (pacote)
    // ------------------------------------------------------------------

    void restaurarEmprestimo(String id, String tombo,
                             String matricula,
                             LocalDate dataEmprestimo,
                             LocalDate dataDevolucao)
            throws RecursoNaoEncontradoException {

        Exemplar exemplar = localizarExemplar(tombo);
        Usuario usuario = usuarios.localizar(matricula);

        Emprestimo novo = new Emprestimo(
                id, exemplar, usuario, dataEmprestimo);

        if (dataDevolucao != null) {
            novo.registrarDevolucao(dataDevolucao);
        }

        emprestimos.add(novo);

        if (novo.estaAtivo()) {
            exemplar.setStatus(StatusExemplar.EMPRESTADO);
            usuario.registrarEmprestimo();
        }
    }

    /**
     * @param tomboSeparado tombo do exemplar separado, ou null se a reserva
     *                      ainda espera exemplar
     */
    void restaurarReserva(String id, String codigoDoItem,
                          String matricula, LocalDate data,
                          String tomboSeparado)
            throws RecursoNaoEncontradoException {

        Jogo jogo = localizarJogo(codigoDoItem);
        Usuario usuario = usuarios.localizar(matricula);

        Reserva reserva = new Reserva(id, jogo, usuario, data);
        jogo.getFila().entrar(reserva);

        if (tomboSeparado != null) {
            Exemplar exemplar = localizarExemplar(tomboSeparado);

            if (exemplar.getItem() != jogo
                    || exemplar.getReservaDona() != null) {
                throw new DadosInvalidosException(
                        "Exemplar separado inválido para a reserva " + id);
            }
            reserva.separar(exemplar);
        }
    }

    List<Emprestimo> listarTodosEmprestimos() {
        return new ArrayList<>(emprestimos);
    }

    /** Todas as reservas, jogo a jogo, cada fila na ordem de chegada. */
    List<Reserva> listarTodasReservas() {
        List<Reserva> todas = new ArrayList<>();
        for (Jogo jogo : acervo.itens()) {
            todas.addAll(jogo.getFila().emOrdem());
        }
        return todas;
    }
}
