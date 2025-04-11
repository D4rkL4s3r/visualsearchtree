package org.uclouvain.visualsearchtree.tree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;

public class TreeExtentComparisonTest {

    private void compareExtents(OldTree.Node<String> oldNode, Tree.Node<String> newNode) throws Exception {
        Method oldDesignMethod = OldTree.Node.class.getDeclaredMethod("design_");
        oldDesignMethod.setAccessible(true);
        OldTree.Pair<OldTree.PositionedNode<String>, OldTree.Extent> oldDesign =
                (OldTree.Pair<OldTree.PositionedNode<String>, OldTree.Extent>) oldDesignMethod.invoke(oldNode);
        Method newDesignMethod = Tree.Node.class.getDeclaredMethod("design_");
        newDesignMethod.setAccessible(true);
        Tree.Pair<Tree.PositionedNode<String>, Tree.Extent> newDesign =
                (Tree.Pair<Tree.PositionedNode<String>, Tree.Extent>) newDesignMethod.invoke(newNode);

        OldTree.Extent oldExtent = oldDesign.right();
        Tree.Extent newExtent = newDesign.right();

        assertEquals(oldNode.nodeId, newNode.nodeId, "Les IDs des nœuds doivent correspondre");

        double oldMinLeft = oldExtent.extentList.stream()
                .mapToDouble(OldTree.Pair::left)
                .min()
                .orElse(0.0);
        double oldMaxRight = oldExtent.extentList.stream()
                .mapToDouble(OldTree.Pair::right)
                .max()
                .orElse(0.0);

        double newLeft = newExtent.basePosition + newExtent.left;
        double newRight = newExtent.basePosition + newExtent.right;

        double tolerance = 0.0001;
        assertEquals(oldMinLeft, newLeft, tolerance, "La limite gauche diffère pour le nœud " + oldNode.nodeId);
        assertEquals(oldMaxRight, newRight, tolerance, "La limite droite diffère pour le nœud " + oldNode.nodeId);

        assertEquals(oldNode.children.size(), newNode.children.size(), "Le nombre d'enfants doit correspondre");

        for (int i = 0; i < oldNode.children.size(); i++) {
            compareExtents(oldNode.children.get(i), newNode.children.get(i));
        }
    }

    // Configuration 1 : Arbre symétrique
    @Test
    public void testSymmetricTree() throws Exception {
        OldTree oldTree = new OldTree(1);
        Tree newTree = new Tree(1);

        oldTree.createNode(2, 1, OldTree.NodeType.INNER, () -> {}, "Info2");
        oldTree.createNode(3, 1, OldTree.NodeType.INNER, () -> {}, "Info3");

        newTree.createNode(2, 1, Tree.NodeType.INNER, () -> {}, "Info2");
        newTree.createNode(3, 1, Tree.NodeType.INNER, () -> {}, "Info3");

        compareExtents(oldTree.root(), newTree.root());
    }

    // Configuration 2 : Arbre dégénéré (chaîne linéaire)
    @Test
    public void testDegenerateTree() throws Exception {
        OldTree oldTree = new OldTree(1);
        Tree newTree = new Tree(1);

        oldTree.createNode(2, 1, OldTree.NodeType.INNER, () -> {}, "Info2");
        oldTree.createNode(3, 2, OldTree.NodeType.INNER, () -> {}, "Info3");
        oldTree.createNode(4, 3, OldTree.NodeType.INNER, () -> {}, "Info4");

        newTree.createNode(2, 1, Tree.NodeType.INNER, () -> {}, "Info2");
        newTree.createNode(3, 2, Tree.NodeType.INNER, () -> {}, "Info3");
        newTree.createNode(4, 3, Tree.NodeType.INNER, () -> {}, "Info4");

        compareExtents(oldTree.root(), newTree.root());
    }

    @Test
    public void testMixedTypeTree() throws Exception {
        OldTree oldTree = new OldTree(1);
        Tree newTree = new Tree(1);

        oldTree.createNode(2, 1, OldTree.NodeType.SKIP, () -> {}, "Info2");
        oldTree.createNode(3, 1, OldTree.NodeType.FAIL, () -> {}, "Info3");
        oldTree.createNode(4, 3, OldTree.NodeType.SOLUTION, () -> {}, "Info4");

        newTree.createNode(2, 1, Tree.NodeType.SKIP, () -> {}, "Info2");
        newTree.createNode(3, 1, Tree.NodeType.FAIL, () -> {}, "Info3");
        newTree.createNode(4, 3, Tree.NodeType.SOLUTION, () -> {}, "Info4");

        compareExtents(oldTree.root(), newTree.root());
    }

    // Configuration 4 : Arbre vide (seulement la racine)
    @Test
    public void testRootOnlyTree() throws Exception {
        OldTree oldTree = new OldTree(1);
        Tree newTree = new Tree(1);
        compareExtents(oldTree.root(), newTree.root());
    }

    @Test
    public void testDeepTree() throws Exception {
        OldTree oldTree = new OldTree(1);
        Tree newTree = new Tree(1);

        int currentId = 1;
        int maxDepth = 20;
        Queue<Integer> queue = new LinkedList<>();
        queue.add(1);

        while (!queue.isEmpty() && maxDepth > 0) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                int parentId = queue.poll();
                int leftChildId = ++currentId;
                int rightChildId = ++currentId;

                oldTree.createNode(leftChildId, parentId, OldTree.NodeType.INNER, () -> {}, "Info" + leftChildId);
                oldTree.createNode(rightChildId, parentId, OldTree.NodeType.INNER, () -> {}, "Info" + rightChildId);

                newTree.createNode(leftChildId, parentId, Tree.NodeType.INNER, () -> {}, "Info" + leftChildId);
                newTree.createNode(rightChildId, parentId, Tree.NodeType.INNER, () -> {}, "Info" + rightChildId);

                queue.add(leftChildId);
                queue.add(rightChildId);
            }
            maxDepth--;
        }

        compareExtents(oldTree.root(), newTree.root());
    }
}