package Utils;

import Query.Database.UseDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

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
            System.out.println(ANSI_RED+"Table file " + tableFile.getAbsolutePath() + " does not exist."+ANSI_RESET);
            return null;
        }
        return tableFile;
    }

    public static List<String> readTableFile(File tableFile) throws IOException {
        List<String> fileLines = Files.readAllLines(tableFile.toPath());
        if (fileLines.isEmpty()) {
            System.out.println(ANSI_RED+"Table " + tableFile.getName() + " is empty."+ANSI_RESET);
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
    public static boolean writeTableFile(File tableFile, List<String> fileLines) {
        try (FileWriter writer = new FileWriter(tableFile)) {
            for (String line : fileLines) {
                writer.write(line + System.lineSeparator());
            }
            return true;
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Failed to write to table file " + tableFile.getName() + "." + ANSI_RESET);
            e.printStackTrace();
            return false;
        }
    }
}
