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
        System.out.println("Writing queries...");
        // Implement the logic for writing queries here
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
