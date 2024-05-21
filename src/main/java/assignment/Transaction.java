package assignment;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Scanner;

@SuppressWarnings({ "resource", "unused" })
public class Transaction {
    private static final AtomicInteger transactionCounter = new AtomicInteger(getLastTransactionID() + 1);

    public static void earnPoints() {
        Scanner scanner = new Scanner(System.in);
        Customer customer = new Customer();
    
        String customerID = ""; 
        boolean isAuthenticated = false;
    
        while (!isAuthenticated) {
            System.out.println("Enter Customer ID or type CANCEL to quit: ");
            customerID = scanner.nextLine();
            
            if (customerID.equalsIgnoreCase("CANCEL")) {
                System.out.println("Transaction canceled.");
                return;
            }
            
            isAuthenticated = customer.checkCustomer(customerID);
            if (!isAuthenticated) {
                System.out.println("Customer not found. Please enter a valid Customer ID or type CANCEL to quit.");
            }
        }
    
        double transactionAmount = 0.0;
    
        boolean validAmount = false;
        while (!validAmount) {
            try {
                System.out.println("Enter Transaction Amount: ");
                String amountInput = scanner.nextLine();
                
                if (amountInput.equalsIgnoreCase("CANCEL")) {
                    System.out.println("Transaction canceled.");
                    return;
                }
                
                transactionAmount = Double.parseDouble(amountInput);
                validAmount = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for the transaction amount or type CANCEL to quit.");
            }
        }
    
        System.out.println("Enter Voucher Code (if any): ");
        String voucherCode = scanner.nextLine();
    
        double deductedAmount = 0.0;
        if (!voucherCode.isEmpty()) {
            try {
                deductedAmount = checkVoucher(voucherCode, transactionAmount);
                System.out.println("Voucher code applied successfully. Deducted amount: " + deductedAmount);
                transactionAmount -= deductedAmount;
            } catch (IOException e) {
                System.out.println("Error processing voucher: " + e.getMessage());
                return;
            }
        }
    
        int pointsGained = (int) (transactionAmount * 10);
    
        int oldPoints = customer.getBalance(customerID);
        int newPoints = oldPoints + pointsGained;
        boolean isPointsUpdated = customer.setPoints(customerID, newPoints);
    
        if (isPointsUpdated) {
            System.out.println("Points updated successfully.");
        } else {
            System.out.println("Failed to update points.");
            return;
        }
    
        String transactionID = generateUniqueTransactionID();
    
        saveTransactionHistory(transactionID, customerID, transactionAmount);
    
        System.out.println("Transaction ID: " + transactionID);
        System.out.println("Points Gained: " + pointsGained);
    }
    

    private static double checkVoucher(String voucherCode, double transactionAmount) throws IOException {
        double deductedAmount = 0.0;
        boolean voucherFound = false;
        StringBuilder voucherData = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("voucher.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(voucherCode)) {
                    voucherFound = true;
                    if (parts.length == 3) {
                        System.out.println("Voucher code has already been redeemed. Press 0 to continue without voucher or Press Enter to use another voucher:");
                        Scanner scanner = new Scanner(System.in);
                        String choice = scanner.nextLine();
                        if (choice.equals("0")) {
                            return deductedAmount;
                        } else {
                            System.out.println("Enter another voucher code:");
                            String newVoucherCode = scanner.nextLine();
                            return checkVoucher(newVoucherCode, transactionAmount);
                        }
                    }
                    double value = Double.parseDouble(parts[1]);
                    if (value > transactionAmount) {
                        throw new IOException("Voucher value exceeds transaction amount.");
                    }
                    deductedAmount = value;
                    line += ",REDEEMED";
                }
                voucherData.append(line).append("\n");
            }
        }
        if (!voucherFound) {
            System.out.println("Invalid voucher code. Press 0 to continue without voucher or Press Enter to use another voucher");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine();
            if (choice.equals("0")) {
                return deductedAmount;
            } else {
                System.out.println("Enter another voucher code:");
                String newVoucherCode = scanner.nextLine();
                return checkVoucher(newVoucherCode, transactionAmount);
            }
        }
        try (FileWriter writer = new FileWriter("voucher.txt")) {
            writer.write(voucherData.toString());
            System.out.println("Voucher redeemed successfully.");
        } catch (IOException e) {
            System.out.println("Error updating voucher data: " + e.getMessage());
        }
        return deductedAmount;
    }
     
    private static void saveTransactionHistory(String transactionID, String customerID, double transactionAmount) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = formatter.format(new Date());
        try (FileWriter writer = new FileWriter("transaction.txt", true)) {
            writer.write(transactionID + "," + customerID + "," + timestamp + "," + transactionAmount + "\n");
            System.out.println("Transaction history saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving transaction history: " + e.getMessage());
        }
    }

    private static int getLastTransactionID() {
        int lastTransactionID = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("transaction.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String transactionIDString = parts[0].substring(1); 
                int transactionID = Integer.parseInt(transactionIDString);
                if (transactionID > lastTransactionID) {
                    lastTransactionID = transactionID;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading transaction history: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid transaction ID format: " + e.getMessage());
        }
        return lastTransactionID;
    }

    private static String generateUniqueTransactionID() {
        int transactionID = transactionCounter.getAndIncrement();
        if (transactionID > 999) {
            transactionCounter.set(1);
            transactionID = 1;
        }
        return String.format("T%03d", transactionID);
    }

    public static void transaction(){
        Scanner scanner = new Scanner(System.in);
        earnPoints();
        Policy.policyProcessor();
        System.out.println("Press Any Key to back to main menu...");
        scanner.nextLine(); 
        Main.main(null);
    }

    public static void main(String[] args) {
        Customer customer = new Customer();
        System.out.println(customer.checkCustomer("C100"));
    }
}
