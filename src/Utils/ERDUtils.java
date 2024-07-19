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

                        ColumnDetail detail = new ColumnDetail(type, constraints, reference);
                        columnDetails.put(columnName, detail);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED+"Error reading table file: " + e.getMessage()+ANSI_RESET);
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
        if (refIndex >= 0) {
            constraintsPart = constraintsPart.substring(0, refIndex).trim();
        }
        return constraintsPart.replaceAll(" +", " ").trim();
    }

    private static String extractReference(String columnAttributes) {
        int refIndex = columnAttributes.indexOf("REFERENCES");
        if (refIndex >= 0) {
            return columnAttributes.substring(refIndex + "REFERENCES".length()).trim();
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
                    fkList.add(new ForeignKey(entry.getKey(), targetTable, targetColumn));
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
                System.out.println(ANSI_GREEN+"Created directory: " + erdFolder.getAbsolutePath()+ANSI_RESET);
            } else {
                System.out.println("Failed to create directory: " + erdFolder.getAbsolutePath());
                return null;
            }
        }
        return erdFolder;
    }

    public static void writeErdToFile(String outputPath, String databaseName, Map<String, Map<String, ColumnDetail>> tableDetails, Map<String, List<ForeignKey>> foreignKeys) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("# ERD Overview");
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
                ColumnDetail detail = columnEntry.getValue();
                writer.write("  - " + columnName + ": ");
                writer.newLine();
                writer.write("      -TYPE=" + detail.getType());
                writer.newLine();
                if (detail.getConstraints() != null) {
                    writer.write("      -CONSTRAINT=" + mapConstraints(detail.getConstraints()));
                    writer.newLine();
                }
                if (detail.getReference() != null) {
                    writer.write("      -REFERENCE=" + detail.getReference());
                    writer.newLine();
                }
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

                writer.write("- " + sourceTable + " -> " + fk.getTargetTable() + "." + fk.getTargetColumn() + " (" + cardinality + ")");
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

    private static String mapConstraints(String constraints) {
        String[] constraintArray = constraints.split(" ");
        StringBuilder mappedConstraints = new StringBuilder();
        for (String constraint : constraintArray) {
            if (!mappedConstraints.isEmpty()) {
                mappedConstraints.append(" ");
            }
            switch (constraint) {
                case "(PK)":
                    mappedConstraints.append("PRIMARY KEY");
                    break;
                case "(NN)":
                    mappedConstraints.append("NOT NULL");
                    break;
                case "(U)":
                    mappedConstraints.append("UNIQUE");
                    break;
                default:
                    mappedConstraints.append(constraint);
                    break;
            }
        }
        return mappedConstraints.toString();
    }
}