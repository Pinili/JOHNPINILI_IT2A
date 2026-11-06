package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.model.ActivityLog;
import com.scc.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final ActivityLogDao activityLogDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {

        this.userDao = userDao;
        this.scanner = scanner;
        this.activityLogDao = new ActivityLogDao();
    }

    // ADMIN DASHBOARD
    public void display(User admin) {

        if (!"ADMIN".equalsIgnoreCase(admin.roles())) {
            System.out.println("Access denied. Admin only.");
            return;
        }

        boolean running = true;

        while (running) {

            System.out.println("\n==========================================");
            System.out.printf("%27s%n", "ADMIN DASHBOARD");
            System.out.println("==========================================");

            System.out.println("Logged in as: " + admin.username());

            System.out.println("\n1. View All Users");
            System.out.println("2. View Pending Users");
            System.out.println("3. Approve User Registration");
            System.out.println("4. Add New User");
            System.out.println("5. Update User Role / Status");
            System.out.println("6. Delete User Account");
            System.out.println("7. View System Audit Logs");
            System.out.println("8. Logout");

            System.out.print("\nChoose an option (1-8): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> viewAllUsers();

                case "2" -> viewPendingUsers();

                case "3" -> approveUser(admin);

                case "4" -> addUser(admin);

                case "5" -> updateUser(admin);

                case "6" -> deleteUser(admin);

                case "7" -> auditLogMenu(admin);

                case "8" -> {
                    running = false;
                    System.out.println("Admin logged out successfully.");
                }

                default ->
                        System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // ================= USER MANAGEMENT =================

    // VIEW ALL USERS
    private void viewAllUsers() {

        System.out.println("\n========== ALL USERS ==========");

        try {

            List<User> users = userDao.getAllUsers();

            if (users.isEmpty()) {
                System.out.println("No users found.");
                return;
            }

            printUsers(users);

        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
    }

    // VIEW PENDING USERS
    private void viewPendingUsers() {

        System.out.println("\n========== PENDING USERS ==========");

        try {

            List<User> users = userDao.getPendingUsers();

            if (users.isEmpty()) {
                System.out.println("No pending users found.");
                return;
            }

            printUsers(users);

        } catch (SQLException e) {
            System.out.println("Error retrieving pending users: " + e.getMessage());
        }
    }

    // APPROVE USER
    private void approveUser(User admin) {

        System.out.println("\n========== APPROVE USER ==========");

        try {

            List<User> users = userDao.getPendingUsers();

            if (users.isEmpty()) {
                System.out.println("No pending users found.");
                return;
            }

            printUsers(users);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        int userId = readInt("\nEnter user ID to approve: ");

        if (userId <= 0) {
            System.out.println("Invalid user ID.");
            return;
        }

        try {

            boolean success = userDao.approveUser(userId);

            if (success) {

                System.out.println("\nUser approved successfully!");

                recordLog(
                        admin.id(),
                        "USER_APPROVED",
                        "Admin " + admin.id()
                                + " approved account for User ID " + userId
                );

                viewAllUsers();

            } else {

                System.out.println("User not found or not pending.");
            }

        } catch (SQLException e) {
            System.out.println("Error approving user: " + e.getMessage());
        }
    }

    // ADD NEW USER
    private void addUser(User admin) {

        System.out.println("\n========== ADD NEW USER ==========");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (username.isBlank() || password.isBlank()) {
            System.out.println("Username and password are required.");
            return;
        }

        String role = selectRole();

        if (role == null) {
            System.out.println("Invalid role selection.");
            return;
        }

        String status = selectStatus();

        if (status == null) {
            System.out.println("Invalid status selection.");
            return;
        }

        try {

            if (userDao.usernameExists(username)) {
                System.out.println("Username already exists.");
                return;
            }

            boolean success = userDao.addUser(
                    username,
                    password,
                    role,
                    status
            );

            if (success) {

                System.out.println("User added successfully.");

                recordLog(
                        admin.id(),
                        "USER_CREATED",
                        "Admin " + admin.id()
                                + " created account for " + username
                                + " with role " + role
                );

            } else {

                System.out.println("Failed to add user.");
            }

        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    // UPDATE USER ROLE AND STATUS
    private void updateUser(User admin) {

        System.out.println("\n========== UPDATE USER ==========");

        viewAllUsers();

        int userId = readInt("\nEnter user ID to update: ");

        if (userId <= 0) {
            System.out.println("Invalid user ID.");
            return;
        }

        String role = selectRole();

        if (role == null) {
            System.out.println("Invalid role selection.");
            return;
        }

        String status = selectStatus();

        if (status == null) {
            System.out.println("Invalid status selection.");
            return;
        }

        try {

            boolean success = userDao.updateUser(userId, role, status);

            if (success) {

                System.out.println("\nUser updated successfully!");

                recordLog(
                        admin.id(),
                        "USER_UPDATED",
                        "Admin " + admin.id()
                                + " changed role of User " + userId
                                + " to " + role
                                + " and status to " + status
                );

                viewAllUsers();

            } else {

                System.out.println("User not found or update failed.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    // DELETE USER
    private void deleteUser(User admin) {

        System.out.println("\n========== DELETE USER ==========");

        viewAllUsers();

        int userId = readInt("\nEnter user ID to delete: ");

        if (userId <= 0) {
            System.out.println("Invalid user ID.");
            return;
        }

        if (userId == admin.id()) {
            System.out.println("You cannot delete your own admin account.");
            return;
        }

        System.out.print(
                "Are you sure you want to delete this user? (Y/N): "
        );

        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Delete cancelled.");
            return;
        }

        try {

            boolean success = userDao.deleteUser(userId);

            if (success) {

                System.out.println("\nUser deleted successfully!");

                recordLog(
                        admin.id(),
                        "USER_DELETED",
                        "Admin " + admin.id()
                                + " deleted User ID " + userId
                );

                viewAllUsers();

            } else {

                System.out.println("User not found or delete failed.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    // ================= AUDIT LOG MANAGEMENT =================

    // AUDIT LOG MENU
    private void auditLogMenu(User admin) {

        boolean running = true;

        while (running) {

            System.out.println("\n======================================");
            System.out.println("          SYSTEM AUDIT LOGS");
            System.out.println("======================================");

            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
            System.out.println("3. Clear Historical Logs");
            System.out.println("4. Back to Admin Dashboard");

            System.out.print("\nChoose an option (1-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> viewAllLogs();

                case "2" -> filterLogsByUserId();

                case "3" -> clearHistoricalLogs(admin);

                case "4" -> running = false;

                default -> System.out.println("Invalid option.");
            }
        }
    }

    // VIEW ALL LOGS
    private void viewAllLogs() {

        System.out.println("\n========== ALL SYSTEM AUDIT LOGS ==========");

        try {

            List<ActivityLog> logs = activityLogDao.getAllLogs();

            printLogs(logs);

        } catch (SQLException e) {
            System.out.println("Error retrieving logs: " + e.getMessage());
        }
    }

    // FILTER LOGS BY USER ID
    private void filterLogsByUserId() {

        System.out.println("\n========== FILTER LOGS BY USER ==========");

        int userId = readInt("Enter User ID: ");

        if (userId <= 0) {
            System.out.println("Invalid User ID.");
            return;
        }

        try {

            List<ActivityLog> logs =
                    activityLogDao.getLogsByUserId(userId);

            if (logs.isEmpty()) {
                System.out.println(
                        "No activity logs found for User ID: " + userId
                );
                return;
            }

            printLogs(logs);

        } catch (SQLException e) {
            System.out.println("Error filtering logs: " + e.getMessage());
        }
    }

    // DISPLAY LOGS IN TABLE FORMAT
    private void printLogs(List<ActivityLog> logs) {

        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
            return;
        }

        System.out.printf(
                "%-5s | %-8s | %-18s | %-35s | %-20s%n",
                "ID",
                "USER ID",
                "ACTION",
                "DESCRIPTION",
                "TIMESTAMP"
        );

        System.out.println(
                "---------------------------------------------------------------------------------------------------------------"
        );

        for (ActivityLog log : logs) {

            System.out.printf(
                    "%-5d | %-8d | %-18s | %-35.35s | %-20s%n",
                    log.id(),
                    log.userId(),
                    log.actionType(),
                    log.description(),
                    log.timestamp()
            );
        }

        System.out.println(
                "---------------------------------------------------------------------------------------------------------------"
        );

        System.out.println("Total Logs: " + logs.size());
    }

    // CLEAR HISTORICAL LOGS
    private void clearHistoricalLogs(User admin) {

        System.out.println("\n========== CLEAR HISTORICAL LOGS ==========");

        System.out.print(
                "Are you sure you want to delete ALL activity logs? (Y/N): "
        );

        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Clear operation cancelled.");
            return;
        }

        try {

            int deleted = activityLogDao.clearAllLogs();

            System.out.println(
                    deleted + " historical logs cleared successfully."
            );

            recordLog(
                    admin.id(),
                    "LOGS_CLEARED",
                    "Admin " + admin.id()
                            + " cleared " + deleted + " historical logs."
            );

        } catch (SQLException e) {
            System.out.println("Error clearing logs: " + e.getMessage());
        }
    }

    // ================= HELPER METHODS =================

    // SELECT ROLE
    private String selectRole() {

        System.out.println("\nSelect Role:");

        System.out.println("1. ADMIN");
        System.out.println("2. STAFF");
        System.out.println("3. STUDENT");
        System.out.println("4. TEACHER");

        System.out.print("Choose role (1-4): ");

        return switch (scanner.nextLine().trim()) {
            case "1" -> "ADMIN";
            case "2" -> "STAFF";
            case "3" -> "STUDENT";
            case "4" -> "TEACHER";
            default -> null;
        };
    }

    // SELECT STATUS
    private String selectStatus() {

        System.out.println("\nSelect Status:");

        System.out.println("1. PENDING");
        System.out.println("2. APPROVED");
        System.out.println("3. REJECTED");

        System.out.print("Choose status (1-3): ");

        return switch (scanner.nextLine().trim()) {
            case "1" -> "PENDING";
            case "2" -> "APPROVED";
            case "3" -> "REJECTED";
            default -> null;
        };
    }

    // READ INTEGER
    private int readInt(String prompt) {

        System.out.print(prompt);

        try {
            return Integer.parseInt(scanner.nextLine().trim());

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return -1;
        }
    }

    // PRINT USERS
    private void printUsers(List<User> users) {

        System.out.printf(
                "%-5s %-20s %-15s %-15s%n",
                "ID", "USERNAME", "ROLE", "STATUS"
        );

        System.out.println(
                "------------------------------------------------------"
        );

        for (User user : users) {

            System.out.printf(
                    "%-5d %-20s %-15s %-15s%n",
                    user.id(),
                    user.username(),
                    user.roles(),
                    user.status()
            );
        }

        System.out.println("-----------------------------------------------");
        System.out.println("Total Users: " + users.size());
    }

    // RECORD ACTIVITY
    private void recordLog(
            int userId,
            String actionType,
            String description
    ) {

        try {

            activityLogDao.logAction(
                    userId,
                    actionType,
                    description
            );

        } catch (SQLException e) {

            System.out.println(
                    "Activity logging error: " + e.getMessage()
            );
        }
    }
}