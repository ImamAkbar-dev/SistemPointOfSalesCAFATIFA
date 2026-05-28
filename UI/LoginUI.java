package UI;

import Database.UserDAO;
import ModelLogic.Admin;
import ModelLogic.Kasir;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginUI extends JFrame {
    private CustomTextField usernameField;
    private CustomPasswordField passwordField;
    private UserDAO userDAO;

    public LoginUI() {
        userDAO = new UserDAO();
        setTitle("Login - CAFATIFA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        GradientBackgroundPanel bgPanel = new GradientBackgroundPanel();
        bgPanel.setLayout(new GridBagLayout());

        RoundedPanel cardPanel = new RoundedPanel(30, Color.WHITE);
        cardPanel.setPreferredSize(new Dimension(650, 350));
        cardPanel.setLayout(new GridLayout(1, 2));

        LeftGraphicPanel leftPanel = new LeftGraphicPanel();
        JPanel rightPanel = createRightPanel();

        cardPanel.add(leftPanel);
        cardPanel.add(rightPanel);
        bgPanel.add(cardPanel);
        add(bgPanel);
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Sign-In");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(20));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));

        usernameField = new CustomTextField("Username", true);
        usernameField.setMaximumSize(new Dimension(250, 45));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(15));

        passwordField = new CustomPasswordField("Password");
        passwordField.setMaximumSize(new Dimension(250, 45));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(30));

        CustomButton loginButton = new CustomButton("Login");
        loginButton.setMaximumSize(new Dimension(250, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(loginButton);

        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());

        return panel;
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            ErrorUI.showError(this, "Username dan password tidak boleh kosong!");
            return;
        }

        if (userDAO.validateUser(username, password)) {
            String role = userDAO.getRole(username);
            int idUser = userDAO.getUserId(username);
            dispose();

            if ("admin".equalsIgnoreCase(role)) {
                Admin admin = new Admin(idUser, username, password);
                admin.showDashboard();
            } else if ("kasir".equalsIgnoreCase(role)) {
                Kasir kasir = new Kasir(idUser, username, password);
                kasir.showDashboard();
            } else {
                ErrorUI.showError(null, "Role tidak dikenali!");
                System.exit(0);
            }
        } else {
            ErrorUI.showError(this, "Username atau password salah!");
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}

// KELAS KOMPONEN KUSTOM 

class GradientBackgroundPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color1 = new Color(135, 206, 250);
        Color color2 = new Color(0, 153, 255);
        GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}

class RoundedPanel extends JPanel {
    private int radius;
    private Color bgColor;

    public RoundedPanel(int radius, Color bgColor) {
        this.radius = radius;
        this.bgColor = bgColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // bayangan
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 5, radius, radius);
        // latar putih
        g2d.setColor(bgColor);
        g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, radius, radius);
    }
}

class LeftGraphicPanel extends JPanel {
    public LeftGraphicPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        try {
            java.net.URL imgURL = getClass().getResource("/Gambar/logoCAFATIFA.png");
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(150, -1, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaledImg));
                logoLabel.setBorder(new EmptyBorder(0, 0, 140, 120));
                add(logoLabel, BorderLayout.CENTER);
            } else {
                System.err.println("File gambar tidak ditemukan!");
                JLabel fallbackLabel = new JLabel("<html><center><b>CAFATIFA</b><br>(LOGO NOT FOUND)</center></html>", SwingConstants.CENTER);
                add(fallbackLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape clip = new RoundRectangle2D.Float(0, 0, getWidth() * 2, getHeight() - 10, 30, 30);
        g2d.clip(clip);
        g2d.setColor(new Color(51, 181, 229));
        g2d.fill(new Ellipse2D.Double(-50, -50, 300, 300));
        g2d.setColor(new Color(135, 206, 250));
        g2d.fill(new Ellipse2D.Double(-80, 150, 250, 250));
        g2d.setColor(new Color(228, 208, 95));
        g2d.fill(new Ellipse2D.Double(100, 200, 150, 150));
    }
}

class CustomTextField extends JTextField {
    private String hint;

    public CustomTextField(String hint, boolean isUser) {
        this.hint = hint;
        setOpaque(false);
        setBorder(new EmptyBorder(5, 40, 5, 15));
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setBackground(new Color(220, 220, 220));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(g);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(15, 10, 10, 10);
        g2d.fillArc(10, 22, 20, 20, 0, 180);
        if (getText().isEmpty()) {
            g2d.setColor(Color.GRAY);
            g2d.drawString(hint, 40, 28);
        }
    }
}

class CustomPasswordField extends JPasswordField {
    private String hint;
    private boolean isPasswordVisible = false;
    private char defaultEchoChar;

    public CustomPasswordField(String hint) {
        this.hint = hint;
        this.defaultEchoChar = getEchoChar();
        setOpaque(false);
        setBorder(new EmptyBorder(5, 40, 5, 40));
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setBackground(new Color(220, 220, 220));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Rectangle clickArea = new Rectangle(getWidth() - 33, 13, 22, 16);
                if (clickArea.contains(e.getPoint())) {
                    togglePasswordVisibility();
                }
            }
        });
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            setEchoChar((char) 0);
        } else {
            setEchoChar(defaultEchoChar);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(g);
        g2d.setColor(Color.BLACK);
        g2d.drawArc(14, 10, 10, 12, 0, 180);
        g2d.fillRect(12, 16, 14, 12);
        drawEyeIcon(g2d, isPasswordVisible);
        if (getPassword().length == 0) {
            g2d.setColor(Color.GRAY);
            g2d.drawString(hint, 40, 28);
        }
    }

    private void drawEyeIcon(Graphics2D g2d, boolean isVisible) {
        Color prev = g2d.getColor();
        g2d.setColor(Color.BLACK);
        int eyeX = getWidth() - 30;
        int eyeY = 16;
        int eyeWidth = 16;
        int eyeHeight = 10;
        g2d.drawOval(eyeX, eyeY, eyeWidth, eyeHeight);
        g2d.fillOval(eyeX + 5, eyeY + 2, 6, 6);
        if (!isVisible) {
            g2d.drawLine(eyeX, eyeY + eyeHeight, eyeX + eyeWidth, eyeY);
        }
        g2d.setColor(prev);
    }
}

class CustomButton extends JButton {
    public CustomButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("SansSerif", Font.BOLD, 18));
        setForeground(Color.WHITE);
        setBackground(new Color(244, 226, 123));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2d.setColor(Color.GRAY);
        g2d.drawString(getText(), x+1, y+1);
        g2d.setColor(getForeground());
        g2d.drawString(getText(), x, y);
    }
}