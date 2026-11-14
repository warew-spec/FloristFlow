package floristflow.view;

import floristflow.model.Flower;
import floristflow.service.StockService;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
 * Form GUI untuk Modul Input Stock (Manajemen Persediaan Bunga).
 * Menerapkan CRUD (Create, Read, Update, Delete), pencarian, dan restock cepat.
 */
public class StockForm extends JFrame {

    private final StockService stockService = StockService.getInstance();
    private final DecimalFormat currencyFormat;

    // Komponen Form Input
    private JTextField txtCode;
    private JTextField txtName;
    private JComboBox<String> cmbCategory;
    private JTextField txtColor;
    private JComboBox<String> cmbUnit;
    private JTextField txtBuyPrice;
    private JTextField txtSellPrice;
    private JTextField txtStock;

    // Tombol Aksi
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnAutoCode;
    private JButton btnQuickRestock;

    // Komponen Tabel & Pencarian
    private JTextField txtSearch;
    private JTable tblFlowers;
    private DefaultTableModel tableModel;

    // Label Statistik
    private JLabel lblTotalVariants;
    private JLabel lblTotalStems;
    private JLabel lblTotalValue;

    // Skema Warna FloristFlow
    private static final Color PRIMARY_COLOR = new Color(46, 117, 89);     // Forest Green
    private static final Color SECONDARY_COLOR = new Color(214, 90, 117);  // Soft Rose
    private static final Color WARNING_COLOR = new Color(230, 126, 34);    // Orange
    private static final Color DANGER_COLOR = new Color(220, 53, 69);      // Red
    private static final Color BG_COLOR = new Color(245, 247, 248);
    private static final Color TEXT_DARK = new Color(40, 44, 52);

    public StockForm() {
        // Inisialisasi format mata uang Rupiah
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("id-ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        this.currencyFormat = new DecimalFormat("Rp #,###", symbols);

        initComponents();
        loadTableData();
        updateStatistics();
        clearForm();
    }

    private void initComponents() {
        setTitle("FloristFlow - Manajemen Input Stock Bunga");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1080, 680);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // Top Header
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("🌸 Manajemen & Input Stock Bunga FloristFlow");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JButton btnBack = new JButton("← Kembali ke Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setBackground(new Color(36, 92, 70));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener((ActionEvent e) -> {
            new MainDashboardForm().setVisible(true);
            dispose();
        });

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(btnBack, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Center Container (Split: Left Form, Right Table)
        JPanel mainContent = new JPanel(new BorderLayout(15, 0));
        mainContent.setBackground(BG_COLOR);
        mainContent.setBorder(new EmptyBorder(15, 15, 15, 15));

        // LEFT PANEL: Form Input
        JPanel leftPanel = createFormPanel();
        mainContent.add(leftPanel, BorderLayout.WEST);

        // RIGHT PANEL: Tabel dan Pencarian
        JPanel rightPanel = createTablePanel();
        mainContent.add(rightPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setPreferredSize(new Dimension(360, 0));
        formContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                new EmptyBorder(15, 18, 15, 18)
        ));

        JLabel lblFormTitle = new JLabel("Form Data Bunga");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(PRIMARY_COLOR);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(lblFormTitle);
        formContainer.add(Box.createVerticalStrut(12));

        // Kode Bunga + Auto Button
        JLabel lblCode = new JLabel("Kode Bunga");
        lblCode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCode = new JTextField();
        txtCode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnAutoCode = new JButton("Auto");
        btnAutoCode.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnAutoCode.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAutoCode.addActionListener((ActionEvent e) -> txtCode.setText(stockService.generateNextCode()));

        JPanel pnlCode = new JPanel(new BorderLayout(5, 0));
        pnlCode.setOpaque(false);
        pnlCode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnlCode.add(txtCode, BorderLayout.CENTER);
        pnlCode.add(btnAutoCode, BorderLayout.EAST);

        formContainer.add(lblCode);
        formContainer.add(Box.createVerticalStrut(3));
        formContainer.add(pnlCode);
        formContainer.add(Box.createVerticalStrut(8));

        // Nama Bunga
        JLabel lblName = new JLabel("Nama Bunga");
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtName = new JTextField();
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        formContainer.add(lblName);
        formContainer.add(Box.createVerticalStrut(3));
        formContainer.add(txtName);
        formContainer.add(Box.createVerticalStrut(8));

