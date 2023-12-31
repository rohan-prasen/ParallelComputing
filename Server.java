import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SuppressWarnings("unchecked")
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345); // Server socket listening on port 12345

            System.out.println("Server is waiting for a connection...");

            // Accept client connection
            Socket clientSocket = serverSocket.accept();

            System.out.println("Connected to client: " + clientSocket.getInetAddress());

            // Create input and output streams
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            // Receive half of the list of data packets from the client
            List<DataPacket> clientData = (List<DataPacket>) in.readObject();

            // Process the other half of data packets on the server in parallel
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future<Integer>> results = new ArrayList<>();

            // Indicate that parallel computing is being used on the server
            System.out.println("Server: Implementing Parallel Computing Approach...");

            for (DataPacket packet : clientData) {
                Future<Integer> result = executor.submit(() -> {
                    // Get the name of the current thread on the server and display it
                    String threadName = Thread.currentThread().getName();
                    System.out.println("Server processing on thread: " + threadName);

                    // Calculate the factorial using the DataPacket's method
                    return packet.calculateFactorial();
                });
                results.add(result);
            }

            // Wait for all calculations to complete
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS); // Wait up to 30 seconds

            // Retrieve and send results to the client
            List<Integer> resultList = new ArrayList<>();
            for (Future<Integer> result : results) {
                resultList.add(result.get());
            }

            out.writeObject(resultList);
            out.flush();
            System.out.println("Task completed on the server. Results sent to client.");

            // Close the connections
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
