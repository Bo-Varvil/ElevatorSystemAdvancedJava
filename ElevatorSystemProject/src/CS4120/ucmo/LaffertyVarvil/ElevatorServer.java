package CS4120.ucmo.LaffertyVarvil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ElevatorServer extends Application {
    private static TextArea ta = new TextArea();

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setTitle("ElevatorServer");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread ( () ->{
            try {
                //create server socket on port 8000
                ServerSocket serverSocket = new ServerSocket(8000);
                ta.appendText("ElevatorServer started at "
                        + new Date() + '\n');

                //wait for connections
                while (true) {
                    ta.appendText("Waiting for floors..." + '\n');


                    //listen for a connection request and accept
                    Socket client = serverSocket.accept();

                    //show info that client connected.
                    Platform.runLater( () -> {
                        ta.appendText("Floor connected" + '\n');
                    });
                }//while true
            }//end try
            catch(IOException ex) {
                System.out.println(ex);
            }
        }).start();

    }
}
