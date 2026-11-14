package floristflow.service;

import floristflow.model.Flower;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class untuk mengelola stok persediaan bunga (Inventory Management).
 * Menyediakan fungsionalitas CRUD, pencarian, dan kalkulasi nilai persediaan.
 * Menerapkan pola Singleton.
 */
public class StockService {
    private static StockService instance;
    private final Map<String, Flower> stockDatabase = new LinkedHashMap<>();

    public StockService() {
        seedInitialFlowers();
    }

    public static synchronized StockService getInstance() {
        if (instance == null) {
            instance = new StockService();
        }
        return instance;
    }

    /**
     * Data awal bibit / katalog bunga segar untuk demo FloristFlow.
     */
    private void seedInitialFlowers() {
        addFlower(new Flower("FL-001", "Mawar Merah Holland", "Mawar", "Merah Marun", 8000, 15000, 50, "Tangkai"));
        addFlower(new Flower("FL-002", "Mawar Putih Avalanche", "Mawar", "Putih Bersih", 8000, 15000, 35, "Tangkai"));
        addFlower(new Flower("FL-003", "Lily Casablanca", "Lily", "Putih", 20000, 35000, 20, "Tangkai"));
        addFlower(new Flower("FL-004", "Tulip Strong Gold", "Tulip", "Kuning", 25000, 45000, 15, "Tangkai"));
        addFlower(new Flower("FL-005", "Baby's Breath", "Filler", "Putih Salju", 12000, 22000, 30, "Ikat"));
        addFlower(new Flower("FL-006", "Bunga Matahari (Sunflower)", "Sunflower", "Kuning Emas", 10000, 20000, 25, "Tangkai"));
        addFlower(new Flower("FL-007", "Anyelir Pink Pastel", "Carnation", "Soft Pink", 6000, 12000, 40, "Tangkai"));
        addFlower(new Flower("FL-008", "Hydrangea Biru Premium", "Hydrangea", "Sky Blue", 35000, 60000, 8, "Tangkai"));
    }

    /**
     * Tambah data bunga baru ke sistem (Create).
     * @param flower objek Flower
     * @return true jika berhasil, false jika kode sudah ada
     */
    public boolean addFlower(Flower flower) {
        if (flower == null || flower.getCode() == null || flower.getCode().trim().isEmpty()) {
            return false;
        }
        String key = flower.getCode().trim().toUpperCase();
        if (stockDatabase.containsKey(key)) {
            return false;
        }
        flower.setCode(key);
        stockDatabase.put(key, flower);
        return true;
    }

    /**
     * Ubah/Update data bunga (Update).
     * @param flower data baru dengan kode yang sama
     * @return true jika berhasil, false jika data tidak ditemukan
     */
    public boolean updateFlower(Flower flower) {
        if (flower == null || flower.getCode() == null) {
            return false;
        }
        String key = flower.getCode().trim().toUpperCase();
        if (!stockDatabase.containsKey(key)) {
            return false;
        }
        stockDatabase.put(key, flower);
        return true;
    }

    /**
     * Hapus data bunga berdasarkan kode (Delete).
     * @param code kode unik bunga
     * @return true jika berhasil dihapus, false jika kode tidak ada
     */
    public boolean deleteFlower(String code) {
        if (code == null) {
            return false;
        }
        String key = code.trim().toUpperCase();
        return stockDatabase.remove(key) != null;
    }

    /**
     * Ambil data bunga berdasarkan kode (Read by ID).
     */
    public Flower getFlowerByCode(String code) {
        if (code == null) {
            return null;
        }
        return stockDatabase.get(code.trim().toUpperCase());
    }

    /**
     * Ambil seluruh koleksi bunga (Read All).
     */
    public Collection<Flower> getAllFlowers() {
        return stockDatabase.values();
    }

    /**
     * Cari bunga berdasarkan keyword (nama, kode, kategori, atau warna).
     * @param keyword kata kunci pencarian
     * @return daftar bunga yang cocok
     */
    public List<Flower> searchFlowers(String keyword) {
        List<Flower> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            results.addAll(stockDatabase.values());
            return results;
        }

        String lowerKey = keyword.trim().toLowerCase();
        for (Flower f : stockDatabase.values()) {
            if (f.getCode().toLowerCase().contains(lowerKey) ||
                f.getName().toLowerCase().contains(lowerKey) ||
                f.getCategory().toLowerCase().contains(lowerKey) ||
                f.getColor().toLowerCase().contains(lowerKey)) {
                results.add(f);
            }
        }
        return results;
    }

    /**
     * Restock cepat untuk menambah kuantitas bunga yang ada.
     * @param code kode bunga
     * @param additionalQty jumlah tambahan
     * @return true jika berhasil
     */
    public boolean restockFlower(String code, int additionalQty) {
        Flower flower = getFlowerByCode(code);
        if (flower != null && additionalQty > 0) {
            flower.addStock(additionalQty);
            return true;
        }
        return false;
    }

    /**
     * Generate otomatis kode berikutnya, misal: FL-009.
     */
    public String generateNextCode() {
        int maxId = 0;
        for (String code : stockDatabase.keySet()) {
            if (code.startsWith("FL-")) {
                try {
                    int num = Integer.parseInt(code.substring(3).trim());
                    if (num > maxId) {
                        maxId = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("FL-%03d", maxId + 1);
    }

    /**
     * Menghitung total varian/jenis bunga.
     */
    public int getTotalFlowerTypes() {
        return stockDatabase.size();
    }

    /**
     * Menghitung total seluruh tangkai bunga fisik dalam persediaan.
     */
    public int getTotalStockCount() {
        int total = 0;
        for (Flower f : stockDatabase.values()) {
            total += f.getStock();
        }
        return total;
    }

    /**
     * Menghitung total nilai aset persediaan (stok * harga modal).
     */
    public double getTotalStockAssetValue() {
        double total = 0;
        for (Flower f : stockDatabase.values()) {
            total += (f.getStock() * f.getBuyPrice());
        }
        return total;
    }
}
