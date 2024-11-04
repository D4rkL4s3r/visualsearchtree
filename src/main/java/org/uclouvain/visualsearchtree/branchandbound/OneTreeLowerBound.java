package org.uclouvain.visualsearchtree.branchandbound;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A lower bound on the TSP based on the 1-tree relaxation.
 * A minimum 1-tree is composed of the edges:
 * - that belong to the minimum spanning-tree that spans all
 *   nodes of the graph (independently of their direction), except the node 0.
 * - the two edges that connect the node 0 to the two
 *   closest nodes (also independently of their direction).
 */
public class OneTreeLowerBound implements TSPLowerBound {

    @Override
    public TSPLowerBoundResult compute(double[][] distMatrix, boolean[][] excluded) {
        int n = distMatrix.length; // Nombre de villes (nœuds)

        // Étape 1 : Construire le MST (Minimum Spanning Tree) à l'aide de Prim's algorithm, en excluant le nœud 0
        boolean[] inMST = new boolean[n];
        double[] minEdgeCost = new double[n];
        int[] parent = new int[n];
        PriorityQueue<Edge> pq = new PriorityQueue<>(); // Queue de priorité pour les arêtes

        // Initialisation pour les nœuds autres que 0
        for (int i = 1; i < n; i++) {
            minEdgeCost[i] = Double.POSITIVE_INFINITY;
            parent[i] = -1;
        }

        pq.add(new Edge(-1, 1, 0)); // On commence avec le nœud 1 (pas le nœud 0)

        // Construire le MST (en excluant le nœud 0)
        double mstCost = 0.0;
        int mstEdges = 0; // Compteur pour s'assurer que le MST a n-2 arêtes

        while (!pq.isEmpty() && mstEdges < n - 2) {
            Edge edge = pq.poll();
            int currentNode = edge.v2(); // Utiliser la méthode v2() pour accéder à v2

            if (inMST[currentNode] || currentNode == 0) {
                continue; // Si déjà dans le MST ou si c'est le nœud 0, on ignore
            }

            // Inclure le nœud courant dans le MST
            inMST[currentNode] = true;
            if (edge.v1() != -1) {
                mstCost += edge.cost();
                mstEdges++;
                parent[currentNode] = edge.v1();
            }

            // Mettre à jour les coûts des arêtes des nœuds voisins
            for (int nextNode = 1; nextNode < n; nextNode++) {
                if (!inMST[nextNode] && !excluded[currentNode][nextNode] && distMatrix[currentNode][nextNode] < minEdgeCost[nextNode]) {
                    minEdgeCost[nextNode] = distMatrix[currentNode][nextNode];
                    pq.add(new Edge(currentNode, nextNode, minEdgeCost[nextNode])); // Ajouter la nouvelle arête
                }
            }
        }

        // Étape 2 : Trouver les deux arêtes qui connectent le nœud 0 aux deux plus proches nœuds
        List<Edge> edges = new ArrayList<>();
        double firstClosest = Double.POSITIVE_INFINITY;
        double secondClosest = Double.POSITIVE_INFINITY;
        int firstClosestNode = -1;
        int secondClosestNode = -1;

        // Recherche des deux nœuds les plus proches de 0
        for (int j = 1; j < n; j++) { // Commencer à partir de 1 pour ignorer le nœud 0
            if (!excluded[0][j] || !excluded[j][0]) { // Ignorer les arêtes exclues
                double cost = distMatrix[0][j];
                if (cost < firstClosest) {
                    secondClosest = firstClosest;
                    secondClosestNode = firstClosestNode;
                    firstClosest = cost;
                    firstClosestNode = j;
                } else if (cost < secondClosest) {
                    secondClosest = cost;
                    secondClosestNode = j;
                }
            }
        }

        // Ajouter les deux arêtes reliant le nœud 0 aux nœuds les plus proches
        if (firstClosestNode != -1) {
            edges.add(new Edge(0, firstClosestNode, firstClosest));
        }
        if (secondClosestNode != -1) {
            edges.add(new Edge(0, secondClosestNode, secondClosest));
        }

        // Étape 3 : Ajouter les arêtes du MST à la liste (les nœuds 1 à n-1)
        for (int i = 1; i < n; i++) {
            if (parent[i] != -1) {
                edges.add(new Edge(parent[i], i, distMatrix[parent[i]][i])); // Utiliser le coût direct
            }
        }

        // Calculer la borne inférieure totale
        double lowerBound = mstCost + firstClosest + secondClosest; // Ne pas inclure le nœud 0 dans le mstCost

        // Retourner le résultat sous forme d'objet TSPLowerBoundResult
        return new TSPLowerBoundResult(lowerBound, edges);
    }
}
