package security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65_536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Wachtwoord is verplicht.");
        }

        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH, ALGORITHM);

        return "%s$%d$%s$%s".formatted(
                ALGORITHM,
                ITERATIONS,
                Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(hash)
        );
    }

    public static boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || rawPassword.isBlank() || storedHash == null || storedHash.isBlank()) {
            return false;
        }

        String[] parts = storedHash.split("\\$");
        if (parts.length != 4) {
            return false;
        }

        String algorithm = parts[0];
        int iterations = Integer.parseInt(parts[1]);
        byte[] salt = Base64.getDecoder().decode(parts[2]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
        byte[] actualHash = pbkdf2(rawPassword.toCharArray(), salt, iterations, expectedHash.length * 8, algorithm);

        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength, String algorithm) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new IllegalStateException("Kon wachtwoordhash niet genereren.", ex);
        }
    }
}
