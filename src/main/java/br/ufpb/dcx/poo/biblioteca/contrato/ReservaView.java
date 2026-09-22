package br.ufpb.dcx.poo.biblioteca.contrato;
import java.time.LocalDate;
public record ReservaView(String id, String codigoDoItem, String matricula,
                          LocalDate dataDaReserva, int posicaoNaFila) { }
