package floristflow.view;

import floristflow.model.User;
import floristflow.service.AuthService;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Form GUI untuk Halaman Autentikasi dan Login Pengguna sistem FloristFlow.
 */
public class LoginForm extends JFrame {

    private final AuthService authService = AuthService.getInstance();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnReset;
    private JLabel lblError;

    // Skema Warna Bertema Toko Bunga (Floral Theme)
    private static final Color PRIMARY_COLOR = new Color(46, 117, 89);     // Forest Garden Green
    private static final Color SECONDARY_COLOR = new Color(214, 90, 117);  // Soft Rose Pink
    private static final Color BG_COLOR = new Color(245, 247, 248);        // Soft Off-White
    private static final Color TEXT_DARK = new Color(40, 44, 52);

    public LoginForm() {
        initComponents();
    }

    private void initComponents() {
        setTitle("FloristFlow - Login Sistem Toko Bunga");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblLogo = new JLabel("🌸 FloristFlow");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTagline = new JLabel("Sistem Manajemen & Transaksi Toko Bunga");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTagline.setForeground(new Color(230, 245, 238));
        lblTagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblLogo);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(lblTagline);
        add(headerPanel, BorderLayout.NORTH);

        // Body / Form Card Panel
        JPanel bodyContainer = new JPanel(new GridBagLayout());
        bodyContainer.setBackground(BG_COLOR);
        bodyContainer.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel lblCardTitle = new JLabel("Silakan Masuk ke Akun Anda");
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCardTitle.setForeground(TEXT_DARK);
        lblCardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label & Field Username
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUsername.setForeground(TEXT_DARK);
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setPreferredSize(new Dimension(300, 38));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label & Field Password
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPassword.setForeground(TEXT_DARK);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setPreferredSize(new Dimension(300, 38));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label Error (Tersembunyi secara default)
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(new Color(220, 53, 69));
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Button Panel
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnReset.setBackground(new Color(230, 235, 240));
        btnReset.setForeground(TEXT_DARK);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPanel.add(btnLogin);
        btnPanel.add(btnReset);

        // Panel Info Akun Default
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(240, 247, 244));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 230, 215), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel lblInfoTitle = new JLabel("📌 Akun Default Pengujian:");
        lblInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblInfoTitle.setForeground(PRIMARY_COLOR);

        JLabel lblInfoAdmin = new JLabel("• Admin: username 'admin', password 'admin123'");
        lblInfoAdmin.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfoAdmin.setForeground(TEXT_DARK);

        JLabel lblInfoKasir = new JLabel("• Kasir: username 'kasir', password 'kasir123'");
        lblInfoKasir.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfoKasir.setForeground(TEXT_DARK);

        infoPanel.add(lblInfoTitle);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(lblInfoAdmin);
        infoPanel.add(lblInfoKasir);

        // Menyusun komponen ke Form Card
        formCard.add(lblCardTitle);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(lblUsername);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(txtUsername);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(lblPassword);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(txtPassword);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(lblError);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(btnPanel);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(infoPanel);

        bodyContainer.add(formCard);
        add(bodyContainer, BorderLayout.CENTER);

        // Event Listeners
        btnLogin.addActionListener((ActionEvent e) -> handleLogin());
        btnReset.addActionListener((ActionEvent e) -> handleReset());

        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };

        txtUsername.addKeyListener(enterKeyAdapter);
        txtPassword.addKeyListener(enterKeyAdapter);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // Validasi input kosong
        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Username dan Password wajib diisi!");
            JOptionPane.showMessageDialog(this,
                    "Harap masukkan username dan password terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = authService.login(username, password);

        if (success) {
            User loggedInUser = authService.getCurrentUser();
            lblError.setText(" ");
            JOptionPane.showMessageDialog(this,
                    "Selamat datang, " + loggedInUser.getFullName() + "!\nLogin berhasil sebagai: " + loggedInUser.getRole().getDisplayName(),
                    "Login Berhasil", JOptionPane.INFORMATION_MESSAGE);

            // Buka Dashboard Utama & Tutup Form Login
            SwingUtilities.invokeLater(() -> {
                new MainDashboardForm().setVisible(true);
                dispose();
            });
        } else {
            lblError.setText("Username atau password salah!");
            JOptionPane.showMessageDialog(this,
                    "Kombinasi Username dan Password tidak cocok.\nSilakan coba lagi.",
                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
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
        // Terapkan System Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
