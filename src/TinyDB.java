import Security.login.UserLoginImpl;
import Security.register.UserRegistrationImpl;
import Utils.MenuUtils;

import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.Scanner;

import static Utils.ColorConstraint.*;

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

                while (!scanner.hasNextInt()) {
                    System.out.println(ANSI_RED + "Invalid input. Please enter a number." + ANSI_RESET);
                    scanner.nextLine();
                    System.out.print("Enter your choice: ");
                }

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        if (userLogin.userLogin()) {
                            MenuUtils.displayUserOptions();
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
            } catch (InputMismatchException e) {
                System.out.println(ANSI_RED + "Invalid input. Please enter a number." + ANSI_RESET);
                scanner.nextLine();
            }
        }
    }
}