package assignment;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int menuChoice = 0;
        Scanner scanner = new Scanner(System.in);
        Main main = new Main();
        main.showMenu();
        try {
            menuChoice = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.close(); 
        }
        
        switch (menuChoice) {
            case 1:
                System.out.print("\033[H\033[2J");
                System.out.flush();
                main.contactManagement(scanner);
                break;
            case 2:
                System.out.print("\033[H\033[2J");
                System.out.flush();
                Transaction.transaction();
                break;
            case 3:
                System.out.print("\033[H\033[2J");
                System.out.flush();
                Points points = new Points();
                points.redemption();
                break;
            case 4:
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println("Access blocked, please log in to continue...");
                System.out.println("Enter admin ID:");
                String adminID = scanner.next();
                System.out.println("Enter password:");
                String password = scanner.next();
                if (Administrator.authenticateAdmin(adminID, password)) {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    Main.reporting(scanner);
                } else {
                    System.out.println("Authentication failed. Exiting...");
                    main(args);
                }
                break;
            case 5:
                System.out.print("\033[H\033[2J");
                System.out.flush();
                Claims.userMenu();
                Claims.handleRedemptionProcess();
                break;
            case 6:
                Reward reward = new Reward();
                System.out.print("\033[H\033[2J");
                System.out.flush();
                Reward.userMenu();
                reward.rewardsOperation();
                break;
            case 7:
                return;
            default:
                System.out.println("Invalid menu choice. Please retry...");
                main(args);
                break;
        }
}
    
    public void showMenu() {
        System.out.println("  _____ ____   ____  ____   __  _  _        ___      ___ ___   ____  ____  ______ ");
        System.out.println(" / ___/|    \\ /    ||    \\ |  |/ ]| |      /  _]    |   |   | /    ||    \\|      |");
        System.out.println("(   \\_ |  o  )  o  ||  D  )|  ' / | |     /  [_     | _   _ ||  o  ||  D  )      |");
        System.out.println(" \\__  ||   _/|     ||    / |    \\ | |___ |    _]    |  \\_/  ||     ||    /|_|  |_|");
        System.out.println(" /  \\ ||  |  |  _  ||    \\ |     \\|     ||   [_     |   |   ||  _  ||    \\  |  |  ");
        System.out.println(" \\    ||  |  |  |  ||  .  \\|  .  ||     ||     |    |   |   ||  |  ||  .  \\ |  |  ");
        System.out.println("  \\___||__|  |__|__||__|\\_||__|\\_||_____||_____|    |___|___||__|__||__|\\_| |__|  ");
        System.out.println("                                                                                   ");
        System.out.println("Welcome to Sparkle Mart! What can we help you today?");
        System.out.println("1. Member Management \n2. Transaction\n3. Redemption \n4. Report \n5. Redemption Management\n6. Rewards Management\n7. Exit");
    }
    
    public void contactManagement(Scanner scanner) {
        int choice;
        
        do {
            Contact.userMenu();
            
            try {
                choice = scanner.nextInt();
                System.out.println("Choice entered by user: " + choice); 
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid menu choice.");
                scanner.nextLine(); 
                continue; 
            }
            
            switch (choice) {
                case 1:
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    Administrator.Menu();
                    Administrator.Adminoperation();
                    break;
                case 2:
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    Customer.Menu();
                    Customer.Customeroperation();
                    break;
                case 3:
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    main(null);
                    break;
                default:
                    System.out.println("Invalid menu choice. Please try again.");
                    break;
            }
        } while (true); 
    }          
    
    public static void reporting(Scanner scanner) {
        UserReport userReport = new UserReport();
        SalesReport salesReport = new SalesReport();
        PointsReport pointsReport = new PointsReport();
        int choice;
        boolean validInput = false; 
        do {
            Report.userMenu();
            if (scanner.hasNextInt()) { 
                choice = scanner.nextInt();
                System.out.println("Choice entered by user: " + choice); 
                validInput = true; 
            } else {
                System.out.println("Invalid input or no input available. Please try again.");
                scanner.nextLine(); 
                validInput = false; 
                continue; 
            }
            switch (choice) {
                case 1:
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    userReport.userReportOperation();
                    break;
                case 2:
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    salesReport.SalesReportOperation();
                    break;
                case 3:
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    pointsReport.pointsReportOperation();
                    break;
                case 4:
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    RewardsReport.rewardsReport();
                    break;
                case 5:
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    Report.deletion();
                    break;
                case 6:
                    Main.main(null);
                    break;
                default:
                    System.out.println("Invalid menu choice. Please try again.");
                    validInput = false; 
                    break;
            }
        } while (!validInput); 
    }
    

}
    
    
