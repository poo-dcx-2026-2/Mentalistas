package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.ExemplarIndisponivelException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** A tabela Ana / Bruno / Carla do passo 13 do guia, passo a passo. */
class CicloDeReservaTest extends TesteDeBiblioteca {

    @Test
    void cicloCompletoDeReservaComUmUnicoExemplar() throws BibliotecaException {
        // Ana empresta T-1
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);
        assertEquals(StatusExemplar.EMPRESTADO, statusDe("L1", "T-1"));
        assertTrue(matriculasDaFila("L1").isEmpty());

        // Bruno reserva L1
        biblioteca.emprestimos().reservar("L1", BRUNO, SEGUNDA);
        assertEquals(List.of(BRUNO), matriculasDaFila("L1"));
        assertEquals(StatusExemplar.EMPRESTADO, statusDe("L1", "T-1"));

        // Carla reserva L1
        String idDaCarla = biblioteca.emprestimos()
                .reservar("L1", CARLA, SEGUNDA);
        assertEquals(List.of(BRUNO, CARLA), matriculasDaFila("L1"));

        List<ReservaView> fila = biblioteca.emprestimos().listarReservas("L1");
        assertEquals(1, fila.get(0).posicaoNaFila());
        assertEquals(2, fila.get(1).posicaoNaFila());

        // Ana devolve T-1: fica RESERVADO para Bruno
        biblioteca.emprestimos().devolver("T-1", SEGUNDA.plusDays(3));
        assertEquals(StatusExemplar.RESERVADO, statusDe("L1", "T-1"));
        assertEquals(List.of(BRUNO, CARLA), matriculasDaFila("L1"));

        // Carla tenta emprestar T-1: recusado
        assertThrows(ExemplarIndisponivelException.class,
                () -> biblioteca.emprestimos()
                        .emprestar("T-1", CARLA, SEGUNDA.plusDays(4)));
        assertEquals(StatusExemplar.RESERVADO, statusDe("L1", "T-1"));

        // Bruno empresta T-1: a reserva dele é atendida e sai da fila
        biblioteca.emprestimos().emprestar("T-1", BRUNO, SEGUNDA.plusDays(4));
        assertEquals(StatusExemplar.EMPRESTADO, statusDe("L1", "T-1"));
        assertEquals(List.of(CARLA), matriculasDaFila("L1"));
        assertEquals(1, biblioteca.emprestimos()
                .listarReservas("L1").get(0).posicaoNaFila());

        // Bruno devolve T-1: fica RESERVADO para Carla
        biblioteca.emprestimos().devolver("T-1", SEGUNDA.plusDays(6));
        assertEquals(StatusExemplar.RESERVADO, statusDe("L1", "T-1"));

        // Carla cancela a reserva: fila vazia e T-1 DISPONIVEL
        biblioteca.emprestimos().cancelarReserva(idDaCarla);
        assertTrue(matriculasDaFila("L1").isEmpty());
        assertEquals(StatusExemplar.DISPONIVEL, statusDe("L1", "T-1"));
    }

    @Test
    void cancelarReservaComExemplarSeparadoPassaOExemplarParaAProxima()
            throws BibliotecaException {
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);
        String idDoBruno = biblioteca.emprestimos()
                .reservar("L1", BRUNO, SEGUNDA);
        biblioteca.emprestimos().reservar("L1", CARLA, SEGUNDA);
        biblioteca.emprestimos().devolver("T-1", SEGUNDA.plusDays(1));

        biblioteca.emprestimos().cancelarReserva(idDoBruno);

        assertEquals(List.of(CARLA), matriculasDaFila("L1"));
        assertEquals(StatusExemplar.RESERVADO, statusDe("L1", "T-1"));

        // e agora é Carla quem pode levar
        assertDoesNotThrow(() -> biblioteca.emprestimos()
                .emprestar("T-1", CARLA, SEGUNDA.plusDays(2)));
    }

    @Test
    void baixarExemplarReservadoFazAReservaVoltarAEsperar()
            throws BibliotecaException {
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);
        biblioteca.emprestimos().reservar("L1", BRUNO, SEGUNDA);
        biblioteca.emprestimos().devolver("T-1", SEGUNDA.plusDays(1));
        assertEquals(StatusExemplar.RESERVADO, statusDe("L1", "T-1"));

        biblioteca.acervo().baixarExemplar("T-1");

        assertEquals(StatusExemplar.INDISPONIVEL, statusDe("L1", "T-1"));
        assertEquals(List.of(BRUNO), matriculasDaFila("L1"));

        // volta um exemplar novo: é DISPONIVEL (simplificação da §3.3)
        // e a reserva continua na fila esperando
        biblioteca.acervo().adicionarExemplar("L1", "T-2");
        assertEquals(StatusExemplar.DISPONIVEL, statusDe("L1", "T-2"));
    }

    @Test
    void exemplarNovoEmItemComFilaPodeSerEmprestadoACualquerUsuario()
            throws BibliotecaException {
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);
        biblioteca.emprestimos().reservar("L1", BRUNO, SEGUNDA);

        biblioteca.acervo().adicionarExemplar("L1", "T-2");

        assertDoesNotThrow(() -> biblioteca.emprestimos()
                .emprestar("T-2", CARLA, SEGUNDA));
    }

    @Test
    void duasReservasNoMesmoDiaMantemAOrdemDeChegada()
            throws BibliotecaException {
        biblioteca.emprestimos().emprestar("T-1", ANA, SEGUNDA);
        biblioteca.emprestimos().reservar("L1", CARLA, SEGUNDA);
        biblioteca.emprestimos().reservar("L1", BRUNO, SEGUNDA);

        assertEquals(List.of(CARLA, BRUNO), matriculasDaFila("L1"));

        biblioteca.emprestimos().devolver("T-1", SEGUNDA);
        assertThrows(ExemplarIndisponivelException.class,
                () -> biblioteca.emprestimos().emprestar("T-1", BRUNO, SEGUNDA));
    }
}
