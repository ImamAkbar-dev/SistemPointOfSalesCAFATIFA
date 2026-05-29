package UI.Admin;

import ModelLogic.Admin;
import Database.TransaksiDAO;
import UI.ErrorUI;
import UI.LoginUI;
import UI.LogoutUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LaporanUI extends JFrame {
    private Admin admin;
    private JComboBox<String> cbPeriode;
    private JTextField tfStartDate, tfEndDate;
    private JButton btnTampilkan;
    private JPanel chartPanelContainer;
    private JLabel lblTotalPenjualan, lblTotalTransaksi, lblProdukTerjual;
    private JLabel lblPeriodeRange;

    public LaporanUI(Admin admin) {
        this.admin = admin;
        setTitle("Laporan Penjualan - CAFATIFA");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Main content
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        main.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.NORTH);

        JPanel cardPanel = createCardPanel();
        topPanel.add(cardPanel, BorderLayout.CENTER);

        main.add(topPanel, BorderLayout.NORTH);

        chartPanelContainer = new JPanel(new BorderLayout());
        chartPanelContainer.setPreferredSize(new Dimension(800, 400));
        chartPanelContainer.setBorder(BorderFactory.createTitledBorder("Angka Penjualan"));
        main.add(chartPanelContainer, BorderLayout.CENTER);

        lblPeriodeRange = new JLabel("Periode: -", SwingConstants.CENTER);
        lblPeriodeRange.setFont(new Font("SansSerif", Font.PLAIN, 14));
        main.add(lblPeriodeRange, BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);

        // Set default periode dari tanggal pertama transaksi sampai tanggal terakhir
        setDefaultPeriod();
    }

    private void setDefaultPeriod() {
        TransaksiDAO dao = new TransaksiDAO();
        List<Object[]> all = dao.getAllTransaksi();
        if (all.isEmpty()) {
            LocalDate today = LocalDate.now();
            tfStartDate.setText(today.format(DateTimeFormatter.ISO_LOCAL_DATE));
            tfEndDate.setText(today.format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date minDate = null;
            Date maxDate = null;
            for (Object[] row : all) {
                java.sql.Timestamp ts = (java.sql.Timestamp) row[1];
                Date tgl = new Date(ts.getTime());
                if (minDate == null || tgl.before(minDate)) minDate = tgl;
                if (maxDate == null || tgl.after(maxDate)) maxDate = tgl;
            }
            tfStartDate.setText(sdf.format(minDate));
            tfEndDate.setText(sdf.format(maxDate));
        }
        loadChartAndSummary("Hari", tfStartDate.getText(), tfEndDate.getText());
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
            btn.setFocusPainted(false);
            if (menu.equals("Laporan")) btn.setBackground(new Color(244, 226, 123));
            final String m = menu;
            btn.addActionListener(e -> {
                if (m.equals("Pesanan")) new RiwayatTransaksiUI(admin).setVisible(true);
                else if (m.equals("Produk")) new KelolaProdukUI(admin).setVisible(true);
                else if (m.equals("Laporan")) { /* already here */ }
                dispose();
            });
            panel.add(btn);
            panel.add(Box.createVerticalStrut(10));
        }
        JButton logoutBtn = new JButton("👤 Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(180, 40));
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        panel.add(Box.createVerticalGlue());
        panel.add(logoutBtn);
        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Periode:"));
        cbPeriode = new JComboBox<>(new String[]{"Hari", "Bulan", "Tahun"});
        panel.add(cbPeriode);
        panel.add(new JLabel("Dari:"));
        tfStartDate = new JTextField(12);
        panel.add(tfStartDate);
        panel.add(new JLabel("Sampai:"));
        tfEndDate = new JTextField(12);
        panel.add(tfEndDate);
        btnTampilkan = new JButton("Tampilkan");
        btnTampilkan.addActionListener(e -> {
            String start = tfStartDate.getText().trim();
            String end = tfEndDate.getText().trim();
            if (isDateValid(start, end)) {
                loadChartAndSummary((String) cbPeriode.getSelectedItem(), start, end);
            } else {
                ErrorUI.showError(this, "Periode akhir tidak boleh lebih kecil dari periode awal!");
            }
        });
        panel.add(btnTampilkan);
        return panel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Card 1 - Total Penjualan
        JPanel card1 = new JPanel(new BorderLayout());
        card1.setBackground(new Color(76, 175, 80));
        card1.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel title1 = new JLabel("Total Penjualan");
        title1.setForeground(Color.WHITE);
        title1.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotalPenjualan = new JLabel("Rp0");
        lblTotalPenjualan.setForeground(Color.WHITE);
        lblTotalPenjualan.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotalPenjualan.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel textPanel1 = new JPanel(new BorderLayout());
        textPanel1.setOpaque(false);
        textPanel1.add(title1, BorderLayout.WEST);
        textPanel1.add(lblTotalPenjualan, BorderLayout.EAST);
        card1.add(textPanel1, BorderLayout.CENTER);

        // Card 2 - Total Transaksi
        JPanel card2 = new JPanel(new BorderLayout());
        card2.setBackground(new Color(33, 150, 243));
        card2.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel title2 = new JLabel("Total Transaksi");
        title2.setForeground(Color.WHITE);
        title2.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotalTransaksi = new JLabel("0");
        lblTotalTransaksi.setForeground(Color.WHITE);
        lblTotalTransaksi.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotalTransaksi.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel textPanel2 = new JPanel(new BorderLayout());
        textPanel2.setOpaque(false);
        textPanel2.add(title2, BorderLayout.WEST);
        textPanel2.add(lblTotalTransaksi, BorderLayout.EAST);
        card2.add(textPanel2, BorderLayout.CENTER);

        // Card 3 - Produk Terjual
        JPanel card3 = new JPanel(new BorderLayout());
        card3.setBackground(new Color(255, 87, 34));
        card3.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel title3 = new JLabel("Produk Terjual");
        title3.setForeground(Color.WHITE);
        title3.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblProdukTerjual = new JLabel("0");
        lblProdukTerjual.setForeground(Color.WHITE);
        lblProdukTerjual.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblProdukTerjual.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel textPanel3 = new JPanel(new BorderLayout());
        textPanel3.setOpaque(false);
        textPanel3.add(title3, BorderLayout.WEST);
        textPanel3.add(lblProdukTerjual, BorderLayout.EAST);
        card3.add(textPanel3, BorderLayout.CENTER);

        panel.add(card1);
        panel.add(card2);
        panel.add(card3);
        return panel;
    }

    private void loadChartAndSummary(String periode, String startDate, String endDate) {
        TransaksiDAO dao = new TransaksiDAO();
        List<Object[]> all = dao.getAllTransaksi();

        Map<String, Integer> dataMap = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long totalPenjualan = 0;
        long totalTransaksi = 0;
        long totalProduk = 0;

        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(end);
            calEnd.add(Calendar.DAY_OF_MONTH, 1);
            Date endPlusOne = calEnd.getTime();

            Calendar cal = Calendar.getInstance();

            for (Object[] row : all) {
                if (!"selesai".equals(row[4])) continue;
                java.sql.Timestamp ts = (java.sql.Timestamp) row[1];
                Date tgl = new Date(ts.getTime());
                if (tgl.before(start) || !tgl.before(endPlusOne)) continue;

                String key;
                cal.setTime(tgl);
                if ("Hari".equals(periode)) {
                    key = sdf.format(tgl);
                } else if ("Bulan".equals(periode)) {
                    key = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1);
                } else {
                    key = String.valueOf(cal.get(Calendar.YEAR));
                }
                int total = (int) row[2];
                dataMap.put(key, dataMap.getOrDefault(key, 0) + total);
                totalPenjualan += total;
                totalTransaksi++;
            }

            // Hitung total produk terjual dalam rentang
            totalProduk = dao.getTotalProdukTerjualInRange(start, endPlusOne);

        } catch (Exception e) {
            e.printStackTrace();
        }

        lblTotalPenjualan.setText(formatRupiah(totalPenjualan));
        lblTotalTransaksi.setText(String.valueOf(totalTransaksi));
        lblProdukTerjual.setText(String.valueOf(totalProduk));

        lblPeriodeRange.setText("Periode " + formatTanggal(startDate) + " - " + formatTanggal(endDate));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<String> sortedKeys = new ArrayList<>(dataMap.keySet());
        Collections.sort(sortedKeys);
        for (String key : sortedKeys) {
            dataset.addValue(dataMap.get(key), "Penjualan", key);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "", "Periode", "Jumlah (Rp)", dataset);
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0, 153, 0));
        renderer.setSeriesOutlinePaint(0, Color.BLACK);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanelContainer.removeAll();
        chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();
    }

    private String formatRupiah(long amount) {
        return "Rp" + String.format("%,d", amount).replace(',', '.');
    }

    private String formatTanggal(String yyyyMMdd) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            return output.format(input.parse(yyyyMMdd));
        } catch (Exception e) {
            return yyyyMMdd;
        }
    }

    private boolean isDateValid(String start, String end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d1 = sdf.parse(start);
            Date d2 = sdf.parse(end);
            return !d2.before(d1);
        } catch (Exception e) {
            return false;
        }
    }

    private void logout() {
        LogoutUI.logout(this);
    }
}
