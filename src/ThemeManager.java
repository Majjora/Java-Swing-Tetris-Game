// Em ThemeManager.java
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ThemeManager {

    public static final String CUSTOM_THEME_NAME = "Personalizado";
    private final Map<String, Map<Tetromino, Color>> themes = new HashMap<>();
    private String currentThemeName;
    private final Map<Tetromino, Color> customTheme = new HashMap<>();
    private final Properties themeProperties = new Properties();

    // --- NOVA LÓGICA DE MODO DE INTERFACE (CLARO/ESCURO) ---
    public enum UIMode { LIGHT, DARK }
    private UIMode currentUIMode = UIMode.DARK; // Padrão para Escuro (combinando com o menu)
    // ---------------------------------------------------

    public ThemeManager() {
        // ... (Temas padrão: Clássico, Moderno, Floresta)
        Map<Tetromino, Color> classic = new HashMap<>();
        classic.put(Tetromino.I, new Color(0, 240, 240));
        classic.put(Tetromino.J, new Color(0, 0, 240));
        classic.put(Tetromino.L, new Color(240, 160, 0));
        classic.put(Tetromino.O, new Color(240, 240, 0));
        classic.put(Tetromino.S, new Color(0, 240, 0));
        classic.put(Tetromino.T, new Color(160, 0, 240));
        classic.put(Tetromino.Z, new Color(240, 0, 0));
        themes.put("Clássico", classic);

        Map<Tetromino, Color> modern = new HashMap<>();
        modern.put(Tetromino.I, new Color(50, 200, 255));
        modern.put(Tetromino.J, new Color(50, 50, 255));
        modern.put(Tetromino.L, new Color(255, 150, 50));
        modern.put(Tetromino.O, new Color(255, 210, 50));
        modern.put(Tetromino.S, new Color(50, 255, 50));
        modern.put(Tetromino.T, new Color(200, 50, 255));
        modern.put(Tetromino.Z, new Color(255, 50, 50));
        themes.put("Moderno", modern);

        Map<Tetromino, Color> forest = new HashMap<>();
        forest.put(Tetromino.I, new Color(135, 206, 235)); // Sky Blue
        forest.put(Tetromino.J, new Color(100, 149, 237)); // Cornflower Blue
        forest.put(Tetromino.L, new Color(188, 143, 143)); // Rosy Brown
        forest.put(Tetromino.O, new Color(218, 165, 32));  // Goldenrod
        forest.put(Tetromino.S, new Color(60, 179, 113));  // Medium Sea Green
        forest.put(Tetromino.T, new Color(128, 0, 128));   // Purple
        forest.put(Tetromino.Z, new Color(255, 127, 80));  // Coral
        themes.put("Floresta", forest);

        loadCustomTheme();
        themes.put(CUSTOM_THEME_NAME, customTheme);
        currentThemeName = "Clássico";
    }

    public Color getColor(Tetromino piece) {
        return themes.get(currentThemeName).get(piece);
    }

    public String[] getThemeNames() {
        return themes.keySet().toArray(new String[0]);
    }

    public void setCurrentTheme(String themeName) {
        this.currentThemeName = themeName;
    }

    public String getCurrentThemeName() {
        return currentThemeName;
    }

    public Color getCustomColor(Tetromino piece) {
        return customTheme.get(piece);
    }

    public void setCustomColor(Tetromino piece, Color color) {
        customTheme.put(piece, color);
        saveCustomTheme();
    }

    private void loadCustomTheme() {
        for (Tetromino piece : Tetromino.values()) {
            Color defaultColor = themes.get("Clássico").get(piece);
            try (FileInputStream fis = new FileInputStream("custom_theme.properties")) {
                themeProperties.load(fis);
                int rgb = Integer.parseInt(themeProperties.getProperty(piece.name(), String.valueOf(defaultColor.getRGB())));
                customTheme.put(piece, new Color(rgb));
            } catch (IOException e) {
                customTheme.put(piece, defaultColor);
            }
        }
    }

    private void saveCustomTheme() {
        try (FileOutputStream fos = new FileOutputStream("custom_theme.properties")) {
            for (Map.Entry<Tetromino, Color> entry : customTheme.entrySet()) {
                themeProperties.setProperty(entry.getKey().name(), String.valueOf(entry.getValue().getRGB()));
            }
            themeProperties.store(fos, "Cores do Tema Personalizado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- NOVOS MÉTODOS DE MODO DE INTERFACE ---

    public void toggleUIMode() {
        currentUIMode = (currentUIMode == UIMode.DARK) ? UIMode.LIGHT : UIMode.DARK;
    }

    public Color getPanelBackground() {
        return (currentUIMode == UIMode.DARK) ? new Color(40, 40, 40) : Color.LIGHT_GRAY;
    }

    public Color getPanelForeground() {
        return (currentUIMode == UIMode.DARK) ? Color.WHITE : Color.BLACK;
    }
}