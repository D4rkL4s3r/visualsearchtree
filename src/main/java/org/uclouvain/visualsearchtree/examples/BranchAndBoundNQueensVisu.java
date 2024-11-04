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
import org.uclouvain.visualsearchtree.branchandbound.*;
import org.uclouvain.visualsearchtree.tree.Tree;
import org.uclouvain.visualsearchtree.tree.TreeVisual;
import org.uclouvain.visualsearchtree.tree.Visualizer;

import java.util.HashMap;
import java.util.Map;

public class BranchAndBoundNQueensVisu {

    public static void main(String[] args) throws Exception {
        Gson gson = new Gson();
        Tree t = new Tree(-1);

        OpenNodes<NQueensNode> openNodes = new BestFirstOpenNodes<>();
        //OpenNodes<NQueensNode> openNodes = new DepthFirstOpenNodes<>();

        NQueensNode root = new NQueensNode(8);
        openNodes.add(root);

        System.out.println("Start tv");

        TreeVisual tv = new TreeVisual(() -> BranchAndBound.minimize(new SolverListener() {
            @Override
            public void solution(int id, int pId) {
                String info;
                synchronized (openNodes) {
                    NQueensNode nQueensNode = (NQueensNode) openNodes.remove();
                    System.out.println("solution");
                    info = "{\"cost\": " + nQueensNode.getId() + ", \"domain\": " + nQueensNode.getId() + ", \"other\": \"" + getNodeValue(nQueensNode.getBoard()) + "\"}";
                    //openNodes.add(nQueensNode);
                }
                /*NQueensNode nQueensNode = (NQueensNode) openNodes.remove();
                System.out.println("solution");
                String info = "{\"cost\": "+id+", \"domain\": "+id+", \"other\": \""+ getNodeValue(nQueensNode.getBoard())+"\"}";
                openNodes.add(nQueensNode);*/
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());

                // S'assurer que showChessBoard est appelé dans le bon thread
                Platform.runLater(() -> t.createNode(id, pId, Tree.NodeType.SOLUTION, () -> showChessBoard(infoData, Tree.NodeType.SOLUTION), info));
            }

            @Override
            public void fail(int id, int pId) {
                String info;
                synchronized (openNodes) {
                    NQueensNode nQueensNode = (NQueensNode) openNodes.remove();
                    System.out.println("solution");
                    info = "{\"cost\": " + nQueensNode.getId() + ", \"domain\": " + nQueensNode.getId() + ", \"other\": \"" + getNodeValue(nQueensNode.getBoard()) + "\"}";
                    //openNodes.add(nQueensNode);
                }
                /*NQueensNode nQueensNode = (NQueensNode) openNodes.remove();
                System.out.println("fail");
                String info = "{\"cost\": "+id+", \"domain\": "+id+", \"other\": \""+ getNodeValue(nQueensNode.getBoard())+"\"}";
                openNodes.add(nQueensNode);*/
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());

                // Utiliser Platform.runLater pour garantir l'exécution dans le bon thread
                Platform.runLater(() -> t.createNode(id, pId, Tree.NodeType.FAIL, () -> showChessBoard(infoData, Tree.NodeType.FAIL), info));
            }

            @Override
            public void branch(int id, int pId, int nChilds) {
                String info;
                synchronized (openNodes) {
                    NQueensNode nQueensNode = (NQueensNode) openNodes.remove();
                    System.out.println("solution");
                    info = "{\"cost\": " + nQueensNode.getId() + ", \"domain\": " + nQueensNode.getId() + ", \"other\": \"" + getNodeValue(nQueensNode.getBoard()) + "\"}";
                    //openNodes.add(nQueensNode);
                }
                /*NQueensNode nQueensNode = (NQueensNode) openNodes.remove();
                System.out.println("branch");
                String info = "{\"cost\": "+id+", \"domain\": "+id+", \"other\": \""+ getNodeValue(nQueensNode.getBoard())+"\"}";
                openNodes.add(nQueensNode);*/
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>(){}.getType());

                // S'assurer que l'UI est manipulée dans le bon thread
                Platform.runLater(() -> t.createNode(id, pId, Tree.NodeType.INNER, () -> showChessBoard(infoData, Tree.NodeType.INNER), info));
            }
        }, openNodes, nQueensNodeNode -> {
            System.out.println("new best solution: " + nQueensNodeNode.lowerBound());
            System.out.println(nQueensNodeNode.toString());;
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
    public static void showChessBoard(TreeVisual.NodeInfoData nodeInfoData, Tree.NodeType type){
        int n = NQueensSolver.numVisualizations;
        Map<Integer, Integer> coordinates = new Gson().fromJson(nodeInfoData.other, new TypeToken<HashMap<Integer, Integer>>() {}.getType());
        GridPane chess = new GridPane();
        Scene chessScene = new Scene(chess, n*50 +n, n*50 +n);
        Stage chessWindow = new Stage();

        chessWindow.setTitle("Nqueens Visualisation Board");
        chessWindow.setScene(chessScene);

        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(coordinates.get(i) == j){
                    chess.add(createRectangleForVisualisation(true, type), j, i);
                }else{
                    chess.add(createRectangleForVisualisation(false,type), j, i);
                }
            }
        }
        chessWindow.show();
    }
}
