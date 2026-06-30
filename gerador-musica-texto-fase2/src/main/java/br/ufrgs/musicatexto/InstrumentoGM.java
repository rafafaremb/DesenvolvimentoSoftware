package br.ufrgs.musicatexto;

/**
 * Centraliza todos os instrumentos General MIDI usados no sistema.
 * Resolve o bad smell "cirurgia com espingarda": antes, adicionar ou
 * alterar um instrumento exigia mudanças em 4 classes diferentes.
 * Agora exige apenas um novo valor aqui.
 */
public enum InstrumentoGM {
    PIANO("Piano", 0),
    CRAVO("Cravo", 6),
    ORGAO("Órgão", 20),
    FAGOTE("Fagote", 71),
    HARMONICA("Harmônica", 22),
    TUBULAR_BELLS("Tubular Bells", 15),
    CHURCH_ORGAN("Church Organ", 19);

    private final String nome;
    private final int codigoMIDI;

    InstrumentoGM(String nome, int codigoMIDI) {
        this.nome = nome;
        this.codigoMIDI = codigoMIDI;
    }

    public String getNome() { return nome; }
    public int getCodigoMIDI() { return codigoMIDI; }

    public Instrumento criarInstancia() {
        return new Instrumento(nome, codigoMIDI);
    }

    /** Usado para popular o JComboBox da interface sem repetir strings. */
    public String getRotuloExibicao() {
        return nome + " (GM " + codigoMIDI + ")";
    }

    public static InstrumentoGM porRotulo(String rotulo) {
        for (InstrumentoGM i : values()) {
            if (i.getRotuloExibicao().equals(rotulo)) return i;
        }
        return PIANO;
    }
}