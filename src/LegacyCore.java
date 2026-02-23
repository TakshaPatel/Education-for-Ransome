/*import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
//import javax.crypto.NoSuchPaddingException;
//import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.lang.Exception;
import java.util.Scanner;*/

public class LegacyCore {
  /*public static void main(String[] args) throws Exception {
    System.out.println("[*] Running Program\n    Usage is for ethical purposes only;\n");

    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(256);
    SecretKey secretKey = keyGen.generateKey(); //example: javax.crypto.spec.SecretKeySpec@fffe98cf

    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

    System.out.println("[+] Secret Key: " + secretKey);
    String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
    System.out.println("[+] Secret Key (Base64): " + encodedKey);
    System.out.println("[*] Starting Encryption");

    byte [] encryptedBytes = cipher.doFinal("wassup".getBytes());
    String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
    System.out.println("[+] Encrypted Text: " + encryptedText);

    System.out.println("[+] Decrypting, Enter Secret key (Base64): ");
    Scanner scanner = new Scanner(System.in);
    String checkEncodedKey = scanner.nextLine();
    scanner.close();

    if (encodedKey.equals(checkEncodedKey)) {
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
      String plainTextDecoded = new String(decryptedBytes);
      System.out.println("[+] Decrypted bytes: " + plainTextDecoded);
    } else {
      System.out.println("Whoops :(");
    }
  }
    */
}