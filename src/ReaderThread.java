import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
ReaderThread will receive its job from the slave class through inReader.readLine();
This will allow the thread to add this job to the done list that is passed in from the master
job variable will be reset to null so the thread can go back to listening for jobs from the master
 */

public class ReaderThread extends Thread {
    String job;
    BufferedReader inReader;
    Queue done;

    public ReaderThread(BufferedReader inReader, Queue done) {
        this.inReader = inReader;
        this.done = done;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }


    @Override
    public void run() {
        String stringMasterJob;
        while (true) {
            try {
                //while (!((stringMasterJob = inReader.readLine()) == null)) ;
                done.add(inReader.readLine());
                //System.out.println(inReader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
