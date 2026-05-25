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

public class InterfaceUsuario extends JFrame {
    private final JTextArea campoTexto = new JTextArea();
    private final JLabel areaStatus = new JLabel("Aguardando entrada...");
    private final JComboBox<Integer> seletorBPM = new JComboBox<>(new Integer[]{60, 80, 100, 120, 140, 160, 180});
    private final JButton botaoCarregar = new JButton("Carregar TXT");
    private final JButton botaoSalvarTexto = new JButton("Salvar Texto");
    private final JButton botaoGerar = new JButton("Gerar / Tocar");
    private final JButton botaoParar = new JButton("Parar");
    private final JButton botaoLimpar = new JButton("Limpar");
    private final JButton botaoSalvarMIDI = new JButton("Salvar MIDI");

    private final GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo();
    private final MapeadorTextoMusical mapeador = new MapeadorTextoMusical();
    private final ControladorAudio controladorAudio = new ControladorAudio();

    private SequenciaMusical ultimaSequencia;

    public InterfaceUsuario() {
        configurarJanela();
        configurarEventos();
    }

    private void configurarJanela() {
        setTitle("Gerador de Música por Texto - Fase 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);

        campoTexto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        campoTexto.setText("[0] G A H C\n[4] D E F G\n[8] C D E F");

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.add(botaoCarregar);
        painelTopo.add(botaoSalvarTexto);
        painelTopo.add(new JLabel("BPM inicial:"));
        seletorBPM.setSelectedItem(120);
        painelTopo.add(seletorBPM);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.add(botaoGerar);
        painelBotoes.add(botaoParar);
        painelBotoes.add(botaoLimpar);
        painelBotoes.add(botaoSalvarMIDI);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelBotoes, BorderLayout.CENTER);
        painelSul.add(areaStatus, BorderLayout.SOUTH);

        add(painelTopo, BorderLayout.NORTH);
        add(new JScrollPane(campoTexto), BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        botaoCarregar.addActionListener(e -> carregarTXT());
        botaoSalvarTexto.addActionListener(e -> salvarTexto());
        botaoGerar.addActionListener(e -> gerarETocar());
        botaoParar.addActionListener(e -> parar());
        botaoLimpar.addActionListener(e -> limpar());
        botaoSalvarMIDI.addActionListener(e -> salvarMIDI());
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
            ultimaSequencia = mapeador.interpretarTexto(campoTexto.getText(), bpm);
            controladorAudio.reproduzir(ultimaSequencia);
            mostrarMensagem("Reproduzindo sequência com " + ultimaSequencia.getVozes().size() + " voz(es).");
        } catch (MidiUnavailableException | InvalidMidiDataException ex) {
            mostrarErro("Erro MIDI: " + ex.getMessage());
        } catch (Exception ex) {
            mostrarErro("Erro ao gerar música: " + ex.getMessage());
        }
    }

    private void parar() {
        controladorAudio.parar();
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
                ultimaSequencia = mapeador.interpretarTexto(campoTexto.getText(), bpm);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
        return "musica_" + LocalDateTime.now().format(formatter) + ".mid";
    }

    private void mostrarMensagem(String mensagem) {
        areaStatus.setText(mensagem);
    }

    private void mostrarErro(String mensagem) {
        areaStatus.setText(mensagem);
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