        // Kategori
        JLabel lblCategory = new JLabel("Kategori Bunga");
        lblCategory.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String[] categories = {"Mawar", "Lily", "Tulip", "Sunflower", "Carnation", "Hydrangea", "Filler", "Lainnya"};
        cmbCategory = new JComboBox<>(categories);
        cmbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbCategory.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        formContainer.add(lblCategory);
        formContainer.add(Box.createVerticalStrut(3));
        formContainer.add(cmbCategory);
        formContainer.add(Box.createVerticalStrut(8));

        // Warna Kelopak & Satuan
        JPanel pnlColorUnit = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlColorUnit.setOpaque(false);
        pnlColorUnit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel pnlCol1 = new JPanel();
        pnlCol1.setLayout(new BoxLayout(pnlCol1, BoxLayout.Y_AXIS));
        pnlCol1.setOpaque(false);
        JLabel lblColor = new JLabel("Warna");
        lblColor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtColor = new JTextField();
        txtColor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtColor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnlCol1.add(lblColor);
        pnlCol1.add(Box.createVerticalStrut(3));
        pnlCol1.add(txtColor);

        JPanel pnlCol2 = new JPanel();
        pnlCol2.setLayout(new BoxLayout(pnlCol2, BoxLayout.Y_AXIS));
        pnlCol2.setOpaque(false);
        JLabel lblUnit = new JLabel("Satuan");
        lblUnit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String[] units = {"Tangkai", "Ikat", "Pot", "Buket"};
        cmbUnit = new JComboBox<>(units);
        cmbUnit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbUnit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnlCol2.add(lblUnit);
        pnlCol2.add(Box.createVerticalStrut(3));
        pnlCol2.add(cmbUnit);

        pnlColorUnit.add(pnlCol1);
        pnlColorUnit.add(pnlCol2);

        formContainer.add(pnlColorUnit);
        formContainer.add(Box.createVerticalStrut(8));

        // Harga Beli & Harga Jual
        JPanel pnlPrices = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlPrices.setOpaque(false);
        pnlPrices.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel pnlBuy = new JPanel();
        pnlBuy.setLayout(new BoxLayout(pnlBuy, BoxLayout.Y_AXIS));
        pnlBuy.setOpaque(false);
        JLabel lblBuy = new JLabel("Harga Beli (Rp)");
        lblBuy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuyPrice = new JTextField();
        txtBuyPrice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuyPrice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnlBuy.add(lblBuy);
        pnlBuy.add(Box.createVerticalStrut(3));
        pnlBuy.add(txtBuyPrice);

        JPanel pnlSell = new JPanel();
        pnlSell.setLayout(new BoxLayout(pnlSell, BoxLayout.Y_AXIS));
        pnlSell.setOpaque(false);
        JLabel lblSell = new JLabel("Harga Jual (Rp)");
        lblSell.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSellPrice = new JTextField();
        txtSellPrice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSellPrice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnlSell.add(lblSell);
        pnlSell.add(Box.createVerticalStrut(3));
        pnlSell.add(txtSellPrice);

        pnlPrices.add(pnlBuy);
        pnlPrices.add(pnlSell);

        formContainer.add(pnlPrices);
        formContainer.add(Box.createVerticalStrut(8));

        // Stok
        JLabel lblStock = new JLabel("Jumlah Stok Persediaan");
        lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtStock = new JTextField();
        txtStock.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtStock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        formContainer.add(lblStock);
        formContainer.add(Box.createVerticalStrut(3));
        formContainer.add(txtStock);
        formContainer.add(Box.createVerticalStrut(15));

