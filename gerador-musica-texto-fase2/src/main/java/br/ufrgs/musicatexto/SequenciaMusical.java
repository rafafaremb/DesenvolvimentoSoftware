package br.ufrgs.musicatexto;

import java.util.ArrayList;
import java.util.List;

public class SequenciaMusical {
    private final List<Voz> vozes = new ArrayList<>();
    private int bpmInicial;

    public SequenciaMusical(int bpmInicial) {
        this.bpmInicial = bpmInicial;
    }

    public void adicionarVoz(Voz voz) {
        vozes.add(voz);
    }

    public void limparSequencia() {
        vozes.clear();
    }

    public List<Voz> getVozes() {
        return vozes;
    }

    public int getBpmInicial() {
        return bpmInicial;
    }

    public void setBpmInicial(int bpmInicial) {
        this.bpmInicial = Math.max(20, Math.min(300, bpmInicial));
    }
}