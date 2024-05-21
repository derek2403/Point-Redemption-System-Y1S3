package assignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class RewardsReport extends Report {

    static class RewardInfo {
        String rewardID;
        String rewardName;
        int redeemedQuantity;

        public RewardInfo(String rewardID, String rewardName, int redeemedQuantity) {
            this.rewardID = rewardID;
            this.rewardName = rewardName;
            this.redeemedQuantity = redeemedQuantity;
        }
    }

    @Override
    public void generateReport(String reportType) {
        List<RewardInfo> rewardInfoList = new ArrayList<>();

        try (BufferedReader redemptionReader = new BufferedReader(new FileReader("redemption.txt"))) {
            String redemptionLine;
            while ((redemptionLine = redemptionReader.readLine()) != null) {
                String[] redemptionParts = redemptionLine.split(",");
                String rewardID = redemptionParts[2];
                int quantity = Integer.parseInt(redemptionParts[3]);
                String rewardName = getRewardName(rewardID);
                updateRewardInfoList(rewardInfoList, rewardID, rewardName, quantity);
            }
        } catch (IOException e) {
            System.out.println("Error reading redemption data: " + e.getMessage());
        }

        Collections.sort(rewardInfoList, Comparator.comparingInt(info -> -info.redeemedQuantity));

        String fileName = "rewardshistory.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (RewardInfo rewardInfo : rewardInfoList) {
                writer.write(rewardInfo.rewardName + " - " + rewardInfo.redeemedQuantity + "\n");
            }
            System.out.println("Rewards report generated successfully and saved to " + fileName + ".");
            Report.addGeneratedFile(fileName);
        } catch (IOException e) {
            System.out.println("Error writing rewards report: " + e.getMessage());
        }
    }

    private static String getRewardName(String rewardID) throws IOException {
        try (BufferedReader rewardsReader = new BufferedReader(new FileReader("rewards.txt"))) {
            String rewardsLine;
            while ((rewardsLine = rewardsReader.readLine()) != null) {
                String[] rewardsParts = rewardsLine.split(",");
                if (rewardsParts[0].equals(rewardID)) {
                    return rewardsParts[1];
                }
            }
        }
        return "Unknown Reward";
    }

    private static void updateRewardInfoList(List<RewardInfo> rewardInfoList, String rewardID, String rewardName, int quantity) {
        for (RewardInfo rewardInfo : rewardInfoList) {
            if (rewardInfo.rewardID.equals(rewardID)) {
                rewardInfo.redeemedQuantity += quantity;
                return;
            }
        }
        rewardInfoList.add(new RewardInfo(rewardID, rewardName, quantity));
    }

    public static void rewardsReport(){
        Scanner scanner = new Scanner(System.in);
        RewardsReport rewardsReport = new RewardsReport();
        rewardsReport.generateReport("Rewards");
        System.out.println("Press Any Key to return...");
        scanner.nextLine(); 
        System.out.print("\033[H\033[2J");
        System.out.flush();
        Main.reporting(scanner);
    }
    
}