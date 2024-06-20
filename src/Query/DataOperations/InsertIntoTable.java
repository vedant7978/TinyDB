package Query.DataOperations;

import Utills.RegexPatterns;
import Utills.TableUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

public class InsertIntoTable {
    public static void insert(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            return;
        }

        Matcher matcher = RegexPatterns.INSERT_INTO_PATTERN.matcher(query);
        if (!matcher.matches()) {
            System.out.println("Invalid INSERT INTO TABLE query.");
            return;
        }

        String tableName = matcher.group(1);
        String values = matcher.group(2).replaceAll("\n", "").replaceAll("\r", "").trim();

        File tableFile = TableUtils.getTableFile(tableName);
        if (tableFile == null) {
            return;
        }

        try {
            List<String> fileLines = TableUtils.readTableFile(tableFile);
            if (fileLines == null) {
                return;
            }

            String firstLine = fileLines.get(0);
            int columnCount = firstLine.split("~~").length;

            String[] valuesArray = values.split(",");
            if (valuesArray.length != columnCount) {
                System.out.println("Column count (" + columnCount + ") does not match value count (" + valuesArray.length + ").");
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
                System.out.println("Record inserted successfully.");
            }
        } catch (IOException e) {
            System.out.println("Failed to insert record into table " + tableName + ".");
            e.printStackTrace();
        }
    }
}
