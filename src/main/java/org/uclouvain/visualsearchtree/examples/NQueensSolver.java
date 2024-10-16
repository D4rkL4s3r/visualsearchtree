package org.uclouvain.visualsearchtree.examples;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class solves the N-Queens problem using different algorithms:
 * Depth-First Search (DFS), Breadth-First Search (BFS), etc.
 */
public class NQueensSolver {

    int[] queens;
    int boardSize = 0;
    static int numVisualizations = 0;
    static int numRecursions = 0;

    public NQueensSolver(int n) {
        this.boardSize = n;
        numVisualizations = n;
        queens = new int[n];
    }

    private int nodeId = 0;

    /**
     * Solves the N-Queens problem using the specified algorithm.
     *
     * @param algorithm The algorithm to use for solving (e.g., "dfs", "bfs").
     * @param listener  A listener to handle branches, failures, and solutions.
     */
    public void solve(String algorithm, SolverListener listener) {
        switch (algorithm.toLowerCase()) {
            case "dfs":
                solveDFS(listener);
                break;
            case "bfs":
                solveBFS(listener);
                break;
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }

    /**
     * Private method that implements Depth-First Search (DFS) algorithm.
     */
    private void solveDFS(SolverListener listener) {
        nodeId = -1;
        try {
            dfs(0, listener, -1);
        } catch (RuntimeException exception) {
            // Catch solution found exception
        }
    }

    private void dfs(int rowIndex, SolverListener listener, int parentId) {
        numRecursions++;
        int id = ++nodeId;
        if (rowIndex == boardSize) {
            listener.solution(id, parentId);
        } else {
            listener.branch(id, parentId, boardSize);
            for (int i = 0; i < boardSize; i++) {
                queens[rowIndex] = i;
                if (areConstraintsSatisfied(rowIndex)) {
                    dfs(rowIndex + 1, listener, id);
                } else {
                    nodeId++;
                    listener.fail(nodeId, parentId);
                }
            }
        }
    }

    /**
     * Private method that implements Breadth-First Search (BFS) algorithm.
     */
    private void solveBFS(SolverListener listener) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(new int[0], -1)); // Ajouter le premier nœud avec un tableau vide de reines

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll(); // Récupère le premier nœud de la file d'attente
            int[] currentQueens = currentNode.queens; // Reines placées jusqu'à présent
            int parentId = currentNode.parentId;
            int id = ++nodeId; // Incrémentez nodeId pour chaque nœud exploré

            if (currentQueens.length == boardSize) {
                listener.solution(id, parentId); // Si on a placé toutes les reines, c'est une solution
                continue;
            }

            listener.branch(id, parentId, boardSize); // Crée une branche à partir du nœud actuel

            // Essayer toutes les colonnes pour la prochaine reine
            for (int i = 0; i < boardSize; i++) {
                int[] newQueens = new int[currentQueens.length + 1];
                System.arraycopy(currentQueens, 0, newQueens, 0, currentQueens.length);
                newQueens[currentQueens.length] = i; // Placer la prochaine reine

                // Vérifie si la nouvelle disposition respecte les contraintes
                if (areConstraintsSatisfied(newQueens)) {
                    queue.add(new Node(newQueens, id)); // Ajoute le nœud valide à la queue
                } else {
                    int failId = ++nodeId; // Incrémentez nodeId seulement pour un échec
                    listener.fail(failId, id); // Signale un échec
                }
            }
        }
    }

    /**
     * Checks if placing a queen satisfies the constraints.
     */
    public boolean areConstraintsSatisfied(int rowIndex) {
        for (int i = 0; i < rowIndex; i++) {
            // Check if queens are on the same row
            if (queens[i] == queens[rowIndex]) return false;
            // Check if queens are on the same diagonal
            if (Math.abs(queens[rowIndex] - queens[i]) == rowIndex - i) {
                return false;
            }
        }
        return true;
    }

    /**
     * Overloaded method to check constraints for an array of queens (used in BFS).
     */
    public boolean areConstraintsSatisfied(int[] queens) {
        int rowIndex = queens.length - 1;
        for (int i = 0; i < rowIndex; i++) {
            if (queens[i] == queens[rowIndex]) return false;
            if (Math.abs(queens[rowIndex] - queens[i]) == rowIndex - i) {
                return false;
            }
        }
        return true;
    }

    /**
     * Solves the N-Queens problem and compares DFS vs BFS performance.
     */
    public static void main(String[] args) {
        NQueensSolver solver = new NQueensSolver(5);

        // Example DFS usage
        long t0 = System.currentTimeMillis();
        solver.solve("dfs", new SolverListener() {
            @Override
            public void solution(int nodeId, int parentId) {
                // Logic to handle a solution
                System.out.println("DFS Solution found at node " + nodeId);
            }

            @Override
            public void branch(int nodeId, int parentId, int numBranches) {
                // Visualization or logging logic for branching
            }

            @Override
            public void fail(int nodeId, int parentId) {
                // Visualization or logging logic for failures
            }
        });
        long t1 = System.currentTimeMillis();
        System.out.println("DFS Time(ms): " + (t1 - t0));

        // Example BFS usage
        long t2 = System.currentTimeMillis();
        solver.solve("bfs", new SolverListener() {
            @Override
            public void solution(int nodeId, int parentId) {
                // Logic to handle a solution
                System.out.println("BFS Solution found at node " + nodeId);
            }

            @Override
            public void branch(int nodeId, int parentId, int numBranches) {
                // Visualization or logging logic for branching
            }

            @Override
            public void fail(int nodeId, int parentId) {
                // Visualization or logging logic for failures
            }
        });
        long t3 = System.currentTimeMillis();
        System.out.println("BFS Time(ms): " + (t3 - t2));
    }

    /**
     * Helper class to store nodes for the BFS search.
     */
    private class Node {
        int[] queens;
        int parentId;

        public Node(int[] queens, int parentId) {
            this.queens = queens;
            this.parentId = parentId;
        }
    }
}
