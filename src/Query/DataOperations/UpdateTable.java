package Query.DataOperations;

import Utills.RegexPatterns;
import Utills.TableUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import static Utills.ColorConstraint.*;
import static Utills.TableUtils.updateRecords;

public class UpdateTable {
    public static void update(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            return;
        }

        Matcher matcher = RegexPatterns.UPDATE_TABLE_PATTERN.matcher(query);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String updateColumn = matcher.group(2);
            String updateValue = matcher.group(3);
            String conditionColumn = matcher.group(4);
            String conditionValue = matcher.group(5);

            File tableFile = TableUtils.getTableFile(tableName);
            if (tableFile == null) {
                return;
            }

            try {
                List<String> fileLines = TableUtils.readTableFile(tableFile);
                if (fileLines == null) {
                    return;
                }

                String[] columns = fileLines.getFirst().split("~~");
                int updateColumnIndex = TableUtils.getColumnIndex(columns, updateColumn);
                int conditionColumnIndex = TableUtils.getColumnIndex(columns, conditionColumn);

                if (updateColumnIndex == -1) {
                    System.out.println(ANSI_RED + updateColumn + " not found in table " + tableName + "." + ANSI_RESET);
                    return;
                }

                if (conditionColumnIndex == -1) {
                    System.out.println(ANSI_RED + "Column " + conditionColumn + " not found in table " + tableName + "." + ANSI_RESET);
                    return;
                }

                boolean updated = updateRecords(fileLines, updateColumnIndex, updateValue, conditionColumnIndex, conditionValue);

                if (updated) {
                    try (FileWriter writer = new FileWriter(tableFile)) {
                        for (String line : fileLines) {
                            writer.write(line + System.lineSeparator());
                        }
                        System.out.println(ANSI_GREEN + "Record(s) updated successfully." + ANSI_RESET);
                    }
                } else {
                    System.out.println(ANSI_RED + "No records matched the condition." + ANSI_RESET);
                }
            } catch (IOException e) {
                System.out.println(ANSI_RED + "An error occurred while updating the table." + ANSI_RESET);
                e.printStackTrace();
            }
        } else {
            System.out.println(ANSI_RED + "Invalid UPDATE query format." + ANSI_RESET);
        }
    }

}
