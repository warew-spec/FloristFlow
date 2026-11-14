package floristflow.model;

/**
 * Subclass Admin yang mewarisi class User (Inheritance).
 * Memiliki hak akses penuh terhadap seluruh fitur FloristFlow.
 */
public class Admin extends User {

    public Admin(String username, String password, String fullName) {
        super(username, password, fullName, Role.ADMIN);
    }

    @Override
    public String getAccessDescription() {
        return "Hak Akses Administrator: Kelola Input Stock, Proses Bisnis Transaksi, & Rekapitulasi Laporan Keuangan.";
    }
}
