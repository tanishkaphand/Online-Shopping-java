package com.Onlineshop.app;

		import java.sql.*;
		import java.util.*;
		import java.io.*;

		// Multithreading class to simulate background tasks every 10 minutes
		class AutoSaveThread extends Thread {
		    public void run() {
		        try {
		            while (true) {
		                Thread.sleep(10 * 60 * 1000); // 10 minutes
		                System.out.println("[AutoSaveThread] 📝 Autosave triggered...");
		                // Add backup logic if needed
		            }
		        } catch (InterruptedException e) {
		            System.out.println("[AutoSaveThread] Interrupted.");
		        }
		    }
		}

		public class Onlineshoppingsystem {

		    static Scanner sc = new Scanner(System.in);

		    public static void main(String[] args) {
		        new AutoSaveThread().start(); // Start background thread

		        while (true) {
		            System.out.println("\n===== ONLINE SHOPPING SYSTEM =====");
		            System.out.println("1. Add Product");
		            System.out.println("2. View All Products");
		            System.out.println("3. Place Order");
		            System.out.println("4. View Orders");
		            System.out.println("5. Export Orders to File");
		            System.out.println("6. Exit");
		            System.out.print("Enter choice: ");
		            int ch = sc.nextInt();

		            switch (ch) {
		                case 1: addProduct(); break;
		                case 2: viewProducts(); break;
		                case 3: placeOrder(); break;
		                case 4: viewOrders(); break;
		                case 5: exportOrdersToFile(); break;
		                case 6:
		                    System.out.println("Exiting... Thank you!");
		                    System.exit(0);
		                default:
		                    System.out.println("Invalid choice.");
		            }
		        }
		    }

		    // 1. Add product
		    public static void addProduct() {
		        System.out.print("Enter product name: ");
		        sc.nextLine(); // Consume leftover newline
		        String name = sc.nextLine();
		        System.out.print("Enter price: ");
		        double price = sc.nextDouble();
		        System.out.print("Enter quantity: ");
		        int qty = sc.nextInt();

		        try (Connection conn = DBConnection.getConnection()) {
		            String sql = "INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)";
		            PreparedStatement ps = conn.prepareStatement(sql);
		            ps.setString(1, name);
		            ps.setDouble(2, price);
		            ps.setInt(3, qty);
		            ps.executeUpdate();
		            System.out.println("✅ Product added!");
		        } catch (SQLException e) {
		            System.out.println("Error adding product: " + e.getMessage());
		        }
		    }

		    // 2. View all products
		    public static void viewProducts() {
		        try (Connection conn = DBConnection.getConnection()) {
		            Statement stmt = conn.createStatement();
		            ResultSet rs = stmt.executeQuery("SELECT * FROM products");
		            System.out.println("----- Product List -----");
		            while (rs.next()) {
		                System.out.println("ID: " + rs.getInt("id") +
		                                   ", Name: " + rs.getString("name") +
		                                   ", Price: " + rs.getDouble("price") +
		                                   ", Quantity: " + rs.getInt("quantity"));
		            }
		        } catch (SQLException e) {
		            System.out.println("Error viewing products: " + e.getMessage());
		        }
		    }

		    // 3. Place an order
		    public static void placeOrder() {
		        System.out.print("Enter product ID to order: ");
		        int pid = sc.nextInt();
		        System.out.print("Enter quantity: ");
		        int qty = sc.nextInt();

		        try (Connection conn = DBConnection.getConnection()) {
		            PreparedStatement ps1 = conn.prepareStatement("SELECT quantity FROM products WHERE id = ?");
		            ps1.setInt(1, pid);
		            ResultSet rs = ps1.executeQuery();

		            if (rs.next()) {
		                int availableQty = rs.getInt("quantity");
		                if (availableQty >= qty) {
		                    PreparedStatement ps2 = conn.prepareStatement("INSERT INTO orders (product_id, quantity) VALUES (?, ?)");
		                    ps2.setInt(1, pid);
		                    ps2.setInt(2, qty);
		                    ps2.executeUpdate();

		                    PreparedStatement ps3 = conn.prepareStatement("UPDATE products SET quantity = quantity - ? WHERE id = ?");
		                    ps3.setInt(1, qty);
		                    ps3.setInt(2, pid);
		                    ps3.executeUpdate();

		                    System.out.println("✅ Order placed!");
		                } else {
		                    System.out.println("❌ Not enough stock!");
		                }
		            } else {
		                System.out.println("❌ Product not found.");
		            }
		        } catch (SQLException e) {
		            System.out.println("Error placing order: " + e.getMessage());
		        }
		    }

		    // 4. View orders
		    public static void viewOrders() {
		        try (Connection conn = DBConnection.getConnection()) {
		            String sql = "SELECT o.id, p.name, o.quantity, o.order_time " +
		                         "FROM orders o JOIN products p ON o.product_id = p.id";
		            Statement stmt = conn.createStatement();
		            ResultSet rs = stmt.executeQuery(sql);

		            System.out.println("----- Orders List -----");
		            while (rs.next()) {
		                System.out.println("Order ID: " + rs.getInt("id") +
		                                   ", Product: " + rs.getString("name") +
		                                   ", Qty: " + rs.getInt("quantity") +
		                                   ", Time: " + rs.getTimestamp("order_time"));
		            }
		        } catch (SQLException e) {
		            System.out.println("Error viewing orders: " + e.getMessage());
		        }
		    }

		    // 5. Export orders to file
		    public static void exportOrdersToFile() {
		        try (Connection conn = DBConnection.getConnection();
		             PrintWriter pw = new PrintWriter(new FileWriter("orders.txt"))) {

		            String sql = "SELECT o.id, p.name, o.quantity, o.order_time " +
		                         "FROM orders o JOIN products p ON o.product_id = p.id";
		            Statement stmt = conn.createStatement();
		            ResultSet rs = stmt.executeQuery(sql);

		            pw.println("OrderID | Product | Quantity | Time");
		            pw.println("--------------------------------------------");

		            while (rs.next()) {
		                pw.println(rs.getInt("id") + " | " +
		                           rs.getString("name") + " | " +
		                           rs.getInt("quantity") + " | " +
		                           rs.getTimestamp("order_time"));
		            }

		            System.out.println("✅ Orders exported to 'orders.txt'");
		        } catch (Exception e) {
		            System.out.println("Error exporting orders: " + e.getMessage());
		        }
		    }
		


	}


