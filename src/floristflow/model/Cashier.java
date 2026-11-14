package floristflow.model;

/**
 * Subclass Cashier yang mewarisi class User (Inheritance).
 * Memiliki hak akses khusus operasional kasir toko bunga.
 */
public class Cashier extends User {

    public Cashier(String username, String password, String fullName) {
        super(username, password, fullName, Role.CASHIER);
    }

    @Override
    public String getAccessDescription() {
        return "Hak Akses Kasir: Akses Proses Bisnis Transaksi Penjualan & Cek Ketersediaan Stok Bunga.";
    }
}
