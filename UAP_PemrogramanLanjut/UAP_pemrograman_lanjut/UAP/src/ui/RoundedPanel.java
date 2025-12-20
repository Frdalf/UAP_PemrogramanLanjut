package ui;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private final int radius;
    private Color bg = new Color(18, 29, 46); // default navy

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    public RoundedPanel setBackgroundColor(Color c) {
        this.bg = c;
        repaint();
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w, h, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}
