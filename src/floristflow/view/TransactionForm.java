package floristflow.view;

import floristflow.model.Flower;
import floristflow.model.OrderItem;
import floristflow.model.OrderType;
import floristflow.model.Transaction;
import floristflow.model.User;
import floristflow.service.AuthService;
import floristflow.service.StockService;
import floristflow.service.TransactionService;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Form GUI untuk Modul Proses Bisnis (Transaksi Penjualan & Pembuatan Buket).
 * Mengintegrasikan keranjang belanja, kalkulasi biaya jasa buket, diskon,
 * pengurangan stok otomatis, dan penerbitan nota transaksi digital.
 */
public class TransactionForm extends JFrame {

    private final StockService stockService = StockService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();
    private final AuthService authService = AuthService.getInstance();
    private final DecimalFormat currencyFormat;

    private final List<OrderItem> cartItems = new ArrayList<>();
    private String currentTransactionId;

    // Header & Info User
    private JLabel lblCashierName;

    // Form Pesanan & Pelanggan
    private JTextField txtTrxId;
    private JTextField txtCustomerName;
    private JTextField txtCustomerPhone;
    private JComboBox<OrderType> cmbOrderType;
    private JTextField txtWrappingFee;
    private JTextField txtGreetingCard;

    // Pilihan Bunga & Keranjang
    private JComboBox<String> cmbFlowers;
    private JSpinner spnQuantity;
    private JButton btnAddToCart;
    private JTable tblCart;
    private DefaultTableModel cartTableModel;
    private JButton btnRemoveItem;
    private JButton btnClearCart;

    // Panel Pembayaran & Kasir
    private JLabel lblSubtotal;
    private JLabel lblWrappingTotal;
    private JTextField txtDiscount;
    private JLabel lblGrandTotal;
    private JTextField txtCashPaid;
    private JLabel lblChange;
    private JButton btnProcessCheckout;

    // Transaksi terakhir yang sukses (untuk cetak ulang struk)
    private Transaction lastCompletedTransaction;

    // Skema Warna FloristFlow
    private static final Color PRIMARY_COLOR = new Color(46, 117, 89);     // Forest Green
    private static final Color SECONDARY_COLOR = new Color(214, 90, 117);  // Soft Rose
    private static final Color ACCENT_GOLD = new Color(212, 160, 23);
    private static final Color BG_COLOR = new Color(245, 247, 248);
    private static final Color TEXT_DARK = new Color(40, 44, 52);

    public TransactionForm() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("id-ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        this.currencyFormat = new DecimalFormat("Rp #,###", symbols);

        initComponents();
        resetNewTransaction();
    }

