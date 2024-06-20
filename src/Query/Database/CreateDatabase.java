package Query.Database;

import java.io.File;

import static Query.Database.DatabaseQueryValidator.validateCreateDatabaseQuery;

public class CreateDatabase {
    public static void create(String query) {
        String[] parts = query.split(" ");

        if (validateCreateDatabaseQuery(parts)) {
            String databaseName = parts[2];
            File databaseDirectory = new File("./databases/" + databaseName);

            if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {
                System.out.println("Database " + databaseName + " already exists.");
            } else {
                if (databaseDirectory.mkdirs()) {
                    System.out.println("Database " + databaseName + " created successfully.");
                } else {
                    System.out.println("Failed to create database " + databaseName + ".");
                }
            }
        } else {
            System.out.println("Invalid CREATE DATABASE query.");
        }
    }
}
