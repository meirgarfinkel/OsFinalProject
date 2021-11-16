import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.io.*;
import java.util.concurrent.SynchronousQueue;

public class Master {
    // reader thread expects a buffered reader object which connects it to the slave and expects access to done list
    // writer expects a printwriter object. -- writer.setJob(String)

    public static void main(String[] args) throws IOException {

        // Hard code in port number if necessary:
        args = new String[] { "30121" };

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
             //
             Socket SlaveA = serverSocket.accept();
             Socket SlaveB = serverSocket.accept();

             PrintWriter SLaveAOutWriter = new PrintWriter(SlaveA.getOutputStream(), true);
             PrintWriter SLaveBOutWriter = new PrintWriter(SlaveB.getOutputStream(), true);

             BufferedReader SlaveAInReader = new BufferedReader(new InputStreamReader(SlaveA.getInputStream()));
             BufferedReader SlaveBInReader = new BufferedReader(new InputStreamReader(SlaveB.getInputStream()));
        ) {

            //Packet Creator creates packets with assigned indexes
            packetList = packetCreator.createPackets();
            //Create the last packet with special index and total size as data
            Packet lastPacket = new Packet(-1, String.valueOf(packetList.size()));

            while (true) {

                System.out.println("Connected to client.");

                //Creates list of shuffled packets with a 80% chance of being "dropped"
                ArrayList<Packet> toSend = eightyPercentList(packetList);
                //Add the last packet to the end of the list
                toSend.add(lastPacket);

                System.out.println("Sending packets");

                for (Packet p : toSend ) {
                    outWriter.println(p.toStringIndex());
                    outWriter.println(p.toStringData());
                }

                System.out.println("Packets Sent");

                //Listens for a message from the client to see if it is done sending packets
                if ((inReader.readLine()).equals("FINISHED")) {
                    break;
                }

                //If the client has not received all packets, server resets and sends packets again
                System.out.println("Restarting");
            }
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    //Method to "drop" 80% of packets
    private static ArrayList<Packet> eightyPercentList(ArrayList<Packet> packets){
        Random rand = new Random();
        ArrayList<Packet> batch = new ArrayList<>();

        for (Packet packet : packets) {
            if (rand.nextInt(10)<8)
                batch.add(packet);
        }
        return batch;
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
