// Em GameManager.java
import javax.swing.Timer;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

enum GameState {
    MENU,
    ONE_PLAYER,
    TWO_PLAYER
}

public class GameManager {

    private GameState currentState;
    private final Tetris gameWindow;

    // Componentes de lógica
    private GameEngine player1Engine;
    private GameEngine player2Engine;
    private ThemeManager themeManager;
    private HighScoreManager highScoreManager;
    private SoundManager soundManager;
    private DatabaseManager databaseManager;

    private Timer gameTimer;
    private boolean matchOver = false;
    private Gson gson;

    // Estado do Jogo Salvo
    private String loadedSaveName = null;

    // Nicknames 2P
    private String player1Nickname;
    private String player2Nickname;

    public GameManager(Tetris gameWindow) {
        this.gameWindow = gameWindow;
        this.themeManager = new ThemeManager();
        this.highScoreManager = new HighScoreManager();
        this.currentState = GameState.MENU;
        this.databaseManager = new DatabaseManager();
        this.gson = new Gson();
        this.soundManager = new SoundManager();
        soundManager.setVolume(0.8f); // Define o volume, mas não toca
    }

    public SoundManager getSoundManager() { return soundManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }

    public void resetMatchState() {
        this.matchOver = false;
        this.loadedSaveName = null;
        this.player1Nickname = null;
        this.player2Nickname = null;
    }

    public void startNewOnePlayerGame() {
        currentState = GameState.ONE_PLAYER;
        resetMatchState();
        player1Engine = createPlayerEngine();
        gameWindow.showGamePanel(player1Engine, null, themeManager);
        soundManager.startDefaultMusic(); // Toca a música
        player1Engine.startGame();
    }

    public void startTwoPlayerGame() {
        resetMatchState();
        currentState = GameState.TWO_PLAYER;

        player2Nickname = JOptionPane.showInputDialog(
                gameWindow, "Nome do Jogador 1 (WASD):", "Jogador 1", JOptionPane.PLAIN_MESSAGE);
        if (player2Nickname == null || player2Nickname.trim().isEmpty()) player2Nickname = "Jogador 1";

        player1Nickname = JOptionPane.showInputDialog(
                gameWindow, "Nome do Jogador 2 (Setas):", "Jogador 2", JOptionPane.PLAIN_MESSAGE);
        if (player1Nickname == null || player1Nickname.trim().isEmpty()) player1Nickname = "Jogador 2";
        if (player1Nickname.length() > 50) player1Nickname = player1Nickname.substring(0, 50);
        if (player2Nickname.length() > 50) player2Nickname = player2Nickname.substring(0, 50);

        player1Engine = createPlayerEngine();
        player2Engine = createPlayerEngine();

        gameWindow.showGamePanel(player1Engine, player2Engine, themeManager);
        soundManager.startDefaultMusic(); // Toca a música
        player1Engine.startGame();
        player2Engine.startGame();
    }

