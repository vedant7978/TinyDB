package Query.Database;

public class DatabaseQueryValidator {
    public static boolean validateCreateDatabaseQuery(String[] parts) {
        return parts.length == 3 && parts[0].equalsIgnoreCase("CREATE") && parts[1].equalsIgnoreCase("DATABASE");
    }
    public static boolean validateUseDatabaseQuery(String[] parts) {
        return parts.length == 2 && parts[0].equalsIgnoreCase("USE");
    }
}