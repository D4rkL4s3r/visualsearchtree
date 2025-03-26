package org.uclouvain.visualsearchtree.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeGenerator {

    private static final int MAX_DEPTH = 5;
    private static final int MAX_CHILDREN = 3;
    public int depth;

    /**
     * Génère récursivement un arbre balancé.
     *
     * @param depth la profondeur actuelle (commence à 0)
     * @return un nœud racine du sous-arbre généré
     */
    public static Tree.Node<String> generateBalancedTree(long max_depth, long depth) {
        int nChildren = (depth < max_depth) ? 2 : 0;
        List<Tree.Node<String>> children = new ArrayList<>();
        //List<String> labels = new ArrayList<>();

        // Créer récursivement les enfants
        for (int i = 0; i < nChildren; i++) {
            children.add(generateBalancedTree(max_depth, depth+1));
            //labels.add("Child " + i);
        }

        // Utilisation d'un constructeur de Tree.Node qui accepte
        // un label, une info, la liste des enfants, les étiquettes des arcs, et une action (ici, une action vide).
        return new Tree.Node<>("", "", children, null, () -> {});
    }

    public static Tree.Node<String> generateDegeneratedTree(long maxNodes, long currentNodes) {
        List<Tree.Node<String>> children = new ArrayList<>();

        // Si le nombre actuel de nœuds est inférieur au nombre maximum souhaité,
        // ajouter un seul enfant.
        if (currentNodes < maxNodes) {
            children.add(generateDegeneratedTree(maxNodes, currentNodes + 1));
        }

        // Utilisation d'un constructeur de Tree.Node qui accepte un label, une info,
        // la liste des enfants, les étiquettes des arcs (ici null) et une action (ici une action vide).
        return new Tree.Node<>("", "", children, null, () -> {});
    }


    /**
     * Génère récursivement un arbre aléatoire.
     *
     * @param depth la profondeur actuelle (commence à 0)
     * @return un nœud racine du sous-arbre généré
     */
    public static OldTree.Node<String> generateBalancedOldtree(long max_depth, long depth) {
        int nChildren = (depth < max_depth) ? 2 : 0;
        List<OldTree.Node<String>> children = new ArrayList<>();
        //List<String> labels = new ArrayList<>();

        // Créer récursivement les enfants
        for (int i = 0; i < nChildren; i++) {
            children.add(generateBalancedOldtree(max_depth, depth + 1));
            //labels.add("Child " + i);
        }

        // Utilisation d'un constructeur de Tree.Node qui accepte
        // un label, une info, la liste des enfants, les étiquettes des arcs, et une action (ici, une action vide).
        return new OldTree.Node<>("", "", children, null, () -> {});
    }

    public static OldTree.Node<String> generateDegeneratedOldTree(long maxNodes, long currentNodes) {
        List<OldTree.Node<String>> children = new ArrayList<>();

        // Si le nombre actuel de nœuds est inférieur au nombre maximum souhaité,
        // ajouter un seul enfant.
        if (currentNodes < maxNodes) {
            children.add(generateDegeneratedOldTree(maxNodes, currentNodes + 1));
        }

        // Utilisation d'un constructeur de Tree.Node qui accepte un label, une info,
        // la liste des enfants, les étiquettes des arcs (ici null) et une action (ici une action vide).
        return new OldTree.Node<>("", "", children, null, () -> {});
    }

    /**
     * Méthode de test pour afficher l'arbre généré dans la console.
     */
    public static void main(String[] args) {
        Tree.Node<String> root = generateBalancedTree(10, 2);
        printTree(root, 0);
    }

    /**
     * Affiche l'arbre dans la console avec une indentation correspondant à la profondeur.
     *
     * @param node le nœud à afficher
     * @param indent niveau d'indentation (profondeur)
     */
    private static void printTree(Tree.Node<String> node, int indent) {
        // Afficher l'indentation
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
        // Afficher le label du nœud
        System.out.println(node.label);
        // Afficher récursivement les enfants
        for (Tree.Node<String> child : node.children) {
            printTree(child, indent + 1);
        }
    }
}
