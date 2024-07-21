package Query.Database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Log.GeneralLog;
import Log.QueryLog;
import Log.EventLog;

import static Query.Database.DatabaseQueryValidator.validateCreateDatabaseQuery;
import static Utils.DatabaseUtils.getTotalRecords;
import static Utils.ColorConstraint.ANSI_GREEN;
import static Utils.ColorConstraint.ANSI_RED;
import static Utils.ColorConstraint.ANSI_RESET;

public class CreateDatabase {

    /**
     * Creates a new database based on the provided query.
     *
     * @param query the CREATE DATABASE query
     */
    public static void create(String query) {
        String[] parts = query.split(" ");
        long startTime = System.currentTimeMillis();

        if (validateCreateDatabaseQuery(parts)) {
            String databaseName = parts[2];
            File databaseDirectory = new File("./databases/" + databaseName);

            if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {
                System.out.println(ANSI_RED + "Database " + databaseName + " already exists." + ANSI_RESET);
                logQueryAndDatabaseState(query, startTime, databaseName, false, "Database already exists");
            } else {
                if (databaseDirectory.mkdirs()) {
                    createLogFiles(databaseName);
                    QueryLog.initialize(databaseName);
                    GeneralLog.initialize(databaseName);
                    EventLog.initialize(databaseName);

                    EventLog.logDatabaseChange("Created database " + databaseName);

                    System.out.println(ANSI_GREEN + "Database " + databaseName + " created successfully." + ANSI_RESET);
                    logQueryAndDatabaseState(query, startTime, databaseName, true, "Database created successfully");
                } else {
                    System.out.println(ANSI_RED + "Failed to create database " + databaseName + "." + ANSI_RESET);
                    logQueryAndDatabaseState(query, startTime, databaseName, false, "Failed to create database");
                }
            }
        } else {
            System.out.println(ANSI_RED + "Invalid CREATE DATABASE query." + ANSI_RESET);
            logQueryAndDatabaseState(query, startTime, null, false, "Invalid CREATE DATABASE query");
        }
    }
    /**
     * Creates log files for the new database.
     *
     * @param databaseName the name of the database for which log files are created
     */
    private static void createLogFiles(String databaseName) {
        String logsDirectoryPath = "./databases/" + databaseName + "/logs";
        File logsDirectory = new File(logsDirectoryPath);

        if (logsDirectory.mkdirs()) {
            String[] logFileNames = {"general_log.json", "event_log.json", "query_log.json"};
            for (String logFileName : logFileNames) {
                File logFile = new File(logsDirectory, logFileName);
                try {
                    if (logFile.createNewFile()) {
                        try (FileWriter writer = new FileWriter(logFile)) {
                            writer.write("[]"); // Initialize file with empty JSON array
                        }
                    } else {
                        System.out.println(ANSI_RED + "Failed to create log file: " + logFileName + "." + ANSI_RESET);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(ANSI_RED + "Failed to create log file: " + logFileName + "." + ANSI_RESET);
                    EventLog.logCrashReport("Failed to create log file: " + logFileName + ": " + e.getMessage());
                }
            }
        } else {
            System.out.println(ANSI_RED + "Failed to create logs directory." + ANSI_RESET);
            EventLog.logCrashReport("Failed to create logs directory for database: " + databaseName);
        }
    }

    /**
     * Logs query and database state information.
     *
     * @param query          the query executed
     * @param startTime      the start time of the query execution
     * @param databaseName   the name of the database
     * @param success        the success status of the query
     * @param statusMessage  the status message to log
     */
    private static void logQueryAndDatabaseState(String query, long startTime, String databaseName, boolean success, String statusMessage) {
        long executionTime = System.currentTimeMillis() - startTime;

        int numberOfTables = 0;
        int totalRecords = 0;
        if (databaseName != null && success) {
            File databaseDirectory = new File("./databases/" + databaseName);
            numberOfTables = Utils.TableUtils.getNumberOfTables(databaseName);
            totalRecords = getTotalRecords(databaseDirectory);
        }

        GeneralLog.log(query, executionTime, numberOfTables, totalRecords);
        QueryLog.logUserQuery(query, startTime);
        EventLog.logDatabaseChange(statusMessage);
    }
}
