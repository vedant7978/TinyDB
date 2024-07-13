package Query.DataOperations;

import Query.TransactionManagement.TransactionManager;
import Query.TransactionManagement.TransactionManagerImpl;
import Utils.RegexPatterns;
import Utils.TableUtils;
import Log.EventLog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import static Utils.ColorConstraint.*;

public class DeleteFromTable {

    private static final TransactionManager transactionManager = new TransactionManagerImpl();

    public static void delete(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            System.out.println(ANSI_RED + "No database selected." + ANSI_RESET);
            EventLog.logDatabaseChange("No database selected for DELETE query: " + query);
            return;
        }

        Matcher matcher = RegexPatterns.DELETE_FROM_PATTERN.matcher(query);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String column = matcher.group(2);
            String value = matcher.group(3);

            if (transactionManager.isTransactionActive()) {
                // If a transaction is active, store the delete operation in the buffer
                transactionManager.addDeleteToTransaction(tableName, column, value);
                System.out.println(ANSI_GREEN + "Delete operation buffered for table " + tableName + "." + ANSI_RESET);
                EventLog.logDatabaseChange("Delete operation buffered for table " + tableName + " with column: " + column + " and value: " + value);
            } else {
                // If no transaction is active, execute the delete operation directly
                executeDelete(tableName, column, value);
            }
        } else {
            System.out.println(ANSI_RED + "Invalid DELETE query format." + ANSI_RESET);
            EventLog.logDatabaseChange("Invalid DELETE query format: " + query);
        }
    }

    public static void executeDelete(String tableName, String column, String value) {
        File tableFile = TableUtils.getTableFile(tableName);
        if (tableFile == null) {
            System.out.println(ANSI_RED + "Table " + tableName + " does not exist." + ANSI_RESET);
            EventLog.logDatabaseChange("Table " + tableName + " does not exist for DELETE operation.");
            return;
        }

        try {
            List<String> fileLines = TableUtils.readTableFile(tableFile);
            if (fileLines == null || fileLines.isEmpty()) {
                System.out.println(ANSI_RED + "Table " + tableName + " is empty or could not be read." + ANSI_RESET);
                EventLog.logDatabaseChange("Table " + tableName + " is empty or could not be read for DELETE operation.");
                return;
            }

            if (column == null && value == null) {
                // No WHERE clause, delete all records
                fileLines.subList(1, fileLines.size()).clear(); // Keep the header, remove all other lines
                TableUtils.writeTableFile(tableFile, fileLines);
                System.out.println(ANSI_GREEN + "All records deleted successfully from table " + tableName + "." + ANSI_RESET);
                EventLog.logDatabaseChange("All records deleted from table " + tableName);
            } else {
                String firstLine = fileLines.get(0);
                String[] columns = firstLine.split("~~");
                int columnIndex = TableUtils.getColumnIndex(columns, column);
                if (columnIndex == -1) {
                    System.out.println(ANSI_RED + "Column " + column + " not found in table " + tableName + "." + ANSI_RESET);
                    EventLog.logDatabaseChange("Column " + column + " not found in table " + tableName);
                    return;
                }

                boolean recordFound = TableUtils.removeRecord(fileLines, columnIndex, value, tableFile);
                if (recordFound) {
                    System.out.println(ANSI_GREEN + "Record deleted successfully." + ANSI_RESET);
                    EventLog.logDatabaseChange("Record with " + column + " = " + value + " deleted successfully from table " + tableName);
                } else {
                    System.out.println(ANSI_RED + "The specified data was not found in the table." + ANSI_RESET);
                    EventLog.logDatabaseChange("The specified data (" + column + " = " + value + ") was not found in table " + tableName);
                }
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Failed to delete record from table " + tableName + "." + ANSI_RESET);
            EventLog.logCrashReport("Failed to delete record from table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
