
package org.uclouvain.visualsearchtree.tree;

import java.util.*;

public class TreeModified {
    HashMap<Integer, Node> nodeMap;
    int rootId;

    public enum NodeType {
        INNER, SKIP, FAIL, SOLUTION
    }

    public TreeModified(int rootId) {
        nodeMap = new HashMap<>();
        this.rootId = rootId;
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

    public void crateIndNode(int id, int pId, NodeType type, NodeAction nodeAction, String info) {
        nodeMap.put(id, new Node(id, pId, "child", type, new LinkedList<>(), new LinkedList<>(), nodeAction, info));
    }

    public Node root() {
        return nodeMap.get(rootId);
    }

    static record Pair<L, R>(L left, R right) {
    }

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

        public Node(T label) {
            this.label = label;
            this.type = NodeType.INNER;
            this.children = new LinkedList<>();
            this.edgeLabels = new LinkedList<>();
            this.nodeAction = () -> {};
        }

        public Node(int nodeId, int nodePid, T label, NodeType type, List<Node<T>> children, List<T> edgeLabels, NodeAction nodeAction, T info) {
            this.nodeId = nodeId;
            this.nodePid = nodePid;
            this.label = label;
            this.type = type;
            this.children = children;
            this.edgeLabels = edgeLabels;
            this.nodeAction = nodeAction;
            this.info = info;
        }

        public Node addChild(int nodeId, T nodeLabel, NodeType type, T branchLabel, NodeAction nodeAction, T info) {
            Node child = new Node(nodeId, nodePid, nodeLabel, type, new LinkedList<>(), new LinkedList<>(), nodeAction, info);
            children.add(child);
            edgeLabels.add(branchLabel);
            return child;
        }

        public PositionedNode<T> design() {
            Pair<PositionedNode<T>, Extent> res = design_();
            return res.left();
        }

        private Pair<PositionedNode<T>, Extent> design_() {
            List<PositionedNode<T>> subtrees = new LinkedList<>();
            Extent cumulativeExtent = new Extent();

            for (Node<T> child : children) {
                Pair<PositionedNode<T>, Extent> childRes = child.design_();
                subtrees.add(childRes.left());
                cumulativeExtent.mergeIncremental(childRes.right());
            }

            List<Double> positions = Extent.fitList(Collections.singletonList(cumulativeExtent));

            List<PositionedNode<T>> positionedChildren = new LinkedList<>();
            Iterator<PositionedNode<T>> childIter = subtrees.iterator();
            Iterator<Double> posIter = positions.iterator();

            while (childIter.hasNext() && posIter.hasNext()) {
                positionedChildren.add(childIter.next().moveTree(posIter.next()));
            }

            PositionedNode<T> resultNode = new PositionedNode<>(nodeId, label, type, positionedChildren, edgeLabels, nodeAction, 0, info);
            return new Pair<>(resultNode, cumulativeExtent);
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
        List<Double> offsets;

        public Extent() {
            this.offsets = new LinkedList<>();
        }

        public void addOffset(double offset) {
            offsets.add(offset);
        }

        public Extent move(double delta) {
            Extent moved = new Extent();
            for (double offset : offsets) {
                moved.offsets.add(offset + delta);
            }
            return moved;
        }

        public double getMaxOffset() {
            return offsets.stream().max(Double::compareTo).orElse(0.0);
        }

        public void mergeIncremental(Extent other) {
            Iterator<Double> thisIter = this.offsets.iterator();
            Iterator<Double> otherIter = other.offsets.iterator();

            while (thisIter.hasNext() && otherIter.hasNext()) {
                double newOffset = Math.max(thisIter.next(), otherIter.next());
                this.offsets.add(newOffset);
            }

            otherIter.forEachRemaining(this.offsets::add);
        }

        public static List<Double> fitList(List<Extent> extents) {
            List<Double> left = fitListLeft(extents);
            Collections.reverse(extents);
            List<Double> right = fitListLeft(extents);
            Collections.reverse(extents);
            Collections.reverse(right);

            List<Double> result = new LinkedList<>();
            for (int i = 0; i < left.size(); i++) {
                result.add((left.get(i) + right.get(i)) / 2);
            }
            return result;
        }

        public static List<Double> fitListLeft(List<Extent> extents) {
            List<Double> positions = new LinkedList<>();
            double cumulativeOffset = 0;

            for (Extent e : extents) {
                positions.add(cumulativeOffset);
                cumulativeOffset += e.getMaxOffset() + 1;
            }

            return positions;
        }
    }
}
