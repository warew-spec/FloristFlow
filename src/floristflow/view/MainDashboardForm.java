package floristflow.view;

import floristflow.model.User;
import floristflow.service.AuthService;
import floristflow.service.StockService;
import floristflow.service.TransactionService;
import floristflow.view.ui.ModernButton;
import floristflow.view.ui.RoundedPanel;
import floristflow.view.ui.UITheme;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Dashboard Utama FloristFlow dengan tema Modern Floral Boutique.
 * Menggunakan tata letak Sidebar Navigation bergaya POS / SaaS modern.
 */
public class MainDashboardForm extends JFrame {

    private final AuthService authService = AuthService.getInstance();
    private final StockService stockService = StockService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();
    private final User currentUser;

    public MainDashboardForm() {
        this.currentUser = authService.getCurrentUser();
        initComponents();
    }

    private void initComponents() {
        setTitle("FloristFlow - Dashboard Utama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. SIDEBAR KIRI (Dark Botanical Slate)
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. MAIN CONTENT KANAN (Light Clean Canvas)
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(25, 20, 25, 20));

        // Brand Logo
        JLabel lblLogo = new JLabel("🌸 FloristFlow");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Boutique & POS System");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(160, 200, 185));

        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(lblSub);
        sidebar.add(Box.createVerticalStrut(25));

        // User Profile Card
        RoundedPanel userCard = new RoundedPanel(12, new Color(255, 255, 255, 20));
        userCard.setLayout(new BorderLayout(10, 0));
        userCard.setBorder(new EmptyBorder(10, 12, 10, 12));
        userCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        String userName = (currentUser != null) ? currentUser.getFullName() : "Pengguna";
        String userRole = (currentUser != null) ? currentUser.getRole().getDisplayName() : "-";
        String initial = (!userName.isEmpty()) ? String.valueOf(userName.charAt(0)).toUpperCase() : "U";

        // Avatar Circle
        RoundedPanel avatar = new RoundedPanel(24, UITheme.ACCENT_ROSE);
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setLayout(new GridBagLayout());
        JLabel lblInitial = new JLabel(initial);
        lblInitial.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblInitial.setForeground(Color.WHITE);
        avatar.add(lblInitial);

        JPanel userText = new JPanel();
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));
        userText.setOpaque(false);

        JLabel lblName = new JLabel(userName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(Color.WHITE);

        JLabel lblRole = new JLabel(userRole);
        lblRole.setFont(UITheme.FONT_SMALL);
        lblRole.setForeground(new Color(180, 215, 200));

        userText.add(lblName);
        userText.add(lblRole);

        userCard.add(avatar, BorderLayout.WEST);
        userCard.add(userText, BorderLayout.CENTER);
        sidebar.add(userCard);
        sidebar.add(Box.createVerticalStrut(25));

        // Navigasi Menu
        JLabel lblNav = new JLabel("MENU UTAMA");
        lblNav.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblNav.setForeground(new Color(140, 175, 160));
        sidebar.add(lblNav);
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(createSidebarMenuButton("🏠 Dashboard", true, () -> {}));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createSidebarMenuButton("📦 Input Stock Bunga", false, this::handleOpenStock));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createSidebarMenuButton("🧾 Kasir & Buket", false, this::handleOpenBusinessProcess));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createSidebarMenuButton("📊 Rekapitulasi Omzet", false, this::handleOpenReport));

        sidebar.add(Box.createVerticalGlue());

        // Tombol Logout di bagian bawah sidebar
        ModernButton btnLogout = new ModernButton("🚪 Keluar (Logout)", new Color(220, 53, 69, 50), new Color(254, 202, 202), 8);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnLogout.addActionListener((ActionEvent e) -> handleLogout());
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JButton createSidebarMenuButton(String text, boolean isActive, Runnable action) {
        ModernButton btn = new ModernButton(
                text,
                isActive ? UITheme.PRIMARY : new Color(0, 0, 0, 0),
                isActive ? Color.WHITE : new Color(220, 235, 228),
                8
        );
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.addActionListener((ActionEvent e) -> action.run());
        return btn;
    }

    private JPanel createMainContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.BG_MAIN);
        content.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Top Greeting Banner
        JPanel topGreeting = new JPanel(new BorderLayout());
        topGreeting.setOpaque(false);
        topGreeting.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        String userName = (currentUser != null) ? currentUser.getFullName() : "Pengguna";
        JLabel lblGreeting = new JLabel("Selamat Datang, " + userName + "! 🌿");
        lblGreeting.setFont(UITheme.FONT_TITLE_LARGE);
        lblGreeting.setForeground(UITheme.TEXT_DARK);

        String desc = (currentUser != null) ? currentUser.getAccessDescription() : "";
        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(UITheme.FONT_REGULAR_MEDIUM);
        lblDesc.setForeground(UITheme.TEXT_MUTED);

        JPanel leftGreeting = new JPanel();
        leftGreeting.setLayout(new BoxLayout(leftGreeting, BoxLayout.Y_AXIS));
        leftGreeting.setOpaque(false);
        leftGreeting.add(lblGreeting);
        leftGreeting.add(Box.createVerticalStrut(4));
        leftGreeting.add(lblDesc);

        // Status Badge Toko
        RoundedPanel badgeStatus = new RoundedPanel(20, UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, 1);
        badgeStatus.setBorder(new EmptyBorder(6, 14, 6, 14));
        JLabel lblStatusText = new JLabel("🟢 Toko Bunga Buka • " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        lblStatusText.setFont(UITheme.FONT_BOLD);
        lblStatusText.setForeground(UITheme.PRIMARY);
        badgeStatus.add(lblStatusText);

        topGreeting.add(leftGreeting, BorderLayout.WEST);
        topGreeting.add(badgeStatus, BorderLayout.EAST);

        content.add(topGreeting);
        content.add(Box.createVerticalStrut(25));

        // 3 HERO CARDS (Input Stock, Proses Bisnis, Rekapitulasi)
        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        cardsGrid.add(createBoutiqueFeatureCard(
                "📦 Manajemen Stok Bunga",
                "Kelola katalog mawar, lily, tulip, harga modal, harga jual, dan stok segar.",
                stockService.getTotalFlowerTypes() + " Varian Bunga",
                UITheme.PRIMARY,
                "Buka Modul Stock →",
                this::handleOpenStock
        ));

        cardsGrid.add(createBoutiqueFeatureCard(
                "🧾 Kasir & Kreasi Buket",
                "Transaksi cepat, pilihan buket kustom, kartu ucapan, dan cetak nota digital.",
                transactionService.getTotalTransactionsCount() + " Transaksi Berjalan",
                UITheme.ACCENT_ROSE,
                "Buka Mesin Kasir →",
                this::handleOpenBusinessProcess
        ));

        cardsGrid.add(createBoutiqueFeatureCard(
                "📊 Rekapitulasi & Omzet",
                "Pantau riwayat penjualan, omzet kotor, laba bersih, dan varian bunga terlaris.",
                "Laporan Keuangan",
                UITheme.ACCENT_GOLD,
                "Lihat Rekapitulasi →",
                this::handleOpenReport
        ));

        content.add(cardsGrid);
        content.add(Box.createVerticalStrut(25));

        // QUICK STATS SUMMARY BANNER
        RoundedPanel statsBanner = new RoundedPanel(14, Color.WHITE, UITheme.BORDER, 1);
        statsBanner.setLayout(new GridLayout(1, 3, 10, 0));
        statsBanner.setBorder(new EmptyBorder(18, 25, 18, 25));
        statsBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        statsBanner.add(createStatItem("💰 Total Omzet Toko", UITheme.formatRupiah(transactionService.getTotalGrossRevenue())));
        statsBanner.add(createStatItem("📈 Laba Bersih Toko", UITheme.formatRupiah(transactionService.getTotalNetProfit())));
        statsBanner.add(createStatItem("🌸 Nilai Aset Stok Bunga", UITheme.formatRupiah(stockService.getTotalStockAssetValue())));

        content.add(statsBanner);
        content.add(Box.createVerticalGlue());

        return content;
    }

    private JPanel createBoutiqueFeatureCard(String title, String desc, String badgeText, Color accentColor, String btnText, Runnable onClick) {
        RoundedPanel card = new RoundedPanel(16, Color.WHITE, UITheme.BORDER, 1);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 20, 20, 20));

        // Top: Badge & Title
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblBadge = new JLabel(badgeText);
        lblBadge.setFont(UITheme.FONT_SMALL);
        lblBadge.setForeground(accentColor);

        top.add(lblTitle, BorderLayout.NORTH);
        top.add(Box.createVerticalStrut(4), BorderLayout.CENTER);
        top.add(lblBadge, BorderLayout.SOUTH);

        // Center: Description
        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setFont(UITheme.FONT_REGULAR);
        txtDesc.setForeground(UITheme.TEXT_MUTED);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setOpaque(false);
        txtDesc.setBorder(new EmptyBorder(14, 0, 14, 0));

        // Bottom: Action Button
        ModernButton btnAction = new ModernButton(btnText, accentColor, Color.WHITE, 10);
        btnAction.setPreferredSize(new Dimension(0, 38));
        btnAction.addActionListener((ActionEvent e) -> onClick.run());

        card.add(top, BorderLayout.NORTH);
        card.add(txtDesc, BorderLayout.CENTER);
        card.add(btnAction, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createStatItem(String label, String value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 17));
        val.setForeground(UITheme.TEXT_DARK);

        p.add(lbl);
        p.add(Box.createVerticalStrut(2));
        p.add(val);
        return p;
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
                "Apakah Anda yakin ingin keluar dari FloristFlow?",
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
