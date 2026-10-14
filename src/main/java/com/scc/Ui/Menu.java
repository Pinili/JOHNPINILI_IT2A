
package ui;

import dao.UserDao;
import model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class Menu {

    private final Scanner sc = new Scanner(System.in);
    private final UserDao userDao = new UserDao();

    public void start() throws SQLException {
        boolean running = true;

        while (running) {
            System.out.println("\n========================================");
            System.out.println("     SCHOOL PROPERTY TRACKING SYSTEM");
            System.out.println("========================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    login();
                    break;

                case "2":
                    register();
                    break;

                case "3":
                    System.out.println("Thank you for using the system!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void register() throws SQLException {
        System.out.println("\n========== USER REGISTRATION ==========");

        System.out.print("Enter new username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        System.out.println("Select Role:");
        System.out.println("1. TEACHER");
        System.out.println("2. STUDENT");
        System.out.print("Choose a role: ");

        String roleChoice = sc.nextLine().trim();
        String role;

        switch (roleChoice) {
            case "1":
                role = "TEACHER";
                break;

            case "2":
                role = "STUDENT";
                break;

            default:
                System.out.println("Invalid role selection.");
                return;
        }

        boolean success = userDao.register(username, password, role);

        if (success) {
            System.out.println("\nRegistration successful!");
            System.out.println("Username: " + username);
            System.out.println("Role: " + role);
            System.out.println("Account Status: PENDING");
        } else {
            System.out.println(
                    "Registration failed. Username may already exist, "
                            + "or your input is invalid."
            );
        }
    }

    private void login() throws SQLException {
        System.out.println("\n=============== LOGIN ===============");

        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.username() + "!");
        System.out.println("Roles: " + user.roles());
        System.out.println("Account Status: " + user.status());

        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n========== USER MENU ==========");
            System.out.println("1. View Account");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n========== ACCOUNT DETAILS ==========");
                    System.out.println("User ID: " + user.id());
                    System.out.println("Username: " + user.username());
                    System.out.println("Roles: " + user.roles());
                    System.out.println("Status: " + user.status());
                    break;

                case "2":
                    System.out.println("You have logged out.");
                    loggedIn = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
