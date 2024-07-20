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
import static Utils.TableUtils.getPrimaryKeyColumnName;

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

            if (value != null) {
                value = value.replace("'", "");
            }

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

            // Get the primary key column name
            String primaryKeyColumn = getPrimaryKeyColumnName(tableName);
            if (primaryKeyColumn == null) {
                System.out.println(ANSI_RED + "Primary key column not found for table " + tableName + "." + ANSI_RESET);
                EventLog.logDatabaseChange("Primary key column not found for table " + tableName);
                return;
            }

            // Check if the specified column in the WHERE clause matches the primary key column
            if (!column.equalsIgnoreCase(primaryKeyColumn)) {
                System.out.println(ANSI_RED + "Only deletion by primary key (" + primaryKeyColumn + ") is supported." + ANSI_RESET);
                EventLog.logDatabaseChange("Delete operation failed. Only deletion by primary key (" + primaryKeyColumn + ") is supported for table " + tableName);
                return;
            }

            // Find the index of the primary key in the record lines
            String firstLine = fileLines.getFirst();
            String[] columns = firstLine.split("~~");
            int columnIndex = TableUtils.getColumnIndex(columns, column);
            if (columnIndex == -1) {
                System.out.println(ANSI_RED + "ColumnDetail " + column + " not found in table " + tableName + "." + ANSI_RESET);
                EventLog.logDatabaseChange("ColumnDetail " + column + " not found in table " + tableName);
                return;
            }

            // Remove the record if found based on primary key value
            boolean recordFound = TableUtils.removeRecord(fileLines, columnIndex, value, tableFile);
            if (recordFound) {
                System.out.println(ANSI_GREEN + "Record deleted successfully." + ANSI_RESET);
                EventLog.logDatabaseChange("Record with " + column + " = " + value + " deleted successfully from table " + tableName);
            } else {
                System.out.println(ANSI_RED + "The specified data was not found in the table." + ANSI_RESET);
                EventLog.logDatabaseChange("The specified data (" + column + " = " + value + ") was not found in table " + tableName);
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Failed to delete record from table " + tableName + "." + ANSI_RESET);
            EventLog.logCrashReport("Failed to delete record from table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}