package floristflow.model;

/**
 * Superclass abstrak untuk merepresentasikan Produk/Item di Toko Bunga FloristFlow.
 * Menerapkan pilar OOP:
 * 1. Abstraction: Menyediakan kerangka dasar seluruh jenis barang dagangan.
 * 2. Encapsulation: Menjaga integritas data (harga modal, harga jual, kuantitas stok).
 */
public abstract class Product {
    private String code;
    private String name;
    private String category;
    private double buyPrice;
    private double sellPrice;
    private int stock;

    public Product(String code, String name, String category, double buyPrice, double sellPrice, int stock) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.buyPrice = Math.max(0, buyPrice);
        this.sellPrice = Math.max(0, sellPrice);
        this.stock = Math.max(0, stock);
    }

    /**
     * Polymorphic method: Menentukan klasifikasi jenis produk bunga/item.
     * @return jenis produk
     */
    public abstract String getItemType();

    /**
     * Menghitung potensi margin keuntungan per unit produk.
     * @return selisih harga jual dan harga beli
     */
    public double calculateProfitPerUnit() {
        return sellPrice - buyPrice;
    }

    /**
     * Menambah jumlah persediaan stok.
     * @param quantity jumlah tambahan
     */
    public void addStock(int quantity) {
        if (quantity > 0) {
            this.stock += quantity;
        }
    }

    /**
     * Mengurangi jumlah stok (misalnya saat terjadi penjualan).
     * @param quantity jumlah yang dikurangi
     * @return true jika stok mencukupi dan berhasil dikurangi, false jika stok kurang
     */
    public boolean reduceStock(int quantity) {
        if (quantity > 0 && this.stock >= quantity) {
            this.stock -= quantity;
            return true;
        }
        return false;
    }

    // --- Getter & Setter (Encapsulation) ---

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = Math.max(0, buyPrice);
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = Math.max(0, sellPrice);
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = Math.max(0, stock);
    }
}
