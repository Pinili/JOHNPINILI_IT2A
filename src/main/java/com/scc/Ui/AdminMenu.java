
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void display(User admin) {
        if (!"ADMIN".equalsIgnoreCase(admin.roles())) {
            System.out.println("Access denied. Admin only.");
            return;
        }

        boolean running = true;

        while (running) {
            System.out.println("\n========================================");
            System.out.println("       ADMIN DASHBOARD");
            System.out.println("========================================");
            System.out.println("Logged in as: " + admin.username());
            System.out.println("1. View All Users");
            System.out.println("2. View Pending Users");
            System.out.println("3. Approve User Registration");
            System.out.println("4. Add New User");
            System.out.println("5. Update User Role / Status");
            System.out.println("6. Delete User Account");
            System.out.println("7. Logout");
            System.out.print("Choose an option (1-7): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingUsers();
                case "3" -> approveUser();
                case "4" -> addUser();
                case "5" -> updateUser();
                case "6" -> deleteUser(admin);
                case "7" -> {
                    running = false;
                    System.out.println("Admin logged out successfully.");
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

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

    private void approveUser() {
        System.out.println("\n========== APPROVE USER ==========");
        int userId = readInt("Enter user ID to approve: ");

        if (userId <= 0) {
            System.out.println("Invalid user ID.");
            return;
        }

        try {
            boolean success = userDao.approveUser(userId);

            if (success) {
                System.out.println("User approved successfully.");
            } else {
                System.out.println("User not found or could not be approved.");
            }

        } catch (SQLException e) {
            System.out.println("Error approving user: " + e.getMessage());
        }
    }

    private void addUser() {
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
                    username, password, role, status
            );

            if (success) {
                System.out.println("User added successfully.");
            } else {
                System.out.println("Failed to add user.");
            }

        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    private void updateUser() {
        System.out.println("\n========== UPDATE USER ==========");

        int userId = readInt("Enter user ID to update: ");

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
                System.out.println("User updated successfully.");
            } else {
                System.out.println("User not found or update failed.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    private void deleteUser(User admin) {
        System.out.println("\n========== DELETE USER ==========");

        int userId = readInt("Enter user ID to delete: ");

        if (userId <= 0) {
            System.out.println("Invalid user ID.");
            return;
        }

        if (userId == admin.id()) {
            System.out.println("You cannot delete your own admin account.");
            return;
        }

        System.out.print("Are you sure you want to delete this user? (Y/N): ");
        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Delete cancelled.");
            return;
        }

        try {
            boolean success = userDao.deleteUser(userId);

            if (success) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("User not found or delete failed.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

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

    private int readInt(String prompt) {
        System.out.print(prompt);

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return -1;
        }
    }

    private void printUsers(List<User> users) {
        System.out.printf(
                "%-5s %-20s %-15s %-15s%n",
                "ID", "USERNAME", "ROLE", "STATUS"
        );

        System.out.println("------------------------------------------------------");

        for (User user : users) {
            System.out.printf(
                    "%-5d %-20s %-15s %-15s%n",
                    user.id(),
                    user.username(),
                    user.roles(),
                    user.status()
            );
        }
    }
}
