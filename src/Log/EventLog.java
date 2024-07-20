package Log;

import Utils.JSONUtility;

import java.util.HashMap;
import java.util.Map;

import static Security.login.UserLoginImpl.currentUserID;

public class EventLog {
    private static String logFilePath;

    public static void initialize(String databaseName) {
        logFilePath = "./databases/" + databaseName + "/logs/event_log.json";
    }

    public static void logDatabaseChange(String changeDescription) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("UserID", currentUserID);
        logEntry.put("changeDescription", changeDescription);
        logEntry.put("type", "databaseChange");

        JSONUtility.writeLogEntry(logFilePath, logEntry);
    }

    public static void logTransactionEvent(String transactionEvent) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("UserID", currentUserID);
        logEntry.put("transactionEvent", transactionEvent);
        logEntry.put("type", "transactionEvent");

        JSONUtility.writeLogEntry(logFilePath, logEntry);
    }

    public static void logCrashReport(String crashDescription) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("UserID", currentUserID);
        logEntry.put("crashDescription", crashDescription);
/*        logEntry.put("details", Map.of(
                "UserID", currentUserID,
                "crashDescription", crashDescription
        ));*/
        logEntry.put("type", "crashReport");

        JSONUtility.writeLogEntry(logFilePath, logEntry);
    }
}
