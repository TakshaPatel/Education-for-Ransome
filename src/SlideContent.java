import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import javax.crypto.SecretKey;

public class SlideContent {
    public static String baseStyle() {
        return "<style>" +
               "body{font-family:SansSerif;font-size:13px;color:#e6e6f0;" +
                    "background:#10101610;margin:0;padding:0;}" +
               "h3{color:#ff3b3b;font-family:Monospaced;margin-top:14px;margin-bottom:6px;}" +
               "h4{color:#00dcdc;font-family:Monospaced;margin-top:10px;margin-bottom:4px;}" +
               "p{margin:6px 0;line-height:1.6;}" +
               "ul{margin:6px 0 6px 18px;padding:0;}" +
               "li{margin:4px 0;}" +
               "code{background:#0a0a0e;color:#39ff14;font-family:Monospaced;padding:1px 4px;}" +
               ".warn{color:#ff8c00;font-weight:bold;}" +
               ".good{color:#39ff14;}" +
               "</style>";
    }


    public static String deliveryHTML() {
        return "<html>" + baseStyle() + "<body>" +
               "<h3>How Does Ransomware Arrive?</h3>" +
               "<p>Most ransomware infections start with <b>social engineering</b> — " +
               "tricking a human rather than exploiting a technical flaw.</p>" +
               "<h4>Primary Vectors</h4>" +
               "<ul>" +
               "<li><b>Phishing email</b> — malicious attachment (.docx macro, .pdf exploit, .exe) " +
               "or link to a drive-by download site.</li>" +
               "<li><b>RDP brute-force</b> — exposed Remote Desktop ports with weak passwords " +
               "are scanned and attacked automatically.</li>" +
               "<li><b>Supply-chain compromise</b> — malware embedded in a legitimate software " +
               "update (e.g., SolarWinds, 3CX).</li>" +
               "<li><b>Malvertising</b> — malicious ads that auto-execute code on unpatched browsers.</li>" +
               "<li><b>USB / removable media</b> — physical drop attacks in parking lots or offices.</li>" +
               "</ul>" +
               "<h4>After Initial Access</h4>" +
               "<p>The payload often runs a <b>dropper</b> — a small stub that downloads the main " +
               "ransomware binary from a C2 (command-and-control) server. This keeps the initial " +
               "file small and evades signature scanners.</p>" +
               "<p class='warn'>⚠ Over 80% of ransomware incidents begin with a phishing email. " +
               "End-user training is one of the highest-ROI defenses.</p>" +
               "<h4>Defenses at This Stage</h4>" +
               "<ul>" +
               "<li class='good'>Email filtering with sandbox detonation</li>" +
               "<li class='good'>Disable macros by default in Office apps</li>" +
               "<li class='good'>MFA on all remote access (RDP, VPN)</li>" +
               "<li class='good'>Patch management — especially public-facing services</li>" +
               "</ul>" +
               "</body></html>";
    }


    public static String discoveryHTML() {
        return "<html>" + baseStyle() + "<body>" +
               "<h3>File Discovery &amp; Targeting</h3>" +
               "<p>Before encrypting anything, ransomware performs <b>reconnaissance on the " +
               "local file system</b> and often the network.</p>" +
               "<h4>What It Targets</h4>" +
               "<ul>" +
               "<li>Documents: <code>.docx .xlsx .pdf .txt</code></li>" +
               "<li>Databases: <code>.sql .mdb .accdb .sqlite</code></li>" +
               "<li>Images: <code>.jpg .png .bmp .gif</code></li>" +
               "<li>Source code: <code>.java .py .js .c .cpp</code></li>" +
               "<li>Backups: <code>.bak .tar .zip .7z</code></li>" +
               "</ul>" +
               "<h4>What It Skips</h4>" +
               "<p>Ransomware deliberately avoids OS files (<code>C:\\Windows\\</code>, " +
               "<code>/bin/</code>) — it needs the machine to stay bootable so the victim can pay.</p>" +
               "<h4>Network Propagation</h4>" +
               "<p>Advanced variants scan for <b>SMB shares, NAS devices, and mapped drives</b>. " +
               "Some use lateral movement techniques (pass-the-hash, WMI) to infect other machines " +
               "before triggering encryption — maximising impact.</p>" +
               "<p class='warn'>⚠ The simulation here only touches <code>./sandbox/</code>. " +
               "Real ransomware can enumerate hundreds of thousands of files in seconds.</p>" +
               "<h4>Defenses at This Stage</h4>" +
               "<ul>" +
               "<li class='good'>Principle of least privilege — users shouldn't have write access everywhere</li>" +
               "<li class='good'>Network segmentation — limit lateral movement</li>" +
               "<li class='good'>Honeypot files — detect ransomware the moment it touches a canary file</li>" +
               "</ul>" +
               "</body></html>";
    }


