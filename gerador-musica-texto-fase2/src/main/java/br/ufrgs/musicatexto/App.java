package br.ufrgs.musicatexto;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfaceUsuario interfaceUsuario = new InterfaceUsuario();
            interfaceUsuario.setVisible(true);
        });
    }
}