import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastCollinearPoints {
    private final LineSegment[] segments;
    // private final Point[] points;

    /** finds all line segments containing 4 or more points */
    public FastCollinearPoints(Point[] points) {
        try {
            // this.points = Arrays.copyOf(points, points.length);
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

        this.segments = calcSegments(points);
    }

    /** the number of line segments */
    public int numberOfSegments() {

        return segments.length;
    }

    /** the line segments */
    public LineSegment[] segments() {

        return segments.clone();
    }

    private LineSegment[] calcSegments(Point[] points) {
        if (points.length < 4) {
            return new LineSegment[0];
        }
        List<LineSegment> segmentList = new ArrayList<>();
        List<DedupSlopt> dedupSlopts = new ArrayList<>();
        // Map<Double, List<Point>> dedupSlops = new HashMap<>();


        for (int i = 0; i < points.length - 3; i++) {
            Arrays.sort(points, i, points.length);
            Point p = points[i];
            Arrays.sort(points, i + 1, points.length, p.slopeOrder());
            double previsouSlope = p.slopeTo(points[i + 1]);
            int sameSlotCount = 1;
            for (int j = i + 2; j < points.length; j++) {
                double slope = p.slopeTo(points[j]);
                if (slope == previsouSlope) {
                    sameSlotCount++;
                }
                else {
                    saveSegment(points, segmentList, dedupSlopts, i, previsouSlope, sameSlotCount,
                                j - 1);
                    previsouSlope = slope;
                    sameSlotCount = 1;
                }
            }
            saveSegment(points, segmentList, dedupSlopts, i, previsouSlope, sameSlotCount,
                        points.length - 1);

        }
        return segmentList.toArray(new LineSegment[0]);
    }

    private void saveSegment(Point[] points, List<LineSegment> segmentList,
                             List<DedupSlopt> dedupSlops, int startIndex,
                             double slope, int sameSlotCount, int endInclusive) {
        if (sameSlotCount >= 3) {
            // [startIndex, j-1] same
            if (dedupVerify(points[endInclusive], dedupSlops, slope)) {
                segmentList.add(new LineSegment(points[startIndex], points[endInclusive]));

                if (sameSlotCount > 3) {
                    constructDedup(points, dedupSlops, endInclusive, slope, sameSlotCount);
                }
            }

        }
    }

    /**
     *  @param points
     * @param dedupSlops
     * @param start offset inclusive
     * @param end   offset inclusive
     * @param slope
     */
    private void constructDedup(Point[] points, List<DedupSlopt> dedupSlops, int end,
                                double slope, int sameSlotCount) {
        DedupSlopt slopt = null;
        for (DedupSlopt dedupSlop : dedupSlops) {
            if (dedupSlop.slopt == slope) {
                slopt = dedupSlop;
                break;
            }
        }
        if (slopt == null) {
            slopt = new DedupSlopt(slope);
            dedupSlops.add(slopt);
        }
        // List<Point> dedupPoints = dedupSlops.(slope);
        // if (dedupPoints == null) {
        //     dedupPoints = new ArrayList<>();
        //     dedupSlops.put(slope, dedupPoints);
        // }
        for (int k = end; k > end - sameSlotCount; k--) {
            slopt.add(points[k]);
        }
    }

    private boolean dedupVerify(Point point, List<DedupSlopt> dedupSlops, double slope) {
        for (DedupSlopt dedupSlop : dedupSlops) {
            if (dedupSlop.slopt == slope) {
                return !dedupSlop.points.contains(point);
                // break;
            }
        }
        return true;
        // return !dedupSlops.containsKey(slope)
        //         || !dedupSlops.get(slope).contains(point);
    }

    private static class DedupSlopt {
        private final double slopt;
        private final List<Point> points;

        DedupSlopt(double slopt) {
            this.slopt = slopt;
            points = new ArrayList<>();
        }

        public void add(Point point) {
            points.add(point);
        }

        // @Override
        // public boolean equals(Object obj) {
        //     if (obj == null) {
        //         return false;
        //     }
        //     if (this.getClass() != obj.getClass()) {
        //         return false;
        //     }
        //     return this.slopt == ((DedupSlopt) obj).slopt;
        // }
        //
        // @Override
        // public int hashCode() {
        //     return Double.hashCode(slopt);
        // }
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
