package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel sidebar dengan gradasi warna gelap
 * Mendukung dark mode melalui ThemeManager.
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


        GradientPaint gp = new GradientPaint(
                0, 0, ThemeManager.getSidebarGradientTop(),
                0, h, ThemeManager.getSidebarGradientBottom()
        );
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        g2.setComposite(AlphaComposite.SrcOver.derive(ThemeManager.isDarkMode() ? 0.25f : 0.35f));
        g2.setPaint(new RadialGradientPaint(
                new Point(w / 2, h / 5),
                Math.max(w, h) * 0.75f,
                new float[]{0f, 1f},
                new Color[]{ThemeManager.getSidebarGlow(), new Color(0, 0, 0, 0)}
        ));
        g2.fillRect(0, 0, w, h);

        // Vignette
        g2.setComposite(AlphaComposite.SrcOver.derive(ThemeManager.isDarkMode() ? 0.35f : 0.25f));
        g2.setPaint(new GradientPaint(0, 0, new Color(0,0,0,0), w, 0, new Color(0,0,0,120)));
        g2.fillRect(0, 0, w, h);

        g2.dispose();
        super.paintComponent(g);
    }
}
