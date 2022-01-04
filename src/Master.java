import java.net.*;
import java.util.*;
import java.io.*;

/**
 * This is the master of all the operations. The clients send jobs to the master, then
 * the master delegates it, based on certain guidelines, to either SlaveA or SlaveB.
 * Then after they are done performing their jobs, they send a message back to the Master,
 * which then sends a message to client letting them know that the job is finished.
 */
public class Master {

    // reader thread expects a buffered reader object which connects it to the slave and expects access to done list
    // writer expects a printWriter object. -- writer.setJob(String)
    public static void main(String[] args) throws IOException {


        // Hard code in port number if necessary:
        args = new String[]{"30121"};

        int portNumber = Integer.parseInt(args[0]);

        /*Makes two queues to which jobs will be added. This is also used in the delegate
        method below to determine if its better to give a job to the other slave or not
        */
        Queue<String> slaveAQueue = new LinkedList<>();
        Queue<String> slaveBQueue = new LinkedList<>();

        //List of jobs that has been received from the Client
        Queue<String> jobs = new LinkedList<>();

        //Different lists to see how much is finished
        Queue<String> doneListA = new LinkedList<>();
        Queue<String> doneListB = new LinkedList<>();
        Queue<String> client1DoneList = new LinkedList<>();
        Queue<String> client2DoneList = new LinkedList<>();
        Queue<String> doneMasterList = new LinkedList<>();

        //TODO i DELETED THE LOCKERS THAT WERE ABOVE THIS COMMENT


        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));

             //Create slave sockets
             Socket slaveA = serverSocket.accept();
             Socket slaveB = serverSocket.accept();
             //Create client sockets
             Socket client1 = serverSocket.accept();
             Socket client2 = serverSocket.accept();

             //Create slave writers
             PrintWriter slaveAOutWriter = new PrintWriter(slaveA.getOutputStream(), true);
             PrintWriter slaveBOutWriter = new PrintWriter(slaveB.getOutputStream(), true);
             //Create client writers
             PrintWriter client1OutWriter = new PrintWriter(client1.getOutputStream(), true);
             PrintWriter client2OutWriter = new PrintWriter(client2.getOutputStream(), true);

             //Create slave readers
             BufferedReader slaveAInReader = new BufferedReader(new InputStreamReader(slaveA.getInputStream()));
             BufferedReader slaveBInReader = new BufferedReader(new InputStreamReader(slaveB.getInputStream()));
             //Create client readers
             BufferedReader client1InReader = new BufferedReader(new InputStreamReader(client1.getInputStream()));
             BufferedReader client2InReader = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        ) {

            //Create writer threads
            WriterThread slaveAWriter = new WriterThread(slaveAOutWriter, slaveAQueue);
            WriterThread slaveBWriter = new WriterThread(slaveBOutWriter, slaveBQueue);
            WriterThread client1Writer = new WriterThread(client1OutWriter, client1DoneList);
            WriterThread client2Writer = new WriterThread(client2OutWriter, client2DoneList);

            //Create reader threads
            ReaderThread slaveAReader = new ReaderThread(slaveAInReader, doneListA);
            ReaderThread slaveBReader = new ReaderThread(slaveBInReader, doneListB);
            ReaderThread client1Reader = new ReaderThread(client1InReader, jobs);
            ReaderThread client2Reader = new ReaderThread(client2InReader, jobs);

            //Start reader threads
            slaveAReader.start();
            slaveBReader.start();
            client1Reader.start();
            client2Reader.start();

            //Start writer threads
            slaveAWriter.start();
            slaveBWriter.start();
            client1Writer.start();
            client2Writer.start();

            //master delegating jobs to queues
            while (true) {
                while (!jobs.isEmpty()) {
                    delegateToSlaves(slaveAQueue, slaveBQueue, jobs);
                }

                //TAKE IN RESPONSES
                while (!doneListA.isEmpty()) {
                    System.out.println("SLAVE A FINISHED A JOB");
                    doneMasterList.add(doneListA.poll());
                }
                while (!doneListB.isEmpty()) {
                    System.out.println("SLAVE B FINISHED A JOB");
                    doneMasterList.add(doneListB.poll());
                }
                System.out.println("Master has delegated all jobs, waiting 5 seconds.");
                Thread.sleep(5000);

                delegateToClients(client1DoneList, client2DoneList, doneMasterList);

            }
        } catch (IOException | InterruptedException e) { //
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    private static void delegateToSlaves(Queue<String> slaveAQueue, Queue<String> slaveBQueue, Queue<String> jobs) {
        if (slaveAQueue.size() > slaveBQueue.size()) {
            slaveBQueue.add(jobs.poll());
        } else if (slaveAQueue.size() < slaveBQueue.size()) {
            slaveAQueue.add(jobs.poll());
        } else if (slaveAQueue.size() == slaveBQueue.size()) {
            if (jobs.peek().charAt(0) == 'A') {
                slaveAQueue.add(jobs.poll());
            } else if (jobs.peek().charAt(0) == 'B') {
                slaveBQueue.add(jobs.poll());
            }
        }
    }

    private static void delegateToClients(Queue<String> client1DoneList, Queue<String> client2DoneList, Queue<String> doneList) {
        String completedJob;
        while (!doneList.isEmpty()) {
            completedJob = doneList.poll();
            if (completedJob.charAt(1) == '1') {
                client1DoneList.add(completedJob);
            } else if (completedJob.charAt(1) == '2') {
                client2DoneList.add(completedJob);
            }
        }
    }
}
