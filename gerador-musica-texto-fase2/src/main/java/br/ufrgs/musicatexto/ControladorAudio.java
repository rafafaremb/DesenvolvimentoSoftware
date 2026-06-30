package br.ufrgs.musicatexto;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class ControladorAudio {
    private static final int PPQ = 480;
    private Sequencer sequencer;

    public Sequence criarSequenceMIDI(SequenciaMusical sequencia) throws InvalidMidiDataException {
        Sequence sequence = new Sequence(Sequence.PPQ, PPQ);

        Track trackGlobal = sequence.createTrack();
        adicionarEventoTempo(trackGlobal, 0, sequencia.getBpmInicial());

        int canal = 0;
        for (Voz voz : sequencia.getVozes()) {
            if (canal == 9) canal++; // canal 9 reservado para percussão

            Track track = sequence.createTrack();
            adicionarProgramChange(track, canal, voz.getInstrumentoAtual().getCodigoMIDI(), 0);

            long tickAtual = (long) voz.getAtrasoEntrada() * PPQ;

            for (Nota evento : voz.getEventos()) {
                long duracaoTicks = (long) evento.getDuracaoBeats() * PPQ;

                if (!evento.isPausa()) {
                    adicionarNota(track, canal, evento.getMidiNumber(), evento.getVolume(), tickAtual, tickAtual + duracaoTicks);
                }

                tickAtual += duracaoTicks;
            }

            canal = (canal + 1) % 16;
        }

        return sequence;
    }

    public void reproduzir(SequenciaMusical sequencia) throws MidiUnavailableException, InvalidMidiDataException {
        pararReproducao();

        Sequence sequence = criarSequenceMIDI(sequencia);
        sequencer = MidiSystem.getSequencer();

        if (sequencer == null) {
            throw new MidiUnavailableException("Sequenciador MIDI indisponível.");
        }

        sequencer.open();
        sequencer.setSequence(sequence);
        sequencer.start();
    }

    /** Pausa a reprodução preservando a posição atual (RF26). */
    public void pausarReproducao() {
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();
        }
    }

    /** Retoma a reprodução a partir do ponto pausado (RF26). */
    public void retomarReproducao() {
        if (sequencer != null && sequencer.isOpen() && !sequencer.isRunning()) {
            sequencer.start();
        }
    }

    /** Interrompe completamente e fecha o sequenciador. */
    public void pararReproducao() {
        if (sequencer != null) {
            if (sequencer.isRunning()) {
                sequencer.stop();
            }
            if (sequencer.isOpen()) {
                sequencer.close();
            }
        }
    }

    public boolean isReproduzindo() {
        return sequencer != null && sequencer.isRunning();
    }

    public boolean isPausado() {
        return sequencer != null && sequencer.isOpen() && !sequencer.isRunning();
    }

    public long getPosicaoAtual() {
        return sequencer != null ? sequencer.getTickPosition() : 0;
    }

    public long getDuracaoTotal() {
        return sequencer != null ? sequencer.getTickLength() : 1;
    }

    public void salvarMIDI(SequenciaMusical sequencia, File arquivo) throws InvalidMidiDataException, IOException {
        Sequence sequence = criarSequenceMIDI(sequencia);
        MidiSystem.write(sequence, 1, arquivo);
    }

    private void adicionarNota(Track track, int canal, int nota, int volume, long inicio, long fim)
            throws InvalidMidiDataException {
        ShortMessage noteOn = new ShortMessage();
        noteOn.setMessage(ShortMessage.NOTE_ON, canal, nota, volume);
        track.add(new MidiEvent(noteOn, inicio));

        ShortMessage noteOff = new ShortMessage();
        noteOff.setMessage(ShortMessage.NOTE_OFF, canal, nota, 0);
        track.add(new MidiEvent(noteOff, fim));
    }

    private void adicionarProgramChange(Track track, int canal, int instrumento, long tick)
            throws InvalidMidiDataException {
        ShortMessage msg = new ShortMessage();
        msg.setMessage(ShortMessage.PROGRAM_CHANGE, canal, instrumento, 0);
        track.add(new MidiEvent(msg, tick));
    }

    private void adicionarEventoTempo(Track track, long tick, int bpm) throws InvalidMidiDataException {
        int mpq = 60000000 / bpm;

        MetaMessage tempoMessage = new MetaMessage();
        byte[] data = new byte[] {
                (byte) ((mpq >> 16) & 0xFF),
                (byte) ((mpq >> 8) & 0xFF),
                (byte) (mpq & 0xFF)
        };

        tempoMessage.setMessage(0x51, data, data.length);
        track.add(new MidiEvent(tempoMessage, tick));
    }
}