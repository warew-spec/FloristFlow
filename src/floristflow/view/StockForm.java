package floristflow.view;

import floristflow.model.Flower;
import floristflow.service.StockService;
import floristflow.view.ui.ModernButton;
import floristflow.view.ui.RoundedPanel;
import floristflow.view.ui.UITheme;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Form GUI Input Stock Bunga FloristFlow dengan tema Modern Floral Boutique.
 */
public class StockForm extends JFrame {

    private final StockService stockService = StockService.getInstance();

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
    private ModernButton btnAdd;
    private ModernButton btnUpdate;
    private ModernButton btnDelete;
    private ModernButton btnClear;
    private ModernButton btnAutoCode;
    private ModernButton btnQuickRestock;

    // Komponen Tabel & Pencarian
    private JTextField txtSearch;
    private JTable tblFlowers;
    private DefaultTableModel tableModel;

    // Label Statistik
    private JLabel lblTotalVariants;
    private JLabel lblTotalStems;
    private JLabel lblTotalValue;

    public StockForm() {
        initComponents();
        loadTableData();
        updateStatistics();
        clearForm();
    }

    private void initComponents() {
        setTitle("FloristFlow - Manajemen Inventaris Stok Bunga");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1120, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY);
        topBar.setBorder(new EmptyBorder(14, 25, 14, 25));

