package assignment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

class SalesReport extends Report {
    public SalesReport(String reportID, Date date, String adminID) {
        super(reportID, date, adminID);
    }

    public SalesReport(){

    }

    public SalesReport(String adminID){
        
    }

    public void calculateTotalSales() {
        String[][] transactionHistory = readTransactionHistory();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select report type:");
        System.out.println("1. Monthly");
        System.out.println("2. Yearly");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.println("Enter the year:");
                int year = scanner.nextInt();
                calculateMonthlySales(transactionHistory, year);
                break;
            case 2:
                calculateYearlySales(transactionHistory);
                break;
            default:
                System.out.println("Invalid choice.");
        }
        scanner.close();
    }

    private void calculateMonthlySales(String[][] transactionHistory, int year) {
        double[] monthlySales = new double[12];

        for (int i = 0; i < monthlySales.length; i++) {
            monthlySales[i] = 0.0;
        }

        for (String[] transaction : transactionHistory) {
            String date = transaction[2];
            int transactionYear = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));

            if (transactionYear == year) {
                double amount = Double.parseDouble(transaction[3]);
                monthlySales[month - 1] += amount;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sales for " + year + ".txt"))) {
            writer.write("Monthly Sales for Year " + year + ":\n");
            for (int i = 0; i < monthlySales.length; i++) {
                String monthName = getMonthName(i + 1);
                String line = monthName + ": " + monthlySales[i];
                System.out.println(line);
                writer.write(line + "\n");
            }
            System.out.println("Sales data written to file: sales for " + year + ".txt");
            Report.addGeneratedFile("Sales for " + year + ".txt");
        } catch (IOException e) {
            System.out.println("Error writing sales data to file: " + e.getMessage());
        }
    }

    private void calculateYearlySales(String[][] transactionHistory) {
        List<Double> yearlySales = new ArrayList<>();
        List<Integer> years = new ArrayList<>();

        for (String[] transaction : transactionHistory) {
            int transactionYear = Integer.parseInt(transaction[2].substring(0, 4));
            if (!years.contains(transactionYear)) {
                years.add(transactionYear);
            }
        }

        for (int i = 0; i < years.size(); i++) {
            yearlySales.add(0.0);
        }

        for (String[] transaction : transactionHistory) {
            int transactionYear = Integer.parseInt(transaction[2].substring(0, 4));
            double amount = Double.parseDouble(transaction[3]);
            int index = years.indexOf(transactionYear);
            yearlySales.set(index, yearlySales.get(index) + amount);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("yearly_sales.txt"))) {
            writer.write("Yearly Sales:\n");
            for (int i = 0; i < years.size(); i++) {
                int year = years.get(i);
                double totalSales = yearlySales.get(i);
                String line = year + ": " + totalSales;
                System.out.println(line);
                writer.write(line + "\n");
            }
            System.out.println("Sales data written to file: yearly_sales.txt");
            Report.addGeneratedFile("yearly_sales.txt");
        } catch (IOException e) {
            System.out.println("Error writing sales data to file: " + e.getMessage());
        }
    }

    private String getMonthName(int month) {
        switch (month) {
            case 1: return "Jan";
            case 2: return "Feb";
            case 3: return "Mar";
            case 4: return "Apr";
            case 5: return "May";
            case 6: return "Jun";
            case 7: return "Jul";
            case 8: return "Aug";
            case 9: return "Sep";
            case 10: return "Oct";
            case 11: return "Nov";
            case 12: return "Dec";
            default: return "";
        }
    }

    public String[][] readTransactionHistory() {
        List<String[]> transactionList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("transaction.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                transactionList.add(parts);
            }
        } catch (IOException e) {
            System.out.println("Error reading transaction history from file: " + e.getMessage());
        }
        String[][] transactionHistory = new String[transactionList.size()][];
        transactionList.toArray(transactionHistory);
        return transactionHistory;
    }

    public String[][] filterByCustomerID(String[][] transactionHistory, String customerID) {
        List<String[]> filteredTransactions = new ArrayList<>();
        for (String[] transaction : transactionHistory) {
            if (transaction[1].equals(customerID)) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions.toArray(new String[0][0]);
    }

    public String[][] filterByYear(String[][] transactionHistory, int year) {
        List<String[]> filteredTransactions = new ArrayList<>();
        for (String[] transaction : transactionHistory) {
            String date = transaction[2];
            if (date.startsWith(Integer.toString(year))) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions.toArray(new String[0][0]);
    }

    @Override
    public void generateReport(String reportType) {
        String[][] transactionHistory = readTransactionHistory();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select filtering option:");
        System.out.println("1. Filter by year");
        System.out.println("2. Filter by customer ID");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.println("Enter the year:");
                int year = scanner.nextInt();
                String[][] filteredTransactionsByYear = filterByYear(transactionHistory, year);
                String fileNameByYear = "Transaction_" + year + ".xlsx";
                exportToExcel(filteredTransactionsByYear, fileNameByYear);
                Report.addGeneratedFile(fileNameByYear);             
                break;
            case 2:
                System.out.println("Enter the customer ID:");
                String customerID = scanner.next();
                String[][] filteredTransactionsByCustomer = filterByCustomerID(transactionHistory, customerID);
                String fileNameByCustomer = "Transaction_" + customerID + ".xlsx";
                exportToExcel(filteredTransactionsByCustomer, fileNameByCustomer);
                Report.addGeneratedFile(fileNameByCustomer);
                break;
            default:
                System.out.println("Invalid choice.");
        }
        scanner.close();
        
    }

    private void exportToExcel(String[][] transactionHistory, String fileName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transaction Report");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Transaction ID", "Customer ID", "Transaction Date", "Amount (RM)"};
            for (int col = 0; col < headers.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers[col]);
            }

            for (int i = 0; i < transactionHistory.length; i++) {
                Row dataRow = sheet.createRow(i + 1); // Start from row 1, after the header
                for (int j = 0; j < transactionHistory[i].length; j++) {
                    Cell cell = dataRow.createCell(j);
                    cell.setCellValue(transactionHistory[i][j]);
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }
            System.out.println("Excel file '" + fileName + "' created successfully.");
        } catch (IOException e) {
            System.out.println("Error creating Excel file: " + e.getMessage());
        }
    }
    
    public void SalesReportOperation() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("You've selected Sales Report.\nDo you want to generate report for:\n1. Transaction\n2. Sales\n3. Exit");
        int salesChoice = scanner.nextInt();
        switch (salesChoice) {
            case 1:
                generateTransactionReport(scanner);
                System.out.println("Press Any Key to return...");
                scanner.nextLine(); 
                scanner.nextLine(); 
                System.out.print("\033[H\033[2J");
                System.out.flush();
                main(null);
                break;
            case 2:
                generateSalesReport(scanner);
                System.out.println("Press Any Key to return...");
                scanner.nextLine(); 
                scanner.nextLine(); 
                System.out.print("\033[H\033[2J");
                System.out.flush();
                main(null);
                break;
            case 3:
                Main.reporting(scanner);
            default:
                System.out.println("Invalid choice.");
                SalesReportOperation();
                break;
        }
        scanner.close();
    }
    
    private void generateTransactionReport(Scanner scanner) {
        String[][] transactionHistory = readTransactionHistory();
    
        System.out.println("Select filtering option:");
        System.out.println("1. Filter by year");
        System.out.println("2. Filter by customer ID");
        int choice = scanner.nextInt();
    
        switch (choice) {
            case 1:
                System.out.println("Enter the year:");
                int year = scanner.nextInt();
                String[][] filteredTransactionsByYear = filterByYear(transactionHistory, year);
                exportToExcel(filteredTransactionsByYear, "Transaction_" + year + ".xlsx");
                System.out.println("Press Any Key to return...");
                scanner.nextLine(); 
                scanner.nextLine(); 
                System.out.print("\033[H\033[2J");
                System.out.flush();
                main(null);
                break;
            case 2:
                System.out.println("Enter the customer ID:");
                String customerID = scanner.next();
                String[][] filteredTransactionsByCustomer = filterByCustomerID(transactionHistory, customerID);
                exportToExcel(filteredTransactionsByCustomer, "Transaction_" + customerID + ".xlsx");
                System.out.println("Press Any Key to return...");
                scanner.nextLine(); 
                scanner.nextLine(); 
                System.out.print("\033[H\033[2J");
                System.out.flush();
                main(null);
                break;
            default:
                System.out.println("Invalid choice.");
                generateTransactionReport(scanner);
                break;
        }
    }
    
    private void generateSalesReport(Scanner scanner) {
        System.out.println("Select report type:");
        System.out.println("1. Yearly");
        System.out.println("2. Monthly");
        int choice = scanner.nextInt();
    
        switch (choice) {
            case 1:
                calculateYearlySales(readTransactionHistory());
                break;
            case 2:
                System.out.println("Enter the year:");
                int year = scanner.nextInt();
                calculateMonthlySales(readTransactionHistory(), year);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }    
    

    public static void main(String[] args) {
        SalesReport salesReport = new SalesReport();
        salesReport.SalesReportOperation();
    }
}