    public static String encryptHTML() {
        return "<html>" + baseStyle() + "<body>" +
               "<h3>AES-256 Encryption</h3>" +
               "<p>This simulator uses <b>AES-256-ECB</b> — the same cipher (though a weaker mode) " +
               "as many real ransomware families.</p>" +
               "<h4>Why AES?</h4>" +
               "<ul>" +
               "<li>Extremely fast — can encrypt gigabytes per second on modern hardware.</li>" +
               "<li>Unbreakable with correct key management — no known practical attack against AES-256.</li>" +
               "<li>Available in every language/platform via standard libraries.</li>" +
               "</ul>" +
               "<h4>Binary Files (e.g. PNG)</h4>" +
               "<p>Images and other binary files are read as raw bytes, encrypted, and written back " +
               "as Base64-encoded ciphertext. The file extension is preserved so the OS still " +
               "recognises it — but the content is unreadable until decrypted.</p>" +
               "<h4>Key Management is Everything</h4>" +
               "<p>Real ransomware generates a <b>per-victim AES key</b>, then encrypts <i>that</i> " +
               "key with the attacker's RSA public key. The attacker holds the RSA private key on " +
               "their server. This means:</p>" +
               "<ul>" +
               "<li>Without the attacker's RSA key, decryption is impossible.</li>" +
               "<li>Even capturing the ransomware binary doesn't help — the RSA private key never " +
               "touches the victim's machine.</li>" +
               "</ul>" +
               "<p class='warn'>⚠ ECB mode (used here for simplicity) is not semantically secure — " +
               "identical plaintext blocks produce identical ciphertext blocks. Real ransomware uses " +
               "<b>CBC or CTR mode</b> with a random IV.</p>" +
               "<h4>Defenses at This Stage</h4>" +
               "<ul>" +
               "<li class='good'>Offline backups (3-2-1 rule) — the ultimate defense</li>" +
               "<li class='good'>Tamper-evident backup storage (write-once media / immutable cloud snapshots)</li>" +
               "<li class='good'>EDR solutions with behavior-based detection (mass file renaming/modification)</li>" +
               "</ul>" +
               "</body></html>";
    }


    public static String paymentHTML() {
        return "<html>" + baseStyle() + "<body>" +
               "<h3>The Ransom Demand</h3>" +
               "<p>After encryption, ransomware displays a <b>ransom note</b> — sometimes a full GUI, " +
               "sometimes a .txt file, sometimes a desktop wallpaper change.</p>" +
               "<h4>Psychology of the Demand</h4>" +
               "<ul>" +
               "<li><b>Countdown timer</b> — artificial urgency. Price often doubles after the deadline.</li>" +
               "<li><b>Proof-of-decryption</b> — attacker offers to decrypt 1-2 files free to prove " +
               "the key works.</li>" +
               "<li><b>\"Support\" chat</b> — professional-looking dark web portal to negotiate and " +
               "submit payment.</li>" +
               "<li><b>Double extortion</b> — \"pay or we publish your data\" (Maze, LockBit, Cl0p).</li>" +
               "</ul>" +
               "<h4>Why Bitcoin?</h4>" +
               "<p>Cryptocurrencies provide <b>pseudonymous, irreversible transactions</b> across " +
               "jurisdictions. Attackers further launder funds through mixers and chain-hopping to " +
               "obscure trails.</p>" +
               "<h4>Should You Pay?</h4>" +
               "<p class='warn'>⚠ Law enforcement and security researchers advise <b>against paying</b>:</p>" +
               "<ul>" +
               "<li>~40% of victims who pay do <i>not</i> receive a working decryptor.</li>" +
               "<li>Payment funds future attacks and signals you are a viable target.</li>" +
               "<li>Paying may violate OFAC sanctions if the group is on a sanctions list.</li>" +
               "</ul>" +
               "<h4>Response Playbook</h4>" +
               "<ul>" +
               "<li class='good'>Isolate infected systems immediately</li>" +
               "<li class='good'>Preserve memory and disk images for forensics</li>" +
               "<li class='good'>Check nomoreransom.org — free decryptors exist for many strains</li>" +
               "<li class='good'>Notify legal, HR, regulators (GDPR 72-hour window)</li>" +
               "<li class='good'>Restore from last known-clean backup</li>" +
               "</ul>" +
               "</body></html>";
    }

