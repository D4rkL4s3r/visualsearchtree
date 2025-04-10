package org.uclouvain.visualsearchtree.tree;

import java.util.*;

public class Tree {
    HashMap<Integer, Node> nodeMap;
    int rootId;

    public enum NodeType {
        INNER, SKIP, FAIL, SOLUTION
    }

    public Tree(int rootId) {
        nodeMap = new HashMap<>();
        this.rootId = rootId;
        System.out.println("put root " + rootId);
        nodeMap.put(rootId, new Node("root"));
    }

    public void createNode(int id, int pId, NodeType type, NodeAction nodeAction, String info) {
        Node n = nodeMap.get(pId).addChild(id, "child", type, "branch", nodeAction, info);
        nodeMap.put(id, n);
    }

    public void attachToParent(int pId, Node n) {
        if (nodeMap.get(pId) != null) {
            nodeMap.get(pId).children.add(nodeMap.get(n.nodeId));
        }
    }

//    public void crateIndNode(int id, int pId, NodeType type, NodeAction nodeAction, String info) {
//        nodeMap.put(id, new Tree.Node(id, pId, "child", type, new LinkedList<>(), new LinkedList(), nodeAction, info));
//    }

    public Node root() {
        return nodeMap.get(rootId);
    }

    static record Pair<L, R>(L left, R right) {}

    public static class Node<T> {
        public int nodeId;
        public int nodePid;
        public T info;
        public NodeType type;
        public T label;
        public List<Node<T>> children;
        public List<T> edgeLabels;
        public NodeAction nodeAction;

        public Node() {
            this.type = NodeType.INNER;
            this.children = new LinkedList<>();
            this.edgeLabels = new LinkedList<>();
            this.nodeAction = () -> {};
        }

        public Node(T label, T info, List<Node<T>> children, List<T> edgeLabels, NodeAction nodeAction) {
            this.label = label;
            this.info = info;
            this.children = children;
            this.edgeLabels = edgeLabels;
            this.nodeAction = nodeAction;
        }

        public Node(int nodeId, int nodePid, T label, List<Node<T>> children, List<T> edgeLabels, NodeType type, T info) {
            this.nodeId = nodeId;
            this.nodePid = nodePid;
            this.label = label;
            this.children = children;
            this.edgeLabels = edgeLabels;
            this.type = type;
            this.info = info;
        }

        public Node(T label) {
            this.label = label;
            this.type = NodeType.INNER;
            this.children = new LinkedList<>();
            this.edgeLabels = new LinkedList<>();
            this.nodeAction = () -> {};
        }

        public Node(int nodeId, T label, NodeType type, List<Node<T>> children, List<T> edgeLabels, NodeAction nodeAction, T info) {
            this.nodeId = nodeId;
            this.label = label;
            this.type = type;
            this.children = children;
            this.edgeLabels = edgeLabels;
            this.nodeAction = nodeAction;
            this.info = info;
        }

        public Node addChild(int nodeId, T nodeLabel, NodeType type, T branchLabel, NodeAction nodeAction, T info) {
            Node child = new Node(nodeId, nodeLabel, type, new LinkedList<>(), new LinkedList(), nodeAction, info);
            children.add(child);
            edgeLabels.add(branchLabel);
            return child;
        }

        public PositionedNode<T> design() {
            Pair<PositionedNode<T>, Extent> res = design_();
            return res.left();
        }

