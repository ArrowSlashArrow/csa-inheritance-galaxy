import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class ScrollHandler implements MouseWheelListener {
    // when true, there are scroll wheel events that still need to be handled.
    public boolean unhandled_events = false;
    public boolean scrolled_up = false;
    public boolean scrolled_down = false;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        this.unhandled_events = true;

        if (notches < 0) {
            this.scrolled_up = true;
        } else {
            this.scrolled_down = true;
        }
    }

    public void reset_state() {
        this.unhandled_events = false;
        this.scrolled_down = false;
        this.scrolled_up = false;
    }
}