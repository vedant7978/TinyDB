package Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static Log.EventLog.mapToJson;

public class QueryLog {
    private static String logFilePath;

    public static void initialize(String databaseName) {
        logFilePath = "./databases/" + databaseName + "/query_log.json";
    }

    public static void logUserQuery(String userID, String query, long timestamp) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("type", "query");
        logEntry.put("timestamp", timestamp);
        logEntry.put("details", Map.of(
                "userID", userID,
                "query", query
        ));

        writeLogEntry(logEntry);
    }

    public static void logDataChange(String changeDescription) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("type", "dataChange");
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("details", Map.of(
                "changeDescription", changeDescription
        ));

        writeLogEntry(logEntry);
    }

    private static void writeLogEntry(Map<String, Object> logEntry) {
        try (FileWriter file = new FileWriter(logFilePath, true)) {
            file.write(mapToJson(logEntry) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
