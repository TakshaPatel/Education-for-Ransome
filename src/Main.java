import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
      safteyChecks();

      SecretKey secretKey = AESCore.generateKey();
      String encodedKey = AESCore.toBase64(secretKey);
      System.out.println("[+] Secret Key (Base64): " + encodedKey);

      Set<String> filesToEncrypt = listFiles();
      System.out.println(filesToEncrypt);

      for (String file : filesToEncrypt) {
        Path path = Paths.get("./sandbox", file);
        try {
          String fileContent = Files.readString(path);
          String encryptedFile = AESCore.encrypt(fileContent, secretKey);
          Files.writeString(path, encryptedFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
          System.out.println("FILE: " + path + "\n CONTENT: " + encryptedFile);
        } catch (Exception e) {
          System.err.println("[!] Encrypt error on " + file + ": ");
          e.printStackTrace();
        }
      }

      System.out.println("[*] Decrypting, Enter Secret Key: ");
      String userEncodedKey = scanner.nextLine();
      scanner.close();

      
      if (userEncodedKey != null && !userEncodedKey.isBlank()) {
        try {
          // Reconstruct SecretKey from the pasted base64 string
          SecretKey userKey = AESCore.keyFromBase64(userEncodedKey.trim());

          for (String file : filesToEncrypt) {
            Path path = Paths.get("./sandbox", file);
            try {
              String fileContent = Files.readString(path);
              String decryptedFile = AESCore.decrypt(fileContent, secretKey);
              Files.writeString(path, decryptedFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
              System.out.println("FILE: " + path + "\n CONTENT: " + decryptedFile);
            } catch (Exception e) {
              System.err.println("[!] Decrypt error on " + file + ": ");
              e.printStackTrace();
            }
          }
        } catch (Exception e) {
          System.err.println("[!] Invalid key format or key reconstruction failed: ");
          e.printStackTrace();
        }
      } else {
        System.out.println("No key provided. Aborting decrypt.");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Set<String> listFiles() {
    File[] files = new File("./sandbox").listFiles();
    if (files == null) return java.util.Collections.emptySet();
    return Stream.of(files)
      .filter(f -> f != null && f.isFile())
      .map(File::getName)
      .collect(Collectors.toSet());
  }

  public static void safteyChecks() throws Exception {
    Path PATH = Paths.get("./sandbox/");
    boolean exists = Files.exists(PATH);
    boolean isDirectory = Files.isDirectory(PATH);
    if (!exists || !isDirectory) {
      Files.createDirectory(PATH);
      System.out.println("[!] SANDBOX WAS NOT A DIR, SANDBOX IS REQUIRED TO RUN THE PROGRAM\n Made ./sandbox/ a dir. Exiting now");
      System.exit(1);
    } else {
      System.out.println("Saftey Check Executed...");
    }
  }
}
