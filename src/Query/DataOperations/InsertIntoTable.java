package Query.DataOperations;

import Utills.RegexPatterns;
import Utills.TableUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import static Utills.ColorConstraint.*;

public class InsertIntoTable {
    public static void insert(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            return;
        }

        Matcher matcher = RegexPatterns.INSERT_INTO_PATTERN.matcher(query);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String values = matcher.group(2).replaceAll("\n", "").replaceAll("\r", "").trim();

            File tableFile = TableUtils.getTableFile(tableName);
            if (tableFile == null) {
                return;
            }

            try {
                List<String> fileLines = TableUtils.readTableFile(tableFile);
                if (fileLines == null || fileLines.isEmpty()) {
                    return;
                }

                String firstLine = fileLines.getFirst();
                String[] columnDefinitions = firstLine.split("~~");
                int columnCount = columnDefinitions.length;

                String[] valuesArray = values.split(",");
                if (valuesArray.length != columnCount) {
                    System.out.println(ANSI_RED + "Column count (" + columnCount + ") does not match value count (" + valuesArray.length + ")."+ANSI_RESET);
                    return;
                }

                boolean primaryKeyValid = validatePrimaryKey(tableFile, columnDefinitions, valuesArray);
                if (!primaryKeyValid) {
                    return;
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
                    System.out.println(ANSI_GREEN + "Record inserted successfully." + ANSI_RESET);
                }
            } catch (IOException e) {
                System.out.println(ANSI_RED + "Failed to insert record into table " + tableName + "." + ANSI_RESET);
                e.printStackTrace();
            }
        } else {
            System.out.println(ANSI_RED + "Invalid INSERT INTO TABLE query." + ANSI_RESET);
        }
    }

    private static boolean validatePrimaryKey(File tableFile, String[] columnDefinitions, String[] valuesArray) {
        int primaryKeyIndex = -1;
        for (int i = 0; i < columnDefinitions.length; i++) {
            if (columnDefinitions[i].contains("(PK)")) {
                primaryKeyIndex = i;
                break;
            }
        }

        if (valuesArray.length <= primaryKeyIndex || valuesArray[primaryKeyIndex].isEmpty()) {
            System.out.println(ANSI_RED + "Primary key column cannot be null." + ANSI_RESET);
            return false;
        }

        try {
            List<String> fileLines = TableUtils.readTableFile(tableFile);
            if (fileLines != null && fileLines.size() > 1) {
                String primaryKeyValue = valuesArray[primaryKeyIndex].trim();
                for (int i = 1; i < fileLines.size(); i++) {
                    String[] existingValues = fileLines.get(i).split("~~");
                    if (existingValues.length > primaryKeyIndex && existingValues[primaryKeyIndex].trim().equals(primaryKeyValue)) {
                        System.out.println(ANSI_RED + "Duplicate primary key value." + ANSI_RESET);
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Error reading table file for primary key validation." + ANSI_RESET);
            e.printStackTrace();
            return false;
        }

        return true;
    }
}