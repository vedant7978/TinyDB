package Utils;

import Query.Database.UseDatabase;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static Utils.ColorConstraint.ANSI_RED;
import static Utils.ColorConstraint.ANSI_RESET;

public class TableUtils {

    /**
     * Checks if a database is currently selected.
     * @return {@code true} if a database is selected, {@code false} otherwise.
     */
    public static boolean isDatabaseSelected() {
        if (!UseDatabase.isDatabaseSelected()) {
            System.out.println(ANSI_RED + "No database selected. Use the USE DATABASE command first." + ANSI_RESET);
            return false;
        }
        return true;
    }
    /**
     * Retrieves the file associated with a given table name.
     * @param tableName The name of the table.
     * @return The {@link File} object representing the table file, or {@code null} if the file does not exist.
     */
    public static File getTableFile(String tableName) {
        String tableFilePath = "./databases/" + UseDatabase.getCurrentDatabase() + "/" + tableName + ".txt";
        File tableFile = new File(tableFilePath);
        if (!tableFile.exists()) {
            System.out.println(ANSI_RED + "Table file " + tableFile.getAbsolutePath() + " does not exist." + ANSI_RESET);
            return null;
        }
        return tableFile;
    }

    /**
     * Reads the content of a table file.
     * @param tableFile The {@link File} object representing the table file.
     * @return A list of lines read from the table file, or {@code null} if the file is empty.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static List<String> readTableFile(File tableFile) throws IOException {
        List<String> fileLines = Files.readAllLines(tableFile.toPath());
        if (fileLines.isEmpty()) {
            return null;
        }
        return fileLines;
    }

    /**
     * Finds the index of a column in the list of columns.
     * @param columns An array of column definitions.
     * @param columnName The name of the column to find.
     * @return The index of the column in the array, or {@code -1} if the column is not found.
     */
    public static int getColumnIndex(String[] columns, String columnName) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].split(" ")[0].equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes a record from a table file based on a column value.
     * @param fileLines The list of lines read from the table file.
     * @param columnIndex The index of the column to check.
     * @param value The value to match for removal.
     * @param tableFile The {@link File} object representing the table file.
     * @return {@code true} if a record was found and removed, {@code false} otherwise.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public static boolean removeRecord(List<String> fileLines, int columnIndex, String value, File tableFile) throws IOException {
        boolean recordFound = false;
        try (FileWriter writer = new FileWriter(tableFile)) {
            writer.write(fileLines.getFirst() + System.lineSeparator());
            for (String line : fileLines.subList(1, fileLines.size())) {
                String[] values = line.split("~~");
                if (!values[columnIndex].equals(value)) {
                    writer.write(line + System.lineSeparator());
                } else {
                    recordFound = true;
                }
            }
        }
        return recordFound;
    }
    /**
     * Updates records in a table file based on a condition.
     * @param fileLines The list of lines read from the table file.
     * @param updateColumnIndex The index of the column to be updated.
     * @param updateValue The new value to set in the column.
     * @param conditionColumnIndex The index of the column to check for condition.
     * @param conditionValue The value to match for updating.
     * @return {@code true} if records were updated, {@code false} otherwise.
     */
    public static boolean updateRecords(List<String> fileLines, int updateColumnIndex, String updateValue, int conditionColumnIndex, String conditionValue) {
        boolean updated = false;
        for (int i = 1; i < fileLines.size(); i++) {
            String[] values = fileLines.get(i).split("~~");
            if (values[conditionColumnIndex].equals(conditionValue)) {
                values[updateColumnIndex] = updateValue;
                fileLines.set(i, String.join("~~", values));
                updated = true;
            }
        }
        return updated;
    }
    /**
     * Gets the number of tables in a specified database.
     * @param databaseName The name of the database.
     * @return The number of tables in the database, or {@code 0} if the database does not exist or an error occurs.
     */
    public static int getNumberOfTables(String databaseName) {
        File databaseDirectory = new File("./databases/" + databaseName);
        if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {
            // Filter files that are table files (ending with .txt)
            File[] tableFiles = databaseDirectory.listFiles((dir, name) -> name.endsWith(".txt"));
            if (tableFiles != null) {
                return tableFiles.length;
            } else {
                System.out.println(ANSI_RED + "Failed to list files in directory " + databaseDirectory.getAbsolutePath() + "." + ANSI_RESET);
            }
        } else {
            System.out.println(ANSI_RED + "Database directory " + databaseDirectory.getAbsolutePath() + " does not exist." + ANSI_RESET);
        }
        return 0;
    }
    /**
     * Checks if a column in a table has a NOT NULL constraint.
     * @param tableName The name of the table.
     * @param columnName The name of the column to check.
     * @return {@code true} if the column has a NOT NULL constraint, {@code false} otherwise.
     */
    public static boolean isNotNullConstraint(String tableName, String columnName) {
        File tableFile = getTableFile(tableName);
        if (tableFile == null) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
            // Read the first line which contains column definitions with constraints
            String line = reader.readLine();
            if (line == null) {
                return false;
            }
            String[] columns = line.split("~~");
            for (String column : columns) {
                String[] parts = column.split("\\s+");
                if (parts[0].equals(columnName) && column.toUpperCase().contains("NN")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if a column in a table has a UNIQUE constraint.
     * @param tableName The name of the table.
     * @param columnName The name of the column to check.
     * @return {@code true} if the column has a UNIQUE constraint, {@code false} otherwise.
     */
    public static boolean isUniqueConstraint(String tableName, String columnName) {
        File tableFile = getTableFile(tableName);
        if (tableFile == null) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
            // Read the first line which contains column definitions with constraints
            String line = reader.readLine();
            if (line == null) {
                return false;
            }
            String[] columns = line.split("~~");
            for (String column : columns) {
                String[] parts = column.split("\\s+");
                if (parts[0].equals(columnName) && column.toUpperCase().contains("U")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
    /**
     * Checks if a specific value exists in a column of a table file.
     * @param tableFile The {@link File} object representing the table file.
     * @param columnIndex The index of the column to check.
     * @param value The value to search for.
     * @return {@code true} if the value exists, {@code false} otherwise.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static boolean isValueExists(File tableFile, int columnIndex, String value) throws IOException {
        List<String> fileLines = readTableFile(tableFile);
        for (String line : fileLines.subList(1, fileLines.size())) {
            String[] values = line.split("~~");
            if (values[columnIndex].equals(value)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Retrieves the primary key column name from a table file.
     * @param tableName The name of the table.
     * @return The primary key column name, or {@code null} if not found or an error occurs.
     */
    public static String getPrimaryKeyColumnName(String tableName) {
        File tableFile = TableUtils.getTableFile(tableName);
        if (tableFile == null) {
            System.out.println("Table " + tableName + " does not exist.");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
            String firstLine = reader.readLine();
            if (firstLine != null && !firstLine.trim().isEmpty()) {
                String[] columns = firstLine.split("~~");
                for (String column : columns) {
                    if (column.contains("(PK)")) {
                        return column.split("\\s+")[0]; // Assuming column name is first word before (PK)
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading table file: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Retrieves column names from a specified table file that have a primary key or unique constraint.
     * @param databaseName The name of the database.
     * @param tableName The name of the table.
     * @return A list of column names with primary key or unique constraints.
     */
    public static List<String> getColumnNames(String databaseName, String tableName) {
        List<String> columnNames = new ArrayList<>();
        File tableFile = new File("./databases/" + databaseName + "/" + tableName + ".txt");

        // Check if the table file exists
        if (!tableFile.exists() || !tableFile.isFile()) {
            System.out.println("Table " + tableName + " does not exist in database " + databaseName + ".");
            return columnNames;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
            String line = reader.readLine();
            if (line != null) {
                // Split the line by "~~" and process each column definition
                String[] columns = line.split("~~");
                for (String column : columns) {
                    String trimmedColumn = column.trim();
                    // Check if the column contains either (PK) or (U)
                    if (trimmedColumn.contains("(PK)") || trimmedColumn.contains("(U)")) {
                        // Get only the column name before the first space
                        String columnName = trimmedColumn.split(" ")[0];
                        columnNames.add(columnName);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading table file: " + e.getMessage());
        }

        return columnNames;
    }
}