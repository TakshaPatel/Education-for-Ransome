import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Window {

    private JFrame frame;
    private JTextArea output;
    private JTextField input;

    public Window() {
        frame = new JFrame("Ransomware Simulation");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        output = new JTextArea();
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setBackground(Color.RED);
        output.setForeground(Color.WHITE);
        output.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(output);

        input = new JTextField();
        input.setFont(new Font("Monospaced", Font.PLAIN, 14));

        frame.add(scroll, BorderLayout.CENTER);
        frame.add(input, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void print(String text) {
        output.append(text + "\n");
        output.setCaretPosition(output.getDocument().getLength());
    }

    public void onInput(ActionListener action) {
        input.addActionListener(e -> {
            action.actionPerformed(e);
        });
    }

    public String getInput() {
        String txt = input.getText();
        input.setText("");
        return txt;
    }
}
