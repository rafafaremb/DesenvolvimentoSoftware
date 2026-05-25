package br.ufrgs.musicatexto;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class GerenciadorArquivo {
    private File arquivoTXTAtual;

    public String lerArquivoTXT(File arquivo) throws IOException {
        arquivoTXTAtual = arquivo;
        return Files.readString(arquivo.toPath(), StandardCharsets.UTF_8);
    }

    public void salvarArquivoTXT(String texto) throws IOException {
        if (arquivoTXTAtual == null) {
            throw new IOException("Nenhum arquivo TXT foi carregado.");
        }
        Files.writeString(arquivoTXTAtual.toPath(), texto, StandardCharsets.UTF_8);
    }

    public File getArquivoTXTAtual() {
        return arquivoTXTAtual;
    }
}
