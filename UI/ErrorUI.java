package UI;

import javax.swing.*;
import java.awt.*;

public class ErrorUI {
    public static void showError(Component parent, String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog(parent, "Something Invalid");
        dialog.setAlwaysOnTop(true);
        dialog.setModal(true);
        dialog.setVisible(true);
    }
}

