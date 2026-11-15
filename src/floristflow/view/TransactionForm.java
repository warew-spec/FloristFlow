package floristflow.view;

import floristflow.model.Flower;
import floristflow.model.OrderItem;
import floristflow.model.OrderType;
import floristflow.model.Transaction;
import floristflow.model.User;
import floristflow.service.AuthService;
import floristflow.service.StockService;
import floristflow.service.TransactionService;
import floristflow.view.ui.ModernButton;
import floristflow.view.ui.RoundedPanel;
import floristflow.view.ui.UITheme;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Form GUI Kasir & Proses Bisnis FloristFlow dengan tema Modern Floral Boutique.
 */
public class TransactionForm extends JFrame {

    private final StockService stockService = StockService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();
    private final AuthService authService = AuthService.getInstance();

    private final List<OrderItem> cartItems = new ArrayList<>();
    private String currentTransactionId;

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
    private ModernButton btnAddToCart;
    private JTable tblCart;
    private DefaultTableModel cartTableModel;
    private ModernButton btnRemoveItem;
    private ModernButton btnClearCart;

    // Panel Pembayaran & Kasir
    private JLabel lblSubtotal;
    private JLabel lblWrappingTotal;
    private JTextField txtDiscount;
    private JLabel lblGrandTotal;
    private JTextField txtCashPaid;
    private JLabel lblChange;
    private ModernButton btnProcessCheckout;

    public TransactionForm() {
        initComponents();
        resetNewTransaction();
    }

    private void initComponents() {
        setTitle("FloristFlow - Kasir & Proses Bisnis Pemesanan Buket");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1180, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY);
        topBar.setBorder(new EmptyBorder(14, 25, 14, 25));

        JLabel lblTitle = new JLabel("🌸 FloristFlow - Mesin Kasir & Pemesanan Buket");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightTop.setOpaque(false);

        User currentUser = authService.getCurrentUser();
        String cashier = (currentUser != null) ? currentUser.getFullName() : "Kasir";
        JLabel lblCashier = new JLabel("👤 Kasir: " + cashier);
        lblCashier.setFont(UITheme.FONT_BOLD);
        lblCashier.setForeground(Color.WHITE);

        ModernButton btnBack = new ModernButton("← Dashboard", new Color(28, 74, 57), Color.WHITE, 8);
        btnBack.setFont(UITheme.FONT_BOLD);
        btnBack.addActionListener((ActionEvent e) -> {
            new MainDashboardForm().setVisible(true);
            dispose();
        });

        rightTop.add(lblCashier);
        rightTop.add(btnBack);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // MAIN 3-PANEL GRID
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(UITheme.BG_MAIN);
        mainContent.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // 1. Panel Kiri: Form Pesanan (32%)
        gbc.gridx = 0;
        gbc.weightx = 0.32;
        gbc.insets = new Insets(0, 0, 0, 12);
        mainContent.add(createLeftOrderPanel(), gbc);

        // 2. Panel Tengah: Keranjang Belanja (40%)
        gbc.gridx = 1;
        gbc.weightx = 0.40;
        gbc.insets = new Insets(0, 0, 0, 12);
        mainContent.add(createCenterCartPanel(), gbc);

