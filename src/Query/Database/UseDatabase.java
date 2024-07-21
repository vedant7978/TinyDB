package Query.Database;

import java.io.File;
import static Query.Database.DatabaseQueryValidator.validateUseDatabaseQuery;
import static Utils.ColorConstraint.*;
import static Utils.DatabaseUtils.getTotalRecords;

import Log.EventLog;
import Log.GeneralLog;
import Log.QueryLog;
import Utils.TableUtils;

public class UseDatabase {
    private static String currentDatabase = null;


    /**
     * Switches to the specified database based on the provided query.
     *
     * @param query the USE DATABASE query
     */
    public static void use(String query) {
        String[] parts = query.split(" ");
        long startTime = System.currentTimeMillis();

        if (validateUseDatabaseQuery(parts)) {
            String databaseName = parts[1];
            File databaseDirectory = new File("./databases/" + databaseName);

            if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {
                currentDatabase = databaseName;
                System.out.println(ANSI_GREEN + "Using database " + databaseName + "." + ANSI_RESET);
                QueryLog.initialize(databaseName);
                EventLog.initialize(databaseName);
                GeneralLog.initialize(databaseName);

                // Log the successful use of the database
                EventLog.logDatabaseChange("Switched to database " + databaseName);
                QueryLog.logUserQuery(query, startTime);

                long executionTime = System.currentTimeMillis() - startTime;
                int numberOfTables = databaseDirectory.list().length; // Assuming each table is a file in the directory
                int totalRecords = getTotalRecords(databaseDirectory);

                GeneralLog.log(query, executionTime, numberOfTables, totalRecords);
            } else {
                System.out.println(ANSI_RED + "Database " + databaseName + " does not exist." + ANSI_RESET);
            }
        } else {
            System.out.println(ANSI_RED + "Invalid USE DATABASE query." + ANSI_RESET);
            // Log the invalid query
            EventLog.logDatabaseChange("Invalid USE DATABASE query: " + query);
            QueryLog.logUserQuery(query, startTime);
        }
    }

    /**
     * Gets the current database in use.
     *
     * @return the name of the current database
     */
    public static String getCurrentDatabase() {
        return currentDatabase;
    }
    /**
     * Checks if a database is currently selected.
     *
     * @return true if a database is selected, false otherwise
     */
    public static boolean isDatabaseSelected() {
        return currentDatabase != null;
    }
}
