package assignment;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

class UserReport extends Report {
    public UserReport(String reportID, Date date, String adminID) {
        super(reportID, date, adminID);
    }

    public UserReport() {
        
    }

    public UserReport(String adminID){

    }

    public String[][] getAdminInfo() {
        String[][] adminInfo = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("admin.txt"))) {
            List<String[]> adminList = new ArrayList<>();
            String line;
    
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    parts[6] = parts[6].trim(); 
                    parts = Arrays.copyOf(parts, parts.length - 1);
                    adminList.add(parts); 
                } else {
                    System.out.println("Invalid data format in admin.txt: " + line);
                }
            }
    
            adminInfo = new String[adminList.size()][];
            adminList.toArray(adminInfo);
    
        } catch (IOException e) {
            System.out.println("Error reading admin data from file: " + e.getMessage());
        }
    
        return adminInfo;
    }
    

    public String[][] getCustomerInfo() {
        String[][] customerInfo = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("customer.txt"))) {
            List<String[]> customerList = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                customerList.add(parts); 
            }

            customerInfo = new String[customerList.size()][];
            customerList.toArray(customerInfo);

        } catch (IOException e) {
            System.out.println("Error reading customer data from file: " + e.getMessage());
        }

        return customerInfo;
    }

    @Override
    public void generateReport(String reportType) {
        String[][] reportData = null;

        switch (reportType.toLowerCase()) {
            case "admin":
                reportData = getAdminInfo();
                insertionSortAdmins(reportData);
                break;
            case "customer":
                reportData = getCustomerInfo();
                insertionSortCustomers(reportData);
                break;
            default:
                System.out.println("Invalid report type. Please specify either 'admin' or 'customer'.");
                return;
        }

        if (reportData == null) {
            System.out.println("Error: Unable to retrieve report data.");
            return;
        }

        String fileName = reportType.equalsIgnoreCase("admin") ? "AdminReport.xlsx" : "CustomerReport.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(reportType + " Report");

            Row headerRow = sheet.createRow(0);
            String[] headers = reportType.equalsIgnoreCase("admin") ?
                new String[]{"Admin ID", "Name", "Email", "Address", "Gender", "Phone Number", "Department"} :
                new String[]{"Customer ID", "Name", "Email", "Address", "Gender", "Phone Number", "Points"};

            for (int col = 0; col < headers.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers[col]);
            }

            for (int row = 0; row < reportData.length; row++) {
                Row dataRow = sheet.createRow(row + 1); 
                for (int col = 0; col < reportData[row].length; col++) {
                    Cell cell = dataRow.createCell(col);
                    cell.setCellValue(reportData[row][col]);
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }
            System.out.println("Excel file '" + fileName + "' created successfully.");
            Report.addGeneratedFile(fileName);
        } catch (IOException e) {
            System.out.println("Error creating Excel file: " + e.getMessage());
        }
    }

    private void insertionSortCustomers(String[][] customers) {
        for (int i = 1; i < customers.length; i++) {
            String[] currentCustomer = customers[i];
            int j = i - 1;
            while (j >= 0 && Integer.parseInt(customers[j][6]) < Integer.parseInt(currentCustomer[6])) {
                customers[j + 1] = customers[j];
                j--;
            }
            customers[j + 1] = currentCustomer;
        }
    }

    private void insertionSortAdmins(String[][] admins) {
        for (int i = 1; i < admins.length; i++) {
            String[] currentAdmin = admins[i];
            int j = i - 1;
            while (j >= 0 && admins[j][6].compareToIgnoreCase(currentAdmin[6]) > 0) {
                admins[j + 1] = admins[j];
                j--;
            }
            admins[j + 1] = currentAdmin;
        }
    }

    public void userReportOperation() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("You've selected User Report.\nChoose the report to be generated:\n1. Admin\n2. Customer\n3. Back");
        int choice = scanner.nextInt();
        UserReport userReport = new UserReport();
        
        switch (choice) {
            case 1:
                userReport.generateReport("admin");
                userReportOperation();
                break;
            case 2:
                userReport.generateReport("customer");
                userReportOperation();
                break;
            case 3:
                Main.reporting(scanner);
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
        
    }


    public static void main(String[] args) {
        UserReport userReport = new UserReport("12345", new Date(), "admin123");
        userReport.userReportOperation();
    }
}
