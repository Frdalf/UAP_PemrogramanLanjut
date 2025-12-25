package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class PillButton extends JButton {
    private Color bg = new Color(28, 95, 200);
    private Color bgHover = new Color(38, 120, 240);
    private Color bgPressed = new Color(20, 75, 165);

    private Color borderColor = new Color(20, 70, 160);

    private boolean hovering = false;
    private boolean pressing = false;
    

    private float hoverProgress = 0f;
    private Timer animationTimer;
    private float rippleProgress = 0f;
    private Point rippleCenter = new Point(0, 0);
    private boolean rippleActive = false;
    
    // Untuk shine sweep animation
    private float shinePosition = -0.5f;

    public PillButton(String text) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setForeground(Color.WHITE);
        setFont(getFont().deriveFont(Font.BOLD, 12.5f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setBorder(new EmptyBorder(10, 18, 10, 18));

        // Animasi untuk transisi
        animationTimer = new Timer(16, e -> {
            boolean needRepaint = false;
            

            if (hovering && hoverProgress < 1f) {
                hoverProgress = Math.min(1f, hoverProgress + 0.15f);
                needRepaint = true;
            } else if (!hovering && hoverProgress > 0f) {
                hoverProgress = Math.max(0f, hoverProgress - 0.1f);
                needRepaint = true;
            }

            // Menjalankan animasi shine sweep saat hover
            if (hovering) {
                shinePosition += 0.04f;
                if (shinePosition > 1.5f) shinePosition = -0.5f;
                needRepaint = true;
            }
            
            // Menjalankan animasi ripple (efek gelombang)
            if (rippleActive && rippleProgress < 1f) {
                rippleProgress = Math.min(1f, rippleProgress + 0.1f);
                needRepaint = true;
            } else if (rippleProgress >= 1f) {
                rippleActive = false;
                rippleProgress = 0f;
            }
            
            if (needRepaint) {
                repaint();
            } else if (!hovering && hoverProgress == 0f && !rippleActive) {
                shinePosition = -0.5f;
                ((Timer) e.getSource()).stop();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseEntered(MouseEvent e) { 
                hovering = true;
                shinePosition = -0.5f;
                if (!animationTimer.isRunning()) animationTimer.start();
                repaint(); 
            }
            @Override 
            public void mouseExited(MouseEvent e) { 
                hovering = false; 
                pressing = false; 
                if (!animationTimer.isRunning()) animationTimer.start();
                repaint(); 
            }
            @Override 
            public void mousePressed(MouseEvent e) { 
                pressing = true; 
                rippleCenter = e.getPoint();
                rippleActive = true;
                rippleProgress = 0f;
                if (!animationTimer.isRunning()) animationTimer.start();
                repaint(); 
            }
            @Override 
            public void mouseReleased(MouseEvent e) { 
                pressing = false; 
                repaint(); 
            }
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
        int arc = h;


        int margin = 4;
        int bx = margin;
        int by = margin;
        int bw = w - margin * 2;
        int bh = h - margin * 2;
        int bArc = bh; // pill arc berdasarkan tinggi button

        // Y shift untuk efek press/hover
        int ys = pressing ? 2 : 0;

        Color fill = bg;
        if (!isEnabled()) fill = new Color(160, 170, 185);
        else if (pressing) fill = bgPressed;
        else if (hoverProgress > 0) {
            fill = interpolateColor(bg, bgHover, hoverProgress);
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // SOFT SHADOW (dalam bounds)
        if (isEnabled()) {
            int shadowLayers = 3;
            for (int i = shadowLayers; i >= 1; i--) {
                int alpha = pressing ? 15 : (int)(8 + 4 * hoverProgress);
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.fillRoundRect(bx + i, by + ys + i + 2, bw - i, bh - i, bArc, bArc);
            }
        }

        // Efek cahaya luar saat hover (halus dan tidak keluar area)
        if (hoverProgress > 0 && isEnabled()) {
            int glowAlpha = (int)(30 * hoverProgress);
            g2.setColor(new Color(bgHover.getRed(), bgHover.getGreen(), bgHover.getBlue(), glowAlpha));
            g2.fillRoundRect(bx - 2, by + ys - 2, bw + 4, bh + 4, bArc + 4, bArc + 4);
        }

        //BUTTON BODY
        if (hoverProgress > 0 && isEnabled() && !pressing) {
            GradientPaint gradient = new GradientPaint(
                bx, by + ys, brightenColor(fill, 0.1f * hoverProgress),
                bx, by + ys + bh, fill
            );
            g2.setPaint(gradient);
        } else {
            g2.setColor(fill);
        }
        g2.fillRoundRect(bx, by + ys, bw, bh, bArc, bArc);

        //SHINE SWEEP EFFECT saat hover
        if (hoverProgress > 0.3f && isEnabled() && !pressing) {
            Shape oldClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Float(bx, by + ys, bw, bh, bArc, bArc));
            
            int shineWidth = bw / 3;
            int shineX = (int)(shinePosition * (bw + shineWidth)) + bx - shineWidth / 2;

            GradientPaint shine = new GradientPaint(
                shineX, 0, new Color(255, 255, 255, 0),
                shineX + shineWidth / 2, 0, new Color(255, 255, 255, (int)(50 * hoverProgress)),
                true
            );
            g2.setPaint(shine);
            g2.fillRect(shineX, by + ys, shineWidth, bh);
            
            g2.setClip(oldClip);
        }

        //TOP HIGHLIGHT untuk efek glossy
        if (isEnabled()) {
            Shape oldClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Float(bx, by + ys, bw, bh / 2, bArc, bArc));
            int highlightAlpha = pressing ? 10 : (int)(20 + 15 * hoverProgress);
            g2.setColor(new Color(255, 255, 255, highlightAlpha));
            g2.fillRoundRect(bx, by + ys, bw, bh / 2, bArc, bArc);
            g2.setClip(oldClip);
        }

        //RIPPLE EFFECT saat klik
        if (rippleActive && rippleProgress > 0) {
            Shape oldClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Float(bx, by + ys, bw, bh, bArc, bArc));
            
            int maxRadius = (int) Math.sqrt(bw * bw + bh * bh);
            int rippleRadius = (int)(maxRadius * rippleProgress);
            int rippleAlpha = (int)(100 * (1 - rippleProgress));

            g2.setColor(new Color(255, 255, 255, rippleAlpha));
            g2.fillOval(
                rippleCenter.x - rippleRadius, 
                rippleCenter.y - rippleRadius + ys, 
                rippleRadius * 2, 
                rippleRadius * 2
            );
            
            g2.setClip(oldClip);
        }

        // BORDER
        if (hoverProgress > 0 && isEnabled()) {
            Color hoverBorder = brightenColor(borderColor, 0.2f * hoverProgress);
            g2.setColor(hoverBorder);
            g2.setStroke(new BasicStroke(1.5f));
        } else {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
        }
        g2.drawRoundRect(bx, by + ys, bw - 1, bh - 1, bArc, bArc);

        //INNER BORDER HIGHLIGHT saat hover
        if (hoverProgress > 0 && isEnabled() && !pressing) {
            g2.setColor(new Color(255, 255, 255, (int)(20 * hoverProgress)));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(bx + 1, by + ys + 1, bw - 3, bh - 3, bArc - 2, bArc - 2);
        }

        g2.dispose();


        Graphics2D gText = (Graphics2D) g.create();
        gText.translate(0, ys);
        super.paintComponent(gText);
        gText.dispose();
    }
    
    // Helper: interpolasi warna
    private Color interpolateColor(Color c1, Color c2, float ratio) {
        int r = (int)(c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int gr = (int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int b = (int)(c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        return new Color(
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, gr)),
            Math.max(0, Math.min(255, b))
        );
    }
    
    // Helper: brighten warna
    private Color brightenColor(Color c, float factor) {
        int r = Math.min(255, (int)(c.getRed() + (255 - c.getRed()) * factor));
        int gr = Math.min(255, (int)(c.getGreen() + (255 - c.getGreen()) * factor));
        int b = Math.min(255, (int)(c.getBlue() + (255 - c.getBlue()) * factor));
        return new Color(r, gr, b);
    }
}
