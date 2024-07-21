package Query.Table;

import Utils.RegexPatterns;

import java.util.regex.Matcher;


/**
 * Validates SQL queries related to database tables.
 */
public class DatabaseTableValidator {
    /**
     * Validates a DROP TABLE query.
     *
     * @param parts The parts of the SQL query.
     * @return True if the query is valid, otherwise false.
     */
    public static boolean validateDropTable(String[] parts) {
        return parts.length >= 3 && parts[0].equalsIgnoreCase("DROP") && parts[1].equalsIgnoreCase("TABLE");
    }

    /**
     * Checks if a CREATE TABLE query is valid.
     *
     * @param query The SQL CREATE TABLE query.
     * @return True if the query is valid, otherwise false.
     */
    public static boolean isValidCreateTableQuery(String query) {
        Matcher matcher = RegexPatterns.VALID_QUERY_PATTERN.matcher(query.trim());

        if (matcher.matches()) {
            String columnDefinitions = matcher.group(2).trim();
            String[] columns = columnDefinitions.split(",");
            boolean foundPrimaryKey = false;

            for (String column : columns) {
                column = column.trim();

                if (column.toUpperCase().endsWith("PRIMARY KEY")) {
                    foundPrimaryKey = true;
                    break;
                }
            }

            return foundPrimaryKey;
        }

        return false;
    }
}