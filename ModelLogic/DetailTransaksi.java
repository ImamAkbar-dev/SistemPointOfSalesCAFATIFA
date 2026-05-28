/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ModelLogic;


public class DetailTransaksi {
    private int jumlah;
    private Produk produk;
    
    DetailTransaksi(Produk produk, int jumlah){
        this.produk = produk;
        this.jumlah = jumlah;
        this.produk.setStok(this.produk.getStok()-jumlah);
    }

    public int getJumlah() {
        return jumlah;
    }
    
    public int hitungSubTotal(){
        return this.jumlah * this.produk.getHarga();
    }
}
