import java.net.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.SynchronousQueue;

public class Master {
    // reader thread expects a buffered reader object which connects it to the slave and expects access to done list
    // writer expects a printwriter object. -- writer.setJob(String)
    public static void main(String[] args) throws IOException {

        Queue<String> slaveAQueue = new LinkedList<>();
        Queue<String> slaveBQueue = new LinkedList<>();

        Queue<String> jobs = new LinkedList<>();
        jobs.add("Bb");
        jobs.add("An");
        jobs.add("Am");
        jobs.add("Ah");
        jobs.add("Ap");

        // Hard code in port number if necessary:
        args = new String[]{"30121"};

        int portNumber = Integer.parseInt(args[0]);

        Queue<String> doneListA = new LinkedList<>();
        Queue<String> doneListB = new LinkedList<>();
        Queue<String> clientXDoneList = new LinkedList<>();
        Object readerLocker = new Object();
        Object writerLocker = new Object();


        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
             //Create slave sockets
             Socket slaveA = serverSocket.accept();
             Socket slaveB = serverSocket.accept();
             //Create client sockets
             Socket clientX = serverSocket.accept();

             //Create slave writers
             PrintWriter slaveAOutWriter = new PrintWriter(slaveA.getOutputStream(), true);
             PrintWriter slaveBOutWriter = new PrintWriter(slaveB.getOutputStream(), true);
             //Create client writers
             PrintWriter clientXOutWriter = new PrintWriter(clientX.getOutputStream(), true);

             //Create slave readers
             BufferedReader slaveAInReader = new BufferedReader(new InputStreamReader(slaveA.getInputStream()));
             BufferedReader slaveBInReader = new BufferedReader(new InputStreamReader(slaveB.getInputStream()));
             //Create client readers
             BufferedReader clientXInReader = new BufferedReader(new InputStreamReader(clientX.getInputStream()));
        ) {
            //Create writer threads
            WriterThread slaveAWriter = new WriterThread(slaveAOutWriter, slaveAQueue);
            WriterThread slaveBWriter = new WriterThread(slaveBOutWriter, slaveBQueue);
            WriterThread clientXWriter = new WriterThread(clientXOutWriter, clientXDoneList);

            //Create reader threads
            ReaderThread slaveAReader = new ReaderThread(slaveAInReader, doneListA);
            ReaderThread slaveBReader = new ReaderThread(slaveBInReader, doneListB);
            ReaderThread clientXReader = new ReaderThread(clientXInReader, jobs);

            //Start reader threads
            slaveAReader.start();
            slaveBReader.start();
            clientXReader.start();
            //Start writer threads
            slaveAWriter.start();
            slaveBWriter.start();
            clientXWriter.start();

            //master delegating jobs to queues
            while (true) {
                if(!jobs.isEmpty()){
                    delegate(slaveAQueue, slaveBQueue, jobs);
                }
                else{
                    //TAKE IN RESPONSES
                    if(!doneListA.isEmpty()){
                        System.out.println("DONE FROM A LIST: " + doneListA.poll());
                    }
                    if(!doneListB.isEmpty()){
                        System.out.println("DONE FROM B LIST: " + doneListB.poll());
                    }
                    System.out.println("Master has delegated all jobs, waiting 5 seconds.");
                    Thread.sleep(5000);
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    private static void delegate(Queue<String> slaveAQueue, Queue<String> slaveBQueue, Queue<String> jobs) {
        if(slaveAQueue.size() > slaveBQueue.size()){
            slaveBQueue.add(jobs.poll());
        }else if(slaveAQueue.size() < slaveBQueue.size()){
            slaveAQueue.add(jobs.poll());
        }else if(slaveAQueue.size() == slaveBQueue.size()){
            if(jobs.peek().charAt(0) == 'A'){
                slaveAQueue.add(jobs.poll());
            }else if(jobs.peek().charAt(0) == 'B'){
                slaveBQueue.add(jobs.poll());
            }
        }
    }
}
