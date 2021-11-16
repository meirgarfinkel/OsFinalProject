import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.io.*;
import java.util.concurrent.SynchronousQueue;

public class Master {
    // reader thread expects a buffered reader object which connects it to the slave and expects access to done list
    // writer expects a printwriter object. -- writer.setJob(String)

    public static void main(String[] args) throws IOException {

        // Hard code in port number if necessary:
        args = new String[] { "30121" };

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
             //Create slave sockets
             Socket SlaveA = serverSocket.accept();
             Socket SlaveB = serverSocket.accept();

             //Create slave writers
             PrintWriter SlaveAOutWriter = new PrintWriter(SlaveA.getOutputStream(), true);
             PrintWriter SlaveBOutWriter = new PrintWriter(SlaveB.getOutputStream(), true);

             //Create slave readers
             BufferedReader SlaveAInReader = new BufferedReader(new InputStreamReader(SlaveA.getInputStream()));
             BufferedReader SlaveBInReader = new BufferedReader(new InputStreamReader(SlaveB.getInputStream()));
        ) {
            while (true) {

                System.out.println("Connected to client.");

                //Listens for a message from the client to see if it is done sending packets
                if ((inReader.readLine()).equals("FINISHED")) {
                    break;
                }

                //If the client has not received all packets, server resets and sends packets again
                System.out.println("Restarting");
            }
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
