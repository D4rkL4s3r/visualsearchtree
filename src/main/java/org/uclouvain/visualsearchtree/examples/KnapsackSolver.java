package org.uclouvain.visualsearchtree.examples;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class solves the 0/1 Knapsack problem using different algorithms:
 * Depth-First Search (DFS), Breadth-First Search (BFS), etc.
 */
public class KnapsackSolver {

    private int[] weights;
    private int[] values;
    private int capacity;
    private int nodeId = 0;
    private int[] itemsSelected;  // Variable pour stocker les éléments sélectionnés
    private int[] bestSolution;  // Variable pour stocker la solution finale sélectionnée
    private int maxValue;  // Variable pour stocker la valeur maximale

    public KnapsackSolver(int[] weights, int[] values, int capacity) {
        this.weights = weights;
        this.values = values;
        this.capacity = capacity;
        this.itemsSelected = new int[weights.length];  // Initialiser le tableau
        this.bestSolution = null;
        this.maxValue = 0;
    }

    /**
     * Solves the 0/1 Knapsack problem using the specified algorithm.
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

    public int[] getItemsSelected(){
        return itemsSelected;
    }

    public int[] getBestSolution() {
        return bestSolution;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getTotalWeight(int[] itemsSelected) {
        int totalWeight = 0;
        for (int i = 0; i < itemsSelected.length; i++) {
            if (itemsSelected[i] == 1) { // Si l'item est sélectionné
                totalWeight += weights[i]; // Ajoutez son poids
            }
        }
        return totalWeight;
    }

    public int getTotalValue(int[] itemsSelected) {
        int totalValue = 0;
        for (int i = 0; i < itemsSelected.length; i++) {
            if (itemsSelected[i] == 1) { // Si l'item est sélectionné
                totalValue += values[i]; // Ajoutez sa valeur
            }
        }
        return totalValue;
    }

    /**
     * Private method that implements Depth-First Search (DFS) algorithm.
     */
    private void solveDFS(SolverListener listener) {
        nodeId = -1;
        try {
            dfs(0, 0, 0, listener, -1, itemsSelected);  // Start DFS with no items selected, 0 weight, and 0 value.
        } catch (RuntimeException exception) {
            // Catch solution found exception
        }
    }

    private void dfs(int index, int currentWeight, int currentValue, SolverListener listener, int parentId, int[] itemsSelected) {
        int id = ++nodeId;  // Nouveau nœud pour chaque appel DFS

        // Cas de base : si nous avons parcouru tous les items
        if (index == weights.length) {
            if (currentWeight <= capacity) {
                if (currentValue > maxValue) {
                    maxValue = currentValue;
                    bestSolution = itemsSelected.clone();  // Stocker la nouvelle meilleure solution
                }
                listener.solution(id, parentId);  // Notifier qu'une solution est trouvée (note : utilisation de 'id')
            } else {
                listener.fail(id, parentId);  // Notifier qu'un échec est rencontré pour ce nœud
            }
            return;  // Fin du parcours pour cette branche
        }

        // Choix binaire : inclure l'item ou l'exclure
        listener.branch(id, parentId, 2);

        // 1. Exclure l'item courant
        dfs(index + 1, currentWeight, currentValue, listener, id, itemsSelected.clone());

        // 2. Inclure l'item courant, si le poids le permet
        if (currentWeight + weights[index] <= capacity) {
            itemsSelected[index] = 1;  // Marquer l'item comme sélectionné
            dfs(index + 1, currentWeight + weights[index], currentValue + values[index], listener, id, itemsSelected.clone());
        } else {
            // Si inclure l'item dépasse la capacité, notifier l'échec de cette branche spécifique
            listener.fail(++nodeId, id);  // Utilise un nouvel identifiant unique ici
        }
    }

    /**
     * Private method that implements Breadth-First Search (BFS) algorithm.
     */
    private void solveBFS(SolverListener listener) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(0, 0, 0, -1, new int[weights.length]));  // Initialiser avec un tableau vide des items sélectionnés

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            int index = currentNode.index;
            int currentWeight = currentNode.currentWeight;
            int currentValue = currentNode.currentValue;
            int parentId = currentNode.parentId;
            int id = ++nodeId;

            if (index == weights.length) {
                // Si la solution actuelle est meilleure que la meilleure solution connue
                if (currentWeight <= capacity && currentValue > maxValue) {
                    maxValue = currentValue;
                    bestSolution = currentNode.itemsSelected.clone();  // Stocker la nouvelle meilleure solution
                }
                listener.solution(currentValue, parentId);  // Notifier qu'une solution est trouvée
                continue;
            }

            listener.branch(id, parentId, 2);  // Choix binaire : inclure l'item ou l'exclure

            // Exclure l'item courant
            queue.add(new Node(index + 1, currentWeight, currentValue, id, currentNode.itemsSelected.clone()));

            // Inclure l'item courant, s'il rentre dans le sac
            if (currentWeight + weights[index] <= capacity) {
                int[] newItemsSelected = currentNode.itemsSelected.clone();
                newItemsSelected[index] = 1;  // Marquer l'item comme sélectionné
                queue.add(new Node(index + 1, currentWeight + weights[index], currentValue + values[index], id, newItemsSelected));
            } else {
                listener.fail(++nodeId, id);  // Échec d'inclusion de l'item en raison de poids excessif
            }
        }
    }


    /**
     * Helper class to store nodes for the BFS search.
     */
    private class Node {
        int index;
        int currentWeight;
        int currentValue;
        int parentId;
        int[] itemsSelected;

        public Node(int index, int currentWeight, int currentValue, int parentId, int[] itemsSelected) {
            this.index = index;
            this.currentWeight = currentWeight;
            this.currentValue = currentValue;
            this.parentId = parentId;
            this.itemsSelected = itemsSelected;
        }
    }

    /**
     * Solves the 0/1 Knapsack problem and compares DFS vs BFS performance.
     */
    public static void main(String[] args) {
        int[] weights = {2, 3, 4, 5};
        int[] values = {3, 4, 5, 6};
        int capacity = 5;

        KnapsackSolver solver = new KnapsackSolver(weights, values, capacity);

        long t0 = System.currentTimeMillis();
        solver.solve("dfs", new SolverListener() {
            @Override
            public void solution(int nodeId, int parentId) {
                System.out.println("Solution found at node " + nodeId);
            }

            @Override
            public void branch(int nodeId, int parentId, int numBranches) {
                // Logique de visualisation
            }

            @Override
            public void fail(int nodeId, int parentId) {
                // Logique de visualisation
            }
        });
        long t1 = System.currentTimeMillis();
        System.out.println("DFS Time(ms): " + (t1 - t0));

        System.out.println("Best solution with DFS (Max Value: " + solver.getMaxValue() + "):");
        int[] bestItems = solver.getBestSolution();
        for (int i = 0; i < bestItems.length; i++) {
            if (bestItems[i] == 1) {
                System.out.println("Item " + i + " (Weight: " + weights[i] + ", Value: " + values[i] + ")");
            }
        }
    }


}
