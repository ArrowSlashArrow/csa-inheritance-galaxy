public class Ball {
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
        this.radius = Math.max(1, (int)(Math.sqrt(this.mass) * 10));
    }
}