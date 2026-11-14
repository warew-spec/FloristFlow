package floristflow.model;

/**
 * Subclass Flower yang mewarisi class Product (Inheritance).
 * Merepresentasikan komoditas bunga potong segar (fresh cut flowers).
 */
public class Flower extends Product {
    private String color;
    private String unit;

    public Flower(String code, String name, String category, String color, double buyPrice, double sellPrice, int stock, String unit) {
        super(code, name, category, buyPrice, sellPrice, stock);
        this.color = color;
        this.unit = (unit == null || unit.trim().isEmpty()) ? "Tangkai" : unit;
    }

    public Flower(String code, String name, String category, String color, double buyPrice, double sellPrice, int stock) {
        this(code, name, category, color, buyPrice, sellPrice, stock, "Tangkai");
    }

    @Override
    public String getItemType() {
        return "Bunga Segar (" + unit + ")";
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
