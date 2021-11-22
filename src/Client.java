import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Client {
    /**
     * Clients are going to connect directly to the master and submit jobs of either type.
     * The client’s submission should include the type, and an ID number that will be
     * used to identify the job throughout the system.
     */

    public static void main(String[] args) throws IOException {
        // Hard code in port number if necessary:
        args = new String[]{"30121"};
        HashMap<Integer,Character> jobs = new HashMap<>();
        jobs.put(1,'A');
        jobs.put(2,'B');
        jobs.put(3,'B');
        jobs.put(4,'A');
        jobs.put(5,'B');
        jobs.put(6,'A');

        int jobID = 1;
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
             Socket clientSocket1 = serverSocket.accept();

             PrintWriter responseWriter1 = new PrintWriter(clientSocket1.getOutputStream(), true);
             BufferedReader requestReader1 = new BufferedReader(
                     new InputStreamReader(clientSocket1.getInputStream())) )
        {
            //everytime time the client sends a job, the id increments
            //somthing to this extent, thou not correct right now
            responseWriter1.println(jobs.get(jobID) + jobID);
            jobID++;

        }

        catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

}
