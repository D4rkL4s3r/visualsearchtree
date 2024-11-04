package org.uclouvain.visualsearchtree.dynamicprogramming;

import org.uclouvain.visualsearchtree.util.Solution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DynamicProgramming<S extends State> {

    Model<S> model;               // the dynamic programming model to solve
    HashMap<State, Double> table; // table to store the best value found for each state
    HashMap<State, Integer> decisions;

    /**
     * Creates a DynamicProgramming object for the given problem
     * @param model the DP problem to solve
     */
    public DynamicProgramming(Model<S> model) {
        this.model = model;
        this.table = new HashMap<>();
        this.decisions = new HashMap<>();
    }

    /**
     * Computes the optimal solution of the DP problem and returns the Solution object
     * @return the solution containing the objective value and the decisions taken
     */
    public Solution getSolution() {
        //My code
        S rootState = model.getRootState();
        double optimalValue = getValueForState(rootState);

        return rebuildSolution();
    }

    /**
     * Computes the optimal solution for a given state of the DP model
     * @param state a state of the DP model
     * @return the value of the optimal solution for the given state
     */
    private double getValueForState(S state) {
        // Check if the state is a base case
        if (model.isBaseCase(state)) {
            return model.getBaseCaseValue(state);
        }

        // Check if the value is already computed
        if (table.containsKey(state)) {
            return table.get(state);
        }

        double bestValue = model.isMaximization() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        int bestDecision = -1;

        // Iterate over all transitions from the current state
        for (Transition<S> transition : model.getTransitions(state)) {
            S successor = transition.getSuccessor();
            double value = transition.getValue() + getValueForState(successor);

            // Choose the best value depending on whether we are maximizing or minimizing
            if ((model.isMaximization() && value > bestValue) || (!model.isMaximization() && value < bestValue)) {
                bestValue = value;
                bestDecision = transition.getDecision();
            }
        }

        // Store the best value and the best decision
        table.put(state, bestValue);
        decisions.put(state, bestDecision);

        return bestValue;
    }

    /**
     * Recomputes and returns the optimal solution of the DP problem and the set of decisions taken to obtain it
     * @return the optimal solution of the DP problem and the set of decisions taken to obtain it
     */
    private Solution rebuildSolution() {
        LinkedList<Integer> decisionList = new LinkedList<>();
        S state = model.getRootState();

        // Rebuild the solution by following the best decisions stored
        while (!model.isBaseCase(state)) {
            int decision = decisions.get(state);
            decisionList.add(decision);

            // Move to the successor state corresponding to the decision
            for (Transition<S> transition : model.getTransitions(state)) {
                if (transition.getDecision() == decision) {
                    state = transition.getSuccessor();
                    break;
                }
            }
        }

        // Create the solution object with the best value and the decisions taken
        double objectiveValue = table.get(model.getRootState());
        return new Solution(objectiveValue, decisionList);
    }

}

/**
 * Interface for describing a dynamic programming model
 */
abstract class Model<S extends State> {

    /**
     * Returns true if the state is a base case of the dynamic programming model
     * @return true if the state is a base case of the dynamic programming model
     */
    abstract boolean isBaseCase(S state);

    /**
     * Returns the value of the base case
     * @return the value of the base case
     */
    abstract double getBaseCaseValue(S state);

    /**
     * Returns the root state of the dynamic programming model, the one that represents the whole problem
     * @return the root state of the dynamic programming model
     */
    abstract S getRootState();

    /**
     * Returns the list of transitions from the given state
     * @return the list of transitions from the given state
     */
    abstract List<Transition<S>> getTransitions(S state);

    /**
     * Returns true if the problem is a maximization
     * @return true if the problem is a maximization
     */
    abstract boolean isMaximization();
}

/**
 * State interface for dynamic programming
 * Equivalent states should have equal hash values
 */
abstract class State {

    /**
     * Computes a hash value that uniquely identifies a state of the dynamic programming model
     * Equivalent states should thus have equal hash values
     * Hint: use Objects.hash(...) with the fields related to the dynamic programming state
     * @return a hash of the state
     */
    abstract int hash();

    /**
     * Returns true if both states are equal, needed in case of collisions in the hash table
     * @return true if both states are equal
     */
    abstract boolean isEqual(State state);
    
    @Override
    public int hashCode() {
        return this.hash();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof State) {
            State state = (State) o;
            return isEqual(state);
        }
        return false;
    }

}

/**
 * Class representing a transition in the dynamic programming model
 */
class Transition<S extends State> {

    private S successor;
    private int decision;
    private double value;

    public Transition(S successor, int decision, double value) {
        this.successor = successor;
        this.decision = decision;
        this.value = value;
    }

    public S getSuccessor() {
        return successor;
    }

    public int getDecision() {
        return decision;
    }

    public double getValue() {
        return value;
    }
}