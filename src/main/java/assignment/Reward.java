package assignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

    @SuppressWarnings("unused")
    public class Reward {
        private int rewardNumber;
        private String description;
        private int pointsRequired;
    
        public Reward(int rewardNumber, String description) {
            this.rewardNumber = rewardNumber;
            this.description = description;
        }
        
        public Reward(){

        }
    
        public static int checkStock(int rewardId) {
            int availableStock = 0;
    
            try (BufferedReader reader = new BufferedReader(new FileReader("rewards.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int id = Integer.parseInt(parts[0]);
                    int stock = Integer.parseInt(parts[3]);
                    if (id == rewardId) {
                        availableStock = stock;
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading rewards data: " + e.getMessage());
            }
    
            return availableStock;
        }

        public void addReward(int rewardNumber, String description, int pointsRequired, int stock) {
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            boolean exists = false;
        
            System.out.println("Starting to check for existing reward ID...");
            try (BufferedReader reader = new BufferedReader(new FileReader("rewards.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int existingRewardNumber = Integer.parseInt(parts[0]);
                    int existingStock = Integer.parseInt(parts[3]);
                    if (existingRewardNumber == rewardNumber && existingStock > 0) {
                        System.out.println("Reward ID " + rewardNumber + " already exists with stock " + existingStock + ". Cannot add.");
                        exists = true;
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading rewards file: " + e.getMessage());
            }
        
            if (!exists) {
                System.out.println("No existing reward found. Adding new reward...");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("rewards.txt", true))) {
                    writer.write("\n" + rewardNumber + "," + description + "," + pointsRequired + "," + stock); // Ensure the newline character is added at the beginning
                    writer.flush();
                    System.out.println("Reward added successfully.");
                } catch (IOException e) {
                    System.out.println("Error adding reward: " + e.getMessage());
                }
            } else {
                System.out.println("Reward ID exists, not adding new reward.");
            }
        
            System.out.println("Press Any Key to return...");
            scanner.nextLine();
            System.out.print("\033[H\033[2J");
            System.out.flush();
            userMenu();
            rewardsOperation();
        }
        

        public void deleteReward(int rewardId) {
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            try {
                File inputFile = new File("rewards.txt");
                File tempFile = new File("temp.txt");
    
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    
                String lineToRemove = rewardId + ",";
    
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    String trimmedLine = currentLine.trim();
                    if (!trimmedLine.startsWith(lineToRemove)) {
                        writer.write(currentLine + System.getProperty("line.separator"));
                    }
                }
                writer.close();
                reader.close();
    
                if (!inputFile.delete()) {
                    System.out.println("Error deleting the original file.");
                    return;
                }
                if (!tempFile.renameTo(inputFile)) {
                    System.out.println("Error renaming the temp file.");
                }
                System.out.println("Reward with ID " + rewardId + " deleted successfully.");
                System.out.println("Press Any Key to return...");
                scanner.nextLine(); 
                System.out.print("\033[H\033[2J");
                System.out.flush();
                userMenu();
                rewardsOperation();
            } catch (IOException e) {
                System.out.println("Error deleting reward: " + e.getMessage());
                System.out.println("Press Any Key to return...");
                scanner.nextLine(); 
                System.out.print("\033[H\033[2J");
                System.out.flush();
                userMenu();
                rewardsOperation();
            }
        }

        public static int checkRequiredPoints(int rewardId) {
            int requiredPoints = -1; 
    
            try (BufferedReader reader = new BufferedReader(new FileReader("rewards.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int id = Integer.parseInt(parts[0]);
                    if (id == rewardId) {
                        requiredPoints = Integer.parseInt(parts[2]);
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading rewards data: " + e.getMessage());
            }
    
            return requiredPoints;
        }

    public void rewardsOperation() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.println("Enter reward ID:");
                        int rewardId = scanner.nextInt();
                        scanner.nextLine(); 
                        System.out.println("Enter description:");
                        String description = scanner.nextLine();
                        System.out.println("Enter points required:");
                        int pointsRequired = scanner.nextInt();
                        System.out.println("Enter stock:");
                        int stock = scanner.nextInt();
                        addReward(rewardId, description, pointsRequired, stock);
                        break;
                    case 2:
                        System.out.println("Enter reward ID to delete:");
                        int idToDelete = scanner.nextInt();
                        deleteReward(idToDelete);
                        break;
                    case 3:
                        System.out.println("Enter reward ID to modify stock records:");
                        int idToModify = scanner.nextInt();
                        System.out.println("Enter new stock amount:");
                        int newStock = scanner.nextInt();
                        updateStock(idToModify, newStock);
                        break;
                    case 4:
                        System.out.println("Exiting to main menu...");
                        Main.main(null);
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer choice.");
                scanner.nextLine(); 
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer value.");
                scanner.nextLine(); 
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }

    }
        public static void showAvailableRewards() {
            String headerFormat = "%-12s | %-50s | %-15s";
            System.out.printf(headerFormat, "Rewards ID", "Description", "Points Required");
            System.out.println();
    
            try (BufferedReader reader = new BufferedReader(new FileReader("rewards.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int rewardId = Integer.parseInt(parts[0]);
                    String description = parts[1];
                    int pointsRequired = Integer.parseInt(parts[2]);
                    int stock = Integer.parseInt(parts[3]);
    
                    if (stock > 0) {
                        System.out.printf("%-12s | %-50s | %-15s\n", rewardId, description, pointsRequired);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading rewards data: " + e.getMessage());
            }
        }

        public static void updateStock(int rewardId, int newStock) {
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            Reward reward = new Reward();
            try {
                File inputFile = new File("rewards.txt");
                File tempFile = new File("temp.txt");
    
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    
                String lineToUpdate = rewardId + ",";
                String newLine = "";
    
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    String trimmedLine = currentLine.trim();
                    if (trimmedLine.startsWith(lineToUpdate)) {
                        String[] parts = currentLine.split(",");
                        parts[3] = String.valueOf(newStock);
                        newLine = String.join(",", parts);
                        writer.write(newLine + System.getProperty("line.separator"));
                    } else {
                        writer.write(currentLine + System.getProperty("line.separator"));
                    }
                }
                writer.close();
                reader.close();
    
                if (!inputFile.delete()) {
                    System.out.println("Error deleting the original file.");
                    return;
                }
                if (!tempFile.renameTo(inputFile)) {
                    System.out.println("Error renaming the temp file.");
                }
                System.out.println("Stock for Reward with ID " + rewardId + " updated successfully.");
                System.out.println("Press Any Key to return...");
                scanner.nextLine(); 
                System.out.print("\033[H\033[2J");
                System.out.flush();
                userMenu();
                reward.rewardsOperation();
            } catch (IOException e) {
                System.out.println("Error updating stock: " + e.getMessage());
            }
        }

        public static void updateTransStock(int rewardId, int amountToRedeem) {
            try {
                File inputFile = new File("rewards.txt");
                File tempFile = new File("temp.txt");
    
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    
                String lineToUpdate = rewardId + ",";
                String newLine = "";
    
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    String trimmedLine = currentLine.trim();
                    if (trimmedLine.startsWith(lineToUpdate)) {
                        String[] parts = currentLine.split(",");
                        int originalStock = Integer.parseInt(parts[3]);
                        int updatedStock = originalStock - amountToRedeem;
                        if (updatedStock < 0) {
                            updatedStock = 0; 
                        }
                        parts[3] = String.valueOf(updatedStock);
                        newLine = String.join(",", parts);
                        writer.write(newLine + System.getProperty("line.separator"));
                    } else {
                        writer.write(currentLine + System.getProperty("line.separator"));
                    }
                }
                writer.close();
                reader.close();
    
                if (!inputFile.delete()) {
                    System.out.println("Error deleting the original file.");
                    return;
                }
                if (!tempFile.renameTo(inputFile)) {
                    System.out.println("Error renaming the temp file.");
                }
                System.out.println("Stock for Reward with ID " + rewardId + " updated successfully.");
            } catch (IOException e) {
                System.out.println("Error updating stock: " + e.getMessage());
            }
        }

        public static void generateRedemptionRecords() {
            try (BufferedReader customerReader = new BufferedReader(new FileReader("customer.txt"));
                 BufferedReader redemptionReader = new BufferedReader(new FileReader("redemption.txt"));
                 BufferedReader rewardsReader = new BufferedReader(new FileReader("rewards.txt"));
                 FileWriter writer = new FileWriter("redemption_records.txt")) {
        
                String[] customerData = readLinesToArray(customerReader);
        
                String[] rewardsData = readLinesToArray(rewardsReader);
        
                String redemptionLine;
                while ((redemptionLine = redemptionReader.readLine()) != null) {
                    if (!redemptionLine.isEmpty()) { // Check if the line is not empty
                        String[] redemptionFields = redemptionLine.split(",");
                        String customerID = redemptionFields[1];
        
                        String customerDetails = getCustomerDetails(customerData, customerID);
        
                        String rewardsDescription = getRewardsDescription(rewardsData, redemptionFields[2]);
        
                        String record = redemptionFields[0] + "," + customerDetails + "," + rewardsDescription + ","
                                        + redemptionFields[3] + "\n";
                        writer.write(record);
                    }
                }
                System.out.println("Redemption records generated successfully.");
            } catch (IOException e) {
                System.out.println("Error generating redemption records: " + e.getMessage());
            }
        }
    
        private static String[] readLinesToArray(BufferedReader reader) throws IOException {
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString().split("\n");
        }
    
        private static String getCustomerDetails(String[] customerData, String customerID) {
            for (String record : customerData) {
                String[] fields = record.split(",");
                if (fields[0].equals(customerID)) {
                    return fields[0] + "," + fields[1] + "," + fields[2] + "," + fields[3];
                }
            }
            return "";
        }
    
        private static String getRewardsDescription(String[] rewardsData, String rewardID) {
            for (String record : rewardsData) {
                String[] fields = record.split(",");
                if (fields[0].equals(rewardID)) {
                    return fields[1];
                }
            }
            return "";
        }
        public static void userMenu(){
            System.out.println(" _______   ________  __       __   ______   _______   _______    ______  ");
            System.out.println("/       \\ /        |/  |  _  /  | /      \\ /       \\ /       \\  /      \\ ");
            System.out.println("$$$$$$$  |$$$$$$$$/ $$ | / \\ $$ |/$$$$$$  |$$$$$$$  |$$$$$$$  |/$$$$$$  |");
            System.out.println("$$ |__$$ |$$ |__    $$ |/$  \\$$ |$$ |__$$ |$$ |__$$ |$$ |  $$ |$$ \\__$$/ ");
            System.out.println("$$    $$< $$    |   $$ /$$$  $$ |$$    $$ |$$    $$< $$ |  $$ |$$      \\ ");
            System.out.println("$$$$$$$  |$$$$$/    $$ $$/$$ $$ |$$$$$$$$ |$$$$$$$  |$$ |  $$ | $$$$$$  |");
            System.out.println("$$ |  $$ |$$ |_____ $$$$/  $$$$ |$$ |  $$ |$$ |  $$ |$$ |__$$ |/  \\__$$ |");
            System.out.println("$$ |  $$ |$$       |$$$/    $$$ |$$ |  $$ |$$ |  $$ |$$    $$/ $$    $$ |");
            System.out.println("$$/   $$/ $$$$$$$$/ $$/      $$/ $$/   $$/ $$/   $$/ $$$$$$$/   $$$$$$/ ");
            System.out.println("Select Rewards Operation\n1. Add Rewards\n2. Delete Rewards\n3. Update Stock\n4. Back to Main Menu");
        }

        public static void main(String[] args) {
            generateRedemptionRecords();
        }
    
        
    }
