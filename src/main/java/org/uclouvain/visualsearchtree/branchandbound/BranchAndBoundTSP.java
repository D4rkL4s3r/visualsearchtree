package org.uclouvain.visualsearchtree.branchandbound;

import org.uclouvain.visualsearchtree.util.tsp.TSPInstance;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
/*Aidé by GPT*/

public class BranchAndBoundTSP {

    /**
     * Entry point for your Branch and Bound TSP Solver
     *
     * You must use the BrandAndBound class without modifying it (except possibly to compute the gap).
     * Have a look at @see {@link org.uclouvain.visualsearchtree.branchandbound.BranchAndBoundKnapsack} for an example.
     * As you will see, you have to implement your own State/Node class.
     *
     * You are free to choose your state representation.
     * This is part of the difficulty of the exercise.
     * Choose one representation that makes it easy to generate the successors of a node.
     *
     * @param instance an instance for the TSP
     * @param lbAlgo a lowe-bound algorithm for TSP
     * @return the list of edges in the optimal solution
     */
    public static List<Edge> optimize(TSPInstance instance, TSPLowerBound lbAlgo) {
        OpenNodes<NodeTSP> openNodes = new BestFirstOpenNodes<>();

        double[][] distanceMatrix = instance.distanceMatrix;
        int numCities = instance.n;

        BitSet visitedCities = new BitSet(numCities);
        visitedCities.set(0, true);

        boolean[][] excluded = new boolean[numCities][numCities];

        TSPLowerBoundResult ub = lbAlgo.compute(distanceMatrix, excluded);

        //root
        openNodes.add(new NodeTSP(distanceMatrix, visitedCities, 0.0, 0, ub.lb()));

        AtomicReference<List<Edge>> atomicReference = new AtomicReference<>();

        BranchAndBound.minimize(null, openNodes,node -> {
            atomicReference.set(node.getState().edges);
        });
        return atomicReference.get();
    }
}

class NodeTSP implements Node<NodeTSP> {
    double[][] distances;
    BitSet visitedCities;
    double currentCost;
    int currentCity;
    int numCities;

    NodeTSP parent;
    int depth;
    double ub;
    LinkedList<Edge> edges;

    public NodeTSP(double[][] distances, BitSet visitedCities,
                   double currentCost, int currentCity, double ub) {
        this.parent = null;
        this.distances = distances;
        this.visitedCities = (BitSet) visitedCities.clone();
        this.currentCost = currentCost;
        this.currentCity = currentCity;
        this.numCities = distances.length;
        this.depth = parent == null ? 0 : parent.depth + 1;

        // Calculer la borne supérieure à l'aide de la méthode détendue
        //this.ub = lpRelaxUBound();
        this.ub = lpRelaxUBound();
        this.edges = new LinkedList<>();
        //this.edges.add(new Edge(0, this.currentCity, distances[0][this.currentCity]));
    }

    public NodeTSP(NodeTSP parent, double[][] distances,
                   double currentCost, int currentCity, double ub) {
        this.parent = parent;
        this.distances = distances;
        this.visitedCities = (BitSet) parent.visitedCities.clone();
        this.visitedCities.set(currentCity, true);
        this.currentCost = currentCost;
        this.currentCity = currentCity;
        this.numCities = distances.length;
        this.depth = parent.depth + 1;

        // Calculer la borne supérieure à l'aide de la méthode détendue
        //this.ub = lpRelaxUBound();
        this.ub = lpRelaxUBound();
        this.edges = (LinkedList<Edge>) parent.edges.clone();
        this.edges.add(new Edge(this.parent.currentCity, currentCity, distances[this.parent.currentCity][this.currentCity]));
    }

    @Override
    public boolean isSolutionCandidate() {
        return ((visitedCities.cardinality() == numCities) && (edges.size() == numCities));
    }

    @Override
    public double objectiveFunction() {
        // L'objectif est de minimiser le coût du chemin (on ajoute la distance pour retourner à la ville de départ si le chemin est complet)
        if (isSolutionCandidate()) {
            return currentCost + distances[currentCity][getFirstVisitedCity()];
        }
        return currentCost;
        /*return ub;*/
    }

    @Override
    public double lowerBound() {
        // Simple heuristique pour la borne inférieure (on peut ajouter des améliorations ici)
        return ub;
    }

