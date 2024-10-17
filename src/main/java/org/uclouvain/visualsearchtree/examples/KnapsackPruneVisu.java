package org.uclouvain.visualsearchtree.examples;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import org.uclouvain.visualsearchtree.tree.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Example that illustrates how to solve the Knapsack problem
 * with a chronological backtracking algorithm and visualisation.
 */
public class KnapsackPruneVisu {

    public static void main(String[] args) throws Exception {

        int[] weights = {2, 3, 4, 5}; // Example weights
        int[] values = {3, 4, 5, 6};  // Example values
        int capacity = 7;  // Example capacity
        /*int capacity = 1;
        int[] values = {1};
        int[] weights = {1};*/

        KnapsackSolver knapsackSolver = new KnapsackSolver(weights, values, capacity);
        Gson gson = new Gson();
        Tree t = new Tree(-1);

        TreeVisual tv = new TreeVisual(() -> knapsackSolver.solve("dfs", new SolverListener() {
            @Override
            public void solution(int id, int pId) {
                int totalWeight = knapsackSolver.getTotalWeight(knapsackSolver.getItemsSelected());
                int totalValue = knapsackSolver.getTotalValue(knapsackSolver.getItemsSelected());
                String info = "{\"value\": "+totalValue+", \"weight\": "+totalWeight+", \"domain\": "+id+", \"other\": \""+ getNodeValue(knapsackSolver.getItemsSelected()) +"\"}";
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());

                // S'assurer que showKnapsack est appelé dans le bon thread
                Platform.runLater(() -> t.createNode(id, pId, Tree.NodeType.SOLUTION, () -> {
                        formatIntList(knapsackSolver.getItemsSelected());
                        System.out.println(info);
                        showKnapsack(infoData, Tree.NodeType.SOLUTION);
                    },
                        info)
                );
            }

            @Override
            public void fail(int id, int pId) {
                int totalWeight = knapsackSolver.getTotalWeight(knapsackSolver.getItemsSelected());
                int totalValue = knapsackSolver.getTotalValue(knapsackSolver.getItemsSelected());
                String info = "{\"value\": "+totalValue+", \"weight\": "+totalWeight+", \"domain\": "+id+", \"other\": \""+ getNodeValue(knapsackSolver.getItemsSelected()) +"\"}";
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());

                // Utiliser Platform.runLater pour garantir l'exécution dans le bon thread
                Platform.runLater(() -> t.createNode(id, pId, Tree.NodeType.FAIL, () -> {
                        formatIntList(knapsackSolver.getItemsSelected());
                        System.out.println(info);
                        showKnapsack(infoData, Tree.NodeType.FAIL);
                    },
                        info)
                );
            }

            @Override
            public void branch(int id, int pId, int nChilds) {
                int totalWeight = knapsackSolver.getTotalWeight(knapsackSolver.getItemsSelected());
                int totalValue = knapsackSolver.getTotalValue(knapsackSolver.getItemsSelected());
                String info = "{\"value\": "+totalValue+", \"weight\": "+totalWeight+", \"domain\": "+id+", \"other\": \""+ getNodeValue(knapsackSolver.getItemsSelected()) +"\"}";
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());

                // S'assurer que l'UI est manipulée dans le bon thread
                Platform.runLater(() -> t.createNode(id, pId, Tree.NodeType.INNER, () -> {
                        formatIntList(knapsackSolver.getItemsSelected());
                        System.out.println(info);
                        showKnapsack(infoData, Tree.NodeType.INNER);
                    },
                    info)
                );
            }
        }), t, false);

        // Lancer l'interface graphique avec TreeVisual
        Platform.runLater(() -> {
            try {
                Visualizer.show(tv);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String formatIntList(int[] itemsSelected) {
        StringBuilder formattedString = new StringBuilder("{");

        for (int i = 0; i < itemsSelected.length; i++) {
            formattedString.append(i).append(":").append(itemsSelected[i]);
            if (i != (itemsSelected.length - 1)) {
                formattedString.append(","); // Ajouter une virgule entre les éléments
            }
        }

        formattedString.append("}"); // Fermer la chaîne avec une accolade
        return formattedString.toString(); // Retourner la chaîne formatée
    }


    /**
     * Function to return the node value as string during the search
     * for the Knapsack problem (items selected).
     * @param itemsSelected: array of items (0 or 1 if selected)
     * @return : String value
     */
    public static String getNodeValue(int[] itemsSelected){
        StringBuilder value = new StringBuilder("{");
        for (int i = 0; i < itemsSelected.length; i++) {
            value.append(i).append(":").append(itemsSelected[i]);
            if (i != (itemsSelected.length - 1))
                value.append(",");
        }
        value.append("}");
        return value.toString();
    }

    /**
     * Draw Rectangle for Knapsack Visualisation.
     * In this case, visualize selected items.
     * @param isSelected boolean to indicate if an item is selected in the knapsack.
     * @return Rectangle which represents an item.
     */
    private static Rectangle createRectangleForVisualisation(boolean isSelected, Tree.NodeType type){
        Rectangle r = new Rectangle(50,50);
        Color c = (type == Tree.NodeType.FAIL)? Color.RED : (type == Tree.NodeType.SOLUTION)? Color.GREEN : Color.CORNFLOWERBLUE;
        r.setFill(isSelected ? c : Color.WHITE);
        r.setStrokeType(StrokeType.OUTSIDE);
        r.setStrokeWidth(.4);
        r.setStroke(Color.BLACK);
        return r;
    }

    /**
     * Show a visualization: A grid that represents the Knapsack problem
     * where each item is either selected or not.
     * @param nodeInfoData info parse to Gson object of the concerned node
     */
    public static void showKnapsack(TreeVisual.NodeInfoData nodeInfoData, Tree.NodeType type) {
        // Convertir les données en Map
        Map<Integer, Integer> items = new Gson().fromJson(nodeInfoData.other, new TypeToken<HashMap<Integer, Integer>>() {}.getType());

        // Vérifiez la taille des items pour éviter les problèmes d'index
        int n = items.size(); // Utiliser la taille de la carte directement

        // Créer la grille pour l'affichage
        GridPane grid = new GridPane();
        Scene gridScene = new Scene(grid, n * 50 + n, 100); // Hauteur de 100 pour une seule ligne.
        Stage knapsackWindow = new Stage();

        knapsackWindow.setTitle("Knapsack Visualisation");
        knapsackWindow.setScene(gridScene);

        // Boucle sur les éléments de la carte
        for (int i = 0; i < n; i++) {
            // Vérifiez que l'élément existe dans la carte
            if (items.containsKey(i)) {
                grid.add(createRectangleForVisualisation(items.get(i) == 1, type), i, 0);
            } else {
                System.out.println("Aucun élément trouvé pour la clé : " + i);
                // Ajoutez éventuellement un rectangle pour représenter l'absence d'élément
                grid.add(createRectangleForVisualisation(false, type), i, 0); // Par exemple, un rectangle vide
            }
        }
        knapsackWindow.show();
    }


}
