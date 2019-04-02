import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;

public class KdTree {

    private Node root;
    private int size = 0;

    /** construct an empty set of points */
    public KdTree() {
        // tree = new RedBlackBST<>();
    }

    /** is the set empty? */
    public boolean isEmpty() {
        return size == 0;
    }

    /** number of points in the set */
    public int size() {
        return size;
    }

    /** add the point to the set (if it is not already in the set) */
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (root == null) {
            root = new Node(p, new RectHV(0, 0, 1, 1));
            size++;
        }

        Node findParent = root;
        boolean verticalSplit = true;
        while (true) {
            if (findParent.p.equals(p)) {
                return; // Do-nothing when p exist.
            }
            if (verticalSplit) {
                if (p.x() <= findParent.p.x()) {
                    // Insert left.
                    if (findParent.lb != null) {
                        findParent = findParent.lb;
                        verticalSplit = false;
                        continue;
                    }
                    findParent.lb = new Node(p, new RectHV(findParent.rect.xmin(),
                                                           findParent.rect.ymin(),
                                                           findParent.p.x(),
                                                           findParent.rect.ymax()));
                    break;
                }
                else {
                    // Insert right.
                    if (findParent.rt != null) {
                        findParent = findParent.rt;
                        verticalSplit = false;
                        continue;
                    }
                    findParent.rt = new Node(p, new RectHV(findParent.p.x(),
                                                           findParent.rect.ymin(),
                                                           findParent.rect.xmax(),
                                                           findParent.rect.ymax()));
                    break;
                }
            }
            else {
                if (p.y() <= findParent.p.y()) {
                    // Insert bottom.
                    if (findParent.lb != null) {
                        findParent = findParent.lb;
                        verticalSplit = true;
                        continue;
                    }
                    findParent.lb = new Node(p, new RectHV(findParent.rect.xmin(),
                                                           findParent.rect.ymin(),
                                                           findParent.rect.xmax(),
                                                           findParent.p.y()));
                    break;
                }
                else {
                    // Insert top.
                    if (findParent.rt != null) {
                        findParent = findParent.rt;
                        verticalSplit = true;
                        continue;
                    }
                    findParent.rt = new Node(p, new RectHV(findParent.rect.xmin(),
                                                           findParent.p.y(),
                                                           findParent.rect.xmax(),
                                                           findParent.rect.ymax()));
                    break;
                }
            }
        }
        size++;
    }

    /** does the set contain point p? */
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (root == null) {
            return false;
        }
        Node findParent = root;
        boolean verticalSplit = true;
        while (true) {
            if (findParent.p.equals(p)) {
                return true;
            }

            if (verticalSplit) {
                if (p.x() <= findParent.p.x()) {
                    if (findParent.lb == null) {
                        return false;
                    }
                    findParent = findParent.lb;

                }
                else {
                    if (findParent.rt == null) {
                        return false;
                    }
                    findParent = findParent.rt;
                }
                verticalSplit = false;
            }
            else {
                if (p.y() <= findParent.p.y()) {
                    if (findParent.lb == null) {
                        return false;
                    }
                    findParent = findParent.lb;

                }
                else {
                    if (findParent.rt == null) {
                        return false;
                    }
                    findParent = findParent.rt;
                }
                verticalSplit = true;
            }
        }
    }

    /** draw all points to standard draw */
    public void draw() {
        if (root != null) {
            drawNode(root, true);
        }

        // for (Point2D p : tree.keys()) {
        //     p.draw();
        // }
    }

    private void drawNode(Node node, boolean vertical) {
        if (vertical) {
            StdDraw.setPenColor(Color.RED);
            StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
        }
        else {
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
        }
        StdDraw.setPenColor(Color.BLACK);
        double radius = StdDraw.getPenRadius();
        StdDraw.setPenRadius(radius * 5);
        node.p.draw();
        StdDraw.setPenRadius(radius);

        if (node.lb != null) {
            drawNode(node.lb, !vertical);
        }

        if (node.rt != null) {
            drawNode(node.rt, !vertical);
        }
    }

    /** all points that are inside the rectangle (or on the boundary) */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        Queue<Point2D> inRange = new Queue<>();
        if (root == null) {
            return inRange;
        }

        // Point2D downLeft = new Point2D(rect.xmin(), rect.ymin());
        // Point2D topRight = new Point2D(rect.xmax(), rect.ymax());

        // int startIndex = tree.rank(downLeft);
        // int endIndex = tree.rank(topRight);

        addInrange(inRange, root, rect);
        return inRange;
    }

    private void addInrange(Queue<Point2D> inRange, Node node, RectHV rect) {
        if (rect.contains(node.p)) {
            inRange.enqueue(node.p);
        }
        if (node.lb != null && node.lb.rect.intersects(rect)) {
            addInrange(inRange, node.lb, rect);
        }

        if (node.rt != null && node.rt.rect.intersects(rect)) {
            addInrange(inRange, node.rt, rect);
        }
    }

    /** a nearest neighbor in the set to point p; null if the set is empty */
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (isEmpty()) {
            return null;
        }
        return nearestDist(root, true, p, Double.POSITIVE_INFINITY, root.p);

        // Node findParent = root;
        // boolean verticalSplit = true;
        // while (true) {
        //     if (findParent.p.equals(p)) {
        //         return p;
        //     }
        //
        //     if (verticalSplit) {
        //         if (p.x() <= findParent.p.x()) {
        //             if (findParent.lb == null) {
        //                 break;
        //             }
        //             findParent = findParent.lb;
        //
        //         } else {
        //             if (findParent.rt == null) {
        //                 break;
        //             }
        //             findParent = findParent.rt;
        //         }
        //         verticalSplit = false;
        //     } else {
        //         if (p.y() <= findParent.p.y()) {
        //             if (findParent.lb == null) {
        //                 break;
        //             }
        //             findParent = findParent.lb;
        //
        //         } else {
        //             if (findParent.rt == null) {
        //                 break;
        //             }
        //             findParent = findParent.rt;
        //         }
        //         verticalSplit = true;
        //     }
        // }
        //
        // Point2D nearest = findParent.p;
        // double nearestDistSquare = p.distanceSquaredTo(nearest);
        // double nearestDist = p.distanceTo(nearest);
        // RectHV searchRect = new RectHV(p.x() - nearestDist, p.y() - nearestDist,
        //                                p.x() + nearestDist, p.y() + nearestDist);
        //
        // for (Point2D point2D : range(searchRect)) {
        //     double squaredTo = p.distanceSquaredTo(point2D);
        //     if (squaredTo < nearestDistSquare) {
        //         nearestDistSquare = squaredTo;
        //         nearest = point2D;
        //     }
        // }
        //
        // return nearest;
    }

    private Point2D nearestDist(Node cur, boolean isVertial, Point2D p, double nearestDistSqrt,
                                Point2D nearestP) {
        if (cur == null) {
            return nearestP;
        }
        double curDistSqrt = cur.p.distanceSquaredTo(p);
        if (curDistSqrt < nearestDistSqrt) {
            nearestDistSqrt = curDistSqrt;
            nearestP = cur.p;
        }

        if ((isVertial && p.x() <= cur.p.x())
                || (!isVertial && p.y() <= cur.p.y())) {
            nearestP = nearestDist(cur.lb, !isVertial, p, nearestDistSqrt, nearestP);
            nearestDistSqrt = p.distanceSquaredTo(nearestP);

            if (cur.rt != null && cur.rt.rect.distanceSquaredTo(p) < nearestDistSqrt) {
                return nearestDist(cur.rt, !isVertial, p, nearestDistSqrt, nearestP);
                // nearestDistSqrt = p.distanceSquaredTo(nearestP);
            }

        }
        else {
            nearestP = nearestDist(cur.rt, !isVertial, p, nearestDistSqrt, nearestP);
            nearestDistSqrt = p.distanceSquaredTo(nearestP);

            if (cur.lb != null && cur.lb.rect.distanceSquaredTo(p) < nearestDistSqrt) {
                return nearestDist(cur.lb, !isVertial, p, nearestDistSqrt, nearestP);
            }

        }
        return nearestP;

    }


    /** unit testing of the methods (optional) */
    public static void main(String[] args) {
    }

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }


}
