package Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Koneksi {

    private static Connection koneksi;

    public static Connection getConnection() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/sistem_penjualan_cafatifa";
            String user = "root";
            String password = "ZNVia243165ARVin#++";

            koneksi = DriverManager.getConnection(url, user, password);

            System.out.println("Database berhasil terkoneksi!");

        } catch (Exception e) {

            System.out.println("Koneksi database gagal!");
            System.out.println(e.getMessage());

        }

        return koneksi;
    }
}