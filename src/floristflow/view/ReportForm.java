package floristflow.view;

import floristflow.model.Flower;
import floristflow.model.FlowerSalesSummary;
import floristflow.model.OrderItem;
import floristflow.model.OrderType;
import floristflow.model.Transaction;
import floristflow.service.ReportService;
import floristflow.service.TransactionService;
import floristflow.view.ui.ModernButton;
import floristflow.view.ui.RoundedPanel;
import floristflow.view.ui.UITheme;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Form GUI Rekapitulasi & Laporan Bisnis FloristFlow dengan tema Modern Floral Boutique.
 */
public class ReportForm extends JFrame {

    private final ReportService reportService = ReportService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();

    // KPI Labels
    private JLabel lblKpiRevenue;
    private JLabel lblKpiProfit;
    private JLabel lblKpiTrxCount;
    private JLabel lblKpiFlowersSold;

    // Tab 1: Histori Transaksi
    private JComboBox<String> cmbFilterType;
    private JTextField txtSearchTrx;
    private JTable tblTransactions;
    private DefaultTableModel trxTableModel;
    private ModernButton btnViewReceipt;

    // Tab 2: Rekap Performa Bunga
    private JTable tblFlowerSales;
    private DefaultTableModel flowerSalesModel;

    public ReportForm() {
        initComponents();
        refreshAllData();
    }

    private void initComponents() {
        setTitle("FloristFlow - Rekapitulasi Laporan Keuangan & Penjualan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1140, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY);
        topBar.setBorder(new EmptyBorder(14, 25, 14, 25));

        JLabel lblTitle = new JLabel("📊 FloristFlow - Rekapitulasi & Laporan Finansial");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);

        ModernButton btnExport = new ModernButton("🖨️ Cetak / Ekspor Laporan", UITheme.ACCENT_GOLD, Color.WHITE, 8);
        btnExport.addActionListener((ActionEvent e) -> showExportReportDialog());

        ModernButton btnBack = new ModernButton("← Dashboard", new Color(28, 74, 57), Color.WHITE, 8);
        btnBack.setFont(UITheme.FONT_BOLD);
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
        mainContent.setBackground(UITheme.BG_MAIN);
        mainContent.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. KPI Cards Bar
        JPanel kpiCards = createKpiCardsPanel();
        mainContent.add(kpiCards, BorderLayout.NORTH);

        // 2. Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.addTab("📋 Histori Seluruh Transaksi", createTransactionHistoryTab());
        tabbedPane.addTab("🌺 Rekap Produk Bunga Terlaris", createFlowerPerformanceTab());

        mainContent.add(tabbedPane, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createKpiCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 88));

        lblKpiRevenue = new JLabel("Rp 0");
        lblKpiProfit = new JLabel("Rp 0");
        lblKpiTrxCount = new JLabel("0");
        lblKpiFlowersSold = new JLabel("0 Tangkai");

        panel.add(createKpiCard("💰 Total Omzet Kotor", lblKpiRevenue, UITheme.PRIMARY));
        panel.add(createKpiCard("📈 Total Laba Bersih", lblKpiProfit, UITheme.STATUS_SUCCESS));
        panel.add(createKpiCard("🧾 Transaksi Selesai", lblKpiTrxCount, UITheme.STATUS_INFO));
        panel.add(createKpiCard("🌸 Total Bunga Terjual", lblKpiFlowersSold, UITheme.ACCENT_ROSE));

