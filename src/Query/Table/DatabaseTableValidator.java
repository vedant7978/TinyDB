package Query.Table;

import Utills.RegexPatterns;

import java.util.regex.Matcher;

public class DatabaseTableValidator {
    public static boolean validateDropTable(String[] parts) {
        return parts.length >= 3 && parts[0].equalsIgnoreCase("DROP") && parts[1].equalsIgnoreCase("TABLE");
    }

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