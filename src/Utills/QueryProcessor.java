package Utills;

import Query.DataOperations.DeleteFromTable;
import Query.DataOperations.InsertIntoTable;
import Query.DataOperations.SelectFromTable;
import Query.DataOperations.UpdateTable;
import Query.Database.CreateDatabase;
import Query.Database.UseDatabase;
import Query.Table.CreateTable;
import Query.Table.DropTable;
import Query.TransactionManagement.TransactionManagerImpl;

import java.util.Scanner;

import static Utills.ColorConstraint.ANSI_RED;
import static Utills.ColorConstraint.ANSI_RESET;

public class QueryProcessor {
    private static TransactionManagerImpl transactionManager = new TransactionManagerImpl();

    public static void writeQueries() {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your SQL query: ");
        StringBuilder queryBuilder = new StringBuilder();
        String line = scanner.nextLine();
        queryBuilder.append(line).append(" ");
        String query = queryBuilder.toString().trim();

        if (!query.endsWith(";")) {
            System.out.println(ANSI_RED + "Invalid query format. Query must end with a semicolon (;)." + ANSI_RESET);
            return;
        }

        query = query.substring(0, query.length() - 1).trim();
        query = query.replaceAll("\\s+", " ");

        if (query.toLowerCase().startsWith("create database")) {
            CreateDatabase.create(query);
        } else if (query.toLowerCase().startsWith("use")) {
            UseDatabase.use(query);
        } else if (query.toLowerCase().startsWith("create table")) {
            CreateTable.create(query);
        } else if (query.toLowerCase().startsWith("insert into")) {
            InsertIntoTable.insert(query);
        } else if (query.toLowerCase().startsWith("select")) {
            SelectFromTable.select(query);
        } else if (query.toLowerCase().startsWith("update")) {
            UpdateTable.update(query);
        } else if (query.toLowerCase().startsWith("delete from")) {
            DeleteFromTable.delete(query);
        } else if (query.toLowerCase().startsWith("drop table")) {
            DropTable.drop(query);
        } else if (query.toLowerCase().startsWith("start transaction")) {
            transactionManager.startTransaction();
        } else if (query.toLowerCase().startsWith("commit transaction")) {
            transactionManager.commitTransaction();
        } else if (query.toLowerCase().startsWith("rollback")) {
            transactionManager.rollbackTransaction();
        } else {
            System.out.println(ANSI_RED + "Invalid query. Please enter a valid SQL query." + ANSI_RESET);
        }
    }
}
