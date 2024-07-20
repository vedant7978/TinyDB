package Utils;

import java.util.InputMismatchException;
import java.util.Scanner;

import static ExportData.ExportData.databaseExists;
import static ExportData.ExportData.exportDatabase;
import static ReverseEngineering.GenerateErd.generateERD;
import static Utils.ColorConstraint.*;
import static Utils.QueryProcessor.writeQueries;

public class MenuUtils {

    public static void displayUserOptions() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Choose an action: ");
            System.out.println("1. Write Queries");
            System.out.println("2. Export Data and Structure");
            System.out.println("3. ERD");
            System.out.println("4. Exit");

            int choice = 0;
            boolean validInput = false;

            while (!validInput) {
                try {
                    System.out.print("Enter your choice: ");
                    choice = scanner.nextInt();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println(ANSI_RED + "Invalid input. Please enter a number between 1 and 4." + ANSI_RESET);
                    scanner.next();
                }
            }

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
                    System.out.println(ANSI_RED + "Invalid choice. Please enter 1, 2, 3, or 4." + ANSI_RESET);
            }
        }
    }

    private static void exportDataAndStructure() {
        Scanner scanner = new Scanner(System.in);
        String databaseName;

        while (true) {
            System.out.print("Enter the database name to export: ");
            databaseName = scanner.nextLine();
            if (databaseExists(databaseName)) {
                System.out.println("Exporting data and structure...");
                exportDatabase(databaseName);
                break;
            } else {
                System.out.println(ANSI_RED + "Database " + databaseName + " does not exist. Please try again." + ANSI_RESET);
            }
        }
    }

    private static void erd() {
        Scanner scanner = new Scanner(System.in);
        String databaseName;

        while (true) {
            System.out.print("Enter the database name to generate ERD: ");
            databaseName = scanner.nextLine();
            if (databaseExists(databaseName)) {
                System.out.println(ANSI_GREEN + "Generating the ERD....." + ANSI_RESET);
                generateERD(databaseName);
                break;
            } else {
                System.out.println(ANSI_RED + "Database " + databaseName + " does not exist. Please try again." + ANSI_RESET);
            }
        }
    }
}