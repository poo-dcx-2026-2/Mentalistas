package br.ufpb.dcx.poo.biblioteca.contrato.excecoes;
public class OperacaoNaoPermitidaException extends BibliotecaException {

    private static final long serialVersionUID = 1L;
    public OperacaoNaoPermitidaException(String mensagem) { super(mensagem); }
}
