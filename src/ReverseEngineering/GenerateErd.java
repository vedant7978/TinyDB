package ReverseEngineering;

import Model.ColumnDetail;
import Model.ForeignKey;
import java.io.File;
import java.util.*;

import static Utils.ColorConstraint.ANSI_GREEN;
import static Utils.ColorConstraint.ANSI_RESET;
import static Utils.DatabaseUtils.getAllTables;
import static Utils.ERDUtils.*;

public class GenerateErd {

    /**
     * Generates an Entity-Relationship Diagram (ERD) for the specified database.
     *
     * @param databaseName The name of the database for which to generate the ERD.
     */
    public static void generateERD(String databaseName) {
        File databaseDir = new File("./databases/" + databaseName);
        if (!databaseDir.exists() || !databaseDir.isDirectory()) {
            System.out.println("Database " + databaseName + " does not exist.");
            return;
        }

        List<String> tableNames = getAllTables(databaseName);
        Map<String, Map<String, ColumnDetail>> tableDetails = new HashMap<>();
        Map<String, List<ForeignKey>> foreignKeys = new HashMap<>();

        for (String tableName : tableNames) {
            Map<String, ColumnDetail> columns = getColumnDetails(databaseName, tableName);
            tableDetails.put(tableName, columns);
            foreignKeys.put(tableName, getForeignKeys(columns));
        }

        File erdFolder = createErdFolder(databaseName);
        if (erdFolder == null) return;

        String outputPath = erdFolder.getAbsolutePath() + "/ERD.txt";

        writeErdToFile(outputPath, databaseName, tableDetails, foreignKeys);

        System.out.println(ANSI_GREEN + "Your ERD for the database '" + databaseName + "' has been created under the folder 'ERD'." + ANSI_RESET);
    }
}