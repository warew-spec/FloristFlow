package floristflow.view.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * Tombol modern kustom dengan sudut melengkung, efek hover dinamis, dan flat styling.
 */
public class ModernButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private int cornerRadius;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text, Color baseColor, Color textColor, int radius) {
        super(text);
        this.normalColor = baseColor;
        this.hoverColor = brighten(baseColor, 0.12f);
        this.pressedColor = darken(baseColor, 0.15f);
        this.cornerRadius = radius;

        setForeground(textColor);
        setFont(UITheme.FONT_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public ModernButton(String text, Color baseColor, Color textColor) {
        this(text, baseColor, textColor, 10);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!isEnabled()) {
            g2.setColor(new Color(203, 213, 225));
        } else if (isPressed) {
            g2.setColor(pressedColor);
        } else if (isHovered) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(normalColor);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.dispose();

        super.paintComponent(g);
    }

    private static Color brighten(Color color, float fraction) {
        int r = Math.min(255, (int) (color.getRed() + (255 - color.getRed()) * fraction));
        int g = Math.min(255, (int) (color.getGreen() + (255 - color.getGreen()) * fraction));
        int b = Math.min(255, (int) (color.getBlue() + (255 - color.getBlue()) * fraction));
        return new Color(r, g, b, color.getAlpha());
    }

    private static Color darken(Color color, float fraction) {
        int r = Math.max(0, (int) (color.getRed() * (1 - fraction)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - fraction)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - fraction)));
        return new Color(r, g, b, color.getAlpha());
    }
}
