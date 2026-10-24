
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n========================================");
            System.out.println("     SCHOOL PROPERTY TRACKING SYSTEM");
            System.out.println("========================================");
            System.out.println("1. Login");
            System.out.println("2. Register Account");
            System.out.println("3. Exit");
            System.out.print("Choose an option (1-3): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> login();
                case "2" -> register();
                case "3" -> {
                    running = false;
                    System.out.println("Thank you for using the system!");
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void register() {
        System.out.println("\n========== USER REGISTRATION ==========");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (username.isBlank() || password.isBlank()) {
            System.out.println("Username and password are required.");
            return;
        }

        System.out.println("\nSelect Role:");
        System.out.println("1. ADMIN");
        System.out.println("2. STAFF");
        System.out.println("3. STUDENT");
        System.out.println("4. TEACHER");
        System.out.print("Choose role (1-4): ");

        String role = switch (scanner.nextLine().trim()) {
            case "1" -> "ADMIN";
            case "2" -> "STAFF";
            case "3" -> "STUDENT";
            case "4" -> "TEACHER";
            default -> null;
        };

        if (role == null) {
            System.out.println("Invalid role selection.");
            return;
        }

        try {
            if (userDao.usernameExists(username)) {
                System.out.println("Username already exists.");
                return;
            }

            boolean success = userDao.register(username, password, role);

            if (success) {
                System.out.println("\nRegistration successful!");
                System.out.println("Username: " + username);
                System.out.println("Role: " + role);
                System.out.println("Status: PENDING");
                System.out.println("Please wait for admin approval.");
            } else {
                System.out.println("Registration failed.");
            }

        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }

    private void login() {
        System.out.println("\n========== LOGIN ==========");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            User user = userDao.login(username, password);

            if (user == null) {
                System.out.println("Invalid username or password.");
                return;
            }

            String status = user.status().trim().toUpperCase();

            if (status.equals("PENDING")) {
                System.out.println("Your account is waiting for admin approval.");
                return;
            }

            if (status.equals("REJECTED")) {
                System.out.println("Your account has been rejected.");
                return;
            }

            if (!status.equals("APPROVED") && !status.equals("APPROVE")) {
                System.out.println("Your account is not approved for login.");
                return;
            }

            System.out.println("\nLogin successful!");
            System.out.println("Welcome, " + user.username());
            System.out.println("Role: " + user.roles());

            routeByRole(user);

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }

    private void routeByRole(User user) {
        String role = user.roles().trim().toUpperCase();

        switch (role) {
            case "ADMIN" -> {
                AdminMenu adminMenu = new AdminMenu(userDao, scanner);
                adminMenu.display(user);
            }
            case "STAFF" -> staffMenu(user);
            case "STUDENT" -> studentMenu(user);
            case "TEACHER" -> teacherMenu(user);
            default -> System.out.println("Unknown role. Contact the administrator.");
        }
    }

    private void staffMenu(User user) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n========== STAFF MENU ==========");
            System.out.println("Logged in as: " + user.username());
            System.out.println("1. View Profile");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> displayProfile(user);
                case "2" -> loggedIn = false;
                default -> System.out.println("Invalid option.");
            }
        }

        System.out.println("Staff logged out successfully.");
    }

    private void studentMenu(User user) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n========== STUDENT MENU ==========");
            System.out.println("Logged in as: " + user.username());
            System.out.println("1. View Profile");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> displayProfile(user);
                case "2" -> loggedIn = false;
                default -> System.out.println("Invalid option.");
            }
        }

        System.out.println("Student logged out successfully.");
    }

    private void teacherMenu(User user) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n========== TEACHER MENU ==========");
            System.out.println("Logged in as: " + user.username());
            System.out.println("1. View Profile");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> displayProfile(user);
                case "2" -> loggedIn = false;
                default -> System.out.println("Invalid option.");
            }
        }

        System.out.println("Teacher logged out successfully.");
    }

    private void displayProfile(User user) {
        System.out.println("\n========== USER PROFILE ==========");
        System.out.println("ID: " + user.id());
        System.out.println("Username: " + user.username());
        System.out.println("Role: " + user.roles());
        System.out.println("Status: " + user.status());
    }
}
