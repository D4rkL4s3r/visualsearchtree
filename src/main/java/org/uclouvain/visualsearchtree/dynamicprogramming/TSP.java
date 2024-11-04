package org.uclouvain.visualsearchtree.dynamicprogramming;

import org.uclouvain.visualsearchtree.util.tsp.TSPInstance;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class TSP extends Model<TSPState> {

    TSPInstance instance;
    TSPState root;

    public TSP(TSPInstance instance) {
        this.instance = instance;

        BitSet visitedCities = new BitSet(instance.n);
        visitedCities.set(0);
        root = new TSPState(0, visitedCities);
    }

    @Override
    boolean isBaseCase(TSPState state) {
        // My code
        return state.getVisitedCities().cardinality() == instance.n;
    }

    @Override
    double getBaseCaseValue(TSPState state) {
        // My code
        return instance.distanceMatrix[state.getCurrentCity()][0];
    }

    @Override
    TSPState getRootState() {
        return root;
    }

    @Override
    List<Transition<TSPState>> getTransitions(TSPState state) {
        List<Transition<TSPState>> distances = new LinkedList<>();
        int currentCity = state.getCurrentCity();
        for (int i = 0; i < instance.n; i++) {
            if (!state.getVisitedCities().get(i)){
                BitSet newVisited = (BitSet) state.getVisitedCities().clone();
                newVisited.set(i);

                TSPState newState = new TSPState(i, newVisited);

                double distanceCost = instance.distanceMatrix[currentCity][i];
                distances.add(new Transition<>(newState, i, distanceCost));
            }
        }
         return distances;
    }

    @Override
    boolean isMaximization() {
        return false;
    }
    
}
