package org.uclouvain.visualsearchtree.bridge;

import org.uclouvain.visualsearchtree.bridge.Connector.NodeStatus;

public class ConnectorTest {
    public static void main(String[] args) {
        // Crée une instance de la classe Connector
        Connector connector = new Connector();
        int port = 6666; // Remplacez par le port utilisé par le serveur CPProfiler

        try {
            // Connexion au serveur
            connector.connect(port);

            // Démarrer une session avec un RID -1 (première session)
            connector.start("Test Session", -1);

            // Créer et envoyer des nœuds à titre de démonstration
            System.out.println("Envoi de nœuds de test...");
            connector.sendNode(1, 0, 0, 2, NodeStatus.BRANCH); // Root node
            connector.sendNode(2, 1, 0, 0, NodeStatus.SOLVED); // First child
            connector.sendNode(3, 1, 1, 0, NodeStatus.FAILED); // Second child

            // Fin de la session et fermeture de la connexion
            connector.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
