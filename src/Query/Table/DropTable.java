package Query.Table;

import Query.Database.UseDatabase;

import java.io.File;

import static Query.Database.UseDatabase.getCurrentDatabase;
import static Query.Table.DatabaseTableValidator.validateDropTable;

public class DropTable {
    public static void drop(String query) {
        if (!UseDatabase.isDatabaseSelected()) {
            System.out.println("No database selected. Use the USE DATABASE command first.");
            return;
        }
        String[] parts = query.split(" ", 3);
        if (validateDropTable(parts)) {
            String tableName = parts[2];
            File tableFile = new File("./databases/" + getCurrentDatabase() + "/" + tableName + ".txt");
            if (!tableFile.exists()) {
                System.out.println("Table " + tableName + " does not exist.");
                return;
            }
            if (tableFile.delete()) {
                System.out.println("Table " + tableName + " dropped successfully.");
            } else {
                System.out.println("Failed to drop table " + tableName + ".");
            }
        } else {
            System.out.println("Invalid DROP TABLE query.");
        }
    }
}
