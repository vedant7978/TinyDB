package Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static Log.EventLog.mapToJson;
import static Security.login.UserLoginImpl.currentUserID;

public class QueryLog {
    private static String logFilePath;

    public static void initialize(String databaseName) {
        logFilePath = "./databases/" + databaseName + "/query_log.json";
    }

    public static void logUserQuery(String query, long timestamp) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", timestamp);
        logEntry.put("details", Map.of(
                "userID", currentUserID,
                "query", query
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
