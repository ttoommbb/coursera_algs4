import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private final GameTreeNode solving;

    /** find a solution to the initial board (using the A* algorithm) */
    public Solver(Board initial) {

        if (initial == null) {
            throw new IllegalArgumentException();
        }
        MinPQ<GameTreeNode> minPQ = new MinPQ<>();

        GameTreeNode root = new GameTreeNode(initial);
        minPQ.insert(root);
        GameTreeNode twinRoot = root.genTwin();
        minPQ.insert(twinRoot);

        // BST<Long, Integer> visited = new BST<>();

        GameTreeNode findSolvingOrTwinSolving = null;
        while (!minPQ.isEmpty()) {
            GameTreeNode node = minPQ.delMin();
            // visited.put(node.board.getBlockBits(), node.moves);

            if (node.board.isGoal()) {
                // we got a solving.
                findSolvingOrTwinSolving = node;
                break;
            }
            // if (node.children == null) {
            //     node.children = new MinPQ<>();
                for (Board neighbor : node.board.neighbors()) {
                    GameTreeNode ancestor = node.parent;
                    // ancestor = ancestor.parent;
                    if (ancestor != null && ancestor.board.equals(neighbor)) {
                        continue;
                    }

                    // Integer existMoves = visited.get(neighbor.getBlockBits());
                    // if (existMoves == null || existMoves > node.moves + 1 ) {
                        GameTreeNode child = new GameTreeNode(neighbor, node);
                        minPQ.insert(child);
                        // node.children.insert(child);
                    // }
                    // GameTreeNode exist = searchNode(node.twin ? twinRoot : root, neighbor, node.moves+ 1);
                    // if (exist == null || exist.moves > node.moves + 1) {

                    // }

                }
            // }
        }

        this.solving = findSolvingOrTwinSolving;
    }

    private static class GameTreeNode implements Comparable<GameTreeNode> {
        final Board board;
        final int moves;
        final boolean twin;
        final GameTreeNode parent;
        private int priority;

        // MinPQ<GameTreeNode> children = null;

        public GameTreeNode(Board initial) {
            this(initial, false, null);
        }

        public GameTreeNode(Board board, GameTreeNode parent) {
            this(board, parent != null && parent.twin, parent);
        }

        public GameTreeNode(Board board, boolean twin, GameTreeNode parent) {
            this.board = board;
            this.moves = parent == null ? 0 : parent.moves + 1;
            this.twin = twin;
            this.parent = parent;

            this.priority = moves + board.manhattan() + (twin ? 5 : 0);
        }

        @Override
        public int compareTo(GameTreeNode o) {
            return priority - o.priority;
        }

        public GameTreeNode genTwin() {
            return new GameTreeNode(board.twin(), !twin, null);
        }
    }

    /** is the initial board solvable? */
    public boolean isSolvable() {
        return solving != null && !solving.twin;
    }

    /** min number of moves to solve initial board; -1 if unsolvable */
    public int moves() {
        return solving.twin ? -1 : solving.moves;
    }

    /** sequence of boards in a shortest solution; null if unsolvable */
    public Iterable<Board> solution() {
        if (solving.twin) {
            return null;
        }
        Stack<Board> steps = new Stack<>();
        GameTreeNode node = solving;
        while (node != null) {
            steps.push(node.board);
            node = node.parent;
        }
        return steps;
    }

    /** solve a slider puzzle (given below) */
    public static void main(String[] args) {

        // create initial board from file

        for (int i = 0; i <= 31; i++) {
            String fileName = String.format("puzzle3x3-%02d.txt", i);
            System.out.println(" ready tet file:" + fileName);
            testFile(fileName);
        }
        testFile("puzzle3x3-unsolvable2.txt");

        // for (int i = 0; i <= 50; i++) {
        //     String fileName = String.format("puzzle4x4-%02d.txt", i);
        //     System.out.println(" ready tet file:" + fileName);
        //     testFile(fileName);
        // }

        // testFile("puzzle4x4-80.txt");
        // testFile("puzzle4x4-unsolvable.txt");
    }


    private static void testFile(String file) {
        In in = new In(file);
        // In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
