package org.uclouvain.visualsearchtree.tree;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkExtentMoveTest {

    public static void main(String[] args) {
        // Différentes tailles pour l'extentList
        int[] sizes = {10, 100, 1000, 10000};
        int iterations = 1000000;

        for (int size : sizes) {
            List<Tree.Pair<Double, Double>> pairs = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                pairs.add(new Tree.Pair<>(i * 1.0, i * 1.0 + 1.0));
            }
            Tree.Extent ext = new Tree.Extent(pairs);

            //warm up
            /*for (int i = 0; i < 100000; i++) {
                ext = ext.move(1.0);
            }*/

            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                ext = ext.move(1.0);
            }
            long end = System.nanoTime();

            double totalTimeNs = (end - start);
            double avgTimePerCall = totalTimeNs / iterations;

            System.out.println("Taille de l'extentList: " + size
                    + " - Temps moyen par appel move(): " + avgTimePerCall + " ns");
        }
    }
}


