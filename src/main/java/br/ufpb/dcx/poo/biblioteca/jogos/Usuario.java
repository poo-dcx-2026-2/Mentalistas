package br.ufpb.dcx.poo.biblioteca.jogos;

public class Usuario {

    private final String matricula;
    private final String nome;
    private boolean ativo;
    private int emprestimosAtivos;
    private int idade;

    public Usuario(String matricula, String nome) {
        this.matricula = matricula;
        this.nome = nome;
        this.ativo = true;
        this.emprestimosAtivos = 0;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public int getEmprestimosAtivos() {
        return emprestimosAtivos;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void reativar() {
        this.ativo = true;
    }

    void registrarEmprestimo() {
        emprestimosAtivos++;
    }

    void registrarDevolucao() {
        if (emprestimosAtivos > 0) {
            emprestimosAtivos--;
        }
    }

    public int getIdade() {
        return idade;
    }

    public void definirIdade(int idade) {
        if (idade < 0) {
            throw new IllegalArgumentException("Idade inválida.");
        }

        this.idade = idade;
    }
}
