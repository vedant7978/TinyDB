package Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static Security.login.UserLoginImpl.currentUserID;

public class EventLog {
    private static String logFilePath;

    public static void initialize(String databaseName) {
        logFilePath = "./databases/" + databaseName + "/event_log.json";
    }

    public static void logDatabaseChange(String changeDescription) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("details", Map.of(
                    "UserID", currentUserID,
                "changeDescription", changeDescription
        ));
        logEntry.put("type", "databaseChange");

        writeLogEntry(logEntry);
    }

    public static void logTransactionEvent(String transactionEvent) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("details", Map.of(
                    "UserID", currentUserID,
                    "transactionEvent", transactionEvent
        ));
        logEntry.put("type", "transactionEvent");

        writeLogEntry(logEntry);
    }

    public static void logCrashReport(String crashDescription) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("details", Map.of(
                "UserID", currentUserID,
                "crashDescription", crashDescription
        ));
        logEntry.put("type", "crashReport");

        writeLogEntry(logEntry);
    }

    private static void writeLogEntry(Map<String, Object> logEntry) {
        try (FileWriter file = new FileWriter(logFilePath, true)) {
            file.write(mapToJson(logEntry) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\": ");

            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else if (entry.getValue() instanceof Map) {
                json.append(mapToJson((Map<String, Object>) entry.getValue()));
            } else {
                json.append(entry.getValue());
            }

            json.append(", ");
        }

        if (json.length() > 1) {
            json.setLength(json.length() - 2); // Remove trailing comma and space
        }

        json.append("}");
        return json.toString();
    }

}