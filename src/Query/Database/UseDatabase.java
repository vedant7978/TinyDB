package Query.Database;

import java.io.File;

import static Query.Database.DatabaseQueryValidator.validateUseDatabaseQuery;
import static Utills.ColorConstraint.*;

public class UseDatabase {
    private static String currentDatabase = null;

    public static void use(String query) {
        String[] parts = query.split(" ");

        if (validateUseDatabaseQuery(parts)) {
            String databaseName = parts[1];
            File databaseDirectory = new File("./databases/" + databaseName);

            if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {
                currentDatabase = databaseName;
                System.out.println(ANSI_GREEN + "Using database " + databaseName + "." + ANSI_RESET);
            } else {
                System.out.println(ANSI_RED + "Database " + databaseName + " does not exist." + ANSI_RESET);
            }
        } else {
            System.out.println(ANSI_GREEN + "Invalid USE DATABASE query." + ANSI_RESET);
        }
    }

    public static String getCurrentDatabase() {
        return currentDatabase;
    }

    public static boolean isDatabaseSelected() {
        return currentDatabase != null;
    }
}
