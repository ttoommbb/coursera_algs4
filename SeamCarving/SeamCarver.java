import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;
import java.util.function.Function;

public class SeamCarver {
    private Picture picture;

    /** create a seam carver object based on the given picture */
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("pic can't be null");
        }
        this.picture = new Picture(picture);
    }

    /** current picture */
    public Picture picture() {
        return new Picture(picture);
    }

    /** sequence of indices for horizontal seam */
    public int[] findHorizontalSeam() {
        return findVerticalSeam(true);
    }

    /**
     * row col don't count border
     *
     * @param inversion
     * @return
     */
    private int[] findVerticalSeam(boolean inversion) {
        int height = height(inversion);
        int[] verticalSeams = new int[height];
        int innerWidth = width(inversion) - 2;
        int innerHeight = height - 2;
        if (innerWidth <= 1 || innerHeight < 1) {
            Arrays.fill(verticalSeams, innerWidth <= 0 ? 0 : 1);
            return verticalSeams;
        }

        int offset = 0;
        double[] energy = new double[innerHeight * innerWidth];
        for (int row = 0; row < innerHeight; row++) {
            for (int col = 0; col < innerWidth; col++) {
                energy[offset++] = energy(col + 1, row + 1, inversion);
            }
        }

        /* comput min sum to this node */
        double[] minSum = new double[energy.length - innerWidth]; // No need space for last row
        double[] minSumForNext = new double[innerWidth]; // comput sum available for next layer

        // calcMinSumForNext(minSumForNext, energy, 0);


        offset = 0;
        for (int row = 0; row < innerHeight - 1; row++) {
            // int innerOffset = offset;
            for (int col = 0; col < innerWidth; col++) {
                minSum[offset + col] = energy[offset + col] + minSumForNext[col];
                // innerOffset++
            }
            // offset -= innerWidth;
            calcMinSumForNext(minSumForNext, minSum, offset);
            offset += innerWidth;
        }
        // last row left, find min sum of calcMinSumForNext[x] + energy[x]
        // offset += innerWidth;
        int minCol = getMinCol(minSumForNext, energy, offset);

        verticalSeams[verticalSeams.length - 1] = minCol;
        verticalSeams[verticalSeams.length - 2] = minCol + 1;
        offset = minSum.length - (innerWidth);
        for (int row = innerHeight - 2; row >= 0; row--) {
            double minTotalToRow;
            int newMinCol;
            if (minCol != 0 && minSum[offset + minCol - 1] < minSum[offset + minCol]) {
                minTotalToRow = minSum[offset + minCol - 1];
                newMinCol = minCol - 1;
            }
            else {
                minTotalToRow = minSum[offset + minCol];
                newMinCol = minCol;
            }
            if (minCol < innerWidth - 1 && minSum[offset + minCol + 1] < minTotalToRow) {
                newMinCol = minCol + 1;
            }
            verticalSeams[row + 1] = newMinCol + 1;
            minCol = newMinCol;
            offset -= innerWidth;
        }
        verticalSeams[0] = verticalSeams[1] - 1;

        return verticalSeams;
    }

    private int width(boolean inversion) {
        return inversion ? height() : width();
    }

    private int height(boolean inversion) {
        return inversion ? width() : height();
    }

    private void calcMinSumForNext(double[] minSumForNext, double[] minSum, final int offset) {
        // 3 1 4 1 6 9 2 6 5 3 5
        // 1 1 1 1 6 6 2 2 3 3 3
        //
        for (int i = 0; i < minSumForNext.length - 1; i += 2) {
            minSumForNext[i] = Math.min(minSum[offset + i], minSum[offset + i + 1]);
            minSumForNext[i + 1] = minSumForNext[i];
        }
        if ((minSumForNext.length & 1) == 1) {
            minSumForNext[minSumForNext.length - 1] = minSum[offset + minSumForNext.length - 1];
        }
        for (int i = 1; i < minSumForNext.length - 1; i += 2) {
            minSumForNext[i] = Math.min(minSumForNext[i], minSum[offset + i + 1]);
            minSumForNext[i + 1] = Math.min(minSumForNext[i + 1], minSum[offset + i]);
        }


        // System.out.println("minsumNext = " + Arrays.toString(minSumForNext));
        // System.out.println("offset = " + offset);
        // System.out.println("minSumToRow = " + Arrays
        //         .toString(Arrays.copyOfRange(minSum, offset, offset + minSumForNext.length)));
    }

    private int getMinCol(double[] minSumForNext, double[] energy, int offsetToLastRow) {
        int minCol = -1;
        double minTotal = Integer.MAX_VALUE;
        for (int col = 0; col < minSumForNext.length; col++) {
            double minCurCol = energy[offsetToLastRow + col] + minSumForNext[col];
            if (minCurCol < minTotal) {
                minTotal = minCurCol;
                minCol = col;
            }
        }
        return minCol;
    }

    /** height of current picture */
    public int height() {
        return this.picture.height();
    }

    /** width of current picture */
    public int width() {
        return this.picture.width();
    }

    /** sequence of indices for vertical seam */
    public int[] findVerticalSeam() {
        return findVerticalSeam(false);
    }

    private double energy(int x, int y, boolean inversion) {
        return inversion ? energy(y, x) : energy(x, y);
    }

    /** energy of pixel at column x and row y */
    public double energy(int x, int y) {
        if (isOnBorder(x, width() - 1) | isOnBorder(y, height() - 1)) {
            return 1000;
        }
        Color colorLeft = this.picture.get(x - 1, y);
        Color colorRight = this.picture.get(x + 1, y);

        double energyPowX = energyPow(colorLeft, colorRight);
        double energyPowY = energyPow(this.picture.get(x, y - 1), this.picture.get(x, y + 1));
        return Math.sqrt(energyPowX + energyPowY);
    }

    private boolean isOnBorder(int xOrY, int borderMax) {
        if (xOrY < 0 || xOrY > borderMax) {
            throw new IllegalArgumentException("out of border");
        }
        return xOrY == borderMax || xOrY == 0;
    }

    private static double energyPow(Color c1, Color c2) {

        // double energyPowR = Math.pow(Math.abs(c1.getRed() - c2.getRed()), 2);
        double energyPowR = energyPow(c1, c2, Color::getRed);
        double energyPowG = energyPow(c1, c2, Color::getGreen);
        double energyPowB = energyPow(c1, c2, Color::getBlue);

        return energyPowR + energyPowG + energyPowB;

    }

    private static double energyPow(Color c1, Color c2, Function<Color, Integer> colorFun) {

        return Math.pow(Math.abs(colorFun.apply(c1) - colorFun.apply(c2)), 2);
    }

    /** remove horizontal seam from current picture */
    public void removeHorizontalSeam(int[] seam) {
        removeVerticalSeam(seam, true);
    }

    private void removeVerticalSeam(int[] seam, boolean inversion) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        if (seam.length != (inversion ? width() : height())) {
            throw new IllegalArgumentException();
        }

        Picture seamed = new Picture(inversion ? width() : width() - 1,
                                     inversion ? height() - 1 : height());
        for (int seamOffset = 0; seamOffset < seam.length; seamOffset++) {
            int otherOffsetLimit = inversion ? height() - 1 : width() - 1;
            if (seam[seamOffset] < 0 || seam[seamOffset] > otherOffsetLimit) {
                throw new IllegalArgumentException();
            }
            if (seamOffset > 0 && Math.abs(seam[seamOffset] - seam[seamOffset - 1]) > 1) {
                throw new IllegalArgumentException("gap between seams are bigger than 1");
            }
            for (int otherOffset = 0; otherOffset < seam[seamOffset]; otherOffset++) {
                int col = inversion ? seamOffset : otherOffset;
                int row = inversion ? otherOffset : seamOffset;
                seamed.setRGB(col, row, picture.getRGB(col, row));
            }

            for (int otherOffset = seam[seamOffset]; otherOffset < otherOffsetLimit;
                 otherOffset++) {
                int col = inversion ? seamOffset : otherOffset;
                int row = inversion ? otherOffset : seamOffset;
                seamed.setRGB(col, row, picture.getRGB(inversion ? col : col + 1,
                                                       inversion ? row + 1 : row));
            }

        }
        this.picture = seamed;
    }

    /** remove vertical seam from current picture */
    public void removeVerticalSeam(int[] seam) {
        removeVerticalSeam(seam, false);
    }
}
