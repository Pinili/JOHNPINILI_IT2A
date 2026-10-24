
package com.scc;

import com.scc.dao.DatabaseConnection;
import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.ui.Authentication;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            DatabaseInitializer initializer = new DatabaseInitializer();
            initializer.initialize();

            DatabaseConnection.testConnection();

            UserDao userDao = new UserDao();

            try (Scanner scanner = new Scanner(System.in)) {
                Authentication authentication =
                        new Authentication(userDao, scanner);

                authentication.start();
            }

        } catch (SQLException e) {
            System.err.println(
                    "Failed to start application: " + e.getMessage()
            );
        }
    }
}
