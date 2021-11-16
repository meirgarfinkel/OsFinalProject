import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/*
ReaderThread will receive it's job from the slave class through inReader.readLine();
This will allow the thread to add this job to the done list that is passed in from the master
job variable will be reset to null so the thread can go back to listening for jobs from the master
 */

public class ReaderThread extends Thread{
    String job;
    BufferedReader inReader;
    ArrayList done;

    public ReaderThread(BufferedReader inReader, ArrayList done){
        this.inReader = inReader;
        this.done = done;
    }

    public void setJob(String job){
        this.job = job;
    }

    public String getJob(){
        return job;
    }

    @Override
    public void run(){
        while(true){
            if(job.equals(null)){
                try {
                    inReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                done.add(getJob());
                setJob(null);
            }
        }
    }
}
