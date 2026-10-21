import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.*;

public class Runner extends JPanel {
    private Debug perf = new Debug();
    private World world;
    private Camera camera = new Camera(Consts.WINDOW_WIDTH / 2, Consts.WINDOW_HEIGHT / 2, 1.0);
    private static Keybinds kb = new Keybinds();
    private static ScrollHandler sc = new ScrollHandler();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("your mother");
            // inception
            Runner d = new Runner();
            window.add(d);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setSize(Consts.WINDOW_WIDTH, Consts.WINDOW_HEIGHT);
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            KeyboardFocusManager.setCurrentKeyboardFocusManager(kb);
            window.addMouseWheelListener(sc);
        });
    }

    public Runner() {
        this.world = new World(5000, 0.02f);
        this.run_world();
    }

    private void run_world() {
        setBackground(Color.BLACK);
        Timer tung = new Timer(1000 / Consts.FRAMERATE, e -> {
            perf.start_physics_time = System.nanoTime();
            this.world.updatePhysics();
            perf.compute_physics_delta();
        });
        tung.start();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        super.repaint();
        this.world.render(g2, this.camera);

        // debug texts
        g2.drawString(String.format("Physics: %.3fms", (double) (this.perf.physics_delta) / 1000000.0), 10, 10);
        g2.drawString(String.format("Balls: %d", this.world.get_particle_count()), 10, 20);

        // this.cam_zoom *= 1.02;
    }
}

// debug info container
class Debug {
    long start_physics_time;
    long end_physics_time;
    // long start_render_time;
    // long end_render_time;

    long physics_delta;

    public void compute_physics_delta() {
        this.end_physics_time = System.nanoTime();
        this.physics_delta = this.end_physics_time - this.start_physics_time;
    }
}

class Keybinds extends DefaultKeyboardFocusManager {
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

class ScrollHandler implements MouseWheelListener {
    // when true, there are scroll wheel events that still need to be handled.
    public boolean unhandled_events = false;
    public boolean scrolled_up = false;
    public boolean scrolled_down = false;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        this.unhandled_events = true;

        if (notches < 0) {
            this.scrolled_up = true;
        } else {
            this.scrolled_down = true;
        }
    }
}