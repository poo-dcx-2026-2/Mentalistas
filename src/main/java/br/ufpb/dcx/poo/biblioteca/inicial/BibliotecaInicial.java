package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.RelatorioService;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;

/**
 * Reúne os quatro serviços em um único ponto de acesso.
 *
 * <p>Guarde esta classe na memória: na Aula 13 você vai descobrir que ela tem nome
 * e que a decisão de existir ou não tem consequências.</p>
 */
public class BibliotecaInicial implements Biblioteca {

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
        throw new UnsupportedOperationException("Entrega 2: implementar salvar");
    }

    @Override
    public void carregar() throws PersistenciaException {
        throw new UnsupportedOperationException("Entrega 2: implementar carregar");
    }
}
