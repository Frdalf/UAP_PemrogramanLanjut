package ui;

import javax.swing.*;
import java.awt.*;

public class NavButton extends JButton {
    private boolean active = false;

    private final Color bgNormal = new Color(245, 246, 248);   // putih/abu muda
    private final Color bgActive = new Color(61, 129, 219);    // biru aktif
    private final Color fgNormal = new Color(35, 40, 50);
    private final Color fgActive = Color.WHITE;

    public NavButton(String text) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("SansSerif", Font.PLAIN, 12));
        setPreferredSize(new Dimension(140, 72));

        Dimension size = new Dimension(160, 70); // <--- kamu bisa ubah
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
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

        // shadow halus
        g2.setColor(new Color(0, 0, 0, active ? 30 : 15));
        g2.fillRoundRect(4, 6, w - 8, h - 10, 16, 16);

        // bg
        g2.setColor(active ? bgActive : bgNormal);
        g2.fillRoundRect(0, 0, w - 8, h - 10, 16, 16);

        // text
        g2.setColor(active ? fgActive : fgNormal);
        FontMetrics fm = g2.getFontMetrics();
        String t = getText();
        int tw = fm.stringWidth(t);
        int th = fm.getAscent();

        g2.drawString(t, (w - 8 - tw) / 2, (h - 10 + th) / 2);

        g2.dispose();
    }
}
