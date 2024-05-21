package assignment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unused")
public class Points {
    private boolean customerExist;
    private int availablePoints;
    private int pointsRequired;

    public Points(){

    }

    @SuppressWarnings("resource")
    public void redemption() {
        Scanner scanner = new Scanner(System.in);
        Customer customer = new Customer(); 
    
        System.out.println("Enter Customer ID: ");
        String customerID = scanner.nextLine();
        boolean isAuthenticated = customer.checkCustomer(customerID);
    
        if (!isAuthenticated) {
            System.out.println("Customer not found. Redemption cancelled.");
            System.out.println("Press Any Key to back to main menu...");
            scanner.nextLine(); 
            Main.main(null);
        }
    
        int availablePoints = customer.getBalance(customerID);
        System.out.println("Your currently available points: " + availablePoints);
    
        Reward.showAvailableRewards();
    
        int rewardID;
        int pointsRequired;
        int amountToRedeem;
        int availableStock;
    
        while (true) {
            System.out.println("Enter the points ID of the desired reward (or type CANCEL to quit):");
            String rewardInput = scanner.nextLine();
            
            if (rewardInput.equalsIgnoreCase("CANCEL")) {
                System.out.println("Redemption cancelled.");
                System.out.println("Press Any Key to back to main menu...");
                scanner.nextLine(); 
                Main.main(null);
            }
    
            try {
                rewardID = Integer.parseInt(rewardInput);
                pointsRequired = Reward.checkRequiredPoints(rewardID);
                availableStock = Reward.checkStock(rewardID);
    
                if (pointsRequired == -1 || availableStock <= 0) {
                    System.out.println("Invalid reward ID or insufficient stock. Please enter a valid ID.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer reward ID or type CANCEL to quit.");
            }
        }
    
        System.out.println("Enter the amount of rewards you want to redeem:");
        while (true) {
            try {
                amountToRedeem = scanner.nextInt();
                if (amountToRedeem <= 0) {
                    System.out.println("Invalid amount. Please enter a positive integer.");
                    continue;
                }
                if (amountToRedeem > availableStock) {
                    System.out.println("Insufficient stock. Please enter a lower amount.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer amount.");
                scanner.nextLine(); 
            }
        }
    
        int customerPoints = customer.getBalance(customerID);
    
        int totalPointsRequired = pointsRequired * amountToRedeem;
    
        if (customerPoints >= totalPointsRequired) {
            System.out.println("You have enough points to redeem the selected reward.");
            System.out.println("Do you want to proceed with the redemption? (yes/no)");
            String confirmation = scanner.next();
    
            if (confirmation.equalsIgnoreCase("yes")) {
                int newPointsBalance = customerPoints - totalPointsRequired;
                customer.setPoints(customerID, newPointsBalance); // Update points balance in text file
    
                Reward.updateTransStock(rewardID, amountToRedeem);
    
                System.out.println("Redemption successful! Your points balance is now: " + newPointsBalance);
    
                saveRedemptionDetails(customerID, rewardID, amountToRedeem);
            } else {
                System.out.println("Redemption cancelled.");
            }
        } else {
            System.out.println("Insufficient points to redeem the selected reward.");
        }
        System.out.println("Press Any Key to back to main menu...");
        scanner.nextLine(); 
        scanner.nextLine(); 
        Main.main(null);
    }

    private void saveRedemptionDetails(String customerID, int rewardID, int amountRedeemed) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
    
        int lastRedemptionID = getLastRedemptionID();
    
        String redemptionID = generateRedemptionID(lastRedemptionID);
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("redemption.txt", true))) {
            writer.write(redemptionID + "," + customerID + "," + rewardID + "," + amountRedeemed + "," + currentDate + "\n");
            System.out.println("Redemption details saved to redemption.txt");
        } catch (IOException e) {
            System.out.println("Error saving redemption details: " + e.getMessage());
            return; 
        }
    
        Reward.generateRedemptionRecords();
    }

    private String generateRedemptionID(int lastRedemptionID) {
        int nextRedemptionID = lastRedemptionID + 1;
        DecimalFormat df = new DecimalFormat("000");
        return "RED" + df.format(nextRedemptionID);
    }

    private int getLastRedemptionID() {
        int lastRedemptionID = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("redemption.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    String redemptionID = parts[0];
                    if (redemptionID.startsWith("RED")) {
                        try {
                            int id = Integer.parseInt(redemptionID.substring(3)); // Skip the "RED" prefix
                            if (id > lastRedemptionID) {
                                lastRedemptionID = id;
                            }
                        } catch (NumberFormatException ex) {
                            System.out.println("Invalid redemption ID format: " + redemptionID);
                        }
                    } else {
                        System.out.println("Unexpected redemption ID format: " + redemptionID);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading redemption data: " + e.getMessage());
        }
        return lastRedemptionID;
    }
    
    public static void main(String[] args) {
        Points points = new Points();
        points.redemption();
    }
}