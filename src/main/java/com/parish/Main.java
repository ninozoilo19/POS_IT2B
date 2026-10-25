package com.parish;

import com.parish.dao.DatabaseInitializer;
import com.parish.dao.ParishSystemDAO;
import com.parish.model.SacramentalRecord;
import com.parish.model.User;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initializeDatabase();
        ParishSystemDAO dao = new ParishSystemDAO();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=========================================");
        System.out.println("  PARISH OFFICE RECORDS SYSTEM (CONSOLE)  ");
        System.out.println("=========================================");

        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = dao.login(username, password);
        if (user == null) {
            System.out.println("Invalid credentials! Exiting.");
            return;
        }

        System.out.println("\nWelcome, " + user.getFullName() + " (" + user.getRole() + ")!");

        boolean running = true;
        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. View All Sacramental Records");
            if (!user.getRole().equals("PRIEST")) {
                System.out.println("2. Add Parishioner & Sacramental Record");
            }
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    List<SacramentalRecord> records = dao.getAllRecords();
                    System.out.println("\n--- SACRAMENTAL RECORDS ---");
                    for (SacramentalRecord r : records) {
                        System.out.println("ID: " + r.getRecordId() +
                                " | Type: " + r.getSacramentType() +
                                " | Date: " + r.getSacramentDate() +
                                " | Priest: " + r.getOfficiatingPriest());
                    }
                    break;
                case 2:
                    if (user.getRole().equals("PRIEST")) {
                        System.out.println("Access denied. Priests have read-only permissions.");
                        break;
                    }
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

                    boolean success = dao.addParishionerAndRecord(fName, lName, dob, contact, type, date, priest, book, page);
                    if (success) {
                        System.out.println("Record successfully added!");
                    } else {
                        System.out.println("Failed to add record.");
                    }
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }
}