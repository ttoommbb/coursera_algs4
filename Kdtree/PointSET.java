import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;

public class PointSET {

    private RedBlackBST<Point2D, Point2D> tree;

    /** construct an empty set of points */
    public PointSET() {
        tree = new RedBlackBST<>();
    }

    /** is the set empty? */
    public boolean isEmpty() {
        return tree.isEmpty();
    }

    /** number of points in the set */
    public int size() {
        return tree.size();
    }

    /** add the point to the set (if it is not already in the set) */
    public void insert(Point2D p) {
        tree.put(p, p);
    }

    /** does the set contain point p? */
    public boolean contains(Point2D p) {
        return tree.contains(p);
    }

    /** draw all points to standard draw */
    public void draw() {
        for (Point2D p : tree.keys()) {
            p.draw();
        }
    }

    /** all points that are inside the rectangle (or on the boundary) */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        Point2D downLeft = new Point2D(rect.xmin(), rect.ymin());
        Point2D topRight = new Point2D(rect.xmax(), rect.ymax());

        int startIndex = tree.rank(downLeft);
        int endIndex = tree.rank(topRight);
        Queue<Point2D> inRange = new Queue<>();
        for (int index = startIndex; index < endIndex; index++) {
            Point2D testP = tree.select(index);
            if (testP.x() >= rect.xmin() && testP.x() <= rect.xmax()) {
                inRange.enqueue(testP);
            }
        }
        if (tree.contains(topRight)) {
            inRange.enqueue(topRight);
        }
        return inRange;
    }

    /** a nearest neighbor in the set to point p; null if the set is empty */
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (isEmpty()) {
            return null;
        }

        Point2D floor = tree.floor(p);
        if (floor == null) {
            floor = tree.ceiling(p);
        }

        double minDistSquare = p.distanceSquaredTo(floor);
        int startIndex = tree.rank(new Point2D(p.x(), p.y() - Math.sqrt(minDistSquare)));
        int endIndex = tree.rank(new Point2D(p.x(), p.y() + Math.sqrt(minDistSquare)));

        for (int i = startIndex; i < endIndex; i++) {
            Point2D testP = tree.select(i);
            double testDistSqrt = testP.distanceSquaredTo(p);
            if (testDistSqrt < minDistSquare) {
                minDistSquare = testDistSqrt;
                floor = testP;
                endIndex = tree.rank(new Point2D(p.x(), p.y() + Math.sqrt(minDistSquare)));
            }
        }
        return floor;
    }

    /** unit testing of the methods (optional) */
    public static void main(String[] args) {
    }
}
