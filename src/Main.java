import java.util.Scanner;
import javax.crypto.SecretKey;

public class Main {
  public static void main(String[] args) {
    try {
      Scanner scanner = new Scanner(System.in);

      System.out.println("[*] Program running, Use ethically");

      SecretKey secretKey = AESCore.generateKey();
      String encodedKey = AESCore.toBase64(secretKey);
      System.out.println("[+] Secret Key (Base64): " + encodedKey);

      String plaintext = "Here goes a secret Message"; //TODO, replace this with files in a dir, encrypt them 1 by one, write them

      System.out.println("[*] Starting Encryption");
      String ciphertext = AESCore.encrypt(plaintext, secretKey);
      System.out.println("[+] Encrypted: " + ciphertext);

      System.out.println("[*] Decrypting, Enter Secret Key: ");
      String UserEncodedKey = scanner.nextLine();
      scanner.close();

      /*if(encodedKey.equals(UserEncodedKey)) {
        String decrypted = AESCore.decrypt(ciphertext, secretKey);
        System.out.println("[+] Decrypted: " + decrypted);
      } else {
        System.out.println("Whoops");
      }*/

      if(UserEncodedKey != null && UserEncodedKey.equals("AAABBBCCC")) {
        String decrypted = AESCore.decrypt(ciphertext, secretKey);
        System.out.println("[+] Decrypted: " + decrypted);
      } else {
        System.out.println("Whops");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
