import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {
    // private final Point[] points;
    private final LineSegment[] segments;

    /** finds all line segments containing 4 points */
    public BruteCollinearPoints(Point[] points) {
        // if (points == null) {
        //     throw new IllegalArgumentException();
        // }
        //if any point in the array is null, or if the argument to the constructor contains a repeated poin
        try {
            points = points.clone();
            Arrays.sort(points);
            if (points[0] == null) {
                throw new IllegalArgumentException("null entry");
            }
        }
        catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        }

        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].compareTo(points[i + 1]) == 0) {
                throw new IllegalArgumentException("Has repeated point");
            }
        }

        // this.points = points;
        this.segments = calcSegments(points);
    }

    /** the number of line segments */
    public int numberOfSegments() {
        // if (segments == null) {
        //     calcSegments();
        // }
        return segments.length;
    }

    private LineSegment[] calcSegments(Point[] points) {

        if (points.length < 4) {
            return new LineSegment[0];
        }
        List<LineSegment> segmentsList = new ArrayList<>();
        for (int i = 0; i < points.length - 3; i++) {
            Point p = points[i];
            for (int j = i + 1; j < points.length - 2; j++) {
                Point q = points[j];
                double slopPQ = p.slopeTo(q);

                for (int k = j + 1; k < points.length - 1; k++) {
                    Point r = points[k];
                    double slopPR = p.slopeTo(r);
                    if (slopPQ != slopPR) {
                        continue;
                    }
                    for (int l = k + 1; l < points.length; l++) {
                        Point s = points[l];
                        double slopPS = p.slopeTo(s);
                        if (slopPQ == slopPS) {
                            segmentsList.add(new LineSegment(p, s));
                        }

                    }
                }
            }
        }
        return segmentsList.toArray(new LineSegment[0]);

    }

    /** the line segments */
    public LineSegment[] segments() {
        // if (segments == null) {
        //     calcSegments();
        // }
        return segments.clone();
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}