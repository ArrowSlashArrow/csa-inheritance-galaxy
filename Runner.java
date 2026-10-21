import java.awt.*;

import javax.swing.*;

public class Runner extends JPanel {
    private Debug perf = new Debug();
    private World world;
    private Camera camera = new Camera(0, 0, 1.0);
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
        this.world = new World(2000, 0.02f);
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
