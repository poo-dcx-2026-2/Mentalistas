package br.ufpb.dcx.poo.biblioteca.contrato.excecoes;
public class UsuarioInativoException extends OperacaoNaoPermitidaException {

    private static final long serialVersionUID = 1L;
    public UsuarioInativoException(String mensagem) { super(mensagem); }
}
