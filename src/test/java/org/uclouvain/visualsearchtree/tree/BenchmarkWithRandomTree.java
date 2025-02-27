package org.uclouvain.visualsearchtree.tree;

import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
@Warmup(iterations = 5)      // Phase d'échauffement de 5 itérations
@Measurement(iterations = 10) // Mesure sur 10 itérations
@Fork(1)                     // Exécution dans un fork unique (modifiable)
public class BenchmarkWithRandomTree {

    // On définit ici la profondeur avec toutes les valeurs souhaitées.
    @Param({
            "100",
            "1000",
            "10000",
            "100000",
            "1000000",
            "10000000",
            "100000000",
            "1000000000",
            "10000000000",
            "100000000000",
            "1000000000000"
    })
    public long depth;

    private Tree.Node<String> treeRoot;
    private OldTree.Node<String> oldTreeRoot;

    @Setup
    public void setup() {
        // On suppose que RandomTreeGenerator.randomTree et randomOldTree
        // ont été adaptés pour accepter une valeur long.
        treeRoot = RandomTreeGenerator.randomTree(depth);
        oldTreeRoot = RandomTreeGenerator.randomOldTree(depth);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Object benchmarkTreeDesign() {
        return treeRoot.design();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Object benchmarkOldTreeDesign() {
        return oldTreeRoot.design();
    }
}
