import java.awt.*;

import javax.swing.*;

public class Runner extends JPanel {
    private Debug perf = new Debug();
    private World world;
    private Camera camera = new Camera(Consts.WINDOW_WIDTH / 2, Consts.WINDOW_HEIGHT / 2, 1.0);
    private static Keybinds kb = new Keybinds();
    private static ScrollHandler sc = new ScrollHandler();

    private boolean paused = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("not a virus");
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
        this.world = new World(500, 0.05f);
        this.run_world();
    }

    private void run_world() {
        setBackground(Color.BLACK);
        Timer tung = new Timer(1000 / Consts.FRAMERATE, e -> {
            if (!this.paused) {
                perf.start_physics_time = System.nanoTime();
                this.world.updatePhysics();
                perf.compute_physics_delta();
            }

            this.handle_events();
        });
        tung.start();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        super.repaint();
        this.world.render(g2, this.camera);

        g2.setColor(Color.white);

        // debug texts
        g2.drawString(String.format("Physics: %.3fms", (double) (this.perf.physics_delta) / 1000000.0), 1, 11);
        g2.drawString(String.format("Balls: %d", this.world.get_particle_count()), 1, 21);
        g2.drawString(String.format("Camera: (%.2f, %.2f) %.2fx", this.camera.x, this.camera.y, this.camera.zoom), 1,
                31);
        if (this.paused) {
            g2.drawString("[PAUSED]", 1, 41);
        }

        // this.cam_zoom *= 1.02;
    }

    private void handle_events() {
        double move_amount = 5.0 / this.camera.zoom;
        if (this.kb.shift) {
            move_amount *= 5.0;
        }

        if (this.kb.w_pressed) {
            this.camera.move_vertical(-move_amount);
        }
        if (this.kb.s_pressed) {
            this.camera.move_vertical(move_amount);
        }
        if (this.kb.a_pressed) {
            this.camera.move_horizontal(-move_amount);
        }
        if (this.kb.d_pressed) {
            this.camera.move_horizontal(move_amount);
        }

        if (this.kb.space) {
            this.paused = !this.paused;
        }

        if (this.sc.unhandled_events) {
            if (this.sc.scrolled_down) {
                this.camera.zoom *= 0.9;
            }
            if (this.sc.scrolled_up) {
                this.camera.zoom /= 0.9;
            }
            this.sc.reset_state();
        }
    }
}

// debug info container
class Debug {
    long start_physics_time;
    long end_physics_time;
    long physics_delta;

    public void compute_physics_delta() {
        this.end_physics_time = System.nanoTime();
        this.physics_delta = this.end_physics_time - this.start_physics_time;
    }
}
