// Em Board.java
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Board {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;

    private final Tetromino[][] grid;
    private final Random random = new Random();

    public Board() {
        grid = new Tetromino[HEIGHT][WIDTH];
    }

    public boolean isValidPosition(int[][] shape, int posX, int posY) {
        if (shape == null) return false; // Proteção
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {
                    int boardX = posX + x;
                    int boardY = posY + y;
                    if (boardX < 0 || boardX >= WIDTH || boardY < 0 || boardY >= HEIGHT) {
                        return false;
                    }
                    if (grid[boardY][boardX] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void placePiece(int[][] shape, int posX, int posY, Tetromino pieceType) {
        if (shape == null) return; // Proteção
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {
                    if (posY + y >= 0 && posY + y < HEIGHT && posX + x >= 0 && posX + x < WIDTH) {
                        grid[posY + y][posX + x] = pieceType;
                    }
                }
            }
        }
    }

    // Lógica de Lixo 2P
    public void addGarbageLines(int lineCount) {
        // Desloca o grid para CIMA
        for(int y = 0; y < HEIGHT - lineCount; y++) {
            System.arraycopy(grid[y + lineCount], 0, grid[y], 0, WIDTH);
        }
        // Preenche as novas linhas de baixo com lixo
        for (int y = HEIGHT - lineCount; y < HEIGHT; y++) {
            int hole = random.nextInt(WIDTH);
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = (x == hole) ? null : Tetromino.O;
            }
        }
    }

    // Lógica de Limpeza de Linha (para Animação)
    public List<Integer> findFullLines() {
        List<Integer> fullLines = new ArrayList<>();
        for (int y = HEIGHT - 1; y >= 0; y--) {
            boolean lineIsFull = true;
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] == null) {
                    lineIsFull = false;
                    break;
                }
            }
            if (lineIsFull) {
                fullLines.add(y);
            }
        }
        return fullLines;
    }

    public void executeLineClearance(List<Integer> linesToClear) {
        if (linesToClear.isEmpty()) return;
        linesToClear.sort(null);
        for (int y : linesToClear) {
            for (int row = y; row > 0; row--) {
                System.arraycopy(grid[row - 1], 0, grid[row], 0, WIDTH);
            }
            for (int x = 0; x < WIDTH; x++) {
                grid[0][x] = null;
            }
        }
    }

    public Tetromino[][] getGrid() {
        return grid;
    }

    public void reset() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = null;
            }
        }
    }
}