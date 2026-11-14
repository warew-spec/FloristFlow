package floristflow.view;

import floristflow.model.Flower;
import floristflow.model.FlowerSalesSummary;
import floristflow.model.OrderItem;
import floristflow.model.OrderType;
import floristflow.model.Transaction;
import floristflow.service.ReportService;
import floristflow.service.TransactionService;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Form GUI untuk Modul Rekapitulasi (Laporan Keuangan, Omzet, Histori Transaksi,
 * dan Analisis Bunga Terlaris).
 */
public class ReportForm extends JFrame {

    private final ReportService reportService = ReportService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();
    private final DecimalFormat currencyFormat;

    // KPI Summary Labels
    private JLabel lblKpiRevenue;
    private JLabel lblKpiProfit;
    private JLabel lblKpiTrxCount;
    private JLabel lblKpiFlowersSold;

    // Komponen Tab 1: Riwayat Transaksi
    private JComboBox<String> cmbFilterType;
    private JTextField txtSearchTrx;
    private JTable tblTransactions;
    private DefaultTableModel trxTableModel;
    private JButton btnViewReceipt;

    // Komponen Tab 2: Rekap Performa Bunga
    private JTable tblFlowerSales;
    private DefaultTableModel flowerSalesModel;

    // Skema Warna FloristFlow
    private static final Color PRIMARY_COLOR = new Color(46, 117, 89);     // Forest Green
    private static final Color SECONDARY_COLOR = new Color(214, 90, 117);  // Soft Rose
    private static final Color ACCENT_BLUE = new Color(41, 128, 185);
    private static final Color ACCENT_GOLD = new Color(212, 160, 23);
    private static final Color BG_COLOR = new Color(245, 247, 248);
    private static final Color TEXT_DARK = new Color(40, 44, 52);

    public ReportForm() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("id-ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        this.currencyFormat = new DecimalFormat("Rp #,###", symbols);

        initComponents();
        refreshAllData();
    }

    private void initComponents() {
        setTitle("FloristFlow - Rekapitulasi Laporan Keuangan & Penjualan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1120, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setBorder(new EmptyBorder(12, 25, 12, 25));

        JLabel lblTitle = new JLabel("📊 FloristFlow - Rekapitulasi & Laporan Keuangan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);

        JButton btnExport = new JButton("🖨️ Cetak / Ekspor Laporan");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExport.setBackground(ACCENT_GOLD);
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener((ActionEvent e) -> showExportReportDialog());

        JButton btnBack = new JButton("← Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setBackground(new Color(36, 92, 70));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener((ActionEvent e) -> {
            new MainDashboardForm().setVisible(true);
            dispose();
        });

        rightTop.add(btnExport);
        rightTop.add(btnBack);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // MAIN CONTENT
        JPanel mainContent = new JPanel(new BorderLayout(0, 15));
        mainContent.setBackground(BG_COLOR);
        mainContent.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. KPI Cards Bar di bagian atas
        JPanel kpiCardsPanel = createKpiCardsPanel();
        mainContent.add(kpiCardsPanel, BorderLayout.NORTH);

        // 2. Tabbed Pane: Tab 1 (Histori Transaksi), Tab 2 (Rekap Produk Bunga)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("📋 Histori Seluruh Transaksi", createTransactionHistoryTab());
        tabbedPane.addTab("🌺 Rekap Produk Bunga Terlaris", createFlowerPerformanceTab());

        mainContent.add(tabbedPane, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createKpiCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 85));

        lblKpiRevenue = new JLabel("Rp 0");
        lblKpiProfit = new JLabel("Rp 0");
        lblKpiTrxCount = new JLabel("0");
        lblKpiFlowersSold = new JLabel("0 Tangkai");

        panel.add(createSingleKpiCard("💰 Total Omzet Kotor", lblKpiRevenue, PRIMARY_COLOR));
        panel.add(createSingleKpiCard("📈 Total Laba Bersih", lblKpiProfit, new Color(30, 132, 73)));
        panel.add(createSingleKpiCard("🧾 Transaksi Selesai", lblKpiTrxCount, ACCENT_BLUE));
        panel.add(createSingleKpiCard("🌸 Total Bunga Terjual", lblKpiFlowersSold, SECONDARY_COLOR));

        return panel;
    }

