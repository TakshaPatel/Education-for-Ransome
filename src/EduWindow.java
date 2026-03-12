import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.nio.file.*;
import java.util.*;
import java.util.Set;
import javax.crypto.SecretKey;

public class EduWindow {

    static final Color BG         = new Color(10,  10,  14);
    static final Color PANEL_BG   = new Color(16,  16,  22);
    static final Color CARD_BG    = new Color(22,  22,  30);
    static final Color ACCENT     = new Color(255, 59,  59);
    static final Color ACCENT2    = new Color(255, 140, 0);
    static final Color GREEN      = new Color(57,  255, 20);
    static final Color CYAN       = new Color(0,   220, 220);
    static final Color TEXT_PRI   = new Color(230, 230, 240);
    static final Color TEXT_SEC   = new Color(140, 140, 160);
    static final Color BORDER_COL = new Color(40,  40,  55);
    static final Color CONSOLE_BG = new Color(8,   8,   12);

    static final Font FONT_MONO  = new Font("Monospaced", Font.PLAIN,  12);
    static final Font FONT_TITLE = new Font("Monospaced", Font.BOLD,   22);
    static final Font FONT_LABEL = new Font("Monospaced", Font.BOLD,   13);
    static final Font FONT_BODY  = new Font("SansSerif",  Font.PLAIN,  13);
    static final Font FONT_SMALL = new Font("Monospaced", Font.PLAIN,  11);

    private JFrame     frame;
    private CardLayout cards;
    private JPanel     cardRoot;

    private final SecretKey[] sharedKey = { null };

    public EduWindow() {
        frame = new JFrame("RansomSim — Educational Mode");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 640);
        frame.setMinimumSize(new Dimension(900, 580));
        frame.getContentPane().setBackground(BG);

        cards    = new CardLayout();
        cardRoot = new JPanel(cards);
        cardRoot.setBackground(BG);

        cardRoot.add(buildSplashScreen(),    "SPLASH");
        cardRoot.add(buildDeliveryScreen(),  "DELIVERY");
        cardRoot.add(buildDiscoveryScreen(), "DISCOVERY");
        cardRoot.add(buildEncryptScreen(),   "ENCRYPT");
        cardRoot.add(buildPayScreen(),       "PAY");

