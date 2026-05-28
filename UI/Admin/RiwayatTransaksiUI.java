package UI.Admin;

import ModelLogic.Admin;
import UI.ErrorUI;
import UI.LoginUI;
import UI.LogoutUI;
import Database.TransaksiDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class RiwayatTransaksiUI extends JFrame {
    private Admin admin;
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfCariTanggal;
    private JButton btnCari, btnRefresh, btnLogout;

    public RiwayatTransaksiUI(Admin admin) {
        this.admin = admin;
        setTitle("Riwayat Transaksi - CAFATIFA");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Cari Tanggal (YYYY-MM-DD):"));
        tfCariTanggal = new JTextField(12);
        filterPanel.add(tfCariTanggal);
        btnCari = new JButton("Cari");
        btnRefresh = new JButton("Refresh");
        filterPanel.add(btnCari);
        filterPanel.add(btnRefresh);
        main.add(filterPanel, BorderLayout.NORTH);

        String[] cols = {"ID Order", "Tanggal", "Total Harga", "Kasir", "Status"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        main.add(scroll, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

        loadData(null);

        btnCari.addActionListener(e -> {
            String tgl = tfCariTanggal.getText().trim();
            if (!tgl.isEmpty()) {
                if (isValidDateFormat(tgl)) {
                    loadData(tgl);
                } else {
                    ErrorUI.showError(this, "Format tanggal salah! Gunakan format YYYY-MM-DD (contoh: 2026-05-27)");
                }
            } else {
                loadData(null);
            }
        });

        btnRefresh.addActionListener(e -> {
            tfCariTanggal.setText("");
            loadData(null);
        });
    }

    private boolean isValidDateFormat(String dateStr) {
        // Memeriksa format YYYY-MM-DD dan validitas tanggal
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false); // tidak mengizinkan tanggal invalid seperti 2026-02-30
        try {
            sdf.parse(dateStr);
            return true;
        } catch (java.text.ParseException e) {
            return false;
        }
    }

    private void loadData(String filterDate) {
        model.setRowCount(0);
        TransaksiDAO dao = new TransaksiDAO();
        List<Object[]> list = dao.getAllTransaksi();
        for (Object[] row : list) {
            java.sql.Timestamp ts = (java.sql.Timestamp) row[1];
            String tgl = ts.toString().split(" ")[0];
            if (filterDate != null && !filterDate.equals(tgl)) continue;
            model.addRow(row);
        }
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(51, 181, 229));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        String[] menus = {"Pesanan", "Produk", "Laporan"};
        for (String menu : menus) {
            JButton btn = new JButton(menu);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setBackground(Color.WHITE);
            if (menu.equals("Pesanan")) btn.setBackground(new Color(244, 226, 123));
            final String m = menu;
            btn.addActionListener(e -> {
                if (m.equals("Laporan")) new LaporanUI(admin).setVisible(true);
                else if (m.equals("Produk")) new KelolaProdukUI(admin).setVisible(true);
                else if (m.equals("Pesanan")) { /* already here */ }
                dispose();
            });
            panel.add(btn);
            panel.add(Box.createVerticalStrut(10));
        }
        JButton logoutBtn = new JButton("👤 Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> logout());
        panel.add(Box.createVerticalGlue());
        panel.add(logoutBtn);
        return panel;
    }

    private void logout() {
        LogoutUI.logout(this);
    }
}