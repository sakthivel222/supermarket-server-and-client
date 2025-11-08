import java.io.*;
import java.net.*;
import java.sql.*;

public class BillingServer {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/supermarket?useSSL=false&serverTimezone=UTC";
        String user = "root";          // your MySQL username
        String pass = "12345";         // your MySQL password

        try (ServerSocket server = new ServerSocket(5000)) {
            System.out.println("Server started... Waiting for clients...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            while (true) {
                Socket client = server.accept();
                new Thread(() -> handleClient(client, con)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket client, Connection con) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true)
        ) {
            String product = in.readLine();
            if (product == null) return;

            PreparedStatement ps = con.prepareStatement("SELECT price FROM products WHERE name=?");
            ps.setString(1, product.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double price = rs.getDouble("price");
                double gst = price * 0.18;
                double total = price + gst;
                out.println("Product: " + product + " | Price: " + price +
                            " | GST: " + gst + " | Total: " + total);
            } else {
                out.println("Product not found!");
            }
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
