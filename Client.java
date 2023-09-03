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

            // Generate a list of DataPacket objects with task descriptions and data
            List<DataPacket> dataPackets = generateDataPackets(5); // You can change the number of calculations

            // Send the list of data packets to the server for parallel processing
            out.writeObject(dataPackets);
            out.flush();

            // Receive the results from the server
            List<Integer> resultList = (List<Integer>) in.readObject();
            System.out.println("Received results from server: " + resultList);

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