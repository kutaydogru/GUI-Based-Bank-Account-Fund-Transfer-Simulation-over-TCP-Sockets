package server.net;

import server.services.AuthenticationService;
import server.services.TransactionService;
import common.Account;
import common.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private AuthenticationService authService;
    private TransactionService txService;
    private String loggedInUsername = null;
    private String loggedInFullName = null;
    private Gson gson = new Gson();

    public ClientHandler(Socket socket, AuthenticationService authService, TransactionService txService) {
        this.socket = socket;
        this.authService = authService;
        this.txService = txService;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println("New client connected: " + socket.getInetAddress()
                    + " Thread: " + Thread.currentThread().getId());

            out.println("{\"msg\": \"Welcome! Please LOGIN or REGISTER\"}");

            String line;
            while ((line = in.readLine()) != null) {
                String response = handleCommand(line);
                out.println(response);
                if (isQuit(line)) {
                    System.out.println("Client disconnected: " + socket.getInetAddress()
                            + " Thread: " + Thread.currentThread().getId());
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Client connection error: " + e.getMessage());
        } finally {
            // Ensure user is logged out when connection terminates unexpectedly
            if (loggedInUsername != null) {
                authService.logout(loggedInUsername);
                System.out.println("User " + loggedInUsername + " session closed due to disconnect.");
            }
        }
    }

    private boolean isQuit(String jsonLine) {
        try {
            JsonObject obj = gson.fromJson(jsonLine, JsonObject.class);
            String command = obj.get("command").getAsString();
            return command.equalsIgnoreCase("QUIT");
        } catch (Exception ignored) { return false; }
    }

    private String handleCommand(String line) {
        try {
            JsonObject req = gson.fromJson(line, JsonObject.class);
            String cmd = req.get("command").getAsString().toUpperCase();

            switch (cmd) {
                case "REGISTER":
                    String regUser = req.get("username").getAsString();
                    String regPass = req.get("password").getAsString();
                    String regName = req.get("fullname").getAsString();
                    String regRes = authService.register(regUser, regPass, regName);
                    if (regRes.startsWith("OK")) {
                        System.out.println("User registered: " + regUser + " (" + socket.getInetAddress() + ")");
                        JsonObject res = new JsonObject();
                        res.addProperty("status", "OK");
                        res.addProperty("fullname", regName);
                        return res.toString();
                    } else {
                        JsonObject res = new JsonObject();
                        res.addProperty("status", "ERROR");
                        res.addProperty("error", regRes);
                        return res.toString();
                    }

                case "LOGIN":
                    String username = req.get("username").getAsString();
                    String password = req.get("password").getAsString();
                    String loginResult = authService.login(username, password);
                    if (loginResult.startsWith("OK:")) {
                        loggedInUsername = username;
                        loggedInFullName = loginResult.substring(3);
                        System.out.println("User " + loggedInUsername + " logged in (" + socket.getInetAddress() + ")");
                        JsonObject res = new JsonObject();
                        res.addProperty("status", "OK");
                        res.addProperty("fullname", loggedInFullName);
                        return res.toString();
                    } else {
                        JsonObject res = new JsonObject();
                        res.addProperty("status", "ERROR");
                        String errorMsg = loginResult.replace("ERROR:", "");
                        // User-friendly error message
                        if ("alreadyloggedin".equals(errorMsg)) {
                            res.addProperty("error", "This account is already logged in from another session");
                        } else {
                            res.addProperty("error", errorMsg);
                        }
                        return res.toString();
                    }

                case "LOGOUT":
                    if (loggedInUsername != null) {
                        System.out.println("User " + loggedInUsername + " logged out (" + socket.getInetAddress() + ")");
                        authService.logout(loggedInUsername);
                        loggedInUsername = null;
                        loggedInFullName = null;
                    }
                    JsonObject logout = new JsonObject();
                    logout.addProperty("status", "OK");
                    return logout.toString();

                case "LIST":
                    if (!isLoggedIn()) return error("notloggedin");
                    List<Account> accs = txService.listAccounts(loggedInUsername);
                    JsonObject accres = new JsonObject();
                    JsonArray arr = new JsonArray();
                    for (Account acc : accs) {
                        JsonObject ob = new JsonObject();
                        ob.addProperty("accountNo", acc.getAccountNo());
                        ob.addProperty("balance", acc.getBalance());
                        arr.add(ob);
                    }
                    accres.add("accounts", arr);
                    return accres.toString();

                case "CREATE":
                    if (!isLoggedIn()) return error("notloggedin");
                    String accNo = req.get("accountNo").getAsString();
                    double balance = req.get("balance").getAsDouble();
                    Account created = txService.createAccount(accNo, balance, loggedInFullName, loggedInUsername);
                    if (created != null) {
                        System.out.println("User " + loggedInUsername + " created account " + accNo);
                        JsonObject res = new JsonObject();
                        res.addProperty("status", "OK");
                        return res.toString();
                    } else {
                        return error("create");
                    }

                case "TRANSFER":
                    if (!isLoggedIn()) return error("notloggedin");
                    String fromAccNo = req.get("fromAccount").getAsString();
                    String toAccNo = req.get("toAccount").getAsString();
                    double amt = req.get("amount").getAsDouble();
                    String transferResult = txService.transfer(fromAccNo, toAccNo, amt, loggedInUsername);
                    if ("OK".equals(transferResult)) {
                        System.out.println("User " + loggedInUsername + " sent " + amt + " from " + fromAccNo + " to " + toAccNo);
                        JsonObject res = new JsonObject();
                        res.addProperty("status", "OK");
                        return res.toString();
                    } else {
                        return error(transferResult.replace("ERROR:",""));
                    }
                case "WITHDRAW":
                    // Withdraw money from an account (only owner can do)
                    if (!isLoggedIn()) return error("notloggedin");
                    String withdrawAcc = req.get("accountNo").getAsString();
                    double withdrawAmt = req.get("amount").getAsDouble();
                    String withdrawResult = txService.withdraw(withdrawAcc, withdrawAmt, loggedInUsername);
                    if ("OK".equals(withdrawResult)) {
                        System.out.println("User " + loggedInUsername + " withdrew " + withdrawAmt + " from " + withdrawAcc);
                        JsonObject res = new JsonObject();
                        res.addProperty("status", "OK");
                        return res.toString();
                    } else {
                        return error(withdrawResult.replace("ERROR:", ""));
                    }

                case "DEPOSIT":
                    // Deposit money to an account (only owner can do)
                    if (!isLoggedIn()) return error("notloggedin");
                    String depositAcc = req.get("accountNo").getAsString();
                    double depositAmt = req.get("amount").getAsDouble();
                    String depositResult = txService.deposit(depositAcc, depositAmt, loggedInUsername);
                    if ("OK".equals(depositResult)) {
                        System.out.println("User " + loggedInUsername + " deposited " + depositAmt + " to " + depositAcc);
                        JsonObject res = new JsonObject();
                        res.addProperty("status", "OK");
                        return res.toString();
                    } else {
                        return error(depositResult.replace("ERROR:", ""));
                    }

                case "BALANCE":
                    if (!isLoggedIn()) return error("notloggedin");
                    String balAccNo = req.get("accountNo").getAsString();
                    String balResult = txService.getBalance(balAccNo, loggedInUsername);
                    try {
                        double balanceAns = Double.parseDouble(balResult);
                        JsonObject res = new JsonObject();
                        res.addProperty("balance", balanceAns);
                        return res.toString();
                    } catch (Exception e) {
                        return error(balResult.replace("ERROR:",""));
                    }

                case "QUIT":
                    if (loggedInUsername != null) {
                        authService.logout(loggedInUsername);
                    }
                    System.out.println("Client disconnected: " + socket.getInetAddress());
                    return "{\"status\":\"BYE\"}";

                default:
                    return error("unknowncommand");
            }
        } catch (Exception e) {
            return error("internal");
        }
    }

    private boolean isLoggedIn() {
        return loggedInUsername != null;
    }
    private String error(String msg) {
        JsonObject o = new JsonObject();
        o.addProperty("status", "ERROR");
        o.addProperty("error", msg);
        return o.toString();
    }
}