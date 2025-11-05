// Em GameEngine.java
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

// Import para o JSON (Salvar/Carregar)
import com.google.gson.Gson;

public class GameEngine {

    // --- Campos da Classe ---
    private final Board board;
    private final Random random = new Random();

    // Referências externas
    private GamePanel gamePanel;
    private ScorePanel scorePanel;
    private ThemeManager themeManager;
    private GameManager gameManager;
    private SoundManager soundManager;

    // Estado da peça, Estado do Jogo
    private Tetromino currentPiece;
    private int currentX, currentY;
    private int currentRotation;
    private Tetromino nextPiece;
    private int score;
    private int level;
    private int linesCleared;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean isWinner = false;
    private Timer timer;

    // Variáveis de Animação
    private boolean isAnimatingLineClear = false;
    private int animationCounter = 0;
    private List<Integer> linesToClear = new ArrayList<>();
    private int ghostY;
    private int rotationFlash = 0;

    // --- Construtor e Configuração ---
    public GameEngine() {
        this.board = new Board();
    }

    public void setPanels(GamePanel gamePanel, ScorePanel scorePanel) { this.gamePanel = gamePanel; this.scorePanel = scorePanel; }
    public void setThemeManager(ThemeManager themeManager) { this.themeManager = themeManager; }
    public void setGameManager(GameManager manager) { this.gameManager = manager; }
    public void setSoundManager(SoundManager manager) { this.soundManager = manager; }
    public GamePanel getGamePanel() { return gamePanel; }

    // --- Controle do Loop do Jogo ---
    public void startGame() {
        board.reset();
        score = 0;
        level = 1;
        linesCleared = 0;
        isGameOver = false;
        isPaused = false;
        isWinner = false;
        isAnimatingLineClear = false;
        animationCounter = 0;
        linesToClear.clear();
        rotationFlash = 0;

        spawnNewPiece(); // Define a peça ATUAL
        spawnNewPiece(); // Define a PRÓXIMA peça

        int timerDelay = 1000 / 60;
        final int[] dropCounter = {0};
        ActionListener gameLoop = e -> {
            if (rotationFlash > 0) rotationFlash--;

            if (isAnimatingLineClear) {
                // Estado de Animação (Jogo Pausado)
                animationCounter++;
                if (animationCounter > 30) {
                    isAnimatingLineClear = false;
                    animationCounter = 0;
                    board.executeLineClearance(linesToClear);
                    updateScoreAndLevel(); // <- BUG DE PONTOS/LIXO 2P ESTAVA AQUI
                    linesToClear.clear();
                    spawnNewPiece(); // <- BUG DA PRÓXIMA PEÇA/GAME OVER ESTAVA AQUI
                }
            } else if (!isPaused && !isGameOver) {
                // Estado de Jogo Normal
                int dropInterval = Math.max(1, 40 - level * 2);
                dropCounter[0]++;
                if (dropCounter[0] >= dropInterval) {
                    moveDown();
                    dropCounter[0] = 0;
                }
            }

            // Atualiza a UI
            if (gamePanel != null) gamePanel.repaint();
            if (scorePanel != null) scorePanel.update(); // <- BUG VISUAL DOS PONTOS ESTAVA AQUI
        };
        timer = new Timer(timerDelay, gameLoop);
        timer.start();
    }

    public void restartGame() {
        if(timer != null) timer.stop();
        if (gameManager != null) {
            gameManager.resetMatchState();
        }
        startGame();
    }

    public void stopGame() {
        if (timer != null) {
            timer.stop();
        }
    }

    // --- Lógica Principal do Jogo ---
    private void spawnNewPiece() {
        currentPiece = nextPiece; // A "próxima" peça se torna a "atual"
        currentRotation = 0;
        currentX = Board.WIDTH / 2 - 2;
        currentY = 0;
        nextPiece = Tetromino.values()[random.nextInt(Tetromino.values().length)]; // Gera uma nova "próxima" peça

        // --- VERIFICAÇÃO DE "GAME OVER" (CORRIGIDA) ---
        if (currentPiece != null && !board.isValidPosition(getCurrentPieceShape(), currentX, currentY)) {
            if (gameManager != null && gameManager.getCurrentState() != GameState.MENU) {
                gameManager.playerLost(this);
            } else {
                isGameOver = true;
                stopGame();
            }
        } else {
            updateGhostY();
        }
    }

