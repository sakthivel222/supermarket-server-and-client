import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class BillingClient extends JFrame implements ActionListener {
    JTextField productField;
    JTextArea outputArea;
    JButton getBtn;

    public BillingClient() {
        setTitle("Supermarket Billing (Client)");
        setSize(400, 300);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.add(new JLabel("Enter Product:"));
        productField = new JTextField(10);
        panel.add(productField);

        getBtn = new JButton("Get Bill");
        panel.add(getBtn);
        add(panel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        getBtn.addActionListener(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        try (Socket socket = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(productField.getText());
            String response = in.readLine();
            outputArea.append(response + "\n");
        } catch (Exception ex) {
            outputArea.append("Error: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        new BillingClient();
    }
}
