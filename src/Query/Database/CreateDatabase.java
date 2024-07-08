package Query.Database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import Log.GeneralLog;
import Log.QueryLog;
import Log.EventLog;

import static Query.Database.DatabaseQueryValidator.validateCreateDatabaseQuery;
import static Utils.ColorConstraint.ANSI_GREEN;
import static Utils.ColorConstraint.ANSI_RED;
import static Utils.ColorConstraint.ANSI_RESET;

public class CreateDatabase {
    public static void create(String query) {
        String[] parts = query.split(" ");

        if (validateCreateDatabaseQuery(parts)) {
            String databaseName = parts[2];
            File databaseDirectory = new File("./databases/" + databaseName);

            if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {
                System.out.println(ANSI_RED + "Database " + databaseName + " already exists." + ANSI_RESET);
            } else {
                if (databaseDirectory.mkdirs()) {
                    createLogFiles(databaseName);
                    QueryLog.initialize(databaseName);
                    GeneralLog.initialize(databaseName);
                    EventLog.initialize(databaseName);

                    logCreateDatabaseQuery(query, databaseName);
                    EventLog.logDatabaseChange("Created database " + databaseName);

                    System.out.println(ANSI_GREEN + "Database " + databaseName + " created successfully." + ANSI_RESET);
                } else {
                    System.out.println(ANSI_RED + "Failed to create database " + databaseName + "." + ANSI_RESET);
                }
            }
        } else {
            System.out.println(ANSI_RED + "Invalid CREATE DATABASE query." + ANSI_RESET);
        }
    }

    private static void createLogFiles(String databaseName) {
        String[] logFileNames = {"general_log.json", "event_log.json", "query_log.json"};
        for (String logFileName : logFileNames) {
            File logFile = new File("./databases/" + databaseName + "/" + logFileName);
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
            }
        }
    }

    private static void logCreateDatabaseQuery(String query, String databaseName) {
        long startTime = System.currentTimeMillis();
        // Simulate the query execution if needed
        long endTime = System.currentTimeMillis();

        // Log the query
        QueryLog.logUserQuery("system", query, endTime);

        // Log the database state
        GeneralLog.logDatabaseState(1, Map.of(databaseName, 0)); // Assuming database starts with 0 tables
        GeneralLog.logQueryExecutionTime(query, endTime - startTime);
    }
}
