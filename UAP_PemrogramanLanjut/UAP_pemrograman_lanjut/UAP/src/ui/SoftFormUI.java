package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Kumpulan komponen UI ringan untuk membuat tampilan form "soft blue"
 * (mirip mockup yang kamu kirim) tanpa butuh aset icon eksternal.
 */
public final class SoftFormUI {
    private SoftFormUI() {}

    // ===== Colors =====
    public static final Color TITLE_BLUE = new Color(38, 120, 240);
    public static final Color PILL_BLUE = new Color(40, 125, 235);

    public static final Color FIELD_BORDER = new Color(120, 195, 235);
    public static final Color FIELD_FILL_TOP = new Color(232, 246, 255);
    public static final Color FIELD_FILL_BOTTOM = new Color(220, 242, 255);

    public static final Color FIELD_BORDER_DISABLED = new Color(185, 205, 220);
    public static final Color FIELD_FILL_DISABLED = new Color(242, 245, 248);

    public static final Color ICON_BLUE = new Color(45, 120, 215);

    // ===== Styling helper untuk JComboBox supaya menyatu dengan IconField =====
    private static void applySoftComboBoxStyle(JComboBox<?> cb) {
        cb.setBorder(null);
        cb.setOpaque(false);
        cb.setBackground(new Color(0, 0, 0, 0));
        cb.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Renderer: saat tampil di field (index == -1) dibuat transparan
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (index < 0) {
                    l.setOpaque(false);
                } else {
                    l.setOpaque(true);
                }
                l.setBorder(new EmptyBorder(4, 8, 4, 8));
                return l;
            }
        });

        cb.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(25, 90, 170));
                        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                        int w = getWidth();
                        int h = getHeight();
                        int cx = w / 2;
                        int cy = h / 2 + 1;
                        // chevron down
                        g2.drawLine(cx - 5, cy - 2, cx, cy + 3);
                        g2.drawLine(cx, cy + 3, cx + 5, cy - 2);

                        g2.dispose();
                    }
                };
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setBorderPainted(false);
                b.setFocusPainted(false);
                b.setRolloverEnabled(false);
                b.setPreferredSize(new Dimension(30, 22));
                return b;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                // jangan gambar background bawaan, biar menyatu dengan IconField
            }

            @Override
            protected ComboPopup createPopup() {
                ComboPopup p = super.createPopup();
                // styling list popup biar clean (optional)
                if (p.getList() != null) {
                    p.getList().setSelectionBackground(new Color(40, 125, 235));
                    p.getList().setSelectionForeground(Color.WHITE);
                }
                return p;
            }
        });
    }

    // ===== Background =====
    public static class FormBackground extends JPanel {
        public FormBackground() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            GradientPaint gp = new GradientPaint(0, 0, new Color(247, 250, 255),
                    w, h, new Color(232, 242, 252));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            // soft blobs
            g2.setColor(new Color(255, 255, 255, 150));
            g2.fillOval((int) (w * 0.55), (int) (h * 0.02), (int) (w * 0.55), (int) (w * 0.55));

            g2.setColor(new Color(210, 232, 248, 140));
            g2.fillOval((int) (w * 0.38), (int) (h * 0.52), (int) (w * 0.60), (int) (w * 0.60));

            g2.dispose();
        }
    }

    // ===== Card (white rounded + soft shadow) =====
    public static class CardPanel extends JPanel {
        private final int radius;

        public CardPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Shadow (drawn inside bounds, so it never looks "kepotong")
            g2.setColor(new Color(0, 0, 0, 18));
            int shX = 5, shY = 7;
            g2.fillRoundRect(shX, shY, Math.max(1, w - shX - 1), Math.max(1, h - shY - 1), radius, radius);

            // Card
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, Math.max(1, w - 1), Math.max(1, h - 1), radius, radius);
            g2.setColor(new Color(220, 230, 242));
            g2.drawRoundRect(0, 0, Math.max(1, w - 1), Math.max(1, h - 1), radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ===== Pill label (tab-like) =====
    public static class PillLabel extends JLabel {
        public PillLabel(String text) {
            super(text);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setBorder(new EmptyBorder(6, 12, 6, 12));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(PILL_BLUE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);
            g2.setColor(new Color(25, 90, 170));
            g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ===== Icon types =====
    public enum IconType {
        ID, USER, PHONE, PIN,
        CALENDAR, LIST, TAG, MONEY,
        BOX, NUMBER, NOTE,
        SEARCH
    }

    /** Icon sederhana (vector) supaya tidak butuh file gambar. */
    public static class SoftIcon implements Icon {
        private final IconType type;
        private final int size;

        public SoftIcon(IconType type, int size) {
            this.type = type;
            this.size = size;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ICON_BLUE);

            int s = size;
            int cx = x;
            int cy = y;

            switch (type) {
                case ID -> {
                    g2.drawRoundRect(cx + 1, cy + 3, s - 3, s - 6, 6, 6);
                    g2.fillOval(cx + 4, cy + 6, 6, 6);
                    g2.fillRoundRect(cx + 12, cy + 7, s - 16, 2, 2, 2);
                    g2.fillRoundRect(cx + 12, cy + 12, s - 18, 2, 2, 2);
                }
                case USER -> {
                    g2.fillOval(cx + 6, cy + 3, s - 12, s - 12);
                    g2.fillRoundRect(cx + 4, cy + (s / 2) + 2, s - 8, (s / 2) - 4, 10, 10);
                }
                case PHONE -> {
                    g2.drawRoundRect(cx + 7, cy + 2, s - 14, s - 4, 6, 6);
                    g2.fillOval(cx + (s / 2) - 1, cy + s - 5, 3, 3);
                }
                case PIN -> {
                    g2.fillOval(cx + 7, cy + 3, s - 14, s - 14);
                    Polygon p = new Polygon();
                    p.addPoint(cx + s / 2, cy + s - 2);
                    p.addPoint(cx + s / 2 - 6, cy + s / 2 + 4);
                    p.addPoint(cx + s / 2 + 6, cy + s / 2 + 4);
                    g2.fillPolygon(p);
                    g2.setColor(new Color(235, 247, 255));
                    g2.fillOval(cx + s / 2 - 2, cy + s / 2 - 2, 4, 4);
                }
                case CALENDAR -> {
                    g2.drawRoundRect(cx + 2, cy + 4, s - 4, s - 6, 6, 6);
                    g2.fillRoundRect(cx + 2, cy + 4, s - 4, 6, 6, 6);
                    g2.setColor(new Color(235, 247, 255));
                    g2.fillRect(cx + 4, cy + 12, s - 8, s - 16);
                    g2.setColor(ICON_BLUE);
                    g2.fillRect(cx + 6, cy + 14, 3, 3);
                    g2.fillRect(cx + 11, cy + 14, 3, 3);
                    g2.fillRect(cx + 16, cy + 14, 3, 3);
                }
                case LIST -> {
                    for (int i = 0; i < 3; i++) {
                        int yy = cy + 5 + i * 6;
                        g2.fillOval(cx + 3, yy, 3, 3);
                        g2.fillRoundRect(cx + 9, yy, s - 12, 3, 2, 2);
                    }
                }
                case TAG -> {
                    Polygon p = new Polygon();
                    p.addPoint(cx + 4, cy + 4);
                    p.addPoint(cx + s - 4, cy + s / 2);
                    p.addPoint(cx + 4, cy + s - 4);
                    g2.fillPolygon(p);
                    g2.setColor(new Color(235, 247, 255));
                    g2.fillOval(cx + 6, cy + s / 2 - 2, 4, 4);
                }
                case MONEY -> {
                    g2.fillOval(cx + 4, cy + 6, s - 8, s - 8);
                    g2.setColor(new Color(235, 247, 255));
                    g2.fillOval(cx + 7, cy + 9, s - 14, s - 14);
                    g2.setColor(ICON_BLUE);
                    g2.drawOval(cx + 7, cy + 9, s - 14, s - 14);
                    g2.fillRoundRect(cx + 9, cy + 12, s - 18, 2, 2, 2);
                }
                case BOX -> {
                    g2.drawRoundRect(cx + 4, cy + 6, s - 8, s - 10, 6, 6);
                    g2.drawLine(cx + 4, cy + 10, cx + s - 4, cy + 10);
                    g2.drawLine(cx + s / 2, cy + 6, cx + s / 2, cy + s - 4);
                }
                case NUMBER -> {
                    g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(12, s - 10)));
                    FontMetrics fm = g2.getFontMetrics();
                    String t = "#";
                    int tx = cx + (s - fm.stringWidth(t)) / 2;
                    int ty = cy + (s + fm.getAscent() - fm.getDescent()) / 2 - 1;
                    g2.drawString(t, tx, ty);
                }
                case NOTE -> {
                    g2.drawRoundRect(cx + 5, cy + 3, s - 10, s - 6, 6, 6);
                    g2.drawLine(cx + 8, cy + 9, cx + s - 8, cy + 9);
                    g2.drawLine(cx + 8, cy + 13, cx + s - 10, cy + 13);
                    g2.drawLine(cx + 8, cy + 17, cx + s - 12, cy + 17);
                }
                case SEARCH -> {
                    // magnifier
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawOval(cx + 4, cy + 4, s - 10, s - 10);
                    g2.drawLine(cx + s - 8, cy + s - 8, cx + s - 3, cy + s - 3);
                }
            }

            g2.dispose();
        }
    }

    // ===== Field wrapper (rounded input with icon) =====
    public static class IconField extends JPanel {
        private final JComponent inner;
        private boolean focused = false;
        private final int radius;

        public IconField(IconType iconType, JComponent inner) {
            this(iconType, inner, 18);
        }

        public IconField(IconType iconType, JComponent inner, int radius) {
            this.inner = inner;
            this.radius = radius;
            setOpaque(false);
            setLayout(new BorderLayout(10, 0));
            setBorder(new EmptyBorder(10, 12, 10, 12));

            JLabel icon = new JLabel(new SoftIcon(iconType, 20));
            icon.setOpaque(false);
            add(icon, BorderLayout.WEST);

            prepareInner(inner);
            add(inner, BorderLayout.CENTER);

            // focus tracking
            installFocusTracker(inner);
        }

        private void prepareInner(JComponent c) {
            c.setOpaque(false);
            c.setBorder(null);

            if (c instanceof JTextField tf) {
                tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
            }

            if (c instanceof JComboBox<?> cb) {
                applySoftComboBoxStyle(cb);
            }

            if (c instanceof JSpinner sp) {
                sp.setBorder(null);
                sp.setOpaque(false);
                JComponent editor = sp.getEditor();
                if (editor instanceof JSpinner.DefaultEditor de) {
                    de.getTextField().setOpaque(false);
                    de.getTextField().setBorder(null);
                    de.getTextField().setFont(new Font("SansSerif", Font.PLAIN, 14));
                }
            }
        }

        private void installFocusTracker(JComponent c) {
            // track focus on inner components (textfield, combobox editor, spinner editor, etc.)
            if (c instanceof JTextField tf) {
                tf.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                    @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
                });
            } else if (c instanceof JComboBox<?> cb) {
                cb.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                    @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
                });
            } else if (c instanceof JSpinner sp) {
                sp.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                    @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
                });
            } else if (c instanceof JScrollPane sp) {
                Component view = sp.getViewport().getView();
                if (view instanceof JComponent jc) installFocusTracker(jc);
            } else if (c instanceof JTextArea ta) {
                ta.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                    @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
                });
            }
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            inner.setEnabled(enabled);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            boolean enabled = inner.isEnabled();

            // shadow (offset but still inside bounds)
            g2.setColor(new Color(0, 0, 0, 12));
            int shX = 3, shY = 5;
            g2.fillRoundRect(shX, shY, Math.max(1, w - shX - 1), Math.max(1, h - shY - 1), radius, radius);

            // fill
            if (!enabled) {
                g2.setColor(FIELD_FILL_DISABLED);
                g2.fillRoundRect(0, 0, Math.max(1, w - 1), Math.max(1, h - 1), radius, radius);
            } else {
                GradientPaint gp = new GradientPaint(0, 0, FIELD_FILL_TOP, 0, h, FIELD_FILL_BOTTOM);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, Math.max(1, w - 1), Math.max(1, h - 1), radius, radius);
            }

            // border
            if (!enabled) g2.setColor(FIELD_BORDER_DISABLED);
            else if (focused) g2.setColor(new Color(60, 160, 235));
            else g2.setColor(FIELD_BORDER);
            g2.drawRoundRect(0, 0, Math.max(1, w - 1), Math.max(1, h - 1), radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

}