        // Buttons Action Panel
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 8, 8));
        pnlButtons.setOpaque(false);
        pnlButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        btnAdd = new JButton("➕ Tambah");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setBackground(PRIMARY_COLOR);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener((ActionEvent e) -> handleAddFlower());

        btnUpdate = new JButton("✏️ Ubah");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUpdate.setBackground(WARNING_COLOR);
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdate.addActionListener((ActionEvent e) -> handleUpdateFlower());

        btnDelete = new JButton("🗑️ Hapus");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setBackground(DANGER_COLOR);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener((ActionEvent e) -> handleDeleteFlower());

        btnClear = new JButton("🔄 Reset");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClear.setBackground(new Color(230, 235, 240));
        btnClear.setForeground(TEXT_DARK);
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener((ActionEvent e) -> clearForm());

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnClear);

        formContainer.add(pnlButtons);
        formContainer.add(Box.createVerticalGlue());

        return formContainer;
    }

    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setBackground(BG_COLOR);

        // Top of Table: Search Bar & KPI Summary Card
        JPanel topSection = new JPanel(new BorderLayout(10, 0));
        topSection.setBackground(Color.WHITE);
        topSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍 Cari:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTableData(txtSearch.getText().trim());
            }
        });

        JButton btnResetSearch = new JButton("Clear");
        btnResetSearch.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnResetSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResetSearch.addActionListener((ActionEvent e) -> {
            txtSearch.setText("");
            loadTableData();
        });

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnResetSearch);

        // KPI Summary Badges
        JPanel kpiPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        kpiPanel.setOpaque(false);

        lblTotalVariants = new JLabel("🌺 Varian: 0");
        lblTotalVariants.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalVariants.setForeground(PRIMARY_COLOR);

        lblTotalStems = new JLabel("📦 Stok: 0");
        lblTotalStems.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalStems.setForeground(SECONDARY_COLOR);

        lblTotalValue = new JLabel("💰 Nilai Aset: Rp 0");
        lblTotalValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalValue.setForeground(new Color(40, 116, 166));

        kpiPanel.add(lblTotalVariants);
        kpiPanel.add(lblTotalStems);
        kpiPanel.add(lblTotalValue);

        topSection.add(searchPanel, BorderLayout.WEST);
        topSection.add(kpiPanel, BorderLayout.EAST);

        tableContainer.add(topSection, BorderLayout.NORTH);

        // Table JTable
        String[] columnNames = {"Kode", "Nama Bunga", "Kategori", "Warna", "Satuan", "Harga Beli", "Harga Jual", "Laba/Unit", "Stok"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only cells
            }
        };

        tblFlowers = new JTable(tableModel);
        tblFlowers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblFlowers.setRowHeight(26);
        tblFlowers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblFlowers.setGridColor(new Color(230, 235, 240));

        JTableHeader header = tblFlowers.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(238, 242, 245));
        header.setForeground(TEXT_DARK);

        // Center align text for Code, Unit, and Stock
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblFlowers.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblFlowers.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblFlowers.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);

        // Right align for currency columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tblFlowers.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        tblFlowers.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        tblFlowers.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);

        // Column widths
        tblFlowers.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblFlowers.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblFlowers.getColumnModel().getColumn(2).setPreferredWidth(85);
        tblFlowers.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblFlowers.getColumnModel().getColumn(4).setPreferredWidth(65);
        tblFlowers.getColumnModel().getColumn(5).setPreferredWidth(85);
        tblFlowers.getColumnModel().getColumn(6).setPreferredWidth(85);
        tblFlowers.getColumnModel().getColumn(7).setPreferredWidth(85);
        tblFlowers.getColumnModel().getColumn(8).setPreferredWidth(55);

        // Row Selection Listener
        tblFlowers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblFlowers.getSelectedRow();
                if (selectedRow != -1) {
                    populateFormFromSelectedRow(selectedRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblFlowers);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230), 1));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Bar: Quick Restock
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomBar.setOpaque(false);

        btnQuickRestock = new JButton("⚡ Tambah Stok Cepat (+Restock)");
        btnQuickRestock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnQuickRestock.setBackground(new Color(40, 116, 166));
        btnQuickRestock.setForeground(Color.WHITE);
        btnQuickRestock.setFocusPainted(false);
        btnQuickRestock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuickRestock.addActionListener((ActionEvent e) -> handleQuickRestock());

        bottomBar.add(btnQuickRestock);
        tableContainer.add(bottomBar, BorderLayout.SOUTH);

        return tableContainer;
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        for (Flower f : stockService.getAllFlowers()) {
            addFlowerToTableModel(f);
        }
    }

    private void filterTableData(String keyword) {
        tableModel.setRowCount(0);
        List<Flower> results = stockService.searchFlowers(keyword);
        for (Flower f : results) {
            addFlowerToTableModel(f);
        }
    }

    private void addFlowerToTableModel(Flower f) {
        tableModel.addRow(new Object[]{
            f.getCode(),
            f.getName(),
            f.getCategory(),
            f.getColor(),
            f.getUnit(),
            currencyFormat.format(f.getBuyPrice()),
            currencyFormat.format(f.getSellPrice()),
            currencyFormat.format(f.calculateProfitPerUnit()),
            f.getStock()
        });
    }

    private void updateStatistics() {
        lblTotalVariants.setText("🌺 Varian: " + stockService.getTotalFlowerTypes());
        lblTotalStems.setText("📦 Total Stok: " + stockService.getTotalStockCount());
        lblTotalValue.setText("💰 Nilai Aset: " + currencyFormat.format(stockService.getTotalStockAssetValue()));
    }

    private void populateFormFromSelectedRow(int row) {
        String code = tableModel.getValueAt(row, 0).toString();
        Flower flower = stockService.getFlowerByCode(code);
        if (flower != null) {
            txtCode.setText(flower.getCode());
            txtCode.setEditable(false);
            txtName.setText(flower.getName());
            cmbCategory.setSelectedItem(flower.getCategory());
            txtColor.setText(flower.getColor());
            cmbUnit.setSelectedItem(flower.getUnit());
            txtBuyPrice.setText(String.valueOf((long) flower.getBuyPrice()));
            txtSellPrice.setText(String.valueOf((long) flower.getSellPrice()));
            txtStock.setText(String.valueOf(flower.getStock()));
        }
    }

    private void handleAddFlower() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();
        String category = (String) cmbCategory.getSelectedItem();
        String color = txtColor.getText().trim();
        String unit = (String) cmbUnit.getSelectedItem();

        if (code.isEmpty() || name.isEmpty() || color.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Harap lengkapi Kode, Nama, dan Warna Bunga!",
                    "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double buyPrice;
        double sellPrice;
        int stock;

        try {
            buyPrice = Double.parseDouble(txtBuyPrice.getText().trim());
            sellPrice = Double.parseDouble(txtSellPrice.getText().trim());
            stock = Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Harga dan Stok harus berupa angka valid!",
                    "Format Angka Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Flower newFlower = new Flower(code, name, category, color, buyPrice, sellPrice, stock, unit);
        boolean success = stockService.addFlower(newFlower);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Bunga " + name + " (" + code + ") berhasil ditambahkan!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadTableData();
            updateStatistics();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Kode Bunga " + code + " sudah terdaftar! Gunakan kode lain atau klik [Auto].",
                    "Gagal Tambah", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateFlower() {
        String code = txtCode.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih baris data bunga di tabel yang ingin diubah!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String category = (String) cmbCategory.getSelectedItem();
        String color = txtColor.getText().trim();
        String unit = (String) cmbUnit.getSelectedItem();

        double buyPrice;
        double sellPrice;
        int stock;

        try {
            buyPrice = Double.parseDouble(txtBuyPrice.getText().trim());
            sellPrice = Double.parseDouble(txtSellPrice.getText().trim());
            stock = Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Harga dan Stok harus berupa angka valid!",
                    "Format Angaran Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Flower updatedFlower = new Flower(code, name, category, color, buyPrice, sellPrice, stock, unit);
        boolean success = stockService.updateFlower(updatedFlower);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Data bunga " + code + " berhasil diperbarui!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadTableData();
            updateStatistics();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Bunga dengan kode " + code + " tidak ditemukan.",
                    "Gagal Ubah", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteFlower() {
        String code = txtCode.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data bunga dari tabel yang ingin dihapus!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus bunga (" + code + ") " + txtName.getText() + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = stockService.deleteFlower(code);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Bunga " + code + " berhasil dihapus dari sistem.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
                updateStatistics();
                clearForm();
            }
        }
    }

    private void handleQuickRestock() {
        int selectedRow = tblFlowers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Silakan klik salah satu bunga di tabel yang ingin di-restock!",
                    "Pilih Bunga", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = tableModel.getValueAt(selectedRow, 0).toString();
        String flowerName = tableModel.getValueAt(selectedRow, 1).toString();

        String input = JOptionPane.showInputDialog(this,
                "Masukkan jumlah tambahan stok untuk:\n" + flowerName + " (" + code + "):",
                "Restock Cepat", JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int qty = Integer.parseInt(input.trim());
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah harus lebih besar dari 0!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                stockService.restockFlower(code, qty);
                loadTableData();
                updateStatistics();
                clearForm();

                JOptionPane.showMessageDialog(this,
                        "Berhasil menambahkan " + qty + " stok untuk " + flowerName + "!",
                        "Restock Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Harap masukkan angka bilangan bulat yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtCode.setText(stockService.generateNextCode());
        txtCode.setEditable(true);
        txtName.setText("");
        cmbCategory.setSelectedIndex(0);
        txtColor.setText("");
        cmbUnit.setSelectedIndex(0);
        txtBuyPrice.setText("");
        txtSellPrice.setText("");
        txtStock.setText("");
        tblFlowers.clearSelection();
        txtCode.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StockForm().setVisible(true);
        });
    }
}
