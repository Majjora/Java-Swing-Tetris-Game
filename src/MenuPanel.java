// Em MenuPanel.java
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory; // <-- ADICIONADO
import java.awt.Font;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color; // <-- ADICIONADO
import java.awt.Graphics; // <-- ADICIONADO
import java.awt.FontMetrics; // <-- ADICIONADO

// --- (Importações do Ranking, JTextArea, etc., permanecem) ---
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import java.util.List;

public class MenuPanel extends JPanel {

    private final GameManager gameManager;
    private JLabel highScoreLabel;

    // As cores clássicas do Tetris para o título
    private final Color[] TETRIS_COLORS = {
            new Color(0, 240, 240),   // I (Ciano)
            new Color(240, 240, 0),   // O (Amarelo)
            new Color(160, 0, 240),   // T (Roxo)
            new Color(0, 240, 0),     // S (Verde)
            new Color(240, 0, 0),     // Z (Vermelho)
            new Color(240, 160, 0)    // L (Laranja)
            // new Color(0, 0, 240),  // J (Azul) - "TETRIS" só tem 6 letras
    };

    public MenuPanel(GameManager gameManager) {
        this.gameManager = gameManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(800, 600));

        // --- MUDANÇAS DE ESTILO ---
        setBackground(Color.BLACK); // Fundo preto
        // Borda verde de 5 pixels
        setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));

        // --- (Título antigo removido) ---
        // JLabel title = new JLabel("Gemini Tetris");

        // --- 1. O NOVO PAINEL DE TÍTULO COLORIDO ---
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                String title = "TETRIS";
                Font titleFont = new Font("Arial", Font.BOLD, 80); // Fonte maior
                g.setFont(titleFont);
                FontMetrics fm = g.getFontMetrics();

                int totalWidth = fm.stringWidth(title);
                int startX = (getWidth() - totalWidth) / 2;
                // Centraliza verticalmente
                int startY = fm.getAscent() + (getHeight() - fm.getHeight()) / 2;

                // Desenha letra por letra
                for (int i = 0; i < title.length(); i++) {
                    char c = title.charAt(i);
                    g.setColor(TETRIS_COLORS[i % TETRIS_COLORS.length]); // Pega uma cor
                    g.drawString(String.valueOf(c), startX, startY);
                    startX += fm.charWidth(c);
                }
            }
        };
        titlePanel.setBackground(Color.BLACK); // Fundo do painel do título
        titlePanel.setPreferredSize(new Dimension(800, 150));
        titlePanel.setMaximumSize(new Dimension(800, 150));

        // --- 2. ESTILO DO HIGH SCORE ---
        highScoreLabel = new JLabel("Recorde Local: 0");
        highScoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScoreLabel.setForeground(Color.WHITE); // Fonte branca
        updateHighScore();

        // --- 3. ESTILO DOS BOTÕES ---
        JButton newOnePlayerButton = new JButton("Novo Jogo (1P)");
        styleButton(newOnePlayerButton); // Aplica o novo estilo
        newOnePlayerButton.addActionListener(e -> gameManager.startNewOnePlayerGame());

        JButton loadGameButton = new JButton("Carregar Jogo (1P)");
        styleButton(loadGameButton);
        loadGameButton.addActionListener(e -> showLoadGameDialog());

        JButton twoPlayerButton = new JButton("2 Jogadores");
        styleButton(twoPlayerButton);
        twoPlayerButton.addActionListener(e -> gameManager.startTwoPlayerGame());

        JButton ranking1PButton = new JButton("Ranking Pontuação (1P)");
        styleButton(ranking1PButton);
        ranking1PButton.addActionListener(e -> show1PRanking());

        JButton ranking2PButton = new JButton("Ranking Vitórias (2P)");
        styleButton(ranking2PButton);
        ranking2PButton.addActionListener(e -> show2PRanking());

        // --- 4. LAYOUT (Montagem) ---
        add(Box.createVerticalGlue());
        add(titlePanel); // Adiciona o novo painel de título
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(highScoreLabel);
        add(Box.createRigidArea(new Dimension(0, 40)));
        add(newOnePlayerButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(loadGameButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(twoPlayerButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(ranking1PButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(ranking2PButton);
        add(Box.createVerticalGlue());
    }

    /**
     * Um método helper para aplicar o novo estilo verde e branco aos botões.
     */
    private void styleButton(JButton button) {
        Font buttonFont = new Font("Arial", Font.BOLD, 22);
        button.setFont(buttonFont);

        // Cor verde (um pouco mais escura que o verde-limão da borda)
        button.setBackground(new Color(0, 180, 0));
        button.setForeground(Color.WHITE); // Fonte branca

        // Truques do Swing para fazer a cor de fundo funcionar
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusable(false);

        // Define um tamanho fixo e agradável
        Dimension size = new Dimension(400, 50);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    // --- (Os métodos updateHighScore, showLoadGameDialog, show1PRanking, e show2PRanking permanecem exatamente iguais) ---

    public void updateHighScore() {
        highScoreLabel.setText("Recorde Local: " + gameManager.getHighScore());
    }

    private void showLoadGameDialog() {
        List<String> saveNames = gameManager.getSavedGameNames();
        if (saveNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum jogo salvo encontrado.", "Carregar Jogo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object[] options = saveNames.toArray();
        String selectedSave = (String)JOptionPane.showInputDialog(
                this, "Escolha um jogo para carregar:", "Carregar Jogo",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0] );
        if (selectedSave != null && !selectedSave.isEmpty()) {
            gameManager.loadGame(selectedSave);
        }
    }

    private void show1PRanking() {
        List<String> topScores = gameManager.getDatabaseManager().getTopScores();
        if (topScores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma pontuação no ranking ainda!", "Ranking 1P", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder rankingText = new StringBuilder("--- TOP 10 PONTUADORES ---\n\n");
        for (String scoreEntry : topScores) {
            rankingText.append(scoreEntry).append("\n");
        }
        JTextArea textArea = new JTextArea(rankingText.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Ranking Pontuação (1P)", JOptionPane.PLAIN_MESSAGE);
    }

    private void show2PRanking() {
        List<String> topWinners = gameManager.getDatabaseManager().getTopWinners();
        if (topWinners.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma vitória registrada ainda!", "Ranking 2P", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder rankingText = new StringBuilder("--- TOP 10 VENCEDORES ---\n\n");
        for (String winnerEntry : topWinners) {
            rankingText.append(winnerEntry).append("\n");
        }
        JTextArea textArea = new JTextArea(rankingText.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Ranking Vitórias (2P)", JOptionPane.PLAIN_MESSAGE);
    }
}