    private void initComponents() {
        setTitle("FloristFlow - Proses Bisnis & Transaksi Kasir Toko Bunga");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1150, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setBorder(new EmptyBorder(12, 25, 12, 25));

        JLabel lblTitle = new JLabel("🌸 FloristFlow - Proses Bisnis Kasir & Pemesanan Buket");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightTop.setOpaque(false);

        User currentUser = authService.getCurrentUser();
        String cashier = (currentUser != null) ? currentUser.getFullName() : "Kasir";
        lblCashierName = new JLabel("👤 Kasir: " + cashier);
        lblCashierName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCashierName.setForeground(Color.WHITE);

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

        rightTop.add(lblCashierName);
        rightTop.add(btnBack);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // MAIN CONTENT (3 Bagian: Kiri = Input & Bunga, Tengah = Keranjang, Kanan = Pembayaran)
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(BG_COLOR);
        mainContent.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // 1. Kolom Kiri: Form Pelanggan & Pemilihan Bunga (Weight 0.32)
        gbc.gridx = 0;
        gbc.weightx = 0.32;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainContent.add(createLeftOrderPanel(), gbc);

        // 2. Kolom Tengah: Tabel Keranjang Pesanan (Weight 0.40)
        gbc.gridx = 1;
        gbc.weightx = 0.40;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainContent.add(createCenterCartPanel(), gbc);

        // 3. Kolom Kanan: Rincian Pembayaran Kasir (Weight 0.28)
        gbc.gridx = 2;
        gbc.weightx = 0.28;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContent.add(createRightPaymentPanel(), gbc);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createLeftOrderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                new EmptyBorder(15, 16, 15, 16)
        ));

        JLabel lblSec1 = new JLabel("1. Identitas Transaksi & Pelanggan");
        lblSec1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSec1.setForeground(PRIMARY_COLOR);
        panel.add(lblSec1);
        panel.add(Box.createVerticalStrut(10));

        // No Transaksi
        JLabel lblTrx = new JLabel("No. Transaksi");
        lblTrx.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTrxId = new JTextField();
        txtTrxId.setEditable(false);
        txtTrxId.setBackground(new Color(245, 248, 250));
        txtTrxId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtTrxId.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        panel.add(lblTrx);
        panel.add(Box.createVerticalStrut(2));
        panel.add(txtTrxId);
        panel.add(Box.createVerticalStrut(8));

        // Nama Pelanggan
        JLabel lblCustName = new JLabel("Nama Pembeli / Pemesan");
        lblCustName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCustomerName = new JTextField();
        txtCustomerName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCustomerName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        panel.add(lblCustName);
        panel.add(Box.createVerticalStrut(2));
        panel.add(txtCustomerName);
        panel.add(Box.createVerticalStrut(8));

        // No HP / WhatsApp
        JLabel lblCustPhone = new JLabel("No. WhatsApp / HP");
        lblCustPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCustomerPhone = new JTextField();
        txtCustomerPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCustomerPhone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        panel.add(lblCustPhone);
        panel.add(Box.createVerticalStrut(2));
        panel.add(txtCustomerPhone);
        panel.add(Box.createVerticalStrut(8));

        // Jenis Pesanan
        JLabel lblType = new JLabel("Jenis Pesanan");
        lblType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbOrderType = new JComboBox<>(OrderType.values());
        cmbOrderType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbOrderType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        cmbOrderType.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                OrderType selected = (OrderType) cmbOrderType.getSelectedItem();
                boolean isBouquet = (selected == OrderType.CUSTOM_BOUQUET || selected == OrderType.GIFT_BOX);
                if (isBouquet && txtWrappingFee.getText().trim().equals("0")) {
                    txtWrappingFee.setText("25000"); // Biaya default wrapping buket
                }
                recalculateTotals();
            }
        });

        panel.add(lblType);
        panel.add(Box.createVerticalStrut(2));
        panel.add(cmbOrderType);
        panel.add(Box.createVerticalStrut(8));

        // Biaya Wrapping / Jasa Buket
        JLabel lblWrap = new JLabel("Biaya Wrapping & Pita (Rp)");
        lblWrap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtWrappingFee = new JTextField("0");
        txtWrappingFee.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtWrappingFee.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        txtWrappingFee.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                recalculateTotals();
            }
        });

        panel.add(lblWrap);
        panel.add(Box.createVerticalStrut(2));
        panel.add(txtWrappingFee);
        panel.add(Box.createVerticalStrut(8));

        // Pesan Kartu Ucapan
        JLabel lblCard = new JLabel("Pesan Kartu Ucapan (Greeting Card)");
        lblCard.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtGreetingCard = new JTextField("-");
        txtGreetingCard.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtGreetingCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        panel.add(lblCard);
        panel.add(Box.createVerticalStrut(2));
        panel.add(txtGreetingCard);
        panel.add(Box.createVerticalStrut(15));

        // SEPARATOR
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(12));

        // BAGIAN 2: PILIH BUNGA
        JLabel lblSec2 = new JLabel("2. Pilih Bunga Persediaan");
        lblSec2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSec2.setForeground(PRIMARY_COLOR);
        panel.add(lblSec2);
        panel.add(Box.createVerticalStrut(8));

        cmbFlowers = new JComboBox<>();
        cmbFlowers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbFlowers.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        refreshFlowerDropdown();

        panel.add(new JLabel("Pilih Bunga (Stok & Harga):"));
        panel.add(Box.createVerticalStrut(2));
        panel.add(cmbFlowers);
        panel.add(Box.createVerticalStrut(8));

        // Qty & Tombol Tambah
        JPanel pnlQtyAdd = new JPanel(new BorderLayout(8, 0));
        pnlQtyAdd.setOpaque(false);
        pnlQtyAdd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spnQuantity.setFont(new Font("Segoe UI", Font.BOLD, 13));
        spnQuantity.setPreferredSize(new Dimension(75, 32));

        btnAddToCart = new JButton("+ Tambah ke Keranjang");
        btnAddToCart.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddToCart.setBackground(PRIMARY_COLOR);
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFocusPainted(false);
        btnAddToCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddToCart.addActionListener((ActionEvent e) -> handleAddToCart());

        pnlQtyAdd.add(spnQuantity, BorderLayout.WEST);
        pnlQtyAdd.add(btnAddToCart, BorderLayout.CENTER);

        panel.add(pnlQtyAdd);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createCenterCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                new EmptyBorder(15, 16, 15, 16)
        ));

        // Header Cart
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("🛒 Keranjang Belanja Bunga");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(PRIMARY_COLOR);

        topHeader.add(lblTitle, BorderLayout.WEST);
        panel.add(topHeader, BorderLayout.NORTH);

        // Tabel Cart
        String[] columns = {"Kode", "Nama Bunga", "Warna", "Harga", "Qty", "Subtotal"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblCart = new JTable(cartTableModel);
        tblCart.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCart.setRowHeight(25);
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCart.setGridColor(new Color(230, 235, 240));

        JTableHeader th = tblCart.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(238, 242, 245));

        // Alignment
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        tblCart.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        tblCart.getColumnModel().getColumn(4).setCellRenderer(centerRender);

        DefaultTableCellRenderer rightRender = new DefaultTableCellRenderer();
        rightRender.setHorizontalAlignment(SwingConstants.RIGHT);
        tblCart.getColumnModel().getColumn(3).setCellRenderer(rightRender);
        tblCart.getColumnModel().getColumn(5).setCellRenderer(rightRender);

        tblCart.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblCart.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblCart.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblCart.getColumnModel().getColumn(3).setPreferredWidth(75);
        tblCart.getColumnModel().getColumn(4).setPreferredWidth(45);
        tblCart.getColumnModel().getColumn(5).setPreferredWidth(85);

        JScrollPane scrollPane = new JScrollPane(tblCart);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Tombol Aksi Keranjang
        JPanel bottomActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottomActions.setOpaque(false);

        btnRemoveItem = new JButton("🗑️ Hapus Item");
        btnRemoveItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRemoveItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRemoveItem.addActionListener((ActionEvent e) -> handleRemoveItem());

        btnClearCart = new JButton("🔄 Kosongkan");
        btnClearCart.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClearCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClearCart.addActionListener((ActionEvent e) -> handleClearCart());

        bottomActions.add(btnRemoveItem);
        bottomActions.add(btnClearCart);

        panel.add(bottomActions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPaymentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                new EmptyBorder(15, 16, 15, 16)
        ));

        JLabel lblTitle = new JLabel("3. Pembayaran & Checkout");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(PRIMARY_COLOR);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(15));

        // Subtotal Bunga
        panel.add(createRowLabelValue("Subtotal Bunga:", lblSubtotal = new JLabel("Rp 0")));
        panel.add(Box.createVerticalStrut(8));

        // Jasa Wrapping Buket
        panel.add(createRowLabelValue("Biaya Wrapping & Pita:", lblWrappingTotal = new JLabel("Rp 0")));
        panel.add(Box.createVerticalStrut(8));

        // Diskon Potongan
        JPanel pnlDisc = new JPanel(new BorderLayout(5, 0));
        pnlDisc.setOpaque(false);
        pnlDisc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel lblDisc = new JLabel("Potongan Diskon (Rp):");
        lblDisc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDiscount = new JTextField("0");
        txtDiscount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDiscount.setPreferredSize(new Dimension(90, 28));
        txtDiscount.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                recalculateTotals();
            }
        });
        pnlDisc.add(lblDisc, BorderLayout.WEST);
        pnlDisc.add(txtDiscount, BorderLayout.EAST);
        panel.add(pnlDisc);
        panel.add(Box.createVerticalStrut(12));

        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(12));

        // GRAND TOTAL (Mencolok)
        JLabel lblTotalTitle = new JLabel("TOTAL HARGA:");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalTitle.setForeground(TEXT_DARK);
        lblTotalTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblGrandTotal = new JLabel("Rp 0");
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblGrandTotal.setForeground(PRIMARY_COLOR);
        lblGrandTotal.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTotalTitle);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblGrandTotal);
        panel.add(Box.createVerticalStrut(15));

        // Uang Pembayaran (Tunai)
        JLabel lblCash = new JLabel("Uang Diterima / Bayar (Rp)");
        lblCash.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCash.setForeground(TEXT_DARK);
        txtCashPaid = new JTextField();
        txtCashPaid.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtCashPaid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtCashPaid.setPreferredSize(new Dimension(200, 36));
        txtCashPaid.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                recalculateTotals();
            }
        });

        panel.add(lblCash);
        panel.add(Box.createVerticalStrut(3));
        panel.add(txtCashPaid);
        panel.add(Box.createVerticalStrut(10));

        // Kembalian
        panel.add(createRowLabelValue("Uang Kembalian:", lblChange = new JLabel("Rp 0")));
        lblChange.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblChange.setForeground(new Color(40, 116, 166));
        panel.add(Box.createVerticalStrut(18));

        // Tombol Checkout Utama
        btnProcessCheckout = new JButton("🧾 BAYAR & PROSES NOTA");
        btnProcessCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProcessCheckout.setBackground(SECONDARY_COLOR);
        btnProcessCheckout.setForeground(Color.WHITE);
        btnProcessCheckout.setFocusPainted(false);
        btnProcessCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProcessCheckout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnProcessCheckout.setPreferredSize(new Dimension(200, 44));
        btnProcessCheckout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnProcessCheckout.addActionListener((ActionEvent e) -> handleProcessCheckout());

        panel.add(btnProcessCheckout);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createRowLabelValue(String labelText, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_DARK);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        row.add(lbl, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private void refreshFlowerDropdown() {
        cmbFlowers.removeAllItems();
        for (Flower f : stockService.getAllFlowers()) {
            cmbFlowers.addItem(f.getCode() + " - " + f.getName() + " (" + f.getColor() + ") | " +
                               currencyFormat.format(f.getSellPrice()) + " [Stok: " + f.getStock() + "]");
        }
    }

    private void handleAddToCart() {
        int selectedIndex = cmbFlowers.getSelectedIndex();
        if (selectedIndex == -1) {
            return;
        }

        List<Flower> flowers = new ArrayList<>(stockService.getAllFlowers());
        if (selectedIndex >= flowers.size()) {
            return;
        }

        Flower selectedFlower = flowers.get(selectedIndex);
        int qty = (int) spnQuantity.getValue();

        // Validasi ketersediaan stok
        int alreadyInCart = 0;
        for (OrderItem item : cartItems) {
            if (item.getFlower().getCode().equalsIgnoreCase(selectedFlower.getCode())) {
                alreadyInCart += item.getQuantity();
            }
        }

        if (alreadyInCart + qty > selectedFlower.getStock()) {
            JOptionPane.showMessageDialog(this,
                    "Stok tidak mencukupi!\n" +
                    "Tersedia: " + selectedFlower.getStock() + " " + selectedFlower.getUnit() + "\n" +
                    "Sudah di keranjang: " + alreadyInCart + "\n" +
                    "Diminta: " + qty,
                    "Stok Kurang", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tambah atau gabung ke keranjang
        boolean found = false;
        for (OrderItem item : cartItems) {
            if (item.getFlower().getCode().equalsIgnoreCase(selectedFlower.getCode())) {
                item.setQuantity(item.getQuantity() + qty);
                found = true;
                break;
            }
        }

        if (!found) {
            cartItems.add(new OrderItem(selectedFlower, qty));
        }

        refreshCartTable();
        recalculateTotals();
        spnQuantity.setValue(1);
    }

    private void handleRemoveItem() {
        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris item di keranjang yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cartItems.remove(selectedRow);
        refreshCartTable();
        recalculateTotals();
    }

    private void handleClearCart() {
        if (cartItems.isEmpty()) {
            return;
        }
        cartItems.clear();
        refreshCartTable();
        recalculateTotals();
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        for (OrderItem item : cartItems) {
            Flower f = item.getFlower();
            cartTableModel.addRow(new Object[]{
                f.getCode(),
                f.getName(),
                f.getColor(),
                currencyFormat.format(item.getUnitPrice()),
                item.getQuantity(),
                currencyFormat.format(item.getSubtotal())
            });
        }
    }

    private void recalculateTotals() {
        double subtotal = 0;
        for (OrderItem item : cartItems) {
            subtotal += item.getSubtotal();
        }

        double wrapFee = 0;
        try {
            wrapFee = Double.parseDouble(txtWrappingFee.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        double discount = 0;
        try {
            discount = Double.parseDouble(txtDiscount.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        double grandTotal = Math.max(0, subtotal + wrapFee - discount);

        double cashPaid = 0;
        try {
            cashPaid = Double.parseDouble(txtCashPaid.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        double change = Math.max(0, cashPaid - grandTotal);

        lblSubtotal.setText(currencyFormat.format(subtotal));
        lblWrappingTotal.setText(currencyFormat.format(wrapFee));
        lblGrandTotal.setText(currencyFormat.format(grandTotal));
        lblChange.setText(currencyFormat.format(change));
    }

    private void handleProcessCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Keranjang belanja masih kosong! Tambahkan bunga terlebih dahulu.",
                    "Keranjang Kosong", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double grandTotal = 0;
        for (OrderItem item : cartItems) {
            grandTotal += item.getSubtotal();
        }

        double wrapFee = 0;
        try {
            wrapFee = Double.parseDouble(txtWrappingFee.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        double discount = 0;
        try {
            discount = Double.parseDouble(txtDiscount.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        grandTotal = Math.max(0, grandTotal + wrapFee - discount);

        double cashPaid = 0;
        try {
            cashPaid = Double.parseDouble(txtCashPaid.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Harap masukkan nominal uang pembayaran yang valid!",
                    "Input Tidak Valid", JOptionPane.ERROR_MESSAGE);
            txtCashPaid.requestFocus();
            return;
        }

        if (cashPaid < grandTotal) {
            JOptionPane.showMessageDialog(this,
                    "Uang pembayaran kurang!\nTotal Bayar: " + currencyFormat.format(grandTotal) +
                    "\nUang Diterima: " + currencyFormat.format(cashPaid),
                    "Uang Kurang", JOptionPane.WARNING_MESSAGE);
            txtCashPaid.requestFocus();
            return;
        }

        // Susun objek Transaction
        User currentUser = authService.getCurrentUser();
        String cashier = (currentUser != null) ? currentUser.getFullName() : "Staff Florist";

        Transaction trx = new Transaction(
                currentTransactionId,
                txtCustomerName.getText().trim(),
                txtCustomerPhone.getText().trim(),
                (OrderType) cmbOrderType.getSelectedItem(),
                cashier
        );

        trx.setWrappingFee(wrapFee);
        trx.setDiscount(discount);
        trx.setGreetingCard(txtGreetingCard.getText().trim());
        trx.setCashPaid(cashPaid);

        for (OrderItem item : cartItems) {
            trx.addItem(item);
        }

        // Proses melalui TransactionService
        String errorMsg = transactionService.processTransaction(trx);

        if (errorMsg != null) {
            JOptionPane.showMessageDialog(this, errorMsg, "Gagal Proses Transaksi", JOptionPane.ERROR_MESSAGE);
        } else {
            this.lastCompletedTransaction = trx;
            JOptionPane.showMessageDialog(this,
                    "Transaksi " + trx.getTransactionId() + " berhasil diproses!\n" +
                    "Stok bunga otomatis diperbarui di sistem inventaris.",
                    "Transaksi Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Buka Nota Struk Digital
            showReceiptDialog(trx);

            // Reset untuk transaksi berikutnya
            resetNewTransaction();
        }
    }

    private void resetNewTransaction() {
        this.currentTransactionId = transactionService.generateNextTransactionId();
        txtTrxId.setText(currentTransactionId);
        txtCustomerName.setText("");
        txtCustomerPhone.setText("");
        cmbOrderType.setSelectedIndex(0);
        txtWrappingFee.setText("0");
        txtGreetingCard.setText("-");
        txtDiscount.setText("0");
        txtCashPaid.setText("");
        cartItems.clear();
        refreshCartTable();
        refreshFlowerDropdown();
        recalculateTotals();
        txtCustomerName.requestFocus();
    }

    private void showReceiptDialog(Transaction trx) {
        JDialog dialog = new JDialog(this, "Struk Nota Penjualan - " + trx.getTransactionId(), true);
        dialog.setSize(440, 600);
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
        sb.append("       Jl. Melati No. 88, Kota Bunga\n");
        sb.append("==========================================\n");
        sb.append(String.format("No. Nota  : %s\n", trx.getTransactionId()));
        sb.append(String.format("Tanggal   : %s\n", trx.getFormattedDateTime()));
        sb.append(String.format("Kasir     : %s\n", trx.getCashierName()));
        sb.append(String.format("Pelanggan : %s\n", trx.getCustomerName()));
        sb.append(String.format("No. HP    : %s\n", trx.getCustomerPhone()));
        sb.append(String.format("Jenis     : %s\n", trx.getOrderType().getDescription()));
        sb.append("------------------------------------------\n");
        sb.append("ITEM PESANAN:\n");

        for (OrderItem item : trx.getItems()) {
            Flower f = item.getFlower();
            String name = f.getName();
            if (name.length() > 20) {
                name = name.substring(0, 18) + "..";
            }
            sb.append(String.format(" %-2x %-20s %14s\n",
                    item.getQuantity(),
                    name,
                    currencyFormat.format(item.getSubtotal())));
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
            sb.append("Pesan Kartu Ucapan:\n");
            sb.append("\"").append(trx.getGreetingCard()).append("\"\n");
        }

        sb.append("==========================================\n");
        sb.append("    Terima kasih atas kunjungan Anda!\n");
        sb.append("   Koleksi Bunga Segar Pilihan Terbaik\n");
        sb.append("==========================================\n");

        txtReceipt.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(txtReceipt);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnClose = new JButton("Tutup Nota");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.addActionListener((ActionEvent e) -> dialog.dispose());
        btnPanel.add(btnClose);

        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TransactionForm().setVisible(true);
        });
    }
}
