package br.ufpb.dcx.poo.biblioteca.contrato.excecoes;
public class DadosInvalidosException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;
    public DadosInvalidosException(String mensagem) { super(mensagem); }
}
