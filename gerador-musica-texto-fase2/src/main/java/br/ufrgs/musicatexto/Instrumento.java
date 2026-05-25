package br.ufrgs.musicatexto;

public class Instrumento {
    private String nome;
    private int codigoMIDI;

    public Instrumento(String nome, int codigoMIDI) {
        alterarInstrumento(nome, codigoMIDI);
    }

    public void alterarInstrumento(String nome, int codigoMIDI) {
        if (codigoMIDI < 0 || codigoMIDI > 127) {
            throw new IllegalArgumentException("Código MIDI deve estar entre 0 e 127.");
        }
        this.nome = nome;
        this.codigoMIDI = codigoMIDI;
    }

    public void alterarInstrumento(int codigoMIDI) {
        alterarInstrumento("GM " + codigoMIDI, codigoMIDI);
    }

    public String getNome() {
        return nome;
    }

    public int getCodigoMIDI() {
        return codigoMIDI;
    }

    @Override
    public String toString() {
        return nome + " (GM " + codigoMIDI + ")";
    }
}
