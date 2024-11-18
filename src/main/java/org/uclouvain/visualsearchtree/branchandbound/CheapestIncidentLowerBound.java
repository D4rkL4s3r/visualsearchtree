package org.uclouvain.visualsearchtree.branchandbound;

import java.util.ArrayList;
import java.util.List;

/**
 * Compute the cheapest incident edges lower bound for the TSP.
 * For each node, the cheapest incoming edge is selected.
 * The sum of these edges is a lower bound on the TSP.
 */
public class CheapestIncidentLowerBound implements TSPLowerBound {

    public CheapestIncidentLowerBound() {
    }

    @Override
    public TSPLowerBoundResult compute(double[][] distanceMatrix, boolean[][] excludedEdges) {
        int n = distanceMatrix.length; // Nombre de villes (nœuds)
        double lowerBound = 0.0; // Borne inférieure
        List<Edge> edges = new ArrayList<>(); // Liste des arêtes pour la borne inférieure

        // Pour chaque nœud, trouver l'arête entrante la moins chère
        for (int j = 0; j < n; j++) {
            double minCost = Double.POSITIVE_INFINITY; // Coût minimal pour le nœud j
            int minEdgeFrom = -1; // Nœud d'origine de l'arête entrante

            for (int i = 0; i < n; i++) {
                // Vérifie que ce n'est pas le même nœud et que l'arête n'est pas exclue
                if (i != j && !excludedEdges[i][j]) {
                    if (distanceMatrix[i][j] < minCost) {
                        minCost = distanceMatrix[i][j]; // Met à jour le coût minimal
                        minEdgeFrom = i; // Met à jour le nœud d'origine de l'arête
                    }
                }
            }

            // Si nous avons trouvé une arête entrante, l'ajouter à la borne inférieure
            if (minCost < Double.POSITIVE_INFINITY) {
                lowerBound += minCost;
                edges.add(new Edge(minEdgeFrom, j, minCost)); // Ajouter l'arête à la liste
            }
        }

        // Retourne le résultat sous forme d'objet TSPLowerBoundResult
        return new TSPLowerBoundResult(lowerBound, edges);
    }
}
