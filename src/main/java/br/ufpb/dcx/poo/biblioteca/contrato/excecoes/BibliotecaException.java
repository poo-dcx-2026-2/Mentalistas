package br.ufpb.dcx.poo.biblioteca.contrato.excecoes;
public class BibliotecaException extends Exception {

    private static final long serialVersionUID = 1L;
    public BibliotecaException(String mensagem) { super(mensagem); }
    public BibliotecaException(String mensagem, Throwable causa) { super(mensagem, causa); }
}
