package Query.DataOperations;

import Utills.RegexPatterns;
import Utills.TableUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import static Utills.ColorConstraint.ANSI_RED;
import static Utills.ColorConstraint.ANSI_RESET;

public class SelectFromTable {
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

            File tableFile = TableUtils.getTableFile(tableName);
            if (tableFile == null) {
                return;
            }

            try {
                List<String> fileLines = TableUtils.readTableFile(tableFile);
                if (fileLines == null) {
                    return;
                }

                String[] headers = fileLines.getFirst().split("~~");

                int[] columnIndices = getColumnIndices(columnsPart, headers, tableName);
                if (columnIndices == null) {
                    return;
                }

                for (int index : columnIndices) {
                    System.out.print(headers[index].trim().split(" ")[0] + "\t");
                }
                System.out.println();

                int conditionColumnIndex = -1;
                if (conditionColumn != null) {
                    conditionColumnIndex = TableUtils.getColumnIndex(headers, conditionColumn);
                    if (conditionColumnIndex == -1) {
                        System.out.println(ANSI_RED + "Column " + conditionColumn + " not found in table " + tableName + "." + ANSI_RESET);
                        return;
                    }
                }

                for (String line : fileLines.subList(1, fileLines.size())) {
                    String[] rowValues = line.split("~~", -1);
                    if (conditionColumnIndex == -1 || rowValues[conditionColumnIndex].trim().equalsIgnoreCase(conditionValue)) {
                        for (int index : columnIndices) {
                            System.out.print(rowValues[index].trim().replace("'", "") + "\t");
                        }
                        System.out.println();
                    }
                }
            } catch (IOException e) {
                System.out.println(ANSI_RED + "An error occurred while reading the table." + ANSI_RESET);
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid SELECT query format.");
        }
    }

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
                    System.out.println(ANSI_RED + "Column " + requestedColumns[i].trim() + " not found in table " + tableName + "." + ANSI_RESET);
                    return null;
                }
            }
        }
        return columnIndices;
    }
}
