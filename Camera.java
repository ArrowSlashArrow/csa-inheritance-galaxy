public class Camera {
    public double x;
    public double y;
    public double zoom;

    public Camera(double x, double y, double zoom) {
        this.x = x;
        this.y = y;
        this.zoom = zoom;
    }

    public void move_vertical(double units) {
        this.y += units;
    }

    public void move_horizontal(double units) {
        this.x += units;
    }
}
