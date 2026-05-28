Cara Menjalankan Program : 
1. buka VS-Code
2. Import file DatabaseCAFATIFA.sql
3. Buka file Database/Koneksi.java
4. Sesuaikan user dan password database-mu
5. Sesuaikan juga nama database jika sudah di-import
6. Pergi Ke terminal VScode kemudian ketik directori file. Contoh : cd C:\PenjualanCAFATIFA
7. Ketik pada terminal di VScode javac -cp ".;lib\mysql-connector-j-9.6.0.jar;lib\jfreechart-1.5.6.jar" -d . Database\*.java ModelLogic\*.java UI\*.java UI\Admin\*.java UI\Kasir\*.java Main\*.java
8. Jika sudah maka lanjutkan ketik ini kemudian enter java -cp ".;lib\mysql-connector-j-9.6.0.jar;lib\jfreechart-1.5.6.jar" Main.Main