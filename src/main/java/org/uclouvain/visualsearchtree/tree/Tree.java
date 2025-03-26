package org.uclouvain.visualsearchtree.tree;

import java.util.*;

// https://www.microsoft.com/en-us/research/wp-content/uploads/1996/01/drawingtrees.pdf

public class Tree {
    HashMap<Integer, Node> nodeMap;
    int rootId;

    public enum NodeType {
        INNER,
        SKIP,
        FAIL,
        SOLUTION
    }

    /**
     * <b>Note: </b> Création d'un arbre à partir de l'id d'un {@link org.uclouvain.visualsearchtree.tree.Tree.Node Node}
     * @param rootId
     */
    public Tree(int rootId) {
        nodeMap = new HashMap<>();
        this.rootId = rootId;
        System.out.println("put root " + rootId);
        nodeMap.put(rootId, new Node("root"));
    }

    /**
     * <b>Note: </b> Crée un nouveau Node
     */
    public void createNode(int id, int pId, NodeType type, NodeAction nodeAction, String info) {
        Node n = nodeMap.get(pId).addChild(id, "child", type, "branch", nodeAction, info);
        nodeMap.put(id, n);
    }

    /**
     * <b>Note: </b> Associe un Node à son parent pour construire la hiérarchie de l'arbre
     * @param pId parent Id
     * @param n node à attacher
     */
    public void attachToParent(int pId, Node n) {
        if (nodeMap.get(pId) != null) {
            nodeMap.get(pId).children.add(nodeMap.get(n.nodeId));
        }
    }

    /**
     * <b>Note: </b> Ajoute un node dans le nodeMap sans le lier à son parent
     * @param id node Id
     * @param pId parent Id
     * @param type node Type
     * @param nodeAction node action
     * @param info node info
     */
    public void crateIndNode(int id, int pId, NodeType type, NodeAction nodeAction, String info) {
        nodeMap.put(id, new Tree.Node(id, pId, "child", type, new LinkedList<>(), new LinkedList<>(), nodeAction, info));
    }


    /**
     * <b>Note: </b> Retourne le noeud de la racine
     * @return Un {@link org.uclouvain.visualsearchtree.tree.Tree.Node Node} qui est la racine de l'arbre
     */
    public Node root() {
        return nodeMap.get(rootId);
    }

    static record Pair<L, R>(L left, R right) { }

    /**
     * <b>Note: </b> Classe Node utilisée pour créer l'arbre
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
        // Ajout de la largeur du nœud (bounding box). Par défaut 1.0.
        public double nodeWidth = 3.0;

        @Override
        public String toString() {
            return "Node [" +
                    "label=" + label +
                    ", children=" + children +
                    ", edgeLabels=" + edgeLabels +
                    ", nodeAction=" + nodeAction +
                    ", type=" + type +
                    ", width=" + nodeWidth +
                    ']';
        }

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
         * Constructeur complet
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

        /**
         * Ajoute un enfant et retourne ce nouveau Node.
         */
        public Node addChild(int nodeId, T nodeLabel, NodeType type, T branchLabel, NodeAction nodeAction, T info) {
            Node child = new Node(nodeId, nodeLabel, type, new LinkedList<>(), new LinkedList<>(), nodeAction, info);
            // Si nécessaire, vous pouvez définir child.nodeWidth ici (par exemple en fonction de nodeLabel)
            children.add(child);
            edgeLabels.add(branchLabel);
            return child;
        }

        /**
         * La méthode design construit récursivement la disposition de l'arbre et renvoie un PositionedNode.
         */
        /**
         * Lance l'algorithme de Reingold–Tilford pour calculer la disposition finale de l'arbre.
         * Cette méthode réalise une première passe (design_) pour positionner les nœuds relativement
         * puis une deuxième passe (secondWalk) pour appliquer les offsets cumulés.
         */
        public PositionedNode<T> design() {
            // Première passe : construction du tree préliminaire et calcul de l'étendue
            Pair<PositionedNode<T>, Extent> preliminary = design_();
            // Seconde passe : ajustement des positions finales en appliquant les offsets
            PositionedNode<T> finalTree = secondWalk(preliminary.left(), 0);
            return finalTree;
        }

        /**
         * Première passe (design_) : construit récursivement l'arbre positionné et calcule l'Extent.
         * Cette méthode est déjà présente dans votre code et utilise les méthodes Extent.fitList et merge.
         */
        private Pair<PositionedNode<T>, Extent> design_() {
            List<PositionedNode<T>> subtrees = new LinkedList<>();
            List<Extent> subtreeExtents = new LinkedList<>();
            for (Node<T> child : children) {
                Pair<PositionedNode<T>, Extent> res = child.design_();
                subtrees.add(res.left());
                subtreeExtents.add(res.right());
            }
            // Calcul des positions à partir des extents des sous-arbres
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

            // Fusion des extents des sous-arbres
            Extent resExtent = Extent.merge(extentsMoved);
            // Ajout de l'extent du nœud courant, en tenant compte de sa largeur (nodeWidth)
            resExtent.addFirst(-nodeWidth / 2, nodeWidth / 2);
            // Le nœud courant est initialisé avec une position préliminaire à 0
            PositionedNode<T> resTree = new PositionedNode<>(nodeId, label, type, subtreesMoved, edgeLabels, nodeAction, 0, info);
            return new Pair<>(resTree, resExtent);
        }

