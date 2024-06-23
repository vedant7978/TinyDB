package Query.DataOperations;

import Utills.RegexPatterns;
import Utills.TableUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import static Utills.ColorConstraint.*;
import static Utills.TableUtils.removeRecord;

public class DeleteFromTable {
    public static void delete(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            return;
        }

        Matcher matcher = RegexPatterns.DELETE_FROM_PATTERN.matcher(query);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String column = matcher.group(2);
            String value = matcher.group(3);

            File tableFile = TableUtils.getTableFile(tableName);
            if (tableFile == null) {
                return;
            }

            try {
                List<String> fileLines = TableUtils.readTableFile(tableFile);
                if (fileLines == null) {
                    return;
                }

                String firstLine = fileLines.getFirst();
                String[] columns = firstLine.split("~~");
                int columnIndex = TableUtils.getColumnIndex(columns, column);
                if (columnIndex == -1) {
                    System.out.println(ANSI_RED + "Column " + column + " not found in table " + tableName + "." + ANSI_RESET);
                    return;
                }

                boolean recordFound = removeRecord(fileLines, columnIndex, value, tableFile);
                if (recordFound) {
                    System.out.println(ANSI_GREEN + "Record deleted successfully." + ANSI_RESET);
                } else {
                    System.out.println(ANSI_RED + "The specified data was not found in the table." + ANSI_RESET);
                }
            } catch (IOException e) {
                System.out.println(ANSI_RED + "Failed to delete record from table " + tableName + "." + ANSI_RESET);
                e.printStackTrace();
            }
        } else {
            System.out.println(ANSI_RED + "Invalid DELETE query format." + ANSI_RESET);
        }
    }
}
