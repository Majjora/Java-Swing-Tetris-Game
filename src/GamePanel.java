// Em GamePanel.java
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.List;

public class GamePanel extends JPanel {
    private static final int TILE_SIZE = 30;
    private final GameEngine engine;
    private final ThemeManager themeManager;

    public GamePanel(GameEngine engine, ThemeManager themeManager) {
        this.engine = engine;
        this.themeManager = themeManager;
        setPreferredSize(new Dimension(Board.WIDTH * TILE_SIZE, Board.HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);

        if (!engine.isAnimatingLineClear()) {
            drawGhostPiece(g);
            drawCurrentPiece(g);
        }

        drawGridLines(g);

        // --- FLASH DE ATERRISSAGEM REMOVIDO DAQUI ---
        // if (engine.getLockFlash() > 0) {
        //     drawLockFlash(g);
        // }

        // Lógica de Overlay (Vencedor, Fim de Jogo, Pausado)
        if (engine.isWinner()) {
            drawOverlay(g, "VENCEDOR!");
        } else if (engine.isGameOver()) {
            drawOverlay(g, "FIM DE JOGO");
        } else if (engine.isPaused()) {
            drawOverlay(g, "PAUSADO");
        }
    }

    private void drawBoard(Graphics g) {
        // ... (Este método permanece exatamente o mesmo de antes)
        Tetromino[][] grid = engine.getBoard().getGrid();
        boolean isAnimating = engine.isAnimatingLineClear();
        List<Integer> linesToClear = engine.getLinesToClear();
        boolean showFlash = isAnimating && (engine.getAnimationCounter() / 10) % 2 != 1;

        for (int y = 0; y < Board.HEIGHT; y++) {
            if (isAnimating && linesToClear.contains(y)) {
                if (showFlash) {
                    g.setColor(Color.WHITE);
                    g.fillRect(0, y * TILE_SIZE, Board.WIDTH * TILE_SIZE, TILE_SIZE);
                } else {
                    for (int x = 0; x < Board.WIDTH; x++) {
                        if (grid[y][x] != null) {
                            Color color = themeManager.getColor(grid[y][x]);
                            drawTile(g, x, y, color);
                        }
                    }
                }
            } else {
                for (int x = 0; x < Board.WIDTH; x++) {
                    if (grid[y][x] != null) {
                        Color color = themeManager.getColor(grid[y][x]);
                        drawTile(g, x, y, color);
                    }
                }
            }
        }
    }

    private void drawGhostPiece(Graphics g) {
        // ... (Este método permanece exatamente o mesmo de antes)
        Color pieceColor = engine.getCurrentPieceColor();
        if (pieceColor == null) return;
        Color ghostColor = new Color(pieceColor.getRed(), pieceColor.getGreen(), pieceColor.getBlue(), 50);
        g.setColor(ghostColor);
        int[][] shape = engine.getCurrentPieceShape();
        int posX = engine.getCurrentPieceX();
        int posY = engine.getGhostY();
        if (posY <= engine.getCurrentPieceY()) {
            return;
        }
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {
                    g.fillRect((posX + x) * TILE_SIZE, (posY + y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    // --- drawCurrentPiece() (Sem alteração, o flash de rotação continua) ---
    private void drawCurrentPiece(Graphics g) {
        int[][] shape = engine.getCurrentPieceShape();

        Color color;
        if (engine.getRotationFlash() > 0) {
            color = Color.WHITE; // Pisca em branco
        } else {
            color = engine.getCurrentPieceColor();
        }

        if (shape == null || color == null) {
            return;
        }

        int posX = engine.getCurrentPieceX();
        int posY = engine.getCurrentPieceY();

        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {
                    drawTile(g, posX + x, posY + y, color);
                }
            }
        }
    }

    private void drawGridLines(Graphics g) {
        // ... (Este método permanece exatamente o mesmo de antes)
        g.setColor(Color.DARK_GRAY);
        for (int x = 0; x < Board.WIDTH + 1; x++) {
            g.drawLine(x * TILE_SIZE, 0, x * TILE_SIZE, Board.HEIGHT * TILE_SIZE);
        }
        for (int y = 0; y < Board.HEIGHT + 1; y++) {
            g.drawLine(0, y * TILE_SIZE, Board.WIDTH * TILE_SIZE, y * TILE_SIZE);
        }
    }

    private void drawTile(Graphics g, int x, int y, Color color) {
        // ... (Este método permanece exatamente o mesmo de antes)
        if (color == null) return;
        g.setColor(color);
        g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        g.setColor(color.darker());
        g.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    // --- MÉTODO drawLockFlash() REMOVIDO ---

    private void drawOverlay(Graphics g, String text) {
        // ... (Este método permanece exatamente o mesmo de antes)
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(30f));
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
    }
}