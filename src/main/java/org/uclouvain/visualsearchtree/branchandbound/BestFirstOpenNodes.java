package org.uclouvain.visualsearchtree.branchandbound;

import java.util.Comparator;
import java.util.PriorityQueue;
/**
 * Implementation of a fringe
 * to get best-first-search strategy
 * @param <T>
 */
public class BestFirstOpenNodes<T> implements OpenNodes<T> {

    PriorityQueue<Node<T>> queue;

    public BestFirstOpenNodes() {
        queue = new PriorityQueue<Node<T>>(new Comparator<Node<T>>() {
            @Override
            public int compare(Node<T> o1, Node<T> o2) {
                return Double.compare(o1.lowerBound(), o2.lowerBound());
            }
        });
    }

    public void add(Node<T> n) {
        queue.add(n);
    }

    public Node<T> remove() {
        return queue.remove();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public int size() {
        return queue.size();
    }
}
