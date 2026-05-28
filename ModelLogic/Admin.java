package ModelLogic;

import Database.ProdukDAO;
import Database.TransaksiDAO;
import UI.Admin.LaporanUI;
import java.util.List;

public class Admin extends User {
    private ProdukDAO produkDAO = new ProdukDAO();
    private TransaksiDAO transaksiDAO = new TransaksiDAO();

    public Admin(int idUser, String username, String password) {
        super(idUser, username, password, "admin");
    }

    @Override
    public void showDashboard() {
        new LaporanUI(this).setVisible(true);
    }

    public void tambahProduk(Produk produk) { 
        produkDAO.tambahProduk(produk); 
    }
    public void editProduk(Produk produk) { 
        produkDAO.editProduk(produk); 
    }
    public void hapusProduk(int idProduk) { 
        produkDAO.hapusProduk(idProduk); 
    }
    public List<Produk> lihatProduk() { 
        return produkDAO.getAllProduk(); 
    }
    public List<Object[]> lihatTransaksi() { 
        return transaksiDAO.getAllTransaksi(); 
    }
    public List<Object[]> lihatLaporan() { 
        return transaksiDAO.getLaporanPenjualan(); 
    }
}