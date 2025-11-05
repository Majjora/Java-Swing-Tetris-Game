// Em HighScoreManager.java
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class HighScoreManager {
    private int highScore;
    private static final String FILE_NAME = "highscore.txt";

    public HighScoreManager() {
        this.highScore = loadHighScore();
    }

    private int loadHighScore() {
        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextInt()) {
                    int score = scanner.nextInt();
                    scanner.close();
                    return score;
                }
                scanner.close();
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o high score: " + e.getMessage());
        }
        return 0; // Retorna 0 se não houver arquivo ou der erro
    }

    private void saveHighScore() {
        try {
            FileWriter writer = new FileWriter(FILE_NAME);
            writer.write(String.valueOf(highScore));
            writer.close();
        } catch (IOException e) {
            System.err.println("Erro ao salvar o high score: " + e.getMessage());
        }
    }

    public void checkAndSetHighScore(int newScore) {
        if (newScore > highScore) {
            this.highScore = newScore;
            saveHighScore();
        }
    }

    public int getHighScore() {
        return highScore;
    }
}