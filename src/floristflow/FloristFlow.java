package floristflow;

import floristflow.view.LoginForm;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Class & Entry Point Aplikasi FloristFlow.
 * Tugas Besar / Praktikum Pemrograman Berorientasi Objek (PBO).
 */
public class FloristFlow {

    /**
     * Entry point utama program.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Mengatur tema tampilan (Look & Feel) agar modern dan konsisten
        setupLookAndFeel();

        // Menjalankan form Login pada Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }

    /**
     * Mengatur Look and Feel ke Nimbus atau System UI jika tersedia.
     */
    private static void setupLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equalsIgnoreCase(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Gagal mengatur Look and Feel: " + e.getMessage());
        }
    }
}
