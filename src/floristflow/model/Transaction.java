package floristflow.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class model untuk merepresentasikan satu transaksi utuh dalam proses bisnis FloristFlow.
 * Mencakup identitas pemesan, daftar item bunga, biaya jasa buket, diskon, pembayaran, dan kalkulasi profit.
 */
public class Transaction {
    private String transactionId;
    private LocalDateTime dateTime;
    private String customerName;
    private String customerPhone;
    private OrderType orderType;
    private String greetingCard;
    private List<OrderItem> items;
    private double wrappingFee;
    private double discount;
    private double cashPaid;
    private String cashierName;

    public Transaction(String transactionId, String customerName, String customerPhone, OrderType orderType, String cashierName) {
        this.transactionId = transactionId;
        this.dateTime = LocalDateTime.now();
        this.customerName = (customerName == null || customerName.trim().isEmpty()) ? "Pelanggan Umum" : customerName;
        this.customerPhone = (customerPhone == null) ? "-" : customerPhone;
        this.orderType = (orderType != null) ? orderType : OrderType.DIRECT_PURCHASE;
        this.greetingCard = "-";
        this.items = new ArrayList<>();
        this.wrappingFee = 0;
        this.discount = 0;
        this.cashPaid = 0;
        this.cashierName = (cashierName == null) ? "Kasir" : cashierName;
    }

    public void addItem(OrderItem item) {
        if (item != null) {
            this.items.add(item);
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            this.items.remove(index);
        }
    }

    public void clearItems() {
        this.items.clear();
    }

    /**
     * Menghitung total harga belanja bunga sebelum biaya jasa dan diskon.
     */
    public double calculateItemsSubtotal() {
        double subtotal = 0;
        for (OrderItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    /**
     * Menghitung Grand Total yang harus dibayar pembeli.
     */
    public double calculateGrandTotal() {
        double total = calculateItemsSubtotal() + wrappingFee - discount;
        return Math.max(0, total);
    }

    /**
     * Menghitung jumlah uang kembalian pembeli.
     */
    public double calculateChange() {
        return Math.max(0, cashPaid - calculateGrandTotal());
    }

    /**
     * Menghitung total keuntungan bersih dari transaksi ini (laba bunga + biaya jasa buket - diskon).
     */
    public double calculateTotalProfit() {
        double itemsProfit = 0;
        for (OrderItem item : items) {
            itemsProfit += item.calculateProfit();
        }
        double totalProfit = itemsProfit + wrappingFee - discount;
        return Math.max(0, totalProfit);
    }

    /**
     * Menghitung total tangkai/item bunga yang terbeli.
     */
    public int getTotalQuantity() {
        int total = 0;
        for (OrderItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return (dateTime != null) ? dateTime.format(formatter) : "-";
    }

    // --- Getter & Setter (Encapsulation) ---

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getGreetingCard() {
        return greetingCard;
    }

    public void setGreetingCard(String greetingCard) {
        this.greetingCard = greetingCard;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getWrappingFee() {
        return wrappingFee;
    }

    public void setWrappingFee(double wrappingFee) {
        this.wrappingFee = Math.max(0, wrappingFee);
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = Math.max(0, discount);
    }

    public double getCashPaid() {
        return cashPaid;
    }

    public void setCashPaid(double cashPaid) {
        this.cashPaid = Math.max(0, cashPaid);
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }
}
