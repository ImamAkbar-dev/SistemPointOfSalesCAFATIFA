package UI.Kasir;

import ModelLogic.Kasir;
import Database.TransaksiDAO;
import UI.ErrorUI;
import UI.LoginUI;
import UI.LogoutUI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class TransaksiUI extends JFrame {
    private Kasir kasir;
    private TransaksiDAO transaksiDAO;
    private JTable table;
    private DefaultTableModel model;

    public TransaksiUI(Kasir kasir) {
        this.kasir = kasir;
        this.transaksiDAO = new TransaksiDAO();
        setTitle("Riwayat Transaksi - CAFATIFA");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabel dengan kolom Detail Pesanan berupa tombol
        String[] cols = {"ID Order", "Tanggal", "Total Harga", "Detail Pesanan", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return JButton.class; // kolom Detail Pesanan
                return super.getColumnClass(columnIndex);
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // hanya kolom Detail yang bisa diklik
            }
        };
        table = new JTable(model);
        // Renderer & Editor untuk tombol Detail
        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane scroll = new JScrollPane(table);
        main.add(scroll, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        List<Object[]> list = transaksiDAO.getTransaksiByStatus("selesai");
        for (Object[] row : list) {
            // Kolom ke-3 diisi teks "Detail" (akan ditimpa oleh tombol)
            model.addRow(new Object[]{row[0], row[1], row[2], "Detail", "SELESAI"});
        }
    }

    private void showDetailTransaksi(int idTransaksi) {
        Object[][] details = transaksiDAO.getDetailTransaksiWithPayment(idTransaksi);
        if (details.length == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada detail");
            return;
        }
        Object[] first = details[0];
        int total = (int) first[5];
        java.sql.Timestamp tglTransaksi = (java.sql.Timestamp) first[6];
        String metode = first[7] != null ? (String) first[7] : "-";
        int jumlahBayar = first[8] != null ? (int) first[8] : 0;
        java.sql.Timestamp tglBayar = (java.sql.Timestamp) first[9];

        StringBuilder sb = new StringBuilder();
        sb.append("Tanggal Transaksi: ").append(tglTransaksi).append("\n");
        sb.append("Total: Rp").append(total).append("\n");
        sb.append("Metode Pembayaran: ").append(metode).append("\n");
        sb.append("Jumlah Bayar: Rp").append(jumlahBayar).append("\n");
        sb.append("Kembalian: Rp").append(jumlahBayar - total).append("\n");
        sb.append("Tanggal Bayar: ").append(tglBayar).append("\n\n");
        sb.append("Rincian Barang:\n");
        for (Object[] row : details) {
            sb.append("- ").append(row[1]).append(" x ").append(row[2]).append(" = Rp").append(row[4]).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Detail Pesanan", JOptionPane.INFORMATION_MESSAGE);
    }

    // Renderer dan Editor untuk Tombol Detail
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Detail");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            selectedRow = row;
            button.setText("Detail");
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Tunda eksekusi sampai event queue selesai
                SwingUtilities.invokeLater(() -> {
                    int idTransaksi = (int) model.getValueAt(selectedRow, 0);
                    showDetailTransaksi(idTransaksi);
                });
            }
            isPushed = false;
            return "Detail";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(51, 181, 229));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        String[] menus = {"Pesanan Masuk", "Riwayat Transaksi"};
        for (String menu : menus) {
            JButton btn = new JButton(menu);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setBackground(Color.WHITE);
            if (menu.equals("Riwayat Transaksi")) btn.setBackground(new Color(244, 226, 123));
            btn.addActionListener(e -> {
                if (menu.equals("Pesanan Masuk")) {
                    new TransaksiMasukUI(kasir).setVisible(true);
                    dispose();
                }
            });
            panel.add(btn);
            panel.add(Box.createVerticalStrut(10));
        }
        JButton logoutBtn = new JButton("👤 Logout");
        logoutBtn.addActionListener(e -> logout());
        panel.add(Box.createVerticalGlue());
        panel.add(logoutBtn);
        return panel;
    }

    private void logout() {
        LogoutUI.logout(this);
    }
}