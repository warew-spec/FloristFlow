package floristflow.view;

import floristflow.model.Role;
import floristflow.model.User;
import floristflow.service.AuthService;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Form Dashboard Utama FloristFlow setelah pengguna berhasil Login.
 * Menyediakan navigasi ke modul: Input Stock, Proses Bisnis, Rekapitulasi, serta Logout.
 */
public class MainDashboardForm extends JFrame {

    private final AuthService authService = AuthService.getInstance();
    private final User currentUser;

    // Skema Warna FloristFlow
    private static final Color PRIMARY_COLOR = new Color(46, 117, 89);     // Forest Green
    private static final Color SECONDARY_COLOR = new Color(214, 90, 117);  // Rose Pink
    private static final Color ACCENT_GOLD = new Color(212, 160, 23);      // Warm Gold
    private static final Color BG_COLOR = new Color(245, 247, 248);
    private static final Color TEXT_DARK = new Color(40, 44, 52);

    public MainDashboardForm() {
        this.currentUser = authService.getCurrentUser();
        initComponents();
    }

    private void initComponents() {
        setTitle("FloristFlow - Dashboard Utama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // Top Navigation Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblBrand = new JLabel("🌸 FloristFlow Management");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        String userName = (currentUser != null) ? currentUser.getFullName() : "Tamu";
        String userRole = (currentUser != null) ? currentUser.getRole().getDisplayName() : "-";

        JLabel lblUserInfo = new JLabel("👤 " + userName + " (" + userRole + ")");
        lblUserInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUserInfo.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener((ActionEvent e) -> handleLogout());

        userPanel.add(lblUserInfo);
        userPanel.add(btnLogout);

        topBar.add(lblBrand, BorderLayout.WEST);
        topBar.add(userPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Center Content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BG_COLOR);
        mainContent.setBorder(new EmptyBorder(25, 35, 25, 35));

        // Welcome Banner Card
        JPanel bannerCard = new JPanel(new BorderLayout());
        bannerCard.setBackground(Color.WHITE);
        bannerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        bannerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 235), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblWelcome = new JLabel("Selamat Datang di FloristFlow, " + userName + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblWelcome.setForeground(TEXT_DARK);

        String accessDesc = (currentUser != null) ? currentUser.getAccessDescription() : "";
        JLabel lblAccess = new JLabel(accessDesc);
        lblAccess.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblAccess.setForeground(new Color(108, 117, 125));

        bannerCard.add(lblWelcome, BorderLayout.NORTH);
        bannerCard.add(lblAccess, BorderLayout.SOUTH);

        mainContent.add(bannerCard);
        mainContent.add(Box.createVerticalStrut(25));

        // Section Title
        JLabel lblMenuTitle = new JLabel("Menu & Modul Aplikasi:");
        lblMenuTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMenuTitle.setForeground(TEXT_DARK);
        lblMenuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(lblMenuTitle);
        mainContent.add(Box.createVerticalStrut(15));

        // Grid Menu Cards (Input Stock, Proses Bisnis, Rekapitulasi)
        JPanel gridMenu = new JPanel(new GridLayout(1, 3, 20, 0));
        gridMenu.setOpaque(false);
        gridMenu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        gridMenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 1. Modul Input Stock
        gridMenu.add(createMenuCard(
                "📦 Input Stock",
                "Kelola katalog bunga, harga, kategori, dan restock persediaan.",
                PRIMARY_COLOR,
                "Buka Modul Stock",
                () -> handleOpenStock()
        ));

        // 2. Modul Proses Bisnis (Transaksi)
        gridMenu.add(createMenuCard(
                "🧾 Proses Bisnis",
                "Transaksi kasir, pembuatan pesanan buket, dan cetak nota belanja.",
                SECONDARY_COLOR,
                "Buka Transaksi",
                () -> handleOpenBusinessProcess()
        ));

        // 3. Modul Rekapitulasi (Laporan)
        gridMenu.add(createMenuCard(
                "📊 Rekapitulasi",
                "Laporan penjualan harian, omzet, dan ringkasan bunga terlaris.",
                ACCENT_GOLD,
                "Buka Rekapitulasi",
                () -> handleOpenReport()
        ));

        mainContent.add(gridMenu);
        add(mainContent, BorderLayout.CENTER);

        // Footer / Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(235, 238, 240));
        statusBar.setBorder(new EmptyBorder(8, 20, 8, 20));

        JLabel lblStatus = new JLabel("FloristFlow System • Pemrograman Berorientasi Objek (PBO) • Siap digunakan");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(100, 110, 120));

        statusBar.add(lblStatus, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createMenuCard(String title, String desc, Color headerColor, String btnText, Runnable onClick) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Header Panel of Card
        JPanel cardHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        cardHeader.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(headerColor);
        cardHeader.add(lblTitle);

        // Body of Card
        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDesc.setForeground(TEXT_DARK);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setOpaque(false);
        txtDesc.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Button of Card
        JButton btnAction = new JButton(btnText);
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAction.setBackground(headerColor);
        btnAction.setForeground(Color.WHITE);
        btnAction.setFocusPainted(false);
        btnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAction.setPreferredSize(new Dimension(0, 36));
        btnAction.addActionListener((ActionEvent e) -> onClick.run());

        card.add(cardHeader, BorderLayout.NORTH);
        card.add(txtDesc, BorderLayout.CENTER);
        card.add(btnAction, BorderLayout.SOUTH);

        return card;
    }

    private void handleOpenStock() {
        SwingUtilities.invokeLater(() -> {
            new StockForm().setVisible(true);
            dispose();
        });
    }

    private void handleOpenBusinessProcess() {
        SwingUtilities.invokeLater(() -> {
            new TransactionForm().setVisible(true);
            dispose();
        });
    }

    private void handleOpenReport() {
        SwingUtilities.invokeLater(() -> {
            new ReportForm().setVisible(true);
            dispose();
        });
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin logout dari FloristFlow?",
                "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            SwingUtilities.invokeLater(() -> {
                new LoginForm().setVisible(true);
                dispose();
            });
        }
    }
}
