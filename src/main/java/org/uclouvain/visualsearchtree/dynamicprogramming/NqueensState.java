package org.uclouvain.visualsearchtree.dynamicprogramming;

import java.util.Arrays;
import java.util.Objects;

public class NqueensState extends State {
    int numberOfQueens;
    int[][] queens;

    public NqueensState(int numberOfQueens, int[][] queens) {
        this.numberOfQueens = numberOfQueens;
        // Copie de l'échiquier pour éviter la mutation de l'état initial
        this.queens = Arrays.stream(queens)
                .map(int[]::clone)
                .toArray(int[][]::new);
    }

    public int getRow() {
        // Renvoie le nombre de reines déjà placées (chaque reine est placée sur une ligne unique)
        return (int) Arrays.stream(queens).flatMapToInt(Arrays::stream).filter(i -> i == 1).count();
    }

    public int getSize() {
        return numberOfQueens;
    }

    public boolean isValid() {
        // Vérifie que chaque reine est placée sans attaque mutuelle
        for (int row = 0; row < numberOfQueens; row++) {
            for (int col = 0; col < numberOfQueens; col++) {
                if (queens[row][col] == 1 && !isSafe(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isSafe(int row, int col) {
        // Vérifie s'il y a des conflits sur la colonne, les diagonales, et la ligne
        for (int i = 0; i < row; i++) {
            // Vérifie la colonne
            if (queens[i][col] == 1) return false;

            // Vérifie la diagonale supérieure gauche
            if (col - (row - i) >= 0 && queens[i][col - (row - i)] == 1) return false;

            // Vérifie la diagonale supérieure droite
            if (col + (row - i) < numberOfQueens && queens[i][col + (row - i)] == 1) return false;
        }
        return true;
    }

    public NqueensState placeQueen(int row, int col) {
        // Crée un nouvel état avec une reine placée en (row, col)
        int[][] newQueens = Arrays.stream(queens)
                .map(int[]::clone)
                .toArray(int[][]::new);
        newQueens[row][col] = 1;
        return new NqueensState(numberOfQueens, newQueens);
    }

    @Override
    int hash() {
        return Objects.hash(numberOfQueens, Arrays.deepHashCode(queens));
    }

    @Override
    boolean isEqual(State state) {
        if (!(state instanceof NqueensState)) {
            return false;
        }
        NqueensState other = (NqueensState) state;
        return numberOfQueens == other.numberOfQueens && Arrays.deepEquals(queens, other.queens);
    }
}
