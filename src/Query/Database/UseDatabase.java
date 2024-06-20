package Query.Database;

import java.io.File;

import static Query.Database.DatabaseQueryValidator.validateUseDatabaseQuery;

public class UseDatabase {
    private static String currentDatabase = null;

    public static void use(String query) {
        String[] parts = query.split(" ");

        if (validateUseDatabaseQuery(parts)) {
            String databaseName = parts[1];
            File databaseDirectory = new File("./databases/" + databaseName);

            if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {
                currentDatabase = databaseName;
                System.out.println("Using database " + databaseName + ".");
            } else {
                System.out.println("Database " + databaseName + " does not exist.");
            }
        } else {
            System.out.println("Invalid USE DATABASE query.");
        }
    }

    public static String getCurrentDatabase() {
        return currentDatabase;
    }
    public static boolean isDatabaseSelected() {
        return currentDatabase != null;
    }
}
