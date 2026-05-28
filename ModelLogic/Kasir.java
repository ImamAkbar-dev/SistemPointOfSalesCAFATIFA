package ModelLogic;

import Database.*;
import UI.Kasir.TransaksiMasukUI;
import java.sql.Timestamp;
import java.util.*;

public class Kasir extends User {
    private ProdukDAO produkDAO = new ProdukDAO();
    private TransaksiDAO transaksiDAO = new TransaksiDAO();
    private DetailTransaksiDAO detailDAO = new DetailTransaksiDAO();
    private PembayaranDAO pembayaranDAO = new PembayaranDAO();

    private int currentIdTransaksi = -1;
    private Transaksi currentTransaksi;
    private List<DetailTransaksi> keranjangDetail;

    public Kasir(int idUser, String username, String password) {
        super(idUser, username, password, "kasir");
        keranjangDetail = new ArrayList<>();
    }

    @Override
    public void showDashboard() {
        new TransaksiMasukUI(this).setVisible(true);
    }

    public int getIdUser() { return idUser; }
    public int getIdTransaksiAktif() { return currentIdTransaksi; }

    // Mulai transaksi: buat objek Transaksi + simpan ke database
    public int mulaiTransaksi(int idUser) {
        currentTransaksi = new Transaksi(this);
        currentIdTransaksi = transaksiDAO.buatTransaksi(idUser);
        keranjangDetail.clear();
        return currentIdTransaksi;
    }

    // Tambah produk: buat DetailTransaksi, masukkan ke Transaksi, simpan ke DB
    public void tambahProdukKeTransaksi(int idProduk, int jumlah) {
        if (currentIdTransaksi == -1 || currentTransaksi == null) {
            System.out.println("Mulai transaksi dulu");
            return;
        }
        Produk p = produkDAO.getProdukById(idProduk);
        if (p == null || p.getStok() < jumlah) {
            System.out.println("Produk tidak ada / stok habis");
            return;
        }
        // Objek DetailTransaksi dibuat di sini
        DetailTransaksi detail = new DetailTransaksi(p, jumlah);
        keranjangDetail.add(detail);
        currentTransaksi.addDetail(detail);

        // Hitung ulang total
        int totalSementara = currentTransaksi.hitungTotalBiaya();

        // Simpan detail dan total ke database
        int subtotal = p.getHarga() * jumlah;
        detailDAO.tambahDetail(currentIdTransaksi, idProduk, jumlah, p.getHarga(), subtotal);
        transaksiDAO.updateTotal(currentIdTransaksi, totalSementara);
    }

    public void prosesPembayaran(int idTransaksi, int total, int jumlahBayar, String metode) {
        // Buat objek Pembayaran
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Pembayaran pembayaran = new Pembayaran(metode, now, jumlahBayar);
        
        // Hitung kembalian 
        int kembalian = pembayaran.hitungKembalian(total);
        //System.out.println("Kembalian: " + kembalian); // debugging
        
        // Simpan ke database (DAO)
        pembayaranDAO.simpanPembayaran(idTransaksi, metode, jumlahBayar, "berhasil");
        transaksiDAO.selesaikanTransaksi(idTransaksi);
        
        // Update static variabel
        Transaksi.totalPemasukan += total;
        Transaksi.totalTransaksi++;
        
        // Hitung total produk terjual dari detail transaksi
        int totalProduk = 0;
        try {
            Object[][] details = transaksiDAO.getDetailTransaksiWithPayment(idTransaksi);
            for (Object[] row : details) {
                if (row[2] != null) totalProduk += (int) row[2];
            }
        } catch (Exception e) { e.printStackTrace(); }
        Transaksi.totalProdukTerjual += totalProduk;
        
        // Jika transaksi yang diproses adalah transaksi aktif saat ini, reset state
        if (this.currentIdTransaksi == idTransaksi) {
            this.currentIdTransaksi = -1;
            this.currentTransaksi = null;
            this.keranjangDetail.clear();
        }
    }

    public List<Produk> lihatSemuaProduk() {
        return produkDAO.getAllProduk();
    }
}