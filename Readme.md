# Aplikasi Penjualan CAFATIFA

## Anggota Kelompok
1. Devano Gustira Akbar	D1041241047
2. Imam Akbar Arbain	D1041241013
3. Jhon Miranto		D1041241055
4. Kevin Yakhin Samosir	D1041231021

## Penjelasan Projek
Aplikasi Penjualan CAFATIFA adalah sistem informasi *Point of Sales* (POS) berbasis desktop yang dirancang khusus untuk mendigitalisasi dan mengoptimalkan pengelolaan transaksi pada Organisasi CAFATIFA Universitas Tanjungpura (UNTAN), Pontianak. Aplikasi ini dibangun menggunakan bahasa pemrograman **Java** dengan **MySQL** sebagai media penyimpanan data (*database*), serta didukung oleh pustaka *JFreeChart* untuk visualisasi grafik data penjualan dan pendapatan.

Sebelum sistem ini diimplementasikan, proses pencatatan transaksi, manajemen stok barang (seperti baju, gantungan kunci, stiker, dan makanan), serta penyusunan laporan keuangan pada organisasi masih dilakukan secara manual. Hal tersebut berpotensi memicu terjadinya kesalahan pencatatan, ketidaksesuaian jumlah stok, hingga risiko kehilangan berkas transaksi. Aplikasi ini hadir sebagai solusi digital untuk membuat proses operasional penjualan menjadi lebih cepat, akurat, efektif, dan efisien.

### Fitur Utama Aplikasi
Aplikasi ini membagi hak akses pengguna ke dalam 2 tingkatan (*role*) melalui pembatasan menu fungsional yang spesifik:

1. **Aktor Kasir (Manajemen Transaksi):**
   * **Login Sistem:** Mengakses antarmuka kasir secara aman menggunakan kredensial akun yang terdaftar.
   * **Kelola Transaksi:** Memilih katalog produk, menginput jumlah pesanan, dan menghitung total harga belanjaan konsumen secara otomatis melalui sistem.
   * **Proses Pembayaran:** Menginput nominal pembayaran dari konsumen, menghitung uang kembalian secara tepat, merubah status transaksi menjadi selesai, serta melakukan cetak struk transaksi.
   * **Melihat Riwayat Transaksi:** Mengakses daftar pesanan yang telah selesai dikerjakan beserta rincian detailnya (tanggal, total harga, bayar, kembalian, dan rincian item produk).

2. **Aktor Admin (Manajemen Data & Laporan):**
   * **Login Sistem:** Mengakses antarmuka administratif utama.
   * **Mengelola Produk (CRUD):** Menambah data produk baru sesuai format, mengubah informasi produk, memperbarui jumlah stok, serta menghapus data produk dari katalog penjualan.
   * **Melihat Riwayat Transaksi:** Memantau semua transaksi masuk dari kasir yang sudah berstatus selesai.
   * **Menu Laporan & Grafik Pendapatan:** Menampilkan akumulasi total penjualan, jumlah transaksi, dan total produk terjual secara *real-time* berbasis periode waktu tertentu, lengkap dengan visualisasi grafik tren pendapatan organisasi.

### Penerapan Pilar Pemrograman Berorientasi Objek (OOP)
Sebagai bentuk pemenuhan standar tugas Pemrograman Berorientasi Objek, sistem ini dirancang dengan mengimplementasikan pilar-pilar utama OOP sebagai berikut:
* **Abstraction:** Diterapkan pada kelas induk `User` melalui metode `showDashboard()` untuk menyembunyikan detail logika sistem dan hanya mengeksekusi fungsi antarmuka yang relevan.
* **Polymorphism:** Menggunakan *method overriding* pada `showDashboard()`, di mana objek dengan *role* Admin akan diarahkan ke antarmuka laporan, sedangkan *role* Kasir akan diarahkan ke antarmuka pesanan masuk.
* **Inheritance (Pewarisan):** Kelas `User` bertindak sebagai *superclass* (induk) yang menurunkan atribut serta metodenya ke kelas `Admin` dan `Kasir` sebagai *subclass* (anak) guna meminimalisir pengulangan kode (*code redundancy*).
* **Encapsulation:** Melindungi data sensitif di dalam objek dengan menyetel *access modifier* setiap atribut menjadi `private`, yang kemudian hanya dapat diakses atau diubah menggunakan metode *getter* dan *setter*.
* **Konsep Tambahan:** Dilengkapi dengan metode *Constructor* untuk inisialisasi awal objek, variabel *Static* pada akumulasi kelas `Transaksi`, pengelompokan item belanjaan menggunakan *Array*, serta *Error Handling* berbasis pop-up pesan error untuk mencegah terjadinya kegagalan sistem akibat salah input data.

## Cara Menjalankan Program 
1. buka VS-Code
2. Import file DatabaseCAFATIFA.sql
3. Buka file Database/Koneksi.java
4. Sesuaikan user dan password database-mu
5. Sesuaikan juga nama database jika sudah di-import
6. Pergi Ke terminal VScode kemudian ketik directori file. Contoh : cd C:\PenjualanCAFATIFA
7. Ketik pada terminal di VScode javac -cp ".;lib\mysql-connector-j-9.6.0.jar;lib\jfreechart-1.5.6.jar" -d . Database\*.java ModelLogic\*.java UI\*.java UI\Admin\*.java UI\Kasir\*.java Main\*.java
8. Jika sudah maka lanjutkan ketik ini kemudian enter java -cp ".;lib\mysql-connector-j-9.6.0.jar;lib\jfreechart-1.5.6.jar" Main.Main
9. Jika ingin Mengedit Program maka ketik Get-ChildItem -Recurse -Filter *.class | Remove-Item. Kemudian ulangi lanhkah 7 dan 8.

## Kredensial Login
1. Admin
   * Username : admin
   * Password : admin123
3. Kasir
   * Username : kasir1
   * Password : kasir123