        return panel;
    }

    private JPanel createKpiCard(String title, JLabel valLabel, Color accentColor) {
        RoundedPanel card = new RoundedPanel(12, Color.WHITE, UITheme.BORDER, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lbl = new JLabel(title);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valLabel.setForeground(accentColor);

        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valLabel);
        return card;
    }

    private JPanel createTransactionHistoryTab() {
        RoundedPanel panel = new RoundedPanel(14, Color.WHITE, UITheme.BORDER, 1);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);

        JLabel lblFilter = new JLabel("Filter Jenis:");
        lblFilter.setFont(UITheme.FONT_BOLD);

        cmbFilterType = new JComboBox<>(new String[]{"Semua Jenis Pesanan", "Beli Bunga Langsung", "Buket Bunga Custom", "Vas Meja", "Gift Box"});
        cmbFilterType.setFont(UITheme.FONT_REGULAR_MEDIUM);
        cmbFilterType.addActionListener((ActionEvent e) -> loadTransactionTable());

        JLabel lblSearch = new JLabel("🔍 Cari:");
        lblSearch.setFont(UITheme.FONT_BOLD);

        txtSearchTrx = new JTextField(15);
        UITheme.styleTextField(txtSearchTrx);
        txtSearchTrx.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadTransactionTable();
            }
        });

        ModernButton btnReset = new ModernButton("Reset", new Color(241, 245, 249), UITheme.TEXT_DARK, 6);
        btnReset.setPreferredSize(new Dimension(65, 30));
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

        // Table
        String[] columns = {"No. Transaksi", "Waktu Transaksi", "Pelanggan", "Kasir", "Jenis Pesanan", "Jml Item", "Total Bayar", "Laba Bersih"};
        trxTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblTransactions = new JTable(trxTableModel);
        UITheme.styleTable(tblTransactions);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblTransactions.getColumnModel().getColumn(0).setCellRenderer(center);
        tblTransactions.getColumnModel().getColumn(1).setCellRenderer(center);
        tblTransactions.getColumnModel().getColumn(5).setCellRenderer(center);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblTransactions.getColumnModel().getColumn(6).setCellRenderer(right);
        tblTransactions.getColumnModel().getColumn(7).setCellRenderer(right);

        tblTransactions.getColumnModel().getColumn(0).setPreferredWidth(125);
        tblTransactions.getColumnModel().getColumn(1).setPreferredWidth(130);
        tblTransactions.getColumnModel().getColumn(2).setPreferredWidth(130);
        tblTransactions.getColumnModel().getColumn(3).setPreferredWidth(110);
        tblTransactions.getColumnModel().getColumn(4).setPreferredWidth(140);
        tblTransactions.getColumnModel().getColumn(5).setPreferredWidth(65);
        tblTransactions.getColumnModel().getColumn(6).setPreferredWidth(95);
        tblTransactions.getColumnModel().getColumn(7).setPreferredWidth(95);

        JScrollPane scroll = new JScrollPane(tblTransactions);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        // Bottom Action
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomBar.setOpaque(false);

        btnViewReceipt = new ModernButton("🔍 Pratinjau / Cetak Struk Terpilih", UITheme.PRIMARY, Color.WHITE, 8);
        btnViewReceipt.setPreferredSize(new Dimension(240, 36));
        btnViewReceipt.addActionListener((ActionEvent e) -> handleViewSelectedReceipt());

        bottomBar.add(btnViewReceipt);
        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFlowerPerformanceTab() {
        RoundedPanel panel = new RoundedPanel(14, Color.WHITE, UITheme.BORDER, 1);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblInfo = new JLabel("🏆 Analisis Penjualan & Produk Terlaris (Diurutkan dari Penjualan Tertinggi):");
        lblInfo.setFont(UITheme.FONT_SUBTITLE);
        lblInfo.setForeground(UITheme.PRIMARY);
        panel.add(lblInfo, BorderLayout.NORTH);

        String[] columns = {"Ranking", "Kode", "Nama Bunga", "Kategori", "Harga Modal", "Harga Jual", "Terjual", "Total Omzet", "Total Laba Bersih"};
        flowerSalesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblFlowerSales = new JTable(flowerSalesModel);
        UITheme.styleTable(tblFlowerSales);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblFlowerSales.getColumnModel().getColumn(0).setCellRenderer(center);
        tblFlowerSales.getColumnModel().getColumn(1).setCellRenderer(center);
        tblFlowerSales.getColumnModel().getColumn(6).setCellRenderer(center);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblFlowerSales.getColumnModel().getColumn(4).setCellRenderer(right);
        tblFlowerSales.getColumnModel().getColumn(5).setCellRenderer(right);
        tblFlowerSales.getColumnModel().getColumn(7).setCellRenderer(right);
        tblFlowerSales.getColumnModel().getColumn(8).setCellRenderer(right);

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
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void refreshAllData() {
        lblKpiRevenue.setText(UITheme.formatRupiah(transactionService.getTotalGrossRevenue()));
        lblKpiProfit.setText(UITheme.formatRupiah(transactionService.getTotalNetProfit()));
        lblKpiTrxCount.setText(transactionService.getTotalTransactionsCount() + " Transaksi");
        lblKpiFlowersSold.setText(transactionService.getTotalFlowersSold() + " Tangkai");

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
                UITheme.formatRupiah(trx.calculateGrandTotal()),
                UITheme.formatRupiah(trx.calculateTotalProfit())
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
                UITheme.formatRupiah(f.getBuyPrice()),
                UITheme.formatRupiah(f.getSellPrice()),
                s.getTotalSold() + " " + f.getUnit(),
                UITheme.formatRupiah(s.getTotalRevenue()),
                UITheme.formatRupiah(s.getTotalProfit())
            });
            rank++;
        }
    }

    private void handleViewSelectedReceipt() {
        int row = tblTransactions.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silakan klik baris transaksi pada tabel di atas!", "Pilih Transaksi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String trxId = trxTableModel.getValueAt(row, 0).toString();
        Transaction target = null;
        for (Transaction trx : transactionService.getAllTransactions()) {
            if (trx.getTransactionId().equalsIgnoreCase(trxId)) {
                target = trx;
                break;
            }
        }

        if (target != null) {
            showReceiptPopup(target);
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
                    item.getQuantity(), name, UITheme.formatRupiah(item.getSubtotal())));
        }

        sb.append("------------------------------------------\n");
        sb.append(String.format("Subtotal Bunga       : %18s\n", UITheme.formatRupiah(trx.calculateItemsSubtotal())));
        if (trx.getWrappingFee() > 0) {
            sb.append(String.format("Jasa Wrapping & Pita : %18s\n", UITheme.formatRupiah(trx.getWrappingFee())));
        }
        if (trx.getDiscount() > 0) {
            sb.append(String.format("Potongan Diskon      : %18s\n", UITheme.formatRupiah(trx.getDiscount())));
        }
        sb.append("------------------------------------------\n");
        sb.append(String.format("GRAND TOTAL          : %18s\n", UITheme.formatRupiah(trx.calculateGrandTotal())));
        sb.append(String.format("TUNAI DITERIMA       : %18s\n", UITheme.formatRupiah(trx.getCashPaid())));
        sb.append(String.format("KEMBALIAN            : %18s\n", UITheme.formatRupiah(trx.calculateChange())));

        if (trx.getGreetingCard() != null && !trx.getGreetingCard().trim().equals("-")) {
            sb.append("------------------------------------------\n");
            sb.append("Ucapan: \"").append(trx.getGreetingCard()).append("\"\n");
        }
        sb.append("==========================================\n");

        txtReceipt.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(txtReceipt);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        ModernButton btnClose = new ModernButton("Tutup", UITheme.PRIMARY, Color.WHITE, 8);
        btnClose.addActionListener((ActionEvent e) -> dialog.dispose());
        btnPanel.add(btnClose);

        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showExportReportDialog() {
        JDialog dialog = new JDialog(this, "Laporan Eksekutif FloristFlow", true);
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

        ModernButton btnCopy = new ModernButton("📋 Salin Teks Laporan", UITheme.PRIMARY, Color.WHITE, 8);
        btnCopy.addActionListener((ActionEvent e) -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(reportText), null);
            JOptionPane.showMessageDialog(dialog, "Teks laporan berhasil disalin ke Clipboard!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        });

        ModernButton btnClose = new ModernButton("Tutup", new Color(241, 245, 249), UITheme.TEXT_DARK, 8);
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
