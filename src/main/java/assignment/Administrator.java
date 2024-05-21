package assignment;

import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Administrator extends Contact {
    private String adminID;
    private String deptID;
    private String password;
    
    public Administrator() {
        super(); 
        this.adminID = generateAdminID(); 
        this.deptID = "";
        this.password = "";
    }

    public Administrator(String adminID, String name, String email, String address, char gender, String phoneNum, String deptID, String password) {
        super(name, email, address, gender, phoneNum);
        this.adminID = adminID;
        this.deptID = deptID;
        this.password = password;
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
        boolean validDeptID = false;
        boolean validPassword = false;

        while (!validName || !validEmail || !validAddress || !validGender || !validPhoneNum || !validDeptID || !validPassword) {
            try {
                if (!validName) {
                    System.out.println("Enter administrator name:");
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

                if (!validDeptID) {
                    System.out.println("Enter Department Name:");
                    this.deptID = validateDeptID(scanner.nextLine());
                    validDeptID = true;
                }

                if (!validPassword) {
                    System.out.println("Enter password:");
                    this.password = validatePassword(scanner.nextLine());
                    validPassword = true;
                }

                saveAdminDetailsToFile();
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
                } else if (e.getMessage().contains("Department Name")) {
                    validDeptID = false;
                } else if (e.getMessage().contains("Password")) {
                    validPassword = false;
                }
            }
        }
    }

    private String validateDeptID(String deptID) {
        if (deptID.isEmpty()) {
            throw new IllegalArgumentException("Department Name cannot be empty");
        }
        return deptID;
    }

    private String validatePassword(String password) {
        if (password.length() < 8 || !password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\p{Punct}).{8,}$")) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 symbol");
        }
        return password;
    }
       

    private static String generateAdminID() {
        Random rand = new Random();
        int randNum = rand.nextInt(900) + 100; 
        return "A" + randNum;
    }

    private void saveAdminDetailsToFile() {
        try {
            FileWriter writer = new FileWriter("admin.txt", true);
            writer.write(adminID + "," + name + "," + email + "," + address + "," + gender + "," + phoneNum + "," + deptID + "," + password + "\n");
            writer.close();
            System.out.println("Administrator details saved to admin.txt");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    @Override
    public void displayDetails() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("Admin ID: " + adminID);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Address: " + address);
        System.out.println("Gender: " + gender);
        System.out.println("Phone Number: " + phoneNum);
        System.out.println("Department Name: " + deptID);
        System.out.println("Password: " + password);
    }

    public void updateDetails() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter admin ID to update details:");
        String adminIdToUpdate = scanner.nextLine();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("admin.txt"));
            String line;
            StringBuilder updatedFileContent = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length > 0 && fields[0].equals(adminIdToUpdate)) {
                    System.out.println("Current details:");
                    System.out.println("Name: " + fields[1]);
                    System.out.println("Email: " + fields[2]);
                    System.out.println("Address: " + fields[3]);
                    System.out.println("Gender: " + fields[4]);
                    System.out.println("Phone Number: " + fields[5]);
                    System.out.println("Department ID: " + fields[6]);
                    System.out.println("Password: " + fields[7]);

                    System.out.println("Select the detail to update:");
                    System.out.println("1. Name");
                    System.out.println("2. Email");
                    System.out.println("3. Address");
                    System.out.println("4. Gender");
                    System.out.println("5. Phone Number");
                    System.out.println("6. Department ID");
                    System.out.println("7. Password");
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
                        case 6:
                            System.out.println("Enter new department Name:");
                            fields[6] = scanner.nextLine();
                            break;
                        case 7:
                            System.out.println("Enter new password:");
                            fields[7] = validatePassword(scanner.nextLine());
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

            FileWriter writer = new FileWriter("admin.txt");
            writer.write(updatedFileContent.toString());
            writer.close();

            System.out.println("Administrator details updated successfully.");
        } catch (IOException e) {
            System.out.println("Error updating administrator details: " + e.getMessage());
        }
    }

    public void getAdminDetail() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter admin ID:");
        String adminIdToGet = scanner.nextLine();
    
        try {
            BufferedReader reader = new BufferedReader(new FileReader("admin.txt"));
            String line;
    
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
    
                if (fields.length > 0 && fields[0].equals(adminIdToGet)) {
                    System.out.println("Admin ID: " + fields[0]);
                    System.out.println("Name: " + fields[1]);
                    System.out.println("Email: " + fields[2]);
                    System.out.println("Address: " + fields[3]);
                    System.out.println("Gender: " + fields[4]);
                    System.out.println("Phone Number: " + fields[5]);
                    System.out.println("Department Name: " + fields[6]);
                    System.out.println("Password: " + fields[7]);
                    reader.close();
                    return;
                }
            }
    
            reader.close();
            System.out.println("Admin ID not found.");
        } catch (IOException e) {
            System.out.println("Error retrieving administrator details: " + e.getMessage());
        }
    }

    public void deleteAdmin() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter admin ID to delete:");
        String adminIdToDelete = scanner.nextLine();
    
        try {
            BufferedReader reader = new BufferedReader(new FileReader("admin.txt"));
            String line;
            StringBuilder updatedFileContent = new StringBuilder();
    
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
    
                if (fields.length > 0 && !fields[0].equals(adminIdToDelete)) {
                    updatedFileContent.append(line).append("\n");
                }
            }
    
            reader.close();
    
            FileWriter writer = new FileWriter("admin.txt");
            writer.write(updatedFileContent.toString());
            writer.close();
    
            System.out.println("Admin with ID " + adminIdToDelete + " deleted successfully.");
        } catch (IOException e) {
            System.out.println("Error deleting admin: " + e.getMessage());
        }
    }

    public static boolean authenticateAdmin(String adminID, String password) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("admin.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length >= 8 && fields[0].equals(adminID) && fields[7].equals(password)) {
                    reader.close();
                    return true;
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Error authenticating administrator: " + e.getMessage());
        }

        return false;
    }

    public boolean login() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter Admin ID:");
        String adminID = scanner.nextLine();
        
        System.out.println("Enter Password:");
        String password = scanner.nextLine();
        
        if (Administrator.authenticateAdmin(adminID, password)) {
            System.out.println("Login successful.");
            return true;
        } else {
            System.out.println("Login failed. Invalid admin ID or password.");
            return false;
        }
    }
    
    public static void Menu(){
        System.out.println("  ____  ___    ___ ___  ____  ____  "); 
        System.out.println(" /    ||   \\  |   |   ||    ||    \\ "); 
        System.out.println("|  o  ||    \\ | _   _ | |  | |  _  |");
        System.out.println("|     ||  D  ||  \\_/  | |  | |  |  |");
        System.out.println("|  _  ||     ||   |   | |  | |  |  |");
        System.out.println("|  |  ||     ||   |   | |  | |  |  |");
        System.out.println("|__|__||_____||___|___||____||__|__|");
        System.out.println("                                    ");
        System.out.println("Choose an operation.\n1. Register\n2. Check Details\n3. Update Details\n4. Delete\n5. Return to Main Menu");

    }

    public static void Adminoperation(){
        Administrator administrator = new Administrator();
        int option;
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        do {
            Administrator.Menu();
            
            try {
                option = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid menu choice.");
                scanner.nextLine(); 
                continue; 
            }
            
            switch (option) {
                case 1:
                    administrator.register();
                    System.out.println("Press Any Key to continue...");
                    scanner.nextLine();
                    scanner.nextLine();
                    Adminoperation();
                    break;
                case 2:
                    administrator.getAdminDetail();
                    System.out.println("Press Any Key to continue...");
                    scanner.nextLine();
                    scanner.nextLine();
                    Adminoperation();
                    break;
                case 3:
                    administrator.updateDetails();
                    System.out.println("Press Any Key to continue...");
                    scanner.nextLine();
                    scanner.nextLine();
                    Adminoperation();
                    break;
                case 4:
                    administrator.deleteAdmin();
                    System.out.println("Press Any Key to continue...");
                    scanner.nextLine();
                    scanner.nextLine();
                    Adminoperation();
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
}
