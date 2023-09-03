import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SuppressWarnings("unchecked")
public class ServerGUI {
    private JFrame frame;
    private JButton startButton;
    private JTextArea outputArea;

    public ServerGUI() {
        frame = new JFrame("Server GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        startButton = new JButton("Start Server");
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        panel.add(startButton);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane);

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void startServer() {
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Server socket listening on port 12345
                    ServerSocket serverSocket = new ServerSocket(12345);
                    outputArea.append("Server is waiting for a connection...\n");

                    while (true) {
                        // Accept client connection
                        Socket clientSocket = serverSocket.accept();
                        outputArea.append("Connected to client: " + clientSocket.getInetAddress() + "\n");

                        // Create input and output streams
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                        // Receive half of the list of data packets from the client
                        List<DataPacket> clientData = (List<DataPacket>) in.readObject();

                        // Process the other half of data packets on the server in parallel
                        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                        List<Future<Integer>> results = new ArrayList<>();

                        outputArea.append("Implementing Parallel Computing Approach...\n");

                        for (DataPacket packet : clientData) {
                            Future<Integer> result = executor.submit(() -> {
                                // Get the name of the current thread on the server and display it
                                String threadName = Thread.currentThread().getName();
                                outputArea.append("Server processing on thread: " + threadName + "\n");

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
                        outputArea.append("Task completed on the server. Results sent to client.\n");

                        // Close the connections
                        in.close();
                        out.close();
                        clientSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerGUI();
            }
        });
    }
}
