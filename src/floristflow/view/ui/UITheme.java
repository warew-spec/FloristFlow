package floristflow.view.ui;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Tema Desain Terpadu FloristFlow: Modern Floral Boutique.
 * Palet warna: Emerald/Sage Green + Soft Rose Pink + Slate Gray.
 */
public class UITheme {

    // --- Warna Primer & Aksen (Floral Palette) ---
    public static final Color PRIMARY = new Color(38, 97, 75);          // Deep Emerald Sage
    public static final Color PRIMARY_DARK = new Color(28, 74, 57);     // Darker Emerald
    public static final Color PRIMARY_LIGHT = new Color(236, 246, 241); // Soft Mint Tint
    public static final Color ACCENT_ROSE = new Color(224, 86, 117);    // Blush Floral Rose
    public static final Color ACCENT_ROSE_DARK = new Color(194, 65, 87);// Darker Rose
    public static final Color ACCENT_ROSE_LIGHT = new Color(254, 242, 244); // Very soft rose
    public static final Color ACCENT_GOLD = new Color(212, 160, 23);    // Warm Gold

    // --- Warna Netral & Background ---
    public static final Color BG_MAIN = new Color(247, 249, 250);       // Ultra-clean light background
    public static final Color CARD_BG = Color.WHITE;                    // Card Background
    public static final Color SIDEBAR_BG = new Color(24, 52, 42);       // Dark Botanical Sidebar
    public static final Color BORDER = new Color(226, 232, 240);        // Subtle Border
    public static final Color BORDER_FOCUS = new Color(38, 97, 75);

    // --- Warna Teks ---
    public static final Color TEXT_DARK = new Color(30, 41, 59);        // Slate 800
    public static final Color TEXT_MUTED = new Color(100, 116, 139);    // Slate 500
    public static final Color TEXT_LIGHT = new Color(241, 245, 249);

    // --- Warna Indikator Status ---
    public static final Color STATUS_SUCCESS = new Color(34, 197, 94);  // Green
    public static final Color STATUS_WARNING = new Color(245, 158, 11); // Amber
    public static final Color STATUS_DANGER = new Color(239, 68, 68);   // Red
    public static final Color STATUS_INFO = new Color(59, 130, 246);    // Blue

    // --- Font Hierarchy ---
    public static final Font FONT_TITLE_LARGE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_REGULAR_MEDIUM = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    // --- Format Rupiah ---
    private static final DecimalFormat CURRENCY_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("id-ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("Rp #,###", symbols);
    }

    public static String formatRupiah(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    /**
     * Terapkan styling modern pada JTable
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR_MEDIUM);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(new Color(241, 245, 249));

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);
    }

    /**
     * Membuat border card modern dengan padding
     */
    public static Border createCardBorder(int padding) {
        return BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(padding, padding, padding, padding)
        );
    }

    /**
     * Styling modern input JTextField
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_REGULAR_MEDIUM);
        textField.setBackground(Color.WHITE);
        textField.setForeground(TEXT_DARK);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }
}
