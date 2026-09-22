package br.ufpb.dcx.poo.biblioteca.contrato.excecoes;
public class RecursoDuplicadoException extends BibliotecaException {

    private static final long serialVersionUID = 1L;
    public RecursoDuplicadoException(String mensagem) { super(mensagem); }
}
