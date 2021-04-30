package CS4120.ucmo.LaffertyVarvil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ElevatorServer extends Application {
    public static ArrayList<FloorTask> floorList = new ArrayList<>();
    public static Elevator[] elevators = {new Elevator(5, 0),
            new Elevator(5, 0), new Elevator(5, 0)};
    public int count = 0;
    private static TextArea ta = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setTitle("ElevatorServer");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> {
            try {
                //create server socket on port 8000
                ServerSocket serverSocket = new ServerSocket(8000);
                ta.appendText("Server started at "
                        + new Date() + '\n');

                //wait for connections
                while (true) {
                    ta.appendText("Waiting for floors..." + '\n');


                    //listen for a connection request and accept
                    Socket client = serverSocket.accept();

                    //increment number of clients
                    count++;

                    //show info that client connected.
                    Platform.runLater(() -> {
                        ta.appendText("floor " + count + " connected" + '\n');
                    });

                    //Create a ClientTask instance  name might be "Client" + count
                    FloorTask floorTask = new FloorTask("Floor" + count, client);

                    //add ClientTask object to the ArrayList of ClientTask
                    floorList.add(floorTask);
                    //Replace *** with name of your ClientTask object
                    new Thread(floorTask).start();
                }//while true
            }//end try
            catch (IOException ex) {
                System.out.println(ex);
            }
        }).start();

    }//end start

    public static class ElevatorTask implements Runnable {
        String clientName;
        private Socket socket;
        int requestedFloor;

        public ElevatorTask(String n, Socket socket, int requestedFloor) {
            this.clientName = n;
            this.socket = socket;
            this.requestedFloor = requestedFloor;
        }

        @Override
        public void run() {
            int[] currentFloors = new int[3];
            for (int i = 0; i < 3; i++) {
                currentFloors[i] = elevators[i].getCurrentFloor();
            }
            for (int i = 0; i < 3; i++) {
                if (currentFloors[i] == requestedFloor) {
                    //open door
                }
            }
            for (int i = 0; i < 3; i++) {
                if (currentFloors[i] > requestedFloor) {
                    System.out.println(elevators[i].move(requestedFloor));
                    break;
                }
            }
            for (int i = 0; i < 3; i++) {
                if (currentFloors[i] < requestedFloor) {
                    System.out.println(elevators[i].move(requestedFloor));
                    break;
                }
            }


        }
    }

    //class ClientTask implement Runnable
    public static class FloorTask implements Runnable {
        String clientName;
        private Socket socket;
        private ObjectInputStream fromClient;
        private ObjectOutputStream toClient;

        public FloorTask(String n, Socket socket) throws IOException {
            //initialize propbert clientName
            this.clientName = n;
            //setup socket
            this.socket = socket;

            //setup io streams - the properties fromClient and toClient
            fromClient = new ObjectInputStream(socket.getInputStream());
            toClient = new ObjectOutputStream(socket.getOutputStream());
        }

        public void run() {
            String request = "";

            try {
                toClient.writeObject("Hello you are on " + clientName + '\n');
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                try {
                    //message will be concatenated with the two
                    //objects sent from the client. Use readObject(), two times
                    String clientRequest = (String) fromClient.readObject();
                    request = clientRequest;

                    //output to server to see what is happening.
                    ta.appendText("Received message: " + this.clientName + ": " + request + '\n');

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                Thread thread = new Thread(new ElevatorTask(this.clientName, this.socket, Integer.parseInt(request)));
                thread.start();

                //broadcast message to all the clients
                for (FloorTask f : floorList) {
                    //check to see if message is from the originator
                    if (f.clientName == this.clientName) {
                        try {
                            f.toClient.writeObject("Me requests: " + request + '\n');
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            f.toClient.writeObject(this.clientName + "requests: " + request + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                request = "";
            }
        }
    }
}