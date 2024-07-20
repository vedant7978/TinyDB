package Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JSONUtility {

    public static void writeLogEntry(String logFilePath, Map<String, Object> logEntry) {
        if (logFilePath == null || logFilePath.isEmpty()) {
            System.err.println("Log file path is null or empty.");
            return;
        }

        File logFile = new File(logFilePath);
        String newLogEntry = mapToJson(logEntry);

        try {
            // Ensure the parent directories exist
            File parentDir = logFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    System.err.println("Error creating directories: " + parentDir.getPath());
                    return;
                }
            }

            // If the log file does not exist, create it and add the opening bracket
            if (!logFile.exists()) {
                if (!logFile.createNewFile()) {
                    System.err.println("Error creating log file: " + logFilePath);
                    return;
                }
                try (FileWriter file = new FileWriter(logFilePath, true)) {
                    file.write("[\n");
                }
            }

            // Read the current content of the file
            String content = new String(Files.readAllBytes(Paths.get(logFilePath))).trim();

            // If the file contains more than just the opening bracket
            if (content.length() > 1) {
                // Remove the closing bracket
                content = content.substring(0, content.length() - 1).trim();

                // If there is more than just the opening bracket, add a comma to separate entries
                if (content.length() > 1) {
                    content += ",\n";
                }
            }

            // Write the new log entry and add the closing bracket
            try (FileWriter file = new FileWriter(logFilePath, false)) {
                file.write(content + newLogEntry + "\n]");
            }

        } catch (IOException e) {
            System.err.println("Error writing log entry: " + e.getMessage());
        }
    }


    private static Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = new HashMap<>();
        json = json.substring(1, json.length() - 1).trim(); // Remove outer braces
        String[] entries = json.split(",(?=\")");

        for (String entry : entries) {
            String[] keyValue = entry.split(":", 2);
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim();
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            map.put(key, parseValue(value));
        }
        return map;
    }

    private static Object parseValue(String value) {
        if (value.equals("null")) {
            return null;
        } else if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value);
        } else {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException e2) {
                    if (value.startsWith("{") && value.endsWith("}")) {
                        return jsonToMap(value);
                    } else {
                        return value;
                    }
                }
            }
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