        /**
         * Deuxième passe (secondWalk) : applique récursivement l'offset cumulé pour fixer les positions finales.
         * @param node Le PositionedNode à ajuster
         * @param offset L'offset cumulé jusqu'à présent
         * @return Le PositionedNode avec sa position finale corrigée
         */
        private PositionedNode<T> secondWalk(PositionedNode<T> node, double offset) {
            // Applique l'offset courant à la position du nœud
            double finalPos = node.position + offset;
            // Reconstruire le nœud avec la position finale
            List<PositionedNode<T>> finalChildren = new LinkedList<>();
            for (PositionedNode<T> child : node.children) {
                finalChildren.add(secondWalk(child, offset));
            }
            return new PositionedNode<>(node.nodeId, node.label, node.type, finalChildren, node.edgeLabels, node.nodeAction, finalPos, node.info);
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
            return info;
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

        @Override
        public String toString() {
            return "PositionedNode{" +
                    "nodeId=" + nodeId +
                    ", position=" + position +
                    ", label=" + label +
                    ", type=" + type +
                    ", children=" + children +
                    ", edgeLabels=" + edgeLabels +
                    ", info=" + info +
                    ", nodeAction=" + nodeAction +
                    '}';
        }
    }

    /**
     * La classe Extent permet de représenter l'étendue horizontale d'un sous-arbre.
     * Ici, nous utilisons un offset pour réaliser les déplacements en O(1).
     * Nous utilisons également NODE_SPACING pour ajuster l'espacement en tenant compte
     * éventuellement de la taille réelle des nœuds.
     */
    static class Extent {
        // Offset pour représenter le déplacement global
        double offset;
        List<Pair<Double, Double>> extentList;
        // Espace minimal entre les nœuds (modifiable)
        static final double NODE_SPACING = 0.0;

        public Extent() {
            this(new LinkedList<>());
        }

        public Extent(List<Pair<Double, Double>> extentList) {
            this.extentList = extentList;
            this.offset = 0.0;
        }

        public Extent(double left, double right) {
            this();
            this.extentList.add(new Pair<>(left, right));
        }

        public boolean isEmpty() {
            return extentList.isEmpty();
        }

        public void add(double x1, double x2) {
            extentList.add(new Pair<>(x1, x2));
        }

        public void addFirst(double x1, double x2) {
            extentList.add(0, new Pair<>(x1, x2));
        }

        /**
         * Déplace l'extent en temps constant grâce à l'offset.
         */
        public Extent move(double x) {
            Extent result = new Extent(this.extentList);
            result.offset = this.offset + x;
            return result;
        }

        /**
         * Fusionne deux extents en tenant compte de leurs offsets, puis normalise le résultat.
         */
        public Extent merge(Extent other) {
            List<Pair<Double, Double>> r = new LinkedList<>();
            Iterator<Pair<Double, Double>> fi = this.extentList.iterator();
            Iterator<Pair<Double, Double>> si = other.extentList.iterator();

            while (fi.hasNext() && si.hasNext()) {
                Pair<Double, Double> p = fi.next();
                Pair<Double, Double> q = si.next();
                double left = p.left() + this.offset;
                double right = q.right() + other.offset;
                r.add(new Pair<>(left, right));
            }
            while (fi.hasNext()) {
                Pair<Double, Double> p = fi.next();
                r.add(new Pair<>(p.left() + this.offset, p.right() + this.offset));
            }
            while (si.hasNext()) {
                Pair<Double, Double> q = si.next();
                r.add(new Pair<>(q.left() + other.offset, q.right() + other.offset));
            }
            return new Extent(r);
        }

        public static Extent merge(List<Extent> extents) {
            Extent r = new Extent(); // extent vide
            for (Extent e : extents) {
                r = r.merge(e);
            }
            return r;
        }

        /**
         * Calcule l'écart minimal nécessaire entre cet extent et l'autre, en tenant compte des offsets.
         * On utilise NODE_SPACING pour définir un espace minimal en plus.
         */
        public Double fit(Extent other) {
            Iterator<Pair<Double, Double>> fi = this.extentList.iterator();
            Iterator<Pair<Double, Double>> si = other.extentList.iterator();
            double minDist = 0.0;
            while (fi.hasNext() && si.hasNext()) {
                Pair<Double, Double> p = fi.next();
                Pair<Double, Double> q = si.next();
                double right = p.right() + this.offset;
                double left = q.left() + other.offset;
                minDist = Math.max(minDist, right - left + NODE_SPACING);
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
            List<Extent> reversed = new LinkedList<>(extents);
            Collections.reverse(reversed);
            List<Double> res = new LinkedList<>();
            Extent acc = new Extent();
            for (Extent e : reversed) {
                double x = -e.fit(acc);
                res.add(x);
                acc = e.move(x).merge(acc);
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
