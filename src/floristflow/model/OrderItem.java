package floristflow.model;

/**
 * Class model untuk merepresentasikan satu baris item pesanan dalam transaksi.
 * Menerapkan prinsip Encapsulation dan perhitungan keuntungan (profit).
 */
public class OrderItem {
    private Flower flower;
    private int quantity;
    private double unitPrice;

    public OrderItem(Flower flower, int quantity) {
        this.flower = flower;
        this.quantity = Math.max(1, quantity);
        this.unitPrice = (flower != null) ? flower.getSellPrice() : 0;
    }

    public OrderItem(Flower flower, int quantity, double unitPrice) {
        this.flower = flower;
        this.quantity = Math.max(1, quantity);
        this.unitPrice = Math.max(0, unitPrice);
    }

    /**
     * Menghitung subtotal harga item (kuantiti * harga jual satuan).
     */
    public double getSubtotal() {
        return unitPrice * quantity;
    }

    /**
     * Menghitung margin keuntungan bersih dari item ini berdasarkan modal bunga.
     */
    public double calculateProfit() {
        if (flower == null) {
            return 0;
        }
        return (unitPrice - flower.getBuyPrice()) * quantity;
    }

    // --- Getter & Setter (Encapsulation) ---

    public Flower getFlower() {
        return flower;
    }

    public void setFlower(Flower flower) {
        this.flower = flower;
        if (flower != null) {
            this.unitPrice = flower.getSellPrice();
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = Math.max(0, unitPrice);
    }
}
