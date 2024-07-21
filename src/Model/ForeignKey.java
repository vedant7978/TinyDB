package Model;

public class ForeignKey {
    private final String columnName;
    private final String targetTable;
    private final String targetColumn;
    private final String relation;

    public ForeignKey(String columnName, String targetTable, String targetColumn, String relation) {
        this.columnName = columnName;
        this.targetTable = targetTable;
        this.targetColumn = targetColumn;
        this.relation = relation;
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

    public String getRelation() {
        return relation;
    }
}