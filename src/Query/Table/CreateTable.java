package Query.Table;

import Utils.RegexPatterns;
import Utils.TableUtils;
import Log.EventLog;
import Log.QueryLog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;

import static Query.Database.UseDatabase.getCurrentDatabase;
import static Query.Table.DatabaseTableValidator.isValidCreateTableQuery;
import static Utils.ColorConstraint.*;

public class CreateTable {
    public static void create(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            System.out.println(ANSI_RED + "No database selected." + ANSI_RESET);
            QueryLog.logUserQuery("system", query, System.currentTimeMillis());
            EventLog.logDatabaseChange("No database selected for CREATE TABLE query: " + query);
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
                        EventLog.logDatabaseChange("Table " + tableName + " created successfully with columns: " + formattedColumns);
                    } catch (IOException e) {
                        System.out.println(ANSI_RED + "An error occurred while creating the table." + ANSI_RESET);
                        e.printStackTrace();
                        EventLog.logDatabaseChange("Error occurred while creating table " + tableName + ": " + e.getMessage());
                    }
                } else {
                    System.out.println(ANSI_RED + "Table " + tableName + " already exists." + ANSI_RESET);
                    EventLog.logDatabaseChange("Attempted to create table " + tableName + ", but it already exists.");
                }
                QueryLog.logUserQuery("system", query, System.currentTimeMillis());
            } else {
                System.out.println(ANSI_RED + "Invalid CREATE TABLE query format." + ANSI_RESET);
                EventLog.logDatabaseChange("Invalid CREATE TABLE query format: " + query);
                QueryLog.logUserQuery("system", query, System.currentTimeMillis());
            }
        } else {
            System.out.println(ANSI_RED + "Invalid query. Primary key not specified in the CREATE TABLE statement." + ANSI_RESET);
            EventLog.logDatabaseChange("Invalid CREATE TABLE query. Primary key not specified: " + query);
            QueryLog.logUserQuery("system", query, System.currentTimeMillis());
        }
    }
}
