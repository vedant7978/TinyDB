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


/**
 * Utility class for handling ERD (Entity-Relationship Diagram) operations.
 */
public class ERDUtils {
    /**
     * Retrieves the details of the columns in a specified table of a given database.
     *
     * @param databaseName the name of the database
     * @param tableName the name of the table
     * @return a map of column names to their details
     */
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
    /**
     * Extracts the type of a column from its attributes.
     *
     * @param columnAttributes the column attributes string
     * @return the column type
     */

    private static String extractType(String columnAttributes) {
        int spaceIndex = columnAttributes.indexOf(' ');
        return spaceIndex > 0 ? columnAttributes.substring(0, spaceIndex).trim() : columnAttributes.trim();
    }
    /**
     * Extracts the constraints of a column from its attributes.
     *
     * @param columnAttributes the column attributes string
     * @return the column constraints
     */
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
    /**
     * Extracts the reference part of a column's attributes.
     *
     * @param columnAttributes the column attributes string
     * @return the reference string
     */
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
    /**
     * Extracts the relation part of a column's attributes.
     *
     * @param columnAttributes the column attributes string
     * @return the relation string
     */
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
    /**
     * Retrieves the foreign keys from the column details.
     *
     * @param columns a map of column names to their details
     * @return a list of foreign keys
     */
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
    /**
     * Creates a folder for storing ERD files.
     *
     * @param databaseName the name of the database
     * @return the ERD folder file object
     */
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
    /**
     * Writes the ERD to a file.
     *
     * @param outputPath the output path for the ERD file
     * @param databaseName the name of the database
     * @param tableDetails a map of table names to their column details
     * @param foreignKeys a map of table names to their foreign keys
     */
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
    /**
     * Writes the table details to the ERD file.
     *
     * @param writer the buffered writer
     * @param tableDetails a map of table names to their column details
     * @throws IOException if an I/O error occurs
     */
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
    /**
     * Writes the relationships (foreign keys) to the ERD file.
     *
     * @param writer the buffered writer
     * @param tableDetails a map of table names to their column details
     * @param foreignKeys a map of table names to their foreign keys
     * @throws IOException if an I/O error occurs
     */
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
    /**
     * Determines the cardinality of a relationship based on the column details.
     *
     * @param sourceDetail the column detail of the source column
     * @return the cardinality string
     */
    private static String determineCardinality(ColumnDetail sourceDetail) {
        if (sourceDetail.getConstraints() != null) {
            if (sourceDetail.getConstraints().contains("(PK)") || sourceDetail.getConstraints().contains("(U)")) {
                return "One to One";
            }
        }
        return "Many to One";
    }

}