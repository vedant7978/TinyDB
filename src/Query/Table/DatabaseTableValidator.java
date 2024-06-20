package Query.Table;

public class DatabaseTableValidator {
    public static boolean validateDropTable(String[] parts) {
        return parts.length >= 3 && parts[0].equalsIgnoreCase("DROP") && parts[1].equalsIgnoreCase("TABLE");
    }
}