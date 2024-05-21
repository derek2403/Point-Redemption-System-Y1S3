package assignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
abstract class Report {
    private String reportID;
    private Date date;
    private String adminID;
    private static final AtomicInteger reportCounter = new AtomicInteger(1);

    public Report(String reportID, Date date, String adminID) {
        this.reportID = reportID;
        this.date = date;
        this.adminID = adminID;
    }

    public Report(String adminID) {
        this.reportID = generateUniqueReportID();
        this.date = new Date();
        this.adminID = adminID;
    }

    public Report() {
    }

    abstract void generateReport(String reportType);

    public static void addGeneratedFile(String fileName) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = formatter.format(new Date());
        String reportId = generateNextReportID();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("generatedFiles.txt", true))) {
            writer.write(reportId + "," + fileName + "," + timestamp + "\n");
            System.out.println("Report ID '" + reportId + "', file name '" + fileName + "', with timestamp '" + timestamp + "' appended to generatedFiles.txt successfully.");
        } catch (IOException e) {
            System.out.println("Error appending file name to generatedFiles.txt: " + e.getMessage());
        }
    }

    public static String generateUniqueReportID() {
        int reportID = reportCounter.getAndIncrement();
        if (reportID > 999) {
            reportCounter.set(1);
            reportID = 1;
        }
        return String.format("R%03d", reportID);
    }
    
    private static String generateNextReportID() {
        int lastReportID = getLastReportID();
        if (lastReportID >= 999) {
            lastReportID = 0;
        }
        lastReportID++;
        return String.format("R%03d", lastReportID);
    }
    
    private static int getLastReportID() {
        int lastReportID = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("generatedFiles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String reportIDString = parts[0].substring(1); // Remove the "R" prefix
                int reportID = Integer.parseInt(reportIDString);
                if (reportID > lastReportID) {
                    lastReportID = reportID;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading generated files data: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid report ID format: " + e.getMessage());
        }
        return lastReportID;
    }

    public static void deleteRecord(String reportIdToDelete) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("generatedFiles.txt"));
            List<String> updatedLines = new ArrayList<>();
            for (String line : lines) {
                if (line.startsWith(reportIdToDelete)) {
                    String[] parts = line.split(",");
                    String fileName = parts[1].trim();
                    Path filePath = Paths.get(fileName);
                    try {
                        Files.deleteIfExists(filePath);
                        System.out.println("File '" + fileName + "' deleted successfully.");
                    } catch (IOException e) {
                        System.out.println("Error deleting file '" + fileName + "': " + e.getMessage());
                    }
                    line += " (deleted)";
                }
                updatedLines.add(line);
            }
            Files.write(Paths.get("generatedFiles.txt"), updatedLines);
            System.out.println("Files associated with report ID '" + reportIdToDelete + "' deleted successfully.");
        } catch (IOException e) {
            System.out.println("Error deleting files associated with report ID '" + reportIdToDelete + "': " + e.getMessage());
        }
    }

    public static void deletion() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("generatedFiles.txt"));
            System.out.println("Report Created:");
            for (String line : lines) {
                String[] parts = line.split(",");
                System.out.println(parts[0] + " : " + parts[1]);
            }
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the report id(s) to be deleted (separated by commas):");
            String input = scanner.nextLine();
            String[] reportIds = input.split(",");
            for (String reportId : reportIds) {
                deleteRecord(reportId.trim());
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Error reading generatedFiles.txt: " + e.getMessage());
        }
    }

    public static void userMenu() {
    System.out.println(" _______   ________  _______    ______   _______   ________ ");
    System.out.println("/       \\ /        |/       \\  /      \\ /       \\ /        |");
    System.out.println("$$$$$$$  |$$$$$$$$/ $$$$$$$  |/$$$$$$  |$$$$$$$  |$$$$$$$$/ ");
    System.out.println("$$ |__$$ |$$ |__    $$ |__$$ |$$ |  $$ |$$ |__$$ |   $$ |   ");
    System.out.println("$$    $$< $$    |   $$    $$/ $$ |  $$ |$$    $$<    $$ |   ");
    System.out.println("$$$$$$$  |$$$$$/    $$$$$$$/  $$ |  $$ |$$$$$$$  |   $$ |   ");
    System.out.println("$$ |  $$ |$$ |_____ $$ |      $$ \\__$$ |$$ |  $$ |   $$ |   ");
    System.out.println("$$ |  $$ |$$       |$$ |      $$    $$/ $$ |  $$ |   $$ |   ");
    System.out.println("$$/   $$/ $$$$$$$$/ $$/        $$$$$$/  $$/   $$/    $$/    ");
    System.out.println("                                                            ");
    System.out.println("                                                            ");
    System.out.println("Choose the Report Operations.\n1. Create User Report\n2. Create Sales Report\n3. Create Points Report\n4. Create Rewards Report\n5. Delete Report\n6. Back to Main Menu");
}

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        deletion();
        System.out.println("Press Any Key to return...");
        scanner.nextLine(); 
        System.out.print("\033[H\033[2J");
        System.out.flush();
        Main.reporting(scanner);
    }
}




