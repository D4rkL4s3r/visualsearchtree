package org.uclouvain.visualsearchtree.tree;

// https://www.microsoft.com/en-us/research/wp-content/uploads/1996/01/drawingtrees.pdf

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class Tree {
    HashMap<Integer,Node> nodeMap;
    int rootId;

    public List<TreeListener> listeners = new ArrayList<TreeListener>();
    public enum NodeType {
        INNER,
        SKIP,
        FAIL,
        SOLUTION
    }

    private void notifyNodeCreated(int id, int pId, NodeType type, NodeAction nodeAction, String info)
    {
        listeners.forEach(lst->lst.onNodeCreated(id, pId, type, nodeAction, info));
    }

    /**
     *
     */
    private void notifyEnd()
    {
        listeners.forEach(l->l.onSearchEnd());
    }

    /**
     *
     */
    public void stopSearch()
    {
        notifyEnd();
    }
    /**
     * Add tree listener
     * @param list
     */
    public void addListener(TreeListener list)
    {
        listeners.add(list);
    }

    /**
     * <b>Note: </b> Create Tree from  {@link org.uclouvain.visualsearchtree.tree.Tree.Node Node} id
     * @param rootId
     */
    public Tree(int rootId) {
        nodeMap = new HashMap<>();
        this.rootId = rootId;
        System.out.println("put root " + rootId);
        nodeMap.put(rootId, new Node("root"));
    }

    /**
     * <b>Note: </b> Create new Npde
     * @param id
     * @param pId
     * @param type
     * @param nodeAction
     * @param info
     */
    public void createNode(int id, int pId, NodeType type, NodeAction nodeAction, String info) {
        Node n = nodeMap.get(pId).addChild(id, pId,"child",type,"branch", nodeAction, info);
        nodeMap.put(id,n);
        notifyNodeCreated(id, pId, type, nodeAction,info);
    }

    /**
     * <b>Note: </b>Assign new  {@link org.uclouvain.visualsearchtree.tree.Tree.Node Node} to it Parent in order to build tree hierarchy
     * @param pId parent Id
     * @param n node to attach
     */
    public void attachToParent(int pId, Node n) {
        if (nodeMap.get(pId) != null) {
            nodeMap.get(pId).children.add(nodeMap.get(n.nodeId));
        }
    }

    /**
     * <b>Note: </b> Add new node to nodemap without linked it to its parent
     * @param id node Id
     * @param pId parent Id
     * @param type node Type
     * @param nodeAction node action
     * @param info node info
     */
    public void crateIndNode(int id, int pId, NodeType type, NodeAction nodeAction, String info){
        nodeMap.put(id, new Tree.Node(id, pId,"child", type, new LinkedList<>(), new LinkedList(), nodeAction, info));
    }

    public Node root() {
        return nodeMap.get(rootId);
    }


    static record Pair<L, R>(L left, R right) {

    }

    /**
     * <b>Note: </b> Used to create Tree
     * @param <T>
     */
    public static class Node<T> {
        public int nodeId;
        public int nodePid;
        public T info;
        public NodeType type;
        public T label;
        public List<Node<T>> children;
        public List<T> edgeLabels;
        public NodeAction nodeAction;

        @Override
        public String toString() {
            return "Node [" +
                    "label=" + label +
                    ", children=" + children +
                    ", edgeLabels=" + edgeLabels +
                    ", nodeAction=" + nodeAction +
                    ", type=" + type +
                    ']';
        }

        public Node(){
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

        public Node(T label, T info, List<Node<T>> children, List<T> edgeLabels, NodeAction nodeAction) {
            this.label = label;
            this.children = children;
            this.edgeLabels = edgeLabels;
            this.nodeAction = nodeAction;
            this.info = info;
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

        public Node(int nodeId, int nodePid, T label, List<Node<T>> children, List<T> edgeLabels, NodeAction nodeAction, NodeType nodeType) {
            this.nodeId = nodeId;
            this.nodePid = nodePid;
            this.label = label;
            this.children = children;
            this.edgeLabels = edgeLabels;
            this.nodeAction = nodeAction;
            this.type = nodeType;
        }

        public Node(int nodeId, int nodePid, T label, List<Node<T>> children, List<T> edgeLabels, NodeType nodeType, T info) {
            this.nodeId = nodeId;
            this.nodePid = nodePid;
            this.label = label;
            this.children = children;
            this.edgeLabels = edgeLabels;
            this.nodeAction = () -> {};
            this.type = nodeType;
            this.info = info;
        }

        /**
         *
         * @param nodeId Node Id
         * @param nodePid node Parent Id
         * @param label Node label
         * @param type Node Type
         * @param children Node children
         * @param edgeLabels Node Edge Labels
         * @param nodeAction Node action
         * @param info Node info
         */
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

        public Node addChild(int nodeId, int nodePid, T nodeLabel, NodeType type, T branchLabel, NodeAction nodeAction, T info) {
            Node child = new Node(nodeId, nodePid,nodeLabel, type, new LinkedList<>(), new LinkedList(), nodeAction, info);
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
            List<Extent> subtreeExtents = new LinkedList<>();
            for (Node<T> child : children) {
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
                subtreesMoved.add(childIte.next().moveTree(pos));
                extentsMoved.add(extentIte.next().move(pos));
            }

            Extent resExtent = Extent.merge(extentsMoved);
            resExtent.addFirst(0, 0);
            PositionedNode<T> resTree = new PositionedNode<T>(nodeId,label, type, subtreesMoved, edgeLabels, nodeAction, 0, info);
            return new Pair(resTree, resExtent);
        }

        public void addChildren(Node<T> newChild) {
            children.add(newChild);
        }

        public T getLabel() {
            return label;
        }
        public int getNodeId() {
            return nodeId;
        }
        public int getNodePid() {
            return nodePid;
        }
        public NodeAction getNodeAction() {
            return nodeAction;
        }
        public NodeType getType() {
            return type;
        }
        public T getInfo() {
            return  info;
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

        public PositionedNode(int id, T label, NodeType type, List<PositionedNode<T>> children, List<T> edgeLabels, org.uclouvain.visualsearchtree.tree.NodeAction nodeAction, double position, T info) {
            this.nodeId=id;
            this.label = label;
            this.type = type;
            this.children = children;
            this.edgeLabels = edgeLabels;
            this.nodeAction = nodeAction;
            this.position = position;
            this.info = info;
        }

//        public PositionedNode moveTree(double x) {
//            return new PositionedNode(label, children, edgeLabels, info, nodeAction, position + x, type);
//        }
        public PositionedNode moveTree(double x) {
            return new PositionedNode(nodeId,label, type, children, edgeLabels, nodeAction, position + x, info);
        }

        @Override
        public String toString() {
            return "PositionedNode{" +
                    "nodeId=" + nodeId +
                    "position=" + position +
                    ", label=" + label +
                    ", type=" + type +
                    ", children=" + children +
                    ", edgeLabels=" + edgeLabels +
                    ", info=" + info +
                    ", nodeAction=" + nodeAction +
                    '}';
        }

    }

    static class Extent {

        double minDist = 1.0;

        List<Pair<Double, Double>> extentList;

        public Extent() {
            this(new LinkedList<Pair<Double, Double>>());
        }

        public Extent(List<Pair<Double, Double>> extentList) {
            this.extentList = extentList;
        }

        public Extent(double left, double right) {
            List.of(new Pair(left, right));
        }


        public boolean isEmpty() {
            return extentList.isEmpty();
        }

        public void add(double x1, double x2) {
            extentList.add(new Pair(x1, x2));
        }

        public void addFirst(double x1, double x2) {
            extentList.add(0, new Pair(x1, x2));
        }

        public Extent move(double x) {
            return new Extent(extentList.stream().map(p -> new Pair<Double, Double>(p.left() + x, p.right() + x)).toList());
        }


        public Extent merge(Extent other) {

            List<Pair<Double, Double>> f = extentList;
            List<Pair<Double, Double>> s = other.extentList;
            List<Pair<Double, Double>> r = new LinkedList<>();

            Iterator<Pair<Double, Double>> fi = f.iterator();
            Iterator<Pair<Double, Double>> si = s.iterator();

            while (fi.hasNext() && si.hasNext()) {
                r.add(new Pair(fi.next().left(), si.next().right()));
            }

            if (!fi.hasNext()) {
                while (si.hasNext()) {
                    r.add(si.next());
                }
            }

            if (!si.hasNext()) {
                while (fi.hasNext()) {
                    r.add(fi.next());
                }
            }
            return new Extent(r);
        }

        public static Extent merge(List<Extent> extents) {
            Extent r = new Extent(); // empty
            for (Extent e : extents) {
                r = r.merge(e);
            }
            return r;
        }

        public Double fit(Extent other) {
            List<Pair<Double, Double>> f = extentList;
            List<Pair<Double, Double>> s = other.extentList;

            Iterator<Pair<Double, Double>> fi = f.iterator();
            Iterator<Pair<Double, Double>> si = s.iterator();

            Double minDist = 0.0;

            while (fi.hasNext() && si.hasNext()) {
                minDist = Math.max(minDist, fi.next().right() - si.next().left() +1);
            }
            return minDist;

        }

        public static List<Double> fitListLeft(List<Extent> extents) {
            List<Double> res = new LinkedList<>();
            Extent acc = new Extent();
            for (Extent e : extents) {
                double x = acc.fit(e);
                res.add(x);
                acc = acc.merge(e.move(x));
            }
            return res;
        }

        public static List<Double> fitListRight(List<Extent> extents) {
            Collections.reverse(extents);
            List<Double> res = new LinkedList<>();
            Extent acc = new Extent();
            for (Extent e : extents) {
                double x = -e.fit(acc);
                res.add(x);
                acc = e.move(x).merge(acc);
            }
            Collections.reverse(extents);
            Collections.reverse(res);
            return res;
        }

        public static List<Double> fitList(List<Extent> extents) {
            List<Double> left = fitListLeft(extents);
            List<Double> right = fitListRight(extents);
            List<Double> res = new LinkedList<>();
            for (Iterator<Double> leftIte = left.iterator(), rightIte = right.iterator(); leftIte.hasNext() && rightIte.hasNext(); ) {
                res.add((leftIte.next() + rightIte.next()) / 2);
            }
            return res;
        }
    }
}


