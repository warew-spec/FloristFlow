package floristflow.service;

import floristflow.model.Admin;
import floristflow.model.Cashier;
import floristflow.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class untuk mengelola autentikasi, pendaftaran akun, dan session pengguna.
 * Menerapkan pola Singleton agar state login konsisten di seluruh form aplikasi.
 */
public class AuthService {
    private static AuthService instance;
    private final Map<String, User> userDatabase = new HashMap<>();
    private User currentUser;

    public AuthService() {
        initDefaultAccounts();
    }

    /**
     * Singleton instance provider.
     */
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Inisialisasi akun bawaan untuk pengujian sistem FloristFlow.
     */
    private void initDefaultAccounts() {
        registerUser(new Admin("admin", "admin123", "Dewi Florist (Manager)"));
        registerUser(new Cashier("kasir", "kasir123", "Siti Kasir (Frontdesk)"));
    }

    /**
     * Mendaftarkan pengguna baru ke database memory.
     * @param user objek pengguna
     * @return true jika berhasil, false jika username sudah digunakan
     */
    public boolean registerUser(User user) {
        if (user == null || user.getUsername() == null) {
            return false;
        }
        String key = user.getUsername().trim().toLowerCase();
        if (userDatabase.containsKey(key)) {
            return false;
        }
        userDatabase.put(key, user);
        return true;
    }

    /**
     * Proses autentikasi login pengguna.
     * @param username input username
     * @param password input password
     * @return true jika kredensial cocok, false jika gagal
     */
    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        String key = username.trim().toLowerCase();
        User user = userDatabase.get(key);
        
        if (user != null && user.validatePassword(password)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * Keluar dari sesi pengguna saat ini.
     */
    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public Collection<User> getAllUsers() {
        return userDatabase.values();
    }
}
