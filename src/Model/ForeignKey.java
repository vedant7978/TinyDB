package Model;

public class ForeignKey {
    private final String columnName;
    private final String targetTable;
    private final String targetColumn;

    public ForeignKey(String columnName, String targetTable, String targetColumn) {
        this.columnName = columnName;
        this.targetTable = targetTable;
        this.targetColumn = targetColumn;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public String getTargetColumn() {
        return targetColumn;
    }
}