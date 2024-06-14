package Security.login;

import Utills.Hashing;
import Utills.Logs;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

public class UserLoginImpl implements UserLogin {

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static Hashing hashing = new Hashing();


    @Override
    public boolean userLogin() throws NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        boolean isUserIDValid = false;
        String userID = "";

        while (!isUserIDValid) {
            System.out.println("Enter your userID");
            userID = scanner.nextLine();
            String hashedUserID = hashing.hashData(userID);
            if (isUserIDExists(hashedUserID)) {
                isUserIDValid = true;
            } else {
                System.out.println(ANSI_RED + "User is not valid" + ANSI_RESET);
            }
        }

        boolean isAuthenticated = false;
        while (!isAuthenticated) {
            System.out.println("Enter password");
            String password = scanner.nextLine();
            String hashedPassword = hashing.hashData(password);
            isAuthenticated = authenticateUser(hashedPassword);
            if (!isAuthenticated) {
                System.out.println(ANSI_RED + "Incorrect Password" + ANSI_RESET);
            } else {
                System.out.println("Logged In");
                System.out.println("Hello " + userID);
                return true;
            }
        }
        return false;
    }


    public boolean isUserIDExists(String hashedUserID) {
        File userCredentials = new File("csci_5408_s24_group06/src/User_Profile.txt");
        try {
            Scanner fileReader = new Scanner(userCredentials);
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                String[] values = data.split(";");
                for (String value : values) {
                    String[] userInfo = value.split("-");
                    if (userInfo.length == 6 && hashedUserID.equals(userInfo[0])) {
                        return true;
                    }
                }
            }
            return false;
        } catch (FileNotFoundException e) {
            System.out.println(ANSI_RED + "User credentials file not found" + ANSI_RESET);
            throw new RuntimeException(e);
        }
    }

    private boolean authenticateUser(String hashedPassword) throws NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        File userCredentials = new File("csci_5408_s24_group06/src/User_Profile.txt");
        try {
            Scanner fileReader = new Scanner(userCredentials);
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                String[] values = data.split(";");
                for (String value : values) {
                    String[] userInfo = value.split("-");
                    if (userInfo.length == 6 && hashedPassword.equals(userInfo[1])) {
                        Random random = new Random();
                        int questionIndex = random.nextInt(2);
                        String selectedQuestion = questionIndex == 0 ? userInfo[2] : userInfo[4];
                        String correctAnswer = questionIndex == 0 ? userInfo[3] : userInfo[5];

                        System.out.println("Answer the security question: " + selectedQuestion);
                        String securityAnswer = scanner.nextLine();

                        if (securityAnswer.equals(correctAnswer)) {
                            return true;
                        } else {
                            System.out.println(ANSI_RED + "Incorrect security answer" + ANSI_RESET);
                            return false;
                        }
                    }
                }
            }
            return false;
        } catch (FileNotFoundException e) {
            System.out.println(ANSI_RED + "User credentials file not found" + ANSI_RESET);
            throw new RuntimeException(e);
        }
    }
}
