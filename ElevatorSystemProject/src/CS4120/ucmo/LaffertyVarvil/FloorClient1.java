package CS4120.ucmo.LaffertyVarvil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FloorClient1 extends Application {

    //Declare the variables for io stream for client to and from the server
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    //variables for the GUI
    public static Label lblname;
    TextField tfFloorRequest;
    static TextArea ta;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException {
        BorderPane bPane = new BorderPane();
        GridPane gPane = new GridPane();

        lblname = new Label();
        tfFloorRequest = new TextField();

        gPane.addRow(0, lblname);
        gPane.addRow(1, new Label("Request a Floor: "), tfFloorRequest);

        bPane.setTop(gPane);
        ta = new TextArea();
        bPane.setCenter(new ScrollPane(ta));
        tfFloorRequest.setOnAction(new sendMessage());

        Scene scene = new Scene(bPane, 450, 200);
        primaryStage.setTitle("Floor Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        //connect to the server, create socket on port 8000
        Socket socket = new Socket("localhost", 8000);
        //set the io stream for this client, initialize to and from
        toServer = new ObjectOutputStream(socket.getOutputStream());
        fromServer = new ObjectInputStream(socket.getInputStream());

        //update GUI to show progress.
        Platform.runLater( () -> {
            ta.appendText("Connected to Server\n");
        });

        //Create and initalize an instance of ReceiveTask. This
        //is the task and thread this client will receive messages.
        ReceiveTask receiveTask = new ReceiveTask(fromServer);

        //Create thread and initialize with ReceiveTask instance above
        Thread thread = new Thread(receiveTask);
        //start the above thread
        thread.start();
    }

    //public static void main(String[] args) throws IOException {
    class sendMessage implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            //get the message and sender name
            //String name = tfname.getText().trim();
            String request = tfFloorRequest.getText().trim();

            //create threads
            //create instance of SendTask
            SendTask sendTask = new SendTask(toServer, request);
            //Create thread with SendTask instance above
            Thread thread = new Thread(sendTask);

            //Start the thread
            thread.start();
        }

    }


    private static class SendTask implements Runnable {
        //Declare stream to write to the server
        ObjectOutputStream toServer;
        String request;


        public SendTask(ObjectOutputStream toServer, String request){
            this.toServer = toServer;
            this.request = request;
        }

        public void run(){

            try {
                toServer.writeObject(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static class ReceiveTask implements Runnable{
        //Declare stream to receive data from the server
        ObjectInputStream fromServer;


        public ReceiveTask(ObjectInputStream fromServer){
            this.fromServer = fromServer;
        }

        @Override
        public void run(){
            try {
                String floor = (String)fromServer.readObject();
                Platform.runLater(()->{
                    lblname.setText(floor);
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            while (true){
                String received = null;
                try {
                    received = (String)fromServer.readObject();
                    String finalReceived = received;
                    Platform.runLater( () -> {
                        ta.appendText(finalReceived);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println(received);
            }
        }
    }

}