import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@SuppressWarnings("unchecked")
public class Client {
    public static void main(String[] args) {
        try {
            Scanner s = new Scanner(System.in);
            System.out.println("Enter the IP Address of the server: ");
            String ipAddress = s.next();
            Socket socket = new Socket(ipAddress, 12345); // Connect to the server on port 12345

            // Create input and output streams
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Enter the number of processes: ");
            int numProcesses = s.nextInt();

            // Generate a list of DataPacket objects with task descriptions and data
            List<DataPacket> dataPackets = generateDataPackets(numProcesses);

            // Send half of the data packets to the server for parallel processing
            int half = dataPackets.size() / 2;
            List<DataPacket> clientData = dataPackets.subList(0, half);
            out.writeObject(clientData);
            out.flush();

            // Perform the other half of calculations on the client side
            List<Integer> clientResults = new ArrayList<>();
            for (DataPacket packet : dataPackets.subList(half, dataPackets.size())) {
                String threadName = Thread.currentThread().getName();
                System.out.println("Client processing on thread: " + threadName);
                int result = packet.calculateFactorial();
                clientResults.add(result);
            }

            // Receive the results from the server
            List<Integer> serverResults = (List<Integer>) in.readObject();
            System.out.println("Received results from server: " + serverResults);

            // Combine results from the client and server
            clientResults.addAll(serverResults);

            // Close the connections
            in.close();
            out.close();
            s.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<DataPacket> generateDataPackets(int count) {
        List<DataPacket> dataPackets = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            int num = random.nextInt(10) + 1; // Generate random numbers between 1 and 10
            dataPackets.add(new DataPacket("Calculate Factorial", num));
        }

        return dataPackets;
    }
}
