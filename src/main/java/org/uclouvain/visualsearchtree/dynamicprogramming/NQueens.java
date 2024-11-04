package org.uclouvain.visualsearchtree.dynamicprogramming;

import java.util.ArrayList;
import java.util.List;

public class NQueens extends Model<NqueensState> {

    NqueensState root;

    public NQueens() {
        // Initialisation de l'état racine avec un échiquier de taille 4 et sans reines placées
        this.root = new NqueensState(4, new int[4][4]);
    }

    @Override
    boolean isBaseCase(NqueensState state) {
        // Le cas de base est atteint quand toutes les reines sont placées sans conflit
        return state.getRow() == state.getSize() && state.isValid();
    }

    @Override
    double getBaseCaseValue(NqueensState state) {
        // Retourne 1.0 pour une solution valide
        return state.isValid() ? 1.0 : 0.0;
    }

    @Override
    NqueensState getRootState() {
        // Retourne l'état racine (échiquier vide)
        return root;
    }

    @Override
    List<Transition<NqueensState>> getTransitions(NqueensState state) {
        List<Transition<NqueensState>> transitions = new ArrayList<>();

        int currentRow = state.getRow();
        int size = state.getSize();

        if (currentRow >= size) return transitions;

        // Essaye de placer une reine dans chaque colonne de la ligne courante
        for (int col = 0; col < size; col++) {
            if (state.isSafe(currentRow, col)) {
                // Crée un nouvel état en plaçant une reine dans la position (currentRow, col)
                NqueensState newState = state.placeQueen(currentRow, col);
                transitions.add(new Transition<NqueensState>(newState, 1, 1.0));  // coût de transition arbitraire
            }
        }
        return transitions;
    }

    @Override
    boolean isMaximization() {
        // Ce n'est pas un problème de maximisation
        return false;
    }
}
