package br.ufrgs.musicatexto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapeadorTextoMusical {
    private static final Pattern PADRAO_ATRASO = Pattern.compile("^\\s*\\[(\\d+)]\\s");

    private int bpmAtual;

    public SequenciaMusical interpretarTexto(String texto, int bpmInicial, int volumeVoz0, int oitavaVoz0, int instrumentoVoz0) {
        bpmAtual = Math.max(20, Math.min(300, bpmInicial));
        SequenciaMusical sequencia = new SequenciaMusical(bpmAtual);
        String[] linhas = texto.split("\\R");
        int indiceVoz = 0;
        for (String linha : linhas) {
            if (linha == null || linha.trim().isEmpty()) continue;

            Voz voz;
            if (indiceVoz == 0) {
                voz = new Voz(0, volumeVoz0, oitavaVoz0,
                        new Instrumento(obterNomeInstrumento(instrumentoVoz0), instrumentoVoz0));
            } else {
                voz = new Voz(indiceVoz);
            }

            processarLinha(linha, voz);
            sequencia.adicionarVoz(voz);
            indiceVoz++;
        }
        return sequencia;
    }

    private String obterNomeInstrumento(int codigo) {
        return switch (codigo) {
            case 6  -> "Cravo";
            case 20 -> "Órgão";
            case 71 -> "Fagote";
            case 22 -> "Harmônica";
            case 15 -> "Tubular Bells";
            case 19 -> "Church Organ";
            default -> "Piano";
        };
    }

    private void processarLinha(String linha, Voz voz) {
        int atraso = parsearAtraso(linha);
        voz.setAtrasoEntrada(atraso);

        String conteudo = removerAtrasoInicial(linha);

        for (int i = 0; i < conteudo.length(); i++) {
            char c = conteudo.charAt(i);

            if (c == 'M' && i + 1 < conteudo.length() && conteudo.charAt(i + 1) == 'b') {
                adicionarNotaPorNome("Mi Bemol", 3, voz);
                i++;
                continue;
            }

            mapearCaractere(c, voz);
        }
    }

    private int parsearAtraso(String linha) {
        Matcher matcher = PADRAO_ATRASO.matcher(linha);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private String removerAtrasoInicial(String linha) {
        return PADRAO_ATRASO.matcher(linha).replaceFirst("");
    }

    private void mapearCaractere(char c, Voz voz) {
        switch (c) {
            case 'A' -> adicionarNotaPorNome("Lá", 9, voz);
            case 'B' -> adicionarNotaPorNome("Si", 11, voz);
            case 'C' -> adicionarNotaPorNome("Dó", 0, voz);
            case 'D' -> adicionarNotaPorNome("Ré", 2, voz);
            case 'E' -> adicionarNotaPorNome("Mi", 4, voz);
            case 'F' -> adicionarNotaPorNome("Fá", 5, voz);
            case 'G' -> adicionarNotaPorNome("Sol", 7, voz);
            case 'H' -> adicionarNotaPorNome("Si Bemol", 10, voz);
            case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' -> voz.adicionarPausa();
            case ' ' -> voz.dobrarVolume();
            case '?' -> voz.aumentarOitava();
            case 'V' -> voz.diminuirOitava();
            case '!' -> voz.setInstrumentoAtual(new Instrumento("Harmônica", 22));
            case ';' -> voz.setInstrumentoAtual(new Instrumento("Tubular Bells", 15));
            case ',' -> voz.setInstrumentoAtual(new Instrumento("Church Organ", 19));
            case '>' -> aumentarBPM();
            case '<' -> diminuirBPM();
            default -> {
                if (Character.isLetter(c)) {
                    voz.repetirUltimaNotaOuPausar();
                } else {
                    voz.adicionarPausa();
                }
            }
        }
    }

    private void adicionarNotaPorNome(String nome, int semitom, Voz voz) {
        int midiNumber = calcularMidiNumber(semitom, voz.getOitavaAtual());
        Nota nota = Nota.musical(nome, midiNumber, voz.getOitavaAtual(), 1, voz.getVolumeAtual());
        voz.adicionarNota(nota);
    }

    private int calcularMidiNumber(int semitom, int oitava) {
        return Math.min(127, 12 * (oitava + 1) + semitom);
    }

    private void aumentarBPM() {
        bpmAtual = Math.min(300, bpmAtual + 10);
    }

    private void diminuirBPM() {
        bpmAtual = Math.max(20, bpmAtual - 10);
    }

    public int getBpmAtual() {
        return bpmAtual;
    }
}