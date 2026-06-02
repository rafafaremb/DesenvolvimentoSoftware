package br.ufrgs.musicatexto;

import java.util.ArrayList;
import java.util.List;

public class Voz {
    private static final int[] OITAVAS_BASE = {6, 5, 4, 3};
    private static final int[] VOLUMES_BASE = {100, 80, 60, 40};
    private static final Instrumento[] INSTRUMENTOS_BASE = {
            new Instrumento("Piano", 0),
            new Instrumento("Órgão", 20),
            new Instrumento("Cravo", 6),
            new Instrumento("Fagote", 71)
    };

    private final int indice;
    private final int oitavaBase;
    private int oitavaAtual;
    private int volumeAtual;
    private Instrumento instrumentoAtual;
    private int atrasoEntrada;
    private final List<Nota> eventos = new ArrayList<>();
    private Nota ultimaNota;
    private boolean ultimoCaractereEraNota;

    public Voz(int indice) {
        this.indice = indice;
        int pos = indice % OITAVAS_BASE.length;
        this.oitavaBase = OITAVAS_BASE[pos];
        this.oitavaAtual = this.oitavaBase;
        this.volumeAtual = VOLUMES_BASE[pos];
        this.instrumentoAtual = new Instrumento(
                INSTRUMENTOS_BASE[pos].getNome(),
                INSTRUMENTOS_BASE[pos].getCodigoMIDI()
        );
        this.atrasoEntrada = 0;
    }

    public void adicionarNota(Nota nota) {
        eventos.add(nota);
        if (!nota.isPausa()) {
            ultimaNota = nota;
            ultimoCaractereEraNota = true;
        } else {
            ultimoCaractereEraNota = false;
        }
    }

    public void adicionarPausa() {
        eventos.add(Nota.pausa(1));
        ultimoCaractereEraNota = false;
    }

    public void repetirUltimaNotaOuPausar() {
        if (ultimoCaractereEraNota && ultimaNota != null) {
            Nota repetida = Nota.musical(
                    ultimaNota.getNome(),
                    ultimaNota.getMidiNumber(),
                    ultimaNota.getOitava(),
                    ultimaNota.getDuracaoBeats(),
                    volumeAtual
            );
            adicionarNota(repetida);
        } else {
            adicionarPausa();
        }
    }

    public void aumentarOitava() {
        if (oitavaAtual < 9) {
            oitavaAtual++;
        } else {
            resetarOitava();
        }
    }

    public void diminuirOitava() {
        if (oitavaAtual > 0) {
            oitavaAtual--;
        }
    }

    public void dobrarVolume() {
        volumeAtual = Math.min(127, volumeAtual * 2);
    }

    public void resetarOitava() {
        oitavaAtual = oitavaBase;
    }

    public int getIndice() {
        return indice;
    }

    public int getOitavaBase() {
        return oitavaBase;
    }

    public int getOitavaAtual() {
        return oitavaAtual;
    }

    public int getVolumeAtual() {
        return volumeAtual;
    }

    public Instrumento getInstrumentoAtual() {
        return instrumentoAtual;
    }

    public void setInstrumentoAtual(Instrumento instrumentoAtual) {
        this.instrumentoAtual = instrumentoAtual;
    }

    public int getAtrasoEntrada() {
        return atrasoEntrada;
    }

    public void setAtrasoEntrada(int atrasoEntrada) {
        this.atrasoEntrada = Math.max(0, atrasoEntrada);
    }

    public List<Nota> getEventos() {
        return eventos;
    }
}