    // --- LÓGICA DE "LOCKPIECE" (CORRIGIDA) ---
    private void lockPiece() {
        board.placePiece(getCurrentPieceShape(), currentX, currentY, currentPiece);
        this.linesToClear = board.findFullLines(); // Pergunta ao Board se limpou linhas

        if (!linesToClear.isEmpty()) {
            // SIM, limpou linhas
            this.isAnimatingLineClear = true;
            this.animationCounter = 0;
            this.linesCleared += linesToClear.size();
            playSound("res/clear.wav");
            // O loop do jogo agora assume o controle (estado de animação)
        } else {
            // NÃO, nenhuma linha limpa
            playSound("res/lock.wav");
            spawnNewPiece(); // Apenas chama a próxima peça
        }
    }

    // --- LÓGICA DE PONTOS E ATAQUE 2P (CORRIGIDA) ---
    private void updateScoreAndLevel() {
        int linesJustCleared = linesToClear.size();
        if (linesJustCleared == 0) return;

        // Lógica de Pontuação
        switch (linesJustCleared) {
            case 1: score += 100 * level; break;
            case 2: score += 300 * level; break;
            case 3: score += 500 * level; break;
            case 4: score += 800 * level; break;
        }
        // Lógica de Nível
        level = 1 + linesCleared / 10;

        // Lógica de Ataque 2P
        if (gameManager != null && gameManager.getCurrentState() == GameState.TWO_PLAYER) {
            int garbageToSend = 0;
            switch (linesJustCleared) {
                case 2: garbageToSend = 1; break;
                case 3: garbageToSend = 2; break;
                case 4: garbageToSend = 4; break;
            }
            if (garbageToSend > 0) {
                gameManager.sendGarbage(this, garbageToSend);
            }
        }
    }

    private void updateGhostY() {
        if (currentPiece == null) return;
        int testY = currentY;
        int[][] shape = getCurrentPieceShape();
        while (board.isValidPosition(shape, currentX, testY + 1)) {
            testY++;
        }
        this.ghostY = testY;
    }

    // Lógica de Defesa 2P
    public void addGarbageLines(int lineCount) {
        if (isGameOver || isAnimatingLineClear) return;
        board.addGarbageLines(lineCount);
        playSound("res/lock.wav");
        if (!board.isValidPosition(getCurrentPieceShape(), currentX, currentY)) {
            int saveY = currentY;
            for (int i = 1; i <= lineCount + 1; i++) {
                if (board.isValidPosition(getCurrentPieceShape(), currentX, currentY - i)) {
                    currentY = currentY - i;
                    break;
                }
            }
            if (currentY == saveY && !board.isValidPosition(getCurrentPieceShape(), currentX, currentY)) {
                if (gameManager != null && gameManager.getCurrentState() != GameState.MENU) {
                    gameManager.playerLost(this);
                }
            }
        }
        updateGhostY();
    }

    // --- Métodos de Movimento ---
    public void moveLeft() { if (!isGameOver && !isPaused && !isAnimatingLineClear) { if (board.isValidPosition(getCurrentPieceShape(), currentX - 1, currentY)) { currentX--; playSound("res/move.wav"); updateGhostY(); } } }
    public void moveRight() { if (!isGameOver && !isPaused && !isAnimatingLineClear) { if (board.isValidPosition(getCurrentPieceShape(), currentX + 1, currentY)) { currentX++; playSound("res/move.wav"); updateGhostY(); } } }
    public void moveDown() { if (!isGameOver && !isPaused && !isAnimatingLineClear) { if (board.isValidPosition(getCurrentPieceShape(), currentX, currentY + 1)) { currentY++; updateGhostY(); } else { lockPiece(); } } }
    public void hardDrop() { if (!isGameOver && !isPaused && !isAnimatingLineClear) { while (board.isValidPosition(getCurrentPieceShape(), currentX, currentY + 1)) { currentY++; score += 2; } lockPiece(); } }
    public void rotate() { if (!isGameOver && !isPaused && !isAnimatingLineClear) { int nextRotation = (currentRotation + 1) % currentPiece.getNumRotations(); int[][] nextShape = currentPiece.getShape(nextRotation); if (board.isValidPosition(nextShape, currentX, currentY)) { currentRotation = nextRotation; playSound("res/rotate.wav"); updateGhostY(); this.rotationFlash = 5; } else if (board.isValidPosition(nextShape, currentX + 1, currentY)) { currentX++; currentRotation = nextRotation; playSound("res/rotate.wav"); updateGhostY(); this.rotationFlash = 5; } else if (board.isValidPosition(nextShape, currentX - 1, currentY)) { currentX--; currentRotation = nextRotation; playSound("res/rotate.wav"); updateGhostY(); this.rotationFlash = 5; } } }
    public void togglePause() { if (!isGameOver && !isAnimatingLineClear) { isPaused = !isPaused; if(isPaused) { if (timer != null) timer.stop(); } else { if (timer != null) timer.start(); } if (gamePanel != null) { gamePanel.repaint(); } } }
    private void playSound(String soundFile) { if (soundManager != null) { soundManager.playSound(soundFile, false); } }

