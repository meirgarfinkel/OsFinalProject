import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client2 {
    public static void main(String[] args) {

        args = new String[]{"127.0.0.1", "30121"};

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        //Creates a PrintWrite and a BufferedReader in order to be able to send jobs
        // and receive completed jobs from the Master
        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter masterWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader masterReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader jobReader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Client 2 is active...");
            while (true) {
                String userInput;
                String serverResponse;

                while ((userInput = jobReader.readLine()) != null) {
                    masterWriter.println(userInput); // send request to server
                    serverResponse = masterReader.readLine();
                    System.out.println("Client 2 received completed job: " + serverResponse);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
