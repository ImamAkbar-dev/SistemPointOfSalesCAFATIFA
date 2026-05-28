package ModelLogic;

import java.sql.Timestamp;

public class Pembayaran {
    private String namaMetode;
    private Timestamp tanggalBayar;
    private int jumlahBayar;
    private Transaksi transaksi;

    public Pembayaran(String namaMetode, Timestamp tanggalBayar, int jumlahBayar) {
        this.namaMetode = namaMetode;
        this.tanggalBayar = tanggalBayar;
        this.jumlahBayar = jumlahBayar;
    }

    public int hitungKembalian(int totalHarga) {
        return this.jumlahBayar - totalHarga;
    }


    public String getNamaMetode() { 
        return namaMetode; 
    }
    public Timestamp getTanggalBayar() { 
        return tanggalBayar; 
    }
    public int getJumlahBayar() { 
        return jumlahBayar; 
    }
}