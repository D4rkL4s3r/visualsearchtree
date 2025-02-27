package org.uclouvain.visualsearchtree.tree;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BenchmarkJUnitTest {

    // Nombre d'itérations pour le warm-up et la mesure
    private static final int WARMUP_ITERATIONS = 50;
    private static final int MEASURE_ITERATIONS = 1000;

    /**
     * Benchmark pour Tree.design().
     * Les résultats sont écrits dans le fichier "benchmark_results_tree.txt".
     */
    @ParameterizedTest
    @ValueSource(longs = {
            100L,
            1000L,
            10000L,
            100000L,
            1000000L,
            10000000L,
            100000000L,
            1000000000L,
            10000000000L,
            100000000000L,
            1000000000000L,
            10000000000000L,
            100000000000000L,
            1000000000000000L,
            100000000000000000L,
            1000000000000000000L
    })
    public void benchmarkTreeDesign(long depth) throws IOException {
        String fileName = "benchmark_results_tree.txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            writer.println("Benchmarking Tree.design() with depth: " + depth);

            // Création de l'arbre à tester
            Tree.Node<String> treeRoot = RandomTreeGenerator.randomTree(depth);

            // Phase de warm-up
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                treeRoot.design();
            }

            // Phase de mesure
            long totalTime = 0;
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                long startTime = System.nanoTime();
                Object result = treeRoot.design();
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                totalTime += duration;
                writer.println("Iteration " + (i + 1) + ": " + duration + " ns, result: " + result);
            }
            long averageTime = totalTime / MEASURE_ITERATIONS;
            writer.println("Average time for Tree.design() at depth " + depth + ": " + averageTime + " ns");
            writer.println("------------------------------------------------------");
        }
    }

    /**
     * Benchmark pour OldTree.design().
     * Les résultats sont écrits dans le fichier "benchmark_results_oldtree.txt".
     */
    @ParameterizedTest
    @ValueSource(longs = {
            100L,
            1000L,
            10000L,
            100000L,
            1000000L,
            10000000L,
            100000000L,
            1000000000L,
            10000000000L,
            100000000000L,
            1000000000000L
    })
    public void benchmarkOldTreeDesign(long depth) throws IOException {
        String fileName = "benchmark_results_oldtree.txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            writer.println("Benchmarking OldTree.design() with depth: " + depth);

            // Création de l'arbre (OldTree) à tester
            OldTree.Node<String> oldTreeRoot = RandomTreeGenerator.randomOldTree(depth);

            // Phase de warm-up
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                oldTreeRoot.design();
            }

            // Phase de mesure
            long totalTime = 0;
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                long startTime = System.nanoTime();
                Object result = oldTreeRoot.design();
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                totalTime += duration;
                writer.println("Iteration " + (i + 1) + ": " + duration + " ns, result: " + result);
            }
            long averageTime = totalTime / MEASURE_ITERATIONS;
            writer.println("Average time for OldTree.design() at depth " + depth + ": " + averageTime + " ns");
            writer.println("------------------------------------------------------");
        }
    }
}
