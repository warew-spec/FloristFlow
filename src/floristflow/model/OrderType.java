package floristflow.model;

/**
 * Enum untuk jenis pesanan / proses bisnis di Toko Bunga FloristFlow.
 */
public enum OrderType {
    DIRECT_PURCHASE("Beli Bunga Tangkai Langsung"),
    CUSTOM_BOUQUET("Buket Bunga Custom (Wrapping & Pita)"),
    TABLE_ARRANGEMENT("Rangkaian Bunga Vas Meja"),
    GIFT_BOX("Floral Gift Box / Hadiah");

    private final String description;

    OrderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
