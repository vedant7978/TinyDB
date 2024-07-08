package Query.TransactionManagement;

public interface TransactionManager {
    void startTransaction();
    void commitTransaction();
    void rollbackTransaction();
    boolean isTransactionActive();
    void addQueryToTransaction(String tableName, String values);
    void addUpdateToTransaction(String tableName, String updateColumn, String updateValue, String conditionColumn, String conditionValue);
    void addDeleteToTransaction(String tableName, String column, String value);
}
