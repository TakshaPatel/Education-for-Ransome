import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

class AESCore {
  private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }

    public static SecretKey keyFromBase64(String base64Key) {
        byte[] decoded = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decoded, ALGORITHM);
    }

    public static String encrypt(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String base64Ciphertext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(base64Ciphertext);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, "UTF-8");
    }

    public static String encrypt(String plaintext, String base64Key) throws Exception {
        return encrypt(plaintext, keyFromBase64(base64Key));
    }

    public static String decrypt(String base64Ciphertext, String base64Key) throws Exception {
        return decrypt(base64Ciphertext, keyFromBase64(base64Key));
    }

    public static String toBase64(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}