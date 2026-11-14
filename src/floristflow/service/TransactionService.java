package floristflow.service;

import floristflow.model.Flower;
import floristflow.model.OrderItem;
import floristflow.model.OrderType;
import floristflow.model.Transaction;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class untuk mengelola alur proses bisnis transaksi penjualan,
 * integrasi pengurangan stok persediaan bunga, serta penyimpanan riwayat transaksi.
 * Menerapkan pola Singleton.
 */
public class TransactionService {
    private static TransactionService instance;
    private final List<Transaction> transactionHistory = new ArrayList<>();
    private final StockService stockService = StockService.getInstance();
    private int transactionCounter = 1;

    public TransactionService() {
        seedInitialTransactions();
    }

    public static synchronized TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
    }

    /**
     * Data awal contoh transaksi untuk demo proses bisnis dan modul rekapitulasi.
     */
    private void seedInitialTransactions() {
        Flower roseRed = stockService.getFlowerByCode("FL-001");
        Flower lily = stockService.getFlowerByCode("FL-003");
        Flower babyBreath = stockService.getFlowerByCode("FL-005");

        if (roseRed != null && babyBreath != null) {
            Transaction trx1 = new Transaction(generateNextTransactionId(), "Anisa Rahma", "081234567890", OrderType.CUSTOM_BOUQUET, "Siti Kasir");
            trx1.setDateTime(LocalDateTime.now().minusHours(4));
            trx1.addItem(new OrderItem(roseRed, 10)); // 10 tangkai mawar
            trx1.addItem(new OrderItem(babyBreath, 2)); // 2 ikat baby breath
            trx1.setWrappingFee(35000); // Kertas wrapping premium & pita
            trx1.setGreetingCard("Happy Graduation Sahabatku!");
            trx1.setDiscount(10000);
            trx1.setCashPaid(250000);
            transactionHistory.add(trx1);
        }

        if (lily != null) {
            Transaction trx2 = new Transaction(generateNextTransactionId(), "Budi Santoso", "085678901234", OrderType.DIRECT_PURCHASE, "Dewi Florist");
            trx2.setDateTime(LocalDateTime.now().minusHours(2));
            trx2.addItem(new OrderItem(lily, 3)); // 3 tangkai lily
            trx2.setCashPaid(150000);
            transactionHistory.add(trx2);
        }
    }

    /**
     * Menghasilkan ID Transaksi unik berurutan: TRX-YYYYMMDD-XXX.
     */
    public synchronized String generateNextTransactionId() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String id = String.format("TRX-%s-%03d", datePart, transactionCounter++);
        return id;
    }

    /**
     * Memproses transaksi penjualan:
     * 1. Validasi keranjang belanja tidak kosong.
     * 2. Validasi kecukupan pembayaran.
     * 3. Validasi dan pemotongan stok fisik bunga dari StockService.
     * 4. Penyimpanan ke histori transaksi.
     *
     * @param transaction objek transaksi yang akan diproses
     * @return pesan hasil pemrosesan (null jika sukses, pesan kesalahan jika gagal)
     */
    public synchronized String processTransaction(Transaction transaction) {
        if (transaction == null || transaction.getItems().isEmpty()) {
            return "Keranjang pesanan masih kosong! Harap tambahkan bunga terlebih dahulu.";
        }

        double grandTotal = transaction.calculateGrandTotal();
        if (transaction.getCashPaid() < grandTotal) {
            return "Uang yang diserahkan kurang! Total bayar: Rp " + (long) grandTotal +
                   ", Uang diterima: Rp " + (long) transaction.getCashPaid();
        }

        // Cek ketersediaan stok seluruh bunga terlebih dahulu
        for (OrderItem item : transaction.getItems()) {
            Flower flower = item.getFlower();
            if (flower == null) {
                return "Data bunga tidak valid!";
            }
            Flower stockFlower = stockService.getFlowerByCode(flower.getCode());
            if (stockFlower == null || stockFlower.getStock() < item.getQuantity()) {
                int available = (stockFlower != null) ? stockFlower.getStock() : 0;
                return "Stok untuk bunga '" + flower.getName() + "' tidak mencukupi!\n" +
                       "Diminta: " + item.getQuantity() + ", Tersedia: " + available;
            }
        }

        // Potong stok secara otomatis di StockService
        for (OrderItem item : transaction.getItems()) {
            stockService.getFlowerByCode(item.getFlower().getCode()).reduceStock(item.getQuantity());
        }

        // Simpan ke riwayat transaksi
        transactionHistory.add(transaction);
        return null; // Sukses tanpa error
    }

    public List<Transaction> getAllTransactions() {
        return Collections.unmodifiableList(transactionHistory);
    }

    public int getTotalTransactionsCount() {
        return transactionHistory.size();
    }

    public double getTotalGrossRevenue() {
        double total = 0;
        for (Transaction trx : transactionHistory) {
            total += trx.calculateGrandTotal();
        }
        return total;
    }

    public double getTotalNetProfit() {
        double total = 0;
        for (Transaction trx : transactionHistory) {
            total += trx.calculateTotalProfit();
        }
        return total;
    }

    public int getTotalFlowersSold() {
        int total = 0;
        for (Transaction trx : transactionHistory) {
            total += trx.getTotalQuantity();
        }
        return total;
    }
}