    @Override
    public int depth() {
        return depth;
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Node<NodeTSP> getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Node<NodeTSP>> children() {
        List<Node<NodeTSP>> children = new ArrayList<>();

        // Générer les enfants en visitant les villes non visitées
        for (int i = 0; i < numCities; i++) {
            // cas de base
            if (visitedCities.cardinality() == numCities) {
                double newCost = currentCost + distances[currentCity][getFirstVisitedCity()];
                children.add(new NodeTSP(this, distances, newCost, 0, ub));
                return children;
            }
            if (!visitedCities.get(i)) {
                double newCost = currentCost + distances[currentCity][i];
                NodeTSP child = new NodeTSP(this, distances, newCost, i, this.ub);
                children.add(child);
            }
        }
        return children;
    }

    @Override
    public NodeTSP getState() {
        return this;
    }

    @Override
    public String toString() {
        return "Path: " + visitedCities.toString() + " | Cost: " + currentCost;
    }

    /**
     * Computes an upper-bound using a relaxed problem approximation
     * with a Minimum Spanning Tree (MST) for the unvisited cities.
     * This is similar to linear relaxation in knapsack.
     */
    private double lpRelaxUBound() {
        double relaxedCost = currentCost;

        // Créer un BitSet pour les villes non visitées
        BitSet unvisited = new BitSet(numCities);
        unvisited.set(0, numCities);  // Initialiser toutes les villes comme non visitées
        unvisited.andNot(visitedCities);  // Retirer les villes déjà visitées

        if (unvisited.cardinality() == 0) {
            // Si toutes les villes sont visitées, on retourne seulement le coût pour revenir à la ville de départ
            return relaxedCost + distances[currentCity][getFirstVisitedCity()];
        }

        // Ajouter le coût minimal pour se connecter à la première ville non visitée
        double minToUnvisited = Double.MAX_VALUE;
        for (int city = unvisited.nextSetBit(0); city >= 0; city = unvisited.nextSetBit(city + 1)) {
            minToUnvisited = Math.min(minToUnvisited, distances[currentCity][city]);
        }
        relaxedCost += minToUnvisited;

        // Ajouter le coût minimal pour revenir de n'importe quelle ville non visitée à la ville de départ
        double minFromUnvisitedToStart = Double.MAX_VALUE;
        for (int city = unvisited.nextSetBit(0); city >= 0; city = unvisited.nextSetBit(city + 1)) {
            minFromUnvisitedToStart = Math.min(minFromUnvisitedToStart, distances[city][getFirstVisitedCity()]);
        }
        relaxedCost += minFromUnvisitedToStart;

        // Approximer le coût pour visiter toutes les villes non visitées en utilisant un Minimum Spanning Tree (MST)
        relaxedCost += mstApproximation(unvisited);

        return relaxedCost;
    }

    /**
     * Approximate the cost of visiting all unvisited cities using a Minimum Spanning Tree (MST).
     * This gives a lower bound for connecting unvisited cities in an optimal way.
     */
    private double mstApproximation(BitSet unvisited) {
        if (unvisited.cardinality() <= 1) return 0.0;

        // Algorithme de Prim pour calculer l'arbre couvrant minimum (MST)
        double[] minEdge = new double[numCities];
        Arrays.fill(minEdge, Double.MAX_VALUE);
        boolean[] inMST = new boolean[numCities];

        // Commencer avec la première ville non visitée
        int startCity = unvisited.nextSetBit(0);
        minEdge[startCity] = 0;
        double mstCost = 0.0;

        for (int i = 0; i < unvisited.cardinality(); i++) {
            // Trouver la ville avec le coût d'arête minimal qui n'est pas encore dans le MST
            double minCost = Double.MAX_VALUE;
            int nextCity = -1;
            for (int city = unvisited.nextSetBit(0); city >= 0; city = unvisited.nextSetBit(city + 1)) {
                if (!inMST[city] && minEdge[city] < minCost) {
                    minCost = minEdge[city];
                    nextCity = city;
                }
            }

            // Ajouter cette ville au MST
            inMST[nextCity] = true;
            mstCost += minCost;

            // Mettre à jour les arêtes minimales pour les autres villes non visitées
            for (int city = unvisited.nextSetBit(0); city >= 0; city = unvisited.nextSetBit(city + 1)) {
                if (!inMST[city]) {
                    minEdge[city] = Math.min(minEdge[city], distances[nextCity][city]);
                }
            }
        }

        return mstCost;
    }

    // Méthode pour obtenir la première ville visitée
    private int getFirstVisitedCity() {
        return visitedCities.nextSetBit(0);
    }
}



