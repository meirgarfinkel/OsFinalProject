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

        ArrayList<String> doneList = new ArrayList<>();
        Object readerLocker = new Object();
        Object writerLocker = new Object();


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
            WriterThread slaveAWriter = new WriterThread(slaveAOutWriter, slaveAQueue, writerLocker);
            WriterThread slaveBWriter = new WriterThread(slaveBOutWriter, slaveBQueue, writerLocker);

            //Create reader threads
            ReaderThread slaveAReader = new ReaderThread(slaveAInReader, doneList, readerLocker);
            ReaderThread slaveBReader = new ReaderThread(slaveBInReader, doneList, readerLocker);

            //Start Threads

            slaveAReader.start();
            slaveBReader.start();

            slaveAWriter.start();
            slaveBWriter.start();

            //master delegating jobs to queues
            while (true) {
                if(!jobs.isEmpty()){
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
                else{
                    System.out.println("Empty list, waiting a second");
                    Thread.sleep(1000);
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public static Queue<String> delegate(Queue<String> aList, Queue<String> bList, String job){
        char jobType = job.charAt(0);
        int aTimeLeft = estimateTimeLeft('A', aList);
        int bTimeLeft = estimateTimeLeft('B', bList);

        if(aTimeLeft > bTimeLeft){
            return bList;
        }else if (bTimeLeft > aTimeLeft){
            return aList;
        }else{ //if (bTimeLeft == aTimeLeft){
            if(jobType == 'A') {
                return aList;
            }else{ //if(jobType == 'B'){
                return bList;
            }
        }
    }

    public static int estimateTimeLeft(char aOrB, Queue<String> jobs){
        if(!(aOrB == ('A') || aOrB == ('B'))){
            throw new IllegalArgumentException("First argument must be either \'A\' or \'B\'");
        }

        int totalTime = 0;

        int aTime = 5;
        int bTime = 15;
        if (aOrB == ('B')) {
            aTime = 15;
            bTime = 5;
        }
        for (String job : jobs) {
            if (job.equals("A")) {
                totalTime += aTime;
            } else if (job.equals("B")) {
                totalTime += bTime;
            } else {
                throw new IllegalArgumentException("The ArrayList you entered contains non \'A\' or \'B\'");
            }
        }
        return totalTime;
    }
}
