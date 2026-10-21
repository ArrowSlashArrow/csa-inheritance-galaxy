import java.awt.DefaultKeyboardFocusManager;
import java.awt.event.KeyEvent;

public class Keybinds extends DefaultKeyboardFocusManager {
    public boolean w_pressed = false;
    public boolean a_pressed = false;
    public boolean s_pressed = false;
    public boolean d_pressed = false;

    @Override
    public boolean dispatchKeyEvent(KeyEvent ke) {
        switch (ke.getID()) {
            case KeyEvent.KEY_PRESSED:
                switch (ke.getKeyCode()) {
                    case KeyEvent.VK_W -> w_pressed = true;
                    case KeyEvent.VK_A -> a_pressed = true;
                    case KeyEvent.VK_S -> s_pressed = true;
                    case KeyEvent.VK_D -> d_pressed = true;
                }
                break;
            case KeyEvent.KEY_RELEASED:
                switch (ke.getKeyCode()) {
                    case KeyEvent.VK_W -> w_pressed = false;
                    case KeyEvent.VK_A -> a_pressed = false;
                    case KeyEvent.VK_S -> s_pressed = false;
                    case KeyEvent.VK_D -> d_pressed = false;
                }
                break;
        }
        return false;

    }
}