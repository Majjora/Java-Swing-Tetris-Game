// Em Tetris.java
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;

public class Tetris extends JFrame {

    public static final String MENU_PANEL = "menu";
    public static final String GAME_PANEL = "game";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private final GameManager gameManager;
    private MenuPanel menuPanel;

    // --- NOVOS CAMPOS PARA GUARDAR REFERÊNCIAS ---
    private JPanel p1WrapperPanel;
    private ScorePanel p1Score;
    private ScorePanel p2Score_p1;
    private ScorePanel p2Score_p2;
    // ---------------------------------------------

    public Tetris() {
        setTitle("Criado por: Felipe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gameManager = new GameManager(this);
        this.menuPanel = new MenuPanel(gameManager);

        mainPanel.add(menuPanel, MENU_PANEL);

        add(mainPanel);

        addKeyListener(new KeyboardController(gameManager));
        setFocusable(true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (gameManager.getSoundManager() != null) {
                    gameManager.getSoundManager().stopMusic();
                }
            }
        });

        showMenu();
    }

    public void showMenu() {
        menuPanel.updateHighScore();
        cardLayout.show(mainPanel, MENU_PANEL);
        pack();
        setLocationRelativeTo(null);
    }

    // --- showGamePanel() ATUALIZADO ---
    public void showGamePanel(GameEngine p1Engine, GameEngine p2Engine, ThemeManager themeManager) {

        // Limpa referências antigas
        p1WrapperPanel = null;
        p1Score = null;
        p2Score_p1 = null;
        p2Score_p2 = null;

        JPanel gameContainer = new JPanel(new BorderLayout(10, 10));

        if (p2Engine == null) {
            // --- MODO 1 JOGADOR ---
            GamePanel p1Panel = new GamePanel(p1Engine, themeManager);
            p1Score = new ScorePanel(p1Engine, themeManager, gameManager); // Salva referência
            p1Engine.setPanels(p1Panel, p1Score);
            p1Score.setMultiplayerMode(false);

            p1WrapperPanel = new JPanel(); // Salva referência
            p1WrapperPanel.setBackground(themeManager.getPanelBackground()); // Usa a cor do tema
            p1WrapperPanel.add(p1Panel);

            gameContainer.add(p1WrapperPanel, BorderLayout.CENTER);
            gameContainer.add(p1Score, BorderLayout.EAST);

        } else {
            // --- MODO 2 JOGADORES ---
            JPanel p1Side = new JPanel(new BorderLayout());
            GamePanel p1Panel = new GamePanel(p1Engine, themeManager);
            p2Score_p1 = new ScorePanel(p1Engine, themeManager, gameManager); // Salva referência
            p1Engine.setPanels(p1Panel, p2Score_p1);
            p2Score_p1.setMultiplayerMode(true);
            p1Side.add(p1Panel, BorderLayout.CENTER);
            p1Side.add(p2Score_p1, BorderLayout.EAST);

            JPanel p2Side = new JPanel(new BorderLayout());
            GamePanel p2Panel = new GamePanel(p2Engine, themeManager);
            p2Score_p2 = new ScorePanel(p2Engine, themeManager, gameManager); // Salva referência
            p2Engine.setPanels(p2Panel, p2Score_p2);
            p2Score_p2.setMultiplayerMode(true);
            p2Side.add(p2Panel, BorderLayout.CENTER);
            p2Side.add(p2Score_p2, BorderLayout.EAST);

            gameContainer.add(p2Side, BorderLayout.WEST);
            gameContainer.add(p1Side, BorderLayout.EAST);
        }

        mainPanel.add(gameContainer, GAME_PANEL);
        cardLayout.show(mainPanel, GAME_PANEL);

        pack();
        setLocationRelativeTo(null);
    }

    // --- NOVO MÉTODO PARA ATUALIZAR AS CORES ---
    public void updateThemeColors(ThemeManager tm) {
        Color bg = tm.getPanelBackground();

        // Atualiza o wrapper do 1P
        if (p1WrapperPanel != null) {
            p1WrapperPanel.setBackground(bg);
        }

        // Atualiza os score panels
        if (p1Score != null) {
            p1Score.updateThemeColors(tm);
        }
        if (p2Score_p1 != null) {
            p2Score_p1.updateThemeColors(tm);
        }
        if (p2Score_p2 != null) {
            p2Score_p2.updateThemeColors(tm);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Tetris().setVisible(true);
        });
    }
}