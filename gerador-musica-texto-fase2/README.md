# Gerador de Música por Texto - Fase 2

Protótipo em Java para o trabalho prático da disciplina INF01120.

## Funcionalidades implementadas

- Inserção de texto manualmente.
- Carregamento de arquivo `.txt`.
- Salvamento do texto editado no arquivo original.
- Interpretação de cada linha como uma voz independente.
- Suporte a atraso inicial por voz com `[n]`.
- Oitava base, volume base e instrumento base por voz.
- Mapeamento de caracteres para notas, pausas, instrumentos, oitava, volume e BPM.
- Reprodução sonora via MIDI.
- Exportação da música gerada para arquivo `.mid`.

## Como compilar e rodar

Requisitos:
- Java 17 ou superior.
- Maven instalado.

No terminal, dentro da pasta do projeto:

```bash
mvn clean package
mvn exec:java
```

Ou execute diretamente pela classe:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="br.ufrgs.musicatexto.App"
```

## Exemplo de entrada

```txt
[0] G A H C
[4] D E F G
[8] C D E F
```

Cada linha é uma voz. O número entre colchetes indica o atraso em beats antes da entrada da voz.

## Observações

Este protótipo usa apenas a biblioteca padrão `javax.sound.midi`, evitando dependências externas.
