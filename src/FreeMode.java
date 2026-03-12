import java.nio.file.*;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.swing.*;

public class FreeMode {

    private static final String SANDBOX_DIR  = "./sandbox/";
    private static final String FAKE_SITE    = "www.fakewebsite-simulation.com/dont-pay";
    private static final String OVERRIDE_KEY = "qwe123";
    private static final int    MAX_ATTEMPTS = 3;

    private static final byte[] PNG_MAGIC = { (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };

    public static void launch(JFrame eduFrame) {
        try {
            Main.safteyChecks();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(eduFrame,
                "Safety check failed:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        eduFrame.setVisible(false);
        SwingUtilities.invokeLater(() -> {
            try { run(); } catch (Exception ex) { ex.printStackTrace(); }
        });
    }

    private static void run() throws Exception {
        Window w = new Window();

        w.print("[*] FREE MODE — Sandbox Ransomware Simulation");
        w.print("[*] All activity is sandboxed to " + SANDBOX_DIR);
        w.print("─────────────────────────────────────────────");
        w.print("IT LOOKS LIKE YOUR FILES ARE ENCRYPTED\n");

        SecretKey key        = AESCore.generateKey();
        String    encodedKey = AESCore.toBase64(key);

        w.print("Your victim identifier key:");
        w.print(encodedKey);
        w.print("");
        w.print("Send payment to: " + FAKE_SITE);
        w.print("[ THIS IS A SIMULATION — DO NOT SEND REAL MONEY ]");
        w.print("─────────────────────────────────────────────");

        Set<String> files = Main.listFiles();

        if (files.isEmpty()) {
            w.print("[!] No files found in " + SANDBOX_DIR);
            w.print("[!] Add files to ./sandbox/ and restart.");
            return;
        }

        w.print("[*] Encrypting " + files.size() + " file(s)...\n");

        for (String f : files) {
            Path p = Paths.get(SANDBOX_DIR, f);
            try {
                long fileSize = Files.size(p);
                if (fileSize == 0) {
                    w.print("[!] Skipped:   " + f + " — file is empty");
                    continue;
                }

                if (SlideContent.isBinary(f)) {
                    byte[] raw = Files.readAllBytes(p);

                    if (!hasValidBinaryHeader(f, raw)) {
                        w.print("[!] Skipped:   " + f + " — file header invalid (corrupted or already encrypted)");
                        w.print("    Tip: restore the original file and try again.");
                        continue;
                    }

                    byte[] enc = AESCore.encryptBytes(raw, key);
                    String b64 = java.util.Base64.getEncoder().encodeToString(enc);
                    Files.writeString(p, b64, StandardOpenOption.TRUNCATE_EXISTING);

                } else {
                    String content = Files.readString(p);

                    if (looksLikeCiphertext(content)) {
                        w.print("[!] Skipped:   " + f + " — appears already encrypted (restore first)");
                        continue;
                    }

                    String encrypted = AESCore.encrypt(content, key);
                    Files.writeString(p, encrypted, StandardOpenOption.TRUNCATE_EXISTING);
                }

                w.print("[+] Encrypted: " + f);

            } catch (Exception ex) {
                w.print("[!] Failed:    " + f + " — " + ex.getMessage());
            }
        }

        w.print("\n[*] Encryption complete.");
        w.print("[*] Enter the override key to decrypt (simulation only):");

        final int[] attempts = { 0 };
        w.onInput(event -> {
            attempts[0]++;
            String input = w.getInput().trim();
            if (input.equals(OVERRIDE_KEY)) {
                w.print("\n[+] Correct key — restoring files...");
                Main.decryptionLoop(files, key);
            } else if (attempts[0] < MAX_ATTEMPTS) {
                w.print("[!] Wrong key. " + (MAX_ATTEMPTS - attempts[0]) + " attempt(s) remaining.");
            } else {
                w.print("\n[-] Out of attempts. Files remain encrypted.");
                w.print("[-] Re-run the simulation to start over.");
                System.exit(1);
            }
        });
    }

    private static boolean hasValidBinaryHeader(String filename, byte[] data) {
        if (data.length < 4) return false;
        String low = filename.toLowerCase();

        if (low.endsWith(".png")) {
            if (data.length < 8) return false;
            for (int i = 0; i < PNG_MAGIC.length; i++) {
                if (data[i] != PNG_MAGIC[i]) return false;
            }
            return true;
        }
        if (low.endsWith(".jpg") || low.endsWith(".jpeg")) {
            return (data[0] & 0xFF) == 0xFF &&
                   (data[1] & 0xFF) == 0xD8 &&
                   (data[2] & 0xFF) == 0xFF;
        }
        if (low.endsWith(".gif")) {
            return data[0] == 'G' && data[1] == 'I' && data[2] == 'F' && data[3] == '8';
        }
        if (low.endsWith(".pdf")) {
            return data[0] == '%' && data[1] == 'P' && data[2] == 'D' && data[3] == 'F';
        }
        if (low.endsWith(".zip") || low.endsWith(".jar")) {
            return data[0] == 'P' && data[1] == 'K';
        }
        return true;
    }

    private static boolean looksLikeCiphertext(String content) {
        if (content.length() < 20) return false;
        String sample = content.substring(0, Math.min(64, content.length())).trim();
        long nonBase64 = sample.chars()
            .filter(c -> !Character.isLetterOrDigit(c) && c != '+' && c != '/' && c != '=')
            .count();
        return nonBase64 < 3;
    }
}
