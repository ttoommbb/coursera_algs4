import edu.princeton.cs.algs4.Stack;

import java.util.Arrays;

public class Board {

    private final long[] blockBits;

    private final int dimension;
    private final int size;

    private final int hamming;
    private final int manhattan;

    private final int posOf0;
    private final int perNumBitCount;
    private final int perLongNums;

    /**
     * construct a board from an n-by-n array of blocks (where blocks[i][j] = block in row i, column
     * j)
     */
    public Board(int[][] blocks) {
        if (blocks == null) {
            throw new IllegalArgumentException();
        }
        dimension = blocks.length;
        size = dimension * dimension;
        // this.blocks = cloneBlocks(blocks);
        perNumBitCount = (int) Math.ceil(Math.log(size) / Math.log(2));
        perLongNums = Long.SIZE / perNumBitCount;
        int longCount = (int) Math.ceil(size / (double) perLongNums);
        this.blockBits = new long[longCount];

        int longIndex = 0;
        int offsetInLong = 0;
        for (int[] row : blocks) {
            for (int num : row) {
                blockBits[longIndex] <<= perNumBitCount;
                blockBits[longIndex] |= num;
                offsetInLong++;
                if (offsetInLong == perLongNums) {
                    longIndex++;
                    offsetInLong = 0;
                }
            }
        }
        if (offsetInLong != 0 && offsetInLong < perLongNums) {
            blockBits[longIndex] <<= perNumBitCount * (perLongNums - offsetInLong);
        }
        // this.blockBits = convertToBits(blocks);


        int[] hammingManhattan = new int[2];
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                int[] cellHM = hammingManhattan(blocks[row][col], row, col, dimension);
                hammingManhattan[0] += cellHM[0];
                hammingManhattan[1] += cellHM[1];
            }
        }
        this.hamming = hammingManhattan[0];
        this.manhattan = hammingManhattan[1];


        int findRow0;
        int findCol0 = 0;
        col0:
        for (findRow0 = 0; findRow0 < dimension; findRow0++) {
            for (findCol0 = 0; findCol0 < dimension; findCol0++) {
                if (blocks[findRow0][findCol0] == 0) {
                    break col0;
                }
            }
        }
        posOf0 = findRow0 * dimension + findCol0;
        // rowOf0 = findRow0;
        // colOf0 = findCol0;
    }

    // public long getBlockBits() {
    //     return blockBits;
    // }

    private Board(Board src, int pos1, int pos2) {
        // this.blocks = cloneBlocks(src.blocks);
        this.dimension = src.dimension;
        this.size = src.size;
        perNumBitCount = src.perNumBitCount;
        perLongNums = src.perLongNums;

        // long calBits = src.blockBits;
        long[] calBits = src.blockBits.clone();
        long valuePos1 = valueOfPos(calBits, pos1);
        long valuePos2 = valueOfPos(calBits, pos2);
        setValueAt(calBits, pos1, valuePos2);
        setValueAt(calBits, pos2, valuePos1);
        // long maskPos1 = 0x1111 << (pos1 << 2);
        // long maskPos2 = 0x1111 << (pos2 << 2);
        this.blockBits = calBits;

        if (src.posOf0 == pos1) {
            this.posOf0 = pos2;
        }
        else if (src.posOf0 == pos2) {
            this.posOf0 = pos1;
        }
        else {
            this.posOf0 = src.posOf0;
        }

        int[] diff1 = hammingManhattanDiff(valuePos1, pos1, pos2);
        int[] diff2 = hammingManhattanDiff(valuePos2, pos2, pos1);


        // int[] hammingManhattanDiff = swapCells(src.blockBits, pos1, pos2);


        this.hamming = src.hamming + diff1[0] + diff2[0];
        this.manhattan = src.manhattan + diff1[1] + diff2[1];
        // hamming man diff.

    }

    // private long convertToBits(int[][] blocks) {
    //     // 4 bits a num, 16 nums at most.
    //     long bits = 0;
    //     for (int[] row : blocks) {
    //         for (int num : row) {
    //             bits <<= 4;
    //             bits |= num;
    //         }
    //     }
    //     return bits;
    // }

    private int[] hammingManhattanDiff(long value, int posBefore, int posAfter) {
        if (value == 0) {
            return new int[] { 0, 0 };
        }
        int hammingDiff;
        int manhattanDiff;
        if (value == posAfter + 1) {
            hammingDiff = -1;
        }
        else if (value == posBefore + 1) {
            hammingDiff = 1;
        }
        else {
            hammingDiff = 0;
        }
        long goalRow = (value - 1) / dimension;
        long goalCol = (value - 1) % dimension;
        manhattanDiff = (int) (Math.abs(posAfter / dimension - goalRow)
                + Math.abs(posAfter % dimension - goalCol)
                - Math.abs(posBefore / dimension - goalRow)
                - Math.abs(posBefore % dimension - goalCol));

        return new int[] { hammingDiff, manhattanDiff };

    }

    private void setValueAt(long[] bitsArray, int pos, long value) {
        // int shiftSize = (size - pos - 1) << 2;
        // long shiftedValue = value << shiftSize;
        // long shiftedMask = 0b1111L << shiftSize;
        //
        // return bits & ~shiftedMask | shiftedValue;

        int longInArrayOffset = pos / perLongNums;
        int partialOffst = pos % perLongNums;
        int shiftSize = (perLongNums - partialOffst - 1) * perNumBitCount;
        long shiftedValue = value << shiftSize;
        long shiftedMask = ((1L << perNumBitCount) - 1) << shiftSize;
         bitsArray[longInArrayOffset] = bitsArray[longInArrayOffset] & ~shiftedMask | shiftedValue;
    }

    private long valueOfPos(long[] bitsArray, int pos) {
        // return (bits >> ((size - pos - 1) << 2)) & 0b1111L; ////;
        long bits = bitsArray[pos / perLongNums];
        int partialOffst = pos % perLongNums;
        return bits >> ((perLongNums - partialOffst - 1) * perNumBitCount)
                & ((1 << perNumBitCount) - 1);
    }

    /** board dimension n */
    public int dimension() {
        return dimension;
    }

    /** number of blocks out of place */
    public int hamming() {
        return hamming;
    }

    /** sum of Manhattan distances between blocks and goal */
    public int manhattan() {

        return manhattan;
    }

    /** is this board the goal board? */
    public boolean isGoal() {
        return hamming == 0;
    }

    /** a board that is obtained by exchanging any pair of blocks */
    public Board twin() {
        // int[][] twinBlocks = cloneBlocks(blocks);
        if (valueOfPos(blockBits, 0) != 0
                && valueOfPos(blockBits, 1) != 0) {
            return new Board(this, 0, 1);
        }
        else {
            return new Board(this, 2, 3);
        }
        // return new Board(twinBlocks);
    }

    /** does this board equal y? */
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }
        if (this.getClass() != y.getClass()) {
            return false;
        }

        Board b2 = (Board) y;
        if (manhattan != ((Board) y).manhattan) {
            return false;
        }
        return Arrays.equals(blockBits, b2.blockBits);

        // return Arrays.deepEquals(blocks, b2.blocks);
    }

    /** all neighboring boards */
    public Iterable<Board> neighbors() {
        // List<Board> neibhors = new ArrayList<>();
        Stack<Board> neibhors = new Stack<Board>();

        if (posOf0 - dimension >= 0) {
            neibhors.push(new Board(this, posOf0, posOf0 - dimension));
        }
        if (posOf0 + dimension < size) {
            neibhors.push(new Board(this, posOf0, posOf0 + dimension));
        }

        if (posOf0 % dimension != 0) {
            neibhors.push(new Board(this, posOf0, posOf0 - 1));

        }
        if (posOf0 % dimension != dimension - 1) {
            neibhors.push(new Board(this, posOf0, posOf0 + 1));
        }
        return neibhors;
    }

    /** string representation of this board (in the output format specified below) */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(dimension);
        sb.append("\n");
        for (int i = 0; i < size; i++) {
            sb.append(String.format("%2d ", valueOfPos(blockBits, i)));
            if (i % dimension == dimension - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * hamming for a single cell
     *
     * @param value
     * @param row
     * @param col
     * @return 0 or 1
     */
    private static int[] hammingManhattan(int value, int row, int col, int dimension) {
        if (value == 0) {
            return new int[] { 0, 0 };
        }
        int goalRow = (value - 1) / dimension;
        int goalCol = (value - 1) % dimension;
        int manhattan = Math.abs(goalCol - col) + Math.abs(goalRow - row);
        int hamming = manhattan == 0 ? 0 : 1;
        return new int[] { hamming, manhattan };
    }

    /** unit tests (not graded) */
    public static void main(String[] args) {
        int[][] blocks = new int[][] { { 4, 1, 3 }, { 0, 2, 5 }, { 7, 8, 6 } };
        Board board = new Board(blocks);
        Board board2 = new Board(blocks);
        System.out.println("board.blockBits = " + board.blockBits);
        System.out.println("board = " + board);
        System.out.println("board.hamming = " + board.hamming);
        System.out.println("board.manhattan = " + board.manhattan);
        System.out.println("eq = " + board.equals(board2));
        Board twin = board.twin();
        System.out.println("twin = " + twin);
        for (Board neighbor : board.neighbors()) {
            System.out.println("neighbor = " + neighbor);
        }
    }
}