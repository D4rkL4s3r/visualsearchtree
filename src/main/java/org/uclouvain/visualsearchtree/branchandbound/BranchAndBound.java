package org.uclouvain.visualsearchtree.branchandbound;

import org.uclouvain.visualsearchtree.examples.SolverListener;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * Generic Branch and Bound solver
 * You solve a problem with Branch and Bound
 * by calling the method {@link #minimize(SolverListener, OpenNodes, Consumer)}}
 */
public class BranchAndBound {

    /**
     * Explore completely the search space
     * defined by the nodes initially
     * in the provided open-nodes.
     * using Branch and Bound
     * @param openNode a collection containing
     *                 initially the root node.
     *                 This collection during execution will contain
     *                 the fringe of the exploration.
     *                 The search might be explored in depth-first-search
     *                 or best-first-search depending on the specific implementation
     *                 for the openNode see {@link BestFirstOpenNodes} and {@link DepthFirstOpenNodes}.
     * @param onSolution a closure called each time a new improving
     *                   solution is found.
     * @param <T> is the type of the state/node for the problem to solve
     */
    public static <T> void minimize (SolverListener listener, OpenNodes<T> openNode, Consumer<Node<T>> onSolution) {
        double upperBound = Double.MAX_VALUE;
        int iter = 0;

        System.out.println(openNode.isEmpty());
        System.out.println(openNode.size());
        while (!openNode.isEmpty()) {
            System.out.println(String.format("Iteration %d", ++iter));
            iter++;
            Node<T> n = openNode.remove();
            if (n.isSolutionCandidate()) {
                double objective = n.objectiveFunction();
                if  (objective < upperBound) {
                    upperBound = objective;
                    onSolution.accept(n);
                    //TODO maybe solverListener.accept
                    //openNode.add(n);
                    listener.solution(n.getId(), n.getParent().getId());
                }else {
                    //TODO maybe else avec solverListener.fail
                    //openNode.add(n);
                    listener.fail(n.getId(), n.getParent().getId());
                }
            } else if (n.lowerBound() < upperBound) {
                //TODO maybe solverListener.branch
                if (n.getParent() == null){
                    System.out.println(n.children());
                    openNode.add(n);
                    listener.branch(n.getId(), -1, n.children().size());
                }else {
                    System.out.println(n.children());
                    openNode.add(n);
                    listener.branch(n.getId(), n.getParent().getId(), n.children().size());
                }
                for (Node<T> child : n.children()) {
                    openNode.add(child);
                }
            }
        }
        System.out.println("#iter:" + iter);
    }



}

/**
 * Implementation of a fringe
 * to get depth-first-search strategy
 * @param <T>
 */
class DepthFirstOpenNodes<T> implements OpenNodes<T> {

    Stack<Node<T>> stack;

    DepthFirstOpenNodes() {
        stack = new Stack<>();
    }

    public void add(Node<T> n) {
        stack.push(n);
    }

    public Node<T> remove() {
        return stack.pop();
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public int size() {
        return stack.size();
    }
}

/**
 * Implementation of a fringe
 * to get depth-first-search strategy but among the ties
 * on the depth, selecting the node with best lower-bound
 * @param <T>
 */
class DepthFirstBestFirstOpenNodes<T> implements OpenNodes<T> {

    PriorityQueue<Node<T>> queue;

    DepthFirstBestFirstOpenNodes() {
        queue = new PriorityQueue<>(new Comparator<Node<T>>() {
            @Override
            public int compare(Node<T> o1, Node<T> o2) {
                if (o1.depth() != o2.depth()) {
                    return (o2.depth() - o1.depth());
                } else {
                    return Double.compare(o1.lowerBound(), o2.lowerBound());
                }
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






