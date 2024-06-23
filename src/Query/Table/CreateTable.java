package Query.Table;

import Utills.RegexPatterns;
import Utills.TableUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import static Query.Database.UseDatabase.getCurrentDatabase;
import static Query.Table.DatabaseTableValidator.isValidCreateTableQuery;
import static Utills.ColorConstraint.*;

public class CreateTable {
    public static void create(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            return;
        }
        if (isValidCreateTableQuery(query)) {

            Matcher matcher = RegexPatterns.CREATE_TABLE_PATTERN.matcher(query);

            if (matcher.matches()) {
                String tableName = matcher.group(1);
                String columnsDefinition = matcher.group(2).replaceAll("\n", "").replaceAll("\r", "").trim();

                File tableFile = new File("./databases/" + getCurrentDatabase() + "/" + tableName + ".txt");
                if (!tableFile.exists()) {
                    String[] columns = columnsDefinition.split(",");
                    StringBuilder formattedColumns = new StringBuilder();
                    boolean firstColumn = true;

                    for (String column : columns) {
                        column = column.trim();

                        if (!firstColumn) {
                            formattedColumns.append("~~");
                        }

                        if (column.toUpperCase().endsWith("PRIMARY KEY")) {
                            column = column.substring(0, column.toUpperCase().indexOf("PRIMARY KEY")).trim() + " (PK)";
                        }

                        formattedColumns.append(column);

                        if (firstColumn) {
                            firstColumn = false;
                        }
                    }

                    try (FileWriter writer = new FileWriter(tableFile)) {
                        writer.write(formattedColumns.toString());
                        System.out.println(ANSI_GREEN + "Table " + tableName + " created successfully with columns: " + formattedColumns + ANSI_RESET);
                    } catch (IOException e) {
                        System.out.println(ANSI_RED + "An error occurred while creating the table." + ANSI_RESET);
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(ANSI_RED + "Table " + tableName + " already exists." + ANSI_RESET);
                }
            }
        } else {
            System.out.println(ANSI_RED + "Invalid query structure." + ANSI_RESET);
        }

    }
}
