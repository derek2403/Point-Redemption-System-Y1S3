package assignment;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class PointsReport extends Report {
    public PointsReport(String reportID, Date date, String adminID) {
        super(reportID, date, adminID);
    }

    public PointsReport(){

    }

    public List<String[]> readDeductionFile() {
        List<String[]> deductionRecords = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("deduction.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                deductionRecords.add(parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return deductionRecords;
    }

    @Override
    void generateReport(String reportType) {
        switch (reportType) {
            case "excel":
                exportToExcel();
                break;
            default:
                System.out.println("Invalid report type.");
        }
    }

    private void exportToExcel() {
        List<String[]> deductionRecords = readDeductionFile();

        String[] columns = {"PointID", "CustomerID", "PointsDeducted", "Date"};

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Points Deduction History");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (String[] record : deductionRecords) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < record.length; i++) {
                    row.createCell(i).setCellValue(record[i]);
                }
            }

            String fileName = "Points_Deduction_History.xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
                System.out.println("Excel file generated: " + fileName);
                Report.addGeneratedFile(fileName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pointsReportOperation(){
        Scanner scanner = new Scanner(System.in);
        PointsReport pointsReport = new PointsReport();
        pointsReport.generateReport("excel");
        System.out.println("Press Any Key to return...");
        scanner.nextLine(); 
        System.out.print("\033[H\033[2J");
        System.out.flush();
        Main.reporting(scanner);
    }
  
    public static void main(String[] args) {
        PointsReport report = new PointsReport("P001", new Date(), "Admin001");
        report.generateReport("excel");
    }
}
