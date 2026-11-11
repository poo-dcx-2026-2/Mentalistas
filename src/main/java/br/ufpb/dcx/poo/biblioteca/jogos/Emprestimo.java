
package br.ufpb.dcx.poo.biblioteca.jogos;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

import java.time.LocalDate;

public class Emprestimo {

    private final String id;
    private final Exemplar exemplar;
    private final Usuario usuario;
    private final LocalDate dataDoEmprestimo;
    private final LocalDate dataPrevistaDeDevolucao;

    private LocalDate dataDaDevolucao;

    public Emprestimo(String id, Exemplar exemplar,
                      Usuario usuario, LocalDate dataDoEmprestimo) {

        if (id == null || id.isBlank()
                || exemplar == null
                || usuario == null
                || dataDoEmprestimo == null) {
            throw new DadosInvalidosException(
                    "Os dados do empréstimo são obrigatórios.");
        }

        this.id = id;
        this.exemplar = exemplar;
        this.usuario = usuario;
        this.dataDoEmprestimo = dataDoEmprestimo;

        this.dataPrevistaDeDevolucao =
                dataDoEmprestimo.plusDays(EmprestimoService.PRAZO_EM_DIAS);

        this.dataDaDevolucao = null;
    }

    public String getId() {
        return id;
    }

    public Exemplar getExemplar() {
        return exemplar;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDate getDataDoEmprestimo() {
        return dataDoEmprestimo;
    }

    public LocalDate getDataPrevistaDeDevolucao() {
        return dataPrevistaDeDevolucao;
    }

    public LocalDate getDataDaDevolucao() {
        return dataDaDevolucao;
    }

    public boolean estaAtivo() {
        return dataDaDevolucao == null;
    }

    public boolean estaAtrasadoEm(LocalDate data) {
        return estaAtivo()
                && data != null
                && data.isAfter(dataPrevistaDeDevolucao);
    }

    public void registrarDevolucao(LocalDate data) {

        if (!estaAtivo()) {
            throw new DadosInvalidosException(
                    "Este empréstimo já foi devolvido.");
        }

        if (data == null || data.isBefore(dataDoEmprestimo)) {
            throw new DadosInvalidosException(
                    "Data de devolução inválida.");
        }

        this.dataDaDevolucao = data;
    }
}
