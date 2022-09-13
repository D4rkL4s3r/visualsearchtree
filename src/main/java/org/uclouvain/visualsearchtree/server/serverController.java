package org.uclouvain.visualsearchtree.server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.uclouvain.visualsearchtree.bridge.Decoder;
import org.uclouvain.visualsearchtree.bridge.Message;
import org.uclouvain.visualsearchtree.tree.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class serverController implements Initializable {

    @FXML
    private Label portLabel;

    private ServerUtil server;
    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        portLabel.setText("6650");
        server = null;
        runServer();
    }

    /**
     * run the server
     */
    public void runServer()
    {
        int port;
        if (server != null) {
            server.stop();
        }
        try{
            port = Integer.parseInt(portLabel.getText());
        }
        catch (Exception er) {
            return;
        }
        // create a server and start it
        server = new ServerUtil(port);
        new ServerRunning().start();
    }

    class ServerUtil {
        // unique ID for each connection
        private int uniqueID;
        private ArrayList<ClientThread> al;
        private int port;
        private boolean keepGoing;

        public ServerUtil(int port){
            this.port = port;
            al = new ArrayList<ClientThread>();
        }

        public void start(){
            keepGoing = true;
            /* create socket server and wait for connection requests */
            try
            {
                ServerSocket serverSocket = new ServerSocket(port);
                while (keepGoing)
                {
                    System.out.println("Server waiting for Clients on port " + port + ".");
                    Socket socket = serverSocket.accept();
                    if (!keepGoing)
                        break;
                    ClientThread t = new ClientThread(socket);
                    al.add(t);
                    t.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void stop(){
            keepGoing = false;
            try {
                new Socket("localhost", port);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        class ClientThread extends Thread {
            Socket socket;
            ObjectInputStream sInput;
            ObjectOutputStream sOutput;
            private DataInputStream in = null;
            private List<Byte> buffer = new ArrayList<>();
            private List<Decoder.DecodedMessage> decodedMessagesList = new ArrayList<>();
            private int msgSize = 0;
            private boolean sizeRead = false;
            // client id
            private Tree tree;
            int id;

            ClientThread(Socket s) {
                id = uniqueID++;
                this.socket = s;
                System.out.println("Thread trying to create Object Input/Output Streams");
                socket = s;
                tree = new Tree(-1);
                tree.addListener(new TreeListener() {
                    @Override
                    public void onNodeCreated(int id, int pId, Tree.NodeType type, NodeAction nodeAction, String info) {
                        System.out.println(type);
                    }
                });
            }

            // run it forever
            public void run(){
                try {
                    in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    System.out.println(" start server ..................");
                    while (keepGoing) {
                        if (socket.getInputStream().available() > 0)
                            keepGoing = true;
                        Decoder.addToBuffer(buffer, socket.getInputStream().readAllBytes());

                        // read the size of the next field if haven't already
                        if (!sizeRead) {
                            if (buffer.size() < 4) {
                                /// can't read, need to wait for more bytes
                                keepGoing = false;
                                continue;
                            }

                            /// enough bytes to read size
                            byte[] msgSizeBytes = new byte[4];
                            Decoder.readBuffer(msgSizeBytes, buffer, 4);
                            msgSize = Decoder.byteArrayToInt(msgSizeBytes, "LITTLE_ENDIAN");
                            sizeRead = true;
                        } else {
                            if (buffer.size() < msgSize) {
                                /// can't read, need to wait for more bytes
                                keepGoing = false;
                                continue;
                            }

                            Decoder.DecodedMessage msgBody = Decoder.deserialize(buffer, msgSize);
                            if (msgBody.msgType == Message.MsgType.NODE.getNumber()) {
                                tree.createNode(msgBody.nodeId, msgBody.nodePid, Decoder.nodeType(msgBody.nodeStatus), ()->{},msgBody.nodeInfo);
                                decodedMessagesList.add(msgBody);
                            }

                            //if (DEBUG)
                            {
                                //System.out.println(msgBody);
                                System.out.println("-----");
                            }

                            if (msgBody.msgType == Message.MsgType.DONE.getNumber()) {
                                tree.createNode(msgBody.nodeId, msgBody.nodePid, Decoder.nodeType(msgBody.msgType), ()->{},msgBody.nodeInfo);
                                System.out.println("create tree");
                                //socket.close();
                                keepGoing = false;
                                TreeVisual tv = new TreeVisual(tree);
                                Platform.runLater(()->{
                                    Stage stage = new Stage();
                                    Scene scene = new Scene(tv.getGroup(), 500, 700);
                                    stage.setScene(scene);
                                    stage.show();
                                });
                            }
                            sizeRead = false;
                        }
                    }
                    

                    System.out.println("Closing connection");
                    // close connection
                    socket.close();
                    in.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
                finally {
                    System.out.println("Client No:" + 56 + " exit!! ");
                }
            }

            private void close() {
                try {
                    if (sOutput != null) sOutput.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try{
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * A tread to run the server
     */
    class ServerRunning extends Thread {
        public void run() {
            server.start(); // until it fails
            server = null;
        }
    }

}
