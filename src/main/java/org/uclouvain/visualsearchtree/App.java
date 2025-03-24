package org.uclouvain.visualsearchtree;

import javafx.application.Application;
import javafx.stage.Stage;
import org.uclouvain.visualsearchtree.tree.TreeVisual;
import org.uclouvain.visualsearchtree.tree.VisualTree;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Ici, on crée une instance de VisualTree sans arbre pré-construit
        TreeVisual treeVisual = new TreeVisual();

        // Lancement de l'interface avec l'instance vide
        VisualTree.treeProfilerLauncher(treeVisual);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