    public void loadGame(String saveName) {
        try {
            String jsonState = databaseManager.loadGameJSON(saveName);
            if (jsonState == null) { throw new Exception("Save '" + saveName + "' não encontrado."); }
            GameStateData state = gson.fromJson(jsonState, GameStateData.class);

            startNewOnePlayerGame(); // Prepara a engine (e chama resetMatchState)
            this.loadedSaveName = saveName; // "Trackeia" o save

            player1Engine.stopGame();
            player1Engine.togglePause();
            player1Engine.loadState(state);

            soundManager.startDefaultMusic(); // Toca a música

            JOptionPane.showMessageDialog(gameWindow, "Jogo '" + saveName + "' carregado!\nPressione 'P' para despausar.", "Jogo Carregado", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(gameWindow, "Erro ao carregar o jogo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void returnToMenu() {
        if (player1Engine != null) player1Engine.stopGame();
        if (player2Engine != null) player2Engine.stopGame();
        soundManager.stopMusic(); // Para a música
        player1Engine = null;
        player2Engine = null;
        resetMatchState();
        currentState = GameState.MENU;
        gameWindow.showMenu();
    }

    // --- ESTE MÉTODO ESTAVA VAZIO ---
    private GameEngine createPlayerEngine() {
        GameEngine engine = new GameEngine();
        engine.setThemeManager(themeManager);
        engine.setGameManager(this);
        engine.setSoundManager(this.soundManager);
        return engine; // <-- O 'return' que faltava
    }

    // --- ESTE MÉTODO ESTAVA VAZIO ---
    public void playerLost(GameEngine lostEngine) {
        if (matchOver) return;

        soundManager.playSound("res/gameover.wav", false);

        if (currentState == GameState.ONE_PLAYER) {
            matchOver = true;
            lostEngine.setGameOver(true);
            int finalScore = lostEngine.getScore();
            highScoreManager.checkAndSetHighScore(finalScore);

            if (finalScore > 0) {
                String nickname = (String)JOptionPane.showInputDialog(
                        gameWindow, "Fim de Jogo! Pontuação: " + finalScore + "\nDigite seu nome para o ranking:",
                        "Salvar Pontuação", JOptionPane.PLAIN_MESSAGE, null, null, "Jogador" );
                if (nickname != null && !nickname.trim().isEmpty()) {
                    String safeNickname = nickname.trim();
                    if (safeNickname.length() > 50) safeNickname = safeNickname.substring(0, 50);
                    databaseManager.addHighScore(safeNickname, finalScore);
                }
            }

            if (this.loadedSaveName != null) {
                databaseManager.deleteSaveGame(this.loadedSaveName);
                this.loadedSaveName = null;
            }

            if (lostEngine.getGamePanel() != null) lostEngine.getGamePanel().repaint();

        } else if (currentState == GameState.TWO_PLAYER) {
            matchOver = true;
            player1Engine.stopGame();
            player2Engine.stopGame();

            String winnerNickname;
            if (lostEngine == player1Engine) {
                player1Engine.setGameOver(true);
                player2Engine.setWinner(true);
                winnerNickname = player2Nickname;
            } else {
                player2Engine.setGameOver(true);
                player1Engine.setWinner(true);
                winnerNickname = player1Nickname;
            }
            databaseManager.addWin(winnerNickname);

            if (player1Engine.getGamePanel() != null) player1Engine.getGamePanel().repaint();
            if (player2Engine.getGamePanel() != null) player2Engine.getGamePanel().repaint();
        }
    }

    // --- ESTE MÉTODO ESTAVA VAZIO ---
    public void sendGarbage(GameEngine sender, int lineCount) {
        if (matchOver || currentState != GameState.TWO_PLAYER) { return; }
        GameEngine target = (sender == player1Engine) ? player2Engine : player1Engine;
        if (target != null) { target.addGarbageLines(lineCount); }
    }

    // --- ESTE MÉTODO ESTAVA VAZIO (PARCIALMENTE) ---
    public List<String> getSavedGameNames() {
        return databaseManager.getSavedGameNames();
    }

    // --- ESTE MÉTODO ESTAVA VAZIO ---
    public void saveCurrentGame(String saveName) {
        if (player1Engine == null || currentState != GameState.ONE_PLAYER) { return; }
        try {
            GameStateData state = player1Engine.captureState();
            String jsonState = gson.toJson(state);
            databaseManager.saveGame(saveName, jsonState);
            if (saveName.equals(this.loadedSaveName)) {
                this.loadedSaveName = null;
            }
            JOptionPane.showMessageDialog(gameWindow, "Jogo salvo como '" + saveName + "'!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(gameWindow, "Erro ao salvar o jogo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- ESTE MÉTODO ESTAVA VAZIO ---
    public void toggleUIMode() {
        themeManager.toggleUIMode();
        gameWindow.updateThemeColors(themeManager);
    }

    // --- handleKeyPress() (O MÉTODO QUE MUDAMOS) ---
    public void handleKeyPress(int keyCode) {
        if (currentState == GameState.TWO_PLAYER && keyCode == 82) { // 'R'
            returnToMenu();
            return;
        }

        if (currentState == GameState.ONE_PLAYER && player1Engine != null) {
            player1Engine.handleKeyPress(keyCode);

        } else if (currentState == GameState.TWO_PLAYER) {

            // P1 (Setas, Espaço, 'C' para Hold)
            if (player1Engine != null &&
                    (keyCode == 37 || keyCode == 39 || keyCode == 40 || keyCode == 38 || keyCode == 32 || keyCode == 67)) {
                player1Engine.handleKeyPress(keyCode);
            }

            // P2 (WASD, 'Q' para Drop, 'E' para Hold)
            if (player2Engine != null) {
                if (keyCode == 65) player2Engine.moveLeft();   // A
                if (keyCode == 68) player2Engine.moveRight();  // D
                if (keyCode == 83) player2Engine.moveDown();   // S
                if (keyCode == 87) player2Engine.rotate();     // W
                if (keyCode == 81) player2Engine.hardDrop();   // Q
                if (keyCode == 69) player2Engine.holdPiece();  // E (Hold)
            }
        }
    }

    public GameState getCurrentState() { return currentState; }
    public GameEngine getPlayer1Engine() { return player1Engine; }
    public GameEngine getPlayer2Engine() { return player2Engine; }
    public int getHighScore() { return highScoreManager.getHighScore(); }
}