package Database;

import ModelLogic.Transaksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {
    public float getTotalPemasukanDariDatabase() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM transaksi WHERE status = 'selesai'";
        try (Connection conn = Koneksi.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getFloat(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalTransaksiSelesai() {
        String sql = "SELECT COUNT(*) FROM transaksi WHERE status = 'selesai'";
        try (Connection conn = Koneksi.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalProdukTerjual() {
        String sql = "SELECT COALESCE(SUM(d.jumlah), 0) FROM detail_transaksi d " +
                    "JOIN transaksi t ON d.id_transaksi = t.id_transaksi WHERE t.status = 'selesai'";
        try (Connection conn = Koneksi.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    
    public int buatTransaksi(int idUser) {
        String sql = "INSERT INTO transaksi (total, id_user, status) VALUES (0, ?, 'diproses')";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idUser);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public void updateTotal(int idTransaksi, int total) {
        String sql = "UPDATE transaksi SET total = ? WHERE id_transaksi = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, total);
            ps.setInt(2, idTransaksi);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void selesaikanTransaksi(int idTransaksi) {
        String sql = "UPDATE transaksi SET status = 'selesai' WHERE id_transaksi = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTransaksi);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Object[]> getAllTransaksi() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT t.id_transaksi, t.tanggal, t.total, u.username, t.status " +
                     "FROM transaksi t JOIN users u ON t.id_user = u.id_user ORDER BY t.tanggal DESC";
        try (Connection conn = Koneksi.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_transaksi"),
                    rs.getTimestamp("tanggal"),
                    rs.getInt("total"),
                    rs.getString("username"),
                    rs.getString("status")
                };
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<Object[]> getLaporanPenjualan() {
        // Laporan per hari/bulan? Sesuaikan kebutuhan
        String sql = "SELECT DATE(tanggal) as tgl, COUNT(*) as jumlah_transaksi, SUM(total) as pendapatan " +
                     "FROM transaksi WHERE status='selesai' GROUP BY DATE(tanggal) ORDER BY tgl DESC";
        List<Object[]> laporan = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Object[] row = { rs.getDate("tgl"), rs.getInt("jumlah_transaksi"), rs.getInt("pendapatan") };
                laporan.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return laporan;
    }

    public List<Object[]> getTransaksiByStatus(String status) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT t.id_transaksi, t.tanggal, t.total, u.username " +
                    "FROM transaksi t JOIN users u ON t.id_user = u.id_user " +
                    "WHERE t.status = ? ORDER BY t.tanggal DESC";
        try (Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_transaksi"),
                    rs.getTimestamp("tanggal"),
                    rs.getInt("total"),
                    rs.getString("username")
                };
                list.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Object[][] getDetailTransaksiWithPayment(int idTransaksi) {
        List<Object[]> details = new ArrayList<>();
        String sql = "SELECT d.id_produk, p.nama_produk, d.jumlah, d.harga_satuan, d.subtotal, " +
                    "       t.total, t.tanggal, b.metode_pembayaran, b.jumlah_bayar, b.tanggal_bayar " +
                    "FROM transaksi t " +
                    "LEFT JOIN detail_transaksi d ON t.id_transaksi = d.id_transaksi " +
                    "LEFT JOIN produk p ON d.id_produk = p.id_produk " +
                    "LEFT JOIN bukti_pembayaran b ON t.id_transaksi = b.id_transaksi " +
                    "WHERE t.id_transaksi = ?";
        try (Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_produk"),
                    rs.getString("nama_produk"),
                    rs.getInt("jumlah"),
                    rs.getInt("harga_satuan"),
                    rs.getInt("subtotal"),
                    rs.getInt("total"),
                    rs.getTimestamp("tanggal"),
                    rs.getString("metode_pembayaran"),
                    rs.getInt("jumlah_bayar"),
                    rs.getTimestamp("tanggal_bayar")
                };
                details.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return details.toArray(new Object[0][]);
    }
}