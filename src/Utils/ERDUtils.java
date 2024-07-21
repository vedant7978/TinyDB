package Utils;

import Model.ColumnDetail;
import Model.ForeignKey;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Utils.ColorConstraint.*;

public class ERDUtils {

    public static Map<String, ColumnDetail> getColumnDetails(String databaseName, String tableName) {
        Map<String, ColumnDetail> columnDetails = new HashMap<>();
        File tableFile = new File("./databases/" + databaseName + "/" + tableName + ".txt");

        if (!tableFile.exists() || !tableFile.isFile()) {
            System.out.println("Table " + tableName + " does not exist in database " + databaseName + ".");
            return columnDetails;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
            String line = reader.readLine();
            if (line != null) {
                String[] columns = line.split("~~");
                for (String column : columns) {
                    String[] parts = column.trim().split(" ", 2);
                    if (parts.length == 2) {
                        String columnName = parts[0].trim();
                        String columnAttributes = parts[1].trim();

                        String type = extractType(columnAttributes);
                        String constraints = extractConstraints(columnAttributes);
                        String reference = extractReference(columnAttributes);
                        String relation = extractRelation(columnAttributes);

                        ColumnDetail detail = new ColumnDetail(type, constraints, reference, relation);
                        columnDetails.put(columnName, detail);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Error reading table file: " + e.getMessage() + ANSI_RESET);
        }

        return columnDetails;
    }

    private static String extractType(String columnAttributes) {
        int spaceIndex = columnAttributes.indexOf(' ');
        return spaceIndex > 0 ? columnAttributes.substring(0, spaceIndex).trim() : columnAttributes.trim();
    }

    private static String extractConstraints(String columnAttributes) {
        int typeEndIndex = columnAttributes.indexOf(' ');
        if (typeEndIndex < 0) return null;

        String constraintsPart = columnAttributes.substring(typeEndIndex).trim();
        int refIndex = constraintsPart.indexOf("REFERENCES");
        int relIndex = constraintsPart.indexOf("RELATION");

        if (refIndex >= 0) {
            constraintsPart = constraintsPart.substring(0, refIndex).trim();
        } else if (relIndex >= 0) {
            constraintsPart = constraintsPart.substring(0, relIndex).trim();
        }
        return constraintsPart.replaceAll(" +", " ").trim();
    }

    private static String extractReference(String columnAttributes) {
        int refIndex = columnAttributes.indexOf("REFERENCES");
        if (refIndex >= 0) {
            int relIndex = columnAttributes.indexOf("RELATION");
            if (relIndex >= 0) {
                return columnAttributes.substring(refIndex + "REFERENCES".length(), relIndex).trim();
            }
            return columnAttributes.substring(refIndex + "REFERENCES".length()).trim();
        }
        return null;
    }

    private static String extractRelation(String columnAttributes) {
        int relIndex = columnAttributes.indexOf("RELATION");
        if (relIndex >= 0) {
            String relation = columnAttributes.substring(relIndex + "RELATION".length()).trim();
            // Remove any surrounding brackets
            if (relation.startsWith("(") && relation.endsWith(")")) {
                relation = relation.substring(1, relation.length() - 1).trim();
            }
            return relation;
        }
        return null;
    }

    public static List<ForeignKey> getForeignKeys(Map<String, ColumnDetail> columns) {
        List<ForeignKey> fkList = new ArrayList<>();
        for (Map.Entry<String, ColumnDetail> entry : columns.entrySet()) {
            String reference = entry.getValue().getReference();
            if (reference != null) {
                Pattern pattern = Pattern.compile("(\\w+)\\((\\w+)\\)");
                Matcher matcher = pattern.matcher(reference);
                if (matcher.find()) {
                    String targetTable = matcher.group(1);
                    String targetColumn = matcher.group(2);
                    String relation = entry.getValue().getRelation();
                    fkList.add(new ForeignKey(entry.getKey(), targetTable, targetColumn, relation));
                }
            }
        }
        return fkList;
    }

    public static File createErdFolder(String databaseName) {
        File erdMainFolder = new File("./ERD");
        if (!erdMainFolder.exists()) {
            if (!erdMainFolder.mkdirs()) {
                System.out.println("Failed to create main ERD directory.");
                return null;
            }
        }

        File erdFolder = new File("./ERD/" + databaseName);
        if (!erdFolder.exists()) {
            if (erdFolder.mkdirs()) {
                System.out.println(ANSI_GREEN + "Created directory: " + erdFolder.getAbsolutePath() + ANSI_RESET);
            } else {
                System.out.println("Failed to create directory: " + erdFolder.getAbsolutePath());
                return null;
            }
        }
        return erdFolder;
    }

    public static void writeErdToFile(String outputPath, String databaseName, Map<String, Map<String, ColumnDetail>> tableDetails, Map<String, List<ForeignKey>> foreignKeys) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("ERD");
            writer.newLine();
            writer.write("Database: " + databaseName);
            writer.newLine();
            writer.newLine();

            writeTables(writer, tableDetails);
            writeRelationships(writer, tableDetails, foreignKeys);

        } catch (IOException e) {
            System.out.println("Error writing ERD file: " + e.getMessage());
        }
    }

    private static void writeTables(BufferedWriter writer, Map<String, Map<String, ColumnDetail>> tableDetails) throws IOException {
        writer.write("Tables:");
        writer.newLine();
        for (Map.Entry<String, Map<String, ColumnDetail>> tableEntry : tableDetails.entrySet()) {
            String tableName = tableEntry.getKey();
            writer.write("- " + tableName);
            writer.newLine();
            for (Map.Entry<String, ColumnDetail> columnEntry : tableEntry.getValue().entrySet()) {
                String columnName = columnEntry.getKey();
                writer.write("  - " + columnName);

                writer.newLine();
            }
            writer.newLine();
        }
    }

    private static void writeRelationships(BufferedWriter writer, Map<String, Map<String, ColumnDetail>> tableDetails, Map<String, List<ForeignKey>> foreignKeys) throws IOException {
        writer.write("Relationships:");
        writer.newLine();
        for (Map.Entry<String, List<ForeignKey>> fkEntry : foreignKeys.entrySet()) {
            String sourceTable = fkEntry.getKey();
            List<ForeignKey> fkList = fkEntry.getValue();
            for (ForeignKey fk : fkList) {
                String sourceColumn = fk.getColumnName();
                ColumnDetail sourceDetail = tableDetails.get(sourceTable).get(sourceColumn);

                String cardinality = determineCardinality(sourceDetail);

                writer.write("- " + sourceTable + "." + sourceColumn + " -> " + fk.getTargetTable() + "." + fk.getTargetColumn() + " (" + cardinality + ")");
                if (fk.getRelation() != null) {
                    writer.write(" RELATION(" + fk.getRelation() + ")");
                }
                writer.newLine();
            }
        }
    }

    private static String determineCardinality(ColumnDetail sourceDetail) {
        if (sourceDetail.getConstraints() != null) {
            if (sourceDetail.getConstraints().contains("(PK)") || sourceDetail.getConstraints().contains("(U)")) {
                return "One to One";
            }
        }
        return "Many to One";
    }

}