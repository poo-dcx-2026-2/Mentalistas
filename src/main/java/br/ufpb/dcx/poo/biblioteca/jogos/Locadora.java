package br.ufpb.dcx.poo.biblioteca.jogos;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.RelatorioService;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;
import br.ufpb.dcx.poo.biblioteca.inicial.AcervoEmMemoria;
import br.ufpb.dcx.poo.biblioteca.inicial.EmprestimosNaoImplementados;
import br.ufpb.dcx.poo.biblioteca.inicial.RelatoriosNaoImplementados;
import br.ufpb.dcx.poo.biblioteca.inicial.UsuariosEmMemoria;



public class Locadora implements Biblioteca {

    private final AcervoEmMemoria acervo = new AcervoEmMemoria();
    private final UsuariosEmMemoria usuarios = new UsuariosEmMemoria();
    private final EmprestimosNaoImplementados emprestimos = new EmprestimosNaoImplementados();
    private final RelatoriosNaoImplementados relatorios = new RelatoriosNaoImplementados();

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

}