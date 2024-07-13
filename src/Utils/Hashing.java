package Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {

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
