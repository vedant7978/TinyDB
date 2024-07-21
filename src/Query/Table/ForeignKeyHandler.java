package Query.Table;

import Utils.MenuUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static Query.Database.UseDatabase.getCurrentDatabase;
import static Utils.ColorConstraint.*;
import static Utils.TableUtils.getColumnNames;
import static Utils.DatabaseUtils.getAllTables;


/**
 * Handles the addition of foreign key constraints to tables.
 */
public class ForeignKeyHandler {


    /**
     * Handles the addition of foreign keys to a table.
     *
     * @param tableName The name of the table.
     * @param columnsDefinition The columns definition string.
     */
    public static void handleForeignKey(String tableName, String columnsDefinition) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to add a foreign key? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            List<String> foreignKeyColumns = new ArrayList<>();
            List<String> foreignKeyConstraints = new ArrayList<>();
            List<String> foreignKeyRelations = new ArrayList<>();

            while (true) {
                System.out.print("For which column do you want to add the foreign key? (type 'done' to finish adding foreign keys, 'exit' to cancel): ");
                String foreignKeyColumn = scanner.nextLine().trim();

                if (foreignKeyColumn.equalsIgnoreCase("exit")) {
                    System.out.println(ANSI_RED + "Foreign key addition canceled." + ANSI_RESET);
                    MenuUtils.displayUserOptions();
                    return;
                }

                if (foreignKeyColumn.equalsIgnoreCase("done")) {
                    break;
                }

                if (isColumnInDefinition(columnsDefinition, foreignKeyColumn)) {
                    foreignKeyColumns.add(foreignKeyColumn);
                } else {
                    System.out.println(ANSI_RED + "Column " + foreignKeyColumn + " does not exist in the table definition." + ANSI_RESET);
                }
            }

            List<String> tables = getAllTables(getCurrentDatabase());
            if (tables.isEmpty()) {
                System.out.println(ANSI_RED + "No tables found in the current database. Creating table without foreign keys." + ANSI_RESET);
                CreateTable.createTableWithForeignKey(tableName, columnsDefinition);
                return;
            }

            System.out.println("Available tables:");
            for (String table : tables) {
                System.out.println(table);
            }

            StringBuilder updatedColumnsDefinition = new StringBuilder(columnsDefinition);
            for (String foreignKeyColumn : foreignKeyColumns) {
                String referencedTable = "";
                while (true) {
                    System.out.print("Enter the table name to which you want to add the foreign key for column '" + foreignKeyColumn + "': ");
                    referencedTable = scanner.nextLine().trim();

                    if (referencedTable.equalsIgnoreCase("exit")) {
                        System.out.println(ANSI_RED + "Foreign key addition canceled." + ANSI_RESET);
                        MenuUtils.displayUserOptions();
                        return;
                    }

                    if (tables.contains(referencedTable)) {
                        break;
                    } else {
                        System.out.println(ANSI_RED + "Table " + referencedTable + " does not exist." + ANSI_RESET);
                    }
                }

                List<String> referencedColumns = getColumnNames(getCurrentDatabase(), referencedTable);
                if (referencedColumns.isEmpty()) {
                    System.out.println(ANSI_RED + "No columns found in the table " + referencedTable + "." + ANSI_RESET);
                    continue;
                }

                System.out.println("Columns in the table " + referencedTable + ":");
                for (String column : referencedColumns) {
                    System.out.println(column);
                }

                String referencedColumn = "";
                while (true) {
                    System.out.print("Enter the column name to which you want to add the foreign key for column " + foreignKeyColumn + " (only unique or primary key columns will be shown): ");

                    referencedColumn = scanner.nextLine().trim();

                    if (referencedColumn.equalsIgnoreCase("exit")) {
                        System.out.println(ANSI_RED + "Foreign key addition canceled." + ANSI_RESET);
                        MenuUtils.displayUserOptions();
                        return;
                    }

                    if (referencedColumns.contains(referencedColumn)) {
                        break;
                    } else {
                        System.out.println(ANSI_RED + "Column " + referencedColumn + " does not exist in the table " + referencedTable + "." + ANSI_RESET);
                    }
                }

                // Prompt the user for the relationship type
                System.out.print("Enter the relationship between the tables (e.g., 'enroll', 'contains'): ");
                String relationship = scanner.nextLine().trim();

                // Add foreign key constraint and relationship directly to the column definition
                String foreignKeyConstraint = foreignKeyColumn + " REFERENCES " + referencedTable + "(" + referencedColumn + ")";
                String foreignKeyRelation = foreignKeyColumn + " RELATION(" + relationship + ")";
                foreignKeyConstraints.add(foreignKeyConstraint);
                foreignKeyRelations.add(foreignKeyRelation);
                System.out.println(ANSI_GREEN + "Foreign key added successfully: " + foreignKeyConstraint + " with relation " + relationship + ANSI_RESET);
            }

