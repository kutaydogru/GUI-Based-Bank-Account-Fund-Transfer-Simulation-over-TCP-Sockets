import client.net.ClientNetworkManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Scanner;

public class MainClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Gson gson = new Gson();

        while (true) {
            ClientNetworkManager cnm = new ClientNetworkManager();
            String ip = "192.168.137.179";
            int port = 5000;
            if (!cnm.connect(ip, port)) {
                System.out.println("Could not connect to server. Exiting.");
                return;
            }
            try {
                String welcome = cnm.readLine();
                System.out.println("[SERVER] " + welcome);

                boolean loggedIn = false;
                while (!loggedIn) {
                    System.out.println("--- BANK CLIENT ---");
                    System.out.println("1. Login");
                    System.out.println("2. Register");
                    System.out.println("0. Exit");
                    System.out.print("Choose: ");
                    String choice = scanner.nextLine().trim();

                    if (choice.equals("1")) { // LOGIN
                        System.out.print("Username: ");
                        String username = scanner.nextLine();
                        System.out.print("Password: ");
                        String password = scanner.nextLine();
                        JsonObject obj = new JsonObject();
                        obj.addProperty("command", "LOGIN");
                        obj.addProperty("username", username);
                        obj.addProperty("password", password);
                        cnm.sendLine(obj.toString());
                        String resp = cnm.readLine();
                        JsonObject jresp = gson.fromJson(resp, JsonObject.class);
                        if (jresp.has("status") && jresp.get("status").getAsString().equals("OK")) {
                            System.out.println("Login successful. Welcome, " + jresp.get("fullname").getAsString() + "!");
                            loggedIn = true;
                        } else {
                            System.out.println("Login error: " + jresp.get("error").getAsString());
                        }
                    } else if (choice.equals("2")) { // REGISTER
                        System.out.print("Choose a username: ");
                        String username = scanner.nextLine();
                        System.out.print("Choose a password: ");
                        String password = scanner.nextLine();
                        System.out.print("Full name: ");
                        String fullname = scanner.nextLine();
                        JsonObject obj = new JsonObject();
                        obj.addProperty("command", "REGISTER");
                        obj.addProperty("username", username);
                        obj.addProperty("password", password);
                        obj.addProperty("fullname", fullname);
                        cnm.sendLine(obj.toString());
                        String resp = cnm.readLine();
                        JsonObject jresp = gson.fromJson(resp, JsonObject.class);
                        if (jresp.has("status") && jresp.get("status").getAsString().equals("OK")) {
                            System.out.println("Registration successful. Welcome, " + fullname + "!");
                            loggedIn = true;
                        } else {
                            System.out.println("Registration error: " + jresp.get("error").getAsString());
                        }
                    } else if (choice.equals("0")) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("command", "QUIT");
                        cnm.sendLine(obj.toString());
                        System.out.println("Goodbye.");
                        scanner.close();
                        cnm.close();
                        return;
                    } else {
                        System.out.println("Invalid option. Try again.");
                    }
                }

                // MAIN MENU
                boolean quitOrLogout = false;
                while (!quitOrLogout) {
                    System.out.println("\n--- MAIN MENU ---");
                    System.out.println("1. List Accounts");
                    System.out.println("2. Open New Account");
                    System.out.println("3. Transfer Money");
                    System.out.println("4. Check Account Balance");
                    System.out.println("5. Log Out");
                    System.out.println("0. Exit");
                    System.out.print("Choose: ");
                    String action = scanner.nextLine().trim();
                    JsonObject obj = new JsonObject();
                    JsonObject jresp;
                    String resp;

                    switch (action) {
                        case "1": // LIST
                            obj.addProperty("command", "LIST");
                            cnm.sendLine(obj.toString());
                            resp = cnm.readLine();
                            jresp = gson.fromJson(resp, JsonObject.class);
                            if (jresp.has("accounts")) {
                                System.out.println("--- Your Accounts ---");
                                for (int i = 0; i < jresp.getAsJsonArray("accounts").size(); i++) {
                                    JsonObject a = jresp.getAsJsonArray("accounts").get(i).getAsJsonObject();
                                    System.out.println((i+1) + ". Account No: " + a.get("accountNo").getAsString()
                                            + " | Balance: " + a.get("balance").getAsDouble());
                                }
                            } else {
                                System.out.println("Error: " + jresp.get("error").getAsString());
                            }
                            break;

                        case "2": // CREATE
                            System.out.print("New account number: ");
                            String accNo = scanner.nextLine();
                            System.out.print("Initial balance: ");
                            String bal = scanner.nextLine();
                            obj.addProperty("command", "CREATE");
                            obj.addProperty("accountNo", accNo);
                            obj.addProperty("balance", bal);
                            cnm.sendLine(obj.toString());
                            resp = cnm.readLine();
                            jresp = gson.fromJson(resp, JsonObject.class);
                            if (jresp.has("status") && jresp.get("status").getAsString().equals("OK")) {
                                System.out.println("Account created successfully.");
                            } else {
                                System.out.println("Failed to create account: " + jresp.get("error").getAsString());
                            }
                            break;

                        case "3": // TRANSFER
                            System.out.print("Your account number: ");
                            String from = scanner.nextLine();
                            System.out.print("Recipient account number: ");
                            String to = scanner.nextLine();
                            System.out.print("Amount: ");
                            String amount = scanner.nextLine();
                            obj.addProperty("command", "TRANSFER");
                            obj.addProperty("fromAccount", from);
                            obj.addProperty("toAccount", to);
                            obj.addProperty("amount", amount);
                            cnm.sendLine(obj.toString());
                            resp = cnm.readLine();
                            jresp = gson.fromJson(resp, JsonObject.class);
                            if (jresp.has("status") && jresp.get("status").getAsString().equals("OK")) {
                                System.out.println("Transfer successful.");
                            } else {
                                System.out.println("Error: " + jresp.get("error").getAsString());
                            }
                            break;

                        case "4": // BALANCE
                            System.out.print("Enter account number: ");
                            String accForBal = scanner.nextLine();
                            obj.addProperty("command", "BALANCE");
                            obj.addProperty("accountNo", accForBal);
                            cnm.sendLine(obj.toString());
                            resp = cnm.readLine();
                            jresp = gson.fromJson(resp, JsonObject.class);
                            if (jresp.has("balance")) {
                                System.out.println("Balance: " + jresp.get("balance").getAsDouble());
                            } else {
                                System.out.println("Error: " + jresp.get("error").getAsString());
                            }
                            break;

                        case "5": // LOGOUT
                            obj.addProperty("command", "LOGOUT");
                            cnm.sendLine(obj.toString());
                            System.out.println("Logged out.");
                            quitOrLogout = true;
                            break;

                        case "0": // EXIT
                            obj.addProperty("command", "QUIT");
                            cnm.sendLine(obj.toString());
                            scanner.close();
                            cnm.close();
                            return;

                        default:
                            System.out.println("Invalid option. Try again.");
                    }
                }
                cnm.close();
            } catch (Exception e) {
                System.out.println("Disconnected: " + e.getMessage());
                cnm.close();
            }
        }
    }
}