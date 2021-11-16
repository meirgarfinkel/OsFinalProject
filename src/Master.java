import java.net.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.SynchronousQueue;

public class Master {
    // reader thread expects a buffered reader object which connects it to the slave and expects access to done list
    // writer expects a printwriter object. -- writer.setJob(String)

    public static void main(String[] args) throws IOException {


        // Hard code in port number if necessary:
        args = new String[] { "30121" };

        int portNumber = Integer.parseInt(args[0]);

        Queue<String> doneList = new LinkedList<String>();

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
             //Create slave sockets
             Socket slaveA = serverSocket.accept();
             Socket slaveB = serverSocket.accept();

             //Create slave writers
             PrintWriter slaveAOutWriter = new PrintWriter(slaveA.getOutputStream(), true);
             PrintWriter slaveBOutWriter = new PrintWriter(slaveB.getOutputStream(), true);

             //Create slave readers
             BufferedReader slaveAInReader = new BufferedReader(new InputStreamReader(slaveA.getInputStream()));
             BufferedReader slaveBInReader = new BufferedReader(new InputStreamReader(slaveB.getInputStream()));
        ) {
            //Create writer threads
            WriterThread slaveAWriter = new WriterThread(slaveAOutWriter);
            WriterThread slaveBWriter = new WriterThread(slaveBOutWriter);

            //Create reader threads



            while (true) {


            }
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public int estimateTimeLeft(String aOrB, ArrayList<String> jobs){
        if(aOrB.equals("A") || aOrB.equals("B")){
            throw new IllegalArgumentException("First argument must be either \'A\' or \'B\'");
        }

        int totalTime = 0;

        int aTime = 5;
        int bTime = 15;
        if(aOrB.equals("B")){
            aTime = 15;
            bTime = 5;
        }
        for(String job : jobs){
            if(job.equals("A")){
                totalTime += aTime;
            }else if(job.equals("B")){
                totalTime += bTime;
            }else {
                throw new IllegalArgumentException("The ArrayList you entered contains non \'A\' or \'B\'");
            }
        }
        return totalTime;
    }
}
