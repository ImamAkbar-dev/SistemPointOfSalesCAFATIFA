package Database;

import java.sql.*;

public class PembayaranDAO {
    
    public void simpanPembayaran(int idTransaksi, String metode, int jumlahBayar, String status) {
        String sql = "INSERT INTO bukti_pembayaran (id_transaksi, metode_pembayaran, jumlah_bayar, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTransaksi);
            ps.setString(2, metode);
            ps.setInt(3, jumlahBayar);
            ps.setString(4, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}