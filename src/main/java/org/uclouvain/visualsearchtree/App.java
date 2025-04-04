package org.uclouvain.visualsearchtree;

import javafx.application.Application;
import javafx.stage.Stage;
import org.uclouvain.visualsearchtree.tree.Tree;
import org.uclouvain.visualsearchtree.tree.TreeGenerator;
import org.uclouvain.visualsearchtree.tree.TreeVisual;
import org.uclouvain.visualsearchtree.tree.VisualTree;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crée l'instance de TreeVisual vide
        TreeVisual treeVisual = new TreeVisual();

        // Définir le nombre de nœuds pour l'arbre dégénéré
        int n = 1000;  // par exemple, 10 niveaux
        int rootId = 0;

        // Créer la racine
        treeVisual.createNode(rootId, -1, Tree.NodeType.INNER, () -> {
            System.out.println("Action sur la racine");
        }, "root");

        // Pour chaque niveau, attacher le nouveau nœud comme enfant unique du précédent
        int parentId = rootId;
        for (int i = 1; i < n; i++) {
            int currentId = i;
            treeVisual.createNode(currentId, parentId, Tree.NodeType.INNER, () -> {
                System.out.println("Action sur le nœud " + currentId);
            }, "node " + currentId);
            parentId = currentId;
        }

        // Lancer l'interface graphique avec l'arbre généré
        VisualTree.treeProfilerLauncher(treeVisual);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
