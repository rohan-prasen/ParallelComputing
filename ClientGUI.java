import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@SuppressWarnings("unchecked")
public class ClientGUI {
    private JFrame frame;
    private JTextField ipAddressField;
    private JTextField numProcessesField;
    private JButton startButton;
    private JTextArea outputArea;

    public ClientGUI() {
        frame = new JFrame("Client GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel ipLabel = new JLabel("Server IP Address:");
        ipAddressField = new JTextField("localhost");
        JLabel numProcessesLabel = new JLabel("Number of Processes:");
        numProcessesField = new JTextField("5");
        startButton = new JButton("Start Client");
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startClient();
            }
        });

        panel.add(ipLabel);
        panel.add(ipAddressField);
        panel.add(numProcessesLabel);
        panel.add(numProcessesField);
        panel.add(startButton);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void startClient() {
        String ipAddress = ipAddressField.getText();
        int numProcesses = Integer.parseInt(numProcessesField.getText());

        outputArea.setText(""); // Clear the output area

        // Run client operations in a separate thread
        Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(ipAddress, 12345); // Connect to the server on port 12345

                    // Create input and output streams
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    // Generate a list of DataPacket objects with task descriptions and data
                    List<DataPacket> dataPackets = generateDataPackets(numProcesses);

                    // Send half of the data packets to the server for parallel processing
                    int half = dataPackets.size() / 2;
                    List<DataPacket> clientData = new ArrayList<>(dataPackets.subList(0, half));
                    out.writeObject(clientData);
                    out.flush();

                    // Perform the other half of calculations on the client side using parallel threads
                    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                    List<Future<Integer>> clientResults = new ArrayList<>();

                    outputArea.append("Client: Implementing Parallel Computing Approach...\n");

                    for (DataPacket packet : dataPackets.subList(half, dataPackets.size())) {
                        Future<Integer> result = executor.submit(() -> {
                            // Get the name of the current thread on the client and display it
                            String threadName = Thread.currentThread().getName();
                            outputArea.append("Client processing on thread: " + threadName + "\n");
                            return packet.calculateFactorial();
                        });
                        clientResults.add(result);
                    }

                    // Wait for all client-side calculations to complete
                    executor.shutdown();
                    executor.awaitTermination(30, TimeUnit.SECONDS);

                    // Receive the results from the server
                    List<Integer> serverResults = (List<Integer>) in.readObject();
                    outputArea.append("Received results from server: " + serverResults + "\n");

                    // Combine results from the client and server
                    List<Integer> allResults = new ArrayList<>();
                    for (Future<Integer> result : clientResults) {
                        allResults.add(result.get());
                    }
                    allResults.addAll(serverResults);

                    // Display the combined results
                    outputArea.append("Combined results: " + allResults + "\n");

                    // Close the connections
                    in.close();
                    out.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        clientThread.start();
    }

    private List<DataPacket> generateDataPackets(int count) {
        List<DataPacket> dataPackets = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            int num = random.nextInt(10) + 1; // Generate random numbers between 1 and 10
            dataPackets.add(new DataPacket("Calculate Factorial", num));
        }

        return dataPackets;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });
    }
}
