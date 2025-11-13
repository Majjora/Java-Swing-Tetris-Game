// Em Tetris.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Tetris extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private GameManager gameManager;
    private GamePanel p1GamePanel; // Referência para atualização de cores
    private GamePanel p2GamePanel; // Referência para atualização de cores
    private ScorePanel p1ScorePanel; // Referência para atualização de cores
    private ScorePanel p2ScorePanel; // Referência para atualização de cores

    public Tetris() {
        setTitle("Tetris Java - OOP Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Inicializa o gerenciador do jogo
        gameManager = new GameManager(this);

        // Configuração do CardLayout para trocar entre Menu e Jogo
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Painel do Menu
        MenuPanel menuPanel = new MenuPanel(gameManager);
        mainPanel.add(menuPanel, "MENU");

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Listener Global de Teclas (Redireciona tudo para o GameManager)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                gameManager.handleKeyPress(e.getKeyCode());
            }
        });
        setFocusable(true);
    }

    public void showMenu() {
        setSize(600, 700); // Tamanho do menu
        cardLayout.show(mainPanel, "MENU");
        setLocationRelativeTo(null);

        // Limpa referências antigas para economizar memória
        p1GamePanel = null;
        p2GamePanel = null;
        p1ScorePanel = null;
        p2ScorePanel = null;
    }

    public void showGamePanel(GameEngine p1Engine, GameEngine p2Engine, ThemeManager themeManager) {
        JPanel gameContainer = new JPanel();

        // --- MODO 1 JOGADOR ---
        if (p2Engine == null) {
            gameContainer.setLayout(new BorderLayout());

            // Cria os painéis
            p1GamePanel = new GamePanel(p1Engine, themeManager);
            p1ScorePanel = new ScorePanel(p1Engine, themeManager, gameManager);

            // Conecta Engine -> Paineis
            p1Engine.setPanels(p1GamePanel, p1ScorePanel);

            gameContainer.add(p1GamePanel, BorderLayout.CENTER);
            gameContainer.add(p1ScorePanel, BorderLayout.EAST);

            setSize(550, 700); // Tamanho para 1 Player

            // --- MODO 2 JOGADORES ---
        } else {
            gameContainer.setLayout(new GridLayout(1, 2, 10, 0)); // Grid com 2 colunas e espaço no meio
            gameContainer.setBackground(Color.BLACK);

            // --- Lado do Jogador 1 (Esquerda) ---
            JPanel p1Container = new JPanel(new BorderLayout());
            p1GamePanel = new GamePanel(p1Engine, themeManager);
            p1ScorePanel = new ScorePanel(p1Engine, themeManager, gameManager);
            p1Engine.setPanels(p1GamePanel, p1ScorePanel);

            p1Container.add(p1GamePanel, BorderLayout.CENTER);
            p1Container.add(p1ScorePanel, BorderLayout.EAST); // Score fica à direita do tabuleiro P1

            // --- Lado do Jogador 2 (Direita) ---
            JPanel p2Container = new JPanel(new BorderLayout());
            p2GamePanel = new GamePanel(p2Engine, themeManager);
            p2ScorePanel = new ScorePanel(p2Engine, themeManager, gameManager);
            p2Engine.setPanels(p2GamePanel, p2ScorePanel);

            p2Container.add(p2GamePanel, BorderLayout.CENTER);
            p2Container.add(p2ScorePanel, BorderLayout.EAST); // Score fica à direita do tabuleiro P2

            // Adiciona ao container principal
            gameContainer.add(p2Container); // Jogador "WASD" (Player 2 visualmente na esquerda ou 1, tanto faz)
            gameContainer.add(p1Container); // Jogador "Setas"

            setSize(1100, 700); // Largura dobrada para 2 Players
        }

        // Adiciona este novo jogo ao CardLayout
        mainPanel.add(gameContainer, "GAME");
        cardLayout.show(mainPanel, "GAME");
        setLocationRelativeTo(null);

        // Garante que a janela pegue o foco para as teclas funcionarem
        this.requestFocusInWindow();
    }

    // Método auxiliar para atualizar cores da janela principal se necessário
    public void updateThemeColors(ThemeManager tm) {
        if (p1ScorePanel != null) p1ScorePanel.updateThemeColors(tm);
        if (p2ScorePanel != null) p2ScorePanel.updateThemeColors(tm);
        // GamePanels se atualizam sozinhos no repaint(), mas podemos forçar:
        if (p1GamePanel != null) p1GamePanel.repaint();
        if (p2GamePanel != null) p2GamePanel.repaint();
    }

    public static void main(String[] args) {
        // Executa na Thread de Eventos do Swing (Boas práticas)
        SwingUtilities.invokeLater(() -> {
            new Tetris();
        });
    }
}