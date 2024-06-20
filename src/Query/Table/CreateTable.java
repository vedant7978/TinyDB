package Query.Table;

import Query.Database.UseDatabase;
import Utills.RegexPatterns;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;

import static Query.Database.UseDatabase.getCurrentDatabase;

public class CreateTable {
    public static void create(String query) {
        if (!UseDatabase.isDatabaseSelected()) {
            System.out.println("No database selected. Use the USE DATABASE command first.");
            return;
        }
        Matcher matcher = RegexPatterns.CREATE_TABLE_PATTERN.matcher(query);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String columnsDefinition = matcher.group(2).replaceAll("\n", "").replaceAll("\r", "").trim();

            File tableFile = new File("./databases/" + getCurrentDatabase() + "/" + tableName + ".txt");
            if (!tableFile.exists()) {
                String[] columns = columnsDefinition.split(",");
                StringBuilder formattedColumns = new StringBuilder();
                for (String column : columns) {
                    column = column.trim();
                    if (!formattedColumns.isEmpty()) {
                        formattedColumns.append("~~");
                    }
                    formattedColumns.append(column);
                }

                try (FileWriter writer = new FileWriter(tableFile)) {
                    writer.write(formattedColumns.toString());
                    System.out.println("Table " + tableName + " created successfully with columns: " + formattedColumns);
                } catch (IOException e) {
                    System.out.println("An error occurred while creating the table.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Table " + tableName + " already exists.");
            }

        }
    }

}
