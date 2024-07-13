package Query.Table;

import Utils.TableUtils;
import Log.EventLog;
import Log.QueryLog;
import Log.GeneralLog;

import java.io.File;

import static Query.Database.UseDatabase.getCurrentDatabase;
import static Query.Table.DatabaseTableValidator.validateDropTable;
import static Utils.ColorConstraint.*;

public class DropTable {
    public static void drop(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            System.out.println(ANSI_RED + "No database selected." + ANSI_RESET);
            logQueryAndState(query, "No database selected", false);
            return;
        }

        String[] parts = query.split(" ", 3);
        if (validateDropTable(parts)) {
            String tableName = parts[2];
            File tableFile = new File("./databases/" + getCurrentDatabase() + "/" + tableName + ".txt");

            if (!tableFile.exists()) {
                System.out.println(ANSI_RED + "Table " + tableName + " does not exist." + ANSI_RESET);
                logQueryAndState(query, "Attempted to drop table " + tableName + ", but it does not exist.", false);
                return;
            }

            if (tableFile.delete()) {
                System.out.println(ANSI_GREEN + "Table " + tableName + " dropped successfully." + ANSI_RESET);
                logQueryAndState(query, "Table " + tableName + " dropped successfully.", true);
            } else {
                System.out.println(ANSI_RED + "Failed to drop table " + tableName + "." + ANSI_RESET);
                logQueryAndState(query, "Failed to drop table " + tableName + ".", false);
            }
        } else {
            System.out.println(ANSI_RED + "Invalid DROP TABLE query." + ANSI_RESET);
            logQueryAndState(query, "Invalid DROP TABLE query: " + query, false);
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
