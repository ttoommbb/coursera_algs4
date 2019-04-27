import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.PatriciaSET;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoggleSolver {
    private static final int R = 26;
    private static final int NEIGHBOR_NOT_EXIST = -1;
    private static final int NEIGHBOR_RIGHT = 0;
    private static final int NEIGHBOR_RIGHT_DOWN = 1;
    private static final int NEIGHBOR_DOWN = 2;
    private static final int NEIGHBOR_LEFT_DOWN = 3;
    private static final int NEIGHBOR_LEFT = 4;
    private static final int NEIGHBOR_TOP_LEFT = 5;
    private static final int NEIGHBOR_TOP = 6;
    private static final int NEIGHBOR_TOP_RIGHT = 7;
    private static final int NEIGHBOR_MAX_COUNT = 8;

    private final DictNode root = new DictNode("高", 0);

    private static class BoardNode {

        // private final char letter;
        private final int col;
        private final int row;
        private int visitOrder;
        private DictNode dict;
        private BoardNode[] neighbors = new BoardNode[NEIGHBOR_MAX_COUNT];


        public BoardNode(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
    private static class BoardExt {
        private boolean boardHasNoWord;
        private BoggleBoard board;
        private BoardNode head;

        private final DictNode root;

        private BoardNode[][] nodes;

        BoardExt(BoggleBoard board, DictNode root) {
            this.board = board;
            this.root = root;

            nodes = new BoardNode[board.rows()][board.cols()];
            for (int row = 0; row < board.rows(); row++) {
                for (int col = 0; col < board.cols(); col++) {
                    nodes[row][col] = new BoardNode(row, col);
                }
            }
            initNeighbors();
            if (!initNextHead(0, 0)) {
                boardHasNoWord = true;
            }
        }

        private boolean initHead(int row, int col) {
            if (row >= nodes.length) {
                return false;
            }
            if (col >= nodes[row].length) {
                return false;
            }
            
            
            this.head = nodes[row][col];
            char letter = board.getLetter(row, col);
            this.head.dict = root.nextNode(letter);
            if (letter == 'Q' && this.head.dict != null) {
                this.head.dict = this.head.dict.nextNode('U');
            }
            if (this.head.dict == null) {
                return false;
            }
            this.head.visitOrder = 1;
            return true;
        }

        private void initNeighbors() {
            int rows = nodes.length;
            int cols = nodes[0].length;
            for (int row = rows - 1; row >= 0; row--) {
                for (int col = cols - 2; col >= 0; col--) {
                    nodes[row][col].neighbors[NEIGHBOR_RIGHT] = nodes[row][col + 1];
                }
            }
            for (int row = rows - 2; row >= 0; row--) {
                for (int col = cols - 2; col >= 0; col--) {
                    nodes[row][col].neighbors[NEIGHBOR_RIGHT_DOWN] = nodes[row + 1][col + 1];
                }
            }
            for (int row = rows - 2; row >= 0; row--) {
                for (int col = cols - 1; col >= 0; col--) {
                    nodes[row][col].neighbors[NEIGHBOR_DOWN] = nodes[row + 1][col];
                }
            }
            for (int row = rows - 2; row >= 0; row--) {
                for (int col = cols - 1; col >= 1; col--) {
                    nodes[row][col].neighbors[NEIGHBOR_LEFT_DOWN] = nodes[row + 1][col - 1];
                }
            }
            for (int row = rows - 1; row >= 0; row--) {
                for (int col = cols - 1; col >= 1; col--) {
                    nodes[row][col].neighbors[NEIGHBOR_LEFT] = nodes[row][col - 1];
                }
            }
            for (int row = rows - 1; row >= 1; row--) {
                for (int col = cols - 1; col >= 1; col--) {
                    nodes[row][col].neighbors[NEIGHBOR_TOP_LEFT] = nodes[row - 1][col - 1];
                }
            }
            for (int row = rows - 1; row >= 1; row--) {
                for (int col = cols - 1; col >= 0; col--) {
                    nodes[row][col].neighbors[NEIGHBOR_TOP] = nodes[row - 1][col];
                }
            }
            for (int row = rows - 1; row >= 1; row--) {
                for (int col = cols - 2; col >= 0; col--) {
                    nodes[row][col].neighbors[NEIGHBOR_TOP_RIGHT] = nodes[row - 1][col + 1];
                }
            }
        }

        public boolean nextStep() {

            int previousDirection = NEIGHBOR_NOT_EXIST;
            while (true) {
                if (!pushNextAvailableHead(previousDirection)) {
                    return false;
                }

                if (head.dict != null) {
                    return true;
                }
                previousDirection = popHead();
            }
        }

        private boolean pushNextAvailableHead(int previousDirection) {
            boolean isPushSuccess = pushHead(previousDirection);
            while (!isPushSuccess) {
                int previousHeadDirection = popHead();
                if (previousHeadDirection == NEIGHBOR_MAX_COUNT) {
                    return false;
                }
                isPushSuccess = pushHead(previousHeadDirection);

            }
            return true;
        }

        private boolean pushHead(int previousDirection) {
            BoardNode nextNode = nextAvailableNeighbor(head, previousDirection);
            if (nextNode != null) {
                nextNode.visitOrder = head.visitOrder + 1;
                char letter = board.getLetter(nextNode.row, nextNode.col);

                nextNode.dict = head.dict.nextNode(letter);
                if (letter == 'Q' && nextNode.dict != null) {
                    nextNode.dict = nextNode.dict.nextNode('U');
                }

                head = nextNode;
                if (head.dict == null) {
                    previousDirection = popHead();
                    return pushHead(previousDirection);
                }
                return true;
            }
            return false;
        }

        /**
         * @return previous head direction, -1 for just moved head. 8 for no next head.
         */
        private int popHead() {
            if (head.visitOrder == 1) {
                // only one head, move head.
                head.visitOrder = 0;
                head.dict = null;
                if (!initNextHead(head.row, head.col + 1)) {
                    return 8;
                }
                return NEIGHBOR_NOT_EXIST;
                // if (!initHead(head.row, head.col + 1)) {
                //     if (!initHead(head.row + 1, 0)) {
                //         return 8;
                //     }
                // }
                // return NEIGHBOR_NOT_EXIST;
            }

            for (int i = 0; i < NEIGHBOR_MAX_COUNT; i++) {
                BoardNode neighbor = head.neighbors[i];
                if (neighbor == null) {
                    continue;
                }
                if (neighbor.visitOrder == head.visitOrder - 1) {
                    // find its previous node,
                    head.visitOrder = 0;
                    head.dict = null;
                    head = neighbor;
                    int previousDirection = (i + 4) & 0B111;
                    return previousDirection;
                }
            }
            throw new RuntimeException("head can't have no neighbor");
        }

        private boolean initNextHead(int row, int col) {
            for (; col < board.cols(); col++) {
                if (initHead(row, col)) {
                    return true;
                }
            }
            row++;
            for (; row < board.rows(); row++) {
                for (col = 0; col < board.cols(); col++) {
                    if (initHead(row, col)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * @param node
         * @param previousDirection -1 if no previous direction.
         * @return
         */
        private BoardNode nextAvailableNeighbor(BoardNode node, int previousDirection) {
            for (int i = previousDirection + 1; i < node.neighbors.length; i++) {
                BoardNode neighbor = node.neighbors[i];
                if (neighbor != null && neighbor.visitOrder == 0) {
                    return neighbor;
                }
            }
            return null;
        }

    }

    /**
     * Initializes the data structure using the given array of strings as the dictionary. (You can
     * assume each word in the dictionary contains only the uppercase letters A through Z.)
     */
    public BoggleSolver(String[] dictionary) {
        for (String word : dictionary) {
            root.add(word);
        }
    }

    /** Returns the set of all valid words in the given Boggle board, as an Iterable. */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        BoardExt boardExt = new BoardExt(board, root);
        if (boardExt.boardHasNoWord) {
            return Collections.emptyList();
        }
        PatriciaSET totalWords = new PatriciaSET();
        while (boardExt.nextStep()) {
            BoardNode head = boardExt.head;
            
            if (head.visitOrder >= 2 && head.dict.isWord()) {
                String word = head.dict.getWord();
                totalWords.add(word);
            }
        }
        return totalWords;
    }

    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise. (You can
     * assume the word contains only the uppercase letters A through Z.)
     */
    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        if (word.length() < 3) {
            return 0;
        }
        boolean contains = root.contains(word);
        if (!contains) {
            return 0;
        }
        switch (word.length()) {
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 11;
        }
    }

    public static void main(String[] args) {
        normalTest();
        // timingTest();
    }

    private static void timingTest() {
        String dictFile = "dictionary-algs4.txt";
        BoggleSolver solver = new BoggleSolver(dictFromFile(dictFile));

        long start = System.currentTimeMillis();
        long expectEnd = start + 5000;
        int count = 0;
        while (System.currentTimeMillis() < expectEnd) {
            BoggleBoard board = new BoggleBoard();
            
            solver.getAllValidWords(board);
            count++;
        }
        System.out.println("count = " + count);

    }

    private static void normalTest() {
        String input = "dictionary = dictionary-zingarelli2005.txt; board = board4x4.txt";
        String[] splits = input.split("[ ;]");
        String dictFile = splits[2];
        String boardFile = splits[6];
        System.out.println("dictFile = " + dictFile);
        System.out.println("boardFile = " + boardFile);
        
        BoggleBoard board = new BoggleBoard(boardFile);
        System.out.println("board = " + board);
        BoggleSolver solver = new BoggleSolver(dictFromFile(dictFile));

        // printAllNode(solver.root);
        System.out.println("solver.paid = " + solver.scoreOf("QUITE"));
        int i = 0;
        for (String word : solver.getAllValidWords(board)) {
            System.out.println(i++ + ") " + "word = " + word + ", score = " + solver.scoreOf(word));
        }
    }

    private static String[] dictFromFile(String dictFile) {
        List<String> dict = new ArrayList<>();
        In in = new In(dictFile);
        String line;
        while ((line = in.readLine()) != null) {
            dict.add(line);
        }
        return dict.toArray(new String[0]);
    }
}
