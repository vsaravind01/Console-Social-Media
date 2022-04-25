package social.media;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// An interface to Hash a give password with SHA-256 Algorithm
interface passwordMask {

    // Method to return the SHA-256 hashed equivalent of the given password
    // NOTE : The username is used as a salt here
    default String get_SHA_256_SecurePassword(String passwordToHash, String username) {
        byte[] salt = getSalt(username);
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    // Method that converts and returns the given username String as array of bytes
    private byte[] getSalt(String username) {
        return username.getBytes();
    }
}
