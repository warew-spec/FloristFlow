package floristflow.view;

import floristflow.model.User;
import floristflow.service.AuthService;
import floristflow.view.ui.ModernButton;
import floristflow.view.ui.RoundedPanel;
import floristflow.view.ui.UITheme;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Form GUI Login FloristFlow dengan tema Modern Floral Boutique.
 * Tampilan Split-Screen: Hero Branding di kiri, Form Card di kanan.
 */
public class LoginForm extends JFrame {

    private final AuthService authService = AuthService.getInstance();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JToggleButton btnTogglePassword;
    private ModernButton btnLogin;
    private ModernButton btnReset;
    private JLabel lblError;

    public LoginForm() {
        initComponents();
    }

    private void initComponents() {
        setTitle("FloristFlow - Sistem Manajemen Toko Bunga");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(840, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // CONTAINER SPLIT SCREEN (Kiri = Hero Panel, Kanan = Form Card)
        JPanel mainContainer = new JPanel(new GridLayout(1, 2));

        // 1. LEFT HERO PANEL (Branding Boutique)
        JPanel leftHero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient Emerald Botanical
                GradientPaint gp = new GradientPaint(
                        0, 0, UITheme.PRIMARY_DARK,
                        0, getHeight(), UITheme.PRIMARY
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        leftHero.setLayout(new BoxLayout(leftHero, BoxLayout.Y_AXIS));
        leftHero.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel lblBrand = new JLabel("🌸 FloristFlow");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblTagline = new JLabel("Artisan Flower Boutique & POS System");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTagline.setForeground(new Color(200, 230, 218));

        leftHero.add(lblBrand);
        leftHero.add(Box.createVerticalStrut(6));
        leftHero.add(lblTagline);
        leftHero.add(Box.createVerticalStrut(35));

        // Feature bullet points
        leftHero.add(createFeatureItem("🌺 Manajemen Stok Aneka Bunga Segar"));
        leftHero.add(Box.createVerticalStrut(14));
        leftHero.add(createFeatureItem("🧾 Kasir Transaksi & Kreasi Buket"));
        leftHero.add(Box.createVerticalStrut(14));
        leftHero.add(createFeatureItem("📊 Rekapitulasi Omzet & Keuntungan"));
        leftHero.add(Box.createVerticalStrut(35));

        // Quote Box
        RoundedPanel quoteBox = new RoundedPanel(12, new Color(255, 255, 255, 25));
        quoteBox.setLayout(new BorderLayout());
        quoteBox.setBorder(new EmptyBorder(12, 16, 12, 16));
        quoteBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel lblQuote = new JLabel("<html><i>\"Menghadirkan keindahan bunga segar dengan pengelolaan bisnis modern.\"</i></html>");
        lblQuote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblQuote.setForeground(new Color(225, 245, 238));
        quoteBox.add(lblQuote, BorderLayout.CENTER);
        leftHero.add(quoteBox);

        mainContainer.add(leftHero);

        // 2. RIGHT FORM CARD
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(40, 45, 40, 45));

        JLabel lblWelcome = new JLabel("Selamat Datang 👋");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcome.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Silakan masuk ke akun kerja Anda");
        lblSub.setFont(UITheme.FONT_REGULAR_MEDIUM);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        rightPanel.add(lblWelcome);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(lblSub);
        rightPanel.add(Box.createVerticalStrut(25));

        // Quick 1-Click Demo Badges (Untuk kemudahan pengujian dosen)
        JPanel demoBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        demoBox.setOpaque(false);
        demoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblDemo = new JLabel("Akun Cepat:");
        lblDemo.setFont(UITheme.FONT_SMALL);
        lblDemo.setForeground(UITheme.TEXT_MUTED);
        demoBox.add(lblDemo);

        ModernButton btnDemoAdmin = new ModernButton("👤 Admin", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, 6);
        btnDemoAdmin.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnDemoAdmin.setPreferredSize(new Dimension(75, 24));
        btnDemoAdmin.addActionListener((ActionEvent e) -> fillCredentials("admin", "admin123"));

        ModernButton btnDemoKasir = new ModernButton("🌸 Kasir", UITheme.ACCENT_ROSE_LIGHT, UITheme.ACCENT_ROSE_DARK, 6);
        btnDemoKasir.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnDemoKasir.setPreferredSize(new Dimension(75, 24));
        btnDemoKasir.addActionListener((ActionEvent e) -> fillCredentials("kasir", "kasir123"));

        demoBox.add(btnDemoAdmin);
        demoBox.add(btnDemoKasir);
        rightPanel.add(demoBox);
        rightPanel.add(Box.createVerticalStrut(15));

        // Field Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setForeground(UITheme.TEXT_DARK);

        txtUsername = new JTextField();
        UITheme.styleTextField(txtUsername);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtUsername.setPreferredSize(new Dimension(300, 36));

        rightPanel.add(lblUser);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(txtUsername);
        rightPanel.add(Box.createVerticalStrut(12));

        // Field Password + Toggle Show/Hide
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UITheme.FONT_BOLD);
        lblPass.setForeground(UITheme.TEXT_DARK);

        JPanel passPanel = new JPanel(new BorderLayout(5, 0));
        passPanel.setOpaque(false);
        passPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        txtPassword = new JPasswordField();
        txtPassword.setFont(UITheme.FONT_REGULAR_MEDIUM);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        btnTogglePassword = new JToggleButton("👁️");
        btnTogglePassword.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        btnTogglePassword.setPreferredSize(new Dimension(42, 36));
        btnTogglePassword.setFocusPainted(false);
        btnTogglePassword.setBackground(new Color(241, 245, 249));
        btnTogglePassword.setBorder(new LineBorder(UITheme.BORDER, 1, true));
        btnTogglePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTogglePassword.addActionListener((ActionEvent e) -> {
            if (btnTogglePassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });

        passPanel.add(txtPassword, BorderLayout.CENTER);
        passPanel.add(btnTogglePassword, BorderLayout.EAST);

        rightPanel.add(lblPass);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(passPanel);
        rightPanel.add(Box.createVerticalStrut(6));

        // Error Label
        lblError = new JLabel(" ");
        lblError.setFont(UITheme.FONT_SMALL);
        lblError.setForeground(UITheme.STATUS_DANGER);
        rightPanel.add(lblError);
        rightPanel.add(Box.createVerticalStrut(10));

        // Action Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnLogin = new ModernButton("Masuk", UITheme.PRIMARY, Color.WHITE, 8);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.addActionListener((ActionEvent e) -> handleLogin());

        btnReset = new ModernButton("Reset", new Color(241, 245, 249), UITheme.TEXT_DARK, 8);
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReset.addActionListener((ActionEvent e) -> handleReset());

        btnPanel.add(btnLogin);
        btnPanel.add(btnReset);
        rightPanel.add(btnPanel);

        mainContainer.add(rightPanel);
        add(mainContainer, BorderLayout.CENTER);

        // Key Listeners untuk Enter
        KeyAdapter enterAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterAdapter);
        txtPassword.addKeyListener(enterAdapter);
    }

    private JPanel createFeatureItem(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(230, 245, 238));
        p.add(lbl);
        return p;
    }

    private void fillCredentials(String username, String password) {
        txtUsername.setText(username);
        txtPassword.setText(password);
        lblError.setText(" ");
        txtPassword.requestFocus();
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Username dan Password wajib diisi!");
            return;
        }

        boolean success = authService.login(username, password);

        if (success) {
            lblError.setText(" ");
            SwingUtilities.invokeLater(() -> {
                new MainDashboardForm().setVisible(true);
                dispose();
            });
        } else {
            lblError.setText("Username atau password salah!");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    private void handleReset() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblError.setText(" ");
        txtUsername.requestFocus();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
