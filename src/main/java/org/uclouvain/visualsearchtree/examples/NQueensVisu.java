/*
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

*/
/**
 * Classe NQueensVisu pour visualiser la résolution du problème N-Reines
 * en utilisant le solveur NQueensSolverVisu.
 *//*

public class NQueensVisu {

    public static void main(String[] args) throws Exception {
        int n = 4; // Nombre de reines
        NQueensSolverVisu nQueensSolverVisu = new NQueensSolverVisu(n);
        Gson gson = new Gson();
        Tree tree = new Tree(-1); // Arbre pour la visualisation

        // Créer l'arbre de visualisation
        TreeVisual treeVisual = new TreeVisual(() -> nQueensSolverVisu.solve(new SolverListener() {
            @Override
            public void solution(int id, int pId) {
                String info = "{\"cost\": " + id + ", \"domain\": " + id + ", \"other\": \"" + getNodeValue(nQueensSolverVisu.queens) + "\"}";
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>() {}.getType());

                // Appeler showChessBoard dans le bon thread
                Platform.runLater(() -> tree.createNode(id, pId, Tree.NodeType.SOLUTION, () -> showChessBoard(infoData, Tree.NodeType.SOLUTION), info));
            }

            @Override
            public void fail(int id, int pId) {
                String info = "{\"cost\": " + id + ", \"domain\": " + id + ", \"other\": \"" + getNodeValue(nQueensSolverVisu.queens) + "\"}";
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>() {}.getType());

                // Appeler showChessBoard dans le bon thread
                Platform.runLater(() -> tree.createNode(id, pId, Tree.NodeType.FAIL, () -> showChessBoard(infoData, Tree.NodeType.FAIL), info));
            }

            @Override
            public void branch(int id, int pId, int nChilds) {
                String info = "{\"cost\": " + id + ", \"domain\": " + id + ", \"other\": \"" + getNodeValue(nQueensSolverVisu.queens) + "\"}";
                TreeVisual.NodeInfoData infoData = gson.fromJson(info, new TypeToken<TreeVisual.NodeInfoData>() {}.getType());

                // Appeler showChessBoard dans le bon thread
                Platform.runLater(() -> tree.createNode(id, pId, Tree.NodeType.INNER, () -> showChessBoard(infoData, Tree.NodeType.INNER), info));
            }
        }), tree, false);

        // Lancer l'interface graphique avec TreeVisual
        Platform.runLater(() -> {
            try {
                Visualizer.show(treeVisual);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    */
/**
     * Convertit la représentation des positions des reines en une chaîne JSON.
     * @param q tableau des positions des reines
     * @return chaîne JSON représentant les positions
     *//*

    public static String getNodeValue(int[] q) {
        StringBuilder value = new StringBuilder("{");
        for (int i = 0; i < q.length; i++) {
            value.append(i).append(":").append(q[i]);
            if (i != (q.length - 1)) {
                value.append(",");
            }
        }
        value.append("}");
        return value.toString();
    }

    */
/**
     * Crée un rectangle pour visualiser le plateau de jeu.
     * @param isFixed indique si la case est fixe (où une reine est placée)
     * @param type type de nœud (solution, échec, inner)
     * @return rectangle représentant une case du plateau
     *//*

    private static Rectangle createRectangleForVisualisation(boolean isFixed, Tree.NodeType type) {
        Rectangle r = new Rectangle(50, 50);
        Color c = (type == Tree.NodeType.FAIL) ? Color.RED : (type == Tree.NodeType.SOLUTION) ? Color.GREEN : Color.CORNFLOWERBLUE;
        r.setFill(isFixed ? c : Color.WHITE);
        r.setStrokeType(StrokeType.OUTSIDE);
        r.setStrokeWidth(.4);
        r.setStroke(Color.BLACK);
        return r;
    }

    */
/**
     * Affiche le plateau d'échecs dans une nouvelle fenêtre.
     * @param nodeInfoData informations sur le nœud à afficher
     * @param type type de nœud
     *//*

    public static void showChessBoard(TreeVisual.NodeInfoData nodeInfoData, Tree.NodeType type) {
        int n = NQueensSolver.numVisualizations; // Nombre de reines
        Map<Integer, Integer> coordinates = new Gson().fromJson(nodeInfoData.other, new TypeToken<HashMap<Integer, Integer>>() {}.getType());
        GridPane chess = new GridPane();
        Scene chessScene = new Scene(chess, n * 50 + n, n * 50 + n);
        Stage chessWindow = new Stage();

        chessWindow.setTitle("N-Queens Visualization Board");
        chessWindow.setScene(chessScene);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (coordinates.get(i) == j) {
                    chess.add(createRectangleForVisualisation(true, type), j, i);
                } else {
                    chess.add(createRectangleForVisualisation(false, type), j, i);
                }
            }
        }
        chessWindow.show();
    }
}
*/
