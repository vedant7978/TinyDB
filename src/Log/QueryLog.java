package Log;

import Utils.JSONUtility;

import java.util.HashMap;
import java.util.Map;

import static Security.login.UserLoginImpl.currentUserID;

public class QueryLog {
    private static String logFilePath;


    /**
     * Initializes the log file path for the specified database.
     *
     * @param databaseName the name of the database to initialize the log for
     */
    public static void initialize(String databaseName) {
        logFilePath = "./databases/" + databaseName + "/logs/query_log.json";
    }

    /**
     * Logs user queries.
     *
     * @param query     the query executed by the user
     * @param timestamp the time when the query was executed
     */
    public static void logUserQuery(String query, long timestamp) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", timestamp);
        logEntry.put("userID", currentUserID);
        logEntry.put("query", query);

        JSONUtility.writeLogEntry(logFilePath, logEntry);
    }
}
