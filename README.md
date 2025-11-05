🎮 Tetris (Java Swing & MySQL)
Este é um clone completo do clássico jogo Tetris, construído do zero em Java puro, usando a biblioteca Swing para a interface gráfica. O projeto vai além de um simples clone, incorporando uma arquitetura de gerenciamento de estado (GameManager), integração total com um banco de dados MySQL e uma série de funcionalidades modernas.

O jogo inclui modos 1P e 2P, rankings globais persistentes, um sistema de salvar/carregar jogo, e um alto nível de personalização de áudio e visual.

✨ Demo
https://imgur.com/a/82jMPBf

🚀 Funcionalidades Principais
Esta aplicação não é apenas um jogo, mas um sistema robusto com os seguintes recursos:

💾 Persistência de Dados (MySQL)
Ranking 1P (Pontuação): As pontuações do modo 1P são salvas em um banco de dados MySQL, e um ranking global dos Top 10 é exibido no menu.

Ranking 2P (Vitórias): Um ranking separado rastreia o número de vitórias por nickname no modo 2P. O sistema usa ON DUPLICATE KEY UPDATE para que as vitórias sejam cumulativas.

Sistema de Salvar/Carregar Jogo:

O estado completo do jogo 1P (tabuleiro, pontuação, nível, peça atual/próxima) é serializado para JSON (usando a biblioteca Gson).

Essa string JSON é armazenada no banco de dados.

O jogador pode carregar um jogo salvo a qualquer momento a partir do menu.

Modo "Hardcore": Para aumentar o desafio, um jogo carregado é automaticamente deletado do banco de dados se o jogador perder.

🎮 Modos de Jogo
Modo 1 Jogador: Jogo clássico focado em pontuação.

Modo 2 Jogadores: Modo competitivo em tela dividida (WASD vs. Setas).

Ataque "Linhas de Lixo": No modo 2P, limpar 2, 3 ou 4 linhas envia "linhas de lixo" para o oponente, criando um verdadeiro duelo de ataque e defesa.

🎨 Áudio e Visual
Menu Estilizado: Um menu com visual "arcade" (fundo preto, botões verdes) e um título "TETRIS" renderizado com as cores de cada peça.

Modo Claro / Escuro: O painel do jogo pode ser alternado entre um tema claro e um tema escuro.

Temas de Peças: Múltiplos temas de cores pré-definidos para as peças do jogo.

Editor de Cores Infinito: Um botão "Personalizar" que abre um JColorChooser para que o jogador possa criar seu próprio tema de cores, que é salvo e persistido.

Sistema de Som Completo:

Música de fundo que só toca durante o jogo (e para no menu).

Seletor de faixas, slider de volume e botão de mudo.

Efeitos sonoros para todas as ações (mover, girar, aterrissar, limpar linha, game over).

Animações ("Game Juice"):

"Peça Fantasma" (Ghost Piece) para mira precisa.

Animação de "piscar" ao limpar linhas.

Animação de "flash" da peça ao girar.

🔧 Pilha Tecnológica (Tech Stack)
Linguagem: Java (JDK)

Interface Gráfica (GUI): Java Swing

Banco de Dados: MySQL Server

Bibliotecas Externas:

mysql-connector-j: O driver JDBC oficial para conectar Java ao MySQL.

com.google.gson: Biblioteca do Google para serializar e desserializar objetos Java para JSON (usada no sistema de Salvar/Carregar).

Áudio: javax.sound.sampled (API de áudio nativa do Java).

🛠️ Instalação e Execução
Para rodar este projeto, você precisará de três componentes: o código-fonte, as bibliotecas (JARs) e um servidor MySQL.

1. Configuração do Banco de Dados (MySQL)
   Este projeto requer um servidor MySQL rodando localmente.

Instale o https://dev.mysql.com/downloads/installer/ e o MySQL Workbench.

Durante a instalação, defina uma senha para o usuário root.

Abra o MySQL Workbench (ou um terminal) e execute os seguintes comandos para criar o banco de dados e o usuário que o jogo espera:

-- Cria o banco de dados que o jogo vai usar
CREATE DATABASE tetris_db;

-- Cria o usuário e a senha que estão no código (DatabaseManager.java)
CREATE USER 'tetris_user'@'localhost' IDENTIFIED BY 'tetris_pass';

-- Dá ao usuário permissão total sobre o novo banco de dados
GRANT ALL PRIVILEGES ON tetris_db.* TO 'tetris_user'@'localhost';

-- Aplica as mudanças
FLUSH PRIVILEGES;

O DatabaseManager.java criará as tabelas (leaderboard, leaderboard_2p, saved_games) automaticamente na primeira vez que o jogo for executado.

2. Configuração do Projeto (IntelliJ IDEA)
   Clone ou baixe este repositório.

Abra o projeto no IntelliJ.

Adicione as Bibliotecas (JARs):

Crie uma pasta chamada lib na raiz do seu projeto.

Baixe o MySQL Connector/J (.jar) e o Gson (.jar) e coloque-os dentro da pasta lib.

Vá em File > Project Structure... > Modules > Dependencies.

Clique no ícone +, selecione "JARs or directories...", e adicione os dois arquivos .jar da sua pasta lib.

Adicione os Recursos de Áudio:

Certifique-se de que você tem uma pasta res na raiz do projeto contendo todos os arquivos .wav necessários (ex: tetris-music.wav, move.wav, clear.wav, etc.).

Execute:

Abra o arquivo src/Tetris.java.

Clique na seta verde ao lado do método main para rodar o jogo.

⌨️ Como Jogar
Menu
Novo Jogo (1P): Inicia um novo jogo solo.

Carregar Jogo (1P): Abre um menu para carregar um jogo salvo anteriormente.

2 Jogadores: Inicia um jogo competitivo (pede os nomes dos jogadores).

Ranking (1P) / (2P): Exibe os rankings globais do banco de dados.

Controles no Jogo

Ação,Jogador 1 (Direita),Jogador 2 (Esquerda)
Mover Esquerda,Seta Esquerda,A
Mover Direita,Seta Direita,D
Mover Baixo,Seta Baixo,S
Girar Peça,Seta Cima,W
Queda Rápida (Hard Drop),Espaço,Q
Pausar,P,(Desabilitado)
Reiniciar,R (Apenas 1P),(Desabilitado)
Voltar ao Menu,R (Apenas 2P),R (Apenas 2P)
