package br.ufrgs.musicatexto;

@FunctionalInterface
public interface ComandoMusical {
    void executar(Voz voz, MapeadorTextoMusical mapeador);
}