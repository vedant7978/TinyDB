package Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Utils.ColorConstraint.ANSI_RED;
import static Utils.ColorConstraint.ANSI_RESET;
import static Utils.TableUtils.readTableFile;

/**
 * Utility class for database operations.
 */
public class DatabaseUtils {
    /**
     * Retrieves a list of all table names in a given database.
     *
     * @param databaseName the name of the database
     * @return a list of table names
     */
    public static List<String> getAllTables(String databaseName) {
        List<String> tables = new ArrayList<>();
        File databaseDir = new File("./databases/" + databaseName);

        // Check if the database directory exists
        if (databaseDir.exists() && databaseDir.isDirectory()) {
            // List all files ending with ".txt" (representing tables)
            File[] tableFiles = databaseDir.listFiles((dir, name) -> name.endsWith(".txt"));
            if (tableFiles != null) {
                for (File tableFile : tableFiles) {
                    tables.add(tableFile.getName().replace(".txt", ""));
                }
            }
        } else {
            System.out.println("Database " + databaseName + " does not exist or is not a directory.");
        }

        return tables;
    }

    /**
     * Calculates the total number of records across all tables in a given database directory.
     *
     * @param databaseDirectory the directory of the database
     * @return the total number of records
     */
    public static int getTotalRecords(File databaseDirectory) {
        int totalRecords = 0;
        for (File tableFile : databaseDirectory.listFiles()) {
            if (tableFile.isFile()) {
                try {
                    List<String> fileLines = readTableFile(tableFile);
                    if (fileLines != null) {
                        totalRecords += fileLines.size() - 1; // Subtracting 1 to exclude header line
                    }
                } catch (IOException e) {
                    System.out.println(ANSI_RED + "Failed to read table file " + tableFile.getName() + "." + ANSI_RESET);
                    e.printStackTrace();
                }
            }
        }
        return totalRecords;
    }
}
