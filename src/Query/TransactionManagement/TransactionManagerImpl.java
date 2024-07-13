package Query.TransactionManagement;

import Log.EventLog;
import Query.DataOperations.DeleteFromTable;
import Query.DataOperations.InsertIntoTable;
import Query.DataOperations.UpdateTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Utils.ColorConstraint.*;

public class TransactionManagerImpl implements TransactionManager {

    // Buffer to store data during a transaction
    public static Map<String, ArrayList<ArrayList<String>>> buffer = new HashMap<>();

    // Flag to indicate if a transaction is active
    public static boolean transactionActive = false;

    @Override
    public void startTransaction() {
        transactionActive = true;
        System.out.println(ANSI_GREEN + "Transaction started." + ANSI_RESET);
        EventLog.logTransactionEvent("Transaction started.");
    }

    @Override
    public void commitTransaction() {
        if (transactionActive) {
            for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : buffer.entrySet()) {
                String tableName = entry.getKey();
                ArrayList<ArrayList<String>> tableBuffer = entry.getValue();
                for (ArrayList<String> rowValues : tableBuffer) {
                    String operation = rowValues.get(0);
                    if ("INSERT".equalsIgnoreCase(operation)) {
                        StringBuilder insertValues = new StringBuilder();
                        for (int i = 1; i < rowValues.size(); i++) {
                            if (insertValues.length() > 0) {
                                insertValues.append(",");
                            }
                            insertValues.append(rowValues.get(i));
                        }
                        InsertIntoTable.executeInsert(tableName, insertValues.toString());
                    } else if ("UPDATE".equalsIgnoreCase(operation)) {
                        UpdateTable.executeUpdate(tableName, rowValues.get(1), rowValues.get(2), rowValues.get(3), rowValues.get(4));
                    }else if ("DELETE".equalsIgnoreCase(operation)) {
                        DeleteFromTable.executeDelete(tableName, rowValues.get(1), rowValues.get(2));
                    }
                }
            }
            buffer.clear();
            transactionActive = false;
            System.out.println(ANSI_GREEN + "Transaction committed." + ANSI_RESET);
            EventLog.logTransactionEvent("Transaction committed.");
        } else {
            System.out.println(ANSI_RED + "No active transaction to commit." + ANSI_RESET);
            EventLog.logTransactionEvent("No active transaction to commit.");
        }
    }

    @Override
    public void rollbackTransaction() {
        if (transactionActive) {
            buffer.clear();
            transactionActive = false;
            System.out.println(ANSI_GREEN + "Transaction rolled back." + ANSI_RESET);
            EventLog.logTransactionEvent("Transaction rolled back.");
        } else {
            System.out.println(ANSI_RED + "No active transaction to rollback." + ANSI_RESET);
            EventLog.logTransactionEvent("No active transaction to rollback.");
        }
    }

    @Override
    public boolean isTransactionActive() {
        return transactionActive;
    }

    @Override
    public void addQueryToTransaction(String tableName, String values) {
        ArrayList<ArrayList<String>> tableBuffer = buffer.getOrDefault(tableName, new ArrayList<>());
        ArrayList<String> rowValues = new ArrayList<>();
        rowValues.add("INSERT"); // Mark this as an INSERT operation
        for (String value : values.split(",")) {
            rowValues.add(value.trim().replaceAll("^'|'$", ""));
        }
        tableBuffer.add(rowValues);
        buffer.put(tableName, tableBuffer);
        System.out.println(ANSI_GREEN + "Insert operation added to buffer: " + values + ANSI_RESET);
        EventLog.logTransactionEvent("Insert operation added to buffer: " + values);
    }

    @Override
    public void addUpdateToTransaction(String tableName, String updateColumn, String updateValue, String conditionColumn, String conditionValue) {
        ArrayList<ArrayList<String>> tableBuffer = buffer.getOrDefault(tableName, new ArrayList<>());
        ArrayList<String> rowValues = new ArrayList<>();
        rowValues.add("UPDATE");
        rowValues.add(updateColumn);
        rowValues.add(updateValue);
        rowValues.add(conditionColumn);
        rowValues.add(conditionValue);
        tableBuffer.add(rowValues);
        buffer.put(tableName, tableBuffer);
        System.out.println(ANSI_GREEN + "Update operation added to buffer: " + updateColumn + " = " + updateValue + " WHERE " + conditionColumn + " = " + conditionValue + ANSI_RESET);
        EventLog.logTransactionEvent("Update operation added to buffer: " + updateColumn + " = " + updateValue + " WHERE " + conditionColumn + " = " + conditionValue);
    }
    public void addDeleteToTransaction(String tableName, String column, String value) {
        ArrayList<ArrayList<String>> tableBuffer = buffer.getOrDefault(tableName, new ArrayList<>());
        ArrayList<String> rowValues = new ArrayList<>();
        rowValues.add("DELETE");
        rowValues.add(column);
        rowValues.add(value);
        tableBuffer.add(rowValues);
        buffer.put(tableName, tableBuffer);
        System.out.println(ANSI_GREEN + "Delete operation added to buffer: " + column + " = " + value + ANSI_RESET);
        EventLog.logTransactionEvent("Delete operation added to buffer: " + column + " = " + value);
    }
}
