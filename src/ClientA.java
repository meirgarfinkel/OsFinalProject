import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ClientA {
    /**
     * Clients are going to connect directly to the master and submit jobs of either type.
     * The client’s submission should include the type, and an ID number that will be
     * used to identify the job throughout the system.
     */

    public static void main(String[] args) throws IOException {
        // Hard code in port number if necessary:
        args = new String[]{"30121"};
        LinkedList<String> doneListA = new LinkedList<>();
        Queue<String> jobs = new LinkedList<>();
        jobs.add("Bb");
        jobs.add("An");
        jobs.add("Am");
        jobs.add("Ah");
        jobs.add("Ap");

        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
             //Create client sockets
             Socket clientA = serverSocket.accept();
             //Create client writers
             PrintWriter clientAOutWriter = new PrintWriter(clientA.getOutputStream(), true);
             //Create client readers
             BufferedReader clientAInReader = new BufferedReader(new InputStreamReader(clientA.getInputStream()));
        )
        {
            WriterThread masterWriter = new WriterThread(clientAOutWriter, jobs);
            ReaderThread masterReader = new ReaderThread(clientAInReader, doneListA);

            //write the list of jobs to the master
            masterWriter.start();

        }

        catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

}
