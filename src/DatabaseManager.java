// Em DatabaseManager.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/tetris_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "tetris_user";
    private static final String DB_PASS = "tetris_pass";

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver MySQL (Connector/J) não encontrado!");
            e.printStackTrace();
        }
        createTables();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private void createTables() {
        // Tabela 1: Leaderboard 1P (sem alteração)
        String sqlLeaderboard1P = "CREATE TABLE IF NOT EXISTS leaderboard (\n"
                + " id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + " nickname VARCHAR(50) NOT NULL,\n"
                + " score INT NOT NULL\n"
                + ");";

        // Tabela 2: Jogos Salvos (sem alteração)
        String sqlSavedGames = "CREATE TABLE IF NOT EXISTS saved_games (\n"
                + " id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + " saveName VARCHAR(100) NOT NULL UNIQUE,\n"
                + " gameStateJSON TEXT NOT NULL\n"
                + ");";

        // --- NOVA TABELA 3: RANKING 2 JOGADORES ---
        String sqlLeaderboard2P = "CREATE TABLE IF NOT EXISTS leaderboard_2p (\n"
                + " id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + " nickname VARCHAR(50) NOT NULL UNIQUE,\n" // Nickname é a chave
                + " wins INT NOT NULL DEFAULT 0\n" // Começa com 0 vitórias
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlLeaderboard1P);
            stmt.execute(sqlSavedGames);
            stmt.execute(sqlLeaderboard2P); // Cria a nova tabela
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }

    // --- Métodos do Leaderboard 1P (sem alteração) ---
    public void addHighScore(String nickname, int score) {
        String sql = "INSERT INTO leaderboard(nickname, score) VALUES(?, ?)";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (SQLException e) { System.err.println("Erro ao salvar high score: " + e.getMessage()); }
    }

    public List<String> getTopScores() {
        String sql = "SELECT nickname, score FROM leaderboard ORDER BY score DESC LIMIT 10";
        List<String> scores = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int rank = 1;
            while (rs.next()) {
                scores.add(rank + ". " + rs.getString("nickname") + " - " + rs.getInt("score"));
                rank++;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler os scores: " + e.getMessage());
            scores.add("Erro ao conectar ao banco de dados!");
        }
        return scores;
    }

    // --- Métodos de Salvar/Carregar (sem alteração) ---
    public void saveGame(String saveName, String jsonState) {
        String sql = "INSERT INTO saved_games (saveName, gameStateJSON) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE gameStateJSON = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, saveName);
            pstmt.setString(2, jsonState);
            pstmt.setString(3, jsonState);
            pstmt.executeUpdate();
        } catch (SQLException e) { System.err.println("Erro ao salvar o jogo: " + e.getMessage()); }
    }

    public String loadGameJSON(String saveName) {
        String sql = "SELECT gameStateJSON FROM saved_games WHERE saveName = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, saveName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { return rs.getString("gameStateJSON"); }
            }
        } catch (SQLException e) { System.err.println("Erro ao carregar o jogo: " + e.getMessage()); }
        return null;
    }

    public List<String> getSavedGameNames() {
        String sql = "SELECT saveName FROM saved_games ORDER BY saveName";
        List<String> saveNames = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { saveNames.add(rs.getString("saveName")); }
        } catch (SQLException e) { System.err.println("Erro ao ler os nomes dos saves: " + e.getMessage()); }
        return saveNames;
    }

    public void deleteSaveGame(String saveName) {
        if (saveName == null || saveName.isEmpty()) return;
        String sql = "DELETE FROM saved_games WHERE saveName = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, saveName);
            pstmt.executeUpdate();
            System.out.println("Jogo salvo '" + saveName + "' deletado.");
        } catch (SQLException e) { System.err.println("Erro ao deletar o save: " + e.getMessage()); }
    }

    // --- NOVOS MÉTODOS PARA O RANKING 2P ---

    /**
     * Adiciona uma vitória a um jogador.
     * Se o jogador não existir, ele é criado com 1 vitória.
     * Se ele existir, seu contador de vitórias é incrementado.
     */
    public void addWin(String nickname) {
        // Comando do MySQL: Insere um novo, mas se a chave 'nickname' já existir,
        // ele executa o comando 'UPDATE' (wins = wins + 1).
        String sql = "INSERT INTO leaderboard_2p (nickname, wins) VALUES (?, 1) "
                + "ON DUPLICATE KEY UPDATE wins = wins + 1";

        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar vitória 2P: " + e.getMessage());
        }
    }

    /**
     * Retorna os 10 melhores jogadores 2P por número de vitórias.
     */
    public List<String> getTopWinners() {
        String sql = "SELECT nickname, wins FROM leaderboard_2p ORDER BY wins DESC LIMIT 10";
        List<String> winners = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int rank = 1;
            while (rs.next()) {
                int wins = rs.getInt("wins");
                String winText = (wins == 1) ? "vitória" : "vitórias"; // (1 vitória, 2 vitórias)
                winners.add(rank + ". " + rs.getString("nickname") + " - " + wins + " " + winText);
                rank++;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler o ranking 2P: " + e.getMessage());
            winners.add("Erro ao conectar ao banco de dados!");
        }
        return winners;
    }
}