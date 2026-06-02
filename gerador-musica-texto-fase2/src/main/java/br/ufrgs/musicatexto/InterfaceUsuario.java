package br.ufrgs.musicatexto;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InterfaceUsuario extends JFrame {

    private final JTextArea campoTexto = new JTextArea();
    private final JLabel areaStatus = new JLabel("Aguardando entrada...");

    private final JComboBox<String> seletorInstrumento = new JComboBox<>(new String[]{
            "Piano (GM 0)", "Cravo (GM 6)", "Órgão (GM 20)", "Fagote (GM 71)",
            "Harmônica (GM 22)", "Tubular Bells (GM 15)", "Church Organ (GM 19)"
    });
    private final JComboBox<Integer> seletorVolume = new JComboBox<>(
            new Integer[]{20, 40, 60, 80, 100, 120, 127});
    private final JComboBox<Integer> seletorBPM = new JComboBox<>(
            new Integer[]{60, 80, 100, 120, 140, 160, 180, 200});
    private final JComboBox<Integer> seletorOitava = new JComboBox<>(
            new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});

    private final JButton botaoCarregar = new JButton("Carregar TXT");
    private final JButton botaoSalvarTexto = new JButton("Salvar Texto");
    private final JButton botaoGerar = new JButton("Gerar música");
    private final JButton botaoLimpar = new JButton("Limpar");
    private final JButton botaoSalvarMIDI = new JButton("Salvar MIDI");

    private final JDialog dialogExecucao = new JDialog(this, "Reprodução", false);
    private final JLabel labelEstado = new JLabel("REPRODUZINDO...");
    private final JLabel labelBPMAtual = new JLabel("BPM ATUAL: 120");
    private final JButton botaoPausarRetomar = new JButton("Pausar");
    private final JButton botaoParar = new JButton("Parar");
    private final JPanel painelVozes = new JPanel();
    private final List<JProgressBar> barrasVoz = new ArrayList<>();
    private final List<JLabel> labelsVoz = new ArrayList<>();
    private Timer timerProgresso;

    private final GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo();
    private final MapeadorTextoMusical mapeador = new MapeadorTextoMusical();
    private final ControladorAudio controladorAudio = new ControladorAudio();

    private SequenciaMusical ultimaSequencia;
    private boolean pausado = false;

    public InterfaceUsuario() {
        configurarJanelaPrincipal();
        configurarDialogExecucao();
        configurarEventos();
    }

    private void configurarJanelaPrincipal() {
        setTitle("Gerador de Música por Texto - Fase 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 640);
        setLocationRelativeTo(null);

        campoTexto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        campoTexto.setText("[0] G A H C\n[4] D E F G\n[8] C D E F");

        JPanel painelCarregar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelCarregar.add(botaoCarregar);
        painelCarregar.add(botaoSalvarTexto);

        JPanel painelConfig = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelConfig.setBorder(BorderFactory.createTitledBorder("Configurações iniciais"));
        seletorBPM.setSelectedItem(120);
        seletorVolume.setSelectedItem(100);
        seletorOitava.setSelectedItem(6);
        painelConfig.add(new JLabel("Instrumento:")); painelConfig.add(seletorInstrumento);
        painelConfig.add(new JLabel("Volume:"));      painelConfig.add(seletorVolume);
        painelConfig.add(new JLabel("BPM:"));         painelConfig.add(seletorBPM);
        painelConfig.add(new JLabel("Oitava:"));      painelConfig.add(seletorOitava);

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.add(painelCarregar, BorderLayout.NORTH);
        painelTopo.add(painelConfig, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.add(botaoGerar);
        painelBotoes.add(botaoLimpar);
        painelBotoes.add(botaoSalvarMIDI);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelBotoes, BorderLayout.CENTER);
        painelSul.add(areaStatus, BorderLayout.SOUTH);

        add(painelTopo, BorderLayout.NORTH);
        add(new JScrollPane(campoTexto), BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);
    }

    private void configurarDialogExecucao() {
        dialogExecucao.setSize(500, 400);
        dialogExecucao.setLocationRelativeTo(this);
        dialogExecucao.setLayout(new BorderLayout(8, 8));

        labelEstado.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        labelEstado.setHorizontalAlignment(SwingConstants.CENTER);

        painelVozes.setLayout(new BoxLayout(painelVozes, BoxLayout.Y_AXIS));

        JPanel painelBPM = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBPM.add(labelBPMAtual);

        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelControles.add(botaoPausarRetomar);
        painelControles.add(botaoParar);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelBPM, BorderLayout.NORTH);
        painelSul.add(painelControles, BorderLayout.CENTER);

        dialogExecucao.add(labelEstado, BorderLayout.NORTH);
        dialogExecucao.add(new JScrollPane(painelVozes), BorderLayout.CENTER);
        dialogExecucao.add(painelSul, BorderLayout.SOUTH);
    }

    private void atualizarPainelVozes(SequenciaMusical sequencia) {
        painelVozes.removeAll();
        barrasVoz.clear();
        labelsVoz.clear();

        for (Voz voz : sequencia.getVozes()) {
            JPanel linha = new JPanel(new BorderLayout(6, 0));
            linha.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

            JLabel rotulo = new JLabel(String.format("VOZ %d", voz.getIndice()));
            rotulo.setPreferredSize(new Dimension(50, 20));
            rotulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

            JProgressBar barra = new JProgressBar(0, 100);
            barra.setStringPainted(false);

            JLabel info = new JLabel(String.format("%s  oitava %d  vol %d",
                    voz.getInstrumentoAtual().getNome(),
                    voz.getOitavaAtual(),
                    voz.getVolumeAtual()));
            info.setPreferredSize(new Dimension(220, 20));

            barrasVoz.add(barra);
            labelsVoz.add(info);

            linha.add(rotulo, BorderLayout.WEST);
            linha.add(barra, BorderLayout.CENTER);
            linha.add(info, BorderLayout.EAST);
            painelVozes.add(linha);
        }

        painelVozes.revalidate();
        painelVozes.repaint();
    }

    private void iniciarTimerProgresso() {
        if (timerProgresso != null) timerProgresso.stop();

        timerProgresso = new Timer(200, e -> {
            if (!controladorAudio.isReproduzindo() && !controladorAudio.isPausado()) {
                timerProgresso.stop();
                labelEstado.setText("CONCLUÍDO");
                botaoPausarRetomar.setEnabled(false);
                for (JProgressBar b : barrasVoz) b.setValue(100);
                return;
            }
            for (JProgressBar b : barrasVoz) {
                int val = Math.min(100, b.getValue() + 2);
                b.setValue(val);
            }
        });

        timerProgresso.start();
    }

    private void configurarEventos() {
        botaoCarregar.addActionListener(e -> carregarTXT());
        botaoSalvarTexto.addActionListener(e -> salvarTexto());
        botaoGerar.addActionListener(e -> gerarETocar());
        botaoLimpar.addActionListener(e -> limpar());
        botaoSalvarMIDI.addActionListener(e -> salvarMIDI());
        botaoPausarRetomar.addActionListener(e -> alternarPausaRetomar());
        botaoParar.addActionListener(e -> parar());
    }

    private void carregarTXT() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String texto = gerenciadorArquivo.lerArquivoTXT(chooser.getSelectedFile());
                campoTexto.setText(texto);
                mostrarMensagem("Arquivo TXT carregado: " + chooser.getSelectedFile().getName());
            } catch (IOException ex) {
                mostrarErro("Erro ao carregar TXT: " + ex.getMessage());
            }
        }
    }

    private void salvarTexto() {
        try {
            gerenciadorArquivo.salvarArquivoTXT(campoTexto.getText());
            mostrarMensagem("Texto salvo no arquivo original.");
        } catch (IOException ex) {
            mostrarErro("Erro ao salvar TXT: " + ex.getMessage());
        }
    }

    private void gerarETocar() {
        try {
            int bpm = (Integer) seletorBPM.getSelectedItem();
            int volume = (Integer) seletorVolume.getSelectedItem();
            int oitava = (Integer) seletorOitava.getSelectedItem();
            int codigoInstrumento = obterCodigoInstrumento((String) seletorInstrumento.getSelectedItem());
            ultimaSequencia = mapeador.interpretarTexto(campoTexto.getText(), bpm, volume, oitava, codigoInstrumento);
            controladorAudio.reproduzir(ultimaSequencia);

            atualizarPainelVozes(ultimaSequencia);
            labelEstado.setText("REPRODUZINDO...");
            labelBPMAtual.setText("BPM ATUAL: " + bpm);
            botaoPausarRetomar.setText("Pausar");
            botaoPausarRetomar.setEnabled(true);
            pausado = false;

            iniciarTimerProgresso();
            dialogExecucao.setVisible(true);

            mostrarMensagem("Reproduzindo " + ultimaSequencia.getVozes().size() + " voz(es).");
        } catch (MidiUnavailableException | InvalidMidiDataException ex) {
            mostrarErro("Erro MIDI: " + ex.getMessage());
        } catch (Exception ex) {
            mostrarErro("Erro ao gerar música: " + ex.getMessage());
        }
    }

    private int obterCodigoInstrumento(String nome) {
        return switch (nome) {
            case "Cravo (GM 6)"          -> 6;
            case "Órgão (GM 20)"         -> 20;
            case "Fagote (GM 71)"        -> 71;
            case "Harmônica (GM 22)"     -> 22;
            case "Tubular Bells (GM 15)" -> 15;
            case "Church Organ (GM 19)"  -> 19;
            default                      -> 0;
        };
    }

    private void alternarPausaRetomar() {
        if (!pausado) {
            controladorAudio.pausarReproducao();
            pausado = true;
            botaoPausarRetomar.setText("Retomar");
            labelEstado.setText("PAUSADO");
            mostrarMensagem("Reprodução pausada.");
        } else {
            controladorAudio.retomarReproducao();
            pausado = false;
            botaoPausarRetomar.setText("Pausar");
            labelEstado.setText("REPRODUZINDO...");
            mostrarMensagem("Reprodução retomada.");
        }
    }

    private void parar() {
        if (timerProgresso != null) timerProgresso.stop();
        controladorAudio.pararReproducao();
        pausado = false;
        dialogExecucao.setVisible(false);
        mostrarMensagem("Reprodução interrompida.");
    }

    private void limpar() {
        campoTexto.setText("");
        ultimaSequencia = null;
        mostrarMensagem("Campo de texto limpo.");
    }

    private void salvarMIDI() {
        try {
            if (ultimaSequencia == null) {
                int bpm = (Integer) seletorBPM.getSelectedItem();
                int volume = (Integer) seletorVolume.getSelectedItem();
                int oitava = (Integer) seletorOitava.getSelectedItem();
                int codigoInstrumento = obterCodigoInstrumento((String) seletorInstrumento.getSelectedItem());
                ultimaSequencia = mapeador.interpretarTexto(campoTexto.getText(), bpm, volume, oitava, codigoInstrumento);
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(gerarNomeMIDIPadrao()));
            chooser.setFileFilter(new FileNameExtensionFilter("Arquivo MIDI (*.mid)", "mid"));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File arquivo = chooser.getSelectedFile();
                if (!arquivo.getName().toLowerCase().endsWith(".mid")) {
                    arquivo = new File(arquivo.getParentFile(), arquivo.getName() + ".mid");
                }
                controladorAudio.salvarMIDI(ultimaSequencia, arquivo);
                mostrarMensagem("MIDI salvo em: " + arquivo.getAbsolutePath());
            }
        } catch (Exception ex) {
            mostrarErro("Erro ao salvar MIDI: " + ex.getMessage());
        }
    }

    private String gerarNomeMIDIPadrao() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
        return "musica_" + LocalDateTime.now().format(fmt) + ".mid";
    }

    private void mostrarMensagem(String mensagem) {
        areaStatus.setText(mensagem);
    }

    private void mostrarErro(String mensagem) {
        areaStatus.setText(mensagem);
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}