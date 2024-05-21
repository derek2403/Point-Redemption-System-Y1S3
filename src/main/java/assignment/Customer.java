package assignment;

import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Customer extends Contact {
    private String customerID;
    private int availablePoints;

    public Customer() {
        super(); 
        this.customerID = generateCustomerID(); 
        this.availablePoints = 0; 
    }

    public Customer(String customerID, String name, String email, String address, char gender, String phoneNum, int availablePoints) {
        super(name, email, address, gender, phoneNum);
        this.customerID = customerID;
        this.availablePoints = availablePoints;
    }

    @Override
    public void register() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        boolean validName = false;
        boolean validEmail = false;
        boolean validAddress = false;
        boolean validGender = false;
        boolean validPhoneNum = false;
    
        while (!validName || !validEmail || !validAddress || !validGender || !validPhoneNum) {
            try {
                if (!validName) {
                    System.out.println("Enter customer name:");
                    this.name = validateName(scanner.nextLine());
                    validName = true;
                }
    
                if (!validEmail) {
                    System.out.println("Enter email:");
                    this.email = validateEmail(scanner.nextLine());
                    validEmail = true;
                }
    
                if (!validAddress) {
                    System.out.println("Enter address:");
                    this.address = validateAddress(scanner.nextLine());
                    validAddress = true;
                }                
    
                if (!validGender) {
                    System.out.println("Enter gender (M/F):");
                    this.gender = validateGender(scanner.nextLine());
                    validGender = true;
                }
    
                if (!validPhoneNum) {
                    System.out.println("Enter phone number:");
                    this.phoneNum = validatePhoneNumber(scanner.nextLine());
                    validPhoneNum = true;
                }
    
                saveCustomerDetailsToFile();
                System.out.print("\033[H\033[2J");
                System.out.flush();
    
                displayDetails();
            } catch (IllegalArgumentException e) {
                System.out.println("Input error: " + e.getMessage());
    
                if (e.getMessage().contains("Name")) {
                    validName = false;
                } else if (e.getMessage().contains("Email")) {
                    validEmail = false;
                } else if (e.getMessage().contains("Address")) {
                    validAddress = false;
                } else if (e.getMessage().contains("Gender")) {
                    validGender = false;
                } else if (e.getMessage().contains("Phone")) {
                    validPhoneNum = false;
                }
            }
        }
    }
    
    
    private String generateCustomerID() {
        Random rand = new Random();
        int randNum = rand.nextInt(900) + 100; 
        return "C" + randNum;
    }

    private void saveCustomerDetailsToFile() {
        try {
            FileWriter writer = new FileWriter("customer.txt", true);
            writer.write(customerID + "," + name + "," + email + "," + address + "," + gender + "," + phoneNum + "," + availablePoints + "\n");
            writer.close();
            System.out.println("Customer details saved to customer.txt");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    @Override
    public void displayDetails() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("Customer ID: " + customerID);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Address: " + address);
        System.out.println("Gender: " + gender);
        System.out.println("Phone Number: " + phoneNum);
        System.out.println("Available Points: " + availablePoints);
    }

    @SuppressWarnings("resource")
    public void updateDetails() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter customer ID to update details:");
        String customerIdToUpdate = scanner.nextLine();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("customer.txt"));
            String line;
            StringBuilder updatedFileContent = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length > 0 && fields[0].equals(customerIdToUpdate)) {
                    System.out.println("Current details:");
                    System.out.println("Name: " + fields[1]);
                    System.out.println("Email: " + fields[2]);
                    System.out.println("Address: " + fields[3]);
                    System.out.println("Gender: " + fields[4]);
                    System.out.println("Phone Number: " + fields[5]);
                    System.out.println("Available Points: " + fields[6]);

                    System.out.println("Select the detail to update:");
                    System.out.println("1. Name");
                    System.out.println("2. Email");
                    System.out.println("3. Address");
                    System.out.println("4. Gender");
                    System.out.println("5. Phone Number");
                    System.out.print("Enter your choice: ");

                    int choice = scanner.nextInt();
                    scanner.nextLine(); 

                    switch (choice) {
                        case 1:
                            System.out.println("Enter new name:");
                            fields[1] = validateName(scanner.nextLine());
                            break;
                        case 2:
                            System.out.println("Enter new email:");
                            fields[2] = validateEmail(scanner.nextLine());
                            break;
                        case 3:
                            System.out.println("Enter new address:");
                            fields[3] = scanner.nextLine();
                            break;
                        case 4:
                            System.out.println("Enter new gender (M/F):");
                            fields[4] = Character.toString(validateGender(scanner.nextLine()));
                            break;
                        case 5:
                            System.out.println("Enter new phone number:");
                            fields[5] = validatePhoneNumber(scanner.nextLine());
                            break;
                        default:
                            System.out.println("Invalid choice.");
                            break;
                    }

                    updatedFileContent.append(String.join(",", fields)).append("\n");
                } else {
                    updatedFileContent.append(line).append("\n");
                }
            }

            reader.close();

            FileWriter writer = new FileWriter("customer.txt");
            writer.write(updatedFileContent.toString());
            writer.close();

            System.out.println("Customer details updated successfully.");
        } catch (IOException e) {
            System.out.println("Error updating customer details: " + e.getMessage());
        }
    }

    @SuppressWarnings("resource")
    public void deleteCustomer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter customer ID to delete:");
        String customerIdToDelete = scanner.nextLine();
    
        try {
            BufferedReader reader = new BufferedReader(new FileReader("customer.txt"));
            String line;
            StringBuilder updatedFileContent = new StringBuilder();
    
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
    
                if (fields.length > 0 && !fields[0].equals(customerIdToDelete)) {
                    updatedFileContent.append(line).append("\n");
                }
            }
    
            reader.close();
    
            FileWriter writer = new FileWriter("customer.txt");
            writer.write(updatedFileContent.toString());
            writer.close();
    
            System.out.println("Customer with ID " + customerIdToDelete + " deleted successfully.");
        } catch (IOException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
        }
    }  

    @SuppressWarnings("resource")
    public static void getCustomerDetail() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter customer ID:");
        String customerIdToGet = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader("customer.txt"))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length > 0 && fields[0].equals(customerIdToGet)) {
                    found = true;
                    System.out.println("Customer Details:");
                    System.out.println("Name: " + fields[1]);
                    System.out.println("Email: " + fields[2]);
                    System.out.println("Address: " + fields[3]);
                    System.out.println("Gender: " + fields[4]);
                    System.out.println("Phone Number: " + fields[5]);
                    System.out.println("Available Points: " + fields[6]);
                    break;
                }
            }

            if (!found) {
                System.out.println("Customer with ID " + customerIdToGet + " not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading customer data.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("resource")
    public int getBalance() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter customer ID:");
        String customerIdToGet = scanner.nextLine();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("customer.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length > 0 && fields[0].equals(customerIdToGet)) {
                    reader.close();
                    return Integer.parseInt(fields[6]);
                }
            }

            reader.close();
            System.out.println("Customer ID not found.");
            return 0;
        } catch (IOException e) {
            System.out.println("Error retrieving customer points: " + e.getMessage());
            return 0;
        }
    }

    public int getBalance(String customerID) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("customer.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length > 0 && fields[0].equals(customerID)) {
                    reader.close();
                    return Integer.parseInt(fields[6]);
                }
            }

            reader.close();
            System.out.println("Customer ID not found.");
            return 0;
        } catch (IOException e) {
            System.out.println("Error retrieving customer points: " + e.getMessage());
            return 0;
        }
    }
    
    @SuppressWarnings("resource")
    public boolean setPoints() {
        Scanner scanner = new Scanner(System.in);
    
        System.out.println("Enter customer ID:");
        String customerIdToUpdate = scanner.nextLine();
    
        System.out.println("Enter new points value:");
        int newPoints = scanner.nextInt();
        scanner.nextLine(); 
    
        try {
            BufferedReader reader = new BufferedReader(new FileReader("customer.txt"));
            String line;
            StringBuilder updatedFileContent = new StringBuilder();
    
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
    
                if (fields.length > 0 && fields[0].equals(customerIdToUpdate)) {
                    fields[6] = Integer.toString(newPoints); 
                    updatedFileContent.append(String.join(",", fields)).append("\n");
                } else {
                    updatedFileContent.append(line).append("\n");
                }
            }
    
            reader.close();
    
            FileWriter writer = new FileWriter("customer.txt");
            writer.write(updatedFileContent.toString());
            writer.close();
    
            return true;
        } catch (IOException e) {
            System.out.println("Error updating customer points: " + e.getMessage());
            return false;
        }
    }

    public boolean setPoints(String CustomerID, int newPoints) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("customer.txt"));
            String line;
            StringBuilder updatedFileContent = new StringBuilder();
    
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
    
                if (fields.length > 0 && fields[0].equals(CustomerID)) { // Use CustomerID here
                    fields[6] = Integer.toString(newPoints); 
                    updatedFileContent.append(String.join(",", fields)).append("\n");
                } else {
                    updatedFileContent.append(line).append("\n");
                }
            }
    
            reader.close();
    
            FileWriter writer = new FileWriter("customer.txt");
            writer.write(updatedFileContent.toString());
            writer.close();
    
            return true;
        } catch (IOException e) {
            System.out.println("Error updating customer points: " + e.getMessage());
            return false;
        }
    }
    
    
    public static void Menu(){
        System.out.println("    __  __ __   _____ ______   ___   ___ ___    ___  ____  "); 
        System.out.println("   /  ]|  |  | / ___/|      | /   \\ |   |   |  /  _]|    \\ "); 
        System.out.println("  /  / |  |  |(   \\_ |      ||     || _   _ | /  [_ |  D  )");
        System.out.println(" /  /  |  |  | \\__  ||_|  |_||  O  ||  \\_/  ||    _]|    / ");
        System.out.println("/   \\_ |  :  | /  \\ |  |  |  |     ||   |   ||   [_ |    \\ ");
        System.out.println("\\     ||     | \\    |  |  |  |     ||   |   ||     ||  .  \\");
        System.out.println(" \\____| \\__,_|  \\___|  |__|   \\___/ |___|___||_____||__|\\_\\");
        System.out.println("                                                           ");
        System.out.println("Choose an operation.\n1. Register\n2. Check Details\n3. Update Details\n4. Delete\n5. Back to Main Menu");
    } 

    @SuppressWarnings("resource")
    public static void Customeroperation(){
        Customer customer = new Customer();
        int option;
        Scanner scanner = new Scanner(System.in);
        do {
            Customer.Menu();
            
            try {
                option = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid menu choice.");
                scanner.nextLine(); 
                continue; 
            }
            
            switch (option) {
                case 1:
                    customer.register();
                    System.out.println("Press Any Key to continue...");
                    scanner.nextLine();
                    scanner.nextLine();
                    Customeroperation();
                    break;
                case 2:
                    Customer.getCustomerDetail();
                    System.out.println("Press Any Key to continue...");
                    scanner.nextLine();
                    scanner.nextLine();
                    Customeroperation();
                    break;
                case 3:
                    customer.updateDetails();
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    scanner.nextLine();
                    Customeroperation();
                    break;
                case 4:
                    customer.deleteCustomer();
                    System.out.println("Press Any Key to continue...");
                    scanner.nextLine();
                    scanner.nextLine();
                    Customeroperation();
                    break;
                case 5:
                    Main.main(null);
                    break;
                default:
                    System.out.println("Invalid menu choice. Please try again.");
                    break;
            }
        } while (true); 
    }

    public boolean checkCustomer(String customerID) {
        try (BufferedReader reader = new BufferedReader(new FileReader("customer.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                // Check if the first field (customer ID) matches the given ID
                if (fields.length > 0 && fields[0].equals(customerID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading customer data: " + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        Customer customer = new Customer();
        customer.setPoints("C100", 0);
    }
}