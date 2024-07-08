package Query.DataOperations;

import Query.TransactionManagement.TransactionManager;
import Query.TransactionManagement.TransactionManagerImpl;
import Utils.RegexPatterns;
import Utils.TableUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static Utils.ColorConstraint.ANSI_RED;
import static Utils.ColorConstraint.ANSI_RESET;

public class SelectFromTable {

    private static final TransactionManager transactionManager = new TransactionManagerImpl();

    public static void select(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            return;
        }

        Matcher matcher = RegexPatterns.SELECT_FROM_PATTERN.matcher(query);

        if (matcher.matches()) {
            String columnsPart = matcher.group(1);
            String tableName = matcher.group(2);
            String conditionColumn = matcher.group(3);
            String conditionValue = matcher.group(4);

            List<String> fileLines = new ArrayList<>();
            try {
                // Read committed data from table file
                File tableFile = TableUtils.getTableFile(tableName);
                if (tableFile == null) {
                    System.out.println(ANSI_RED + "Table " + tableName + " does not exist." + ANSI_RESET);
                    return;
                }
                fileLines = TableUtils.readTableFile(tableFile);
                if (fileLines == null || fileLines.isEmpty()) {
                    System.out.println(ANSI_RED + "Table " + tableName + " is empty or could not be read." + ANSI_RESET);
                    return;
                }
            } catch (IOException e) {
                System.out.println(ANSI_RED + "An error occurred while reading the table." + ANSI_RESET);
                e.printStackTrace();
            }

            // Read data from transaction buffer
            List<String> bufferLines = new ArrayList<>();
            if (transactionManager.isTransactionActive()) {
                for (ArrayList<String> rowValues : TransactionManagerImpl.buffer.getOrDefault(tableName, new ArrayList<>())) {
                    String operation = rowValues.get(0);
                    if ("INSERT".equalsIgnoreCase(operation)) {
                        StringBuilder insertValues = new StringBuilder();
                        for (int i = 1; i < rowValues.size(); i++) {
                            if (insertValues.length() > 0) {
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
            }

            String[] headers = fileLines.get(0).split("~~");

            int[] columnIndices = getColumnIndices(columnsPart, headers, tableName);
            if (columnIndices == null) {
                return;
            }

            int conditionColumnIndex = -1;
            if (conditionColumn != null) {
                conditionColumnIndex = TableUtils.getColumnIndex(headers, conditionColumn);
                if (conditionColumnIndex == -1) {
                    System.out.println(ANSI_RED + "Column " + conditionColumn + " not found in table " + tableName + "." + ANSI_RESET);
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

            // Print headers for buffer data
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

            if (!recordFound) {
                System.out.println(ANSI_RED + "No matching records found for the condition " + conditionColumn + "='" + conditionValue + "'." + ANSI_RESET);
            }
        } else {
            System.out.println(ANSI_RED + "Invalid SELECT query format." + ANSI_RESET);
        }
    }

    private static int[] getColumnIndices(String columnsPart, String[] headers, String tableName) {
        int[] columnIndices;
        if (columnsPart.equals("*")) {
            columnIndices = new int[headers.length];
            for (int i = 0; i < headers.length;  i++) {
                columnIndices[i] = i;
            }
        } else {
            String[] requestedColumns = columnsPart.split(",");
            columnIndices = new int[requestedColumns.length];
            for (int i = 0; i < requestedColumns.length; i++) {
                columnIndices[i] = TableUtils.getColumnIndex(headers, requestedColumns[i].trim());
                if (columnIndices[i] == -1) {
                    System.out.println(ANSI_RED + "Column " + requestedColumns[i].trim() + " not found in table " + tableName + "." + ANSI_RESET);
                    return null;
                }
            }
        }
        return columnIndices;
    }
}
