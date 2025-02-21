package org.uclouvain.visualsearchtree.tree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ExtentComplexityTest {

    @Test
    public void testMoveComplexity() {
        // Différentes tailles de extentList à tester
        int[] sizes = {10, 100, 1000, 10000};
        // Nombre d'appels pour le benchmark
        int iterations = 1_000_000;

        List<Double> avgTimes = new ArrayList<>();

        for (int size : sizes) {
            // Crée une liste de paires pour simuler l'étendue
            List<Tree.Pair<Double, Double>> pairs = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                // On crée des paires simples, par exemple [i, i+1]
                pairs.add(new Tree.Pair<>(i * 1.0, i * 1.0 + 1.0));
            }
            Tree.Extent ext = new Tree.Extent(pairs);

            // Warm-up pour activer le JIT
            for (int i = 0; i < 100_000; i++) {
                ext = ext.move(1.0);
            }

            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                ext = ext.move(1.0);
            }
            long end = System.nanoTime();

            double totalTimeNs = (end - start);
            double avgTime = totalTimeNs / iterations;
            avgTimes.add(avgTime);
            System.out.println("Taille de l'extentList: " + size
                    + " - Temps moyen par appel move(): " + avgTime + " ns");
        }

        // On vérifie que le temps moyen par appel ne varie pas beaucoup
        // Par exemple, on s'attend à ce que le ratio (max/min) soit inférieur à 2
        double minTime = avgTimes.stream().min(Double::compareTo).orElse(0.0);
        double maxTime = avgTimes.stream().max(Double::compareTo).orElse(0.0);
        System.out.println("Temps moyen min: " + minTime + " ns, max: " + maxTime + " ns");
        assertTrue(maxTime / minTime < 2.0,
                "La méthode move() doit être en O(1) : le temps moyen ne doit pas varier significativement.");
    }
}
