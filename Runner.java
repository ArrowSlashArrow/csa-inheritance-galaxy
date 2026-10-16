import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class Runner extends JPanel {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final int FRAMERATE = 60;
    public static final double GRAVITY = 0.4;
    ArrayList<Ball> balls = new ArrayList<>();

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
        this.balls = new ArrayList<>();
        int particle_count = 500;
        for (int i = 0; i < particle_count; i++) {
            balls.add(new Ball(
                    (int) (Math.random() * (WINDOW_WIDTH / 2) + (WINDOW_WIDTH / 4)),
                    (int) (Math.random() * (WINDOW_HEIGHT / 2) + (WINDOW_HEIGHT / 4)),
                    0.001));
        }

        setBackground(Color.BLACK);
        Timer tung = new Timer(1000 / FRAMERATE, e -> updatePhysics());
        tung.start();
    }

    protected void updatePhysics() {
        for (Ball i : this.balls) {
            double net_ax = 0;
            double net_ay = 0;

            for (Ball j : this.balls) {
                if (i == j) {   // dist would be 0
                    continue;
                }
                double dx = j.xpos - i.xpos;
                double dy = j.ypos - i.ypos;
                double dist = Math.hypot(dx, dy);
                // multiply by particle mass to get force
                // faster to multiply at the end
                net_ax += dx / dist * j.mass;
                net_ay += dy / dist * j.mass;
            }
            i.apply_accel(net_ax * GRAVITY, net_ay * GRAVITY);
            i.update_position();
        }

        // check for collisions
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
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

        super.repaint();
        g2.setColor(Color.white);
        for (Ball b : this.balls) {
            g2.fillOval((int) b.xpos, (int) b.ypos, b.radius, b.radius);
        }
    }
}

class Ball {
    public double xpos;
    public double ypos;
    public int radius;
    public double dx;
    public double dy;
    public double mass = 0.02;
    public boolean getting_deleted = false;

    public Ball(double x, double y, double mass) {
        this.xpos = x;
        this.ypos = y;
        this.mass = mass;
        this.update_radius();
    }

    public void update_position() {
        this.xpos += this.dx;
        this.ypos += this.dy;
    }

    public void apply_accel(double ax, double ay) {
        this.dx += ax;
        this.dy += ay;
    }

    public void update_radius() {
       this.radius = (int)(Math.sqrt(mass) * 50);
    }
}