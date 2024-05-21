package assignment;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("resource")
public class Claims {

    public static void handleRedemptionProcess() {
        readAndDisplayRedemptionRecords();
        Scanner scanner = new Scanner(System.in);
        String chosenRedemptionID;
        do {
            System.out.print("Choose a redemption ID to proceed (type 'cancel' to return to Main Menu): ");
            chosenRedemptionID = scanner.nextLine();
            if (chosenRedemptionID.equalsIgnoreCase("cancel")) {
                System.out.println("Returning to Main Menu...");
                System.out.print("\033[H\033[2J");
                System.out.flush();
                Main.main(null);
            }
        } while (!isRedemptionIDValid(chosenRedemptionID));
        handleRedemption(chosenRedemptionID);
        removeDuplicateRecords();
        System.out.println("Press Enter to return to Main Menu...");
        scanner.nextLine(); 
        System.out.print("\033[H\033[2J");
        System.out.flush();
        Main.main(null);
    }

    public static void readAndDisplayRedemptionRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader("redemption_records.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!isHandledRecord(line)) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading redemption records: " + e.getMessage());
        }
    }

    public static void handleRedemption(String redemptionID) {
        try (BufferedReader reader = new BufferedReader(new FileReader("redemption_records.txt"))) {
            StringBuilder newRecords = new StringBuilder();
            boolean found = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(redemptionID)) {
                    found = true;
                    String[] fields = line.split(",");
                    if (fields[5].contains("Cash Voucher")) {
                        int voucherAmount = Integer.parseInt(fields[5].replaceAll("[^0-9]", ""));
                        int voucherQuantity = Integer.parseInt(fields[6]);
                        if (voucherQuantity > 0) {
                            StringBuilder voucherCodes = new StringBuilder();
                            for (int i = 0; i < voucherQuantity; i++) {
                                String voucherCode = generateVoucherCode();
                                voucherCodes.append(voucherCode);
                                if (i < voucherQuantity - 1) {
                                    voucherCodes.append(",");
                                }
                                try (FileWriter writer = new FileWriter("voucher.txt", true)) {
                                    writer.write(voucherCode + "," + voucherAmount + "\n");
                                } catch (IOException e) {
                                    System.out.println("Error writing to voucher.txt: " + e.getMessage());
                                }
                            }
                            String updatedLine = line + ",(Sent: " + getCurrentDate() + "),(Voucher codes: " + voucherCodes + ")";
                            newRecords.append(updatedLine).append("\n");
                            System.out.println("Voucher codes are sent to " + fields[3] + " Voucher Codes: " + voucherCodes);
                        }
                    } else {
                        System.out.println("Item (" + fields[5] + ") will be sent to " + fields[4]);
                        String dispatchDate = getCurrentDate();
                        String expectedDeliveryDate = addDaysToCurrentDate(5);
                        String updatedLine = line + ",(Dispatched: " + dispatchDate + "),(expected delivery: " + expectedDeliveryDate + ")";
                        newRecords.append(updatedLine).append("\n");
                        if (isToday(expectedDeliveryDate)) {
                            updatedLine += ",(delivered)";
                        }
                        newRecords.append(updatedLine).append("\n");
                    }
                } else {
                    newRecords.append(line).append("\n");
                }
            }
            if (!found) {
                System.out.println("Redemption ID not found. Please try again.");
            } else {
                updateRedemptionRecords(newRecords.toString());
            }
        } catch (IOException e) {
            System.out.println("Error handling redemption: " + e.getMessage());
        }
    } 

    private static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return formatter.format(date);
    }

    private static String addDaysToCurrentDate(int days) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return formatter.format(calendar.getTime());
    }

    private static boolean isToday(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateStr = formatter.format(new Date());
        return currentDateStr.equals(dateStr);
    }

    private static void updateRedemptionRecords(String newRecords) {
        try (FileWriter writer = new FileWriter("redemption_records.txt")) {
            writer.write(newRecords);
            System.out.println("Redemption records updated successfully.");
        } catch (IOException e) {
            System.out.println("Error updating redemption records: " + e.getMessage());
        }
    }

    private static String generateVoucherCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder voucherCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            voucherCode.append(characters.charAt(random.nextInt(characters.length())));
        }
        return voucherCode.toString();
    }

    private static boolean isHandledRecord(String line) {
        return line.contains("(Sent:") || line.contains("(Dispatched:") || line.contains("(delivered)");
    }

    public static boolean isRedemptionIDValid(String redemptionID) {
        try (BufferedReader reader = new BufferedReader(new FileReader("redemption_records.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(redemptionID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading redemption records: " + e.getMessage());
        }
        System.out.println("Redemption ID not found. Please try again.");
        return false;
    }

    public static void removeDuplicateRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader("redemption_records.txt"))) {
            List<String> redemptionRecords = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                redemptionRecords.add(line);
            }
            Set<String> seenRedemptionIDs = new HashSet<>();
            List<String> uniqueRecords = new ArrayList<>();
            for (String record : redemptionRecords) {
                String redemptionID = record.split(",")[0];
                if (!seenRedemptionIDs.contains(redemptionID)) {
                    uniqueRecords.add(record);
                    seenRedemptionIDs.add(redemptionID);
                }
            }
            try (FileWriter writer = new FileWriter("redemption_records.txt")) {
                for (String uniqueRecord : uniqueRecords) {
                    writer.write(uniqueRecord + "\n");
                }
                System.out.println("Duplicate records removed successfully.");
            } catch (IOException e) {
                System.out.println("Error writing redemption records: " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error reading redemption records: " + e.getMessage());
        }
    }

    public static void userMenu(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(" _______   ________  _______   ________  __       __  _______   ________  ______   ______   __    __ ");
        System.out.println("/       \\ /        |/       \\ /        |/  \\     /  |/       \\ /        |/      | /      \\ /  \\  /  |");
        System.out.println("$$$$$$$  |$$$$$$$$/ $$$$$$$  |$$$$$$$$/ $$  \\   /$$ |$$$$$$$  |$$$$$$$$/ $$$$$$/ /$$$$$$  |$$  \\ $$ |");
        System.out.println("$$ |__$$ |$$ |__    $$ |  $$ |$$ |__    $$$  \\ /$$$ |$$ |__$$ |   $$ |     $$ |  $$ |  $$ |$$$  \\$$ |");
        System.out.println("$$    $$< $$    |   $$ |  $$ |$$    |   $$$$  /$$$$ |$$    $$/    $$ |     $$ |  $$ |  $$ |$$$$  $$ |");
        System.out.println("$$$$$$$  |$$$$$/    $$ |  $$ |$$$$$/    $$ $$ $$/$$ |$$$$$$$/     $$ |     $$ |  $$ |  $$ |$$ $$ $$ |");
        System.out.println("$$ |  $$ |$$ |_____ $$ |__$$ |$$ |_____ $$ |$$$/ $$ |$$ |         $$ |    _$$ |_ $$ \\__$$ |$$ |$$$$ |");
        System.out.println("$$ |  $$ |$$       |$$    $$/ $$       |$$ | $/  $$ |$$ |         $$ |   / $$   |$$    $$/ $$ | $$$ |");
        System.out.println("$$/   $$/ $$$$$$$$/ $$$$$$$/  $$$$$$$$/ $$/      $$/ $$/          $$/    $$$$$$/  $$$$$$/  $$/   $$/ ");
        System.out.println("                                                                                                      ");
        System.out.println("                                                                                                      ");
        System.out.println("                                                                                                      ");
    }
}
