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


# Verificando se o Java está instalado:
```bash
java -version
```
Se não estiver instalado, baixe em: https://adoptium.net


# Instalando o Maven:
O Maven não vem instalado por padrão no Windows. Verifique se já está disponível:
```bash
mvn --version
```
Se o comando não for reconhecido, siga um dos métodos abaixo:

Opção 1 — Via extensão do VS Code (recomendado)

Abra o VS Code.
Instale a extensão Extension Pack for Java (Microsoft).
Instale a extensão Maven for Java (Microsoft).
O VS Code detectará o pom.xml automaticamente e exibirá um painel lateral do Maven, onde é possível executar os goals sem usar o terminal.

Opção 2 — Instalação manual

Acesse: https://maven.apache.org/download.cgi
Baixe o arquivo apache-maven-3.x.x-bin.zip.
Extraia em um diretório fixo, por exemplo: C:\Program Files\Maven.
Adicione o Maven ao PATH do sistema:

Pesquise "variáveis de ambiente" no menu Iniciar.
Em Variáveis do sistema, edite a variável Path.
Adicione o caminho: C:\Program Files\Maven\apache-maven-3.x.x\bin

Feche e reabra o terminal. Confirme com:

```bash
mvn --version
```

## Executando o projeto:

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
