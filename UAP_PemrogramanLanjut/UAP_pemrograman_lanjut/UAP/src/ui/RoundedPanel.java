package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Rounded panel that supports both custom colors and theme-aware colors.
 */
public class RoundedPanel extends JPanel {
    private final int radius;
    private Color bg = new Color(18, 29, 46); // default navy
    private boolean useThemeTableBg = false;

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    public RoundedPanel setBackgroundColor(Color c) {
        this.bg = c;
        this.useThemeTableBg = false;
        repaint();
        return this;
    }

    /**
     * Use ThemeManager's table background color (auto-updates with theme).
     */
    public RoundedPanel useTableBackground() {
        this.useThemeTableBg = true;
        // Register listener to repaint when theme changes
        ThemeManager.addThemeChangeListener(() -> {
            if (useThemeTableBg) {
                repaint();
            }
        });
        repaint();
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Use theme color if enabled, otherwise use custom bg
        Color fillColor = useThemeTableBg ? getTableWrapBackground() : bg;
        g2.setColor(fillColor);
        g2.fillRoundRect(0, 0, w, h, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }

    private Color getTableWrapBackground() {
        return ThemeManager.isDarkMode() 
            ? new Color(10, 20, 36) 
            : new Color(235, 240, 248);
    }
}
