import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;

/*
WriterThread x in the master class should be given a job through x.setJob(String job).
x.setJob will make the job variable not null, allowing it to be sent to the slave.
It will be sent to the slave through outWriter.println(job);
After sending it to the slave job will be reset to null so the slave doesnt get the same job over and over.
 */

public class WriterThread extends Thread{
    //String job;
    PrintWriter outWriter;
    Queue<String> toDo;
    Object locker;

    public WriterThread(PrintWriter outWriter, Queue<String> toDo, Object locker){
        this.outWriter = outWriter;
        this.toDo = toDo;
        this.locker = locker;
    }

    public void setJob(String job){
        toDo.add(job);
    }

    @Override
    public void run(){
        while(true){
            while(toDo.isEmpty()); //spin
            synchronized(locker) {
                outWriter.println(toDo.poll());
            }
        }
    }
}
