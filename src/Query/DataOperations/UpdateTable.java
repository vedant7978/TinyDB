package Query.DataOperations;

import Query.TransactionManagement.TransactionManagerImpl;
import Utils.RegexPatterns;
import Utils.TableUtils;
import Log.EventLog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import static Utils.ColorConstraint.ANSI_RED;
import static Utils.ColorConstraint.ANSI_GREEN;
import static Utils.ColorConstraint.ANSI_RESET;

public class UpdateTable {
    private static final TransactionManagerImpl transactionManager = new TransactionManagerImpl();

    /**
     * Updates records in the table based on the query.
     *
     * @param query the UPDATE query string
     */
    public static void update(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            System.out.println(ANSI_RED + "No database selected." + ANSI_RESET);
            EventLog.logDatabaseChange("No database selected for UPDATE query: " + query);
            return;
        }

        Matcher matcher = RegexPatterns.UPDATE_TABLE_PATTERN.matcher(query);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String updateColumn = matcher.group(2);
            String updateValue = matcher.group(4);
            String conditionColumn = matcher.group(5);
            String conditionValue = matcher.group(7);

            if (transactionManager.isTransactionActive()) {
                // If a transaction is active, add the update operation to the buffer
                transactionManager.addUpdateToTransaction(tableName, updateColumn, updateValue, conditionColumn, conditionValue);
                EventLog.logTransactionEvent("Update operation buffered for table " + tableName + ": " +
                        "UPDATE " + updateColumn + " = " + updateValue + " WHERE " + conditionColumn + " = " + conditionValue);
            } else {
                // If no transaction is active, execute the update directly on the table file
                executeUpdate(tableName, updateColumn, updateValue, conditionColumn, conditionValue);
            }
        } else {
            System.out.println(ANSI_RED + "Invalid UPDATE query format." + ANSI_RESET);
            EventLog.logDatabaseChange("Invalid UPDATE query format: " + query);
        }
    }

    /**
     * Executes the update operation on the specified table.
     *
     * @param tableName the name of the table
     * @param updateColumn the column to update
     * @param updateValue the value to set in the update column
     * @param conditionColumn the column to match for the condition
     * @param conditionValue the value to match for the condition
     */
    public static void executeUpdate(String tableName, String updateColumn, String updateValue, String conditionColumn, String conditionValue) {
        File tableFile = TableUtils.getTableFile(tableName);
        if (tableFile == null) {
            System.out.println(ANSI_RED + "Table " + tableName + " does not exist." + ANSI_RESET);
            EventLog.logDatabaseChange("Table " + tableName + " does not exist for UPDATE operation.");
            return;
        }

        try {
            List<String> fileLines = TableUtils.readTableFile(tableFile);
            if (fileLines == null) {
                System.out.println(ANSI_RED + "Error reading table file." + ANSI_RESET);
                EventLog.logCrashReport("Error reading table file for table " + tableName);
                return;
            }

            String[] columns = fileLines.getFirst().split("~~");
            int updateColumnIndex = TableUtils.getColumnIndex(columns, updateColumn);
            int conditionColumnIndex = TableUtils.getColumnIndex(columns, conditionColumn);

            if (updateColumnIndex == -1) {
                System.out.println(ANSI_RED + updateColumn + " not found in table " + tableName + "." + ANSI_RESET);
                EventLog.logDatabaseChange(updateColumn + " not found in table " + tableName + " for UPDATE operation.");
                return;
            }

            if (conditionColumnIndex == -1) {
                System.out.println(ANSI_RED + "ColumnDetail " + conditionColumn + " not found in table " + tableName + "." + ANSI_RESET);
                EventLog.logDatabaseChange("ColumnDetail " + conditionColumn + " not found in table " + tableName + " for UPDATE operation.");
                return;
            }

            String primaryKeyColumn = TableUtils.getPrimaryKeyColumnName(tableName);
            boolean isPrimaryKey = updateColumn.equals(primaryKeyColumn);

            // Check for NOT NULL constraint
            if (("NULL".equalsIgnoreCase(updateValue) || updateValue.isEmpty()) && (TableUtils.isNotNullConstraint(tableName, updateColumn) || isPrimaryKey)) {
                System.out.println(ANSI_RED + "Cannot update " + updateColumn + " to NULL as it has a NOT NULL constraint." + ANSI_RESET);
                EventLog.logDatabaseChange("Cannot update " + updateColumn + " to NULL as it has a NOT NULL constraint in table " + tableName);
                return;
            }

            // Check for UNIQUE constraint
            if ((TableUtils.isUniqueConstraint(tableName, updateColumn) || isPrimaryKey) && TableUtils.isValueExists(tableFile, updateColumnIndex, updateValue)) {
                System.out.println(ANSI_RED + "Cannot update " + updateColumn + " to " + updateValue + " as it must be unique." + ANSI_RESET);
                EventLog.logDatabaseChange("Cannot update " + updateColumn + " to " + updateValue + " as it must be unique in table " + tableName);
                return;
            }

            boolean updated = TableUtils.updateRecords(fileLines, updateColumnIndex, updateValue, conditionColumnIndex, conditionValue);

            if (updated) {
                try (FileWriter writer = new FileWriter(tableFile)) {
                    for (String line : fileLines) {
                        writer.write(line + System.lineSeparator());
                    }
                    System.out.println(ANSI_GREEN + "Record(s) updated successfully." + ANSI_RESET);
                    EventLog.logDatabaseChange("Record(s) updated successfully in table " + tableName + ": " +
                            "UPDATE " + updateColumn + " = " + updateValue + " WHERE " + conditionColumn + " = " + conditionValue);
                }
            } else {
                System.out.println(ANSI_RED + "No records matched the condition." + ANSI_RESET);
                EventLog.logDatabaseChange("No records matched the condition for UPDATE operation in table " + tableName + ": " +
                        "UPDATE " + updateColumn + " = " + updateValue + " WHERE " + conditionColumn + " = " + conditionValue);
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "An error occurred while updating the table." + ANSI_RESET);
            EventLog.logCrashReport("An error occurred while updating the table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}