    public static String[] discoveryLogs(Set<String> files) {
        List<String> logs = new ArrayList<>();
        logs.add("[*] Initializing file system traversal...");
        logs.add("[*] Target directory: ./sandbox/");
        logs.add("[*] Scanning for writable files...");
        logs.add("");
        if (files.isEmpty()) {
            logs.add("[!] No files found in sandbox.");
            logs.add("[*] Add files to ./sandbox/ to see discovery.");
        } else {
            for (String f : files) {
                logs.add("[+] FOUND: " + f);
                logs.add("    Path  : ./sandbox/" + f);
                try {
                    long sz = Files.size(Paths.get("./sandbox", f));
                    logs.add("    Size  : " + sz + " bytes");
                } catch (IOException ignored) {}
                logs.add("    Type  : " + fileType(f));
                logs.add("    Status: QUEUED FOR ENCRYPTION");
                logs.add("");
            }
            logs.add("[*] Discovery complete.");
            logs.add("[*] Files queued: " + files.size());
        }
        logs.add("[*] Proceeding to encryption stage...");
        return logs.toArray(new String[0]);
    }

    public static String[] encryptLogs(Set<String> files, SecretKey[] sharedKeyHolder) {
        List<String> logs = new ArrayList<>();
        logs.add("[*] Loading AES-256-ECB cipher...");
        logs.add("[*] Generating ephemeral 256-bit key...");
        try {
            SecretKey key = AESCore.generateKey();
            sharedKeyHolder[0] = key;
            logs.add("[+] Key generated (first 8 chars): " +
                     AESCore.toBase64(key).substring(0, 8) + "...");
        } catch (Exception ex) {
            logs.add("[!] Key generation failed: " + ex.getMessage());
            return logs.toArray(new String[0]);
        }
        logs.add("");

        if (files.isEmpty()) {
            logs.add("[!] No files found. Add files to ./sandbox/ first.");
            return logs.toArray(new String[0]);
        }

        logs.add("[*] Walking file list...");
        for (String f : files) {
            Path path = Paths.get("./sandbox", f);
            logs.add("");
            logs.add("[>] Processing: " + f + "  (" + fileType(f) + ")");
            try {
                if (isBinary(f)) {
                    byte[] raw       = Files.readAllBytes(path);
                    byte[] encrypted = AESCore.encryptBytes(raw, sharedKeyHolder[0]);
                    String b64       = java.util.Base64.getEncoder().encodeToString(encrypted);
                    Files.writeString(path, b64, StandardOpenOption.TRUNCATE_EXISTING);
                    logs.add("    Mode  : binary (bytes → AES → Base64)");
                    logs.add("    Raw size      : " + raw.length + " bytes");
                    logs.add("    Encrypted size: " + b64.length() + " chars");
                } else {
                    String content   = Files.readString(path);
                    String encrypted = AESCore.encrypt(content, sharedKeyHolder[0]);
                    Files.writeString(path, encrypted, StandardOpenOption.TRUNCATE_EXISTING);
                    logs.add("    Mode  : text");
                    logs.add("    Original size : " + content.length() + " chars");
                    logs.add("    Encrypted size: " + encrypted.length() + " chars");
                    logs.add("    Cipher preview: " +
                             encrypted.substring(0, Math.min(24, encrypted.length())) + "...");
                }
                logs.add("    [✓] Written back to disk");
            } catch (Exception ex) {
                logs.add("    [!] Error: " + ex.getMessage());
            }
        }
        logs.add("");
        logs.add("[✓] Encryption complete. " + files.size() + " file(s) encrypted.");
        logs.add("[*] Key held in memory. Displaying ransom screen...");
        return logs.toArray(new String[0]);
    }

    public static boolean isBinary(String filename) {
        String low = filename.toLowerCase();
        return low.endsWith(".png")  || low.endsWith(".jpg")  || low.endsWith(".jpeg") ||
               low.endsWith(".gif")  || low.endsWith(".bmp")  || low.endsWith(".ico")  ||
               low.endsWith(".webp") || low.endsWith(".pdf")  || low.endsWith(".zip")  ||
               low.endsWith(".tar")  || low.endsWith(".7z")   || low.endsWith(".exe");
    }

    private static String fileType(String filename) {
        String low = filename.toLowerCase();
        if (low.endsWith(".png") || low.endsWith(".jpg") || low.endsWith(".jpeg") ||
            low.endsWith(".gif") || low.endsWith(".bmp"))  return "IMAGE (binary)";
        if (low.endsWith(".txt"))                          return "TEXT";
        if (low.endsWith(".pdf"))                          return "PDF (binary)";
        if (low.endsWith(".zip") || low.endsWith(".7z"))   return "ARCHIVE (binary)";
        if (low.endsWith(".java") || low.endsWith(".py") ||
            low.endsWith(".js")  || low.endsWith(".c"))    return "SOURCE CODE";
        return "FILE";
    }
}
