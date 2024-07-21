package Model;
/**
 * Represents a foreign key constraint in a database table.
 * <p>
 * This class encapsulates the column name in the current table, the target table, and the target column that the foreign key references.
 * </p>
 */
public class ForeignKey {
    private final String columnName;
    private final String targetTable;
    private final String targetColumn;
    /**
     * Constructs a {@link ForeignKey} object with the specified column name, target table, and target column.
     * @param columnName The name of the column in the current table that is the foreign key.
     * @param targetTable The name of the table that the foreign key references.
     * @param targetColumn The name of the column in the target table that the foreign key references.
     */
    public ForeignKey(String columnName, String targetTable, String targetColumn) {
        this.columnName = columnName;
        this.targetTable = targetTable;
        this.targetColumn = targetColumn;
    }
    /**
     * Gets the name of the column in the current table that is the foreign key.
     * @return The column name.
     */
    public String getColumnName() {
        return columnName;
    }
    /**
     * Gets the name of the target table that the foreign key references.
     * @return The target table name.
     */
    public String getTargetTable() {
        return targetTable;
    }
    /**
     * Gets the name of the column in the target table that the foreign key references.
     * @return The target column name.
     */
    public String getTargetColumn() {
        return targetColumn;
    }
}