    // --- Controlador de Teclas (Corpo Restaurado) ---
    public void handleKeyPress(int keyCode) {
        if (isPaused && keyCode != 80) { return; }
        if (isGameOver && keyCode != 82) { return; }
        if (isAnimatingLineClear) { return; }

        switch (keyCode) {
            case 37: moveLeft(); break;
            case 39: moveRight(); break;
            case 40: moveDown(); break;
            case 38: rotate(); break;
            case 32: hardDrop(); break;
            case 80: togglePause(); break;
            case 82:
                if (gameManager != null && gameManager.getCurrentState() == GameState.ONE_PLAYER) {
                    restartGame();
                } else if (gameManager == null) {
                    restartGame();
                }
                break;
        }
    }

    // --- Getters e Setters de Estado ---
    public Board getBoard() { return board; }
    public int[][] getCurrentPieceShape() { return (currentPiece != null) ? currentPiece.getShape(currentRotation) : null; }
    public Color getCurrentPieceColor() { return (currentPiece != null) ? themeManager.getColor(currentPiece) : Color.BLACK; }
    public int getCurrentPieceX() { return currentX; }
    public int getCurrentPieceY() { return currentY; }
    public Tetromino getNextPiece() { return nextPiece; } // <- Corrigido
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLinesCleared() { return linesCleared; }
    public boolean isPaused() { return isPaused; }
    public boolean isGameOver() { return isGameOver; }
    public boolean isAnimatingLineClear() { return isAnimatingLineClear; }
    public List<Integer> getLinesToClear() { return linesToClear; }
    public int getAnimationCounter() { return animationCounter; }
    public int getGhostY() { return ghostY; }
    public int getRotationFlash() { return rotationFlash; }
    public void setGameOver(boolean gameOver) { this.isGameOver = gameOver; if (gameOver) { stopGame(); } }
    public void setWinner(boolean winner) { this.isWinner = winner; }
    public boolean isWinner() { return isWinner; }


    // --- Métodos de Salvar/Carregar ---

    public GameStateData captureState() {
        GameStateData state = new GameStateData();

        Tetromino[][] grid = board.getGrid();
        state.boardGrid = new String[Board.HEIGHT][Board.WIDTH];
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if (grid[y][x] != null) {
                    state.boardGrid[y][x] = grid[y][x].name();
                } else {
                    state.boardGrid[y][x] = null;
                }
            }
        }

        if (currentPiece != null) state.currentPieceName = currentPiece.name();
        state.currentX = this.currentX;
        state.currentY = this.currentY;
        state.currentRotation = this.currentRotation;

        if (nextPiece != null) state.nextPieceName = nextPiece.name();

        state.score = this.score;
        state.level = this.level;
        state.linesCleared = this.linesCleared;

        if (themeManager != null) {
            state.currentThemeName = themeManager.getCurrentThemeName();
        }

        return state;
    }

    public void loadState(GameStateData state) {
        board.reset();
        String[][] grid = state.boardGrid;
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if (grid[y][x] != null) {
                    board.placePiece(new int[][]{{1}}, x, y, Tetromino.valueOf(grid[y][x]));
                }
            }
        }

        if (state.currentPieceName != null) {
            this.currentPiece = Tetromino.valueOf(state.currentPieceName);
        }
        this.currentX = state.currentX;
        this.currentY = state.currentY;
        this.currentRotation = state.currentRotation;

        if (state.nextPieceName != null) {
            this.nextPiece = Tetromino.valueOf(state.nextPieceName);
        }

        this.score = state.score;
        this.level = state.level;
        this.linesCleared = state.linesCleared;

        if (this.themeManager != null && state.currentThemeName != null) {
            this.themeManager.setCurrentTheme(state.currentThemeName);
        }

        updateGhostY();
        if (gamePanel != null) gamePanel.repaint();
        if (scorePanel != null) scorePanel.update();
    }
}