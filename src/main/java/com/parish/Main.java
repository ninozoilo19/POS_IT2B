package com.parish;

import com.parish.dao.DatabaseInitializer;
import com.parish.dao.UserDao;
import com.parish.model.User;
import com.parish.ui.AdminUserMenu;
import com.parish.ui.SacramentalServicesMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initializeDatabase();
        UserDao userDao = new UserDao();
        Scanner scanner = new Scanner(System.in);

        boolean appRunning = true;

        while (appRunning) {
            System.out.println("\n=========================================");
            System.out.println(" PARISH OFFICE RECORDS SYSTEM  ");
            System.out.println("=========================================");
            System.out.println("1. Login");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
                continue;
            }

            int authChoice = scanner.nextInt();
            scanner.nextLine();

            if (authChoice == 0) {
                System.out.println("Exiting system. Goodbye!");
                appRunning = false;
                continue;
            } else if (authChoice != 1) {
                System.out.println("Invalid option! Please choose 1 to Login or 0 to Exit.");
                continue;
            }

            User user = null;
            boolean loginCancelled = false;

            while (user == null) {
                System.out.print("Username: ");
                String username = scanner.nextLine();

                if (username.equals("0")) {
                    loginCancelled = true;
                    break;
                }

                System.out.print("Password: ");
                String password = scanner.nextLine();

                user = userDao.login(username, password);
                if (user == null) {
                    System.out.println("Invalid credentials! Please retype your username and password.");
                }
            }

            if (loginCancelled || user == null) {
                continue;
            }

            System.out.println("\nWelcome, " + user.getFullName() + " (" + user.getRole() + ")!");

            boolean loggedIn = true;
            while (loggedIn) {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1. Sacramental Services");
                System.out.println("2. View System Audit Logs");
                if (user.getRole().equals("ADMIN")) {
                    System.out.println("3. Manage User Accounts (Register/Delete)");
                }
                System.out.println("4. Logout");
                System.out.print("Choose an option: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input! Please enter a number.");
                    scanner.nextLine();
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        SacramentalServicesMenu.showMenu(scanner, user);
                        break;
                    case 2:
                        AdminUserMenu.showAuditLogMenu(scanner, user);
                        break;
                    case 3:
                        if (!user.getRole().equals("ADMIN")) {
                            System.out.println("Access denied. Admin privileges required.");
                            break;
                        }
                        AdminUserMenu.showUserManagementMenu(scanner, user);
                        break;
                    case 4:
                        userDao.logout(user);
                        System.out.println("You have been logged out.");
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        }

        scanner.close();
    }
}