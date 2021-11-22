import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class SlaveA {
    public static void main(String[] args) throws IOException {

        args = new String[] {"127.0.0.1", "30121"};

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket SlaveSocket = new Socket(hostName, portNumber);
                PrintWriter outWriter = new PrintWriter(SlaveSocket.getOutputStream(), true);
                BufferedReader inReader= new BufferedReader(new InputStreamReader(SlaveSocket.getInputStream()))
        ) {

            String stringMasterJob;

            while (true) {
                System.out.println("Slave A connected");


                //Gets the first response index from the server
                stringMasterJob = inReader.readLine();
                while(stringMasterJob.equals(null)){
                    stringMasterJob = inReader.readLine();
                }

                System.out.println("Slave A received a job");

                if (stringMasterJob.charAt(0) == 'A'){
                    System.out.println("Doing A type job. Sleeping for 5 seconds");
                    Thread.sleep(5000);
                }
                else {
                    System.out.println("Doing B type job. Sleeping for 5 seconds");
                    Thread.sleep(15_000);
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //Method to grow the list to fit the index
}