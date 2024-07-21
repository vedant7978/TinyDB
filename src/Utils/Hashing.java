package Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for performing hashing operations.
 */
public class Hashing {

    /**
     * Generates a SHA-256 hash of the given input string.
     *
     * @param data the input string to hash
     * @return the hexadecimal representation of the hash
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is not available
     */
    public String hashData(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] byteData = md.digest(data.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : byteData) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
