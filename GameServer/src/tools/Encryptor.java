package tools;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Encryptor {
    
    public static String encrypt(String password) throws NoSuchAlgorithmException {
        // Generar una salt aleatoria
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Calcular el hash de la contraseña y la salt
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        // Concatenar la salt y el hash de la contraseña
        byte[] combined = new byte[hashedPassword.length + salt.length];
        System.arraycopy(hashedPassword, 0, combined, 0, hashedPassword.length);
        System.arraycopy(salt, 0, combined, hashedPassword.length, salt.length);

        // Codificar el resultado en Base64
        return Base64.getEncoder().encodeToString(combined);
    }
}
