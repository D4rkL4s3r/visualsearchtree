package org.uclouvain.visualsearchtree.examples;

import org.uclouvain.visualsearchtree.Tree;

import java.util.ArrayList;
import java.util.List;

public class NQueens {

    public static Tree.Node<String> nQueensTree(int n) {
        return buildTree(new ArrayList<>(), n);
    }

    private static Tree.Node<String> buildTree(List<Integer> queens, int n) {
        if (queens.size() == n) {
            // Si toutes les reines sont placées -> retourner un noeud sans enfants
            return new Tree.Node<>(queensToString(queens), List.of(), List.of(), null);
        }

        List<Tree.Node<String>> children = new ArrayList<>();
        for (int col = 0; col < n; col++) {
            if (isValidMove(queens, col)) {
                List<Integer> newQueens = new ArrayList<>(queens);
                newQueens.add(col);
                children.add(buildTree(newQueens, n));
            }
        }

        return new Tree.Node<>(queensToString(queens), children, List.of(), null);
    }

    // Méthode pour convertir une liste de positions en chaîne de caractères
    private static String queensToString(List<Integer> queens) {
        if (queens.isEmpty()) {
            return "Root";  // Pour le noeud racine, qui n'a pas encore de reine placée
        }
        return queens.toString();  // Convertir la liste des positions en chaîne
    }

    // Vérifie si le placement d'une reine à la colonne "col" est valide
    private static boolean isValidMove(List<Integer> queens, int col) {
        int row = queens.size();
        for (int i = 0; i < row; i++) {
            int placedCol = queens.get(i);
            if (placedCol == col || Math.abs(placedCol - col) == row - i) {
                return false;  // Si dans la même colonne ou sur une diagonale
            }
        }
        return true;
    }
}
