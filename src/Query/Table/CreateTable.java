package Query.Table;

import Utils.RegexPatterns;
import Utils.TableUtils;
import Log.EventLog;
import Log.QueryLog;
import Log.GeneralLog;

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
            logQueryAndState(query, "No database selected", false);
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
                        logQueryAndState(query, "Table " + tableName + " created successfully with columns: " + formattedColumns, true);
                    } catch (IOException e) {
                        System.out.println(ANSI_RED + "An error occurred while creating the table." + ANSI_RESET);
                        e.printStackTrace();
                        logQueryAndState(query, "Error occurred while creating table " + tableName + ": " + e.getMessage(), false);
                    }
                } else {
                    System.out.println(ANSI_RED + "Table " + tableName + " already exists." + ANSI_RESET);
                    logQueryAndState(query, "Attempted to create table " + tableName + ", but it already exists.", false);
                }
            } else {
                System.out.println(ANSI_RED + "Invalid CREATE TABLE query format." + ANSI_RESET);
                logQueryAndState(query, "Invalid CREATE TABLE query format: " + query, false);
            }
        } else {
            System.out.println(ANSI_RED + "Invalid query. Primary key not specified in the CREATE TABLE statement." + ANSI_RESET);
            logQueryAndState(query, "Invalid CREATE TABLE query. Primary key not specified: " + query, false);
        }
    }

    private static void logQueryAndState(String query, String message, boolean success) {
        long startTime = System.currentTimeMillis();
        String databaseName = getCurrentDatabase();
        int numberOfTables = TableUtils.getNumberOfTables(databaseName);
        int totalRecords = TableUtils.getTotalRecords(new File("./databases/" + databaseName));

        GeneralLog.log(query, System.currentTimeMillis() - startTime, numberOfTables, totalRecords);
        QueryLog.logUserQuery(query, startTime);
        EventLog.logDatabaseChange(message);
    }
}
