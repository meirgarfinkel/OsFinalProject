import java.net.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.io.*;

public class SimpleServer {

    //String to send to the client
    static String data = """
			Look, I didn't want to be a half-blood.
			If you're reading this because you think you might be one, my
			advice is: close this book right now. Believe whatever lie your
			mom or dad told you about your birth, and try to lead a normal
			life.
			Being a half-blood is dangerous. It's scary. Most of the time, it
			gets you killed in painful, nasty ways.
			If you're a normal kid, reading this because you think it's fiction,
			great. Read on. I envy you for being able to believe that none of
			this ever happened.
			But if you recognize yourself in these pages-if you feel something
			stirring inside-stop reading immediately. You might be one of us.
			And once you know that, it's only a matter of time before they
			sense it too, and they'll come for you.
			Don't say I didn't warn you.
			My name is Percy Jackson.""";

    //Creates a Packet Creator with the data given
    static PacketCreator packetCreator = new PacketCreator(data);
    static ArrayList<Packet> packetList;

    public static void main(String[] args) throws IOException {

        // Hard code in port number if necessary:
        args = new String[] { "30121" };

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
             Socket clientSocket1 = serverSocket.accept();
             PrintWriter outWriter = new PrintWriter(clientSocket1.getOutputStream(), true);
             BufferedReader inReader= new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()))
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
}
