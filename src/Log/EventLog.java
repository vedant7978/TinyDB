package Log;

import Utils.JSONUtility;

import java.util.HashMap;
import java.util.Map;

import static Security.login.UserLoginImpl.currentUserID;

public class EventLog {
    private static String logFilePath;

    /**
     * Initializes the log file path for the specified database.
     *
     * @param databaseName the name of the database to initialize the log for
     */
    public static void initialize(String databaseName) {
        logFilePath = "./databases/" + databaseName + "/logs/event_log.json";
    }

    /**
     * Logs a database change event.
     *
     * @param changeDescription a description of the database change
     */
    public static void logDatabaseChange(String changeDescription) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("UserID", currentUserID);
        logEntry.put("changeDescription", changeDescription);
        logEntry.put("type", "databaseChange");

        JSONUtility.writeLogEntry(logFilePath, logEntry);
    }

    /**
     * Logs a transaction event.
     *
     * @param transactionEvent a description of the transaction event
     */
    public static void logTransactionEvent(String transactionEvent) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("UserID", currentUserID);
        logEntry.put("transactionEvent", transactionEvent);
        logEntry.put("type", "transactionEvent");

        JSONUtility.writeLogEntry(logFilePath, logEntry);
    }

    /**
     * Logs a crash report.
     *
     * @param crashDescription a description of the crash
     */
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
