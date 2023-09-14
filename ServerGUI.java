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
    private JPanel panel;
    private JButton resetButton;
    private JTextField numClientsField; // Smaller text field for specifying the number of clients
    private int numClients = 0;

    public ServerGUI() {
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Server GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 750);

        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1)); // Adjust layout to accommodate the input field

        startButton = new JButton("Start Server");
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        // Create a label for specifying the number of clients
        JLabel numClientsLabel = new JLabel("Number of Clients:");

        // Create a smaller text field for inputting the number of clients
        numClientsField = new JTextField(2); // Adjust the size as needed

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the number of clients from the input field
                String numClientsStr = numClientsField.getText();
                try {
                    numClients = Integer.parseInt(numClientsStr);
                    startServer();
                } catch (NumberFormatException ex) {
                    outputArea.append("Invalid number of clients. Please enter a valid number.\n");
                }
            }
        });

        panel.add(numClientsLabel);
        panel.add(numClientsField); // Add the smaller text field
        panel.add(startButton);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane);

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        // Initialize the reset button, but don't add it to the panel yet
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the output area
                outputArea.setText("");
            }
        });

        frame.setVisible(true);
    }

    private void startServer() {
        // Add the reset button to the panel
        panel.add(resetButton);
        frame.validate();

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Server socket listening on port 12345
                    String serverIPAddress = InetAddress.getLocalHost().getHostAddress();
                    ServerSocket serverSocket = new ServerSocket(12345);
                    outputArea.append("Server's IP Address: /" + serverIPAddress + "\n");
                    outputArea.append("Waiting for " + numClients + " client connections...\n");

                    // Counter for tracking connected clients
                    int connectedClients = 0;

                    while (connectedClients < numClients) {
                        // Accept client connection
                        Socket clientSocket = serverSocket.accept();
                        outputArea.append("Connected to client: " + clientSocket.getInetAddress() + "\n");
                        connectedClients++;

                        // Create input and output streams
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                        // Receive data from the client and process it as needed

                        // Close the connections for this client
                        in.close();
                        out.close();
                        clientSocket.close();
                    }

                    outputArea.append("All clients connected and processed.\n");

                    // Server has finished processing all clients, so it can close now

                    serverSocket.close();
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
