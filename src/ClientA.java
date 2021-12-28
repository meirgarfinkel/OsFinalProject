import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientA {
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

        int amountOfJobs = jobs.size();

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
            //everytime the client sends a job, the id increments
            //something to this extent, though I don't know if correct
            for (int i = 0; i < jobs.size(); i++) {
                responseWriter1.println(jobs.get(jobID) + jobID);
                jobID++;
            }
            //while the amount of jobs != 0
            while (jobs.size() !=0) {
                int num = 0;
                //send them to the master
                responseWriter1.println(jobs.get(num)); // send request to server
                num++;
            }
            //while it has not got back all the jobs, read to make sure to get it from master
            //todo maybe make it read every two seconds?
            //should print out job x was finished.
//            int jobsReceived = 0;
//            while(jobsReceived <= amountOfJobs){//the amount of jobs received has not reached the total sent
//                serverResponse = responseReader.readLine(); //keep reading
//                System.out.println("SERVER RESPONDS: \"" + serverResponse + "\"");
//            }


        }

        catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

}
