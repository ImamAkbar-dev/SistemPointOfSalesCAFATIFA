package Main;

import Database.Koneksi;
import Database.TransaksiDAO;
import ModelLogic.Transaksi;
import UI.LoginUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Test koneksi database 
        Koneksi.getConnection();
        
        // Inisialisasi static variabel dari database
        TransaksiDAO dao = new TransaksiDAO();
        Transaksi.totalPemasukan = dao.getTotalPemasukanDariDatabase();
        Transaksi.totalTransaksi = dao.getTotalTransaksiSelesai();
        Transaksi.totalProdukTerjual = dao.getTotalProdukTerjual();

        // Jalankan UI Login 
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
