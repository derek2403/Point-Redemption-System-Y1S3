package assignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Policy {
    private static AtomicInteger deductionCounter = new AtomicInteger(getLastDeductionID() + 1);

    public void updateLastPurchaseDays() {
        String lastCustomerID = null;
        Date lastTransactionDate = null;
        int initialPoints = 0;
        int daysSinceLastPurchase = 0;
        int pointsToDeduct = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("transaction.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("last_purchase_days.txt"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String customerID = parts[1];
                    Date transactionDate = parseDate(parts[2]);

                    if (!customerID.equals(lastCustomerID) || transactionDate.after(lastTransactionDate)) {
                        if (lastTransactionDate != null) {
                            daysSinceLastPurchase = calculateDaysSinceLastTransaction(lastTransactionDate);
                            pointsToDeduct = calculatePointsToDeduct(daysSinceLastPurchase, initialPoints);
                            writer.write(lastCustomerID + "," + daysSinceLastPurchase + "," + initialPoints + "," + pointsToDeduct);
                            writer.newLine();
                        }
                        lastCustomerID = customerID;
                        lastTransactionDate = transactionDate;
                        initialPoints = getInitialPoints(customerID);
                    }
                }
                
            }

            if (lastTransactionDate != null) {
                daysSinceLastPurchase = calculateDaysSinceLastTransaction(lastTransactionDate);
                pointsToDeduct = calculatePointsToDeduct(daysSinceLastPurchase, initialPoints);
                writer.write(lastCustomerID + "," + daysSinceLastPurchase + "," + initialPoints + "," + pointsToDeduct);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int calculateDaysSinceLastTransaction(Date lastTransactionDate) {
        Date currentDate = new Date();
        long diff = currentDate.getTime() - lastTransactionDate.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    private int calculatePointsToDeduct(int daysInactive, int initialPoints) {
        if (daysInactive < 90) {
            return 0;
        } else if (daysInactive == 90) {
            return (int) (0.25 * initialPoints);
        } else if (daysInactive == 180) {
            return (int) (0.5 * initialPoints);
        } else if (daysInactive == 365){
            return (int) (initialPoints);
        } else {
            return 0;
        }
    }

    private Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getInitialPoints(String customerID) {
        try (BufferedReader reader = new BufferedReader(new FileReader("customer.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length > 6 && fields[0].equals(customerID.trim())) {
                    return Integer.parseInt(fields[6].trim());
                }
            }
            System.out.println("Customer ID not found or missing initial points.");
        } catch (IOException e) {
            System.out.println("Error retrieving customer points: " + e.getMessage());
        }
        return 0;
    }

    public void removeDuplicateCustomers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("last_purchase_days.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("temp.txt"))) {

            List<String> lines = new ArrayList<>();
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                lines.add(currentLine);
            }

            for (int i = 0; i < lines.size(); i++) {
                if (!isDuplicate(i, lines)) {
                    writer.write(lines.get(i));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            java.nio.file.Files.move(java.nio.file.Paths.get("temp.txt"),
                    java.nio.file.Paths.get("last_purchase_days.txt"),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isDuplicate(int currentIndex, List<String> lines) {
        String[] currentParts = lines.get(currentIndex).split(",");
        int currentDays = Integer.parseInt(currentParts[1]);
        String currentCustomerID = currentParts[0];

        for (int i = currentIndex + 1; i < lines.size(); i++) {
            String[] nextParts = lines.get(i).split(",");
            int nextDays = Integer.parseInt(nextParts[1]);
            String nextCustomerID = nextParts[0];

            if (currentCustomerID.equals(nextCustomerID) && nextDays < currentDays) {
                return true;
            }
        }
        return false;
    }

    public void updateCustomerPoints() {
        try (BufferedReader lastPurchaseReader = new BufferedReader(new FileReader("last_purchase_days.txt"))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("customer_temp.txt"))) {
                String lastPurchaseLine;
                while ((lastPurchaseLine = lastPurchaseReader.readLine()) != null) {
                    String[] lastPurchaseParts = lastPurchaseLine.split(",");
                    if (lastPurchaseParts.length == 4) {
                        String customerID = lastPurchaseParts[0];
                        int pointsToDeduct = Integer.parseInt(lastPurchaseParts[3]);

                        try (BufferedReader customerReader = new BufferedReader(new FileReader("customer.txt"))) {
                            String customerLine;
                            while ((customerLine = customerReader.readLine()) != null) {
                                String[] customerParts = customerLine.split(",");
                                if (customerParts.length > 6 && customerParts[0].equals(customerID)) {
                                    int currentPoints = Integer.parseInt(customerParts[6].trim());
                                    int updatedPoints = Math.max(0, currentPoints - pointsToDeduct);
                                    customerParts[6] = Integer.toString(updatedPoints);
                                    customerLine = String.join(",", customerParts);
                                    // Write the updated customer information once
                                    writer.write(customerLine);
                                    writer.newLine();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            java.nio.file.Files.move(java.nio.file.Paths.get("customer_temp.txt"),
                    java.nio.file.Paths.get("customer.txt"),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public void createDeductionRecord() {
        try (BufferedReader reader = new BufferedReader(new FileReader("last_purchase_days.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("deduction.txt", true))) {
    
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String customerID = parts[0];
                int daysSinceLastPurchase = Integer.parseInt(parts[1]);
                int initialPoints = Integer.parseInt(parts[2]);
                int pointsToDeduct = Integer.parseInt(parts[3]);
    
                if (pointsToDeduct > 0) {
                    if (daysSinceLastPurchase == 90 || daysSinceLastPurchase == 180 || daysSinceLastPurchase == 365) {
                        String deductionID = generateUniqueDeductionID();
                        Date currentDate = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String dateString = dateFormat.format(currentDate);
    
                        String record = deductionID + "," + customerID + "," + pointsToDeduct + "," + dateString;
                        writer.write(record);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    

    public static String generateUniqueDeductionID() {
        int deductionID = deductionCounter.getAndIncrement();
        if (deductionID > 999) {
            deductionCounter.set(1);
            deductionID = 1;
        }
        return String.format("P%03d", deductionID);
    }
    
    private static int getLastDeductionID() {
        int lastDeductionID = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("deduction.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String deductionIDString = parts[0].substring(1); // Remove the "P" prefix
                int deductionID = Integer.parseInt(deductionIDString);
                if (deductionID > lastDeductionID) {
                    lastDeductionID = deductionID;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading deduction data: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid deduction ID format: " + e.getMessage());
        }
        return lastDeductionID;
    }
    
    public static void policyProcessor() {
        if (!isProcessorLoggedToday()) {
            logProcessor();
            Policy processor = new Policy();
            processor.updateLastPurchaseDays();
            processor.removeDuplicateCustomers();
            processor.updateCustomerPoints();
            processor.createDeductionRecord();
        } else {
            System.out.println("Processor already run today.");
        }
    }
    private static void logProcessor() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("processorlog.txt", true))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(new Date());
            writer.write(currentDate);
            writer.newLine();
            System.out.println("Processor logged at: " + currentDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static boolean isProcessorLoggedToday() {
    try {
        String lastLine = Files.lines(Paths.get("processorlog.txt")).reduce((a, b) -> b).orElse(null);
        if (lastLine != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String lastLoggedDate = lastLine.split(" ")[0];
            String currentDate = dateFormat.format(new Date());
            return currentDate.equals(lastLoggedDate);
        }
    } catch (IOException e) {
        return false;
    }
    return false;
}

}
