package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PillButton extends JButton {
    private Color bg = new Color(28, 95, 200);
    private Color bgHover = new Color(38, 120, 240);
    private Color bgPressed = new Color(20, 75, 165);

    private Color borderColor = new Color(20, 70, 160);

    private boolean hovering = false;
    private boolean pressing = false;

    /**
     * Micro-interaction:
     * - Hover: tombol tampak "terangkat" dengan shadow yang lebih tipis/rapat.
     * - Pressed: tombol tampak "masuk" dengan body sedikit turun.
     *
     * Catatan: Jangan gunakan nilai negatif karena area painting Swing ter-clip
     * ke ukuran komponen, sehingga efek hover bisa terlihat "terpotong".
     */
    private int yShift() {
        if (!isEnabled()) return 0;
        return pressing ? 1 : 0;
    }

    public PillButton(String text) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setForeground(Color.WHITE);
        setFont(getFont().deriveFont(Font.BOLD, 12.5f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setBorder(new EmptyBorder(9, 16, 9, 16));

        // hover / pressed tracking
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { hovering = true; repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { hovering = false; pressing = false; repaint(); }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { pressing = true; repaint(); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { pressing = false; repaint(); }
        });
    }

    public PillButton setColors(Color bg, Color bgHover, Color bgPressed, Color border) {
        this.bg = bg;
        this.bgHover = bgHover;
        this.bgPressed = bgPressed;
        this.borderColor = border;
        repaint();
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        int arc = h; // pill

        int ys = yShift(); // hanya 0 atau 1

        Color fill = bg;
        if (!isEnabled()) fill = new Color(160, 170, 185);
        else if (pressing) fill = bgPressed;
        else if (hovering) fill = bgHover;

        // shadow first
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // shadow: selalu digambar DI DALAM bounds agar tidak ter-clip
        int shadowOffset = pressing ? 6 : (hovering ? 4 : 5);
        int shadowY = ys + shadowOffset;
        int shadowH = Math.max(0, (h - 1) - shadowOffset);

        int shadowAlpha = 10;
        if (isEnabled()) shadowAlpha = pressing ? 32 : (hovering ? 16 : 20);
        g2.setColor(new Color(0, 0, 0, shadowAlpha));
        // pakai arc yang aman untuk tinggi shadow
        int arcShadow = Math.max(6, Math.min(arc, shadowH));
        g2.fillRoundRect(0, shadowY, w - 1, shadowH, arcShadow, arcShadow);

        // button body (slightly moves down when pressed)
        g2.setColor(fill);
        g2.fillRoundRect(0, ys, w - 1, h - 1, arc, arc);

        g2.setColor(borderColor);
        g2.drawRoundRect(0, ys, w - 1, h - 1, arc, arc);

        g2.dispose();

        // draw text/icon shifted together with the button body
        Graphics2D gText = (Graphics2D) g.create();
        gText.translate(0, ys);
        super.paintComponent(gText);
        gText.dispose();
    }
}
