
import dao.DatabaseConnection;
import ui.Menu;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeDatabase();

            System.out.println(
                    "SQLite database connected successfully!"
            );

            Menu menu = new Menu();
            menu.start();

        } catch (Exception e) {
            System.out.println(
                    "DATABASE / APPLICATION ERROR:"
            );
            e.printStackTrace();
        }
    }
}
