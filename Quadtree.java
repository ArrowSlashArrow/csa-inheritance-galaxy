import java.util.ArrayList;
import java.util.List;

public class Quadtree {
    private static final int CAPACITY = 8;
    private static final int MAX_DEPTH = 10;

    private final double x, y, width, height;
    private final int depth;

    private final List<Ball> balls = new ArrayList<>();
    private Quadtree[] children; // null until this node splits

    public Quadtree(double x, double y, double width, double height, int depth) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public void insert(Ball ball) {
        if (!contains_center(ball)) {
            return;
        }

        if (children != null) {
            children[child_index(ball)].insert(ball);
            return;
        }

        balls.add(ball);

        if (balls.size() > CAPACITY && depth < MAX_DEPTH) {
            // split into subtrees
            split();
            for (int i = 0; i < balls.size(); i++) {
                Ball existing = balls.get(i);
                int child_idx = child_index(existing);

                if (child_idx >= 0) {
                    balls.remove(i);
                    children[child_idx].insert(existing);
                    i--;
                }
            }
        }
    }

    private boolean contains_center(Ball b) {
        return b.xpos >= this.x && b.xpos < this.x + this.width && b.ypos >= this.y && b.ypos < this.y + this.height;
    }

    private void split() {
        double half_width = this.width / 2.0;
        double half_height = this.height / 2.0;

        children = new Quadtree[] {
                new Quadtree(this.x, this.y, half_width, half_height, this.depth + 1), // top left
                new Quadtree(this.x + half_width, this.y, half_width, half_height, this.depth + 1), // top right
                new Quadtree(this.x, this.y + half_height, half_width, half_height, this.depth + 1), // bottom left
                new Quadtree(this.x + half_width, this.y + half_height, half_width, half_height, this.depth + 1) // bottom-right
        };
    }

    private int child_index(Ball b) {
        double midX = x + width / 2.0;
        double midY = y + height / 2.0;

        boolean left = b.xpos < midX;
        boolean top = b.ypos < midY;

        if (left && top)
            return 0;
        if (!left && top)
            return 1;
        if (left)
            return 2;
        return 3;
    }
}
