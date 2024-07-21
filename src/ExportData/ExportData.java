package ExportData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExportData {

    /**
     * Checks if the specified database exists.
     *
     * @param databaseName the name of the database to check
     * @return true if the database exists, false otherwise
     */
    public static boolean databaseExists(String databaseName) {
        String databasesPath = "./databases";
        String databasePath = databasesPath + "/" + databaseName;
        return Files.isDirectory(Paths.get(databasePath));
    }

    /**
     * Exports the specified database to a SQL file.
     *
     * @param databaseName the name of the database to export
     */
    public static void exportDatabase(String databaseName) {
        String databasesPath = "./databases";
        String exportPath = "./ExportFiles";
        String databasePath = databasesPath + "/" + databaseName;
        String outputFile = exportPath + "/" + databaseName + ".sql";

        try {
            Files.createDirectories(Paths.get(exportPath));
            List<String> sqlDump = new ArrayList<>();
            Files.walk(Paths.get(databasePath))
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.toString().contains("logs"))
                    .forEach(tablePath -> {
                        String tableName = tablePath.getFileName().toString();
                        tableName = tableName.substring(0, tableName.lastIndexOf("."));
                        try {
                            sqlDump.add(exportStructureOfTable(tablePath, tableName));
                            sqlDump.add(exportDataOfData(tablePath, tableName));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            saveToFile(sqlDump, outputFile);
            System.out.println("Database export for " + databaseName + " completed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exports the structure of the specified table.
     *
     * @param path      the path to the table file
     * @param tableName the name of the table
     * @return the SQL string for creating the table
     * @throws IOException if an I/O error occurs
     */
    private static String exportStructureOfTable(Path path, String tableName) throws IOException {
        List<String> lines = Files.readAllLines(path);
        StringBuilder sql = new StringBuilder();
        sql.append("DROP TABLE IF EXISTS ").append(tableName).append(";\n");

        String[] columns = lines.getFirst().split("~~");
        sql.append("CREATE TABLE ").append(tableName).append(" (\n");
        for (String column : columns) {
            String columnDefinition = column.replace(" (PK)", " PRIMARY KEY");
            sql.append("  ").append(columnDefinition).append(",\n");
        }
        sql.setLength(sql.length() - 2);
        sql.append("\n);\n\n");

        return sql.toString();
    }

    /**
     * Exports the data of the specified table.
     *
     * @param path      the path to the table file
     * @param tableName the name of the table
     * @return the SQL string for inserting the data into the table
     * @throws IOException if an I/O error occurs
     */
    private static String exportDataOfData(Path path, String tableName) throws IOException {
        List<String> lines = Files.readAllLines(path);
        StringBuilder sql = new StringBuilder();
        String[] columns = lines.getFirst().split("~~");

        for (int i = 1; i < lines.size(); i++) {
            String[] values = lines.get(i).split("~~");
            sql.append("INSERT INTO ").append(tableName).append(" (");
            for (String column : columns) {
                sql.append(column.split(" ")[0]).append(", ");
            }
            sql.setLength(sql.length() - 2);
            sql.append(") VALUES (");
            for (String value : values) {
                sql.append("'").append(value.equalsIgnoreCase("null") ? null : value).append("', ");
            }
            sql.setLength(sql.length() - 2);
            sql.append(");\n");
        }
        sql.append("\n");

        return sql.toString();
    }

    /**
     * Saves the given content to a file.
     *
     * @param content  the list of strings to write to the file
     * @param filename the name of the file to save the content in
     * @throws IOException if an I/O error occurs
     */
    private static void saveToFile(List<String> content, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : content) {
                writer.write(line);
            }
        }
    }
}