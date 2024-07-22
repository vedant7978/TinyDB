package Query.DataOperations;

import Log.GeneralLog;
import Query.TransactionManagement.TransactionManager;
import Query.TransactionManagement.TransactionManagerImpl;
import Utils.DatabaseUtils;
import Utils.RegexPatterns;
import Utils.TableUtils;
import Log.EventLog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import static Query.Database.UseDatabase.getCurrentDatabase;
import static Utils.ColorConstraint.*;

public class InsertIntoTable {

    private static final TransactionManager transactionManager = new TransactionManagerImpl();

    /**
     * Inserts records into the table based on the query.
     *
     * @param query the INSERT INTO query string
     */
    public static void insert(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            System.out.println(ANSI_RED + "No database selected." + ANSI_RESET);
            EventLog.logDatabaseChange("No database selected for INSERT INTO query: " + query);
            return;
        }

        Matcher matcher = RegexPatterns.INSERT_INTO_PATTERN.matcher(query);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String values = matcher.group(2).replaceAll("\n", "").replaceAll("\r", "").trim();
            values = values.replaceAll("'", "");

            if (transactionManager.isTransactionActive()) {
                // If a transaction is active, store the query in the buffer
                transactionManager.addQueryToTransaction(tableName, values);
                System.out.println(ANSI_GREEN + "Insert operation buffered for table " + tableName + "." + ANSI_RESET);
                EventLog.logDatabaseChange("Insert operation buffered for table " + tableName + " with values: " + values);
            } else {
                // If no transaction is active, execute the query directly on the table file
                executeInsert(tableName, values);
            }
        } else {
            System.out.println(ANSI_RED + "Invalid INSERT INTO TABLE query format." + ANSI_RESET);
            EventLog.logDatabaseChange("Invalid INSERT INTO TABLE query format: " + query);
        }
    }

    /**
     * Executes the insert operation on the specified table.
     *
     * @param tableName the name of the table
     * @param values the values to insert into the table
     */
    public static void executeInsert(String tableName, String values) {
        long startTime = System.currentTimeMillis();

        File tableFile = TableUtils.getTableFile(tableName);
        if (tableFile == null) {
            System.out.println(ANSI_RED + "Table " + tableName + " does not exist." + ANSI_RESET);
            EventLog.logDatabaseChange("Table " + tableName + " does not exist for INSERT operation.");
            return;
        }

        try {
            List<String> fileLines = TableUtils.readTableFile(tableFile);
            if (fileLines == null || fileLines.isEmpty()) {
                System.out.println(ANSI_RED + "Table " + tableName + " is empty or could not be read." + ANSI_RESET);
                EventLog.logDatabaseChange("Table " + tableName + " is empty or could not be read for INSERT operation.");
                return;
            }

            String firstLine = fileLines.getFirst();
            String[] columnDefinitions = firstLine.split("~~");
            int columnCount = columnDefinitions.length;

            String[] valuesArray = values.split(",");
            if (valuesArray.length != columnCount) {
                System.out.println(ANSI_RED + "ColumnDetail count (" + columnCount + ") does not match value count (" + valuesArray.length + ")." + ANSI_RESET);
                EventLog.logDatabaseChange("ColumnDetail count (" + columnCount + ") does not match value count (" + valuesArray.length + ") for table " + tableName);
                return;
            }

            for (int i = 0; i < columnDefinitions.length; i++) {
                if (columnDefinitions[i].contains("(PK)")) {
                    if (!validateNotNull(valuesArray[i]) || !validateUnique(tableFile, i, valuesArray[i])) {
                        return;
                    }
                } else {
                    if (columnDefinitions[i].contains("(NN)") && !validateNotNull(valuesArray[i])) {
                        return;
                    }
                    if (columnDefinitions[i].contains("(U)") && !validateUnique(tableFile, i, valuesArray[i])) {
                        return;
                    }
                }
            }

            StringBuilder formattedValues = new StringBuilder();
            for (String value : valuesArray) {
                if (!formattedValues.isEmpty()) {
                    formattedValues.append("~~");
                }
                value = value.trim().replaceAll("^'|'$", "");
                formattedValues.append(value);
            }

            try (FileWriter writer = new FileWriter(tableFile, true)) {
                writer.write(System.lineSeparator() + formattedValues);
                System.out.println(ANSI_GREEN + "Record inserted successfully into table " + tableName + "." + ANSI_RESET);
                EventLog.logDatabaseChange("Record inserted successfully into table " + tableName + " with values: " + formattedValues);
                // Get current database directory
                File currentDatabaseDir = new File("./databases/" + getCurrentDatabase());

                // Get the execution time
                long executionTime = System.currentTimeMillis() - startTime;

                // Count the number of table files and total records in the current database
                int numberOfTables = DatabaseUtils.getAllTables(getCurrentDatabase()).size();
                int totalRecords = DatabaseUtils.getTotalRecords(currentDatabaseDir);

                // Log the general details
                GeneralLog.log("Insert", executionTime, numberOfTables, totalRecords);
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Failed to insert record into table " + tableName + "." + ANSI_RESET);
            EventLog.logCrashReport("Failed to insert record into table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates that the value is not null or empty.
     *
     * @param value the value to validate
     * @return true if the value is not null or empty, false otherwise
     */
    private static boolean validateNotNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            System.out.println(ANSI_RED + "NOT NULL constraint violated." + ANSI_RESET);
            return false;
        }
        return true;
    }

    /**
     * Validates that the value is unique in the specified column.
     *
     * @param tableFile the table file
     * @param columnIndex the index of the column to check for uniqueness
     * @param value the value to check for uniqueness
     * @return true if the value is unique, false otherwise
     * @throws IOException if an I/O error occurs
     */
    private static boolean validateUnique(File tableFile, int columnIndex, String value) {
        try {
            List<String> fileLines = TableUtils.readTableFile(tableFile);
            if (fileLines != null && fileLines.size() > 1) {
                String uniqueValue = value.trim();
                for (int i = 1; i < fileLines.size(); i++) {
                    String[] existingValues = fileLines.get(i).split("~~");
                    if (existingValues.length > columnIndex && existingValues[columnIndex].trim().equals(uniqueValue)) {
                        System.out.println(ANSI_RED + "Unique constraint violated." + ANSI_RESET);
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Error reading table file for unique constraint validation." + ANSI_RESET);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}