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
            "5, 0", // 63 nodes -> 2^(max_depth+1)-1
            "6, 0",
            "7, 0",
            "8, 0",
            "9, 0",
            "10, 0", // 2.047 nodes
            "11, 0",
            "12, 0",
            "13, 0",
            "14, 0",
            "15, 0", // 65.535 nodes
            "16, 0",
            "17, 0",
            "18, 0",
            "19, 0",
            "20, 0", // 2.097.151 nodes
            "21, 0",
            "22, 0",
            "23, 0",
            "24, 0",
            "25, 0", // 67.108.863 nodes
    })
    public void benchmarkBalancedTreeDesign(long max_depth, long depth) throws IOException {
        String fileName = "benchmark_results_balanced_tree.txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            writer.println("Benchmarking BalancedTree.design() with max_depth: " + max_depth + ", depth: " + depth);

            Tree.Node<String> treeRoot = TreeGenerator.generateBalancedTree(max_depth, depth);

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
            writer.println("Average time for BalancedTree.design() at depth " + depth + ": " + averageTime + " ns");
            writer.println("------------------------------------------------------");
        }
    }

    @ParameterizedTest
    @CsvSource({
           "5000, 0", //nodes
           "10000, 0",
           "15000, 0",
           "20000, 0",
           "25000, 0",
           "30000, 0",
           "35000, 0",
           "40000, 0",
           "45000, 0",
           "50000, 0",
           "55000, 0",
           "60000, 0",
           "65000, 0",
           "70000, 0",
           "75000, 0",
           "80000, 0",
           "85000, 0",
           "90000, 0",
           "95000, 0",
           "100000, 0"
    })
    public void benchmarkDegeneratedTreeDesign(long max_nodes, long node) throws IOException {
        String fileName = "benchmark_results_degenerated_tree.txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            writer.println("Benchmarking DegeneratedTree.design() with max_depth: " + max_nodes + ", depth: " + node);

            Tree.Node<String> treeRoot = TreeGenerator.generateDegeneratedTree(max_nodes, node);

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
            writer.println("Average time for DegeneratedTree.design() at max_nodes " + max_nodes + ": " + averageTime + " ns");
            writer.println("------------------------------------------------------");
        }
    }

    @ParameterizedTest
    @CsvSource({
            "5, 0", // 63 nodes -> 2^(max_depth+1)-1
            "6, 0",
            "7, 0",
            "8, 0",
            "9, 0",
            "10, 0", // 2.047 nodes
            "11, 0",
            "12, 0",
            "13, 0",
            "14, 0",
            "15, 0", // 65.535 nodes
            "16, 0",
            "17, 0",
            "18, 0",
            "19, 0",
            "20, 0", // 2.097.151 nodes
            "21, 0",
            "22, 0",
            "23, 0",
            "24, 0",
            "25, 0", // 67.108.863 nodes
    })
    public void benchmarkBalancedOldTreeDesign(long max_depth, long depth) throws IOException {
        String fileName = "benchmark_results_balanced_oldtree.txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            writer.println("Benchmarking BalancedOldTree.design() with max_depth: " + max_depth + ", depth: " + depth);

            OldTree.Node<String> oldTreeRoot = TreeGenerator.generateBalancedOldtree(max_depth, depth);

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
            writer.println("Average time for BalancedOldTree.design() at depth " + max_depth + ": " + averageTime + " ns");
            writer.println("------------------------------------------------------");
        }
    }

    @ParameterizedTest
    @CsvSource({
            "5000, 0", //nodes
            "10000, 0",
            "15000, 0",
            "20000, 0",
            "25000, 0",
            "30000, 0",
            "35000, 0",
            "40000, 0",
            "45000, 0",
            "50000, 0",
            "55000, 0",
            "60000, 0",
            "65000, 0",
            "70000, 0",
            "75000, 0",
            "80000, 0",
            "85000, 0",
            "90000, 0",
            "95000, 0",
            "100000, 0"
    })
    public void benchmarkDegeneratedOldTreeDesign(long max_nodes, long node) throws IOException {
        String fileName = "benchmark_results_degenerated_oldtree.txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            writer.println("Benchmarking DegeneratedOldTree.design() with max_nodes: " + max_nodes + ", depth: " + node);

            OldTree.Node<String> oldTreeRoot = TreeGenerator.generateDegeneratedOldTree(max_nodes, node);

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
            writer.println("Average time for DegeneratedOldTree.design() at node " + max_nodes + ": " + averageTime + " ns");
            writer.println("------------------------------------------------------");
        }
    }
}
