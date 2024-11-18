package org.uclouvain.visualsearchtree.branchandbound;

import java.util.ArrayList;
import java.util.List; /**
 * Node/State implementation of the Knapsack
 * In this representation, a node is related
 * to the decision at of item positioned at index
 * with value[index] and weight[index].
 * Depending on the `selected` status, the item
 * is considered to be part of the knapsack
 * of excluded of it.
 * In this representation, all the items before
 * index have already been decided and the ones after
 * are undecided and remain to be branched on.
 */
public class NodeKnapsack implements Node<NodeKnapsack> {

    private static int idCounter = 0; // Static counter for generating unique IDs
    private int id; // Unique ID for each node

    public int[] value;
    public int[] weight;
    int selectedValue;
    public int capaLeft;
    public int index;
    public boolean selected;
    public NodeKnapsack parent;
    double ub;

    int depth;

    ArrayList<Integer> selectedValues;
    ArrayList<Integer> selectedWeights;

    /**
     *
     * @param parent reference to the parent node
     * @param value values of each item of the problem
     * @param weight weights of each item of the problem
     * @param selectedValue total amount of value in the knapsack
     * @param capaLeft total amount of capacithy left in the knapsack
     * @param index the index of item related to the decision of this node
     * @param selected is the item at index included or not in the knapsack
     */
    public NodeKnapsack(NodeKnapsack parent, int[] value, int[] weight,
                        int selectedValue, int capaLeft, int index, boolean selected) {
        this.parent = parent;
        this.value = value;
        this.weight = weight;
        this.selectedValue = selectedValue;
        this.capaLeft = capaLeft;
        this.index = index;
        this.selected = selected;
        this.depth = parent == null ? 0: parent.depth+1;
        this.ub = lpRelaxUBound();
        this.id = idCounter++;
        //this.ub = capacityRelaxUBound();

        // Initialize selected items lists based on parent state
        this.selectedValues = (parent == null) ? new ArrayList<>() : new ArrayList<>(parent.selectedValues);
        this.selectedWeights = (parent == null) ? new ArrayList<>() : new ArrayList<>(parent.selectedWeights);
    }

    @Override
    public int depth() {
        return depth;
    }

    /**
     * Computes an upper-bound obtained
     * by relaxing the capacity constraint
     */
    private double capacityRelaxUBound() {
        int valueUb = selectedValue;
        for (int i = index + 1; i < value.length; i++) {
            valueUb += value[i];
        }
        return valueUb; // maximization problem
    }

    /**
     * Computes an upper-bound obtained
     * with linear programming relaxation
     * that is each item yet to be decided
     * can be fractionally selected.
     */
    private double lpRelaxUBound() {
        int valueUb = selectedValue;
        int c = capaLeft;
        for (int i = index + 1; i < value.length; i++) {
            if (weight[i] < c) {
                valueUb += value[i];
                c -= weight[i];
            } else {
                valueUb += ((double) c)/weight[i] * value[i];
                break;
            }
        }
        return valueUb; // maximization problem
    }

    @Override
    public double objectiveFunction() {
        return -ub;
    }

    @Override
    public double lowerBound() {
        return -ub;
    }

        @Override
        public boolean isSolutionCandidate() {
            return index == value.length - 1;
        }

    @Override
    public List<Node<NodeKnapsack>> children() {

        List<Node<NodeKnapsack>> children = new ArrayList<>();
        // do not select item at index+1
        NodeKnapsack right = new NodeKnapsack(this, value, weight,
                selectedValue,
                capaLeft,
                index + 1, false);
        children.add(right);
        if (capaLeft >= weight[index+1]) {
            // select item at index+1
            NodeKnapsack left = new NodeKnapsack(this, value, weight,
                    selectedValue + value[index + 1],
                    capaLeft - weight[index + 1],
                    index + 1, true);
            children.add(left);
        }
        return children;
    }

    @Override
    public String toString() {
        ArrayList<Integer> selected = new ArrayList<>();
        NodeKnapsack current = this;
        while (current.index != -1) {
            if (current.selected) {
                selected.add(current.index);
            }
            current = current.parent;
        }
        return selected.toString();
    }

    @Override
    public NodeKnapsack getState() {
        return this;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Node<NodeKnapsack> getParent() {
        return parent;
    }
}
