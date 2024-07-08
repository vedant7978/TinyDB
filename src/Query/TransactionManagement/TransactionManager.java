package Query.TransactionManagement;

public interface TransactionManager {
    void startTransaction();
    void commitTransaction();
    void rollbackTransaction();
    boolean isTransactionActive();
    void addQueryToTransaction(String tableName, String values);

    void addDeleteToTransaction(String tableName, String column, String value);
}