        // 3. Panel Kanan: Kasir Pembayaran (28%)
        gbc.gridx = 2;
        gbc.weightx = 0.28;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContent.add(createRightPaymentPanel(), gbc);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createLeftOrderPanel() {
        RoundedPanel panel = new RoundedPanel(14, Color.WHITE, UITheme.BORDER, 1);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblSec1 = new JLabel("1. Identitas Transaksi & Pembeli");
        lblSec1.setFont(UITheme.FONT_SUBTITLE);
        lblSec1.setForeground(UITheme.PRIMARY);
        panel.add(lblSec1);
        panel.add(Box.createVerticalStrut(10));

        // No Transaksi
        JLabel lblTrx = new JLabel("No. Nota Transaksi");
        lblTrx.setFont(UITheme.FONT_BOLD);
        lblTrx.setForeground(UITheme.TEXT_DARK);
        txtTrxId = new JTextField();
        UITheme.styleTextField(txtTrxId);
        txtTrxId.setEditable(false);
        txtTrxId.setBackground(new Color(245, 248, 250));
        txtTrxId.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        panel.add(lblTrx);
        panel.add(Box.createVerticalStrut(2));
        panel.add(txtTrxId);
        panel.add(Box.createVerticalStrut(8));

        // Nama Pembeli
        JLabel lblCust = new JLabel("Nama Pembeli / Pemesan");
        lblCust.setFont(UITheme.FONT_BOLD);
        lblCust.setForeground(UITheme.TEXT_DARK);
        txtCustomerName = new JTextField();
        UITheme.styleTextField(txtCustomerName);
        txtCustomerName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        panel.add(lblCust);
        panel.add(Box.createVerticalStrut(2));
        panel.add(txtCustomerName);
        panel.add(Box.createVerticalStrut(8));

        // No HP / WhatsApp
        JLabel lblPhone = new JLabel("No. WhatsApp / HP");
        lblPhone.setFont(UITheme.FONT_BOLD);
        lblPhone.setForeground(UITheme.TEXT_DARK);
        txtCustomerPhone = new JTextField();
        UITheme.styleTextField(txtCustomerPhone);
        txtCustomerPhone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        panel.add(lblPhone);
        panel.add(Box.createVerticalStrut(2));
        panel.add(txtCustomerPhone);
        panel.add(Box.createVerticalStrut(8));

        // Jenis Pesanan
        JLabel lblType = new JLabel("Jenis Pesanan");
        lblType.setFont(UITheme.FONT_BOLD);
        lblType.setForeground(UITheme.TEXT_DARK);
        cmbOrderType = new JComboBox<>(OrderType.values());
        cmbOrderType.setFont(UITheme.FONT_REGULAR_MEDIUM);
        cmbOrderType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cmbOrderType.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                OrderType selected = (OrderType) cmbOrderType.getSelectedItem();
                boolean isBouquet = (selected == OrderType.CUSTOM_BOUQUET || selected == OrderType.GIFT_BOX);
                if (isBouquet && txtWrappingFee.getText().trim().equals("0")) {
                    txtWrappingFee.setText("25000");
                }
                recalculateTotals();
            }
        });

        panel.add(lblType);
        panel.add(Box.createVerticalStrut(2));
        panel.add(cmbOrderType);
        panel.add(Box.createVerticalStrut(8));

        // Biaya Wrapping
        JLabel lblWrap = new JLabel("Biaya Wrapping & Pita (Rp)");
        lblWrap.setFont(UITheme.FONT_BOLD);
        lblWrap.setForeground(UITheme.TEXT_DARK);
        txtWrappingFee = new JTextField("0");
        UITheme.styleTextField(txtWrappingFee);
        txtWrappingFee.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
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

        // Greeting Card
        JLabel lblCard = new JLabel("Pesan Kartu Ucapan");
        lblCard.setFont(UITheme.FONT_BOLD);
        lblCard.setForeground(UITheme.TEXT_DARK);
        txtGreetingCard = new JTextField("-");
        UITheme.styleTextField(txtGreetingCard);
        txtGreetingCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        panel.add(lblCard);
        panel.add(Box.createVerticalStrut(2));
        panel.add(txtGreetingCard);
        panel.add(Box.createVerticalStrut(14));

        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(12));

        // PILIH BUNGA
        JLabel lblSec2 = new JLabel("2. Pilih Bunga Persediaan");
        lblSec2.setFont(UITheme.FONT_SUBTITLE);
        lblSec2.setForeground(UITheme.PRIMARY);
        panel.add(lblSec2);
        panel.add(Box.createVerticalStrut(8));

        cmbFlowers = new JComboBox<>();
        cmbFlowers.setFont(UITheme.FONT_REGULAR_MEDIUM);
        cmbFlowers.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        refreshFlowerDropdown();

        panel.add(cmbFlowers);
        panel.add(Box.createVerticalStrut(8));

        // Qty & Add Button
        JPanel pnlQtyAdd = new JPanel(new BorderLayout(8, 0));
        pnlQtyAdd.setOpaque(false);
        pnlQtyAdd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spnQuantity.setFont(new Font("Segoe UI", Font.BOLD, 14));
        spnQuantity.setPreferredSize(new Dimension(75, 36));

        btnAddToCart = new ModernButton("+ Tambah ke Keranjang", UITheme.PRIMARY, Color.WHITE, 8);
        btnAddToCart.addActionListener((ActionEvent e) -> handleAddToCart());

        pnlQtyAdd.add(spnQuantity, BorderLayout.WEST);
        pnlQtyAdd.add(btnAddToCart, BorderLayout.CENTER);
        panel.add(pnlQtyAdd);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createCenterCartPanel() {
        RoundedPanel panel = new RoundedPanel(14, Color.WHITE, UITheme.BORDER, 1);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Header Cart
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitle = new JLabel("🛒 Keranjang Belanja Bunga");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY);
        top.add(lblTitle, BorderLayout.WEST);
        panel.add(top, BorderLayout.NORTH);

        // Table
        String[] columns = {"Kode", "Nama Bunga", "Warna", "Harga", "Qty", "Subtotal"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblCart = new JTable(cartTableModel);
        UITheme.styleTable(tblCart);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblCart.getColumnModel().getColumn(0).setCellRenderer(center);
        tblCart.getColumnModel().getColumn(4).setCellRenderer(center);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblCart.getColumnModel().getColumn(3).setCellRenderer(right);
        tblCart.getColumnModel().getColumn(5).setCellRenderer(right);

        tblCart.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblCart.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblCart.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblCart.getColumnModel().getColumn(3).setPreferredWidth(75);
        tblCart.getColumnModel().getColumn(4).setPreferredWidth(45);
        tblCart.getColumnModel().getColumn(5).setPreferredWidth(85);

        JScrollPane scroll = new JScrollPane(tblCart);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        btnRemoveItem = new ModernButton("🗑️ Hapus Item", UITheme.STATUS_DANGER, Color.WHITE, 6);
        btnRemoveItem.addActionListener((ActionEvent e) -> handleRemoveItem());

        btnClearCart = new ModernButton("🔄 Kosongkan", new Color(241, 245, 249), UITheme.TEXT_DARK, 6);
        btnClearCart.addActionListener((ActionEvent e) -> handleClearCart());

        actions.add(btnRemoveItem);
        actions.add(btnClearCart);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPaymentPanel() {
        RoundedPanel panel = new RoundedPanel(14, Color.WHITE, UITheme.BORDER, 1);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblTitle = new JLabel("3. Pembayaran Kasir");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(14));

        // Subtotal
        panel.add(createRowItem("Subtotal Bunga:", lblSubtotal = new JLabel("Rp 0")));
        panel.add(Box.createVerticalStrut(6));

        // Wrapping
        panel.add(createRowItem("Jasa Wrapping & Pita:", lblWrappingTotal = new JLabel("Rp 0")));
        panel.add(Box.createVerticalStrut(6));

        // Diskon
        JPanel pnlDisc = new JPanel(new BorderLayout(6, 0));
        pnlDisc.setOpaque(false);
        pnlDisc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lblD = new JLabel("Potongan Diskon (Rp):");
        lblD.setFont(UITheme.FONT_BOLD);
        lblD.setForeground(UITheme.TEXT_DARK);
        txtDiscount = new JTextField("0");
        UITheme.styleTextField(txtDiscount);
        txtDiscount.setPreferredSize(new Dimension(95, 30));
        txtDiscount.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                recalculateTotals();
            }
        });
        pnlDisc.add(lblD, BorderLayout.WEST);
        pnlDisc.add(txtDiscount, BorderLayout.EAST);
        panel.add(pnlDisc);
        panel.add(Box.createVerticalStrut(10));

        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(10));

        // GRAND TOTAL
        JLabel lblTotTitle = new JLabel("TOTAL TAGIHAN:");
        lblTotTitle.setFont(UITheme.FONT_BOLD);
        lblTotTitle.setForeground(UITheme.TEXT_MUTED);

        lblGrandTotal = new JLabel("Rp 0");
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblGrandTotal.setForeground(UITheme.PRIMARY);

        panel.add(lblTotTitle);
        panel.add(Box.createVerticalStrut(2));
        panel.add(lblGrandTotal);
        panel.add(Box.createVerticalStrut(14));

        // Uang Pembayaran Tunai
        JLabel lblPay = new JLabel("Uang Tunai Diterima (Rp)");
        lblPay.setFont(UITheme.FONT_BOLD);
        lblPay.setForeground(UITheme.TEXT_DARK);
        txtCashPaid = new JTextField();
        UITheme.styleTextField(txtCashPaid);
        txtCashPaid.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtCashPaid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtCashPaid.setPreferredSize(new Dimension(200, 36));
        txtCashPaid.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                recalculateTotals();
            }
        });

        panel.add(lblPay);
        panel.add(Box.createVerticalStrut(3));
        panel.add(txtCashPaid);
        panel.add(Box.createVerticalStrut(8));

        // QUICK CASH PRESET BUTTONS (Mempermudah kasir!)
        JPanel quickCash = new JPanel(new GridLayout(1, 3, 6, 0));
        quickCash.setOpaque(false);
        quickCash.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        ModernButton btnExact = new ModernButton("Uang Pas", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, 6);
        btnExact.setFont(UITheme.FONT_SMALL);
        btnExact.addActionListener((ActionEvent e) -> setExactCash());

        ModernButton btn100k = new ModernButton("100 Ribu", new Color(241, 245, 249), UITheme.TEXT_DARK, 6);
        btn100k.setFont(UITheme.FONT_SMALL);
        btn100k.addActionListener((ActionEvent e) -> {
            txtCashPaid.setText("100000");
            recalculateTotals();
        });

        ModernButton btn200k = new ModernButton("200 Ribu", new Color(241, 245, 249), UITheme.TEXT_DARK, 6);
        btn200k.setFont(UITheme.FONT_SMALL);
        btn200k.addActionListener((ActionEvent e) -> {
            txtCashPaid.setText("200000");
            recalculateTotals();
        });

        quickCash.add(btnExact);
        quickCash.add(btn100k);
        quickCash.add(btn200k);
        panel.add(quickCash);
        panel.add(Box.createVerticalStrut(10));

        // Kembalian
        panel.add(createRowItem("Kembalian:", lblChange = new JLabel("Rp 0")));
        lblChange.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblChange.setForeground(UITheme.STATUS_INFO);
        panel.add(Box.createVerticalStrut(16));

        // Checkout Button
        btnProcessCheckout = new ModernButton("🧾 BAYAR & CETAK NOTA", UITheme.ACCENT_ROSE, Color.WHITE, 10);
        btnProcessCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProcessCheckout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnProcessCheckout.setPreferredSize(new Dimension(200, 44));
        btnProcessCheckout.addActionListener((ActionEvent e) -> handleProcessCheckout());

        panel.add(btnProcessCheckout);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createRowItem(String title, JLabel valLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel lbl = new JLabel(title);
        lbl.setFont(UITheme.FONT_REGULAR_MEDIUM);
        lbl.setForeground(UITheme.TEXT_DARK);
        valLabel.setFont(UITheme.FONT_BOLD);
        p.add(lbl, BorderLayout.WEST);
        p.add(valLabel, BorderLayout.EAST);
        return p;
    }

    private void refreshFlowerDropdown() {
        cmbFlowers.removeAllItems();
        for (Flower f : stockService.getAllFlowers()) {
            cmbFlowers.addItem(f.getCode() + " - " + f.getName() + " (" + f.getColor() + ") | " +
                               UITheme.formatRupiah(f.getSellPrice()) + " [Stok: " + f.getStock() + "]");
        }
    }

    private void handleAddToCart() {
        int selectedIndex = cmbFlowers.getSelectedIndex();
        if (selectedIndex == -1) return;

        List<Flower> flowers = new ArrayList<>(stockService.getAllFlowers());
        if (selectedIndex >= flowers.size()) return;

        Flower selectedFlower = flowers.get(selectedIndex);
        int qty = (int) spnQuantity.getValue();

        int alreadyInCart = 0;
        for (OrderItem item : cartItems) {
            if (item.getFlower().getCode().equalsIgnoreCase(selectedFlower.getCode())) {
                alreadyInCart += item.getQuantity();
            }
        }

        if (alreadyInCart + qty > selectedFlower.getStock()) {
            JOptionPane.showMessageDialog(this,
                    "Stok tidak mencukupi!\nTersedia: " + selectedFlower.getStock() + " " + selectedFlower.getUnit(),
                    "Stok Kurang", JOptionPane.WARNING_MESSAGE);
            return;
        }

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
        if (cartItems.isEmpty()) return;
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
                UITheme.formatRupiah(item.getUnitPrice()),
                item.getQuantity(),
                UITheme.formatRupiah(item.getSubtotal())
            });
        }
    }

    private void setExactCash() {
        double grandTotal = calculateCurrentGrandTotal();
        txtCashPaid.setText(String.valueOf((long) grandTotal));
        recalculateTotals();
    }

    private double calculateCurrentGrandTotal() {
        double subtotal = 0;
        for (OrderItem item : cartItems) subtotal += item.getSubtotal();

        double wrap = 0;
        try { wrap = Double.parseDouble(txtWrappingFee.getText().trim()); } catch (NumberFormatException ignored) {}

        double disc = 0;
        try { disc = Double.parseDouble(txtDiscount.getText().trim()); } catch (NumberFormatException ignored) {}

        return Math.max(0, subtotal + wrap - disc);
    }

    private void recalculateTotals() {
        double subtotal = 0;
        for (OrderItem item : cartItems) subtotal += item.getSubtotal();

        double wrap = 0;
        try { wrap = Double.parseDouble(txtWrappingFee.getText().trim()); } catch (NumberFormatException ignored) {}

        double disc = 0;
        try { disc = Double.parseDouble(txtDiscount.getText().trim()); } catch (NumberFormatException ignored) {}

        double grandTotal = Math.max(0, subtotal + wrap - disc);

        double cash = 0;
        try { cash = Double.parseDouble(txtCashPaid.getText().trim()); } catch (NumberFormatException ignored) {}

        double change = Math.max(0, cash - grandTotal);

        lblSubtotal.setText(UITheme.formatRupiah(subtotal));
        lblWrappingTotal.setText(UITheme.formatRupiah(wrap));
        lblGrandTotal.setText(UITheme.formatRupiah(grandTotal));
        lblChange.setText(UITheme.formatRupiah(change));
    }

    private void handleProcessCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja masih kosong! Tambahkan bunga terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double grandTotal = calculateCurrentGrandTotal();
        double cashPaid;
        try {
            cashPaid = Double.parseDouble(txtCashPaid.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harap masukkan uang pembayaran yang valid!", "Input Kurang", JOptionPane.ERROR_MESSAGE);
            txtCashPaid.requestFocus();
            return;
        }

        if (cashPaid < grandTotal) {
            JOptionPane.showMessageDialog(this, "Uang pembayaran kurang!\nTotal Bayar: " + UITheme.formatRupiah(grandTotal), "Uang Kurang", JOptionPane.WARNING_MESSAGE);
            txtCashPaid.requestFocus();
            return;
        }

        User currentUser = authService.getCurrentUser();
        String cashier = (currentUser != null) ? currentUser.getFullName() : "Staff Florist";

        Transaction trx = new Transaction(
                currentTransactionId,
                txtCustomerName.getText().trim(),
                txtCustomerPhone.getText().trim(),
                (OrderType) cmbOrderType.getSelectedItem(),
                cashier
        );

        double wrap = 0;
        try { wrap = Double.parseDouble(txtWrappingFee.getText().trim()); } catch (NumberFormatException ignored) {}
        double disc = 0;
        try { disc = Double.parseDouble(txtDiscount.getText().trim()); } catch (NumberFormatException ignored) {}

        trx.setWrappingFee(wrap);
        trx.setDiscount(disc);
        trx.setGreetingCard(txtGreetingCard.getText().trim());
        trx.setCashPaid(cashPaid);

        for (OrderItem item : cartItems) {
            trx.addItem(item);
        }

        String errorMsg = transactionService.processTransaction(trx);

        if (errorMsg != null) {
            JOptionPane.showMessageDialog(this, errorMsg, "Gagal Proses Transaksi", JOptionPane.ERROR_MESSAGE);
        } else {
            showReceiptDialog(trx);
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
        ModernButton btnClose = new ModernButton("Tutup Nota", UITheme.PRIMARY, Color.WHITE, 8);
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
