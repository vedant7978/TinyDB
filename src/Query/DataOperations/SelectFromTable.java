package Query.DataOperations;

import Query.TransactionManagement.TransactionManager;
import Query.TransactionManagement.TransactionManagerImpl;
import Utils.RegexPatterns;
import Utils.TableUtils;
import Log.EventLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static Utils.ColorConstraint.ANSI_RED;
import static Utils.ColorConstraint.ANSI_RESET;

public class SelectFromTable {

    private static final TransactionManager transactionManager = new TransactionManagerImpl();

    /**
     * Selects records from the table based on the query.
     *
     * @param query the SELECT query string
     */
    public static void select(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            System.out.println(ANSI_RED + "No database selected." + ANSI_RESET);
            EventLog.logDatabaseChange("No database selected for SELECT query: " + query);
            return;
        }

        Matcher matcher = RegexPatterns.SELECT_FROM_PATTERN.matcher(query);

        if (matcher.matches()) {
            String columnsPart = matcher.group(1);
            String tableName = matcher.group(2);
            String conditionColumn = matcher.group(3);
            String conditionValue = matcher.group(5);

            List<String> fileLines = new ArrayList<>();
            try {
                // Read committed data from table file
                File tableFile = TableUtils.getTableFile(tableName);
                if (tableFile == null) {
                    System.out.println(ANSI_RED + "Table " + tableName + " does not exist." + ANSI_RESET);
                    EventLog.logDatabaseChange("Table " + tableName + " does not exist for SELECT operation.");
                    return;
                }
                fileLines = TableUtils.readTableFile(tableFile);
                if (fileLines == null || fileLines.isEmpty()) {
                    System.out.println(ANSI_RED + "Table " + tableName + " is empty or could not be read." + ANSI_RESET);
                    EventLog.logDatabaseChange("Table " + tableName + " is empty or could not be read for SELECT operation.");
                    return;
                }
            } catch (IOException e) {
                System.out.println(ANSI_RED + "An error occurred while reading the table." + ANSI_RESET);
                EventLog.logCrashReport("An error occurred while reading the table " + tableName + ": " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // Read data from transaction buffer
            List<String> bufferLines = new ArrayList<>();
            if (transactionManager.isTransactionActive()) {
                for (ArrayList<String> rowValues : TransactionManagerImpl.buffer.getOrDefault(tableName, new ArrayList<>())) {
                    String operation = rowValues.get(0);
                    if ("INSERT".equalsIgnoreCase(operation)) {
                        StringBuilder insertValues = new StringBuilder();
                        for (int i = 1; i < rowValues.size(); i++) {
                            if (!insertValues.isEmpty()) {
                                insertValues.append(",");
                            }
                            insertValues.append(rowValues.get(i));
                        }
                        bufferLines.add(insertValues.toString());
                    } else if ("UPDATE".equalsIgnoreCase(operation)) {
                        bufferLines.add("UPDATE," + rowValues.get(1) + "," + rowValues.get(2) + "," + rowValues.get(3) + "," + rowValues.get(4));
                    } else if ("DELETE".equalsIgnoreCase(operation)) {
                        bufferLines.add("DELETE," + rowValues.get(1) + "," + rowValues.get(2));
                    }
                }
                EventLog.logTransactionEvent("Buffered data read from transaction for table " + tableName);
            }

            String[] headers = fileLines.getFirst().split("~~");

            int[] columnIndices = getColumnIndices(columnsPart, headers, tableName);
            if (columnIndices == null) {
                return;
            }

            int conditionColumnIndex = -1;
            if (conditionColumn != null) {
                conditionColumnIndex = TableUtils.getColumnIndex(headers, conditionColumn);
                if (conditionColumnIndex == -1) {
                    System.out.println(ANSI_RED + "ColumnDetail " + conditionColumn + " not found in table " + tableName + "." + ANSI_RESET);
                    EventLog.logDatabaseChange("ColumnDetail " + conditionColumn + " not found in table " + tableName + " for SELECT operation.");
                    return;
                }
            }

            boolean recordFound = false;

            // Print headers for committed data
            for (int index : columnIndices) {
                System.out.print(headers[index].trim().split(" ")[0] + "\t");
            }
            System.out.println();

            // Print committed data
            for (String line : fileLines.subList(1, fileLines.size())) {
                String[] rowValues = line.split("~~", -1);
                if (conditionColumnIndex == -1 || rowValues[conditionColumnIndex].trim().equalsIgnoreCase(conditionValue)) {
                    recordFound = true;
                    for (int index : columnIndices) {
                        System.out.print(rowValues[index].trim().replace("'", "") + "\t");
                    }
                    System.out.println();
                }
            }

            // Print headers for buffer data only if a transaction is active
            if (transactionManager.isTransactionActive() && !query.toLowerCase().contains("where")) {
                for (int index : columnIndices) {
                    System.out.print(headers[index].trim().split(" ")[0] + " (Buffer)\t");
                }
                System.out.println();

                // Print buffer data
                for (String line : bufferLines) {
                    String[] rowValues = line.split(",", -1);
                    recordFound = true;
                    for (int index : columnIndices) {
                        System.out.print(rowValues[index].trim().replace("'", "") + "\t");
                    }
                    System.out.println();
                }
            }

            if (!recordFound) {
                System.out.println(ANSI_RED + "No matching records found for the condition " + conditionColumn + "='" + conditionValue + "'." + ANSI_RESET);
                EventLog.logDatabaseChange("No matching records found for condition " + conditionColumn + "='" + conditionValue + "' in table " + tableName);
            }
        } else {
            System.out.println(ANSI_RED + "Invalid SELECT query format." + ANSI_RESET);
            EventLog.logDatabaseChange("Invalid SELECT query format: " + query);
        }
    }

    /**
     * Determines the indices of the requested columns in the table.
     *
     * @param columnsPart the columns part of the SELECT query
     * @param headers the headers of the table
     * @param tableName the name of the table
     * @return an array of indices of the requested columns
     */
    private static int[] getColumnIndices(String columnsPart, String[] headers, String tableName) {
        int[] columnIndices;
        if (columnsPart.equals("*")) {
            columnIndices = new int[headers.length];
            for (int i = 0; i < headers.length; i++) {
                columnIndices[i] = i;
            }
        } else {
            String[] requestedColumns = columnsPart.split(",");
            columnIndices = new int[requestedColumns.length];
            for (int i = 0; i < requestedColumns.length; i++) {
                columnIndices[i] = TableUtils.getColumnIndex(headers, requestedColumns[i].trim());
                if (columnIndices[i] == -1) {
                    System.out.println(ANSI_RED + "ColumnDetail " + requestedColumns[i].trim() + " not found in table " + tableName + "." + ANSI_RESET);
                    EventLog.logDatabaseChange("ColumnDetail " + requestedColumns[i].trim() + " not found in table " + tableName + " for SELECT operation.");
                    return null;
                }
            }
        }
        return columnIndices;
    }
}
