package Database;

import ModelLogic.Produk;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdukDAO {
    
    public void tambahProduk(Produk produk) {
        String sql = "INSERT INTO produk (nama_produk, harga, stok, kategori) VALUES (?,?,?,?)";
        try (Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, produk.getNama());
            ps.setInt(2, produk.getHarga());
            ps.setInt(3, produk.getStok());
            ps.setString(4, produk.getKategori());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) produk.setIdProduk(rs.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public void editProduk(Produk produk) {
        String sql = "UPDATE produk SET nama_produk=?, harga=?, stok=?, kategori=? WHERE id_produk=?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, produk.getNama());
            ps.setInt(2, produk.getHarga());
            ps.setInt(3, produk.getStok());
            ps.setString(4, produk.getKategori());
            ps.setInt(5, produk.getIdProduk());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public void hapusProduk(int idProduk) {
        String sql = "DELETE FROM produk WHERE id_produk = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProduk);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Produk> getAllProduk() {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT * FROM produk";
        try (Connection conn = Koneksi.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Produk p = new Produk(
                    rs.getString("nama_produk"),
                    rs.getInt("harga"),
                    rs.getInt("stok"),
                    rs.getString("kategori")
                );
                p.setIdProduk(rs.getInt("id_produk"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public Produk getProdukById(int id) {
        String sql = "SELECT * FROM produk WHERE id_produk = ?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Produk(
                    rs.getInt("id_produk"),
                    rs.getString("nama_produk"),
                    rs.getInt("harga"),
                    rs.getInt("stok"),
                    rs.getString("kategori")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    public void kurangiStok(int idProduk, int jumlah) {
        String sql = "UPDATE produk SET stok = stok - ? WHERE id_produk = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jumlah);
            ps.setInt(2, idProduk);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}