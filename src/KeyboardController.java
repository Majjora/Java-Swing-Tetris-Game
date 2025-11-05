// Em KeyboardController.java
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardController extends KeyAdapter {

    private final GameManager gameManager;

    public KeyboardController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Apenas repassa o código da tecla para o manager
        gameManager.handleKeyPress(e.getKeyCode());
    }
}