package org.uclouvain.visualsearchtree.tree;

import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@Fork(1)
public class Benchmark {

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
        treeRoot = RandomTreeGenerator.randomTree(depth);
        oldTreeRoot = RandomTreeGenerator.randomOldTree(depth);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Object benchmarkTreeDesign() {
        return treeRoot.design();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Object benchmarkOldTreeDesign() {
        return treeRoot.design();
    }
}
