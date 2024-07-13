package Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static Log.EventLog.mapToJson;

public class GeneralLog {
    private static String logFilePath;

    public static void initialize(String databaseName) {
        logFilePath = "./databases/" + databaseName + "/general_log.json";
    }

    public static void log(String query, long executionTime, int numberOfTables, int totalRecords) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("details", Map.of(
                "queryExecutionTime", executionTime,
                "query", query,
                "numberOfTables", numberOfTables,
                "totalRecords", totalRecords
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
