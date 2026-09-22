package br.ufpb.dcx.poo.biblioteca.inicial;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.RelatorioService;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;

/**
 * Esqueleto dos relatórios. Conteúdo da Entrega 3.
 *
 * <p>Estes métodos são o lugar natural para Streams. E
 * {@link #importarItensEmLote(Path)} é onde o incremento concorrente da Entrega 3
 * tem motivo real para existir.</p>
 */
public class RelatoriosNaoImplementados implements RelatorioService {

    @Override
    public List<ItemView> itensMaisEmprestados(int n) {
        throw new UnsupportedOperationException("Entrega 3: implementar itensMaisEmprestados");
    }

    @Override
    public Map<String, Long> emprestimosPorCategoria() {
        throw new UnsupportedOperationException("Entrega 3: implementar emprestimosPorCategoria");
    }

    @Override
    public List<UsuarioView> usuariosComAtraso(LocalDate data) {
        throw new UnsupportedOperationException("Entrega 3: implementar usuariosComAtraso");
    }

    @Override
    public double taxaDeOcupacaoDoAcervo() {
        throw new UnsupportedOperationException("Entrega 3: implementar taxaDeOcupacaoDoAcervo");
    }

    @Override
    public int importarItensEmLote(Path arquivo) throws PersistenciaException {
        throw new UnsupportedOperationException("Entrega 3: implementar importarItensEmLote");
    }
}
