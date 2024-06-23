package Query.Database;

import java.io.File;

import static Query.Database.DatabaseQueryValidator.validateCreateDatabaseQuery;
import static Utills.ColorConstraint.*;

public class CreateDatabase {
    public static void create(String query) {
        String[] parts = query.split(" ");

        if (validateCreateDatabaseQuery(parts)) {
            String databaseName = parts[2];
            File databaseDirectory = new File("./databases/" + databaseName);

            if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {
                System.out.println(ANSI_RED + "Database " + databaseName + " already exists." + ANSI_RESET);
            } else {
                if (databaseDirectory.mkdirs()) {
                    System.out.println(ANSI_GREEN + "Database " + databaseName + " created successfully." + ANSI_RESET);
                } else {
                    System.out.println(ANSI_RED + "Failed to create database " + databaseName + "." + ANSI_RESET);
                }
            }
        } else {
            System.out.println(ANSI_RED + "Invalid CREATE DATABASE query." + ANSI_RESET);
        }
    }
}