            String finalColumnsDefinition = updateColumnsDefinition(columnsDefinition, foreignKeyConstraints, foreignKeyRelations);
            CreateTable.createTableWithForeignKey(tableName, finalColumnsDefinition);
        } else {
            String formattedColumnsDefinition = formatColumnsDefinition(columnsDefinition);
            CreateTable.createTableWithForeignKey(tableName, formattedColumnsDefinition);
        }
    }

    /**
     * Updates the columns definition to include foreign key constraints and relationships.
     *
     * @param columnsDefinition The original columns definition string.
     * @param foreignKeyConstraints The list of foreign key constraints to add.
     * @param foreignKeyRelations The list of foreign key relationships to add.
     * @return The updated columns definition with foreign key constraints and relationships.
     */
    private static String updateColumnsDefinition(String columnsDefinition, List<String> foreignKeyConstraints, List<String> foreignKeyRelations) {
        StringBuilder updatedColumnsDefinition = new StringBuilder();
        String[] columns = columnsDefinition.split(",\\s*");
        boolean firstColumn = true;

        for (String column : columns) {
            String trimmedColumn = column.trim();
            StringBuilder updatedColumn = new StringBuilder(trimmedColumn);

            // Apply constraints
            boolean isPrimaryKey = trimmedColumn.toUpperCase().endsWith("PRIMARY KEY");
            boolean isNotNull = trimmedColumn.toUpperCase().contains("NOT NULL");
            boolean isUnique = trimmedColumn.toUpperCase().contains("UNIQUE");

            if (isPrimaryKey) {
                updatedColumn = new StringBuilder(trimmedColumn.substring(0, trimmedColumn.toUpperCase().indexOf("PRIMARY KEY")).trim() + " (PK)");
            }

            if (isNotNull) {
                updatedColumn = new StringBuilder(updatedColumn.toString().replaceAll("(?i)\\bNOT NULL\\b", "").trim() + " (NN)");
            }

            if (isUnique) {
                updatedColumn = new StringBuilder(updatedColumn.toString().replaceAll("(?i)\\bUNIQUE\\b", "").trim() + " (U)");
            }

            for (String foreignKeyConstraint : foreignKeyConstraints) {
                if (foreignKeyConstraint.startsWith(updatedColumn.toString().split(" ")[0])) {
                    updatedColumn.append(" ").append(foreignKeyConstraint.substring(foreignKeyConstraint.indexOf("REFERENCES")));
                    break;
                }
            }

            for (String foreignKeyRelation : foreignKeyRelations) {
                if (foreignKeyRelation.startsWith(updatedColumn.toString().split(" ")[0])) {
                    updatedColumn.append(" ").append(foreignKeyRelation.substring(foreignKeyRelation.indexOf("RELATION")));
                    break;
                }
            }

            if (!firstColumn) {
                updatedColumnsDefinition.append("~~");
            }

            updatedColumnsDefinition.append(updatedColumn);

            firstColumn = false;
        }

        return updatedColumnsDefinition.toString();
    }

    /**
     * Formats the columns definition by adding constraint labels.
     *
     * @param columnsDefinition The columns definition string.
     * @return The formatted columns definition with labels for primary key, not null, and unique constraints.
     */
    private static String formatColumnsDefinition(String columnsDefinition) {
        String formattedColumnsDefinition = columnsDefinition
                .replaceAll("(?i)\\bPRIMARY KEY\\b", "(PK)")
                .replaceAll("(?i)\\bNOT NULL\\b", "(NN)")
                .replaceAll("(?i)\\bUNIQUE\\b", "(U)")
                .replaceAll(",\\s*", "~~");
        return formattedColumnsDefinition;
    }

    /**
     * Checks if a column is present in the columns definition.
     *
     * @param columnsDefinition The columns definition string.
     * @param columnName The name of the column to check.
     * @return True if the column is present, otherwise false.
     */
    private static boolean isColumnInDefinition(String columnsDefinition, String columnName) {
        String[] columns = columnsDefinition.split(",\\s*");
        for (String column : columns) {
            if (column.trim().startsWith(columnName + " ")) {
                return true;
            }
        }
        return false;
    }
}