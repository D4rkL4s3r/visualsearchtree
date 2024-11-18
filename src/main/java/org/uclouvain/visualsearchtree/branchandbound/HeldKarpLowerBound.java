package org.uclouvain.visualsearchtree.branchandbound;


import java.util.LinkedList;
import java.util.List;



public class HeldKarpLowerBound implements TSPLowerBound {


    public HeldKarpLowerBound() {
    }


    @Override
    public TSPLowerBoundResult compute(double[][] distanceMatrix, boolean[][] excludedEdges) {
        int n = distanceMatrix.length;
        double[] pi = new double[n];  // Multiplicateurs de Lagrange
        double lb = Double.MAX_VALUE;
        double best = -Double.MAX_VALUE;
        double lambda = 0.1;
        int[] degrees = new int[n];
        List<Edge> bestEdges = new LinkedList<>();
        OneTreeLowerBound oneTree = new OneTreeLowerBound();

        // Algorithme de relaxation lagrangienne
        while (lambda >= 1e-5) {  // Condition de convergence (ε)
            // Étape 1 : Calcul des distances ajustées avec les multiplicateurs actuels
            double[][] adjustedDistance = new double[n][n];
            for (int i=0; i < n; i++) {
                for (int j=0; j < n; j++) {
                    adjustedDistance[i][j] = distanceMatrix[i][j] - pi[i] - pi[j];
                }
            }

            // Étape 2 : Calculer un arbre couvrant avec les distances ajustées
            //OneTreeLowerBound oneTree = new OneTreeLowerBound();
            TSPLowerBoundResult oneTreeResult = oneTree.compute(adjustedDistance, excludedEdges);
            List<Edge> currentEdges = oneTreeResult.edges();

            // Vérifier si l'arbre est hamiltonien (cycle couvrant toutes les villes)
            if (isHamiltonian(currentEdges, n)) {
                // Si un TSP optimal est trouvé
                /*best = oneTreeLb;
                bestEdges.clear();  // Effacer les arêtes précédentes
                bestEdges.addAll(currentEdges);  // Mettre à jour les meilleures arêtes*/
                //break;  // TSP optimal trouvé
                return oneTreeResult;
            }

            // Mise à jour de la borne inférieure et vérification d'amélioration
            if (oneTreeResult.lb() > lb) {
                lambda *= 0.9;  // Réduire λ si une meilleure borne inférieure est trouvée
            }

            // Mise à jour des valeurs si nécessaire
            lb = oneTreeResult.lb();
            //best = Math.max(lb, best);

            for (Edge edge : oneTreeResult.edges()) {
                degrees[edge.v1()]++;
                degrees[edge.v2()]++;
            }

            // Mise à jour des multiplicateurs de Lagrange (pi) avec la formule magique
            double mu = lambda * lb / sumOfSquaresOfDegrees(degrees, n);
            for (int i = 0; i < n; i++) {
                pi[i] += mu * (2-degrees[i]);  // Mise à jour des multiplicateurs
            }

            System.out.println("lb");
            System.out.println(lb);
            System.out.println("best");
            System.out.println(best);

            if (lb > best) {
                best = lb;
                bestEdges = currentEdges;
                System.out.println(currentEdges.size());
            }
        }

        return new TSPLowerBoundResult(best, bestEdges);  // Retourner la meilleure solution trouvée
    }

    // Méthode pour vérifier si un arbre est hamiltonien (si toutes les villes sont visitées exactement une fois)
    private boolean isHamiltonian(List<Edge> edges, int numCities) {
        // Vérifier que chaque ville a exactement 2 arêtes (pour former un cycle)
        int[] degree = new int[numCities];
        for (Edge edge : edges) {
            degree[edge.v1()]++;
            degree[edge.v2()]++;
        }
        for (int i = 0; i < numCities; i++) {
            if (degree[i] != 2) {
                return false;
            }
        }
        return true;
    }

    // Méthode pour calculer la somme des carrés des degrés des sommets
    private double sumOfSquaresOfDegrees(int[] degrees, int numCities) {
        double sum = 0;
        for (int i = 0; i < numCities; i++) {
            sum += Math.pow(degrees[i] - 2, 2);
        }
        return sum;
    }
}
