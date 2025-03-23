package org.uclouvain.visualsearchtree.tree;

public class HeapSizeChecker {
    public static void main(String[] args) {
        long heapSize = Runtime.getRuntime().totalMemory(); // Taille actuelle de la heap
        long maxHeapSize = Runtime.getRuntime().maxMemory(); // Taille max de la heap
        long freeHeapSize = Runtime.getRuntime().freeMemory(); // Mémoire libre

        System.out.println("Taille actuelle de la heap: " + (heapSize / 1024 / 1024) + " Mo");
        System.out.println("Taille max de la heap: " + (maxHeapSize / 1024 / 1024) + " Mo");
        System.out.println("Mémoire libre dans la heap: " + (freeHeapSize / 1024 / 1024) + " Mo");
    }
}