package org.uclouvain.visualsearchtree.tree;

import java.util.*;

public class RandomTreeGenerator {

    private static final Random rand = new Random(1);
    // Profondeur maximale de l'arbre pour éviter une explosion
    private static final int MAX_DEPTH = 5;
    // Nombre maximum d'enfants possibles par nœud
    private static final int MAX_CHILDREN = 3;

    /**
     * Génère un arbre aléatoire à partir d'une profondeur initiale (0).
     */
    public static Tree.Node<String> randomTree() {
        return randomTree(0);
    }

    /**
     * Génère récursivement un arbre aléatoire.
     *
     * @param depth la profondeur actuelle (commence à 0)
     * @return un nœud racine du sous-arbre généré
     */
    public static Tree.Node<String> randomTree(long depth) {
        // Si la profondeur maximale est atteinte, on ne crée pas d'enfants.
        int nChildren = (depth < MAX_DEPTH) ? rand.nextInt(MAX_CHILDREN + 1) : 0;

        List<Tree.Node<String>> children = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Créer récursivement les enfants
        for (int i = 0; i < nChildren; i++) {
            children.add(randomTree(depth + 1));
            labels.add("Child " + i);
        }

        // Utilisation d'un constructeur de Tree.Node qui accepte
        // un label, une info, la liste des enfants, les étiquettes des arcs, et une action (ici, une action vide).
        return new Tree.Node<>("Node" + depth, "Info for Node" + depth, children, labels, () -> {});
    }

    /**
     * Génère récursivement un arbre aléatoire.
     *
     * @param depth la profondeur actuelle (commence à 0)
     * @return un nœud racine du sous-arbre généré
     */
    public static OldTree.Node<String> randomOldTree(long depth) {
        // Si la profondeur maximale est atteinte, on ne crée pas d'enfants.
        int nChildren = (depth < MAX_DEPTH) ? rand.nextInt(MAX_CHILDREN + 1) : 0;

        List<OldTree.Node<String>> children = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Créer récursivement les enfants
        for (int i = 0; i < nChildren; i++) {
            children.add(randomOldTree(depth + 1));
            labels.add("Child " + i);
        }

        // Utilisation d'un constructeur de Tree.Node qui accepte
        // un label, une info, la liste des enfants, les étiquettes des arcs, et une action (ici, une action vide).
        return new OldTree.Node<>("Node" + depth, "Info for Node" + depth, children, labels, () -> {});
    }

    /**
     * Méthode de test pour afficher l'arbre généré dans la console.
     */
    public static void main(String[] args) {
        Tree.Node<String> root = randomTree();
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