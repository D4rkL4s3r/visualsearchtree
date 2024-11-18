package org.uclouvain.visualsearchtree.branchandbound;

public class BranchAndBoundNQueens {

    private int n = 4;

    public static void main(String[] args) {
        OpenNodes<NQueensNode> openNodes = new BestFirstOpenNodes<>();
        //OpenNodes<NQueensNode> openNodes = new DepthFirstOpenNodes<>();

        NQueensNode root = new NQueensNode(8);
        openNodes.add(root);

        BranchAndBound.minimize(null, openNodes, nQueensNodeNode -> {
            System.out.println("new best solution: " + nQueensNodeNode.lowerBound());
            System.out.println(nQueensNodeNode.toString());;
        });
    }
}
