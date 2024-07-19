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

    public static boolean databaseExists(String databaseName) {
        String databasesPath = "./databases";
        String databasePath = databasesPath + "/" + databaseName;
        return Files.isDirectory(Paths.get(databasePath));
    }

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

    private static void saveToFile(List<String> content, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : content) {
                writer.write(line);
            }
        }
    }
}