package org.uclouvain.visualsearchtree.branchandbound;


import org.uclouvain.visualsearchtree.util.InputReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the Knapsack solved using the {@link BranchAndBound}
 */
public class BranchAndBoundKnapsack {

    private static boolean DEBUG = false;
    public static void main(String[] args) {

        int[] value = new int[]{1, 6, 18, 22, 28};
        int[] weight = new int[]{2, 3, 5, 6, 7};
        int capa = 11;
        int n = value.length;


        String instance = "data/knapsack/knapsackA";
        InputReader reader = new InputReader(instance);
        n = reader.getInt();
        capa = reader.getInt();
        value = new int[n];
        weight = new int[n];
        for (int i = 0; i < n; i++) {
            value[i] = reader.getInt();
            weight[i] = reader.getInt();
        }
        // sort decreasing according to value/weight ratio

        sortValueOverWeight(value,weight);
        for (int i = 0; i < n-1; i++) {
            assert ((double) value[i]/weight[i] > (double) value[i-1]/weight[i+1]);
        }




        OpenNodes<NodeKnapsack> openNodes = new BestFirstOpenNodes<>();
        //OpenNodes<NodeKnapsack> openNodes = new DepthFirstOpenNodes<>();

        NodeKnapsack root = new NodeKnapsack(null,value,weight,0,capa,-1,false);
        openNodes.add(root);

        BranchAndBound.minimize(null, openNodes,node -> {
            if (DEBUG)
            System.out.println("new best solution: "+- node.lowerBound());
        });
    }

    private static void sortValueOverWeight(int [] value, int [] weight) {
        int n = value.length;
        double [][] item = new double[n][2];
        for (int i = 0; i < n; i++) {
            item[i][0] = value[i];
            item[i][1] = weight[i];
        }
        Arrays.sort(item,(i1,i2)-> (i1[0]/i1[1] > i2[0]/i2[1]) ? -1 : 1 );
        for (int i = 0; i < n; i++) {
            value[i] = (int) item[i][0];
            weight[i] = (int) item[i][1];
        }
    }
}


