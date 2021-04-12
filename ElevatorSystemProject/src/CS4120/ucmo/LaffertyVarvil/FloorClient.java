package CS4120.ucmo.LaffertyVarvil;

import javafx.application.Application;
import javafx.application.Platform;
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

public class FloorClient extends Application {
    //Declare the variables for io stream for client to and from the server
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    //variables for the GUI
    TextField tfname;
    TextField tfmessage;
    static TextArea ta;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException {
        BorderPane bPane = new BorderPane();
        GridPane gPane = new GridPane();

        tfname = new TextField();
        tfmessage = new TextField();

        gPane.addRow(0, new Label("Name: "), tfname);
        gPane.addRow(1, new Label("Message: "), tfmessage);

        bPane.setTop(gPane);
        ta = new TextArea();
        bPane.setCenter(new ScrollPane(ta));

        Scene scene = new Scene(bPane, 450, 200);
        primaryStage.setTitle("Client1");
        primaryStage.setScene(scene);
        primaryStage.show();

        //connect to the server, create socket on port 8000
        Socket socket = new Socket("localhost", 8000);

        //update GUI to show progress.
        Platform.runLater(() -> {
            ta.appendText("Connected to Server\n");
        });

        //set the io stream for this client, initialize to and from
        toServer = new ObjectOutputStream(socket.getOutputStream());
        fromServer = new ObjectInputStream(socket.getInputStream());
    }
}
