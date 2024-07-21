package Query.Database;

public class DatabaseQueryValidator {
    /**
     * Validates a CREATE DATABASE query.
     *
     * @param parts the parts of the query to validate
     * @return true if the query is valid, false otherwise
     */
    public static boolean validateCreateDatabaseQuery(String[] parts) {
        return parts.length == 3 && parts[0].equalsIgnoreCase("CREATE") && parts[1].equalsIgnoreCase("DATABASE");
    }

    /**
     * Validates a USE DATABASE query.
     *
     * @param parts the parts of the query to validate
     * @return true if the query is valid, false otherwise
     */
    public static boolean validateUseDatabaseQuery(String[] parts) {
        return parts.length == 2 && parts[0].equalsIgnoreCase("USE");
    }
}