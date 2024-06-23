package Query.Table;

import Utills.TableUtils;

import java.io.File;
import static Query.Database.UseDatabase.getCurrentDatabase;
import static Query.Table.DatabaseTableValidator.validateDropTable;
import static Utills.ColorConstraint.*;

public class DropTable {
    public static void drop(String query) {
        if (!TableUtils.isDatabaseSelected()) {
            return;
        }
        String[] parts = query.split(" ", 3);
        if (validateDropTable(parts)) {
            String tableName = parts[2];
            File tableFile = new File("./databases/" + getCurrentDatabase() + "/" + tableName + ".txt");
            if (!tableFile.exists()) {
                System.out.println(ANSI_RED + "Table " + tableName + " does not exist." + ANSI_RESET);
                return;
            }
            if (tableFile.delete()) {
                System.out.println(ANSI_GREEN + "Table " + tableName + " dropped successfully." + ANSI_RESET);
            } else {
                System.out.println(ANSI_RED + "Failed to drop table " + tableName + "." + ANSI_RESET);
            }
        } else {
            System.out.println(ANSI_RED+"Invalid DROP TABLE query." + ANSI_RESET);
        }
    }
}
