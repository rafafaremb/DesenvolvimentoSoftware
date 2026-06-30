package br.ufrgs.musicatexto;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapeadorTextoMusical {
    private static final Pattern PADRAO_ATRASO = Pattern.compile("^\\s*\\[(\\d+)]\\s");
    private final Map<Character, ComandoMusical> comandos = new HashMap<>();
    private int bpmAtual;

    public MapeadorTextoMusical() {
        registrarComandos();
    }

    private void registrarComandos() {
        comandos.put('A', (voz, m) -> m.adicionarNotaPorNome("Lá", 9, voz));
        comandos.put('B', (voz, m) -> m.adicionarNotaPorNome("Si", 11, voz));
        comandos.put('C', (voz, m) -> m.adicionarNotaPorNome("Dó", 0, voz));
        comandos.put('D', (voz, m) -> m.adicionarNotaPorNome("Ré", 2, voz));
        comandos.put('E', (voz, m) -> m.adicionarNotaPorNome("Mi", 4, voz));
        comandos.put('F', (voz, m) -> m.adicionarNotaPorNome("Fá", 5, voz));
        comandos.put('G', (voz, m) -> m.adicionarNotaPorNome("Sol", 7, voz));
        comandos.put('H', (voz, m) -> m.adicionarNotaPorNome("Si Bemol", 10, voz));

        for (char pausa : new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}) {
            comandos.put(pausa, (voz, m) -> voz.adicionarPausa());
        }

        comandos.put(' ', (voz, m) -> voz.dobrarVolume());
        comandos.put('?', (voz, m) -> voz.aumentarOitava());
        comandos.put('V', (voz, m) -> voz.diminuirOitava());
        comandos.put('!', (voz, m) -> voz.setInstrumentoAtual(InstrumentoGM.HARMONICA.criarInstancia()));
        comandos.put(';', (voz, m) -> voz.setInstrumentoAtual(InstrumentoGM.TUBULAR_BELLS.criarInstancia()));
        comandos.put(',', (voz, m) -> voz.setInstrumentoAtual(InstrumentoGM.CHURCH_ORGAN.criarInstancia()));
        comandos.put('>', (voz, m) -> m.aumentarBPM());
        comandos.put('<', (voz, m) -> m.diminuirBPM());
    }

    public SequenciaMusical interpretarTexto(String texto, ConfiguracaoInicial config) {
        bpmAtual = Math.max(20, Math.min(300, config.getBpmInicial()));
        SequenciaMusical sequencia = new SequenciaMusical(bpmAtual);
        String[] linhas = texto.split("\\R");
        int indiceVoz = 0;

        for (String linha : linhas) {
            if (linha == null || linha.trim().isEmpty()) continue;

            Voz voz = (indiceVoz == 0)
                    ? new Voz(0, config.getVolumeVoz0(), config.getOitavaVoz0(), config.getInstrumentoVoz0())
                    : new Voz(indiceVoz);

            processarLinha(linha, voz);
            sequencia.adicionarVoz(voz);
            indiceVoz++;
        }
        return sequencia;
    }

    public SequenciaMusical interpretarTexto(String texto, int bpmInicial) {
        Instrumento piano = InstrumentoGM.PIANO.criarInstancia();
        return interpretarTexto(texto, new ConfiguracaoInicial(bpmInicial, 100, 6, piano));
    }

    private void processarLinha(String linha, Voz voz) {
        int atraso = parsearAtraso(linha);
        voz.setAtrasoEntrada(atraso);
        String conteudo = removerAtrasoInicial(linha);
        mapearSequencia(conteudo, voz);
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

    public void mapearSequencia(String conteudo, Voz voz) {
        for (int i = 0; i < conteudo.length(); i++) {
            char c = conteudo.charAt(i);

            if (c == 'M') {
                if (i + 1 < conteudo.length() && conteudo.charAt(i + 1) == 'b') {
                    adicionarNotaPorNome("Mi Bemol", 3, voz);
                    i++; 
                } else {
                    adicionarNotaPorNome("Mi", 4, voz);
                }
            } else {
                ComandoMusical comando = comandos.get(c);
                if (comando != null) {
                    comando.executar(voz, this);
                } else if (Character.isLetter(c)) {
                    voz.repetirUltimaNotaOuPausar();
                } else {
                    voz.adicionarPausa();
                }
            }
        }
    }

    public void adicionarNotaPorNome(String nome, int semitom, Voz voz) {
        int midiNumber = calcularMidiNumber(semitom, voz.getOitavaAtual());
        Nota nota = Nota.musical(nome, midiNumber, voz.getOitavaAtual(), 1, voz.getVolumeAtual());
        voz.adicionarNota(nota);
    }

    private int calcularMidiNumber(int semitom, int oitava) {
        return Math.min(127, 12 * (oitava + 1) + semitom);
    }

    public void aumentarBPM() { bpmAtual = Math.min(300, bpmAtual + 10); }
    public void diminuirBPM() { bpmAtual = Math.max(20, bpmAtual - 10); }
    public int getBpmAtual() { return bpmAtual; }
}