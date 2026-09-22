package br.ufpb.dcx.poo.biblioteca.contrato.excecoes;
public class LimiteDeEmprestimosExcedidoException extends OperacaoNaoPermitidaException {

    private static final long serialVersionUID = 1L;
    public LimiteDeEmprestimosExcedidoException(String mensagem) { super(mensagem); }
}
