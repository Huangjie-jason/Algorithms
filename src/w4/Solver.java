package w4;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private final Queue<Board> queue;
    private final Boolean isSolvable;
    private int n;

    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("Constructor argument is null");
        Node current = new Node(initial, null);
        Node twinCurrent = new Node(initial.twin(), null);
        queue = new Queue<>();
        MinPQ<Node> pq = new MinPQ<>();
        MinPQ<Node> twinPQ = new MinPQ<>();
        pq.insert(current);
        twinPQ.insert(twinCurrent);
        queue.enqueue(current.board);
        while (true) {
            current = pq.delMin();
            queue.enqueue(current.board);
            if (current.board.isGoal()) {
                isSolvable = true;
                n = current.moves;
                break;
            }
            for (Board b : current.board.neighbors()) {
                if (current.pre != null && b.equals(current.pre.board)) continue;
                pq.insert(new Node(b, current));
            }
            twinCurrent = twinPQ.delMin();
            if (twinCurrent.board.isGoal()) {
                isSolvable = false;
                break;
            }
            for (Board b :
                    twinCurrent.board.neighbors()) {
                if (twinCurrent.pre != null && b.equals(twinCurrent.pre.board)) continue;
                twinPQ.insert(new Node(b, twinCurrent));
            }
        }
    }

    public int moves() {
        return isSolvable ? n : -1;
    }

    public boolean isSolvable() {
        return isSolvable;
    }

    public Iterable<Board> solution() {
        return isSolvable ? queue : null;
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
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

    private class Node implements Comparable<Node> {
        public Board board;
        public int moves;
        public Node pre;
        public final int priority;

        public Node(Board board) {
            this(board, null);
        }

        public Node(Board board, Node pre) {
            this.board = board;
            this.pre = pre;
            if (pre == null) moves = 0;
            else moves = pre.moves + 1;
            priority = moves + board.manhattan();
        }

        @Override
        public int compareTo(Node that) {
            return Integer.compare(this.priority, that.priority);
        }
    }
}
