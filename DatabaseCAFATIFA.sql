CREATE DATABASE sistem_penjualan_cafatifa;
USE sistem_penjualan_cafatifa;

CREATE TABLE `users` (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('admin','kasir') NOT NULL,
  PRIMARY KEY (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `produk` (
  `id_produk` int NOT NULL AUTO_INCREMENT,
  `nama_produk` varchar(100) NOT NULL,
  `harga` int NOT NULL,
  `stok` int NOT NULL,
  `kategori` varchar(50) NOT NULL,
  PRIMARY KEY (`id_produk`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transaksi` (
  `id_transaksi` int NOT NULL AUTO_INCREMENT,
  `tanggal` datetime DEFAULT CURRENT_TIMESTAMP,
  `total` int NOT NULL,
  `status` enum('diproses','selesai') DEFAULT 'diproses',
  `id_user` int DEFAULT NULL,
  PRIMARY KEY (`id_transaksi`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `detail_transaksi` (
  `id_detail` int NOT NULL AUTO_INCREMENT,
  `id_transaksi` int DEFAULT NULL,
  `id_produk` int DEFAULT NULL,
  `jumlah` int NOT NULL,
  `harga_satuan` int NOT NULL,
  `subtotal` int NOT NULL,
  PRIMARY KEY (`id_detail`),
  KEY `id_transaksi` (`id_transaksi`),
  KEY `id_produk` (`id_produk`),
  CONSTRAINT `detail_transaksi_ibfk_1` FOREIGN KEY (`id_transaksi`) REFERENCES `transaksi` (`id_transaksi`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `detail_transaksi_ibfk_2` FOREIGN KEY (`id_produk`) REFERENCES `produk` (`id_produk`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `bukti_pembayaran` (
  `id_bukti` int NOT NULL AUTO_INCREMENT,
  `id_transaksi` int DEFAULT NULL,
  `metode_pembayaran` varchar(50) DEFAULT NULL,
  `tanggal_bayar` datetime DEFAULT CURRENT_TIMESTAMP,
  `jumlah_bayar` int DEFAULT NULL,
  `status` enum('pending','berhasil','gagal') DEFAULT 'pending',
  PRIMARY KEY (`id_bukti`),
  UNIQUE KEY `id_transaksi` (`id_transaksi`),
  CONSTRAINT `bukti_pembayaran_ibfk_1` FOREIGN KEY (`id_transaksi`) REFERENCES `transaksi` (`id_transaksi`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DELIMITER $$

CREATE TRIGGER cek_stok
BEFORE INSERT ON detail_transaksi
FOR EACH ROW
BEGIN
    DECLARE sisa INT;

    SELECT stok INTO sisa
    FROM produk
    WHERE id_produk = NEW.id_produk;

    IF sisa IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Produk tidak ditemukan';
    END IF;
    
    IF sisa < NEW.jumlah THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stok tidak cukup';
    END IF;
END$$

CREATE TRIGGER kurangi_stok
AFTER INSERT ON detail_transaksi
FOR EACH ROW
BEGIN
    UPDATE produk
    SET stok = stok - NEW.jumlah
    WHERE id_produk = NEW.id_produk;
END$$

CREATE TRIGGER update_stok
AFTER UPDATE ON detail_transaksi
FOR EACH ROW
BEGIN
    UPDATE produk
    SET stok = stok + OLD.jumlah
    WHERE id_produk = OLD.id_produk;
    
    UPDATE produk
    SET stok = stok - NEW.jumlah
    WHERE id_produk = NEW.id_produk;
END$$

CREATE TRIGGER delete_stok
AFTER DELETE ON detail_transaksi
FOR EACH ROW
BEGIN
    UPDATE produk
    SET stok = stok + OLD.jumlah
    WHERE id_produk = OLD.id_produk;
END$$

DELIMITER ;

INSERT INTO users (username, password, role)
VALUES
('admin', 'admin123', 'admin'),
('kasir1', 'kasir123', 'kasir');

INSERT INTO produk (nama_produk, harga, stok, kategori) VALUES
('Baju CAFATIFA', 120000, 20, 'Merchandise'),
('Gantungan Kunci', 10000, 50, 'Merchandise'),
('Stiker Logo', 5000, 100, 'Merchandise'),
('Snack Box', 15000, 30, 'Makanan');

INSERT INTO transaksi (total, id_user, status) VALUES
(0, 2, 'diproses');

INSERT INTO detail_transaksi (id_transaksi, id_produk, jumlah, harga_satuan, subtotal) VALUES
(1, 1, 1, 120000, 120000),
(1, 2, 1, 10000, 10000);

UPDATE transaksi
SET total = (
    SELECT SUM(subtotal)
    FROM detail_transaksi
    WHERE id_transaksi = 1
)
WHERE id_transaksi = 1;

UPDATE detail_transaksi
SET jumlah = 2, subtotal = 240000
WHERE id_detail = 1;

DELETE FROM detail_transaksi
WHERE id_detail = 2;

INSERT INTO bukti_pembayaran
(id_transaksi, metode_pembayaran, jumlah_bayar, status)
VALUES
(1, 'BCA', 240000, 'berhasil');

UPDATE produk
SET stok = stok - 1
WHERE id_produk = 1;

UPDATE produk
SET stok = stok - 1
WHERE id_produk = 2;