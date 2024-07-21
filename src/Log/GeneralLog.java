package Log;

import Utils.JSONUtility;

import java.util.HashMap;
import java.util.Map;

public class GeneralLog {
    private static String logFilePath;

    /**
     * Initializes the log file path for the specified database.
     *
     * @param databaseName the name of the database to initialize the log for
     */
    public static void initialize(String databaseName) {
        logFilePath = "./databases/" + databaseName + "/logs/general_log.json";
    }

    /**
     * Logs general query information.
     *
     * @param query          the query executed
     * @param executionTime  the time taken to execute the query
     * @param numberOfTables the number of tables involved in the query
     * @param totalRecords   the total number of records involved in the query
     */
    public static void log(String query, long executionTime, int numberOfTables, int totalRecords) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("query", query);
        logEntry.put("executionTime", executionTime);
        logEntry.put("numberOfTables", numberOfTables);
        logEntry.put("totalRecords", totalRecords);

        JSONUtility.writeLogEntry(logFilePath, logEntry);
    }
}
