import Security.login.UserLoginImpl;
import Security.register.UserRegistrationImpl;

import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.Scanner;

import static Utills.ColorConstraint.ANSI_RED;
import static Utills.ColorConstraint.ANSI_RESET;
import static Utills.QueryProcessor.writeQueries;

public class TinyDB {
    public static void main(String[] args) {
        UserLoginImpl userLogin = new UserLoginImpl();
        UserRegistrationImpl userRegistration = new UserRegistrationImpl();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("Choose an action: ");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        if (userLogin.userLogin()) {
                            displayUserOptions();
                            return;
                        }
                        break;
                    case 2:
                        userRegistration.userRegistration();
                        break;
                    default:
                        System.out.println(ANSI_RED + "Invalid choice. Please enter 1 or 2." + ANSI_RESET);
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
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
                    displayUserOptions();
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
        System.out.println("Exporting data and structure...");
        // Implement the logic for exporting data and structure here
    }

    private static void erd() {
        System.out.println("Generating ERD...");
        // Implement the logic for generating ERD here
    }
}
