package br.ufrgs.musicatexto;

public class Nota {
    private final String nome;
    private final int midiNumber;
    private final int oitava;
    private final int duracaoBeats;
    private final int volume;
    private final boolean pausa;

    private Nota(String nome, int midiNumber, int oitava, int duracaoBeats, int volume, boolean pausa) {
        this.nome = nome;
        this.midiNumber = midiNumber;
        this.oitava = oitava;
        this.duracaoBeats = duracaoBeats;
        this.volume = volume;
        this.pausa = pausa;
    }

    public static Nota musical(String nome, int midiNumber, int oitava, int duracaoBeats, int volume) {
        return new Nota(nome, midiNumber, oitava, duracaoBeats, volume, false);
    }

    public static Nota pausa(int duracaoBeats) {
        return new Nota("Pausa", -1, -1, duracaoBeats, 0, true);
    }

    public String getNome() {
        return nome;
    }

    public int getMidiNumber() {
        return midiNumber;
    }

    public int getOitava() {
        return oitava;
    }

    public int getDuracaoBeats() {
        return duracaoBeats;
    }

    public int getVolume() {
        return volume;
    }

    public boolean isPausa() {
        return pausa;
    }
}