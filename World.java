import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class World {
    public ArrayList<Ball> balls = new ArrayList<>();

    public World(int ball_count, float pmass) {
        this.balls = new ArrayList<>();
        for (int i = 0; i < ball_count; i++) {
            int x = (int) (Math.random() * (Consts.WINDOW_WIDTH / 2) + (Consts.WINDOW_WIDTH / 4));
            int y = (int) (Math.random() * (Consts.WINDOW_HEIGHT / 2) + (Consts.WINDOW_HEIGHT / 4));

            int ball_type = (int) (Math.random() * 1000);

            if (ball_type < 10) {
                balls.add(new Antiball(x, y, pmass));
            } else if (ball_type < 15) {
                balls.add(new Denseball(x, y, pmass * 1000));
            } else {
                balls.add(new Ball(x, y, pmass));
            }
        }
    }

    public void updatePhysics() {
        for (Ball i : this.balls) {
            double net_ax = 0;
            double net_ay = 0;

            double force_multiplier = 1; // antiparticles gotta have a stronger effect
            if (i instanceof Antiball)
                force_multiplier *= -10;

            for (Ball j : this.balls) {
                if (i == j) { // dist would be 0
                    continue;
                }

                double fm = force_multiplier;
                if (j instanceof Antiball)
                    fm = -10;

                double dx = j.xpos - i.xpos;
                double dy = j.ypos - i.ypos;
                double dist = Math.max(Math.hypot(dx, dy), 0.00001); // epsilon
                // multiply by particle mass to get force
                // faster to multiply at the end
                net_ax += dx / dist * fm; // * j.mass;
                net_ay += dy / dist * fm; // * j.mass;
            }
            i.apply_accel(net_ax * Consts.GRAVITY / i.mass, net_ay * Consts.GRAVITY / i.mass);
            i.update_position();
        }

        // check for collisions
        this.handle_collisions();
    }

    // balls are merged if they collide
    private void handle_collisions() {
        for (Ball i : this.balls) {
            if (i.getting_deleted || (i instanceof Denseball)) {
                continue;
            }
            Ball colliding_with = null;
            for (Ball j : this.balls) {
                if (i == j || j.getting_deleted || (j instanceof Denseball)) {
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

    public void render(Graphics2D g2, Camera cam) {
        for (Ball b : this.balls) {
            if (b instanceof Antiball) {
                g2.setColor(Color.CYAN);
            } else if (b instanceof Denseball) {
                g2.setColor(Color.GRAY);
            } else {
                g2.setColor(Color.WHITE);
            }

            // this is supposed to be diameter, but I am rendering the particles smaller
            int radius = Math.max(1, (int) ((double) b.radius * cam.zoom));

            int screen_x = (int) ((b.xpos - cam.x) * cam.zoom + Consts.WINDOW_WIDTH / 2);
            int screen_y = (int) ((b.ypos - cam.y) * cam.zoom + Consts.WINDOW_HEIGHT / 2);

            g2.fillOval(screen_x, screen_y, radius, radius);
        }
    }

    public int get_particle_count() {
        return this.balls.size();
    }
}