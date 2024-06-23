package Security.login;

import java.security.NoSuchAlgorithmException;

public interface UserLogin {
    boolean userLogin() throws NoSuchAlgorithmException;

    boolean isUserIDExists(String hashedUserID);

    boolean authenticateUser(String hashedPassword);
}
