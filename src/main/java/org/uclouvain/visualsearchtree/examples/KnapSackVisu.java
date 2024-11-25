package org.uclouvain.visualsearchtree.examples;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import org.uclouvain.visualsearchtree.branchandbound.*;
import org.uclouvain.visualsearchtree.tree.Tree;
import org.uclouvain.visualsearchtree.tree.TreeVisual;
import org.uclouvain.visualsearchtree.tree.Visualizer;

import java.util.HashMap;
import java.util.Map;

public class KnapSackVisu {

    private static boolean DEBUG = false;

    public static void main(String[] args) throws InterruptedException {

        int[] value = new int[]{1, 6, 18, 22, 28};
        int[] weight = new int[]{2, 3, 5, 6, 7};
        int capa = 11;
        int n = value.length;

        OpenNodes<NodeKnapsack> openNodes = new BestFirstOpenNodes<>();
        //OpenNodes<NodeKnapsack> openNodes = new DepthFirstOpenNodes<>();

        NodeKnapsack root = new NodeKnapsack(null,value,weight,0,capa,-1,false);
        openNodes.add(root);
        TreeVisual tv = new TreeVisual();
        Gson gson = new Gson();

        tv.setRealtimeNbNodeDrawer(20);
        tv.setRealtimeItv(300);
        Visualizer.show(tv);

        Thread t3 = new Thread(() -> BranchAndBound.minimize(new SolverListener() {
            @Override
            public void solution(int id, int pId) {
                if (DEBUG)
                    System.out.println("solution");
                synchronized (openNodes) {
                    NodeKnapsack tmp = (NodeKnapsack) openNodes.remove();
                    String info = "{\"cost\": "+id+", \"domain\": "+id+", \"other\": \""+ getNodeValue(tmp.weight)+"\"}";
                    TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());
                    tv.createNode(id,pId, Tree.NodeType.SOLUTION,() -> {
                        showBag(infoData,Tree.NodeType.SOLUTION);
                    }, info);
                }
            }
            @Override
            public void fail(int id, int pId) {
                if (DEBUG)
                    System.out.println("fail");
                synchronized (openNodes) {
                    NodeKnapsack tmp = (NodeKnapsack) openNodes.remove();
                    String info = "{\"cost\": "+id+", \"domain\": "+id+", \"other\": \""+ getNodeValue(tmp.weight)+"\"}";
                    TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());
                    tv.createNode(id,pId, Tree.NodeType.FAIL,() -> {
                        showBag(infoData,Tree.NodeType.FAIL);
                    }, info);
                }
            }
            @Override
            public void branch(int id, int pId, int nChilds) {
                if (DEBUG)
                    System.out.println("branch");
                synchronized (openNodes) {
                    NodeKnapsack tmp = (NodeKnapsack) openNodes.remove();
                    String info = "{\"cost\": "+id+", \"domain\": "+id+", \"other\": \""+ getNodeValue(tmp.weight)+"\"}";
                    TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());
                    tv.createNode(id,pId, Tree.NodeType.INNER,() -> {
                        showBag(infoData,Tree.NodeType.INNER);
                    }, info);
                }
            }
        }, openNodes, node -> {
            System.out.println("new best solution: " + -node.lowerBound());
        }));

        t3.start();
    }

    /**
     * Smple function to return the node value as string during the search
     * tab of "index" : value (0: q[0] | 1: q[1] |... n: q[n])
     * @param q: int tab
     * @return : String value
     */
    public static String getNodeValue(int[] q){
        StringBuilder value = new StringBuilder("{");
        for (int i = 0; i < q.length; i++) {
            value.append(i).append(":").append(q[i]);
            if (i != (q.length - 1))
                value.append(",");
        }
        value.append("}");
        return value.toString();
    }

    /**
     * Draw Rectangle for Chess Visualisation for Nqueens problem
     * @param isFixed boolean which indicate if a variable has its value fixed
     * @return Rectangle which represent a case
     */
    private static Rectangle createRectangleForVisualisation(boolean isFixed, Tree.NodeType type){
        Rectangle r = new Rectangle(50,50);
        Color c = (type == Tree.NodeType.FAIL)? Color.RED : (type == Tree.NodeType.SOLUTION)? Color.GREEN : Color.CORNFLOWERBLUE;
        r.setFill(isFixed ? c : Color.WHITE);
        r.setStrokeType(StrokeType.OUTSIDE);
        r.setStrokeWidth(.4);
        r.setStroke(Color.BLACK);
        return r;
    }

    /**
     * Draw a visualisation : Here a chess with fixed value of node is drawn
     * @param nodeInfoData info parse to gson object of the concerned node
     */
    public static void showBag(TreeVisual.NodeInfoData nodeInfoData, Tree.NodeType type) {
        // Extraire les informations du nœud
        String[] weights = nodeInfoData.other.replace("{", "").replace("}", "").split(",");
        Map<Integer, Integer> weightMap = new HashMap<>();
        for (String pair : weights) {
            String[] keyValue = pair.split(":");
            weightMap.put(Integer.parseInt(keyValue[0]), Integer.parseInt(keyValue[1]));
        }

        // Créer une fenêtre JavaFX
        Stage stage = new Stage();
        stage.setTitle("Visualisation du sac à dos - Node Type: " + type);

        // Créer un conteneur vertical
        GridPane grid = new GridPane();
        grid.setStyle("-fx-padding: 10; -fx-hgap: 5; -fx-vgap: 5; -fx-background-color: #f0f0f0;");

        // Ajouter un rectangle vertical avec des carrés proportionnels à la valeur
        int index = 0;
        for (Map.Entry<Integer, Integer> entry : weightMap.entrySet()) {
            int value = entry.getValue();
            int weight = entry.getKey();

            // Créer un rectangle pour représenter l'objet
            int rectHeight = Math.max(30, value * 10); // Proportional height, min 30px
            int rectWidth = 50;

            Rectangle rect = new Rectangle(rectWidth, rectHeight);
            rect.setStroke(Color.BLACK);
            rect.setStrokeType(StrokeType.INSIDE);

            // Colorer selon la sélection dans le sac
            if (value > 0) {
                rect.setFill(Color.LIGHTGREEN); // Objet inclus
            } else {
                rect.setFill(Color.LIGHTGRAY); // Objet non inclus
            }

            // Ajouter le texte avec la valeur
            javafx.scene.text.Text text = new javafx.scene.text.Text("Val: " + value);
            text.setFill(Color.BLACK);

            // Ajouter les éléments dans le conteneur vertical
            GridPane.setConstraints(rect, 0, index);
            GridPane.setConstraints(text, 1, index);

            grid.getChildren().addAll(rect, text);
            index++;
        }

        // Créer une scène et afficher
        Scene scene = new Scene(grid, 200, 600);
        stage.setScene(scene);
        stage.show();
    }


}
