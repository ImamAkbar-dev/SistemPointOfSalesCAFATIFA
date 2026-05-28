package ModelLogic;

import java.util.*;

public class Transaksi {
    private int idTransaksi;
    private Date tanggalTransaksi;
    private List<DetailTransaksi> detailTransaksi;
    private Pembayaran pembayaran;
    private Kasir kasir;
    private int totalHarga;

    public static float totalPemasukan;
    public static int totalTransaksi;
    public static int totalProdukTerjual;

    // Constructor 
    public Transaksi(Kasir kasir) {
        this.kasir = kasir;
        this.tanggalTransaksi = new Date();
        this.detailTransaksi = new ArrayList<>();
        this.totalHarga = 0;
    }

    public void addDetail(DetailTransaksi detail) {
        detailTransaksi.add(detail);
        //totalProdukTerjual += detail.getJumlah();
    }

    public int getTotalHarga() {
        return totalHarga;
    }

    public int hitungTotalBiaya() {
        int total = 0;
        for (DetailTransaksi d : detailTransaksi) {
            total += d.hitungSubTotal();
        }
        totalHarga = total;
        //totalPemasukan += total;
        return totalHarga;
    }
}