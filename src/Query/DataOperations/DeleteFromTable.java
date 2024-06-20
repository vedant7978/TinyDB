package Query.DataOperations;

import Utills.RegexPatterns;
import Utills.TableUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import static Utills.TableUtils.removeRecord;

public class DeleteFromTable {
    public static void delete(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            return;
        }

        Matcher matcher = RegexPatterns.DELETE_FROM_PATTERN.matcher(query);
        if (!matcher.matches()) {
            System.out.println("Invalid DELETE query format.");
            return;
        }

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
                System.out.println("Column " + column + " not found in table " + tableName + ".");
                return;
            }

            boolean recordFound = removeRecord(fileLines, columnIndex, value, tableFile);
            if (recordFound) {
                System.out.println("Record deleted successfully.");
            } else {
                System.out.println("The specified data was not found in the table.");
            }
        } catch (IOException e) {
            System.out.println("Failed to delete record from table " + tableName + ".");
            e.printStackTrace();
        }
    }
}
