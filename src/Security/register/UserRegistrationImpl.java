package Security.register;

import Security.login.UserLoginImpl;
import Utils.Hashing;

import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import static Utils.ColorConstraint.*;

public class UserRegistrationImpl implements UserRegister {
    private static String userID;
    private String password;
    private static String securityQuestion1;
    private static String securityQuestion2;
    private static String securityAnswer1;
    private static String securityAnswer2;
    private static final UserLoginImpl userLogin = new UserLoginImpl();
    private static final Hashing hashing = new Hashing();

    public void setUserID(String userID) {
        UserRegistrationImpl.userID = userID;
    }

    public String hashUserID(String userID) throws NoSuchAlgorithmException {
        return hashing.hashData(userID);
    }

    public String hashPassword(String password) throws NoSuchAlgorithmException {
        return hashing.hashData(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSecurityQuestion1(String securityQuestion1) {
        UserRegistrationImpl.securityQuestion1 = securityQuestion1;
    }

    public void setSecurityAnswer1(String securityAnswer1) {
        UserRegistrationImpl.securityAnswer1 = securityAnswer1;
    }

    public void setSecurityQuestion2(String securityQuestion2) {
        UserRegistrationImpl.securityQuestion2 = securityQuestion2;
    }

    public void setSecurityAnswer2(String securityAnswer2) {
        UserRegistrationImpl.securityAnswer2 = securityAnswer2;
    }

    /**
     * Handles the user registration process.
     *
     * @throws NoSuchAlgorithmException if hashing algorithm is not found.
     */
    public void userRegistration() throws NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);

        boolean isUserIDValid = false;
        String userID = "";

        while (!isUserIDValid) {
            System.out.println("Enter your userID: ");
            userID = scanner.nextLine();
            if (userLogin.isUserIDExists(hashUserID(userID))) {
                System.out.println(ANSI_RED + "UserID already exists. Please choose another userID." + ANSI_RESET);
            } else {
                isUserIDValid = true;
            }
        }
        setUserID(hashUserID(userID));

        System.out.println("Enter the password: ");
        String password = scanner.nextLine();
        setPassword(hashPassword(password));

        System.out.println("Enter the first security question: ");
        String securityQuestion1 = scanner.nextLine();
        setSecurityQuestion1(securityQuestion1);

        System.out.println("Enter the answer to that first security question: ");
        String securityAnswer1 = scanner.nextLine();
        setSecurityAnswer1(securityAnswer1);

        System.out.println("Enter the second security question: ");
        String securityQuestion2 = scanner.nextLine();
        setSecurityQuestion2(securityQuestion2);

        System.out.println("Enter the answer to that second security question: ");
        String securityAnswer2 = scanner.nextLine();
        setSecurityAnswer2(securityAnswer2);

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter("./userdata/User_Profile.txt", true);

            fileWriter.write(String.format("%s-%s-%s-%s-%s-%s;\n", hashUserID(userID), hashPassword(password), securityQuestion1, securityAnswer1, securityQuestion2, securityAnswer2));
            fileWriter.close();
            System.out.println(ANSI_GREEN + "User is registered successfully !!" + ANSI_RESET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}