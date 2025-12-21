package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Dark gradient sidebar panel (matches modern look in mockup).
 */
public class SidebarPanel extends JPanel {

    public SidebarPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Base vertical gradient
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(18, 33, 58),
                0, h, new Color(7, 18, 38)
        );
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        // Soft radial glow (top-left)
        g2.setComposite(AlphaComposite.SrcOver.derive(0.35f));
        g2.setPaint(new RadialGradientPaint(
                new Point(w / 2, h / 5),
                Math.max(w, h) * 0.75f,
                new float[]{0f, 1f},
                new Color[]{new Color(65, 120, 230), new Color(0, 0, 0, 0)}
        ));
        g2.fillRect(0, 0, w, h);

        // Vignette
        g2.setComposite(AlphaComposite.SrcOver.derive(0.25f));
        g2.setPaint(new GradientPaint(0, 0, new Color(0,0,0,0), w, 0, new Color(0,0,0,120)));
        g2.fillRect(0, 0, w, h);

        g2.dispose();
        super.paintComponent(g);
    }
}
