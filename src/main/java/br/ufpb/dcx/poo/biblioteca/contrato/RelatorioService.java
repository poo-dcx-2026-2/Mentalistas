package br.ufpb.dcx.poo.biblioteca.contrato;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;
public interface RelatorioService {
    List<ItemView> itensMaisEmprestados(int n);
    Map<String, Long> emprestimosPorCategoria();
    List<UsuarioView> usuariosComAtraso(LocalDate data);
    double taxaDeOcupacaoDoAcervo();
    int importarItensEmLote(Path arquivo) throws PersistenciaException;
}
