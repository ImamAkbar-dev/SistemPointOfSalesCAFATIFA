package UI;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class LogoutUI {
    public static void logout(Component parent) {
        int confirm = JOptionPane.showConfirmDialog(parent, "Logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (parent instanceof JFrame) {
                ((JFrame) parent).dispose();
            }
            new LoginUI().setVisible(true);
        }
    }
}