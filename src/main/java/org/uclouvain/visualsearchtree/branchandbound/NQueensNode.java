package org.uclouvain.visualsearchtree.branchandbound;

import java.util.ArrayList;
import java.util.List;

public class NQueensNode implements Node<NQueensNode> {
    private static int idCounter = 0; // Static counter for generating unique IDs
    private int id; // Unique ID for each node

    private final int[] board; // board[i] = column of the queen in row i
    private final int n; // size of the board (N)
    private final int depth; // current depth (row index)
    private final NQueensNode parent;

    public NQueensNode(int n) {
        this.id = idCounter++;
        this.n = n;
        this.board = new int[n]; // Initialize board to store queen positions
        this.depth = 0; // Initially, no queens are placed
        this.parent = null; // Root node has no parent
    }

    private NQueensNode(int[] board, int depth, int n, NQueensNode parent) {
        this.id = idCounter++;
        this.board = board.clone();
        this.depth = depth;
        this.n = n;
        this.parent = parent; // Set the parent node
    }

    @Override
    public boolean isSolutionCandidate() {
        return depth == n;
    }

    @Override
    public double objectiveFunction() {
        int conflicts = 0;
        for (int i = 0; i < depth; i++) {
            for (int j = i + 1; j < depth; j++) {
                // Vérifie les conflits de colonne et de diagonale
                if (board[i] == board[j] || Math.abs(board[i] - board[j]) == Math.abs(i - j)) {
                    conflicts++;
                }
            }
        }
        return conflicts;
    }

    @Override
    public double lowerBound() {
        return depth;
    }

    @Override
    public int depth() {
        return depth;
    }

    @Override
    public List<Node<NQueensNode>> children() {
        List<Node<NQueensNode>> children = new ArrayList<>();
        for (int col = 0; col < n; col++) {
            if (isValid(depth, col)) {
                int[] newBoard = new int[n];
                System.arraycopy(board, 0, newBoard, 0, depth);
                newBoard[depth] = col;
                children.add(new NQueensNode(newBoard, depth + 1, n, this));
            }
        }
        return children;
    }

    private boolean isValid(int row, int col) {
        for (int i = 0; i < row; i++) {
            if (board[i] == col || Math.abs(board[i] - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public NQueensNode getParent() {
        return parent;
    }

    @Override
    public NQueensNode getState() {
        return this;
    }

    @Override
    public int getId() {
        return id;
    }

    public int[] getBoard() {
        return board.clone();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i] == j) {
                    sb.append("Q "); // Place a queen
                } else {
                    sb.append(". "); // Empty space
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}