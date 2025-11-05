// Em ScorePanel.java
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Component;
import java.awt.event.ActionListener;

public class ScorePanel extends JPanel {
    private static final int TILE_SIZE = 20;

    private final GameEngine engine;
    private final ThemeManager themeManager;
    private final GameManager gameManager;

    // Campos da UI
    private JLabel scoreLabel, levelLabel, linesLabel, themeLabel, audioLabel, nextLabel;
    private JPanel nextPiecePanel;
    private JButton pauseButton, restartButton, backToMenuButton, editCustomThemeButton;
    private ColorEditorDialog colorEditorDialog;
    private JComboBox<String> musicSelector;
    private JSlider volumeSlider;
    private JButton muteButton;
    private JButton saveGameButton;
    private JButton toggleThemeButton;
    private JComboBox<String> themeSelector;

    public ScorePanel(GameEngine engine, ThemeManager themeManager, GameManager gameManager) {
        this.engine = engine;
        this.themeManager = themeManager;
        this.gameManager = gameManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 600));

        initializeComponents();
    }

    private void initializeComponents() {
        Font font = new Font("Arial", Font.BOLD, 18);
        Font smallFont = new Font("Arial", Font.PLAIN, 14);

        scoreLabel = new JLabel("Pontos: 0");
        scoreLabel.setFont(font);
        add(scoreLabel);
        levelLabel = new JLabel("Nível: 1");
        levelLabel.setFont(font);
        add(levelLabel);
        linesLabel = new JLabel("Linhas: 0");
        linesLabel.setFont(font);
        add(linesLabel);

        add(Box.createRigidArea(new Dimension(0, 10)));
        themeLabel = new JLabel("Tema:");
        themeLabel.setFont(smallFont);
        add(themeLabel);

        themeSelector = new JComboBox<>(themeManager.getThemeNames());
        themeSelector.setFocusable(false);
        themeSelector.setSelectedItem(themeManager.getCurrentThemeName());
        themeSelector.addActionListener(e -> {
            String selected = (String) themeSelector.getSelectedItem();
            themeManager.setCurrentTheme(selected);
            // Atualiza os dois painéis de jogo se eles existirem
            if (gameManager.getPlayer1Engine() != null && gameManager.getPlayer1Engine().getGamePanel() != null) {
                gameManager.getPlayer1Engine().getGamePanel().repaint();
            }
            if (gameManager.getPlayer2Engine() != null && gameManager.getPlayer2Engine().getGamePanel() != null) {
                gameManager.getPlayer2Engine().getGamePanel().repaint();
            }
        });
        add(themeSelector);

        editCustomThemeButton = new JButton("Personalizar Cores...");
        styleButton(editCustomThemeButton);
        editCustomThemeButton.addActionListener(e -> openColorEditor());
        add(editCustomThemeButton);

        add(Box.createRigidArea(new Dimension(0, 10)));
        audioLabel = new JLabel("Controles de Áudio:");
        audioLabel.setFont(smallFont);
        add(audioLabel);
        musicSelector = new JComboBox<>(gameManager.getSoundManager().getTrackList());
        musicSelector.setFocusable(false);
        musicSelector.addActionListener(e -> {
            String selectedTrack = (String) musicSelector.getSelectedItem();
            gameManager.getSoundManager().playMusic(selectedTrack);
        });
        add(musicSelector);

        int defaultVolume = (int) (gameManager.getSoundManager().getVolume() * 100);
        volumeSlider = new JSlider(0, 100, defaultVolume);
        volumeSlider.setFocusable(false);
        volumeSlider.addChangeListener(e -> {
            float volume = volumeSlider.getValue() / 100.0f;
            gameManager.getSoundManager().setVolume(volume);
            muteButton.setText(volume == 0.0f ? "Desmutar" : "Mutar");
        });
        add(volumeSlider);

        muteButton = new JButton(defaultVolume == 0 ? "Desmutar" : "Mutar");
        styleButton(muteButton);
        muteButton.addActionListener(e -> {
            boolean isNowMuted = gameManager.getSoundManager().toggleMute();
            muteButton.setText(isNowMuted ? "Desmutar" : "Mutar");
            if (isNowMuted) {
                volumeSlider.setValue(0);
            } else {
                volumeSlider.setValue((int)(gameManager.getSoundManager().getVolume() * 100));
            }
        });
        add(muteButton);

        add(Box.createRigidArea(new Dimension(0, 10)));
        nextLabel = new JLabel("Próxima Peça:");
        nextLabel.setFont(smallFont);
        add(nextLabel);
        nextPiecePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) { super.paintComponent(g); drawNextPiece(g); }
        };
        nextPiecePanel.setPreferredSize(new Dimension(100, 100));
        nextPiecePanel.setBackground(Color.BLACK);
        add(nextPiecePanel);

        add(Box.createVerticalGlue());

        toggleThemeButton = new JButton("Modo Claro/Escuro");
        styleButton(toggleThemeButton);
        toggleThemeButton.addActionListener(e -> gameManager.toggleUIMode());
        add(toggleThemeButton);

        pauseButton = new JButton("Pausar (P)");
        styleButton(pauseButton);
        pauseButton.addActionListener(e -> engine.togglePause());
        add(pauseButton);

        saveGameButton = new JButton("Salvar Jogo");
        styleButton(saveGameButton);
        saveGameButton.addActionListener(e -> saveGame());
        add(saveGameButton);

        restartButton = new JButton("Reiniciar (R)");
        styleButton(restartButton);
        restartButton.addActionListener(e -> engine.restartGame());
        add(restartButton);

        backToMenuButton = new JButton("Voltar ao Menu");
        styleButton(backToMenuButton);
        backToMenuButton.addActionListener(e -> gameManager.returnToMenu());
        add(backToMenuButton);

        add(Box.createRigidArea(new Dimension(0, 5)));

        updateThemeColors(themeManager);
    }

    private void styleButton(JButton button) {
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        button.setFont(buttonFont);
        button.setBackground(new Color(0, 180, 0));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusable(false);
        Dimension size = new Dimension(180, 30);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void updateThemeColors(ThemeManager tm) {
        Color bg = tm.getPanelBackground();
        Color fg = tm.getPanelForeground();

        setBackground(bg);

        scoreLabel.setForeground(fg);
        levelLabel.setForeground(fg);
        linesLabel.setForeground(fg);
        themeLabel.setForeground(fg);
        audioLabel.setForeground(fg);
        nextLabel.setForeground(fg);

        themeSelector.setForeground(fg);
        themeSelector.setBackground(bg);
        musicSelector.setForeground(fg);
        musicSelector.setBackground(bg);

        revalidate();
        repaint();
    }

    private void saveGame() {
        if (!engine.isPaused()) {
            engine.togglePause();
        }
        String saveName = (String)JOptionPane.showInputDialog(
                this, "Digite um nome para o seu jogo salvo:",
                "Salvar Jogo", JOptionPane.PLAIN_MESSAGE, null, null, "Meu Jogo" );
        if (saveName != null && !saveName.trim().isEmpty()) {
            gameManager.saveCurrentGame(saveName.trim());
        }
    }

    private void openColorEditor() {
        if (colorEditorDialog == null) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            colorEditorDialog = new ColorEditorDialog(parentFrame, themeManager, engine);
        }
        colorEditorDialog.setVisible(true);
    }

    // --- MÉTODO ATUALIZADO ---
    public void setMultiplayerMode(boolean isMultiplayer) {
        pauseButton.setEnabled(!isMultiplayer);
        saveGameButton.setVisible(!isMultiplayer); // Continua escondido no 2P (não faz sentido)

        // --- CORREÇÃO AQUI ---
        // Estes botões agora ficam visíveis em ambos os modos
        toggleThemeButton.setVisible(true);
        editCustomThemeButton.setVisible(true);
        themeSelector.setVisible(true);
        // --- FIM DA CORREÇÃO ---

        if (isMultiplayer) {
            backToMenuButton.setVisible(false);
            restartButton.setText("Voltar ao Menu (R)");
            for (ActionListener al : restartButton.getActionListeners()) {
                restartButton.removeActionListener(al);
            }
            restartButton.addActionListener(e -> gameManager.returnToMenu());
        } else {
            backToMenuButton.setVisible(true);
            restartButton.setText("Reiniciar (R)");
            for (ActionListener al : restartButton.getActionListeners()) {
                restartButton.removeActionListener(al);
            }
            restartButton.addActionListener(e -> engine.restartGame());
        }
    }

    public void update() {
        if (engine == null) return;
        scoreLabel.setText("Pontos: " + engine.getScore());
        levelLabel.setText("Nível: " + engine.getLevel());
        linesLabel.setText("Linhas: " + engine.getLinesCleared());
        pauseButton.setText(engine.isPaused() ? "Continuar (P)" : "Pausar (P)");
        nextPiecePanel.repaint();
    }

    private void drawNextPiece(Graphics g) {
        if (engine == null) return;
        Tetromino next = engine.getNextPiece();
        if (next == null) return;
        int[][] shape = next.getShape(0);
        Color color = themeManager.getColor(next);
        int panelWidth = nextPiecePanel.getWidth();
        int panelHeight = nextPiecePanel.getHeight();
        int shapeWidth = shape[0].length * TILE_SIZE;
        int shapeHeight = shape.length * TILE_SIZE;
        int startX = (panelWidth - shapeWidth) / 2;
        int startY = (panelHeight - shapeHeight) / 2;
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {
                    g.setColor(color);
                    g.fillRect(startX + x * TILE_SIZE, startY + y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    g.setColor(color.darker());
                    g.drawRect(startX + x * TILE_SIZE, startY + y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }
}