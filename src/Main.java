import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.SecretKey;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(EduWindow::new);
    }

    private static final Set<String> IGNORED_FILES = Set.of(
        ".DS_Store", "Thumbs.db", "desktop.ini", ".localized"
    );

    public static Set<String> listFiles() {
        File[] files = new File("./sandbox").listFiles();
        if (files == null) return Collections.emptySet();
        return Stream.of(files)
            .filter(f -> f != null && f.isFile())
            .map(File::getName)
            .filter(name -> !name.startsWith("."))
            .filter(name -> !IGNORED_FILES.contains(name))
            .collect(Collectors.toSet());
    }

    public static void safteyChecks() throws Exception {
        Path PATH = Paths.get("./sandbox/");
        boolean exists      = Files.exists(PATH);
        boolean isDirectory = Files.isDirectory(PATH);
        if (!exists || !isDirectory) {
            Files.createDirectory(PATH);
            System.out.println("[!] ./sandbox/ was missing — created it. Add files and restart.");
            System.exit(1);
        } else {
            System.out.println("[*] Safety check passed.");
        }
    }

    public static void decryptionLoop(Set<String> filesToDecrypt, SecretKey secretKey) {
        int ok = 0;
        int failed = 0;

        for (String file : filesToDecrypt) {
            Path path = Paths.get("./sandbox", file);
            try {
                if (SlideContent.isBinary(file)) {
                    byte[] rawText   = Files.readAllBytes(path);
                    String b64       = new String(rawText).replaceAll("\\s", "");
                    byte[] encrypted = Base64.getDecoder().decode(b64);
                    byte[] original  = AESCore.decryptBytes(encrypted, secretKey);
                    Files.write(path, original, StandardOpenOption.TRUNCATE_EXISTING);
                } else {
                    String enc = Files.readString(path);
                    String dec = AESCore.decrypt(enc.trim(), secretKey);
                    Files.writeString(path, dec, StandardOpenOption.TRUNCATE_EXISTING);
                }
                System.out.println("[+] Decrypted: " + path);
                ok++;
            } catch (Exception e) {
                System.err.println("[!] Failed to decrypt: " + file + " — " + e.getMessage());
                e.printStackTrace();
                failed++;
            }
        }

        System.out.println("[*] Done. " + ok + " restored, " + failed + " failed.");
        System.exit(0);
    }
}
