package org.uclouvain.visualsearchtree.tree;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BenchmarkJUnitTest {

    private static final int WARMUP_ITERATIONS =1;
    private static final int MEASURE_ITERATIONS = 5;

    @ParameterizedTest
    @CsvSource({
            "10, 0", // 2.047 nodes -> 2^(max_depth+1)-1
            "15, 0", // 65.535 nodes
            "20, 0", // 2.097.151 nodes
            //"22, 0", // 8.388.607 nodes
            "25, 0", // 67.108.863 nodes
            /*"30, 0", // 2.147.483.647 nodes
            "35, 0", // 68.719.476.735 nodes
            "40, 0", // 2.199.023.255.551 nodes
            "45, 0", // 70.368.744.177.663 nodes*/
    })
    public void benchmarkTreeDesign(long max_depth, long depth) throws IOException {
        String fileName = "benchmark_results_tree.txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            writer.println("Benchmarking Tree.design() with max_depth: " + max_depth + ", depth: " + depth);

            Tree.Node<String> treeRoot = TreeGenerator.generateTree(max_depth, depth);

            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                treeRoot.design();
            }

            long totalTime = 0;
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                long startTime = System.nanoTime();
                Object result = treeRoot.design();
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                totalTime += duration;
                writer.println("Iteration " + (i + 1) + ": " + duration + " ns");
            }
            long averageTime = totalTime / MEASURE_ITERATIONS;
            writer.println("Average time for Tree.design() at depth " + depth + ": " + averageTime + " ns");
            writer.println("------------------------------------------------------");
        }
    }

    @ParameterizedTest
    @CsvSource({
            "10, 0", // 2.047 nodes -> 2^(max_depth+1)-1
            "15, 0", // 65.535 nodes
            "20, 0", // 2.097.151 nodes
            //"22, 0", // 8.388.607 nodes
            "25, 0", // 67.108.863 nodes
            /*"30, 0", // 2.147.483.647 nodes
            "35, 0", // 68.719.476.735 nodes
            "40, 0", // 2.199.023.255.551 nodes
            "45, 0", // 70.368.744.177.663 nodes*/
    })
    public void benchmarkOldTreeDesign(long max_depth, long depth) throws IOException {
        String fileName = "benchmark_results_oldtree.txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            writer.println("Benchmarking OldTree.design() with max_depth: " + max_depth + ", depth: " + depth);

            OldTree.Node<String> oldTreeRoot = TreeGenerator.generateOldtree(max_depth, depth);

            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                oldTreeRoot.design();
            }

            long totalTime = 0;
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                long startTime = System.nanoTime();
                Object result = oldTreeRoot.design();
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                totalTime += duration;
                writer.println("Iteration " + (i + 1) + ": " + duration + " ns");
            }
            long averageTime = totalTime / MEASURE_ITERATIONS;
            writer.println("Average time for OldTree.design() at depth " + max_depth + ": " + averageTime + " ns");
            writer.println("------------------------------------------------------");
        }
    }
}
