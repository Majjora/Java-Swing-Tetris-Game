// Em ColorEditorDialog.java
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

public class ColorEditorDialog extends JDialog {

    private final ThemeManager themeManager;
    private final GameEngine engine;

    // Guarda os "previews" de cor para que possamos atualizá-los
    private final Map<Tetromino, JLabel> previews = new HashMap<>();

    public ColorEditorDialog(JFrame parent, ThemeManager themeManager, GameEngine engine) {
        super(parent, "Editor de Tema Personalizado", true); // 'true' = modal
        this.themeManager = themeManager;
        this.engine = engine;

        setLayout(new BorderLayout());
        setSize(400, 350);
        setLocationRelativeTo(parent);

        // Painel para os botões de cor
        JPanel gridPanel = new JPanel(new GridLayout(Tetromino.values().length, 3, 10, 10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Cria uma linha de edição para cada peça
        for (Tetromino piece : Tetromino.values()) {
            JLabel nameLabel = new JLabel("Peça " + piece.name() + ":");
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

            // Um "preview" da cor atual
            JLabel colorPreview = new JLabel("■"); // Um quadrado
            colorPreview.setFont(new Font("Arial", Font.BOLD, 24));
            colorPreview.setForeground(themeManager.getCustomColor(piece));
            previews.put(piece, colorPreview); // Salva a referência

            // Botão para abrir o JColorChooser
            JButton editButton = new JButton("Escolher Cor...");
            editButton.addActionListener(e -> chooseColor(piece));

            gridPanel.add(nameLabel);
            gridPanel.add(colorPreview);
            gridPanel.add(editButton);
        }

        add(gridPanel, BorderLayout.CENTER);

        // Botão para fechar o diálogo
        JButton closeButton = new JButton("Fechar");
        closeButton.addActionListener(e -> setVisible(false));
        JPanel southPanel = new JPanel();
        southPanel.add(closeButton);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void chooseColor(Tetromino piece) {
        // Pega a cor atual para pré-selecionar no JColorChooser
        Color currentColor = themeManager.getCustomColor(piece);

        // Abre o seletor de cores!
        Color newColor = JColorChooser.showDialog(this, "Escolha a cor para a Peça " + piece.name(), currentColor);

        // Atualiza o themeManager com a nova cor
        themeManager.setCustomColor(piece, newColor);

        // Atualiza o preview de cor dentro deste diálogo
        previews.get(piece).setForeground(newColor);

        // Se o jogador estiver usando o tema "Personalizado" AGORA,
        // força o jogo a redesenhar imediatamente
        if (themeManager.getCurrentThemeName().equals(ThemeManager.CUSTOM_THEME_NAME)) {
            engine.getGamePanel().repaint();
        }
    }
}