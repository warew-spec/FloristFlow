package floristflow.service;

import floristflow.model.Flower;
import floristflow.model.FlowerSalesSummary;
import floristflow.model.OrderItem;
import floristflow.model.OrderType;
import floristflow.model.Transaction;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Service class untuk memproses rekapitulasi data penjualan,
 * kalkulasi omzet, laba bersih, dan analisis performa produk terlaris.
 */
public class ReportService {
    private static ReportService instance;
    private final TransactionService transactionService = TransactionService.getInstance();
    private final StockService stockService = StockService.getInstance();

    public static synchronized ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    /**
     * Mengambil transaksi terfilter berdasarkan jenis pesanan dan kata kunci.
     */
    public List<Transaction> getFilteredTransactions(OrderType typeFilter, String searchKeyword) {
        List<Transaction> all = transactionService.getAllTransactions();
        List<Transaction> results = new ArrayList<>();

        String lowerKey = (searchKeyword != null) ? searchKeyword.trim().toLowerCase() : "";

        for (Transaction trx : all) {
            boolean matchType = (typeFilter == null || trx.getOrderType() == typeFilter);
            boolean matchKey = lowerKey.isEmpty() ||
                    trx.getTransactionId().toLowerCase().contains(lowerKey) ||
                    trx.getCustomerName().toLowerCase().contains(lowerKey) ||
                    trx.getCashierName().toLowerCase().contains(lowerKey);

            if (matchType && matchKey) {
                results.add(trx);
            }
        }
        return results;
    }

    /**
     * Mengelompokkan dan merekap penjualan per varian bunga dari seluruh histori transaksi.
     * Hasil diurutkan dari yang paling laris (jumlah terbanyak).
     */
    public List<FlowerSalesSummary> getFlowerSalesSummaries() {
        Map<String, FlowerSalesSummary> summaryMap = new HashMap<>();

        // Inisialisasi seluruh bunga dari StockService agar varian yang belum laku tetap tercatat
        for (Flower flower : stockService.getAllFlowers()) {
            summaryMap.put(flower.getCode(), new FlowerSalesSummary(flower));
        }

        // Agregasi seluruh item dari transaksi yang tercatat
        for (Transaction trx : transactionService.getAllTransactions()) {
            for (OrderItem item : trx.getItems()) {
                Flower f = item.getFlower();
                if (f != null) {
                    FlowerSalesSummary summary = summaryMap.get(f.getCode());
                    if (summary == null) {
                        summary = new FlowerSalesSummary(f);
                        summaryMap.put(f.getCode(), summary);
                    }
                    summary.addSale(item.getQuantity(), item.getSubtotal(), item.calculateProfit());
                }
            }
        }

        List<FlowerSalesSummary> list = new ArrayList<>(summaryMap.values());
        // Sort descending by totalSold
        list.sort((a, b) -> Integer.compare(b.getTotalSold(), a.getTotalSold()));
        return list;
    }

    /**
     * Menghasilkan teks laporan resmi eksekutif FloristFlow.
     */
    public String generateExecutiveReportText() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("id-ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("Rp #,###", symbols);

        StringBuilder sb = new StringBuilder();
        sb.append("======================================================================\n");
        sb.append("                  LAPORAN REKAPITULASI PENJUALAN & OMZET              \n");
        sb.append("                        TOKO BUNGA FLORISTFLOW                        \n");
        sb.append("======================================================================\n");
        sb.append(String.format("Waktu Cetak Laporan : %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
        sb.append("----------------------------------------------------------------------\n");
        sb.append("RINGKASAN KINERJA KEUANGAN (EXECUTIVE FINANCIAL SUMMARY):\n");
        sb.append(String.format("• Total Transaksi Selesai : %d Transaksi\n", transactionService.getTotalTransactionsCount()));
        sb.append(String.format("• Total Bunga Terjual     : %d Tangkai/Item\n", transactionService.getTotalFlowersSold()));
        sb.append(String.format("• Total Omzet Kotor       : %s\n", df.format(transactionService.getTotalGrossRevenue())));
        sb.append(String.format("• Total Keuntungan Bersih : %s\n", df.format(transactionService.getTotalNetProfit())));
        sb.append(String.format("• Nilai Aset Stok Bunga   : %s (%d Tangkai)\n",
                df.format(stockService.getTotalStockAssetValue()), stockService.getTotalStockCount()));
        sb.append("----------------------------------------------------------------------\n");
        sb.append("REKAP PERFORMA PENJUALAN BUNGA TERLARIS (TOP SALES):\n");
        sb.append(String.format("%-8s %-25s %-12s %-15s %-15s\n", "Kode", "Nama Bunga", "Terjual", "Omzet", "Laba Bersih"));
        sb.append("----------------------------------------------------------------------\n");

        for (FlowerSalesSummary s : getFlowerSalesSummaries()) {
            if (s.getTotalSold() > 0) {
                sb.append(String.format("%-8s %-25s %-12s %-15s %-15s\n",
                        s.getFlower().getCode(),
                        s.getFlower().getName(),
                        s.getTotalSold() + " " + s.getFlower().getUnit(),
                        df.format(s.getTotalRevenue()),
                        df.format(s.getTotalProfit())));
            }
        }

        sb.append("----------------------------------------------------------------------\n");
        sb.append("HISTORI TRANSAKSI:\n");
        sb.append(String.format("%-18s %-16s %-18s %-15s %-12s\n", "No. Transaksi", "Tanggal", "Pelanggan", "Jenis", "Total Bayar"));
        sb.append("----------------------------------------------------------------------\n");

        for (Transaction trx : transactionService.getAllTransactions()) {
            String cust = trx.getCustomerName();
            if (cust.length() > 16) cust = cust.substring(0, 14) + "..";
            sb.append(String.format("%-18s %-16s %-18s %-15s %-12s\n",
                    trx.getTransactionId(),
                    trx.getFormattedDateTime().substring(0, 10),
                    cust,
                    trx.getOrderType().name(),
                    df.format(trx.calculateGrandTotal())));
        }

        sb.append("======================================================================\n");
        sb.append("              Sistem Manajemen FloristFlow - Laporan Sah              \n");
        sb.append("======================================================================\n");

        return sb.toString();
    }
}
