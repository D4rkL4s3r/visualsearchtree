package org.uclouvain.visualsearchtree.branchandbound;

import java.util.ArrayList;
import java.util.List;

public class NQueensNode implements Node<NQueensNode> {
    private static int idCounter = 0; // Static counter for generating unique IDs
    private int id; // Unique ID for each node

    private final int[] board; // board[i] = column of the queen in row i
    private final int n; // size of the board (N)
    private final int depth; // current depth (row index)
    private final NQueensNode parent; // Reference to the parent node

    // Constructor for the root node (initial state)
    public NQueensNode(int n) {
        this.id = idCounter++;
        this.n = n;
        this.board = new int[n]; // Initialize board to store queen positions
        this.depth = 0; // Initially, no queens are placed
        this.parent = null; // Root node has no parent
    }

    // Copy constructor for creating child nodes
    private NQueensNode(int[] board, int depth, int n, NQueensNode parent) {
        this.id = idCounter++;
        this.board = board.clone();
        this.depth = depth;
        this.n = n;
        this.parent = parent; // Set the parent node
    }

    @Override
    public boolean isSolutionCandidate() {
        return depth == n; // Solution is found when all queens are placed
    }

    @Override
    public double objectiveFunction() {
        return depth; // Objective is to place all queens (can return depth or some cost)
    }

    @Override
    public double lowerBound() {
        return depth; // Lower bound is the number of queens placed
    }

    @Override
    public int depth() {
        return depth;
    }

    @Override
    public List<Node<NQueensNode>> children() {
        List<Node<NQueensNode>> children = new ArrayList<>();
        // Try to place a queen in the next row (depth)
        for (int col = 0; col < n; col++) {
            if (isValid(depth, col)) {
                // Create a new child node with the queen placed
                int[] newBoard = new int[n];
                System.arraycopy(board, 0, newBoard, 0, depth);
                newBoard[depth] = col; // Place queen in column `col`
                children.add(new NQueensNode(newBoard, depth + 1, n, this)); // Pass `this` as parent
            }
        }
        return children;
    }

    // Check if placing a queen at (row, col) is valid
    private boolean isValid(int row, int col) {
        for (int i = 0; i < row; i++) {
            // Check column and diagonal conflicts
            if (board[i] == col || Math.abs(board[i] - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
    }

    // Method to get the parent node
    @Override
    public NQueensNode getParent() {
        return parent;
    }

    @Override
    public NQueensNode getState() {
        return this; // Returning the current state
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