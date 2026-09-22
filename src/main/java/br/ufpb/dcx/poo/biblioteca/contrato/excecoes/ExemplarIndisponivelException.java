package br.ufpb.dcx.poo.biblioteca.contrato.excecoes;
public class ExemplarIndisponivelException extends OperacaoNaoPermitidaException {

    private static final long serialVersionUID = 1L;
    public ExemplarIndisponivelException(String mensagem) { super(mensagem); }
}
