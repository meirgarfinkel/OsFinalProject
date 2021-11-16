import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/*
WriterThread x in the master class should be given a job through x.setJob.
x.setJob will make the job variable not null, allowing it to be sent to the slave.
It will be sent to the slave through outWriter.println(job);
After sending it to the slave job will be reset to null so the slave doesnt get the same job over and over.
 */

public class WriterThread extends Thread{
    String job;
    PrintWriter outWriter;

    public WriterThread(PrintWriter outWriter){
        this.outWriter = outWriter;
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

            }else{
                outWriter.println(job);
                job = null;
            }
        }
    }
}
