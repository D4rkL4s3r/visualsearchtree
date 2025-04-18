package org.uclouvain.visualsearchtree.tree;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.uclouvain.visualsearchtree.examples.NQueensPrune;
import org.uclouvain.visualsearchtree.examples.SolverListener;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TreeExtentComparisonTest {

    static class JsonNode {
        int id;
        Integer parentId; // Peut être null pour la racine
        String type; // "INNER", "FAIL", "SOLUTION"
        String info;
    }

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

    @Test
    public void testLargeTreeFromJson() throws Exception {
        // Charger le JSON depuis le classpath
        Gson gson = new Gson();
        Type nodeListType = new TypeToken<List<JsonNode>>(){}.getType();
        List<JsonNode> nodes;
        String jsonFilePath = "C:\\Users\\maxim\\Documents\\Ecole\\UCL\\Master2\\TFE\\myvisualsearchtree\\src\\test\\ressources\\large_tree.json"; // Ajustez le chemin
        try (FileReader reader = new FileReader(jsonFilePath)) {
            nodes = gson.fromJson(reader, nodeListType);
        }

        // Initialiser les arbres
        OldTree oldTree = new OldTree(1);
        Tree newTree = new Tree(1);

        // Construire les arbres à partir des nœuds du JSON
        for (JsonNode node : nodes) {
            if (node.parentId != null) {
                OldTree.NodeType oldNodeType = OldTree.NodeType.valueOf(node.type);
                Tree.NodeType newNodeType = Tree.NodeType.valueOf(node.type);
                oldTree.createNode(node.id, node.parentId, oldNodeType, () -> {}, node.info);
                newTree.createNode(node.id, node.parentId, newNodeType, () -> {}, node.info);
            }
        }

        // Comparer les Extents des arbres
        compareExtents(oldTree.root(), newTree.root());
    }

    @Test
    public void testWithProblems() throws Exception {
        NQueensPrune nqueensFour = new NQueensPrune(4);
        NQueensPrune nqueensSix = new NQueensPrune(6);
        TreeVisual tv = new TreeVisual();
        OldTreeVisual oldTv = new OldTreeVisual();
        Gson gson = new Gson();

        nqueensFour.dfs(new SolverListener() {
            @Override
            public void solution(int id, int pId) {
                String info = "{\"cost\": "+id+", \"domain\": "+id+", \"other\": \""+"\"}";
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());
                tv.createNode(id, pId, Tree.NodeType.SOLUTION, () -> {}, info);
                oldTv.createNode(id, pId, OldTree.NodeType.SOLUTION, () -> {}, info);
            }

            @Override
            public void fail(int id, int pId) {
                String info = "{\"cost\": "+id+", \"domain\": "+id+", \"other\": \""+"\"}";
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());
                tv.createNode(id, pId, Tree.NodeType.FAIL, () -> {}, info);
                oldTv.createNode(id, pId, OldTree.NodeType.FAIL, () -> {}, info);
            }

            @Override
            public void branch(int id, int pId, int nChilds) {
                String info = "{\"cost\": "+id+", \"domain\": "+id+", \"other\": \""+"\"}";
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());
                tv.createNode(id, pId, Tree.NodeType.INNER, () -> {}, info);
                oldTv.createNode(id, pId, OldTree.NodeType.INNER, () -> {}, info);
            }
        });

        Tree.Node<String> treeRoot= tv.getNode();
        OldTree.Node<String> oldTreeRoot = oldTv.getNode();

        compareExtents(oldTreeRoot, treeRoot);
    }
}