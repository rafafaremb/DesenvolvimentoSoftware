package br.ufrgs.musicatexto;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ControladorInterface {
    private final MapeadorTextoMusical mapeador = new MapeadorTextoMusical();
    private final ControladorAudio controladorAudio = new ControladorAudio();
    private final GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo();
    private SequenciaMusical ultimaSequencia;

    public SequenciaMusical gerarSequencia(String texto, ConfiguracaoInicial config) {
        ultimaSequencia = mapeador.interpretarTexto(texto, config);
        return ultimaSequencia;
    }

    public void iniciarReproducao() throws MidiUnavailableException, InvalidMidiDataException {
        if (ultimaSequencia == null) {
            throw new IllegalStateException("Nenhuma sequência gerada ainda.");
        }
        controladorAudio.reproduzir(ultimaSequencia);
    }

    public void pausarOuRetomar(boolean pausado) {
        if (pausado) controladorAudio.retomarReproducao();
        else controladorAudio.pausarReproducao();
    }

    public void parar() { controladorAudio.pararReproducao(); }
    public boolean isReproduzindo() { return controladorAudio.isReproduzindo(); }
    public boolean isPausado() { return controladorAudio.isPausado(); }
    public long getPosicaoAtual() { return controladorAudio.getPosicaoAtual(); }
    public long getDuracaoTotal() { return controladorAudio.getDuracaoTotal(); }
    public SequenciaMusical getUltimaSequencia() { return ultimaSequencia; }
    
    public void limparSequencia() { this.ultimaSequencia = null; }

    public String carregarTXT(File arquivo) throws IOException {
        return gerenciadorArquivo.lerArquivoTXT(arquivo);
    }

    public void salvarTexto(String texto) throws IOException {
        gerenciadorArquivo.salvarArquivoTXT(texto);
    }

    public void salvarMIDI(File arquivo) throws InvalidMidiDataException, IOException {
        if (ultimaSequencia == null) {
            throw new IllegalStateException("Nenhuma sequência gerada ainda.");
        }
        controladorAudio.salvarMIDI(ultimaSequencia, arquivo);
    }

    public String gerarNomeMIDIPadrao() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
        return "musica_" + LocalDateTime.now().format(fmt) + ".mid";
    }
}