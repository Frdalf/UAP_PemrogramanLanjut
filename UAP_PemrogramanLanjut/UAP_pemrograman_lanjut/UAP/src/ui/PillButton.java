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
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = h; // pill

        Color fill = bg;
        if (!isEnabled()) fill = new Color(160, 170, 185);
        else if (pressing) fill = bgPressed;
        else if (hovering) fill = bgHover;

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
