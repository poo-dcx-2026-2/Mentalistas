package br.ufpb.dcx.poo.biblioteca.contrato.excecoes;
public class PersistenciaException extends BibliotecaException {

    private static final long serialVersionUID = 1L;
    public PersistenciaException(String mensagem) { super(mensagem); }
}
