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
        // Lancer l'interface graphique avec l'arbre généré
        VisualTree.treeProfilerLauncher(null, primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
