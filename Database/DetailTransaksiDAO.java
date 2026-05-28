package Database;

import java.sql.*;

public class DetailTransaksiDAO {
    
    public void tambahDetail(int idTransaksi, int idProduk, int jumlah, int hargaSatuan, int subtotal) {
        String sql = "INSERT INTO detail_transaksi (id_transaksi, id_produk, jumlah, harga_satuan, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTransaksi);
            ps.setInt(2, idProduk);
            ps.setInt(3, jumlah);
            ps.setInt(4, hargaSatuan);
            ps.setInt(5, subtotal);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*public Object[] getDetailByTransaksi(int idTransaksi) {
        // Untuk struk
        return null;
    }*/
}