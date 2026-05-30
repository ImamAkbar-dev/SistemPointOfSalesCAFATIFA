package UI.Kasir;

import ModelLogic.Kasir;
import ModelLogic.Produk;
import Database.ProdukDAO;
import Database.TransaksiDAO;
import Database.PembayaranDAO;
import UI.ErrorUI;
import UI.LoginUI;
import UI.LogoutUI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiMasukUI extends JFrame {
    private Kasir kasir;
    private ProdukDAO produkDAO;
    private TransaksiDAO transaksiDAO;

    private JTable tableProduk;
    private DefaultTableModel modelProduk;
    private JTextField tfIdProduk, tfJumlah;
    private JButton btnTambahKeKeranjang;

    private JTable tableKeranjang;
    private DefaultTableModel modelKeranjang;
    private JLabel lblTotalHarga;

    private JTable tableTransaksiDiproses;
    private DefaultTableModel modelTransaksiDiproses;

    private List<Object[]> keranjangItems;
    private List<Integer> originalStok;

    private boolean isEditMode = false;
    private int editingIdTransaksi = -1;
    private JButton btnBuatPesanan;
    private JButton btnBatalEdit;

    public TransaksiMasukUI(Kasir kasir) {
        this.kasir = kasir;
        this.produkDAO = new ProdukDAO();
        this.transaksiDAO = new TransaksiDAO();
        this.keranjangItems = new ArrayList<>();
        this.originalStok = new ArrayList<>();

        setTitle("Pesanan Masuk - CAFATIFA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel Daftar Produk
        JPanel produkPanel = new JPanel(new BorderLayout());
        produkPanel.setBorder(BorderFactory.createTitledBorder("Daftar Produk"));
        modelProduk = new DefaultTableModel(new String[]{"ID", "Nama", "Harga", "Stok"}, 0);
        tableProduk = new JTable(modelProduk);
        produkPanel.add(new JScrollPane(tableProduk), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("ID Produk:"));
        tfIdProduk = new JTextField(5);
        ((AbstractDocument) tfIdProduk.getDocument()).setDocumentFilter(new DigitOnlyFilter());
        inputPanel.add(tfIdProduk);
        inputPanel.add(new JLabel("Jumlah:"));
        tfJumlah = new JTextField(5);
        ((AbstractDocument) tfJumlah.getDocument()).setDocumentFilter(new DigitOnlyFilter());
        inputPanel.add(tfJumlah);
        btnTambahKeKeranjang = new JButton("Tambah");
        inputPanel.add(btnTambahKeKeranjang);
        produkPanel.add(inputPanel, BorderLayout.SOUTH);

        // Panel Keranjang
        JPanel keranjangPanel = new JPanel(new BorderLayout());
        keranjangPanel.setBorder(BorderFactory.createTitledBorder("Keranjang Pesanan"));
        modelKeranjang = new DefaultTableModel(new String[]{"ID Produk", "Nama", "Harga", "Jumlah", "Subtotal", "Aksi"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return JButton.class;
                return super.getColumnClass(columnIndex);
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        tableKeranjang = new JTable(modelKeranjang);
        tableKeranjang.getColumnModel().getColumn(5).setCellRenderer(new KeranjangButtonRenderer());
        tableKeranjang.getColumnModel().getColumn(5).setCellEditor(new KeranjangButtonEditor(new JCheckBox()));
        keranjangPanel.add(new JScrollPane(tableKeranjang), BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotalHarga = new JLabel("Total: Rp0");
        totalPanel.add(lblTotalHarga);
        btnBuatPesanan = new JButton("Buat Pesanan");
        totalPanel.add(btnBuatPesanan);
        btnBatalEdit = new JButton("Batal");
        btnBatalEdit.setVisible(false);
        totalPanel.add(btnBatalEdit);
        keranjangPanel.add(totalPanel, BorderLayout.SOUTH);

        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, produkPanel, keranjangPanel);
        topSplit.setResizeWeight(0.5);
        topPanel.add(topSplit, BorderLayout.CENTER);
        splitPane.setTopComponent(topPanel);

        // Panel bawah: Transaksi Diproses
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Pesanan Masuk (Diproses)"));
        modelTransaksiDiproses = new DefaultTableModel(new String[]{"ID Order", "Tanggal", "Total Harga", "Kasir", "Bayar", "Edit", "Hapus"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 4 && columnIndex <= 6) return JButton.class;
                return super.getColumnClass(columnIndex);
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 4 && column <= 6;
            }
        };
        tableTransaksiDiproses = new JTable(modelTransaksiDiproses);
        tableTransaksiDiproses.getColumnModel().getColumn(4).setCellRenderer(new AksiButtonRenderer("Bayar"));
        tableTransaksiDiproses.getColumnModel().getColumn(4).setCellEditor(new AksiButtonEditor(new JCheckBox(), 4));
        tableTransaksiDiproses.getColumnModel().getColumn(5).setCellRenderer(new AksiButtonRenderer("Edit"));
        tableTransaksiDiproses.getColumnModel().getColumn(5).setCellEditor(new AksiButtonEditor(new JCheckBox(), 5));
        tableTransaksiDiproses.getColumnModel().getColumn(6).setCellRenderer(new AksiButtonRenderer("Hapus"));
        tableTransaksiDiproses.getColumnModel().getColumn(6).setCellEditor(new AksiButtonEditor(new JCheckBox(), 6));
        bottomPanel.add(new JScrollPane(tableTransaksiDiproses), BorderLayout.CENTER);
        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);

        loadProduk();
        loadTransaksiDiproses();

        btnTambahKeKeranjang.addActionListener(e -> tambahKeKeranjang());
        btnBuatPesanan.addActionListener(e -> {
            if (isEditMode) simpanEdit();
            else buatPesanan();
        });
        btnBatalEdit.addActionListener(e -> batalkanEdit());
    }

    // Filter hanya angka untuk ID dan Jumlah
    class DigitOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("\\d*")) {
                super.insertString(fb, offset, string, attr);
            }
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private void loadProduk() {
        modelProduk.setRowCount(0);
        List<Produk> list = kasir.lihatSemuaProduk();
        for (Produk p : list) {
            modelProduk.addRow(new Object[]{p.getIdProduk(), p.getNama(), p.getHarga(), p.getStok()});
        }
    }

    private void loadTransaksiDiproses() {
        modelTransaksiDiproses.setRowCount(0);
        List<Object[]> list = transaksiDAO.getTransaksiByStatus("diproses");
        for (Object[] row : list) {
            modelTransaksiDiproses.addRow(new Object[]{row[0], row[1], row[2], row[3], "Bayar", "Edit", "Hapus"});
        }
    }

    private void tambahKeKeranjang() {
        try {
            String idText = tfIdProduk.getText().trim();
            String jmlText = tfJumlah.getText().trim();
            if (idText.isEmpty() || jmlText.isEmpty()) {
                ErrorUI.showError(this, "ID Produk dan Jumlah harus diisi!");
                return;
            }
            int idProduk = Integer.parseInt(idText);
            int jumlah = Integer.parseInt(jmlText);
            if (jumlah <= 0) {
                ErrorUI.showError(this, "Jumlah harus lebih dari 0!");
                return;
            }

            // Cari produk di tabel (stok di memori)
            int rowProduk = -1;
            int stokTersedia = 0;
            for (int i = 0; i < modelProduk.getRowCount(); i++) {
                if ((int) modelProduk.getValueAt(i, 0) == idProduk) {
                    rowProduk = i;
                    stokTersedia = (int) modelProduk.getValueAt(i, 3);
                    break;
                }
            }
            if (rowProduk == -1) {
                ErrorUI.showError(this, "Produk tidak ditemukan");
                return;
            }
            if (stokTersedia < jumlah) {
                ErrorUI.showError(this, "Stok tidak cukup! Stok tersedia: " + stokTersedia);
                return;
            }

            String nama = (String) modelProduk.getValueAt(rowProduk, 1);
            int harga = (int) modelProduk.getValueAt(rowProduk, 2);
            int subtotal = harga * jumlah;

            boolean found = false;
            for (Object[] item : keranjangItems) {
                if ((int) item[0] == idProduk) {
                    int newJumlah = (int) item[3] + jumlah;
                    int newSubtotal = (int) item[2] * newJumlah;
                    item[3] = newJumlah;
                    item[4] = newSubtotal;
                    found = true;
                    break;
                }
            }
            if (!found) {
                keranjangItems.add(new Object[]{idProduk, nama, harga, jumlah, subtotal});
            }

            // Kurangi stok di tabel produk (visual)
            modelProduk.setValueAt(stokTersedia - jumlah, rowProduk, 3);

            updateKeranjangTable();
            tfIdProduk.setText("");
            tfJumlah.setText("");
        } catch (NumberFormatException ex) {
            ErrorUI.showError(this, "ID dan Jumlah harus angka");
        }
    }

    private void updateKeranjangTable() {
        modelKeranjang.setRowCount(0);
        int total = 0;
        for (Object[] item : keranjangItems) {
            modelKeranjang.addRow(new Object[]{item[0], item[1], item[2], item[3], item[4], "Hapus"});
            total += (int) item[4];
        }
        lblTotalHarga.setText("Total: Rp" + total);
    }

    private void buatPesanan() {
        if (keranjangItems.isEmpty()) {
            ErrorUI.showError(this, "Keranjang kosong, tambahkan produk terlebih dahulu!");
            return;
        }
        try {
            kasir.mulaiTransaksi(kasir.getIdUser());
            for (Object[] item : keranjangItems) {
                int idProduk = (int) item[0];
                int jumlah = (int) item[3];
                kasir.tambahProdukKeTransaksi(idProduk, jumlah);
            }
            keranjangItems.clear();
            updateKeranjangTable();
            JOptionPane.showMessageDialog(this, "Pesanan berhasil dibuat! ID Transaksi: " + kasir.getIdTransaksiAktif());
            loadTransaksiDiproses();
            loadProduk(); // refresh stok dari database
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorUI.showError(this, "Gagal membuat pesanan: " + ex.getMessage());
        }
    }

    private void hapusProdukDariKeranjang(int row) {
        Object[] item = keranjangItems.get(row);
        int idProduk = (int) item[0];
        int jumlahDiKeranjang = (int) item[3];
        for (int i = 0; i < modelProduk.getRowCount(); i++) {
            if ((int) modelProduk.getValueAt(i, 0) == idProduk) {
                int stokLama = (int) modelProduk.getValueAt(i, 3);
                modelProduk.setValueAt(stokLama + jumlahDiKeranjang, i, 3);
                break;
            }
        }
        keranjangItems.remove(row);
        updateKeranjangTable();
    }

    private void mulaiEdit(int idTransaksi) {
        originalStok.clear();
        for (int i = 0; i < modelProduk.getRowCount(); i++) {
            originalStok.add((int) modelProduk.getValueAt(i, 3));
        }

        List<Object[]> details = new ArrayList<>();
        Object[][] rawDetails = transaksiDAO.getDetailTransaksiWithPayment(idTransaksi);
        for (Object[] row : rawDetails) {
            if (row[0] != null) {
                int idProduk = (int) row[0];
                String nama = (String) row[1];
                int harga = (int) row[3];
                int jumlah = (int) row[2];
                int subtotal = (int) row[4];
                details.add(new Object[]{idProduk, nama, harga, jumlah, subtotal});
                for (int i = 0; i < modelProduk.getRowCount(); i++) {
                    if ((int) modelProduk.getValueAt(i, 0) == idProduk) {
                        int stokLama = (int) modelProduk.getValueAt(i, 3);
                        modelProduk.setValueAt(stokLama - jumlah, i, 3);
                        break;
                    }
                }
            }
        }
        if (details.isEmpty()) {
            ErrorUI.showError(this, "Tidak ada detail untuk transaksi ini!");
            return;
        }
        keranjangItems.clear();
        keranjangItems.addAll(details);
        updateKeranjangTable();

        isEditMode = true;
        editingIdTransaksi = idTransaksi;
        btnBuatPesanan.setText("Simpan");
        btnBatalEdit.setVisible(true);
    }

    private void simpanEdit() {
        if (keranjangItems.isEmpty()) {
            ErrorUI.showError(this, "Keranjang kosong! Transaksi tidak dapat disimpan.");
            return;
        }
        try {
            String sqlDelete = "DELETE FROM detail_transaksi WHERE id_transaksi = ?";
            try (var conn = Database.Koneksi.getConnection();
                 var ps = conn.prepareStatement(sqlDelete)) {
                ps.setInt(1, editingIdTransaksi);
                ps.executeUpdate();
            }
            int totalBaru = 0;
            for (Object[] item : keranjangItems) {
                int idProduk = (int) item[0];
                int jumlah = (int) item[3];
                int hargaSatuan = (int) item[2];
                int subtotal = (int) item[4];
                new Database.DetailTransaksiDAO().tambahDetail(editingIdTransaksi, idProduk, jumlah, hargaSatuan, subtotal);
                totalBaru += subtotal;
            }
            transaksiDAO.updateTotal(editingIdTransaksi, totalBaru);
            JOptionPane.showMessageDialog(this, "Transaksi berhasil diperbarui!");
            batalkanEdit();
            loadTransaksiDiproses();
            loadProduk();
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorUI.showError(this, "Gagal menyimpan perubahan: " + ex.getMessage());
        }
    }

    private void batalkanEdit() {
        if (isEditMode) {
            for (int i = 0; i < modelProduk.getRowCount() && i < originalStok.size(); i++) {
                modelProduk.setValueAt(originalStok.get(i), i, 3);
            }
        }
        isEditMode = false;
        editingIdTransaksi = -1;
        keranjangItems.clear();
        updateKeranjangTable();
        btnBuatPesanan.setText("Buat Pesanan");
        btnBatalEdit.setVisible(false);
    }

    private void prosesPembayaran(int idTransaksi) {
        int total = 0;
        for (int i = 0; i < modelTransaksiDiproses.getRowCount(); i++) {
            int id = (int) modelTransaksiDiproses.getValueAt(i, 0);
            if (id == idTransaksi) {
                total = (int) modelTransaksiDiproses.getValueAt(i, 2);
                break;
            }
        }
        if (total == 0) {
            try {
                List<Object[]> list = transaksiDAO.getTransaksiByStatus("diproses");
                for (Object[] row : list) {
                    if ((int) row[0] == idTransaksi) {
                        total = (int) row[2];
                        break;
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
        if (total == 0) {
            ErrorUI.showError(this, "Transaksi tidak ditemukan!");
            return;
        }

        String jumlahBayarStr = JOptionPane.showInputDialog(this, "Total tagihan: Rp" + total + "\nMasukkan jumlah bayar:");
        if (jumlahBayarStr == null) return;
        try {
            int jumlahBayar = Integer.parseInt(jumlahBayarStr);
            if (jumlahBayar < total) {
                ErrorUI.showError(this, "Jumlah bayar kurang!");
                return;
            }
            int kembalian = jumlahBayar - total;
            JOptionPane.showMessageDialog(this, "Pembayaran berhasil!\nKembalian: Rp" + kembalian);
            kasir.prosesPembayaran(idTransaksi, total, jumlahBayar, "Tunai");
            loadTransaksiDiproses();
            loadProduk();
        } catch (NumberFormatException ex) {
            ErrorUI.showError(this, "Masukkan angka yang valid!");
        }
    }

    private void hapusTransaksi(int idTransaksi) {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus transaksi ini? Semua item akan terhapus.", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sqlDetail = "DELETE FROM detail_transaksi WHERE id_transaksi = ?";
            String sqlTrans = "DELETE FROM transaksi WHERE id_transaksi = ?";
            try (var conn = Database.Koneksi.getConnection();
                 var psDetail = conn.prepareStatement(sqlDetail);
                 var psTrans = conn.prepareStatement(sqlTrans)) {
                psDetail.setInt(1, idTransaksi);
                psDetail.executeUpdate();
                psTrans.setInt(1, idTransaksi);
                psTrans.executeUpdate();
                JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus");
                loadTransaksiDiproses();
                loadProduk();
            } catch (Exception e) {
                e.printStackTrace();
                ErrorUI.showError(this, "Gagal menghapus transaksi: " + e.getMessage());
            }
        }
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(51, 181, 229));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(new EmptyBorder(20, 10, 20, 10));
        String[] menus = {"Pesanan Masuk", "Riwayat Transaksi"};
        for (String menu : menus) {
            JButton btn = new JButton(menu);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setBackground(Color.WHITE);
            if (menu.equals("Pesanan Masuk")) btn.setBackground(new Color(244, 226, 123));
            final String m = menu;
            btn.addActionListener(e -> {
                if (m.equals("Riwayat Transaksi")) {
                    new TransaksiUI(kasir).setVisible(true);
                    dispose();
                }
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

    // ========== INNER CLASS RENDERER & EDITOR ==========
    class KeranjangButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public KeranjangButtonRenderer() { setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Hapus");
            return this;
        }
    }

    class KeranjangButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private boolean isPushed;
        private int selectedRow;
        public KeranjangButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            selectedRow = row;
            button.setText("Hapus");
            isPushed = true;
            return button;
        }
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                SwingUtilities.invokeLater(() -> hapusProdukDariKeranjang(selectedRow));
            }
            isPushed = false;
            return "Hapus";
        }
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    class AksiButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        private String text;
        public AksiButtonRenderer(String text) { this.text = text; setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(text);
            return this;
        }
    }

    class AksiButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private boolean isPushed;
        private int selectedRow;
        private int columnIndex;
        public AksiButtonEditor(JCheckBox checkBox, int column) {
            super(checkBox);
            this.columnIndex = column;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            selectedRow = row;
            String text = (columnIndex == 4) ? "Bayar" : (columnIndex == 5) ? "Edit" : "Hapus";
            button.setText(text);
            isPushed = true;
            return button;
        }
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int idTransaksi = (int) modelTransaksiDiproses.getValueAt(selectedRow, 0);
                SwingUtilities.invokeLater(() -> {
                    if (columnIndex == 4) prosesPembayaran(idTransaksi);
                    else if (columnIndex == 5) mulaiEdit(idTransaksi);
                    else if (columnIndex == 6) hapusTransaksi(idTransaksi);
                });
            }
            isPushed = false;
            return (columnIndex == 4) ? "Bayar" : (columnIndex == 5) ? "Edit" : "Hapus";
        }
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
