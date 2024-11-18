package org.uclouvain.visualsearchtree.branchandbound;

/**
 * ADT for representing the fringe (open-nodes)
 * in the implementation of a BnB
 * @param <T> the type of the state of the nodes
 */
public interface OpenNodes<T> {
    /**
     * Add a node to the fringe
     * @param n the node
     */
    void add(Node<T> n);

    /**
     * Remove a node from the fringe
     * @return a node
     */
    Node<T> remove();

    /**
     * Verify if the fringe is empty
     * @return true if the fringe is empty
     */
    boolean isEmpty();

    /**
     * The number of nodes in the fringe
     * @return the number of nodes in the fringe
     */
    int size();
}
