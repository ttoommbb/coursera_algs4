/* *****************************************************************************
 *  Name: GaoZone
 *  Date: 2019-03-25
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final int n;
    private final int totalCellCount;
    private final WeightedQuickUnionUF uf;
    private final WeightedQuickUnionUF ufFull;

    private final int entryIndex;
    private final int exitIndex;
    // private boolean[][] matrix;
    private int numberOfOpenSites = 0;
    private long[] openBits;

    /**
     * create n-by-n grid, with all sites initially blocked
     */
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("grid n-by-n, n must be positive");
        }
        this.n = n;

        totalCellCount = n * n;
        entryIndex = totalCellCount;
        exitIndex = entryIndex + 1;
        uf = new WeightedQuickUnionUF(exitIndex + 1);
        ufFull = new WeightedQuickUnionUF(exitIndex);
        // for (int i = 0; i < n; i++) {
        //     uf.union(i, entryIndex);
        // uf.union(entryIndex - i - 1, exitIndex);
        // }
        openBits = new long[(int) Math.ceil(totalCellCount / (double) Long.SIZE)];
        // matrix = new boolean[n][n];
    }

    /**
     * open the site (row, col) if it is not open already
     */
    public void open(int row, int col) {
        row = row - 1;
        col = col - 1;
        checkInput(row, col);

        int current1D = xyTo1D(row, col);
        if (tryOpen(current1D)) {
            numberOfOpenSites++;
        }

        if (row > 0) {
            int leftSibling1D = xyTo1D(row - 1, col);
            if (isOpen(leftSibling1D)) {
                uf.union(leftSibling1D, current1D);
                ufFull.union(leftSibling1D, current1D);
            }
        }
        else {
            // first row, union to virtual entry site.
            uf.union(entryIndex, current1D);
            ufFull.union(entryIndex, current1D);
        }

        if (row < n - 1) {
            int rightSibling1D = xyTo1D(row + 1, col);
            if (isOpen(rightSibling1D)) {
                uf.union(rightSibling1D, current1D);
                ufFull.union(rightSibling1D, current1D);
            }
        }else {
            //last row
            uf.union(exitIndex, current1D);
        }

        if (col > 0) {
            int topSibling1D = xyTo1D(row, col - 1);
            if (isOpen(topSibling1D)) {
                uf.union(topSibling1D, current1D);
                ufFull.union(topSibling1D, current1D);
            }
        }

        if (col < n - 1) {
            int bottomSibling1D = xyTo1D(row, col + 1);
            if (isOpen(bottomSibling1D)) {
                uf.union(bottomSibling1D, current1D);
                ufFull.union(bottomSibling1D, current1D);
            }
        }
    }

    /**
     * Mark open bits, but don't handle connections.
     *
     * @return true if already open, no changes.
     */
    private boolean tryOpen(int pos1D) {
        int arrayOffset = pos1D / Long.SIZE;
        int bitOffset = pos1D % Long.SIZE;
        long offsetMask = 1L << bitOffset;
        boolean opened = (openBits[arrayOffset] & offsetMask) != 0L;
        if (opened) {
            return false;
        }
        openBits[arrayOffset] |= offsetMask;
        return true;
    }

    private boolean isOpen(int pos1D) {
        int arrayOffset = pos1D / Long.SIZE;
        int bitOffset = pos1D % Long.SIZE;
        long offsetMask = 1L << bitOffset;
        return (openBits[arrayOffset] & offsetMask) != 0L;
    }

    /** is the site (row, col) open? */
    public boolean isOpen(int row, int col) {
        row = row - 1;
        col = col - 1;
        checkInput(row, col);
        return isOpen(xyTo1D(row, col));
    }

    /**
     * is the site (row, col) full?
     */
    public boolean isFull(int row, int col) {
        row = row - 1;
        col = col - 1;
        checkInput(row, col);
        int current1D = xyTo1D(row, col);
        return isOpen(current1D) && ufFull.connected(current1D, entryIndex);
    }

    private void checkInput(int row, int col) {
        if (row < 0 || row >= n) {
            throw new IllegalArgumentException("row must be positive and less than n");
        }
        if (col < 0 || col >= n) {
            throw new IllegalArgumentException("row must be positive and less than n");
        }
    }

    /**
     * number of open sites
     */
    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    /**
     * does the system percolate?
     */
    public boolean percolates() {
        if (numberOfOpenSites < n) {
            return false;
        }
        return uf.connected(entryIndex, exitIndex);
    }

    private int xyTo1D(int row, int col) {
        return row * n + col;
    }
}
