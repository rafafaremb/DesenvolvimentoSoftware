package br.ufrgs.musicatexto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Casos de teste conforme seção 5 do documento da Fase 2.
 */
public class MapeadorTextoMusicalTest {

    private MapeadorTextoMusical mapeador;

    @BeforeEach
    void setUp() {
        mapeador = new MapeadorTextoMusical();
    }

    /**
     * CT01 - Validação de Vozes e Polifonia:
     * Múltiplas linhas geram vozes independentes com oitavas e instrumentos corretos.
     */
    @Test
    void ct01_vozesPolfonia() {
        SequenciaMusical seq = mapeador.interpretarTexto("CDEF\nGABC", 120);

        assertEquals(2, seq.getVozes().size());

        Voz voz0 = seq.getVozes().get(0);
        assertEquals(6, voz0.getOitavaBase());
        assertEquals("Piano", voz0.getInstrumentoAtual().getNome());

        Voz voz1 = seq.getVozes().get(1);
        assertEquals(5, voz1.getOitavaBase());
        assertEquals("Órgão", voz1.getInstrumentoAtual().getNome());
    }

    /**
     * CT02 - Validação de Atraso:
     * "[4] C D E" → atraso de 4 beats; espaço após ] não dobra volume.
     */
    @Test
    void ct02_atrasoInicial() {
        SequenciaMusical seq = mapeador.interpretarTexto("[4] C D E", 120);

        Voz voz = seq.getVozes().get(0);
        assertEquals(4, voz.getAtrasoEntrada());

        // Volume deve permanecer 100 (espaço após ] não conta como dobrar volume)
        List<Nota> eventos = voz.getEventos();
        assertFalse(eventos.isEmpty());
        assertEquals(100, eventos.get(0).getVolume());
    }

    /**
     * CT03 - Validação de Lookahead (Mi Bemol):
     * "Mb M b" → Mi Bemol, Mi (M isolado → repete ou pausa), pausa (b minúsculo).
     */
    @Test
    void ct03_lookaheadMiBemol() {
        Voz voz = new Voz(0);
        mapeador.mapearSequencia("Mb", voz);

        List<Nota> eventos = voz.getEventos();
        assertEquals(1, eventos.size());
        assertFalse(eventos.get(0).isPausa());
        assertEquals("Mi Bemol", eventos.get(0).getNome());
    }

    @Test
    void ct03_bMinusculoIsoladoGeraPausa() {
        Voz voz = new Voz(0);
        mapeador.mapearSequencia("b", voz);

        List<Nota> eventos = voz.getEventos();
        assertEquals(1, eventos.size());
        assertTrue(eventos.get(0).isPausa());
    }

    /**
     * CT04 - Escopo local de oitavas:
     * Linha 1: "C ? D" → Ré na oitava 7; Linha 2: "E F" → oitava base 5.
     */
    @Test
    void ct04_oitavaLocal() {
        SequenciaMusical seq = mapeador.interpretarTexto("C ? D\nE F", 120);

        Voz voz0 = seq.getVozes().get(0);
        List<Nota> eventos0 = voz0.getEventos();
        // C na oitava 6, depois ? sobe para 7, D na oitava 7
        Nota re = eventos0.get(1); // índice 1 = D (depois de ?)
        assertEquals(7, re.getOitava());

        Voz voz1 = seq.getVozes().get(1);
        List<Nota> eventos1 = voz1.getEventos();
        // E e F devem estar na oitava base 5
        assertEquals(5, eventos1.get(0).getOitava()); // Mi
        assertEquals(5, eventos1.get(1).getOitava()); // Fá
    }

    /**
     * Testa RF14: ? no máximo (oitava 9) permanece em 9.
     */
    @Test
    void rf14_oitavaMaximaPermaneceEm9() {
        Voz voz = new Voz(0);
        // Sobe até 9
        for (int i = 0; i < 10; i++) voz.aumentarOitava();
        assertEquals(9, voz.getOitavaAtual());
    }

    /**
     * Testa RF15: V no mínimo (oitava 0) permanece em 0.
     */
    @Test
    void rf15_oitavaMinimaPermaneceEm0() {
        Voz voz = new Voz(0);
        for (int i = 0; i < 20; i++) voz.diminuirOitava();
        assertEquals(0, voz.getOitavaAtual());
    }

    /**
     * Testa RF13: espaço dobra volume com cap em 127.
     */
    @Test
    void rf13_espacoDobrarVolumeComCap() {
        Voz voz = new Voz(0); // volume inicial 100
        voz.dobrarVolume(); // 127 (cap)
        assertEquals(127, voz.getVolumeAtual());
    }

    /**
     * Testa RF19: ; → Tubular Bells GM 15.
     */
    @Test
    void rf19_pontoVirgulaTubularBells() {
        Voz voz = new Voz(0);
        mapeador.mapearSequencia(";", voz);
        assertEquals(15, voz.getInstrumentoAtual().getCodigoMIDI());
    }

    /**
     * Testa RF20: , → Church Organ GM 19.
     */
    @Test
    void rf20_virgulaChurchOrgan() {
        Voz voz = new Voz(0);
        mapeador.mapearSequencia(",", voz);
        assertEquals(19, voz.getInstrumentoAtual().getCodigoMIDI());
    }

    /**
     * Testa RF11: instrumentos base corretos por voz.
     */
    @Test
    void rf11_instrumentosBasePorVoz() {
        assertEquals(0,  new Voz(0).getInstrumentoAtual().getCodigoMIDI()); // Piano
        assertEquals(20, new Voz(1).getInstrumentoAtual().getCodigoMIDI()); // Órgão
        assertEquals(6,  new Voz(2).getInstrumentoAtual().getCodigoMIDI()); // Cravo
        assertEquals(71, new Voz(3).getInstrumentoAtual().getCodigoMIDI()); // Fagote
    }
}
