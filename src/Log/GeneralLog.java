package Log;

import Utils.JSONUtility;

import java.util.HashMap;
import java.util.Map;

public class GeneralLog {
    private static String logFilePath;

    public static void initialize(String databaseName) {
        logFilePath = "./databases/" + databaseName + "/logs/general_log.json";
    }

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
