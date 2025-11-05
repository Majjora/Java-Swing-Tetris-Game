// Em GameStateData.java
// (Não precisa de 'import's, esta é uma classe de dados pura)

public class GameStateData {
    // String[][] é mais seguro para JSON do que Tetromino[][]
    public String[][] boardGrid;

    public String currentPieceName;
    public int currentX;
    public int currentY;
    public int currentRotation;

    public String nextPieceName;

    public int score;
    public int level;
    public int linesCleared;

    // Adicionamos o tema também, para uma restauração completa
    public String currentThemeName;

    // Construtor vazio é necessário para o Gson
    public GameStateData() { }
}