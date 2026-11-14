# 🌸 FloristFlow - Sistem Manajemen & Transaksi Toko Bunga
> **Tugas Besar / Praktikum Pemrograman Berorientasi Objek (PBO)**  
> **Platform:** Java (OpenJDK) & Apache NetBeans (Swing GUI)

---

## 📌 Deskripsi Proyek
**FloristFlow** adalah aplikasi bisnis manajemen toko bunga berbasis desktop yang dirancang menggunakan prinsip **Pemrograman Berorientasi Objek (OOP)** secara menyeluruh. Aplikasi ini menangani seluruh siklus bisnis toko bunga, mulai dari autentikasi pengguna, pengelolaan inventaris stok bunga potong segar, proses bisnis transaksi pemesanan buket kustom, hingga rekapitulasi laporan omzet dan keuntungan bersih.

---

## 🏛️ Penerapan Konsep Pemrograman Berorientasi Objek (OOP)

1. **Encapsulation (Enkapsulasi)**:
   - Semua atribut model (`User`, `Product`, `Flower`, `Transaction`, `OrderItem`, dll.) berstatus `private`.
   - Data diakses dan dimutasi melalui *getter* dan *setter* yang dilengkapi validasi (misal: harga dan stok tidak boleh negatif).

2. **Abstraction (Abstraksi)**:
   - `User` (`abstract class`): Menyediakan kerangka dasar pengguna sistem dengan abstract method `getAccessDescription()`.
   - `Product` (`abstract class`): Menyediakan cetak biru barang inventaris dengan abstract method `getItemType()` dan kalkulasi margin keuntungan `calculateProfitPerUnit()`.

3. **Inheritance (Pewarisan)**:
   - `Admin` dan `Cashier` mewarisi `User` (`extends User`).
   - `Flower` mewarisi `Product` (`extends Product`) dengan penambahan atribut spesifik bunga segar (`color`, `unit`).

4. **Polymorphism (Polimorfisme)**:
   - *Method Overriding* pada `getAccessDescription()` oleh subclass `Admin` dan `Cashier`.
   - *Method Overriding* pada `getItemType()` oleh subclass `Flower`.

5. **Composition & Aggregation**:
   - `Transaction` memiliki relasi komposisi dengan daftar `OrderItem`.
   - `OrderItem` mengagregasi objek `Flower`.

6. **Separation of Concerns (Arsitektur MVC Modular)**:
   - **`model`**: Entitas data (`User`, `Admin`, `Cashier`, `Role`, `Product`, `Flower`, `OrderType`, `OrderItem`, `Transaction`, `FlowerSalesSummary`).
   - **`service`**: Logika bisnis & pengelola data dalam memori dengan pola **Singleton** (`AuthService`, `StockService`, `TransactionService`, `ReportService`).
   - **`view`**: Antarmuka grafis desktop Java Swing (`LoginForm`, `MainDashboardForm`, `StockForm`, `TransactionForm`, `ReportForm`).

---

## 🚀 4 Modul Utama Aplikasi

1. **Modul 1: Halaman Login & Autentikasi (`LoginForm`)**
   - Autentikasi multi-peran (Admin & Kasir).
   - Validasi input, penanganan error, dan integrasi sesi pengguna aktif.

2. **Modul 2: Input Stock Persediaan Bunga (`StockForm`)**
   - CRUD lengkap (Tambah, Lihat, Ubah, Hapus) persediaan bunga.
   - Fitur *Auto-Generate Code* (`FL-001`, `FL-002`, dst.).
   - *Live Search* instan berdasarkan nama, kategori, atau warna bunga.
   - Fitur *Quick Restock* untuk penambahan stok cepat.
   - Kartu ringkasan total varian, total fisik bunga, dan total nilai rupiah aset toko.

3. **Modul 3: Proses Bisnis Transaksi & Kasir (`TransactionForm`)**
   - Transaksi penjualan berbagai jenis pesanan (*Beli Langsung*, *Buket Custom*, *Vas Meja*, *Gift Box*).
   - Penyesuaian jasa *wrapping paper*, pita, dan kartu ucapan (*Greeting Card*).
   - Keranjang belanja interaktif dengan proteksi stok berlebih.
   - Kalkulasi kembalian otomatis dan pemotongan stok fisik secara otomatis saat transaksi berhasil.
   - Penerbitan struk nota digital resmi.

4. **Modul 4: Rekapitulasi Laporan Finansial (`ReportForm`)**
   - 4 Kartu KPI Eksekutif: Total Omzet, Laba Bersih, Jumlah Transaksi, dan Bunga Terjual.
   - Tab Histori Transaksi lengkap dengan fitur filter jenis pesanan dan pencarian.
   - Pratinjau dan cetak ulang nota struk untuk seluruh transaksi masa lalu.
   - Tab Analisis Performa Produk & Bunga Terlaris (*Ranking Best Seller*).
   - Ekspor laporan teks eksekutif resmi ke *clipboard*.

---

## 🔑 Akun Default Pengujian

| Username | Password | Role | Hak Akses |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | **Administrator** | Akses penuh seluruh modul dan laporan manajerial |
| `kasir` | `kasir123` | **Kasir** | Akses operasional transaksi kasir dan pengecekan stok |

---

## 💻 Cara Menjalankan di Apache NetBeans

1. Buka software **Apache NetBeans**.
2. Pilih menu **File ➔ Open Project...**, lalu arahkan ke folder `FloristFlow`.
3. Tekan tombol **Run Project (F6)**, atau klik kanan pada `FloristFlow.java` lalu pilih **Run File (Shift + F6)**.
4. Masuk menggunakan akun demo di atas.
