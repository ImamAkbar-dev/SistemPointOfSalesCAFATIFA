package ModelLogic;

public class Produk {
    private int idProduk;
    private String nama;
    private int harga;
    private int stok;
    private String kategori;

    // Constructor tanpa id (untuk insert baru)
    public Produk(String nama, int harga, int stok, String kategori) {
        this(0, nama, harga, stok, kategori);
    }

    // Constructor dengan id (untuk update atau hasil query)
    public Produk(int idProduk, String nama, int harga, int stok, String kategori) {
        this.idProduk = idProduk;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
        this.kategori = kategori;
    }

    // Getter & Setter
    public int getIdProduk() { 
        return idProduk; 
    }
    public void setIdProduk(int idProduk) { 
        this.idProduk = idProduk; 
    }
    public String getNama() { 
        return nama; 
    }
    public void setNama(String nama) { 
        this.nama = nama; 
    }
    public int getHarga() { 
        return harga; 
    }
    public void setHarga(int harga) { 
        this.harga = harga; 
    }
    public int getStok() { 
        return stok; 
    }
    public void setStok(int stok) { 
        this.stok = stok; 
    }
    public String getKategori() { 
        return kategori; 
    }
    public void setKategori(String kategori) { 
        this.kategori = kategori; 
    }
}