        frame.add(cardRoot);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        cards.show(cardRoot, "SPLASH");
    }

    private JPanel buildSplashScreen() {
        JPanel p = new GradientPanel();
        p.setLayout(new GridBagLayout());

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel skull = makeLabel("☠", new Font("Monospaced", Font.BOLD, 56), ACCENT);
        skull.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = makeLabel("RANSOMSIM", FONT_TITLE, TEXT_PRI);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = makeLabel("A Cybersecurity Education Tool", FONT_BODY, TEXT_SEC);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COL);
        sep.setMaximumSize(new Dimension(400, 1));

        JLabel choose = makeLabel("Choose a mode to begin:", FONT_LABEL, TEXT_SEC);
        choose.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnRow.setOpaque(false);

        JButton walkBtn = makeButton("▶  WALKTHROUGH", ACCENT,  BG);
        JButton freeBtn = makeButton("⚡  FREE MODE",   ACCENT2, BG);
        walkBtn.setToolTipText("Guided tour — explains every stage of a ransomware attack");
        freeBtn.setToolTipText("Runs the sandbox simulation directly, no narration");
        walkBtn.addActionListener(e -> cards.show(cardRoot, "DELIVERY"));
        freeBtn.addActionListener(e -> FreeMode.launch(frame));
        btnRow.add(walkBtn);
        btnRow.add(freeBtn);

        JLabel warn = makeLabel("⚠  All activity is sandboxed to ./sandbox/  ⚠",
                                FONT_SMALL, new Color(180, 60, 60));
        warn.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(skull);  box.add(vgap(6));
        box.add(title);  box.add(vgap(4));
        box.add(sub);    box.add(vgap(18));
        box.add(sep);    box.add(vgap(18));
        box.add(choose); box.add(vgap(14));
        box.add(btnRow); box.add(vgap(24));
        box.add(warn);

        p.add(box);
        return p;
    }

    private JPanel buildDeliveryScreen() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(makeTopBar("STAGE 1 / 4", "DELIVERY",
                            "How ransomware reaches the victim"), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 1, 0));
        content.setBackground(BG);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(PANEL_BG);
        left.setBorder(makeBorder(ACCENT));
        left.add(makeScreenLabel("Delivery Vectors"), BorderLayout.NORTH);
        left.add(makeHtmlPane(SlideContent.deliveryHTML(), PANEL_BG), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(CARD_BG);
        right.setBorder(makeBorder(BORDER_COL));
        right.add(makeScreenLabel("Attack Chain"), BorderLayout.NORTH);
        right.add(buildAttackChain(),              BorderLayout.CENTER);

        content.add(left);
        content.add(right);
        root.add(content, BorderLayout.CENTER);
        root.add(makeNavBar(null, "DISCOVERY"), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildAttackChain() {
        String[] steps  = {
            "Step -1, Phishing Email",
            "Step 0, User Clicks Link / Opens Attachment",
            "Step 1, Payload Downloaded",
            "Step 2, Privilege Escalation",
            "Step 3, File System Traversal",
            "Step 4, AES Encryption Begins",
            "Step 5, Ransom Note Displayed"
        };
        Color[] colors = { ACCENT2, ACCENT2, ACCENT, ACCENT, CYAN, GREEN, ACCENT };

        JPanel chain = new JPanel();
        chain.setBackground(CARD_BG);
        chain.setLayout(new BoxLayout(chain, BoxLayout.Y_AXIS));
        chain.setBorder(new EmptyBorder(20, 24, 20, 24));

        for (int i = 0; i < steps.length; i++) {
            JLabel lbl = makeLabel(steps[i], FONT_LABEL, colors[i]);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            chain.add(lbl);
            if (i < steps.length - 1) {
                JLabel arrow = makeLabel("   │", FONT_MONO, BORDER_COL);
                arrow.setAlignmentX(Component.LEFT_ALIGNMENT);
                chain.add(arrow);
            }
        }
        chain.add(vgap(10));
        JLabel note = makeLabel(
            "Real ransomware can complete stages 3-6 in under 60 seconds.", FONT_SMALL, TEXT_SEC);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        chain.add(note);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_BG);
        wrapper.add(chain, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildDiscoveryScreen() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(makeTopBar("STAGE 2 / 4", "FILE DISCOVERY",
                            "Enumerating target files before encryption"), BorderLayout.NORTH);

        JPanel split = new JPanel(new GridLayout(1, 2, 1, 0));
        split.setBackground(BG);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(PANEL_BG);
        left.setBorder(makeBorder(CYAN));
        left.add(makeScreenLabel("How Ransomware Finds Files"), BorderLayout.NORTH);
        left.add(makeHtmlPane(SlideContent.discoveryHTML(), PANEL_BG), BorderLayout.CENTER);

        split.add(left);
        split.add(buildConsolePanel("File Discovery Console",
            () -> SlideContent.discoveryLogs(Main.listFiles())));

        root.add(split, BorderLayout.CENTER);
        root.add(makeNavBar("DELIVERY", "ENCRYPT"), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildEncryptScreen() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(makeTopBar("STAGE 3 / 4", "ENCRYPTION",
                            "AES-256-ECB applied to sandbox files"), BorderLayout.NORTH);

        JPanel split = new JPanel(new GridLayout(1, 2, 1, 0));
        split.setBackground(BG);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(PANEL_BG);
        left.setBorder(makeBorder(GREEN));
        left.add(makeScreenLabel("AES-256 Encryption"), BorderLayout.NORTH);
        left.add(makeHtmlPane(SlideContent.encryptHTML(), PANEL_BG), BorderLayout.CENTER);

        split.add(left);
        split.add(buildConsolePanel("Encryption Console",
            () -> SlideContent.encryptLogs(Main.listFiles(), sharedKey)));

        root.add(split, BorderLayout.CENTER);
        root.add(makeNavBar("DISCOVERY", "PAY"), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildPayScreen() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(makeTopBar("STAGE 4 / 4", "RANSOM DEMAND",
                            "How ransomware extorts payment"), BorderLayout.NORTH);

        JPanel split = new JPanel(new GridLayout(1, 2, 1, 0));
        split.setBackground(BG);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(PANEL_BG);
        left.setBorder(makeBorder(ACCENT));
        left.add(makeScreenLabel("The Extortion Model"), BorderLayout.NORTH);
        left.add(makeHtmlPane(SlideContent.paymentHTML(), PANEL_BG), BorderLayout.CENTER);
        left.add(buildRecoveryPanel(), BorderLayout.SOUTH);

        split.add(left);
        split.add(buildRansomNotePanel());

        root.add(split, BorderLayout.CENTER);
        root.add(makeNavBar("ENCRYPT", null), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildRecoveryPanel() {
        JPanel recovery = new JPanel();
        recovery.setBackground(new Color(8, 18, 8));
        recovery.setLayout(new BoxLayout(recovery, BoxLayout.Y_AXIS));
        recovery.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GREEN),
            new EmptyBorder(12, 16, 14, 16)));

        JLabel recTitle = makeLabel("SIMULATION RECOVERY", FONT_LABEL, GREEN);
        recTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel recDesc = makeLabel("In this simulation the override decryption key is:",
                                   FONT_SMALL, TEXT_SEC);
        recDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel keyBadge = makeLabel("  qwe123  ", new Font("Monospaced", Font.BOLD, 15), CONSOLE_BG);
        keyBadge.setOpaque(true);
        keyBadge.setBackground(GREEN);
        keyBadge.setBorder(new EmptyBorder(4, 10, 4, 10));
        keyBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel recNote = makeLabel("Click below to restore all sandbox files now.",
                                   FONT_SMALL, TEXT_SEC);
        recNote.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusLbl = makeLabel("", FONT_SMALL, GREEN);
        statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton decryptBtn = makeButton("▶  DECRYPT SANDBOX FILES", GREEN, new Color(8, 18, 8));
        decryptBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        decryptBtn.addActionListener(e -> {
            Set<String> files = Main.listFiles();
            if (sharedKey[0] == null) {
                statusLbl.setText("⚠  No key in memory — run the Encryption stage first.");
                return;
            }
            if (files.isEmpty()) {
                statusLbl.setText("⚠  No files found in ./sandbox/");
                return;
            }
            int ok = 0;
            for (String f : files) {
                Path p = Paths.get("./sandbox", f);
                try {
                    if (SlideContent.isBinary(f)) {
                        String b64       = Files.readString(p).trim();
                        byte[] encrypted = java.util.Base64.getDecoder().decode(b64);
                        byte[] raw       = AESCore.decryptBytes(encrypted, sharedKey[0]);
                        Files.write(p, raw, StandardOpenOption.TRUNCATE_EXISTING);
                    } else {
                        String enc = Files.readString(p);
                        String dec = AESCore.decrypt(enc, sharedKey[0]);
                        Files.writeString(p, dec, StandardOpenOption.TRUNCATE_EXISTING);
                    }
                    ok++;
                } catch (Exception ex) {

                }
            }
            if (ok > 0) {
                decryptBtn.setEnabled(false);
                statusLbl.setText("✓  " + ok + " file(s) restored to ./sandbox/");
            } else {
                statusLbl.setText("⚠  Files may already be decrypted.");
            }
        });

        recovery.add(recTitle);   recovery.add(vgap(6));
        recovery.add(recDesc);    recovery.add(vgap(8));
        recovery.add(keyBadge);   recovery.add(vgap(8));
        recovery.add(recNote);    recovery.add(vgap(10));
        recovery.add(decryptBtn); recovery.add(vgap(6));
        recovery.add(statusLbl);
        return recovery;
    }

    private JPanel buildRansomNotePanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(CONSOLE_BG);
        outer.setBorder(makeBorder(ACCENT));
        outer.add(makeScreenLabel("Simulated Ransom Note UI"), BorderLayout.NORTH);

        JPanel note = new JPanel();
        note.setBackground(new Color(20, 0, 0));
        note.setLayout(new BoxLayout(note, BoxLayout.Y_AXIS));
        note.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel skull = makeLabel("☠  YOUR FILES ARE ENCRYPTED  ☠",
                                 new Font("Monospaced", Font.BOLD, 14), ACCENT);
        skull.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel line1 = makeLabel("All documents, photos, and databases have been",
                                 FONT_SMALL, new Color(220, 180, 180));
        line1.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel line2 = makeLabel("encrypted with military-grade AES-256 encryption.",
                                 FONT_SMALL, new Color(220, 180, 180));
        line2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea keyBox = new JTextArea(
            "VICTIM-ID: A3F9-2C1D-8B44-E60A\n\nSend 0.05 BTC to:\n1A2b3C4d5E6f7G8h9I0jKlMnOpQrStUv");
        keyBox.setFont(FONT_MONO);
        keyBox.setBackground(new Color(30, 0, 0));
        keyBox.setForeground(ACCENT2);
        keyBox.setEditable(false);
        keyBox.setBorder(new EmptyBorder(10, 10, 10, 10));
        keyBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        keyBox.setMaximumSize(new Dimension(380, 90));

        JLabel timer = makeLabel("⏱  Time Remaining: 47:59:23", FONT_LABEL, ACCENT);
        timer.setAlignmentX(Component.CENTER_ALIGNMENT);

        final long[] remaining = { 47 * 3600 + 59 * 60 + 23 };
        new javax.swing.Timer(1000, ev -> {
            if (remaining[0] > 0) remaining[0]--;
            long h = remaining[0] / 3600, m = (remaining[0] % 3600) / 60, s = remaining[0] % 60;
            timer.setText(String.format("⏱  Time Remaining: %02d:%02d:%02d", h, m, s));
        }).start();

        JTextField keyInput = new JTextField("Enter decryption key...");
        keyInput.setFont(FONT_MONO);
        keyInput.setBackground(new Color(25, 0, 0));
        keyInput.setForeground(TEXT_SEC);
        keyInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1),
            new EmptyBorder(5, 8, 5, 8)));
        keyInput.setMaximumSize(new Dimension(380, 34));
        keyInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton fakeBtn = makeButton("SUBMIT KEY", ACCENT, new Color(20, 0, 0));
        fakeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        fakeBtn.addActionListener(e -> JOptionPane.showMessageDialog(frame,
            "In a real attack, this would verify your payment on an attacker-controlled server.\n\n" +
            "Defenders note: NEVER pay — payment does not guarantee recovery.",
            "Educational Note", JOptionPane.WARNING_MESSAGE));

        JLabel warn = makeLabel("⚠  This is a simulation. No real payment is requested.",
                                FONT_SMALL, new Color(120, 80, 80));
        warn.setAlignmentX(Component.CENTER_ALIGNMENT);

        note.add(skull);    note.add(vgap(14));
        note.add(line1);    note.add(line2);  note.add(vgap(14));
        note.add(keyBox);   note.add(vgap(14));
        note.add(timer);    note.add(vgap(16));
        note.add(keyInput); note.add(vgap(10));
        note.add(fakeBtn);  note.add(vgap(20));
        note.add(warn);

        JScrollPane sp = new JScrollPane(note);
        sp.setBorder(null);
        outer.add(sp, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildConsolePanel(String title,
                                     java.util.function.Supplier<String[]> logSupplier) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONSOLE_BG);
        panel.setBorder(makeBorder(BORDER_COL));
        panel.add(makeScreenLabel(title), BorderLayout.NORTH);

        JTextArea console = new JTextArea();
        console.setEditable(false);
        console.setBackground(CONSOLE_BG);
        console.setForeground(GREEN);
        console.setFont(FONT_MONO);
        console.setBorder(new EmptyBorder(8, 12, 8, 12));
        console.setLineWrap(true);

        JScrollPane sp = new JScrollPane(console);
        sp.setBorder(null);
        sp.getViewport().setBackground(CONSOLE_BG);
        panel.add(sp, BorderLayout.CENTER);

        JButton runBtn = makeButton("▶  RUN SIMULATION", GREEN, CONSOLE_BG);
        runBtn.setForeground(CONSOLE_BG);
        runBtn.setFont(FONT_LABEL);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(CONSOLE_BG);
        btnPanel.add(runBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        runBtn.addActionListener(e -> {
            console.setText("");
            runBtn.setEnabled(false);
            String[] logs = logSupplier.get();
            javax.swing.Timer t = new javax.swing.Timer(220, null);
            final int[] idx = { 0 };
            t.addActionListener(ev -> {
                if (idx[0] < logs.length) {
                    console.append(logs[idx[0]++] + "\n");
                    console.setCaretPosition(console.getDocument().getLength());
                } else {
                    t.stop();
                    runBtn.setEnabled(true);
                }
            });
            t.start();
        });

        return panel;
    }

    private JPanel makeTopBar(String stage, String title, String subtitle) {
        JPanel bar = new JPanel();
        bar.setBackground(new Color(14, 14, 20));
        bar.setLayout(new BoxLayout(bar, BoxLayout.Y_AXIS));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL),
            new EmptyBorder(10, 20, 10, 20)));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        row.setOpaque(false);

        JLabel stageTag = makeLabel(" " + stage + " ", FONT_SMALL, CONSOLE_BG);
        stageTag.setOpaque(true);
        stageTag.setBackground(ACCENT);
        stageTag.setBorder(new EmptyBorder(2, 6, 2, 6));

        row.add(stageTag);
        row.add(makeLabel(title,    new Font("Monospaced", Font.BOLD, 16), TEXT_PRI));
        row.add(makeLabel("—",      FONT_LABEL, BORDER_COL));
        row.add(makeLabel(subtitle, FONT_BODY,  TEXT_SEC));
        bar.add(row);
        return bar;
    }

    private JPanel makeNavBar(String prevCard, String nextCard) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(14, 14, 20));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COL),
            new EmptyBorder(10, 20, 10, 20)));

        if (prevCard != null) {
            JButton b = makeButton("← BACK", TEXT_SEC, PANEL_BG);
            b.addActionListener(e -> cards.show(cardRoot, prevCard));
            bar.add(b, BorderLayout.WEST);
        }
        if (nextCard != null) {
            JButton b = makeButton("NEXT →", ACCENT, BG);
            b.addActionListener(e -> cards.show(cardRoot, nextCard));
            bar.add(b, BorderLayout.EAST);
        } else {
            JButton b = makeButton("⟳  RESTART", ACCENT2, BG);
            b.addActionListener(e -> cards.show(cardRoot, "SPLASH"));
            bar.add(b, BorderLayout.EAST);
        }
        return bar;
    }

    private JLabel makeScreenLabel(String text) {
        JLabel l = new JLabel("  " + text, JLabel.LEFT);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_SEC);
        l.setOpaque(true);
        l.setBackground(new Color(14, 14, 20));
        l.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL),
            new EmptyBorder(7, 8, 7, 8)));
        return l;
    }

    private JScrollPane makeHtmlPane(String html, Color bg) {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setBackground(bg);
        pane.setForeground(TEXT_PRI);
        pane.setFont(FONT_BODY);
        pane.setContentType("text/html");
        pane.setText(html);
        pane.setBorder(new EmptyBorder(14, 18, 14, 18));
        JScrollPane sp = new JScrollPane(pane);
        sp.setBorder(null);
        sp.getViewport().setBackground(bg);
        return sp;
    }

    static JLabel makeLabel(String txt, Font font, Color fg) {
        JLabel l = new JLabel(txt);
        l.setFont(font);
        l.setForeground(fg);
        return l;
    }

    static JButton makeButton(String text, Color fg, Color bg) {
        JButton b = new JButton(text);
        b.setFont(FONT_LABEL);
        b.setForeground(fg);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fg, 1),
            new EmptyBorder(8, 18, 8, 18)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(fg); b.setForeground(bg); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); b.setForeground(fg); }
        });
        return b;
    }

    static Border makeBorder(Color c) {
        return BorderFactory.createLineBorder(c, 1);
    }

    static Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    static class GradientPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(20, 8, 8), getWidth(), getHeight(), new Color(8, 8, 20));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
