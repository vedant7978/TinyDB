package Utills;

import Query.Database.UseDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TableUtils {

    public static boolean isDatabaseSelected() {
        if (!UseDatabase.isDatabaseSelected()) {
            System.out.println("No database selected. Use the USE DATABASE command first.");
            return false;
        }
        return true;
    }

    public static File getTableFile(String tableName) {
        String tableFilePath = "./databases/" + UseDatabase.getCurrentDatabase() + "/" + tableName + ".txt";
        File tableFile = new File(tableFilePath);
        if (!tableFile.exists()) {
            System.out.println("Table file " + tableFile.getAbsolutePath() + " does not exist.");
            return null;
        }
        return tableFile;
    }

    public static List<String> readTableFile(File tableFile) throws IOException {
        List<String> fileLines = Files.readAllLines(tableFile.toPath());
        if (fileLines.isEmpty()) {
            System.out.println("Table " + tableFile.getName() + " is empty.");
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
            writer.write(fileLines.get(0) + System.lineSeparator());
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
}
