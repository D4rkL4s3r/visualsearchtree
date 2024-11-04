package org.uclouvain.visualsearchtree.branchandbound;

import java.util.List;

/**
 * Node interface to be used in Branch and Bound.
 * It assumes a minimization problem
 * (just take the opposite if you have a maximization problem)
 *
 * @param <T>: the type of the "state" associated with the given node.
 */
public interface Node<T> {

    /**
     * Return true if the node is a complete solution, that is
     * a leaf node that is a feasible solution to the problem
     * (but not necessarily optimal).
     * @return true if the node is a complete solution (not necessarily optimal)
     */
    boolean isSolutionCandidate();

    /**
     * The objective function of the node.
     * @return The objective function of the node.
     *         It only makes sense to call this function
     *         when the node is a solution candidate
     */
    double objectiveFunction();

    /**
     * A lower bound on the objective function.
     * This function can be called on any-node, not only
     * for a solution candidate.
     * @return A lower bound on the objective function
     */
    double lowerBound();

    /**
     * Depth of the node
     * @return the depth of the node
     */
    int depth();

    /**
     * ID of the node
     * @return the id of the node
     */
    int getId();

    /**
     * Parent of the node
     * @return the parent of the node
     */
    Node<T> getParent();

    List<Node<T>> children();

    /** @return the state associated with this current node */
    T getState();
}