    private JPanel createSingleKpiCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(225, 230, 235), 1),
                        new EmptyBorder(10, 14, 10, 14)
                )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(110, 120, 130));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(TEXT_DARK);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);

        return card;
    }

    private JPanel createTransactionHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);

        JLabel lblFilter = new JLabel("Filter Jenis:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));

        cmbFilterType = new JComboBox<>(new String[]{"Semua Jenis Pesanan", "Beli Bunga Langsung", "Buket Bunga Custom", "Vas Meja", "Gift Box"});
        cmbFilterType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbFilterType.addActionListener((ActionEvent e) -> loadTransactionTable());

        JLabel lblSearch = new JLabel("🔍 Cari (ID/Pelanggan/Kasir):");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtSearchTrx = new JTextField(14);
        txtSearchTrx.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearchTrx.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadTransactionTable();
            }
        });

        JButton btnReset = new JButton("Reset Filter");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener((ActionEvent e) -> {
            cmbFilterType.setSelectedIndex(0);
            txtSearchTrx.setText("");
            loadTransactionTable();
        });

        filterBar.add(lblFilter);
        filterBar.add(cmbFilterType);
        filterBar.add(Box.createHorizontalStrut(10));
        filterBar.add(lblSearch);
        filterBar.add(txtSearchTrx);
        filterBar.add(btnReset);

        panel.add(filterBar, BorderLayout.NORTH);

        // Tabel Riwayat Transaksi
        String[] columns = {"No. Transaksi", "Waktu Transaksi", "Pelanggan", "Kasir", "Jenis Pesanan", "Jml Item", "Total Bayar", "Laba Bersih"};
        trxTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblTransactions = new JTable(trxTableModel);
        tblTransactions.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblTransactions.setRowHeight(25);
        tblTransactions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTransactions.setGridColor(new Color(230, 235, 240));

        JTableHeader th = tblTransactions.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(238, 242, 245));

        // Alignment
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        tblTransactions.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        tblTransactions.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        tblTransactions.getColumnModel().getColumn(5).setCellRenderer(centerRender);

        DefaultTableCellRenderer rightRender = new DefaultTableCellRenderer();
        rightRender.setHorizontalAlignment(SwingConstants.RIGHT);
        tblTransactions.getColumnModel().getColumn(6).setCellRenderer(rightRender);
        tblTransactions.getColumnModel().getColumn(7).setCellRenderer(rightRender);

        tblTransactions.getColumnModel().getColumn(0).setPreferredWidth(125);
        tblTransactions.getColumnModel().getColumn(1).setPreferredWidth(130);
        tblTransactions.getColumnModel().getColumn(2).setPreferredWidth(130);
        tblTransactions.getColumnModel().getColumn(3).setPreferredWidth(110);
        tblTransactions.getColumnModel().getColumn(4).setPreferredWidth(140);
        tblTransactions.getColumnModel().getColumn(5).setPreferredWidth(65);
        tblTransactions.getColumnModel().getColumn(6).setPreferredWidth(95);
        tblTransactions.getColumnModel().getColumn(7).setPreferredWidth(95);

        JScrollPane scroll = new JScrollPane(tblTransactions);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(225, 230, 235), 1));
        panel.add(scroll, BorderLayout.CENTER);

        // Action Bawah
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomBar.setOpaque(false);

        btnViewReceipt = new JButton("🔍 Pratinjau / Cetak Struk Nota Terpilih");
        btnViewReceipt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnViewReceipt.setBackground(PRIMARY_COLOR);
        btnViewReceipt.setForeground(Color.WHITE);
        btnViewReceipt.setFocusPainted(false);
        btnViewReceipt.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewReceipt.addActionListener((ActionEvent e) -> handleViewSelectedReceipt());

        bottomBar.add(btnViewReceipt);
        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFlowerPerformanceTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel lblInfo = new JLabel("🏆 Analisis Penjualan & Produk Terlaris (Diurutkan dari Penjualan Tertinggi):");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInfo.setForeground(PRIMARY_COLOR);
        panel.add(lblInfo, BorderLayout.NORTH);

        String[] columns = {"Ranking", "Kode", "Nama Bunga", "Kategori", "Harga Modal", "Harga Jual", "Terjual", "Total Omzet", "Total Laba Bersih"};
        flowerSalesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblFlowerSales = new JTable(flowerSalesModel);
        tblFlowerSales.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblFlowerSales.setRowHeight(25);
        tblFlowerSales.setGridColor(new Color(230, 235, 240));

        JTableHeader th = tblFlowerSales.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(238, 242, 245));

        // Alignments
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        tblFlowerSales.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        tblFlowerSales.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        tblFlowerSales.getColumnModel().getColumn(6).setCellRenderer(centerRender);

        DefaultTableCellRenderer rightRender = new DefaultTableCellRenderer();
        rightRender.setHorizontalAlignment(SwingConstants.RIGHT);
        tblFlowerSales.getColumnModel().getColumn(4).setCellRenderer(rightRender);
        tblFlowerSales.getColumnModel().getColumn(5).setCellRenderer(rightRender);
        tblFlowerSales.getColumnModel().getColumn(7).setCellRenderer(rightRender);
        tblFlowerSales.getColumnModel().getColumn(8).setCellRenderer(rightRender);

        tblFlowerSales.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblFlowerSales.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblFlowerSales.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblFlowerSales.getColumnModel().getColumn(3).setPreferredWidth(85);
        tblFlowerSales.getColumnModel().getColumn(4).setPreferredWidth(85);
        tblFlowerSales.getColumnModel().getColumn(5).setPreferredWidth(85);
        tblFlowerSales.getColumnModel().getColumn(6).setPreferredWidth(80);
        tblFlowerSales.getColumnModel().getColumn(7).setPreferredWidth(100);
        tblFlowerSales.getColumnModel().getColumn(8).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(tblFlowerSales);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(225, 230, 235), 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void refreshAllData() {
        // 1. Update KPI Card Values
        lblKpiRevenue.setText(currencyFormat.format(transactionService.getTotalGrossRevenue()));
        lblKpiProfit.setText(currencyFormat.format(transactionService.getTotalNetProfit()));
        lblKpiTrxCount.setText(transactionService.getTotalTransactionsCount() + " Transaksi");
        lblKpiFlowersSold.setText(transactionService.getTotalFlowersSold() + " Tangkai/Item");

        // 2. Load Tables
        loadTransactionTable();
        loadFlowerPerformanceTable();
    }

    private void loadTransactionTable() {
        trxTableModel.setRowCount(0);

        OrderType filterType = null;
        int filterIndex = cmbFilterType.getSelectedIndex();
        if (filterIndex == 1) filterType = OrderType.DIRECT_PURCHASE;
        else if (filterIndex == 2) filterType = OrderType.CUSTOM_BOUQUET;
        else if (filterIndex == 3) filterType = OrderType.TABLE_ARRANGEMENT;
        else if (filterIndex == 4) filterType = OrderType.GIFT_BOX;

        String keyword = txtSearchTrx.getText().trim();
        List<Transaction> transactions = reportService.getFilteredTransactions(filterType, keyword);

        for (Transaction trx : transactions) {
            trxTableModel.addRow(new Object[]{
                trx.getTransactionId(),
                trx.getFormattedDateTime(),
                trx.getCustomerName(),
                trx.getCashierName(),
                trx.getOrderType().name(),
                trx.getTotalQuantity() + " item",
                currencyFormat.format(trx.calculateGrandTotal()),
                currencyFormat.format(trx.calculateTotalProfit())
            });
        }
    }

    private void loadFlowerPerformanceTable() {
        flowerSalesModel.setRowCount(0);
        List<FlowerSalesSummary> summaries = reportService.getFlowerSalesSummaries();

        int rank = 1;
        for (FlowerSalesSummary s : summaries) {
            Flower f = s.getFlower();
            String rankLabel = (rank == 1 && s.getTotalSold() > 0) ? "⭐ #1" : "#" + rank;
            flowerSalesModel.addRow(new Object[]{
                rankLabel,
                f.getCode(),
                f.getName(),
                f.getCategory(),
                currencyFormat.format(f.getBuyPrice()),
                currencyFormat.format(f.getSellPrice()),
                s.getTotalSold() + " " + f.getUnit(),
                currencyFormat.format(s.getTotalRevenue()),
                currencyFormat.format(s.getTotalProfit())
            });
            rank++;
        }
    }

    private void handleViewSelectedReceipt() {
        int selectedRow = tblTransactions.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Silakan klik salah satu baris transaksi pada tabel di atas!",
                    "Pilih Transaksi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String trxId = trxTableModel.getValueAt(selectedRow, 0).toString();
        Transaction targetTrx = null;
        for (Transaction trx : transactionService.getAllTransactions()) {
            if (trx.getTransactionId().equalsIgnoreCase(trxId)) {
                targetTrx = trx;
                break;
            }
        }

        if (targetTrx != null) {
            showReceiptPopup(targetTrx);
        }
    }

    private void showReceiptPopup(Transaction trx) {
        JDialog dialog = new JDialog(this, "Struk Nota - " + trx.getTransactionId(), true);
        dialog.setSize(440, 580);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea txtReceipt = new JTextArea();
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReceipt.setBackground(new Color(255, 255, 250));
        txtReceipt.setBorder(new EmptyBorder(15, 15, 15, 15));

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("              FLORISTFLOW\n");
        sb.append("      Toko Bunga & Kreasi Rangkaian\n");
        sb.append("==========================================\n");
        sb.append(String.format("No. Nota  : %s\n", trx.getTransactionId()));
        sb.append(String.format("Tanggal   : %s\n", trx.getFormattedDateTime()));
        sb.append(String.format("Kasir     : %s\n", trx.getCashierName()));
        sb.append(String.format("Pelanggan : %s\n", trx.getCustomerName()));
        sb.append(String.format("Jenis     : %s\n", trx.getOrderType().getDescription()));
        sb.append("------------------------------------------\n");
        sb.append("ITEM PESANAN:\n");

        for (OrderItem item : trx.getItems()) {
            Flower f = item.getFlower();
            String name = f.getName();
            if (name.length() > 20) name = name.substring(0, 18) + "..";
            sb.append(String.format(" %-2x %-20s %14s\n",
                    item.getQuantity(), name, currencyFormat.format(item.getSubtotal())));
        }

        sb.append("------------------------------------------\n");
        sb.append(String.format("Subtotal Bunga       : %18s\n", currencyFormat.format(trx.calculateItemsSubtotal())));
        if (trx.getWrappingFee() > 0) {
            sb.append(String.format("Jasa Wrapping & Pita : %18s\n", currencyFormat.format(trx.getWrappingFee())));
        }
        if (trx.getDiscount() > 0) {
            sb.append(String.format("Potongan Diskon      : %18s\n", currencyFormat.format(trx.getDiscount())));
        }
        sb.append("------------------------------------------\n");
        sb.append(String.format("GRAND TOTAL          : %18s\n", currencyFormat.format(trx.calculateGrandTotal())));
        sb.append(String.format("TUNAI DITERIMA       : %18s\n", currencyFormat.format(trx.getCashPaid())));
        sb.append(String.format("KEMBALIAN            : %18s\n", currencyFormat.format(trx.calculateChange())));

        if (trx.getGreetingCard() != null && !trx.getGreetingCard().trim().equals("-")) {
            sb.append("------------------------------------------\n");
            sb.append("Ucapan: \"").append(trx.getGreetingCard()).append("\"\n");
        }
        sb.append("==========================================\n");

        txtReceipt.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(txtReceipt);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnClose = new JButton("Tutup");
        btnClose.addActionListener((ActionEvent e) -> dialog.dispose());
        btnPanel.add(btnClose);

        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showExportReportDialog() {
        JDialog dialog = new JDialog(this, "Pratinjau Laporan Eksekutif FloristFlow", true);
        dialog.setSize(650, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea txtReport = new JTextArea();
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReport.setBackground(new Color(255, 255, 252));
        txtReport.setBorder(new EmptyBorder(15, 15, 15, 15));

        String reportText = reportService.generateExecutiveReportText();
        txtReport.setText(reportText);

        JScrollPane scroll = new JScrollPane(txtReport);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnCopy = new JButton("📋 Salin Teks Laporan");
        btnCopy.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCopy.addActionListener((ActionEvent e) -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(reportText), null);
            JOptionPane.showMessageDialog(dialog, "Teks laporan berhasil disalin ke Clipboard!", "Sukses Salin", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnClose = new JButton("Tutup");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.addActionListener((ActionEvent e) -> dialog.dispose());

        btnPanel.add(btnCopy);
        btnPanel.add(btnClose);

        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ReportForm().setVisible(true);
        });
    }
}
