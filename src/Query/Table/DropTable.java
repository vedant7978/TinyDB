package Query.Table;

import Utils.TableUtils;
import Log.EventLog;
import Log.QueryLog;

import java.io.File;

import static Query.Database.UseDatabase.getCurrentDatabase;
import static Query.Table.DatabaseTableValidator.validateDropTable;
import static Utils.ColorConstraint.*;

public class DropTable {
    public static void drop(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            System.out.println(ANSI_RED + "No database selected." + ANSI_RESET);
            QueryLog.logUserQuery(query, System.currentTimeMillis());
            EventLog.logDatabaseChange("No database selected for DROP TABLE query: " + query);
            return;
        }

        String[] parts = query.split(" ", 3);
        if (validateDropTable(parts)) {
            String tableName = parts[2];
            File tableFile = new File("./databases/" + getCurrentDatabase() + "/" + tableName + ".txt");

            if (!tableFile.exists()) {
                System.out.println(ANSI_RED + "Table " + tableName + " does not exist." + ANSI_RESET);
                EventLog.logDatabaseChange("Attempted to drop table " + tableName + ", but it does not exist.");
                QueryLog.logUserQuery( query, System.currentTimeMillis());
                return;
            }

            if (tableFile.delete()) {
                System.out.println(ANSI_GREEN + "Table " + tableName + " dropped successfully." + ANSI_RESET);
                EventLog.logDatabaseChange("Table " + tableName + " dropped successfully.");
                QueryLog.logUserQuery(query, System.currentTimeMillis());
            } else {
                System.out.println(ANSI_RED + "Failed to drop table " + tableName + "." + ANSI_RESET);
                EventLog.logDatabaseChange("Failed to drop table " + tableName + ".");
                QueryLog.logUserQuery(query, System.currentTimeMillis());
            }
        } else {
            System.out.println(ANSI_RED + "Invalid DROP TABLE query." + ANSI_RESET);
            EventLog.logDatabaseChange("Invalid DROP TABLE query: " + query);
            QueryLog.logUserQuery(query, System.currentTimeMillis());
        }
    }
}
