public class Antiball extends Ball {
    public Antiball(double x, double y, double mass) {
        super(x, y, mass);
    }

    @Override
    public void apply_accel(double ax, double ay) {
        this.dx -= ax;
        this.dy -= ay;
    }
}
