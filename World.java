import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class World {
    public ArrayList<Ball> balls = new ArrayList<>();

    public World(int ball_count, float pmass) {
        this.balls = new ArrayList<>();
        for (int i = 0; i < ball_count; i++) {
            balls.add(new Ball(
                    (int) (Math.random() * (Consts.WINDOW_WIDTH / 2) + (Consts.WINDOW_WIDTH / 4)),
                    (int) (Math.random() * (Consts.WINDOW_HEIGHT / 2) + (Consts.WINDOW_HEIGHT / 4)),
                    pmass));
        }
    }

    public void updatePhysics() {
        for (Ball i : this.balls) {
            double net_ax = 0;
            double net_ay = 0;

            for (Ball j : this.balls) {
                if (i == j) { // dist would be 0
                    continue;
                }
                double dx = j.xpos - i.xpos;
                double dy = j.ypos - i.ypos;
                double dist = Math.max(Math.hypot(dx, dy), 0.00001); // epsilon
                // multiply by particle mass to get force
                // faster to multiply at the end
                net_ax += dx / dist; // * j.mass;
                net_ay += dy / dist; // * j.mass;
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
            if (i.getting_deleted) {
                continue;
            }
            Ball colliding_with = null;
            for (Ball j : this.balls) {
                if (i == j || j.getting_deleted) { // dist would be 0
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
        g2.setColor(Color.white);
        for (Ball b : this.balls) {
            g2.fillOval((int) ((b.xpos - cam.x) / cam.zoom), (int) ((b.ypos - cam.y) / cam.zoom),
                    b.radius, b.radius);
        }
    }

    public int get_particle_count() {
        return this.balls.size();
    }
}