package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class DarkModeToggle extends JButton {
    private boolean hover = false;

    public DarkModeToggle() {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(56, 56));
        setMaximumSize(new Dimension(56, 56));
        setToolTipText("Toggle Dark Mode");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 14;


        if (hover) {
            g2.setColor(new Color(255, 255, 255, 15));
            g2.fillRoundRect(4, 4, w - 8, h - 8, arc, arc);
            g2.setColor(new Color(255, 255, 255, 25));
            g2.drawRoundRect(4, 4, w - 9, h - 9, arc, arc);
        }

        // ikon matahari (saat mode gelap aktif, klik untuk berpindah ke mode terang)
        int iconSize = 24;
        int cx = w / 2;
        int cy = h / 2;

        g2.setColor(new Color(255, 255, 255, 230));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (ThemeManager.isDarkMode()) {
            drawSunIcon(g2, cx, cy, iconSize);
        } else {
            drawMoonIcon(g2, cx, cy, iconSize);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private void drawSunIcon(Graphics2D g2, int cx, int cy, int size) {
        int r = size / 4;

        //matahari
        g2.drawOval(cx - r, cy - r, r * 2, r * 2);
        int rayLen = size / 4;
        int rayDist = r + 3;
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8;
            int x1 = cx + (int) (rayDist * Math.cos(angle));
            int y1 = cy + (int) (rayDist * Math.sin(angle));
            int x2 = cx + (int) ((rayDist + rayLen) * Math.cos(angle));
            int y2 = cy + (int) ((rayDist + rayLen) * Math.sin(angle));
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawMoonIcon(Graphics2D g2, int cx, int cy, int size) {
        int r = size / 3;

        // Menggambar bulan sabit menggunakan dua lingkaran yang saling tumpang tindih
        g2.setColor(new Color(255, 255, 255, 230));
        int offset = r / 2;
        g2.fillArc(cx - r, cy - r, r * 2, r * 2, -90, 360);

        // potong sebagian untuk membentuk bulan sabit
        g2.setColor(ThemeManager.getSidebarGradientTop());
        g2.fillOval(cx - r + offset + 2, cy - r - 2, r * 2 - 4, r * 2 - 4);

        // bintang
        g2.setColor(new Color(255, 255, 255, 200));
        int starSize = 3;
        g2.fillOval(cx + r - 2, cy - r + 2, starSize, starSize);
        g2.fillOval(cx + r + 2, cy + 2, starSize - 1, starSize - 1);
    }
}
