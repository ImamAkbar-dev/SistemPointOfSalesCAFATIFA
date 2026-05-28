package UI.Admin;

import ModelLogic.Admin;
import ModelLogic.Produk;
import UI.ErrorUI;
import UI.LoginUI;
import UI.LogoutUI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class KelolaProdukUI extends JFrame {
    private Admin admin;
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfNama, tfHarga, tfStok, tfKategori;
    private JButton btnTambah, btnEdit, btnHapus, btnRefresh;

    // Warna placeholder
    private static final Color PLACEHOLDER_COLOR = Color.GRAY;
    private static final Color NORMAL_COLOR = Color.BLACK;

    public KelolaProdukUI(Admin admin) {
        this.admin = admin;
        setTitle("Kelola Produk - CAFATIFA");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form input
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Tambah/Edit Produk"));

        // Nama Produk
        formPanel.add(new JLabel("Nama Produk:"));
        tfNama = new JTextField();
        setPlaceholder(tfNama, "Contoh: Baju Kaos CAFATIFA");
        formPanel.add(tfNama);

        // Harga
        formPanel.add(new JLabel("Harga:"));
        tfHarga = new JTextField();
        setPlaceholder(tfHarga, "Contoh: 100000");
        formPanel.add(tfHarga);

        // Stok
        formPanel.add(new JLabel("Stok:"));
        tfStok = new JTextField();
        setPlaceholder(tfStok, "Contoh: 50");
        formPanel.add(tfStok);

        // Kategori
        formPanel.add(new JLabel("Kategori:"));
        tfKategori = new JTextField();
        setPlaceholder(tfKategori, "Contoh: Merchandise");
        formPanel.add(tfKategori);

        main.add(formPanel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        btnTambah = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        btnPanel.add(btnTambah);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapus);
        btnPanel.add(btnRefresh);
        main.add(btnPanel, BorderLayout.CENTER);

        // Tabel
        String[] cols = {"ID", "Nama Produk", "Kategori", "Harga", "Stok"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(700, 300));
        main.add(scroll, BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);

        loadData();

        btnTambah.addActionListener(e -> tambahProduk());
        btnEdit.addActionListener(e -> editProduk());
        btnHapus.addActionListener(e -> hapusProduk());
        btnRefresh.addActionListener(e -> {
            loadData();
            clearForm();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    tfNama.setText(model.getValueAt(row, 1).toString());
                    tfHarga.setText(model.getValueAt(row, 3).toString());
                    tfStok.setText(model.getValueAt(row, 4).toString());
                    tfKategori.setText(model.getValueAt(row, 2).toString());
                    // Reset warna teks menjadi normal (karena isi dari tabel bukan placeholder)
                    tfNama.setForeground(NORMAL_COLOR);
                    tfHarga.setForeground(NORMAL_COLOR);
                    tfStok.setForeground(NORMAL_COLOR);
                    tfKategori.setForeground(NORMAL_COLOR);
                }
            }
        });
    }

    // Method untuk menambah placeholder pada JTextField
    private void setPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(PLACEHOLDER_COLOR);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(NORMAL_COLOR);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
    }

    // Memeriksa apakah field sedang berisi placeholder (tidak valid)
    private boolean isPlaceholder(JTextField field, String placeholder) {
        return field.getText().equals(placeholder);
    }

    private void loadData() {
        model.setRowCount(0);
        List<Produk> list = admin.lihatProduk();
        for (Produk p : list) {
            model.addRow(new Object[]{p.getIdProduk(), p.getNama(), p.getKategori(), p.getHarga(), p.getStok()});
        }
    }

    private void tambahProduk() {
        try {
            String nama = tfNama.getText().trim();
            if (isPlaceholder(tfNama, "Contoh: Baju Kaos CAFATIFA") || nama.isEmpty()) {
                throw new Exception("Nama produk harus diisi!");
            }

            int harga, stok;
            try {
                harga = Integer.parseInt(tfHarga.getText().trim());
                if (isPlaceholder(tfHarga, "Contoh: 100000")) throw new NumberFormatException();
                if (harga <= 0) throw new Exception("Harga harus lebih dari 0!");
            } catch (NumberFormatException ex) {
                throw new Exception("Harga harus berupa angka positif (contoh: 100000)!");
            }

            try {
                stok = Integer.parseInt(tfStok.getText().trim());
                if (isPlaceholder(tfStok, "Contoh: 50")) throw new NumberFormatException();
                if (stok < 0) throw new Exception("Stok tidak boleh negatif!");
            } catch (NumberFormatException ex) {
                throw new Exception("Stok harus berupa angka (contoh: 50)!");
            }

            String kategori = tfKategori.getText().trim();
            if (isPlaceholder(tfKategori, "Contoh: Merchandise") || kategori.isEmpty()) {
                throw new Exception("Kategori harus diisi!");
            }

            Produk p = new Produk(nama, harga, stok, kategori);
            admin.tambahProduk(p);
            JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan");
            loadData();
            clearForm();
        } catch (Exception ex) {
            ErrorUI.showError(this, ex.getMessage());
        }
    }

    private void editProduk() {
        int row = table.getSelectedRow();
        if (row < 0) {
            ErrorUI.showError(this, "Pilih produk yang akan diedit!");
            return;
        }
        try {
            int id = (int) model.getValueAt(row, 0);
            String nama = tfNama.getText().trim();
            if (isPlaceholder(tfNama, "Contoh: Baju Kaos CAFATIFA") || nama.isEmpty()) {
                throw new Exception("Nama produk harus diisi!");
            }

            int harga, stok;
            try {
                harga = Integer.parseInt(tfHarga.getText().trim());
                if (isPlaceholder(tfHarga, "Contoh: 100000")) throw new NumberFormatException();
                if (harga <= 0) throw new Exception("Harga harus lebih dari 0!");
            } catch (NumberFormatException ex) {
                throw new Exception("Harga harus berupa angka positif!");
            }

            try {
                stok = Integer.parseInt(tfStok.getText().trim());
                if (isPlaceholder(tfStok, "Contoh: 50")) throw new NumberFormatException();
                if (stok < 0) throw new Exception("Stok tidak boleh negatif!");
            } catch (NumberFormatException ex) {
                throw new Exception("Stok harus berupa angka!");
            }

            String kategori = tfKategori.getText().trim();
            if (isPlaceholder(tfKategori, "Contoh: Merchandise") || kategori.isEmpty()) {
                throw new Exception("Kategori harus diisi!");
            }

            Produk p = new Produk(id, nama, harga, stok, kategori);
            admin.editProduk(p);
            JOptionPane.showMessageDialog(this, "Produk berhasil diupdate");
            loadData();
            // Tidak perlu clearForm() agar bisa edit langsung lagi
        } catch (Exception ex) {
            ErrorUI.showError(this, ex.getMessage());
        }
    }

    private void hapusProduk() {
        int row = table.getSelectedRow();
        if (row < 0) {
            ErrorUI.showError(this, "Pilih produk yang akan dihapus!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus produk ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            admin.hapusProduk(id);
            loadData();
            clearForm();
        }
    }

    private void clearForm() {// untuk reset input-an user
        tfNama.setText("Contoh: Baju Kaos CAFATIFA");
        tfNama.setForeground(PLACEHOLDER_COLOR);
        tfHarga.setText("Contoh: 100000");
        tfHarga.setForeground(PLACEHOLDER_COLOR);
        tfStok.setText("Contoh: 50");
        tfStok.setForeground(PLACEHOLDER_COLOR);
        tfKategori.setText("Contoh: Merchandise");
        tfKategori.setForeground(PLACEHOLDER_COLOR);
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(51, 181, 229));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(new EmptyBorder(20, 10, 20, 10));

        String[] menus = {"Pesanan", "Produk", "Laporan"};
        for (String menu : menus) {
            JButton btn = new JButton(menu);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setBackground(Color.WHITE);
            if (menu.equals("Produk")) btn.setBackground(new Color(244, 226, 123));
            final String m = menu;
            btn.addActionListener(e -> {
                if (m.equals("Pesanan")) new RiwayatTransaksiUI(admin).setVisible(true);
                else if (m.equals("Produk")) { /* already here */ }
                else if (m.equals("Laporan")) new LaporanUI(admin).setVisible(true);
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