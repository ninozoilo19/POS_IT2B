package com.parish.ui;

import com.parish.dao.ActivityLogDao;
import com.parish.dao.UserDao;
import com.parish.model.ActivityLog;
import com.parish.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminUserMenu {
    private static final UserDao userDao = new UserDao();
    private static final ActivityLogDao logDao = new ActivityLogDao();

    public static void showAuditLogMenu(Scanner scanner, User adminUser) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- SYSTEM AUDIT LOGS MENU ---");
            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
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
                    List<ActivityLog> allLogs = logDao.getAllLogs();
                    printLogTable(allLogs);
                    break;
                case 2:
                    System.out.print("Enter User ID to filter: ");
                    if (scanner.hasNextInt()) {
                        int targetUserId = scanner.nextInt();
                        scanner.nextLine();
                        List<ActivityLog> userLogs = logDao.getLogsByUserId(targetUserId);
                        printLogTable(userLogs);
                    } else {
                        System.out.println("Invalid User ID format.");
                        scanner.next();
                    }
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public static void showUserManagementMenu(Scanner scanner, User adminUser) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- USER MANAGEMENT MENU ---");
            System.out.println("1. Register New User");
            System.out.println("2. Delete User Account");
            System.out.println("3. View All Users"); // <-- New Option
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    System.out.print("Enter full name: ");
                    String fullName = scanner.nextLine();
                    System.out.print("Enter role (PRIEST, STAFF, ADMIN): ");
                    String role = scanner.nextLine().toUpperCase();

                    boolean created = userDao.registerUser(username, password, fullName, role, adminUser.getUserId());
                    if (created) {
                        System.out.println("User successfully registered and logged!");
                    } else {
                        System.out.println("Registration failed. Username might already exist.");
                    }
                    break;
                case 2:
                    System.out.print("Enter User ID to delete: ");
                    if (scanner.hasNextInt()) {
                        int targetId = scanner.nextInt();
                        scanner.nextLine();

                        if (targetId == adminUser.getUserId()) {
                            System.out.println("Error: You cannot delete your own currently logged-in account!");
                            break;
                        }

                        boolean deleted = userDao.deleteUser(targetId, adminUser.getUserId());
                        if (deleted) {
                            System.out.println("User ID " + targetId + " successfully deleted and logged.");
                        } else {
                            System.out.println("Failed to delete user. User ID may not exist.");
                        }
                    } else {
                        System.out.println("Invalid User ID format.");
                        scanner.next();
                    }
                    break;
                case 3: // <-- New Case Handler
                    List<User> users = userDao.getAllUsers();
                    printUserTable(users);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void printUserTable(List<User> users) {
        System.out.println("\n" + "=".repeat(75));
        System.out.printf("%-8s | %-18s | %-25s | %-15s%n", "User ID", "Username", "Full Name", "Role");
        System.out.println("-".repeat(75));

        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (User u : users) {
                System.out.printf("%-8d | %-18s | %-25s | %-15s%n",
                        u.getUserId(),
                        u.getUsername(),
                        u.getFullName(),
                        u.getRole());
            }
        }
        System.out.println("=".repeat(75));
    }

    private static void printLogTable(List<ActivityLog> logs) {
        System.out.println("\n" + "=".repeat(100));
        System.out.printf("%-5s | %-8s | %-18s | %-35s | %-20s%n", "ID", "User ID", "Action Type", "Description", "Timestamp");
        System.out.println("-".repeat(100));

        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
        } else {
            for (ActivityLog log : logs) {
                System.out.printf("%-5d | %-8d | %-18s | %-35s | %-20s%n",
                        log.getId(),
                        log.getUserId(),
                        log.getActionType(),
                        log.getDescription(),
                        log.getTimestamp());
            }
        }
        System.out.println("=".repeat(100));
    }
}