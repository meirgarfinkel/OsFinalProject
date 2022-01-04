import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class SlaveA {
    public static void main(String[] args) throws IOException {

        args = new String[]{"127.0.0.1", "30121"};

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                //Set up socket, reader, and writer
                Socket SlaveSocket = new Socket(hostName, portNumber);
                PrintWriter outWriter = new PrintWriter(SlaveSocket.getOutputStream(), true);
                BufferedReader inReader = new BufferedReader(new InputStreamReader(SlaveSocket.getInputStream()))
        ) {
            System.out.println("Slave A connected");
            while (true) {

                String stringMasterJob;
                while ((stringMasterJob = inReader.readLine()) == null) ;
                System.out.println("Slave A received job: " + stringMasterJob + ".");

                if (stringMasterJob.charAt(0) == 'A') { //If the job is A type sleep for 5 seconds
                    System.out.println("Doing A type job. Sleeping for 5 seconds");
                    Thread.sleep(5_000);
                    outWriter.println(stringMasterJob);
                } else { //If the job is B type sleep for 15 seconds
                    System.out.println("Doing B type job. Sleeping for 15 seconds");
                    Thread.sleep(15_000);
                    outWriter.println(stringMasterJob);
                }

                System.out.println("Slave A completed job:" + stringMasterJob + ".");
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
}