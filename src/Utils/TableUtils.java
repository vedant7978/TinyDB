package Utils;

import Query.Database.UseDatabase;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static Utils.ColorConstraint.ANSI_RED;
import static Utils.ColorConstraint.ANSI_RESET;

public class TableUtils {

    public static boolean isDatabaseSelected() {
        if (!UseDatabase.isDatabaseSelected()) {
            System.out.println(ANSI_RED + "No database selected. Use the USE DATABASE command first." + ANSI_RESET);
            return false;
        }
        return true;
    }

    public static File getTableFile(String tableName) {
        String tableFilePath = "./databases/" + UseDatabase.getCurrentDatabase() + "/" + tableName + ".txt";
        File tableFile = new File(tableFilePath);
        if (!tableFile.exists()) {
            System.out.println(ANSI_RED + "Table file " + tableFile.getAbsolutePath() + " does not exist." + ANSI_RESET);
            return null;
        }
        return tableFile;
    }

    public static List<String> readTableFile(File tableFile) throws IOException {
        List<String> fileLines = Files.readAllLines(tableFile.toPath());
        if (fileLines.isEmpty()) {
            return null;
        }
        return fileLines;
    }

    public static int getColumnIndex(String[] columns, String columnName) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].split(" ")[0].equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }

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

    // Method to check for UNIQUE constraint
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