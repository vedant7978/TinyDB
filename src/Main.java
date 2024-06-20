import Query.DataOperations.InsertIntoTable;
import Query.Database.CreateDatabase;
import Query.Database.UseDatabase;
import Query.Table.CreateTable;
import Query.Table.DropTable;
import Security.login.UserLoginImpl;
import Security.register.UserRegistrationImpl;

import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserLoginImpl userLogin = new UserLoginImpl();
        UserRegistrationImpl userRegistration = new UserRegistrationImpl();
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Choose an action: ");
            System.out.println("1. Login");
            System.out.println("2. Register");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    if (userLogin.userLogin()) {
                        displayUserOptions();
                    }
                    break;
                case 2:
                    userRegistration.userRegistration();
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void displayUserOptions() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Choose an action: ");
            System.out.println("1. Write Queries");
            System.out.println("2. Export Data and Structure");
            System.out.println("3. ERD");
            System.out.println("4. Exit");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    writeQueries();
                    break;
                case 2:
                    exportDataAndStructure();
                    break;
                case 3:
                    erd();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
            }
        }
    }

    private static void writeQueries() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your SQL query: ");
        StringBuilder queryBuilder = new StringBuilder();
        String line = scanner.nextLine();
        queryBuilder.append(line).append(" ");
        String query = queryBuilder.toString().trim();

        if (!query.endsWith(";")) {
            System.out.println("Invalid query format. Query must end with a semicolon (;).");
            return;
        }

        query = query.substring(0, query.length() - 1).trim(); // Remove the semicolon for processing

        if (query.toLowerCase().startsWith("create database")) {
            CreateDatabase.create(query);
        } else if (query.toLowerCase().startsWith("use")) {
            UseDatabase.use(query);
        } else if (query.toLowerCase().startsWith("create table")) {
            CreateTable.create(query);
        }else if (query.toLowerCase().startsWith("insert into")) {
            InsertIntoTable.insert(query);
        } else {
            System.out.println("Invalid query. Please enter a valid SQL query.");
        }
    }

    private static void exportDataAndStructure() {
        System.out.println("Exporting data and structure...");
        // Implement the logic for exporting data and structure here
    }

    private static void erd() {
        System.out.println("Generating ERD...");
        // Implement the logic for generating ERD here
    }
}