        private Pair<PositionedNode<T>, Extent> design_() {
            if (children.isEmpty()) {
                Extent extent = new Extent(0.0, 0.0, 0.0);
                PositionedNode<T> positioned = new PositionedNode<>(nodeId, label, type, new LinkedList<>(), edgeLabels, nodeAction, 0.0, info);
                return new Pair<>(positioned, extent);
            }

            List<PositionedNode<T>> subtrees = new LinkedList<>();
            List<Extent> subtreeExtents = new LinkedList<>();
            List<Node<T>> childrenCopy = new ArrayList<>(children); // Copie défensive
            for (Node<T> child : childrenCopy) {
                Pair<PositionedNode<T>, Extent> res = child.design_();
                subtrees.add(res.left());
                subtreeExtents.add(res.right());
            }

            List<Double> positions = Extent.fitList(subtreeExtents);

            List<PositionedNode<T>> subtreesMoved = new LinkedList<>();
            List<Extent> extentsMoved = new LinkedList<>();
            Iterator<PositionedNode<T>> childIte = subtrees.iterator();
            Iterator<Extent> extentIte = subtreeExtents.iterator();
            Iterator<Double> posIte = positions.iterator();

            while (childIte.hasNext() && posIte.hasNext() && extentIte.hasNext()) {
                double pos = posIte.next();
                PositionedNode<T> child = childIte.next();
                Extent extent = extentIte.next();
                subtreesMoved.add(child.moveTree(pos));
                extentsMoved.add(extent.move(pos));
            }

            Extent resExtent = extentsMoved.stream().reduce(new Extent(0.0, 0.0, 0.0), Extent::merge);
            PositionedNode<T> resTree = new PositionedNode<>(nodeId, label, type, subtreesMoved, edgeLabels, nodeAction, 0.0, info);
            return new Pair<>(resTree, resExtent);
        }

        public T getLabel() {
            return label;
        }
    }

    public static class PositionedNode<T> {
        public int nodeId;
        public double position;
        public T label;
        public NodeType type;
        public List<PositionedNode<T>> children;
        public List<T> edgeLabels;
        public T info;
        public NodeAction nodeAction;

        public PositionedNode(int id, T label, NodeType type, List<PositionedNode<T>> children, List<T> edgeLabels, NodeAction nodeAction, double position, T info) {
            this.nodeId = id;
            this.label = label;
            this.type = type;
            this.children = children;
            this.edgeLabels = edgeLabels;
            this.nodeAction = nodeAction;
            this.position = position;
            this.info = info;
        }

        public PositionedNode moveTree(double x) {
            return new PositionedNode(nodeId, label, type, children, edgeLabels, nodeAction, position + x, info);
        }
    }

    static class Extent {
        double basePosition;
        double left;  // Position relative gauche
        double right; // Position relative droite

        public Extent(double basePosition, double left, double right) {
            this.basePosition = basePosition;
            this.left = left;
            this.right = right;
        }

        public Extent move(double x) {
            return new Extent(basePosition + x, left, right);
        }

        public static double fit(Extent left, Extent right) {
            return left.basePosition + left.right - (right.basePosition + right.left) + 1.0;
        }

        public static Extent merge(Extent first, Extent second) {
            double left = Math.min(first.left, second.basePosition - first.basePosition + second.left);
            double right = Math.max(first.right, second.basePosition - first.basePosition + second.right);
            return new Extent(first.basePosition, left, right);
        }

        public static List<Double> fitListLeft(List<Extent> extents) {
            List<Double> res = new LinkedList<>();
            Extent acc = new Extent(0.0, 0.0, 0.0);
            for (Extent e : extents) {
                double x = fit(acc, e);
                res.add(x);
                acc = merge(acc, e.move(x));
            }
            return res;
        }

        public static List<Double> fitListRight(List<Extent> extents) {
            List<Extent> reversed = new LinkedList<>(extents);
            Collections.reverse(reversed);
            List<Double> res = new LinkedList<>();
            Extent acc = new Extent(0.0, 0.0, 0.0);
            for (Extent e : reversed) {
                double x = -fit(e, acc);
                res.add(x);
                acc = merge(e.move(x), acc);
            }
            Collections.reverse(res);
            return res;
        }

        public static List<Double> fitList(List<Extent> extents) {
            List<Double> left = fitListLeft(extents);
            List<Double> right = fitListRight(extents);
            List<Double> res = new LinkedList<>();
            Iterator<Double> leftIte = left.iterator();
            Iterator<Double> rightIte = right.iterator();
            while (leftIte.hasNext() && rightIte.hasNext()) {
                res.add((leftIte.next() + rightIte.next()) / 2);
            }
            return res;
        }
    }
}