package floristflow.model;

/**
 * Model untuk merepresentasikan rekapitulasi data penjualan per varian bunga.
 * Digunakan untuk analisis produk terlaris (Best Seller) dan performa omzet.
 */
public class FlowerSalesSummary {
    private final Flower flower;
    private int totalSold;
    private double totalRevenue;
    private double totalProfit;

    public FlowerSalesSummary(Flower flower) {
        this.flower = flower;
        this.totalSold = 0;
        this.totalRevenue = 0;
        this.totalProfit = 0;
    }

    public void addSale(int quantity, double revenue, double profit) {
        this.totalSold += quantity;
        this.totalRevenue += revenue;
        this.totalProfit += profit;
    }

    public Flower getFlower() {
        return flower;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public double getTotalProfit() {
        return totalProfit;
    }
}
