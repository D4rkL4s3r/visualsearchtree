/*
package org.uclouvain.visualsearchtree.examples;

import java.util.Scanner;
import org.uclouvain.visualsearchtree.branchandbound.BestFirstOpenNodes;
import org.uclouvain.visualsearchtree.branchandbound.BranchAndBound;

*/
/**
 * NQueensSolverVisu class to solve the N-Queens problem visually.
 *//*

public class NQueensSolverVisu {

    private final int n; // Size of the board
    private final NQueensNode rootNode; // Root node for the N-Queens problem
    public int[] queens; // Array to store positions of queens

    public NQueensSolverVisu(int n) {
        this.n = n;
        this.rootNode = new NQueensNode(n); // Initialize the root node
        this.queens = new int[n]; // Initialize the queens array
    }

    public void solve(SolverListener listener) {

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the size of the board (N): ");
        int n = scanner.nextInt(); // Read the size of the board from user input

        NQueensSolverVisu solver = new NQueensSolverVisu(n);

        // Start solving using DFS or BFS (you can choose)
        System.out.println("Starting solving with Branch and Bound...");
        long startTime = System.currentTimeMillis();

        // Use the VisualizationListener to handle output
        solver.solve(new VisualizationListener());

        long endTime = System.currentTimeMillis();
        System.out.println("Execution Time: " + (endTime - startTime) + " ms");

        scanner.close();
    }

    // Inner class for visualization listener
    private static class VisualizationListener implements SolverListener {
        @Override
        public void solution(int nodeId, int pId) {
            System.out.println("Solution found at node " + nodeId);
            // Here you could implement more complex visualization logic
        }

        @Override
        public void fail(int id, int pId) {
            System.out.println("Failed at node " + id + " (parent: " + pId + ")");
            // You could log this to a file or a more detailed log
        }

        @Override
        public void branch(int id, int pId, int nChilds) {
            System.out.println("Branching at node " + id + " from parent " + pId + " with " + nChilds + " children.");
            // You could visualize branching, for example using a tree structure
        }
    }
}
*/
