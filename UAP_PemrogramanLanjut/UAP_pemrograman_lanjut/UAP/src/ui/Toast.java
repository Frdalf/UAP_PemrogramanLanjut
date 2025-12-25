package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Toast / snackbar sederhana (kanan-bawah) untuk feedback non-intrusif.
 * Tidak butuh library eksternal.
 */
public final class Toast {
    private Toast() {}

    public static void success(Component parent, String message) {
        show(parent, "\u2705  " + message);
    }

    public static void info(Component parent, String message) {
        show(parent, message);
    }

    public static void show(Component parent, String message) {
        if (parent == null) return;

        Window owner = SwingUtilities.getWindowAncestor(parent);
        if (owner == null) return;

        JWindow w = new JWindow(owner);
        w.setBackground(new Color(0, 0, 0, 0));
        ToastPanel panel = new ToastPanel(message);
        w.setContentPane(panel);
        w.pack();

        // Mengatur posisi toast di pojok kanan bawah area window induk
        Rectangle b = owner.getBounds();
        Insets insets = owner.getInsets();
        int margin = 18;

        int x = b.x + b.width - insets.right - w.getWidth() - margin;
        int y = b.y + b.height - insets.bottom - w.getHeight() - margin;
        w.setLocation(Math.max(b.x + margin, x), Math.max(b.y + margin, y));

        w.setAlwaysOnTop(true);
        w.setVisible(true);

        // Animasi: fade in -> hold -> fade out
        int tickMs = 40;
        int fadeInMs = 160;
        int holdMs = 1400;
        int fadeOutMs = 260;

        final long start = System.currentTimeMillis();
        Timer t = new Timer(tickMs, null);
        t.addActionListener(e -> {
            long now = System.currentTimeMillis();
            long dt = now - start;

            float a;
            if (dt <= fadeInMs) {
                a = clamp01(dt / (float) fadeInMs);
            } else if (dt <= fadeInMs + holdMs) {
                a = 1f;
            } else {
                long outDt = dt - fadeInMs - holdMs;
                a = 1f - clamp01(outDt / (float) fadeOutMs);
            }

            panel.setAlpha(a);
            panel.repaint();

            if (dt >= fadeInMs + holdMs + fadeOutMs) {
                t.stop();
                w.setVisible(false);
                w.dispose();
            }
        });
        t.setRepeats(true);
        t.start();
    }

    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }

    // Panel toast dengan rounded + soft shadow
    private static class ToastPanel extends JPanel {
        private float alpha = 0f;
        private final String text;

        ToastPanel(String text) {
            this.text = text;
            setOpaque(false);
            setBorder(new EmptyBorder(12, 14, 12, 14));
        }

        void setAlpha(float alpha) {
            this.alpha = alpha;
        }

        @Override
        public Dimension getPreferredSize() {
            Font f = new Font("SansSerif", Font.BOLD, 12);
            FontMetrics fm = getFontMetrics(f);
            int w = fm.stringWidth(text) + 28;
            int h = Math.max(36, fm.getHeight() + 18);
            return new Dimension(w, h);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            int w = getWidth();
            int h = getHeight();
            int r = 18;

            // shadow
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(4, 6, w - 8, h - 8, r, r);

            // body
            GradientPaint gp = new GradientPaint(0, 0,
                    new Color(18, 28, 40, 242),
                    0, h,
                    new Color(12, 20, 32, 242));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w - 1, h - 1, r, r);

            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);

            // text
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int tx = 14;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }
}
