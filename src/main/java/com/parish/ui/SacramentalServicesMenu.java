package com.parish.ui;

import com.parish.dao.UserDao;
import com.parish.model.SacramentalRecord;
import com.parish.model.User;

import java.util.List;
import java.util.Scanner;

public class SacramentalServicesMenu {
    private static final UserDao userDao = new UserDao();

    public static void showMenu(Scanner scanner, User currentUser) {
        boolean back = false;
        boolean canModify = !currentUser.getRole().equals("PRIEST");

        while (!back) {
            System.out.println("\n--- SACRAMENTAL SERVICES MENU ---");
            System.out.println("1. View All Sacramental Records");
            if (canModify) {
                System.out.println("2. Add Parishioner & Sacramental Record");
            }
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    List<SacramentalRecord> records = userDao.getAllRecords();
                    printRecordsTable(records);
                    break;
                case 2:
                    if (!canModify) {
                        System.out.println("Access denied. Priests have read-only permissions.");
                        break;
                    }
                    addRecordFlow(scanner, currentUser);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void printRecordsTable(List<SacramentalRecord> records) {
        String border = "+" + "-".repeat(6) + "+" + "-".repeat(16) + "+" + "-".repeat(14) + "+" + "-".repeat(26) + "+";
        System.out.println("\n" + border);
        System.out.printf("| %-4s | %-14s | %-12s | %-24s |%n",
                "ID", "Type", "Date", "Officiating Priest");
        System.out.println(border);

        if (records.isEmpty()) {
            System.out.printf("| %-67s |%n", "No sacramental records found.");
        } else {
            for (SacramentalRecord r : records) {
                System.out.printf("| %-4d | %-14s | %-12s | %-24s |%n",
                        r.getRecordId(),
                        safeStr(r.getSacramentType(), 14),
                        safeStr(r.getSacramentDate(), 12),
                        safeStr(r.getOfficiatingPriest(), 24));
            }
        }
        System.out.println(border);
    }

    private static String safeStr(String value, int maxLen) {
        if (value == null) return "";
        return value.length() > maxLen ? value.substring(0, maxLen) : value;
    }

    private static void addRecordFlow(Scanner scanner, User currentUser) {
        System.out.print("First Name: ");
        String fName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lName = scanner.nextLine();
        System.out.print("Date of Birth (YYYY-MM-DD): ");
        String dob = scanner.nextLine();
        System.out.print("Contact Number: ");
        String contact = scanner.nextLine();
        System.out.print("Sacrament Type (BAPTISM, CONFIRMATION, MARRIAGE, FUNERAL): ");
        String type = scanner.nextLine().toUpperCase();
        System.out.print("Sacrament Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Officiating Priest: ");
        String priest = scanner.nextLine();
        System.out.print("Registry Book Number: ");
        int book = scanner.nextInt();
        System.out.print("Registry Page Number: ");
        int page = scanner.nextInt();
        scanner.nextLine();

        boolean success = userDao.addParishionerAndRecord(
                currentUser, fName, lName, dob, contact, type, date, priest, book, page
        );
        if (success) {
            System.out.println("Record successfully added!");
        } else {
            System.out.println("Failed to add record.");
        }
    }
}