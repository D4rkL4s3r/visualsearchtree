package org.uclouvain.visualsearchtree.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CompareExtentImplementation {

    public static void main(String[] args) {
        // Différentes tailles pour l'extentList
        int[] sizes = {10, 100, 1000, 10000};
        // Nombre d'appels pour le benchmark
        int iterations = 1000000;

        for (int size : sizes) {
            // Création de la liste de paires pour simuler l'étendue
            List<Pair<Double, Double>> pairs = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                pairs.add(new Pair<>(i * 1.0, i * 1.0 + 1.0));
            }
            // Créez un ExtentO1 et un ExtentOn avec les mêmes données
            ExtentO1 extO1 = new ExtentO1(pairs);
            ExtentOn extOn = new ExtentOn(pairs);

            // Warm-up pour le JIT
            /*for (int i = 0; i < 100000; i++) {
                extO1 = extO1.move(1.0);
                extOn = extOn.move(1.0);
            }*/

            // Mesurer le temps moyen pour ExtentO1 (O(1))
            long startO1 = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                extO1 = extO1.move(1.0);
            }
            long endO1 = System.nanoTime();
            double avgO1 = (endO1 - startO1) / (double) iterations;

            // Mesurer le temps moyen pour ExtentOn (O(n))
            long startOn = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                extOn = extOn.move(1.0);
            }
            long endOn = System.nanoTime();
            double avgOn = (endOn - startOn) / (double) iterations;

            System.out.println("Taille de l'extentList: " + size
                    + " - Temps moyen move() O(1): " + avgO1 + " ns, O(n): " + avgOn + " ns");
        }
    }

    // Une classe simple pour représenter une paire
    static record Pair<L, R>(L left, R right) { }

    // Implémentation O(1) : La méthode move ne fait qu'ajouter au champ offset
    static class ExtentO1 {
        double offset;
        List<Pair<Double, Double>> extentList;
        static final double NODE_SPACING = 6.0;

        public ExtentO1(List<Pair<Double, Double>> extentList) {
            this.extentList = extentList;
            this.offset = 0.0;
        }

        public ExtentO1 move(double x) {
            ExtentO1 result = new ExtentO1(this.extentList);
            result.offset = this.offset + x;
            return result;
        }
    }

    // Implémentation O(n) : La méthode move recopie l'intégralité de la liste
    static class ExtentOn {
        double offset;
        List<Pair<Double, Double>> extentList;
        static final double NODE_SPACING = 6.0;

        public ExtentOn(List<Pair<Double, Double>> extentList) {
            // On fait une copie de la liste
            this.extentList = new ArrayList<>(extentList);
            this.offset = 0.0;
        }

        public ExtentOn move(double x) {
            List<Pair<Double, Double>> newList = new ArrayList<>();
            // On itère sur toute la liste pour appliquer le déplacement
            for (Pair<Double, Double> p : extentList) {
                newList.add(new Pair<>(p.left() + x, p.right() + x));
            }
            // Retourne un nouvel objet avec la liste mise à jour et offset réinitialisé
            return new ExtentOn(newList);
        }
    }
}