        JLabel lblTitle = new JLabel("🌸 Manajemen & Input Stock Bunga");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        ModernButton btnBack = new ModernButton("← Dashboard", new Color(28, 74, 57), Color.WHITE, 8);
        btnBack.setFont(UITheme.FONT_BOLD);
        btnBack.addActionListener((ActionEvent e) -> {
            new MainDashboardForm().setVisible(true);
            dispose();
        });

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(btnBack, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // MAIN CONTAINER
        JPanel mainContent = new JPanel(new BorderLayout(15, 0));
        mainContent.setBackground(UITheme.BG_MAIN);
        mainContent.setBorder(new EmptyBorder(15, 20, 15, 20));

        // LEFT: Form Card
        JPanel leftPanel = createFormPanel();
        mainContent.add(leftPanel, BorderLayout.WEST);

        // RIGHT: Table Card
        JPanel rightPanel = createTablePanel();
        mainContent.add(rightPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        RoundedPanel formCard = new RoundedPanel(14, Color.WHITE, UITheme.BORDER, 1);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(360, 0));
        formCard.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel lblFormTitle = new JLabel("Form Data Bunga");
        lblFormTitle.setFont(UITheme.FONT_SUBTITLE);
        lblFormTitle.setForeground(UITheme.PRIMARY);
        formCard.add(lblFormTitle);
        formCard.add(Box.createVerticalStrut(12));

        // Kode Bunga + Tombol Auto
        JLabel lblCode = new JLabel("Kode Bunga");
        lblCode.setFont(UITheme.FONT_BOLD);
        lblCode.setForeground(UITheme.TEXT_DARK);
        txtCode = new JTextField();
        UITheme.styleTextField(txtCode);

        btnAutoCode = new ModernButton("Auto", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, 6);
        btnAutoCode.setFont(UITheme.FONT_SMALL);
        btnAutoCode.setPreferredSize(new Dimension(65, 32));
        btnAutoCode.addActionListener((ActionEvent e) -> txtCode.setText(stockService.generateNextCode()));

        JPanel pnlCode = new JPanel(new BorderLayout(6, 0));
        pnlCode.setOpaque(false);
        pnlCode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        pnlCode.add(txtCode, BorderLayout.CENTER);
        pnlCode.add(btnAutoCode, BorderLayout.EAST);

        formCard.add(lblCode);
        formCard.add(Box.createVerticalStrut(3));
        formCard.add(pnlCode);
        formCard.add(Box.createVerticalStrut(8));

        // Nama Bunga
        JLabel lblName = new JLabel("Nama Bunga");
        lblName.setFont(UITheme.FONT_BOLD);
        lblName.setForeground(UITheme.TEXT_DARK);
        txtName = new JTextField();
        UITheme.styleTextField(txtName);
        txtName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        formCard.add(lblName);
        formCard.add(Box.createVerticalStrut(3));
        formCard.add(txtName);
        formCard.add(Box.createVerticalStrut(8));

        // Kategori
        JLabel lblCat = new JLabel("Kategori Bunga");
        lblCat.setFont(UITheme.FONT_BOLD);
        lblCat.setForeground(UITheme.TEXT_DARK);
        String[] categories = {"Mawar", "Lily", "Tulip", "Sunflower", "Carnation", "Hydrangea", "Filler", "Lainnya"};
        cmbCategory = new JComboBox<>(categories);
        cmbCategory.setFont(UITheme.FONT_REGULAR_MEDIUM);
        cmbCategory.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        formCard.add(lblCat);
        formCard.add(Box.createVerticalStrut(3));
        formCard.add(cmbCategory);
        formCard.add(Box.createVerticalStrut(8));

        // Warna & Satuan
        JPanel pnlColUnit = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlColUnit.setOpaque(false);
        pnlColUnit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        p1.setOpaque(false);
        JLabel lblColor = new JLabel("Warna");
        lblColor.setFont(UITheme.FONT_BOLD);
        lblColor.setForeground(UITheme.TEXT_DARK);
        txtColor = new JTextField();
        UITheme.styleTextField(txtColor);
        txtColor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        p1.add(lblColor);
        p1.add(Box.createVerticalStrut(3));
        p1.add(txtColor);

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        p2.setOpaque(false);
        JLabel lblUnit = new JLabel("Satuan");
        lblUnit.setFont(UITheme.FONT_BOLD);
        lblUnit.setForeground(UITheme.TEXT_DARK);
        String[] units = {"Tangkai", "Ikat", "Pot", "Buket"};
        cmbUnit = new JComboBox<>(units);
        cmbUnit.setFont(UITheme.FONT_REGULAR_MEDIUM);
        cmbUnit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        p2.add(lblUnit);
        p2.add(Box.createVerticalStrut(3));
        p2.add(cmbUnit);

        pnlColUnit.add(p1);
        pnlColUnit.add(p2);
        formCard.add(pnlColUnit);
        formCard.add(Box.createVerticalStrut(8));

        // Harga Beli & Harga Jual
        JPanel pnlPrices = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlPrices.setOpaque(false);
        pnlPrices.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel pBuy = new JPanel();
        pBuy.setLayout(new BoxLayout(pBuy, BoxLayout.Y_AXIS));
        pBuy.setOpaque(false);
        JLabel lblBuy = new JLabel("Harga Modal (Rp)");
        lblBuy.setFont(UITheme.FONT_BOLD);
        lblBuy.setForeground(UITheme.TEXT_DARK);
        txtBuyPrice = new JTextField();
        UITheme.styleTextField(txtBuyPrice);
        txtBuyPrice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        pBuy.add(lblBuy);
        pBuy.add(Box.createVerticalStrut(3));
        pBuy.add(txtBuyPrice);

        JPanel pSell = new JPanel();
        pSell.setLayout(new BoxLayout(pSell, BoxLayout.Y_AXIS));
        pSell.setOpaque(false);
        JLabel lblSell = new JLabel("Harga Jual (Rp)");
        lblSell.setFont(UITheme.FONT_BOLD);
        lblSell.setForeground(UITheme.TEXT_DARK);
        txtSellPrice = new JTextField();
        UITheme.styleTextField(txtSellPrice);
        txtSellPrice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        pSell.add(lblSell);
        pSell.add(Box.createVerticalStrut(3));
        pSell.add(txtSellPrice);

        pnlPrices.add(pBuy);
        pnlPrices.add(pSell);
        formCard.add(pnlPrices);
        formCard.add(Box.createVerticalStrut(8));

        // Stok
        JLabel lblStk = new JLabel("Kuantitas Stok Bunga");
        lblStk.setFont(UITheme.FONT_BOLD);
        lblStk.setForeground(UITheme.TEXT_DARK);
        txtStock = new JTextField();
        UITheme.styleTextField(txtStock);
        txtStock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        formCard.add(lblStk);
        formCard.add(Box.createVerticalStrut(3));
        formCard.add(txtStock);
        formCard.add(Box.createVerticalStrut(14));

        // Tombol Aksi Form
        JPanel pnlBtns = new JPanel(new GridLayout(2, 2, 8, 8));
        pnlBtns.setOpaque(false);
        pnlBtns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

        btnAdd = new ModernButton("➕ Simpan", UITheme.PRIMARY, Color.WHITE, 8);
        btnAdd.addActionListener((ActionEvent e) -> handleAddFlower());

        btnUpdate = new ModernButton("✏️ Ubah", UITheme.STATUS_WARNING, Color.WHITE, 8);
        btnUpdate.addActionListener((ActionEvent e) -> handleUpdateFlower());

        btnDelete = new ModernButton("🗑️ Hapus", UITheme.STATUS_DANGER, Color.WHITE, 8);
        btnDelete.addActionListener((ActionEvent e) -> handleDeleteFlower());

        btnClear = new ModernButton("🔄 Reset", new Color(241, 245, 249), UITheme.TEXT_DARK, 8);
        btnClear.addActionListener((ActionEvent e) -> clearForm());

        pnlBtns.add(btnAdd);
        pnlBtns.add(btnUpdate);
        pnlBtns.add(btnDelete);
        pnlBtns.add(btnClear);

        formCard.add(pnlBtns);
        formCard.add(Box.createVerticalGlue());

        return formCard;
    }

