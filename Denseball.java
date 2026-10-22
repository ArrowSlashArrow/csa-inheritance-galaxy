public class Denseball extends Ball {
    public Denseball(double x, double y, double mass) {
        super(x, y, mass);
    }

    @Override
    public void update_radius() {
        this.radius = Math.max(1, (int) (Math.sqrt(this.mass)));
    }
}
