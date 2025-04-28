package org.uclouvain.visualsearchtree.tree;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * <h1>Complete Visualization Object Class</h1>
 * <p>
 *     VisualTree is a Graphic Component that helps the user to visualize the search algorithm's tree
 *     and interact with it if needed. It allows the user to perform the following actions:
 * </p>
 * <ul>
 *     <li>
 *         <b>Show Tree</b>: [{@link org.uclouvain.visualsearchtree.tree.VisualTree#treeProfilerLauncher(Tree.Node, Stage)}]
 *         Show the search tree (in real time or not), either displayed alone or with all other features.
 *     </li>
 *     <li>
 *         <b>Show optimization graph</b>: Display the optimization graph when the search algorithm is about optimization.
 *     </li>
 *     <li>
 *         <b>Show Legend</b>: Display the count of solutions or failures among all nodes after the search.
 *     </li>
 *     <li>
 *         <b>Add bookmark</b>: Set a reference point on a search tree node for later use.
 *     </li>
 * </ul>
 */
public class VisualTree {

    private static Parent root;
    private static Stage  outputStage;
    private static Scene outputScene;
    private static TreeUIController treeController;
    private static StackPane treeroot;
    private static VBox legendbox;
    private static VBox chartUI;

    /**
     * <p>
     *     Render a new screen instance of {@link org.uclouvain.visualsearchtree.tree.VisualTree VisualTree}.
     * </p>
     * @param node the root node of the tree; if null, the tree will not be preconstructed.
     * @param primaryStage the primary stage.
     * @see #treeProfilerLauncher(TreeVisual)
     */
    public static void treeProfilerLauncher(Tree.Node<String> node, Stage primaryStage) {
        // Si le node passé est null, on utilise le constructeur par défaut qui ne construit pas d'arbre.
        TreeVisual instance = (node == null) ? new TreeVisual() : new TreeVisual(node);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TreeUI.fxml"));
                    root = fxmlLoader.load();
                    treeController = fxmlLoader.getController();
                    treeController.setInstance(instance);

                    Group treeGroup = instance.getGroup();
                    primaryStage.setAlwaysOnTop(true);
                    outputScene = new Scene(root, 500, 700);

                    outputStage = new Stage();
                    outputStage.initOwner(primaryStage);
                    outputStage.setScene(outputScene);
                    outputStage.show();

                    treeroot = (StackPane) outputScene.lookup("#treeroot");
                    treeroot.getChildren().add(treeGroup);

                    AnimationFactory.zoomOnSCroll(treeroot);

                    legendbox = (VBox) outputScene.lookup("#legendbox");
                    legendbox.getChildren().add(instance.generateLegendsStack());
                    treeController.init();

                    /** GRAPH **/
                    // Creating the chart
                    final LineChart<Number,Number> lineChart = instance.getTreeChart(true);
                    chartUI = (VBox) outputScene.lookup("#chartUI");
                    chartUI.getChildren().add(lineChart);

                    instance.addEventOnChart();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * <p>
     *     Render a new screen instance of {@link org.uclouvain.visualsearchtree.tree.VisualTree VisualTree}
     * </p>
     * @param instance a TreeVisual instance (can be built without a tree)
     */
    public static void treeProfilerLauncher(TreeVisual instance) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TreeUI.fxml"));
                try {
                    root = fxmlLoader.load();
                    treeController = fxmlLoader.getController();
                    treeController.setInstance(instance);

                    //outputScene = new Scene(root, 500, 700);
                    treeroot = instance.getTreeStackPane();

                    //Stage outputStage = new Stage();

                    outputStage.setScene(outputScene);
                    outputStage.show();

                    StackPane sp = (StackPane) outputScene.lookup("#treeroot");
                    sp.getChildren().add(treeroot);

                    AnimationFactory.zoomOnSCroll(sp);

                    legendbox.getChildren().clear();
                    legendbox = (VBox) outputScene.lookup("#legendbox");
                    legendbox.getChildren().add(instance.generateLegendsStack());

                    /** GRAPH **/
                    chartUI.getChildren().clear();
                    chartUI = (VBox) outputScene.lookup("#chartUI");
                    chartUI.getChildren().add(instance.getTreeChart(true));
                    instance.addEventOnChart();

                    treeController.init();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /** Met à jour le contenu de la fenêtre existante **/
    public static void updateProfiler(TreeVisual instance) throws FileNotFoundException {
        if (treeController == null) {
            throw new IllegalStateException("TreeUIController not initialized. Call treeProfilerLauncher first.");
        }
        treeroot = instance.getTreeStackPane();
        treeController.setInstance(instance);
        treeroot.getChildren().setAll(instance.getGroup());
        legendbox.getChildren().setAll(instance.generateLegendsStack());
        LineChart<Number, Number> newChart = instance.getTreeChart(true);
        chartUI.getChildren().setAll(newChart);
        instance.addEventOnChart();
        outputStage.toFront();
    }
}