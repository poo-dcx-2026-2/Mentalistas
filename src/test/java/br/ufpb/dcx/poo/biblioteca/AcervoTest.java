package br.ufpb.dcx.poo.biblioteca;

import java.time.LocalDate;
import java.util.List;

import br.ufpb.dcx.poo.biblioteca.jogos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.ExemplarIndisponivelException;
import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes públicos do acervo.
 *
 * <p>Os testes ativos passam com o código que veio no repositório. Os marcados com
 * {@code @Disabled} cobrem o que você ainda precisa implementar: remova a anotação
 * quando for implementar cada método.</p>
 *
 * <p>Estes testes são um piso, não um teto. A avaliação considera a suíte que
 * <em>você</em> escreve — cenários, casos-limite e regras de negócio que estes aqui
 * não cobrem.</p>
 */
class AcervoTest {

    private Biblioteca biblioteca;

    @BeforeEach
    void criarBibliotecaVazia() {
        biblioteca = Fabrica.novaBiblioteca();
    }

    @Test
    @DisplayName("um jogo cadastrado pode ser recuperado pelo código")
    void cadastrarEBuscar() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);

        ItemView item = biblioteca.acervo().buscarItem("L1");

        assertEquals("L1", item.codigo());
        assertEquals("Java Efetivo", item.titulo());
        assertEquals("Bloch", item.autoria());
        assertEquals(2019, item.ano());
    }

    @Test
    @DisplayName("jogo novo começa sem exemplares")
    void itemNovoNaoTemExemplares() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);

        ItemView item = biblioteca.acervo().buscarItem("L1");

        assertEquals(0, item.totalDeExemplares());
        assertEquals(0, item.exemplaresDisponiveis());
    }

    @Test
    @DisplayName("código repetido é recusado")
    void codigoDuplicado() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);

        assertThrows(RecursoDuplicadoException.class,
                () -> biblioteca.acervo().cadastrarItem("L1", "Outro", "Outra", "livro", 2020));
    }

    @Test
    @DisplayName("buscar jogo inexistente lança RecursoNaoEncontradoException")
    void buscarInexistente() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.acervo().buscarItem("NAO-EXISTE"));
    }

    @Test
    @DisplayName("código em branco é entrada inválida, não regra de negócio")
    void codigoEmBranco() {
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().cadastrarItem("  ", "Título", "Autoria", "livro", 2019));
    }

    @Test
    @DisplayName("listar devolve os jogos ordenados por título")
    void listarOrdenado() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Refatoração", "Fowler", "livro", 2004);
        biblioteca.acervo().cadastrarItem("L2", "Código limpo", "Martin", "livro", 2009);
        biblioteca.acervo().cadastrarItem("L3", "Java Efetivo", "Bloch", "livro", 2019);

        List<ItemView> itens = biblioteca.acervo().listarItens();

        assertEquals(3, itens.size());
        assertEquals("Código limpo", itens.get(0).titulo());
        assertEquals("Java Efetivo", itens.get(1).titulo());
        assertEquals("Refatoração", itens.get(2).titulo());
    }

    @Test
    @DisplayName("acervo vazio devolve lista vazia, não null")
    void acervoVazio() {
        assertEquals(List.of(), biblioteca.acervo().listarItens());
    }

    // ------------------------------------------------------------------
    // A implementar na Entrega 1. Remova @Disabled ao implementar cada um.
    // ------------------------------------------------------------------

    @Test
    @DisplayName("exemplar adicionado entra como DISPONIVEL e conta no jogo")
    void adicionarExemplar() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);
        biblioteca.acervo().adicionarExemplar("L1", "T-001");
        biblioteca.acervo().adicionarExemplar("L1", "T-002");

        ItemView item = biblioteca.acervo().buscarItem("L1");
        assertEquals(2, item.totalDeExemplares());
        assertEquals(2, item.exemplaresDisponiveis());

        assertEquals(StatusExemplar.DISPONIVEL,
                biblioteca.acervo().listarExemplares("L1").get(0).status());
    }

    @Test
    @DisplayName("tombo é único no acervo inteiro, não apenas dentro do item")
    void tomboDuplicadoEntreItensDiferentes() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);
        biblioteca.acervo().cadastrarItem("L2", "Refatoração", "Fowler", "livro", 2004);
        biblioteca.acervo().adicionarExemplar("L1", "T-001");

        assertThrows(RecursoDuplicadoException.class,
                () -> biblioteca.acervo().adicionarExemplar("L2", "T-001"));
    }

    @Test
    @DisplayName("não se adiciona exemplar a item que não existe")
    void exemplarDeItemInexistente() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.acervo().adicionarExemplar("NAO-EXISTE", "T-001"));
    }

    @Test
    void buscarPorTitulo() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);
        biblioteca.acervo().cadastrarItem("L2", "Refatoração", "Fowler", "livro", 2004);

        assertEquals(1, biblioteca.acervo().buscarPorTitulo("efetivo").size());
        assertEquals(1, biblioteca.acervo().buscarPorTitulo("JAVA").size());
    }

    @Test
    @DisplayName("busca sem resultado devolve lista vazia, não exceção")
    void buscarPorTituloSemResultado() {
        assertEquals(List.of(), biblioteca.acervo().buscarPorTitulo("inexistente"));
    }

    @Test
    @DisplayName("não permite cadastrar item com ano de publicação futuro")
    void naoPermiteAnoFuturo() {

        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().cadastrarItem(
                        "L4", "Spider-man", "Autor", "Jogo", 2030));
    }

    @Test
    @DisplayName("não permite empréstimo quando o usuário é menor que a classificação")
    void idadeInsuficiente() throws Exception {
        Locadora locadora = new Locadora();

        AcervoDeJogos acervo = (AcervoDeJogos) locadora.acervo();
        DadosdoUsuario usuarios = (DadosdoUsuario) locadora.usuarios();

        acervo.cadastrarItem("J1", "God of War", "Santa Monica", "Ação", 2022);
        acervo.adicionarExemplar("J1", "T001");
        acervo.definirClassificacaoIndicativa("J1", 18);

        usuarios.cadastrarUsuario("U1", "João");
        usuarios.definirIdade("U1", 16);

        assertThrows(IllegalArgumentException.class,
                () -> locadora.emprestarComVerificacaoDeIdade(
                        "T001", "U1", LocalDate.of(2026, 1, 10)
                )
        );
    }

    @Test
    @DisplayName("permite empréstimo quando a idade é igual à classificação indicativa")
    void idadeIgualClassificacao() throws Exception {
        Locadora locadora = new Locadora();

        AcervoDeJogos acervo = (AcervoDeJogos) locadora.acervo();
        DadosdoUsuario usuarios = (DadosdoUsuario) locadora.usuarios();

        // Cadastra o jogo e seu exemplar
        acervo.cadastrarItem("J1", "God of War", "Santa Monica", "Ação", 2022);
        acervo.adicionarExemplar("J1", "T001");

        // Define a classificação indicativa
        acervo.definirClassificacaoIndicativa("J1", 16);

        // Cadastra o usuário com idade igual à classificação
        usuarios.cadastrarUsuario("U1", "João");
        usuarios.definirIdade("U1", 16);

        // Verifica se o empréstimo é permitido
        assertDoesNotThrow(() ->
                locadora.emprestarComVerificacaoDeIdade(
                        "T001",
                        "U1",
                        LocalDate.of(2026, 1, 10)
                )
        );
    }

    @Test
    @DisplayName("permite empréstimo quando a idade é maior que a classificação")
    void idadeMaiorClassificacao() throws Exception {
        Locadora locadora = new Locadora();

        AcervoDeJogos acervo = (AcervoDeJogos) locadora.acervo();
        DadosdoUsuario usuarios = (DadosdoUsuario) locadora.usuarios();

        acervo.cadastrarItem("J1", "God of War", "Santa Monica", "Ação", 2022);
        acervo.adicionarExemplar("J1", "T001");
        acervo.definirClassificacaoIndicativa("J1", 12);

        usuarios.cadastrarUsuario("U1", "João");
        usuarios.definirIdade("U1", 18);

        assertDoesNotThrow(() ->
                locadora.emprestarComVerificacaoDeIdade(
                        "T001", "U1", LocalDate.of(2026, 1, 10)
                )
        );
    }

    @Test
    @DisplayName("mantém o exemplar disponível quando a idade é insuficiente")
    void exemplarContinuaDisponivel() throws Exception {
        Locadora locadora = new Locadora();

        AcervoDeJogos acervo = (AcervoDeJogos) locadora.acervo();
        DadosdoUsuario usuarios = (DadosdoUsuario) locadora.usuarios();

        acervo.cadastrarItem("J1", "God of War", "Santa Monica", "Ação", 2022);
        acervo.adicionarExemplar("J1", "T001");
        acervo.definirClassificacaoIndicativa("J1", 18);

        usuarios.cadastrarUsuario("U1", "João");
        usuarios.definirIdade("U1", 15);

        assertThrows(IllegalArgumentException.class,
                () -> locadora.emprestarComVerificacaoDeIdade(
                        "T001", "U1", LocalDate.of(2026, 1, 10)
                )
        );

        assertEquals(
                StatusExemplar.DISPONIVEL,
                acervo.listarExemplares("J1").get(0).status()
        );
    }

    @Test
    @DisplayName("não permite definir idade negativa")
    void idadeNegativa() {
        Usuario usuario = new Usuario("U1", "João");

        assertThrows(IllegalArgumentException.class,
                () -> usuario.definirIdade(-1)
        );
    }

    @Test
    @DisplayName("não permite classificação indicativa inválida")
    void classificacaoInvalida() {
        Jogo jogo = new Jogo(
                "J1", "God of War", "Santa Monica", "Ação", 2022
        );

        assertThrows(IllegalArgumentException.class,
                () -> jogo.definirClassificacaoIndicativa(15)
        );
    }

    @Test
    @DisplayName("não permite definir idade para usuário inexistente")
    void usuarioInexistente() {
        DadosdoUsuario usuarios = new DadosdoUsuario();

        assertThrows(RecursoNaoEncontradoException.class,
                () -> usuarios.definirIdade("U99", 18)
        );
    }

    @Test
    @DisplayName("não permite adicionar exemplar com tombo em branco")
    void tomboEmBranco() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem(
                "L1", "Java Efetivo", "Bloch", "livro", 2019);

        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().adicionarExemplar("L1", "  "));
    }

    @Test
    @DisplayName("não permite adicionar dois exemplares com o mesmo tombo")
    void tomboDuplicadoNoMesmoItem() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem(
                "L1", "Java Efetivo", "Bloch", "livro", 2019);

        biblioteca.acervo().adicionarExemplar("L1", "T001");

        assertThrows(RecursoDuplicadoException.class,
                () -> biblioteca.acervo().adicionarExemplar("L1", "T001"));
    }

    @Test
    @DisplayName("baixa de exemplar disponível altera seu status")
    void baixarExemplarDisponivel() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem(
                "L1", "Java Efetivo", "Bloch", "livro", 2019);

        biblioteca.acervo().adicionarExemplar("L1", "T001");
        biblioteca.acervo().adicionarExemplar("L1", "T002");

        biblioteca.acervo().baixarExemplar("T001");

        ItemView item = biblioteca.acervo().buscarItem("L1");

        assertEquals(2, item.totalDeExemplares());
        assertEquals(1, item.exemplaresDisponiveis());

        assertEquals(StatusExemplar.INDISPONIVEL,
                biblioteca.acervo().listarExemplares("L1")
                        .get(0).status());
    }

    @Test
    @DisplayName("não permite baixar exemplar que está emprestado")
    void baixarExemplarEmprestado() throws Exception {
        Locadora locadora = new Locadora();

        locadora.acervo().cadastrarItem(
                "J1", "God of War", "Santa Monica", "Jogo", 2022);

        locadora.acervo().adicionarExemplar("J1", "T001");
        locadora.usuarios().cadastrarUsuario("U1", "João");

        locadora.emprestimos().emprestar(
                "T001", "U1", LocalDate.of(2026, 1, 10));

        assertThrows(OperacaoNaoPermitidaException.class,
                () -> locadora.acervo().baixarExemplar("T001"));
    }

    @Test
    @DisplayName("busca por título retorna todos os jogos correspondentes")
    void buscarPorTituloRetornaVariosResultados() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem(
                "L1", "Java Efetivo", "Bloch", "livro", 2019);

        biblioteca.acervo().cadastrarItem(
                "L2", "Java Básico", "Autor", "livro", 2020);

        biblioteca.acervo().cadastrarItem(
                "L3", "Código Limpo", "Martin", "livro", 2009);

        List<ItemView> resultado =
                biblioteca.acervo().buscarPorTitulo("Java");

        assertEquals(2, resultado.size());
    }

    @Test
    void buscarPorCategoriaIgnoraMaiusculasEMinusculas() throws Exception {
        AcervoDeJogos acervo = new AcervoDeJogos();

        acervo.cadastrarItem("J1", "Minecraft", "Mojang", "Aventura", 2011);
        acervo.cadastrarItem("J2", "FIFA", "EA", "Esporte", 2024);

        List<ItemView> resultado = acervo.buscarPorCategoria("AVENTURA");

        assertEquals(1, resultado.size());
        assertEquals("Minecraft", resultado.get(0).titulo());
    }

    @Test
    void buscarPorCategoriaOrdenaPorTitulo() throws Exception {
        AcervoDeJogos acervo = new AcervoDeJogos();

        acervo.cadastrarItem("J1", "Zelda", "Nintendo", "Aventura", 2020);
        acervo.cadastrarItem("J2", "Assassin's Creed", "Ubisoft", "Aventura", 2022);
        acervo.cadastrarItem("J3", "Minecraft", "Mojang", "Aventura", 2011);

        List<ItemView> resultado = acervo.buscarPorCategoria("Aventura");

        assertEquals("Assassin's Creed", resultado.get(0).titulo());
        assertEquals("Minecraft", resultado.get(1).titulo());
        assertEquals("Zelda", resultado.get(2).titulo());
    }

    @Test
    void buscarPorCategoriaSemResultadosRetornaListaVazia() {
        AcervoDeJogos acervo = new AcervoDeJogos();

        List<ItemView> resultado = acervo.buscarPorCategoria("Estratégia");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorCategoriaVaziaLancaExcecao() {
        AcervoDeJogos acervo = new AcervoDeJogos();

        assertThrows(DadosInvalidosException.class,
                () -> acervo.buscarPorCategoria(" "));
    }


    @Test
    @DisplayName("devolver exemplar altera seu status para DISPONIVEL")
    void devolverExemplar() throws Exception {
        Locadora locadora = new Locadora();

        locadora.acervo().cadastrarItem(
                "J1", "God of War", "Santa Monica", "Jogo", 2022);

        locadora.acervo().adicionarExemplar("J1", "T001");
        locadora.usuarios().cadastrarUsuario("U1", "João");

        LocalDate dataEmprestimo = LocalDate.of(2026, 1, 10);
        LocalDate dataDevolucao = LocalDate.of(2026, 1, 15);

        locadora.emprestimos().emprestar(
                "T001", "U1", dataEmprestimo);

        locadora.emprestimos().devolver(
                "T001", dataDevolucao);

        assertEquals(
                StatusExemplar.DISPONIVEL,
                locadora.acervo().listarExemplares("J1").get(0).status());
    }

    @Test
    @DisplayName("não permite devolver com data anterior ao empréstimo")
    void devolverComDataAnterior() throws Exception {
        Locadora locadora = new Locadora();

        locadora.acervo().cadastrarItem(
                "J1", "God of War", "Santa Monica", "Jogo", 2022);

        locadora.acervo().adicionarExemplar("J1", "T001");
        locadora.usuarios().cadastrarUsuario("U1", "João");

        locadora.emprestimos().emprestar(
                "T001", "U1", LocalDate.of(2026, 1, 10));

        assertThrows(DadosInvalidosException.class,
                () -> locadora.emprestimos().devolver(
                        "T001", LocalDate.of(2026, 1, 5)));
    }

    @Test
    @DisplayName("não permite devolver com data nula")
    void devolverComDataNula() throws Exception {
        Locadora locadora = new Locadora();

        locadora.acervo().cadastrarItem(
                "J1", "God of War", "Santa Monica", "Jogo", 2022);

        locadora.acervo().adicionarExemplar("J1", "T001");
        locadora.usuarios().cadastrarUsuario("U1", "João");

        locadora.emprestimos().emprestar(
                "T001", "U1", LocalDate.of(2026, 1, 10));

        assertThrows(DadosInvalidosException.class,
                () -> locadora.emprestimos().devolver("T001", null));
    }

    @Test
    @DisplayName("não permite devolver exemplar sem empréstimo ativo")
    void devolverSemEmprestimoAtivo() {
        Locadora locadora = new Locadora();

        assertThrows(OperacaoNaoPermitidaException.class,
                () -> locadora.emprestimos().devolver(
                        "T001", LocalDate.of(2026, 1, 15)));
    }

    @Test
    @DisplayName("primeiro usuário da fila de reservas tem prioridade")
    void primeiroUsuarioDaFilaTemPrioridade() throws Exception {
        Locadora locadora = new Locadora();

        locadora.acervo().cadastrarItem(
                "J1", "God of War", "Santa Monica", "Jogo", 2022);

        locadora.acervo().adicionarExemplar("J1", "T001");

        locadora.usuarios().cadastrarUsuario("U0", "Carlos");
        locadora.usuarios().cadastrarUsuario("U1", "João");
        locadora.usuarios().cadastrarUsuario("U2", "Maria");

        LocalDate data = LocalDate.of(2026, 1, 10);

        // Carlos pega o exemplar.
        locadora.emprestimos().emprestar("T001", "U0", data);

        // João e Maria entram na fila de reservas.
        locadora.emprestimos().reservar("J1", "U1", data);
        locadora.emprestimos().reservar("J1", "U2", data.plusDays(1));

        // Carlos devolve o exemplar.
        locadora.emprestimos().devolver("T001", data.plusDays(2));

        // João, primeiro da fila, consegue pegar o exemplar.
        assertDoesNotThrow(() ->
                locadora.emprestimos().emprestar(
                        "T001", "U1", data.plusDays(3)));
    }

    @Test
    @DisplayName("usuário que não é o primeiro da fila não pode pegar o exemplar")
    void usuarioForaDaOrdemNaoPodeEmprestar() throws Exception {
        Locadora locadora = new Locadora();

        locadora.acervo().cadastrarItem(
                "J1", "God of War", "Santa Monica", "Jogo", 2022);

        locadora.acervo().adicionarExemplar("J1", "T001");

        locadora.usuarios().cadastrarUsuario("U0", "Carlos");
        locadora.usuarios().cadastrarUsuario("U1", "João");
        locadora.usuarios().cadastrarUsuario("U2", "Maria");

        LocalDate data = LocalDate.of(2026, 1, 10);

        locadora.emprestimos().emprestar("T001", "U0", data);

        locadora.emprestimos().reservar("J1", "U1", data);
        locadora.emprestimos().reservar("J1", "U2", data.plusDays(1));

        locadora.emprestimos().devolver("T001", data.plusDays(2));

        // Maria tenta pegar o exemplar antes de João.
        assertThrows(ExemplarIndisponivelException.class,
                () -> locadora.emprestimos().emprestar(
                        "T001", "U2", data.plusDays(3)));
    }

    @Test
    @DisplayName("reserva é removida após o usuário realizar o empréstimo")
    void reservaRemovidaAposEmprestimo() throws Exception {
        Locadora locadora = new Locadora();

        locadora.acervo().cadastrarItem(
                "J1", "God of War", "Santa Monica", "Jogo", 2022);

        locadora.acervo().adicionarExemplar("J1", "T001");

        locadora.usuarios().cadastrarUsuario("U0", "Carlos");
        locadora.usuarios().cadastrarUsuario("U1", "João");

        LocalDate data = LocalDate.of(2026, 1, 10);

        locadora.emprestimos().emprestar("T001", "U0", data);

        locadora.emprestimos().reservar("J1", "U1", data);

        locadora.emprestimos().devolver("T001", data.plusDays(1));

        locadora.emprestimos().emprestar(
                "T001", "U1", data.plusDays(2));

        assertTrue(
                locadora.emprestimos().listarReservas("J1").isEmpty());
    }


    @Test
    @DisplayName("histórico de empréstimos aparece do mais recente para o mais antigo")
    void historicoEmprestimosOrdenado() throws Exception {
        Locadora locadora = new Locadora();

        locadora.acervo().cadastrarItem(
                "J1", "God of War", "Santa Monica", "Jogo", 2022);

        locadora.acervo().adicionarExemplar("J1", "T001");
        locadora.acervo().adicionarExemplar("J1", "T002");

        locadora.usuarios().cadastrarUsuario("U1", "João");

        // Registra dois empréstimos em datas diferentes.
        locadora.emprestimos().emprestar(
                "T001", "U1", LocalDate.of(2026, 1, 10));

        locadora.emprestimos().emprestar(
                "T002", "U1", LocalDate.of(2026, 1, 15));

        List<br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView> historico =
                locadora.emprestimos().listarEmprestimosDoUsuario("U1");

        assertEquals(2, historico.size());

        // O empréstimo de 15/01 deve aparecer primeiro.
        assertEquals(
                LocalDate.of(2026, 1, 15),
                historico.get(0).dataDoEmprestimo());

        // O empréstimo de 10/01 deve aparecer depois.
        assertEquals(
                LocalDate.of(2026, 1, 10),
                historico.get(1).dataDoEmprestimo());
    }







}
