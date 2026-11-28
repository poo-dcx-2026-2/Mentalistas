package br.ufpb.dcx.poo.biblioteca.jogos;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.RelatorioService;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;
import br.ufpb.dcx.poo.biblioteca.inicial.RelatoriosNaoImplementados;
import java.time.LocalDate;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.ExemplarIndisponivelException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.LimiteDeEmprestimosExcedidoException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;


public class Locadora implements Biblioteca {
    private static final String PROPRIEDADE_DADOS = "biblioteca.dados";

    private Path getPastaDados() {
        String caminho = System.getProperty(PROPRIEDADE_DADOS, "dados");
        return Path.of(caminho);
    }

    private AcervoDeJogos acervo = new AcervoDeJogos();
    private DadosdoUsuario usuarios = new DadosdoUsuario();
    private final RelatoriosNaoImplementados relatorios =
            new RelatoriosNaoImplementados();
    private EmprestimosDeJogos emprestimo =
            new EmprestimosDeJogos(acervo, usuarios);

    @Override
    public AcervoService acervo() { return acervo; }

    @Override
    public UsuarioService usuarios() { return usuarios; }

    @Override
    public EmprestimoService emprestimos() { return emprestimo; }

    @Override
    public RelatorioService relatorios() { return relatorios; }

    @Override
    public void salvar() throws PersistenciaException {
        try {
            Path pastaDados = getPastaDados();
            Files.createDirectories(pastaDados);


            // Salvar jogos
            List<String> linhasJogos = new ArrayList<>();

            for (Jogo jogo : acervo.itens()) {
                linhasJogos.add(
                        codificar(jogo.getCodigo()) + ";" +
                                codificar(jogo.getTitulo()) + ";" +
                                codificar(jogo.getAutoria()) + ";" +
                                codificar(jogo.getCategoria()) + ";" +
                                jogo.getAno() + ";" +
                                jogo.getClassificacaoIndicativa()
                );
            }

            Files.write(
                    pastaDados.resolve("jogos.txt"),
                    linhasJogos,
                    StandardCharsets.UTF_8
            );

            // Salvar exemplares
            List<String> linhasExemplares = new ArrayList<>();

            for (Jogo jogo : acervo.itens()) {
                for (Exemplar exemplar : jogo.getExemplares()) {
                    linhasExemplares.add(
                            codificar(jogo.getCodigo()) + ";" +
                                    codificar(exemplar.getTombo()) + ";" +
                                    exemplar.getStatus().name()
                    );
                }
            }

            Files.write(
                    pastaDados.resolve("exemplares.txt"),
                    linhasExemplares,
                    StandardCharsets.UTF_8
            );

            // Salvar usuários
            List<String> linhasUsuarios = new ArrayList<>();

            for (Usuario usuario : usuarios.listarTodosUsuarios()) {
                linhasUsuarios.add(
                        codificar(usuario.getMatricula()) + ";" +
                                codificar(usuario.getNome()) + ";" +
                                usuario.getIdade() + ";" +
                                usuario.isAtivo()
                );
            }

            Files.write(
                    pastaDados.resolve("usuarios.txt"),
                    linhasUsuarios,
                    StandardCharsets.UTF_8
            );

            // Salvar empréstimos
            List<String> linhasEmprestimos = new ArrayList<>();

            for (Emprestimo e : emprestimo.listarTodosEmprestimos()) {
                String devolucao = e.getDataDaDevolucao() == null
                        ? "-"
                        : e.getDataDaDevolucao().toString();

                linhasEmprestimos.add(
                        codificar(e.getId()) + ";" +
                                codificar(e.getExemplar().getTombo()) + ";" +
                                codificar(e.getUsuario().getMatricula()) + ";" +
                                e.getDataDoEmprestimo() + ";" +
                                devolucao
                );
            }

            Files.write(
                    pastaDados.resolve("emprestimos.txt"),
                    linhasEmprestimos,
                    StandardCharsets.UTF_8
            );

            // Salvar reservas
            List<String> linhasReservas = new ArrayList<>();

            for (Reserva r : emprestimo.listarTodasReservas()) {
                // A posição na fila é a ordem das linhas; o tombo do
                // exemplar separado é "-" quando a reserva ainda espera.
                String tomboSeparado = r.temExemplarSeparado()
                        ? codificar(r.getExemplarSeparado().getTombo())
                        : "-";

                linhasReservas.add(
                        codificar(r.getId()) + ";" +
                                codificar(r.getJogo().getCodigo()) + ";" +
                                codificar(r.getUsuario().getMatricula()) + ";" +
                                r.getData() + ";" +
                                tomboSeparado
                );
            }

            Files.write(
                    pastaDados.resolve("reservas.txt"),
                    linhasReservas,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new PersistenciaException(
                    "Erro ao salvar os dados: " + e.getMessage()
            );
        }
    }

    @Override
    public void carregar() throws PersistenciaException {
        Path pastaDados = getPastaDados();

        // Se a pasta não existir, inicia com estado vazio.
        if (!Files.exists(pastaDados)) {
            acervo = new AcervoDeJogos();
            usuarios = new DadosdoUsuario();
            emprestimo = new EmprestimosDeJogos(acervo, usuarios);
            return;
        }

        // Carrega primeiro em uma locadora temporária.
        Locadora temporaria = new Locadora();

        temporaria.carregarDados(pastaDados);

        // Só substitui os dados atuais se tudo der certo.
        this.acervo = temporaria.acervo;
        this.usuarios = temporaria.usuarios;
        this.emprestimo = temporaria.emprestimo;

    }
    private void carregarDados(Path pastaDados)
            throws PersistenciaException {

        try {
            // Recuperar jogos
            for (String linha : lerArquivo("jogos.txt", pastaDados)) {
                String[] dados = linha.split(";", -1);

                acervo.restaurarJogo(
                        decodificar(dados[0]),
                        decodificar(dados[1]),
                        decodificar(dados[2]),
                        decodificar(dados[3]),
                        Integer.parseInt(dados[4]),
                        Integer.parseInt(dados[5])
                );
            }

            // Recuperar exemplares
            for (String linha : lerArquivo("exemplares.txt", pastaDados)) {
                String[] dados = linha.split(";", -1);

                acervo.restaurarExemplar(
                        decodificar(dados[0]),
                        decodificar(dados[1]),
                        StatusExemplar.valueOf(dados[2])
                );
            }

            // Recuperar usuários
            for (String linha : lerArquivo("usuarios.txt", pastaDados)) {
                String[] dados = linha.split(";", -1);

                usuarios.restaurarUsuario(
                        decodificar(dados[0]),
                        decodificar(dados[1]),
                        Integer.parseInt(dados[2]),
                        Boolean.parseBoolean(dados[3])
                );
            }

            // Recuperar empréstimos
            for (String linha : lerArquivo("emprestimos.txt", pastaDados)) {
                String[] dados = linha.split(";", -1);

                LocalDate devolucao = dados[4].equals("-")
                        ? null
                        : LocalDate.parse(dados[4]);

                emprestimo.restaurarEmprestimo(
                        decodificar(dados[0]),
                        decodificar(dados[1]),
                        decodificar(dados[2]),
                        LocalDate.parse(dados[3]),
                        devolucao
                );
            }

            // Recuperar reservas
            for (String linha : lerArquivo("reservas.txt", pastaDados)) {
                String[] dados = linha.split(";", -1);

                emprestimo.restaurarReserva(
                        decodificar(dados[0]),
                        decodificar(dados[1]),
                        decodificar(dados[2]),
                        LocalDate.parse(dados[3]),
                        dados[4].equals("-") ? null : decodificar(dados[4])
                );
            }

        } catch (IOException | RecursoNaoEncontradoException
                 | RuntimeException e) {

            throw new PersistenciaException(
                    "Erro ao carregar os dados: " + e.getMessage()
            );
        }
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

    private String codificar(String texto) {
        return Base64.getEncoder().encodeToString(
                texto.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String decodificar(String texto) {
        return new String(
                Base64.getDecoder().decode(texto),
                StandardCharsets.UTF_8
        );
    }

    private List<String> lerArquivo(String nome, Path pastaDados)
            throws IOException {

        Path arquivo = pastaDados.resolve(nome);

        if (!Files.exists(arquivo)) {
            return new ArrayList<>();
        }

        return Files.readAllLines(
                arquivo,
                StandardCharsets.UTF_8
        );
    }
}