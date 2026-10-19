import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class Runner extends JPanel {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final int FRAMERATE = 60;
    public static final double GRAVITY = 0.000001;
    
    private Debug perf = new Debug();
    ArrayList<Ball> balls = new ArrayList<>();

    private double cam_zoom = 1.0;
    private double cam_x = WINDOW_WIDTH / 2;
    private double cam_y = WINDOW_HEIGHT / 2;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("your mother");
            // inception
            Runner d = new Runner();
            window.add(d);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }

    public Runner() {
        this.init_world();
        this.run_world();
    }

    private void init_world() {
        this.balls = new ArrayList<>();
        int particle_count = 5000;
        float pmass = 0.02f;
        for (int i = 0; i < particle_count; i++) {
            balls.add(new Ball(
                    (int) (Math.random() * (WINDOW_WIDTH / 2) + (WINDOW_WIDTH / 4)),
                    (int) (Math.random() * (WINDOW_HEIGHT / 2) + (WINDOW_HEIGHT / 4)),
                    pmass));
        }
    }

    private void run_world() {
        setBackground(Color.BLACK);
        Timer tung = new Timer(1000 / FRAMERATE, e -> this.updatePhysics());
        tung.start();
    }

    protected void updatePhysics() {
        perf.start_physics_time = System.nanoTime();
        for (Ball i : this.balls) {
            double net_ax = 0;
            double net_ay = 0;

            for (Ball j : this.balls) {
                if (i == j) {   // dist would be 0
                    continue;
                }
                double dx = j.xpos - i.xpos;
                double dy = j.ypos - i.ypos;
                double dist = Math.max(Math.hypot(dx, dy), 0.00001);    // epsilon
                // multiply by particle mass to get force
                // faster to multiply at the end
                net_ax += dx / dist; // * j.mass;
                net_ay += dy / dist; // * j.mass;
            }
            i.apply_accel(net_ax * GRAVITY / i.mass, net_ay * GRAVITY / i.mass);
            i.update_position();
        }

        // check for collisions
        this.handle_collisions();
        
        perf.compute_physics_delta();
    }

    // balls are merged if they collide
    private void handle_collisions() {
        for (Ball i : this.balls) {
            if (i.getting_deleted) {
                continue;
            }
            Ball colliding_with = null;
            for (Ball j : this.balls) {
                if (i == j || j.getting_deleted) {   // dist would be 0
                    continue;
                }
                double dx = j.xpos - i.xpos;
                double dy = j.ypos - i.ypos;
                double dist = Math.hypot(dx, dy);
                if (dist < Math.min(i.radius, j.radius)) {
                    colliding_with = j;
                    j.getting_deleted = true;
                    break;
                }
            }

            if (colliding_with != null) {
                i.mass += colliding_with.mass;
                i.update_radius();
            } 
        }

        this.balls.removeIf((ball) -> ball.getting_deleted);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        super.repaint();
        g2.setColor(Color.white);
        for (Ball b : this.balls) {
            g2.fillOval((int)((b.xpos - this.cam_x) / this.cam_zoom), (int)((b.ypos - this.cam_y) / this.cam_zoom), b.radius, b.radius);
        }

        g2.drawString(String.format("Physics: %.3fms", (double)(this.perf.physics_delta) / 1000000.0), 10, 10);
        g2.drawString(String.format("Balls: %d", this.balls.size()), 10, 20);

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