package br.ufrgs.musicatexto;

public class ConfiguracaoInicial {
    private final int bpmInicial;
    private final int volumeVoz0;
    private final int oitavaVoz0;
    private final Instrumento instrumentoVoz0;

    public ConfiguracaoInicial(int bpmInicial, int volumeVoz0, int oitavaVoz0, Instrumento instrumentoVoz0) {
        this.bpmInicial = bpmInicial;
        this.volumeVoz0 = volumeVoz0;
        this.oitavaVoz0 = oitavaVoz0;
        this.instrumentoVoz0 = instrumentoVoz0;
    }

    public int getBpmInicial() { return bpmInicial; }
    public int getVolumeVoz0() { return volumeVoz0; }
    public int getOitavaVoz0() { return oitavaVoz0; }
    public Instrumento getInstrumentoVoz0() { return instrumentoVoz0; }
}