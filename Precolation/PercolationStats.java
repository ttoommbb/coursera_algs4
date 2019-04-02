/* *****************************************************************************
 *  Name: GaoZone
 *  Date: 2019-03-25
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private final double mean;
    private final double stddev;
    private final double confidenceLo;
    private final double confidenceHi;

    public PercolationStats(int n, int times) {
        if (n <= 0) {
            throw new IllegalArgumentException("The grid size must be positive");
        }
        if (times <= 0) {
            throw new IllegalArgumentException("The number of experiments must be positive");
        }

        double[] percolationThresholds = new double[times];
        for (int i = 0; i < times; i++) {
            Percolation percolation = new Percolation(n);

            int steps = 0;
            while (!percolation.percolates()) {
                int column;
                int row;

                do {
                    column = 1 + StdRandom.uniform(n);
                    row = 1 + StdRandom.uniform(n);
                } while (percolation.isOpen(row, column));

                percolation.open(row, column);
                steps++;
            }

            percolationThresholds[i] = steps / (double) (n * n);
        }

        mean = StdStats.mean(percolationThresholds);
        stddev = StdStats.stddev(percolationThresholds);
        double confidenceFraction = (1.96 * stddev()) / Math.sqrt(times);
        confidenceLo = mean - confidenceFraction;
        confidenceHi = mean + confidenceFraction;
    }

    public double confidenceHi() {
        return confidenceHi;
    }

    public double confidenceLo() {
        return confidenceLo;
    }

    public double mean() {
        return mean;
    }

    public double stddev() {
        return stddev;
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);

        PercolationStats stats = new PercolationStats(n, t);
        System.out.println("mean                    = " + stats.mean());
        System.out.println("stddev                  = " + stats.stddev());
        System.out.println("95% confidence interval = " + stats.confidenceLo() + ", " + stats.confidenceHi());
    }
}