    private JPanel createTablePanel() {
        RoundedPanel tableCard = new RoundedPanel(14, Color.WHITE, UITheme.BORDER, 1);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(15, 18, 15, 18));

        // Top Search & KPI Stats
        JPanel topSection = new JPanel(new BorderLayout(10, 0));
        topSection.setOpaque(false);

        // Search Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setOpaque(false);
        JLabel lblS = new JLabel("🔍 Cari:");
        lblS.setFont(UITheme.FONT_BOLD);
        txtSearch = new JTextField(15);
        UITheme.styleTextField(txtSearch);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTableData(txtSearch.getText().trim());
            }
        });

        ModernButton btnClearSearch = new ModernButton("Reset", new Color(241, 245, 249), UITheme.TEXT_DARK, 6);
        btnClearSearch.setPreferredSize(new Dimension(60, 30));
        btnClearSearch.addActionListener((ActionEvent e) -> {
            txtSearch.setText("");
            loadTableData();
        });

        searchBar.add(lblS);
        searchBar.add(txtSearch);
        searchBar.add(btnClearSearch);

        // KPI Badges
        JPanel kpiBadges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        kpiBadges.setOpaque(false);

        lblTotalVariants = new JLabel("🌺 0 Varian");
        lblTotalVariants.setFont(UITheme.FONT_BOLD);
        lblTotalVariants.setForeground(UITheme.PRIMARY);

        lblTotalStems = new JLabel("📦 0 Tangkai");
        lblTotalStems.setFont(UITheme.FONT_BOLD);
        lblTotalStems.setForeground(UITheme.ACCENT_ROSE);

        lblTotalValue = new JLabel("💰 Aset: Rp 0");
        lblTotalValue.setFont(UITheme.FONT_BOLD);
        lblTotalValue.setForeground(UITheme.STATUS_INFO);

        kpiBadges.add(lblTotalVariants);
        kpiBadges.add(lblTotalStems);
        kpiBadges.add(lblTotalValue);

        topSection.add(searchBar, BorderLayout.WEST);
        topSection.add(kpiBadges, BorderLayout.EAST);
        tableCard.add(topSection, BorderLayout.NORTH);

        // Table
        String[] columns = {"Kode", "Nama Bunga", "Kategori", "Warna", "Satuan", "Harga Modal", "Harga Jual", "Laba/Unit", "Status Stok"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblFlowers = new JTable(tableModel);
        UITheme.styleTable(tblFlowers);

        // Center alignments
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        tblFlowers.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        tblFlowers.getColumnModel().getColumn(4).setCellRenderer(centerRender);
        tblFlowers.getColumnModel().getColumn(8).setCellRenderer(centerRender);

        // Right alignments
        DefaultTableCellRenderer rightRender = new DefaultTableCellRenderer();
        rightRender.setHorizontalAlignment(SwingConstants.RIGHT);
        tblFlowers.getColumnModel().getColumn(5).setCellRenderer(rightRender);
        tblFlowers.getColumnModel().getColumn(6).setCellRenderer(rightRender);
        tblFlowers.getColumnModel().getColumn(7).setCellRenderer(rightRender);

        tblFlowers.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblFlowers.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblFlowers.getColumnModel().getColumn(2).setPreferredWidth(85);
        tblFlowers.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblFlowers.getColumnModel().getColumn(4).setPreferredWidth(65);
        tblFlowers.getColumnModel().getColumn(5).setPreferredWidth(85);
        tblFlowers.getColumnModel().getColumn(6).setPreferredWidth(85);
        tblFlowers.getColumnModel().getColumn(7).setPreferredWidth(85);
        tblFlowers.getColumnModel().getColumn(8).setPreferredWidth(95);

        tblFlowers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblFlowers.getSelectedRow();
                if (row != -1) {
                    populateFormFromSelectedRow(row);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblFlowers);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        tableCard.add(scroll, BorderLayout.CENTER);

        // Bottom Action: Restock
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomBar.setOpaque(false);

        btnQuickRestock = new ModernButton("⚡ Tambah Stok Cepat (+Restock)", UITheme.PRIMARY, Color.WHITE, 8);
        btnQuickRestock.setPreferredSize(new Dimension(230, 36));
        btnQuickRestock.addActionListener((ActionEvent e) -> handleQuickRestock());

        bottomBar.add(btnQuickRestock);
        tableCard.add(bottomBar, BorderLayout.SOUTH);

        return tableCard;
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        for (Flower f : stockService.getAllFlowers()) {
            addFlowerToTable(f);
        }
    }

    private void filterTableData(String keyword) {
        tableModel.setRowCount(0);
        List<Flower> results = stockService.searchFlowers(keyword);
        for (Flower f : results) {
            addFlowerToTable(f);
        }
    }

    private void addFlowerToTable(Flower f) {
        String stockBadge;
        if (f.getStock() == 0) {
            stockBadge = "🔴 Habis (0)";
        } else if (f.getStock() < 10) {
            stockBadge = "🟠 " + f.getStock() + " " + f.getUnit();
        } else {
            stockBadge = "🟢 " + f.getStock() + " " + f.getUnit();
        }

        tableModel.addRow(new Object[]{
            f.getCode(),
            f.getName(),
            f.getCategory(),
            f.getColor(),
            f.getUnit(),
            UITheme.formatRupiah(f.getBuyPrice()),
            UITheme.formatRupiah(f.getSellPrice()),
            UITheme.formatRupiah(f.calculateProfitPerUnit()),
            stockBadge
        });
    }

    private void updateStatistics() {
        lblTotalVariants.setText("🌺 " + stockService.getTotalFlowerTypes() + " Varian");
        lblTotalStems.setText("📦 " + stockService.getTotalStockCount() + " Tangkai");
        lblTotalValue.setText("💰 Aset: " + UITheme.formatRupiah(stockService.getTotalStockAssetValue()));
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
            JOptionPane.showMessageDialog(this, "Harap lengkapi Kode, Nama, dan Warna Bunga!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka valid!", "Format Angka Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Flower newFlower = new Flower(code, name, category, color, buyPrice, sellPrice, stock, unit);
        boolean success = stockService.addFlower(newFlower);

        if (success) {
            JOptionPane.showMessageDialog(this, "Bunga " + name + " (" + code + ") berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadTableData();
            updateStatistics();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Kode Bunga " + code + " sudah terdaftar!", "Gagal Tambah", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateFlower() {
        String code = txtCode.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih baris data bunga di tabel yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka valid!", "Format Angka Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Flower updatedFlower = new Flower(code, name, category, color, buyPrice, sellPrice, stock, unit);
        boolean success = stockService.updateFlower(updatedFlower);

        if (success) {
            JOptionPane.showMessageDialog(this, "Data bunga " + code + " berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadTableData();
            updateStatistics();
            clearForm();
        }
    }

    private void handleDeleteFlower() {
        String code = txtCode.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data bunga dari tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus bunga (" + code + ") " + txtName.getText() + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = stockService.deleteFlower(code);
            if (success) {
                JOptionPane.showMessageDialog(this, "Bunga " + code + " berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
                updateStatistics();
                clearForm();
            }
        }
    }

    private void handleQuickRestock() {
        int selectedRow = tblFlowers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan klik bunga di tabel yang ingin di-restock!", "Pilih Bunga", JOptionPane.WARNING_MESSAGE);
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

                JOptionPane.showMessageDialog(this, "Berhasil menambahkan " + qty + " stok untuk " + flowerName + "!", "Restock Berhasil", JOptionPane.INFORMATION_MESSAGE);
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
