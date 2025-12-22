package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Sidebar navigation button with icon + text, dark theme.
 * Active state draws a translucent rounded highlight with soft shadow.
 * Supports dark mode via ThemeManager.
 */
public class NavButton extends JButton {

    private boolean active = false;
    private boolean hover = false;

    public NavButton(String text, Icon icon) {
        super(text);
        setIcon(icon);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setHorizontalAlignment(SwingConstants.LEFT);
        setIconTextGap(12);

        setForeground(new Color(255, 255, 255, 235));
        setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Padding to match modern sidebar
        setBorder(new EmptyBorder(14, 18, 14, 18));
        setPreferredSize(new Dimension(220, 56));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
        });
    }

    // Backward-compatible constructor (no icon)
    public NavButton(String text) {
        this(text, null);
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 16;

        boolean drawBg = active || hover;

        if (drawBg) {
            // shadow (subtle)
            g2.setColor(active ? ThemeManager.getNavButtonShadowActive() : ThemeManager.getNavButtonShadowHover());
            g2.fillRoundRect(6, 8, w - 12, h - 12, arc, arc);

            // highlight background
            if (active) {
                g2.setColor(ThemeManager.getNavButtonActive());
            } else {
                g2.setColor(ThemeManager.getNavButtonHover());
            }
            g2.fillRoundRect(4, 4, w - 8, h - 10, arc, arc);

            // subtle border
            g2.setColor(active ? ThemeManager.getNavButtonBorderActive() : ThemeManager.getNavButtonBorderHover());
            g2.drawRoundRect(4, 4, w - 9, h - 11, arc, arc);
        }

        // Text/icon color: keep white; slightly brighter when active
        setForeground(active ? new Color(255, 255, 255, 255) : new Color(255, 255, 255, 230));

        super.paintComponent(g2);
        g2.dispose();
    }

    /**
     * Minimal line icon set for sidebar.
     * Uses component foreground color when painting.
     */
    public static class LineIcon implements Icon {
        public enum Type { DASHBOARD, LIST, USER, HISTORY, UP, DOWN }

        private final Type type;
        private final int size;

        public LineIcon(Type type) {
            this(type, 20);
        }

        public LineIcon(Type type, int size) {
            this.type = type;
            this.size = size;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(c.getForeground());

            int s = size;
            int px = x;
            int py = y;

            switch (type) {
                case DASHBOARD -> paintDashboard(g2, px, py, s);
                case LIST      -> paintList(g2, px, py, s);
                case USER      -> paintUser(g2, px, py, s);
                case HISTORY   -> paintHistory(g2, px, py, s);
                case UP        -> paintUp(g2, px, py, s);
                case DOWN      -> paintDown(g2, px, py, s);
            }

            g2.dispose();
        }

        private void paintDashboard(Graphics2D g2, int x, int y, int s) {
            // 2x2 grid
            int gap = 3;
            int box = (s - gap) / 2;
            g2.drawRoundRect(x, y, box, box, 3, 3);
            g2.drawRoundRect(x + box + gap, y, box, box, 3, 3);
            g2.drawRoundRect(x, y + box + gap, box, box, 3, 3);
            g2.drawRoundRect(x + box + gap, y + box + gap, box, box, 3, 3);
        }

        private void paintList(Graphics2D g2, int x, int y, int s) {
            // bullets + lines
            int cy1 = y + 4;
            int cy2 = y + s / 2;
            int cy3 = y + s - 4;
            g2.fillOval(x, cy1 - 2, 4, 4);
            g2.fillOval(x, cy2 - 2, 4, 4);
            g2.fillOval(x, cy3 - 2, 4, 4);

            int lx = x + 7;
            g2.drawLine(lx, cy1, x + s, cy1);
            g2.drawLine(lx, cy2, x + s, cy2);
            g2.drawLine(lx, cy3, x + s, cy3);
        }

        private void paintUser(Graphics2D g2, int x, int y, int s) {
            // head
            int cx = x + s / 2;
            int headR = 5;
            g2.drawOval(cx - headR, y + 2, headR * 2, headR * 2);
            // shoulders
            int top = y + 12;
            g2.drawRoundRect(x + 4, top, s - 8, s - top - 3, 10, 10);
        }

        private void paintHistory(Graphics2D g2, int x, int y, int s) {
            // circular arrow
            int r = s - 6;
            g2.drawArc(x + 3, y + 3, r, r, 40, 260);
            // arrow head
            int ax = x + 6;
            int ay = y + s / 2 + 2;
            g2.drawLine(ax, ay, ax + 4, ay - 4);
            g2.drawLine(ax, ay, ax + 5, ay + 2);
        }

        private void paintUp(Graphics2D g2, int x, int y, int s) {
            // upload arrow
            int cx = x + s / 2;
            g2.drawLine(cx, y + s - 4, cx, y + 6);
            g2.drawLine(cx, y + 6, cx - 5, y + 11);
            g2.drawLine(cx, y + 6, cx + 5, y + 11);
        }

        private void paintDown(Graphics2D g2, int x, int y, int s) {
            // download arrow
            int cx = x + s / 2;
            g2.drawLine(cx, y + 4, cx, y + s - 6);
            g2.drawLine(cx, y + s - 6, cx - 5, y + s - 11);
            g2.drawLine(cx, y + s - 6, cx + 5, y + s - 11);
        }
    }
}
